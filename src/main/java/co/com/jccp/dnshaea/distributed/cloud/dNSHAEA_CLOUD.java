package co.com.jccp.dnshaea.distributed.cloud;

import co.com.jccp.dnshaea.distributed.cpu.GenerateOffspring;
import co.com.jccp.dnshaea.distributed.cpu.Replace;
import co.com.jccp.dnshaea.function.ObjectiveFunction;
import co.com.jccp.dnshaea.gop.GeneticOperator;
import co.com.jccp.dnshaea.individual.MOEAIndividual;
import co.com.jccp.dnshaea.initialization.PopulationInitialization;
import co.com.jccp.dnshaea.selection.Selection;
import co.com.jccp.dnshaea.utils.*;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.lambda.AWSLambdaAsync;
import com.amazonaws.services.lambda.AWSLambdaAsyncClientBuilder;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;


public class dNSHAEA_CLOUD<T> {

    private PopulationInitialization<T> popInit;
    private ObjectiveFunction<T> function;
    private boolean minimize;
    private int dimensions;
    private int popSize;
    private int iterations;
    private List<GeneticOperator<T>> operators;
    private Selection<T> selection;

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final AWSLambdaAsync ll = AWSLambdaAsyncClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(
            new BasicAWSCredentials("", "")))
            .build();


    private static final int MAX_THREADS = 2;

    public dNSHAEA_CLOUD(PopulationInitialization<T> popInit, ObjectiveFunction<T> function, boolean minimize, int dimensions, int popSize, int iterations, List<GeneticOperator<T>> operators, Selection<T> selection) {
        this.popInit = popInit;
        this.function = function;
        this.minimize = minimize;
        this.dimensions = dimensions;
        this.popSize = popSize;
        this.iterations = iterations;
        this.operators = operators;
        this.selection = selection;
    }

    public List<MOEAIndividual<T>> apply() {
        long tt = System.currentTimeMillis();
        List<MOEAIndividual<T>> pop = popInit.init(popSize, dimensions);
        calculateObjectives(pop);
        for (MOEAIndividual<T> individual : pop) {
            individual.setOpProbabilities(RandomUtils.generateRandomProbabilityVector(operators.size()));
            individual.setDiversityMeasure(0.0);
        }
        FNDS.apply(pop, minimize);
        for (int t = 0; t < iterations; t++) {
            List<String> popString = new ArrayList<>(popSize);
            for (MOEAIndividual<T> ind : pop) {
                try {
                    CloudIndividual<T> ci = new CloudIndividual<>();
                    ci.setIndividual(ind);
                    ci.setPop(pop);
                    popString.add(mapper.writeValueAsString(ci));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            List<Future<InvokeResult>> futures = new ArrayList<>(pop.size());
            for (String s : popString) {
                InvokeRequest ir = new InvokeRequest()
                        .withFunctionName("GenerateOffspring")
                        .withPayload(s)
                        .withInvocationType(InvocationType.RequestResponse);
                futures.add(ll.invokeAsync(ir));
            }
            List<MOEAIndividual<T>> offspring = new LinkedList<>();

            int c = 0;
            for (Future<InvokeResult> future : futures) {
                try {
                    InvokeResult ir = future.get();
                    String pp = new String(ir.getPayload().array(), StandardCharsets.UTF_8);
                    List<MOEAIndividual<T>> off = mapper.readValue(pp, new TypeReference<List<MOEAIndividual<T>>>() {});
                    pop.get(c).setOffspring(off);
                    offspring.addAll(off);
                    c++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            List<MOEAIndividual<T>> r = new ArrayList<>(pop.size() + offspring.size());
            r.addAll(pop);
            r.addAll(offspring);
            double[][] limitsObjective = new double[function.getNObjectives()][2];
            for (int i = 0; i < function.getNObjectives(); i++) {
                limitsObjective[i][0] = Double.MAX_VALUE;
                limitsObjective[i][1] = Double.MIN_VALUE;
            }
            for (MOEAIndividual<T> ind : r) {
                for (int i = 0; i < function.getNObjectives(); i++) {
                    if (ind.getObjectiveValues()[i] < limitsObjective[i][0])
                        limitsObjective[i][0] = ind.getObjectiveValues()[i];
                    if (ind.getObjectiveValues()[i] > limitsObjective[i][1])
                        limitsObjective[i][1] = ind.getObjectiveValues()[i];
                }
            }
            List<List<MOEAIndividual<T>>> fronts = FNDS.apply(r, minimize);
            for (List<MOEAIndividual<T>> front : fronts) {
                CrowdingDistance.apply(front, limitsObjective);
            }

            popString = new ArrayList<>(popSize);
            for (MOEAIndividual<T> ind : pop) {
                try {
                    CloudIndividual<T> ci = new CloudIndividual<>();
                    ci.setIndividual(ind);
                    ci.setPop(ind.getOffspring());
                    popString.add(mapper.writeValueAsString(ci));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            futures = new ArrayList<>(pop.size());
            for (String s : popString) {
                InvokeRequest ir = new InvokeRequest()
                        .withFunctionName("Replace")
                        .withPayload(s)
                        .withInvocationType(InvocationType.RequestResponse);
                futures.add(ll.invokeAsync(ir));
            }
            List<MOEAIndividual<T>> newPop = new LinkedList<>();
            for (Future<InvokeResult> future : futures) {
                try {
                    InvokeResult ir = future.get();
                    String pp = new String(ir.getPayload().array(), StandardCharsets.UTF_8);
                    MOEAIndividual<T> off = mapper.readValue(pp, new TypeReference<MOEAIndividual<T>>() {});
                    newPop.add(off);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            pop = newPop;
        }


        System.out.println(System.currentTimeMillis() - tt);
        return pop;
    }

    public void calculateObjectives(List<MOEAIndividual<T>> pop) {
        for (MOEAIndividual<T> ind : pop) {
            double[] objectiveValues = function.apply(ind.getData());
            ind.setObjectiveValues(objectiveValues);
        }
    }


}
