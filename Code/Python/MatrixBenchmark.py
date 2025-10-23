import random
import time
import tracemalloc

N = 1024
ITER = 10


def init_matrices():
    A = [[random.random() for _ in range(N)] for _ in range(N)]
    B = [[random.random() for _ in range(N)] for _ in range(N)]
    return A, B


def multiply_with_progress(A, B, iteration):
    C = [[0 for _ in range(N)] for _ in range(N)]
    progress_step = N // 10  # 10% chunks
    start_time = time.time()

    for i in range(N):
        for j in range(N):
            total = 0
            for k in range(N):
                total += A[i][k] * B[k][j]
            C[i][j] = total

        # Progreso cada 10%
        if (i + 1) % progress_step == 0:
            progress = (i + 1) / N
            elapsed = time.time() - start_time
            eta = (elapsed / progress) - elapsed
            print(f"Iteración {iteration}/{ITER} – {int(progress * 100)}% completado – ETA: {int(eta)}s")

    return C


wall_times = []
cpu_times = []
memory_usage = []

for it in range(1, ITER + 1):
    A, B = init_matrices()

    tracemalloc.start()
    mem_before = tracemalloc.get_traced_memory()[0]

    wall_start = time.time()
    cpu_start = time.process_time()

    C = multiply_with_progress(A, B, it)

    wall_end = time.time()
    cpu_end = time.process_time()

    mem_after, mem_peak = tracemalloc.get_traced_memory()
    tracemalloc.stop()

    wall = wall_end - wall_start
    cpu = cpu_end - cpu_start
    mem_diff = (mem_after - mem_before) / 1024
    mem_peak_kb = mem_peak / 1024

    wall_times.append(wall)
    cpu_times.append(cpu)
    memory_usage.append(mem_peak_kb)

    print(f"\n--- Iteración {it} COMPLETADA ---")
    print(f"Tiempo Wall: {wall:.2f} s")
    print(f"Tiempo CPU:  {cpu:.2f} s")
    print(f"Memoria inicial: {mem_before / 1024:.2f} KB")
    print(f"Memoria final:   {mem_after / 1024:.2f} KB")
    print(f"Uso peak:        {mem_peak_kb:.2f} KB")
    print(f"Diferencia:      {mem_diff:.2f} KB\n")

#Resultados promedio
print(f"\n=== RESULTADOS PROMEDIO ({ITER} iteraciones) ===")
print(f"Tiempo Wall Promedio: {sum(wall_times) / ITER:.2f} s")
print(f"Tiempo CPU Promedio:  {sum(cpu_times) / ITER:.2f} s")
print(f"Memoria Peak Promedio: {sum(memory_usage) / ITER:.2f} KB")