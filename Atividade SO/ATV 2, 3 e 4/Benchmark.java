import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Benchmark {

    static final int OPERATIONS = 100_000;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== TESTE COM 1 THREAD ===");
        runSingleThreadTest();

        System.out.println("\n=== TESTE COM 16 THREADS ===");
        runMultiThreadTest();
    }

    public static void runSingleThreadTest() {
        int[] sizes = {10_000, 100_000, 500_000, 1_000_000};

        for (int size : sizes) {
            System.out.println("\n>> Tamanho da lista: " + size);

            List<Integer> arrayList = new ArrayList<>();
            ThreadSafeArrayList<Integer> threadSafeList = new ThreadSafeArrayList<>();
            List<Integer> vector = new Vector<>();

            benchmark("ArrayList", arrayList, size);
            benchmark("ThreadSafeArrayList", threadSafeList, size);
            benchmark("Vector", vector, size);
        }
    }

    @SuppressWarnings("unchecked")
    public static void benchmark(String name, Object listObj, int size) {
        Random rand = new Random();

        long start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            if (listObj instanceof List) {
                ((List<Integer>) listObj).add(i);
            } else if (listObj instanceof ThreadSafeArrayList) {
                ((ThreadSafeArrayList<Integer>) listObj).add(i);
            }
        }
        long insertTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            int index = rand.nextInt(size);
            if (listObj instanceof List) {
                ((List<Integer>) listObj).get(index);
            } else if (listObj instanceof ThreadSafeArrayList) {
                ((ThreadSafeArrayList<Integer>) listObj).get(index);
            }
        }
        long searchTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = size - 1; i >= 0; i--) {
            if (listObj instanceof List) {
                ((List<Integer>) listObj).remove(i);
            } else if (listObj instanceof ThreadSafeArrayList) {
                ((ThreadSafeArrayList<Integer>) listObj).remove(i);
            }
        }
        long removeTime = System.nanoTime() - start;

        System.out.printf("%s:\n", name);
        System.out.printf("  Inserção: %.2f ms (%.2f ops/s)\n", insertTime / 1e6, size / (insertTime / 1e9));
        System.out.printf("  Busca:    %.2f ms (%.2f ops/s)\n", searchTime / 1e6, size / (searchTime / 1e9));
        System.out.printf("  Remoção:  %.2f ms (%.2f ops/s)\n", removeTime / 1e6, size / (removeTime / 1e9));
    }

    public static void runMultiThreadTest() throws InterruptedException {
        testWithMultipleThreads("ThreadSafeArrayList", new ThreadSafeArrayList<>());
        testWithMultipleThreads("Vector", new Vector<>());
    }

    @SuppressWarnings("unchecked")
    public static void testWithMultipleThreads(String name, Object listObj) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(16);
        Random rand = new Random();
        AtomicInteger counter = new AtomicInteger();

        long start = System.nanoTime();
        for (int i = 0; i < 16; i++) {
            executor.submit(() -> {
                for (int j = 0; j < OPERATIONS; j++) {
                    if (listObj instanceof List) {
                        ((List<Integer>) listObj).add(counter.getAndIncrement());
                    } else if (listObj instanceof ThreadSafeArrayList) {
                        ((ThreadSafeArrayList<Integer>) listObj).add(counter.getAndIncrement());
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.MINUTES);
        long insertTime = System.nanoTime() - start;

        executor = Executors.newFixedThreadPool(16);
        start = System.nanoTime();
        for (int i = 0; i < 16; i++) {
            executor.submit(() -> {
                for (int j = 0; j < OPERATIONS; j++) {
                    if (listObj instanceof List) {
                        List<Integer> list = (List<Integer>) listObj;
                        if (!list.isEmpty()) {
                            int idx = rand.nextInt(list.size());
                            list.get(idx);
                        }
                    } else if (listObj instanceof ThreadSafeArrayList) {
                        ThreadSafeArrayList<Integer> tsList = (ThreadSafeArrayList<Integer>) listObj;
                        if (tsList.size() > 0) {
                            int idx = rand.nextInt(tsList.size());
                            tsList.get(idx);
                        }
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.MINUTES);
        long searchTime = System.nanoTime() - start;

        executor = Executors.newFixedThreadPool(16);
        start = System.nanoTime();
        for (int i = 0; i < 16; i++) {
            executor.submit(() -> {
                for (int j = 0; j < OPERATIONS; j++) {
                    synchronized (listObj) {
                        if (listObj instanceof List) {
                            List<Integer> list = (List<Integer>) listObj;
                            if (!list.isEmpty()) {
                                list.remove(0);
                            }
                        } else if (listObj instanceof ThreadSafeArrayList) {
                            ThreadSafeArrayList<Integer> tsList = (ThreadSafeArrayList<Integer>) listObj;
                            if (tsList.size() > 0) {
                                tsList.remove(0);
                            }
                        }
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.MINUTES);
        long removeTime = System.nanoTime() - start;

        int totalOps = 16 * OPERATIONS;

        System.out.printf("\n%s:\n", name);
        System.out.printf("  Inserção: %.2f s (%.2f ops/s)\n", insertTime / 1e9, totalOps / (insertTime / 1e9));
        System.out.printf("  Busca:    %.2f s (%.2f ops/s)\n", searchTime / 1e9, totalOps / (searchTime / 1e9));
        System.out.printf("  Remoção:  %.2f s (%.2f ops/s)\n", removeTime / 1e9, totalOps / (removeTime / 1e9));
    }
}
