import java.util.concurrent.*;
import java.util.concurrent.locks.*;

class BancoDados {
    private final Semaphore leituraPermitida = new Semaphore(10);
    private final ReentrantLock escritaLock = new ReentrantLock();
    private final ReadWriteLock controleAcesso = new ReentrantReadWriteLock();

    public void create(String dado) throws InterruptedException {
        escrever(() -> System.out.println("CREATE: Inserindo dado - " + dado));
    }

    public void read(int id) throws InterruptedException {
        leituraPermitida.acquire();
        controleAcesso.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + " READ: lendo dado id=" + id);
            Thread.sleep(1000);
        } finally {
            controleAcesso.readLock().unlock();
            leituraPermitida.release();
        }
    }

    public void update(int id, String novoValor) throws InterruptedException {
        escrever(() -> System.out.println("UPDATE: Atualizando id=" + id + " para " + novoValor));
    }

    public void delete(int id) throws InterruptedException {
        escrever(() -> System.out.println("DELETE: Removendo id=" + id));
    }

    private void escrever(Runnable operacao) throws InterruptedException {
        escritaLock.lock();
        controleAcesso.writeLock().lock();
        try {
            operacao.run();
            Thread.sleep(2000);
        } finally {
            controleAcesso.writeLock().unlock();
            escritaLock.unlock();
        }
    }
}

public class TesteBancoDados {
    public static void main(String[] args) {
        BancoDados banco = new BancoDados();
        ExecutorService executor = Executors.newFixedThreadPool(20);

        Runnable leitura = () -> {
            try {
                banco.read((int) (Math.random() * 100));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Runnable escrita = () -> {
            try {
                int acao = new java.util.Random().nextInt(3);
                int id = new java.util.Random().nextInt(100);
                switch (acao) {
                    case 0 -> banco.create("Dado " + id);
                    case 1 -> banco.update(id, "NovoValor" + id);
                    case 2 -> banco.delete(id);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        for (int i = 0; i < 30; i++) {
            if (i % 5 == 0) {
                executor.submit(escrita);
            } else {
                executor.submit(leitura);
            }
        }

        executor.shutdown();
    }
}
