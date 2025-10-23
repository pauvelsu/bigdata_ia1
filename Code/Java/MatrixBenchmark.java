import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Random;

public class MatrixBenchmark {

    static final int N = 1024;
    static final int ITER = 10;

    public static double[][] initMatrix(Random rand) {
        double[][] M = new double[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                M[i][j] = rand.nextDouble();
            }
        }
        return M;
    }

    public static double[][] multiplyWithProgress(double[][] A, double[][] B, int iteration) {
        double[][] C = new double[N][N];
        int step = N / 10; // cada 10%
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < N; i++) {

            for (int j = 0; j < N; j++) {
                double sum = 0;
                for (int k = 0; k < N; k++) {
                    sum += A[i][k] * B[k][j];
                }
                C[i][j] = sum;
            }

            // Mostrar progreso: 10%, 20%, ..., 100%
            if ((i + 1) % step == 0) {
                double progress = (i + 1) / (double) N;
                long elapsed = System.currentTimeMillis() - startTime;
                long eta = (long)((elapsed / progress) - elapsed);
                System.out.println("Iteración " + iteration + "/" + ITER +
                        " – " + (int)(progress * 100) + "% completado – ETA: " + (eta / 1000) + "s");
            }
        }
        return C;
    }

    public static void main(String[] args) {

        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        Random rand = new Random();

        double[] wallTimes = new double[ITER];
        double[] cpuTimes = new double[ITER];
        double[] memoryPeaks = new double[ITER];

        for (int it = 0; it < ITER; it++) {

            double[][] A = initMatrix(rand);
            double[][] B = initMatrix(rand);

            Runtime runtime = Runtime.getRuntime();
            runtime.gc(); // limpiar memoria antes de medir
            long memBefore = (runtime.totalMemory() - runtime.freeMemory()) / 1024;

            long wallStart = System.currentTimeMillis();
            long cpuStart = threadBean.getCurrentThreadCpuTime(); // nanosegundos

            @SuppressWarnings("unused")
            double[][] C = multiplyWithProgress(A, B, it + 1);

            long wallEnd = System.currentTimeMillis();
            long cpuEnd = threadBean.getCurrentThreadCpuTime();

            long memAfter = (runtime.totalMemory() - runtime.freeMemory()) / 1024;

            double wall = (wallEnd - wallStart) / 1000.0;
            double cpu = (cpuEnd - cpuStart) / 1_000_000_000.0; // ns → s
            long memDiff = memAfter - memBefore;

            wallTimes[it] = wall;
            cpuTimes[it] = cpu;
            memoryPeaks[it] = memDiff;

            System.out.println("\n--- Iteración " + (it + 1) + " COMPLETADA ---");
            System.out.printf("Tiempo Wall: %.2f s%n", wall);
            System.out.printf("Tiempo CPU:  %.2f s%n", cpu);
            System.out.println("Memoria inicial: " + memBefore + " KB");
            System.out.println("Memoria final:   " + memAfter + " KB");
            System.out.println("Diferencia:      " + memDiff + " KB\n");
        }

        // Promedios
        double avgWall = 0, avgCpu = 0, avgMem = 0;
        for (int i = 0; i < ITER; i++) {
            avgWall += wallTimes[i];
            avgCpu += cpuTimes[i];
            avgMem += memoryPeaks[i];
        }

        System.out.println("\n=== RESULTADOS PROMEDIO (" + ITER + " iteraciones) ===");
        System.out.printf("Tiempo Wall Promedio: %.2f s%n", avgWall / ITER);
        System.out.printf("Tiempo CPU Promedio:  %.2f s%n", avgCpu / ITER);
        System.out.printf("Diferencia Memoria Promedio: %.2f KB%n", avgMem / ITER);
    }
}
