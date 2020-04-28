#include <curand.h>
#include <curand_kernel.h>

__device__ void one_point_cross(int* newIndividuals, int* d_pop, int start, int end, curandState* my_curandstate, int dimensions, int pop_size)
{
	int pos = curand(my_curandstate) % (dimensions - 1);
	int partner = curand(my_curandstate) % pop_size;
	int cont = 0, sum = 0;
	for (int i = start; i < start + pos; i++)
	{

		newIndividuals[cont] = d_pop[i];
		sum += d_pop[i];
		cont++;
	}
	for (int i = dimensions * partner + pos; i < dimensions * partner + dimensions - 1; i++)
	{
		newIndividuals[cont] = d_pop[i];
		sum += d_pop[i];
		cont++;
	}
	newIndividuals[cont] = sum;
	cont++;
	sum = 0;
	for (int i = dimensions * partner; i < dimensions * partner + pos; i++)
	{
		newIndividuals[cont] = d_pop[i];
		sum += d_pop[i];
		cont++;
	}
	for (int i = start + pos; i < end - 1; i++)
	{
		newIndividuals[cont] = d_pop[i];
		sum += d_pop[i];
		cont++;
	}
	newIndividuals[cont] = sum;
}

__device__ void mutation(int* newIndividuals, int* d_pop, int start, int end, curandState* my_curandstate, int dimensions)
{
	int pos = curand(my_curandstate) % (dimensions - 1);
	int cont = 0, sum = 0;
	for (int i = start; i < end - 1; i++) {
		if (i == start + pos)
			newIndividuals[cont] = 1 - d_pop[i];
		else
			newIndividuals[cont] = d_pop[i];

		sum += newIndividuals[cont];
		cont++;
	}
	newIndividuals[cont] = sum;
}

extern "C"
__global__ void applyOperators(double* d_operators_probabilites, int* d_pop, int dimensions, int operators_number, curandState* my_curandstate, int pop_size, int totalThreads)
{
	int idx = threadIdx.x + blockDim.x * blockIdx.x;
	int row = idx * (pop_size / totalThreads);
	if (pop_size % totalThreads != 0)
		row++;
	if (idx < totalThreads)
	{
		for (int j = row; j < row + (pop_size / totalThreads); j++)
		{
			int i;
			int start, end;
			//Select Operator
			double sum = 0.0;
			int cont = 0;
			float rand_number = curand_uniform(my_curandstate + idx);
			for (int i = row * operators_number; i < row * operators_number + operators_number; i++)
			{
				sum += d_operators_probabilites[i];
				if (rand_number < sum)
					break;
				cont++;
			}

			start = row * dimensions;
			end = start + dimensions;
			float reward = -1.0;
			//Cross
			int newIndividuals[2 * 1000];
			if (cont == 0)
			{
				one_point_cross(newIndividuals, d_pop, start, end, my_curandstate + idx, dimensions, pop_size);
				int cont_new;
				if (newIndividuals[dimensions - 1] > d_pop[end - 1] && newIndividuals[dimensions - 1] > newIndividuals[2 * dimensions - 1])
				{
					cont_new = 0;
					for (int i = start; i < end; i++)
					{
						d_pop[i] = newIndividuals[cont_new];
						cont_new++;
					}
					reward = 1.0;
				}
				else if (newIndividuals[2 * dimensions - 1] > d_pop[end - 1] && newIndividuals[2 * dimensions - 1] > newIndividuals[dimensions - 1])
				{
					cont_new = dimensions;
					for (int i = start; i < end; i++)
					{
						d_pop[i] = newIndividuals[cont_new];
						cont_new++;
					}
					reward = 1.0;
				}

			}
			//Mutation
			else if (cont == 1)
			{
				mutation(newIndividuals, d_pop, start, end, my_curandstate + idx, dimensions);
				if (newIndividuals[dimensions - 1] > d_pop[end - 1])
				{
					int cont_new = 0;
					for (int i = start; i < end; i++)
					{
						d_pop[i] = newIndividuals[cont_new];
						cont_new++;
					}
					reward = 1.0;
				}
			}

			//Apply reward

			float plus = curand_uniform(my_curandstate + idx);
			plus = 1.0 + (plus * reward);
			d_operators_probabilites[row * operators_number + cont] *= plus;

			//Normalizes
			float sumP = 0.0;
			for (int i = row * operators_number; i < row * operators_number + operators_number; i++)
			{
				sumP += d_operators_probabilites[i];
			}

			for (int i = row * operators_number; i < row * operators_number + operators_number; i++)
			{
				d_operators_probabilites[i] /= sumP;
			}
		}
	}
}

