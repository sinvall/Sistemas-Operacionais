import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Classe de teste para simular o acesso concorrente ao DatabaseManager.
 *
 * Ela cria múltiplas threads para realizar operações de leitura e escrita
 * simultaneamente, demonstrando o funcionamento do controle de acesso.
 */
public class DatabaseTest {

    public static void main(String[] args) {
        System.out.println("Iniciando simulação de acesso ao Banco de Dados...");
        DatabaseManager dbManager = new DatabaseManager();

        // Usamos um ExecutorService para gerenciar nosso pool de threads.
        // Teremos 15 threads no total (12 leitoras + 3 escritoras).
        ExecutorService executor = Executors.newFixedThreadPool(15);

        // --- Criando as Threads Leitoras (Readers) ---
        // Criamos 12 threads que tentarão ler constantemente.
        // Como o limite é 10, veremos 2 threads esperando.
        for (int i = 0; i < 12; i++) {
            final int readerId = i + 1;
            executor.submit(() -> {
                Thread.currentThread().setName("Leitor-" + readerId);
                try {
                    while (true) {
                        dbManager.read(0); // Tenta ler o primeiro registro.
                        TimeUnit.MILLISECONDS.sleep(200); // Pequena pausa antes de tentar de novo.
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // --- Criando as Threads Escritoras (Writers) ---
        // Thread para a operação CREATE
        executor.submit(() -> {
            Thread.currentThread().setName("Escritor-CREATE");
            try {
                TimeUnit.SECONDS.sleep(1); // Espera um pouco para os leitores começarem.
                dbManager.create("Novo Registro via Thread");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Thread para a operação UPDATE
        executor.submit(() -> {
            Thread.currentThread().setName("Escritor-UPDATE");
            try {
                TimeUnit.SECONDS.sleep(5); // Espera um pouco mais.
                dbManager.update(1, "Registro Atualizado");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Thread para a operação DELETE
        executor.submit(() -> {
            Thread.currentThread().setName("Escritor-DELETE");
            try {
                TimeUnit.SECONDS.sleep(10); // Espera ainda mais.
                // CORREÇÃO: Usa o método público para verificar o tamanho.
                if (dbManager.getDataSize() > 0) {
                    dbManager.delete(0);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // O programa principal continuará executando a simulação.
        // Para um teste finito, você poderia usar executor.awaitTermination().
    }
}
