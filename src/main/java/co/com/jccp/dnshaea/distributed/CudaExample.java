package co.com.jccp.dnshaea.distributed;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcurand.JCurand;
import jcuda.jcurand.curandGenerator;
import jcuda.runtime.JCuda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static jcuda.jcurand.JCurand.*;
import static jcuda.jcurand.JCurand.curandDestroyGenerator;
import static jcuda.jcurand.curandRngType.CURAND_RNG_PSEUDO_DEFAULT;
import static jcuda.runtime.JCuda.*;
import static jcuda.runtime.cudaMemcpyKind.cudaMemcpyDeviceToHost;

public class CudaExample {

    /**
     * Entry point of this program
     *
     * @param args Not used
     */
    public static void main(String args[]) {


        // Enable exceptions and omit all subsequent error checks
        JCuda.setExceptionsEnabled(true);
        JCurand.setExceptionsEnabled(true);

        int n = 100;
        curandGenerator generator = new curandGenerator();

        // Allocate n floats on host
        float hostData[] = new float[n];

        // Allocate n floats on device
        Pointer deviceData = new Pointer();
        cudaMalloc(deviceData, n * Sizeof.FLOAT);

        // Create pseudo-random number generator
        curandCreateGenerator(generator, CURAND_RNG_PSEUDO_DEFAULT);

        // Set seed
        curandSetPseudoRandomGeneratorSeed(generator, 1234);

        // Generate n floats on device
        curandGenerateUniform(generator, deviceData, n);

        // Copy device memory to host
        cudaMemcpy(Pointer.to(hostData), deviceData,
                n * Sizeof.FLOAT, cudaMemcpyDeviceToHost);

        // Show result
        System.out.println(Arrays.toString(hostData));

        // Cleanup
        curandDestroyGenerator(generator);
        cudaFree(deviceData);


    }


}
