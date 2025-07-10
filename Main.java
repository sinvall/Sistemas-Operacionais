import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList; // A lista comum, para comparação
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Teste com a nossa lista SEGURA
        ThreadSafeArrayList<Integer> safeList = new ThreadSafeArrayList<>();
        runTest("Lista Segura", items -> safeList.add(items), () -> safeList.size());

        System.out.println("\n----------------------------------\n");

        // Teste com a lista COMUM (que vai falhar)
        List<Integer> unsafeList = new ArrayList<>();
        runTest("Lista Comum (Insegura)", items -> unsafeList.add(items), () -> unsafeList.size());
    }

    // Interface funcional para simplificar o teste
    @FunctionalInterface
    interface AddOperation { void add(int item); }
    @FunctionalInterface
    interface SizeOperation { int size(); }

    private static void runTest(String testName, AddOperation addOp, SizeOperation sizeOp) throws InterruptedException {
        System.out.println(" Iniciando teste: " + testName);
        int threadCount = 10;
        int itemsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < itemsPerThread; j++) {
                    addOp.add(j);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        int expectedSize = threadCount * itemsPerThread;
        int finalSize = sizeOp.size();

        System.out.println("Tamanho esperado: " + expectedSize);
        System.out.println("Tamanho final: " + finalSize);

        if (finalSize == expectedSize) {
            System.out.println(" SUCESSO: O resultado está correto.");
        } else {
            System.out.println(" FALHA: Condição de corrida ocorreu! Itens foram perdidos.");
        }
    }
}
