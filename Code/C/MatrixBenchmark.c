#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>
#include <sys/resource.h>
#include <time.h>

#define N 1024
#define ITER 10

double A[N][N];
double B[N][N];
double C[N][N];

void init_matrices() {
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            A[i][j] = (double) rand() / RAND_MAX;
            B[i][j] = (double) rand() / RAND_MAX;
            C[i][j] = 0;
        }
    }
}

void multiply() {
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            double sum = 0;
            for (int k = 0; k < N; k++) {
                sum += A[i][k] * B[k][j];
            }
            C[i][j] = sum;
        }
    }
}

double get_wall_time() {
    struct timeval time;
    gettimeofday(&time, NULL);
    return (double)time.tv_sec + (double)time.tv_usec * 1e-6;
}

double get_cpu_time() {
    struct rusage r_usage;
    getrusage(RUSAGE_SELF, &r_usage);
    return (double)r_usage.ru_utime.tv_sec + (double)r_usage.ru_utime.tv_usec * 1e-6;
}

long get_memory_kb() {
    struct rusage r_usage;
    getrusage(RUSAGE_SELF, &r_usage);
    return r_usage.ru_maxrss; // KB
}

int main() {
    srand(time(NULL));

    double wall_times[ITER], cpu_times[ITER];
    long mem_before, mem_after;

    for (int it = 0; it < ITER; it++) {
        init_matrices();

        mem_before = get_memory_kb();
        double wall_start = get_wall_time();
        double cpu_start = get_cpu_time();

        multiply();

        double wall_end = get_wall_time();
        double cpu_end = get_cpu_time();
        mem_after = get_memory_kb();

        wall_times[it] = wall_end - wall_start;
        cpu_times[it] = cpu_end - cpu_start;

        printf("\n--- Iteración %d ---\n", it + 1);
        printf("Tiempo Wall: %.6f s\n", wall_times[it]);
        printf("Tiempo CPU: %.6f s\n", cpu_times[it]);
        printf("Memoria inicial: %ld KB\n", mem_before);
        printf("Memoria final:   %ld KB\n", mem_after);
        printf("Diferencia:      %ld KB\n", mem_after - mem_before);
    }

    // Calcular promedio
    double wall_sum = 0, cpu_sum = 0;
    for (int i = 0; i < ITER; i++) {
        wall_sum += wall_times[i];
        cpu_sum += cpu_times[i];
    }

    printf("\n=== RESULTADOS PROMEDIO (%d iteraciones) ===\n", ITER);
    printf("Tiempo Wall Promedio: %.6f s\n", wall_sum / ITER);
    printf("Tiempo CPU Promedio:  %.6f s\n", cpu_sum / ITER);

    return 0;
}
