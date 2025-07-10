import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Barbearia {

    // Constantes para deixar o código mais legível e fácil de modificar.
    static final int CADEIRAS_DE_ESPERA = 10;
    static final int TOTAL_BARBEIROS = 1;

    // A fila de espera. Usamos ArrayBlockingQueue por ser uma fila thread-safe
    // com tamanho fixo (bounded), ideal para o nosso buffer limitado.
    // Ela armazenará os IDs dos clientes.
    static BlockingQueue<Integer> filaDeEspera = new ArrayBlockingQueue<>(CADEIRAS_DE_ESPERA);

    // Um contador atômico para gerar IDs únicos para os clientes de forma segura
    // entre as threads. Evita condições de corrida na geração do ID.
    static AtomicInteger proximoIdCliente = new AtomicInteger(1);

    public static void main(String[] args) {
        System.out.println("Iniciando a barbearia com " + TOTAL_BARBEIROS + " barbeiros e " + CADEIRAS_DE_ESPERA + " cadeiras de espera.");
        System.out.println("---------------------------------------------------------");

        // --- Criando e iniciando as Threads dos Barbeiros ---
        // Eles são criados uma vez e ficam em um loop infinito esperando clientes.
        for (int i = 1; i <= TOTAL_BARBEIROS; i++) {
            Barbeiro barbeiro = new Barbeiro(i);
            Thread threadBarbeiro = new Thread(barbeiro);
            threadBarbeiro.start();
        }

        // --- Criando um Gerador de Clientes ---
        // Esta thread será responsável por simular a chegada de novos clientes
        // em intervalos de tempo aleatórios.
        Thread geradorClientes = new Thread(() -> {
            try {
                while (true) {
                    // Gera um ID para o novo cliente
                    int idCliente = proximoIdCliente.getAndIncrement();
                    
                    // Cria e inicia a thread do cliente
                    Cliente cliente = new Cliente(idCliente);
                    Thread threadCliente = new Thread(cliente);
                    threadCliente.start();

                    // Simula o tempo de chegada de um novo cliente (entre 4 e 6 segundos)
                    // Math.random() * (max - min + 1) + min
                    int tempoChegada = (int) (Math.random() * 3 + 4);
                    TimeUnit.SECONDS.sleep(tempoChegada);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        geradorClientes.start();
    }
}
