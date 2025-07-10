import java.util.concurrent.TimeUnit;

public class Barbeiro implements Runnable {

    private final int id;

    public Barbeiro(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("Barbeiro " + id + " está pronto para trabalhar.");
        try {
            while (true) {
                System.out.println("Barbeiro " + id + " está dormindo, esperando clientes...");
                // O barbeiro bloqueia aqui se a fila estiver vazia.
                int idCliente = Barbearia.filaDeEspera.take();

                // CORREÇÃO: A mensagem sobre o estado da fila é impressa aqui,
                // pois o barbeiro tem a informação mais precisa neste momento.
                System.out.println("=> Barbeiro " + id + " acordou para atender Cliente " + idCliente + ". Restam " + Barbearia.filaDeEspera.size() + " na espera.");

                // Simula o tempo do corte de cabelo (entre 5 e 10 segundos)
                int tempoCorte = (int) (Math.random() * 6 + 5);
                TimeUnit.SECONDS.sleep(tempoCorte);
                
                System.out.println("<= Barbeiro " + id + " terminou o corte do Cliente " + idCliente + ".");
            }
        } catch (InterruptedException e) {
            System.out.println("Barbeiro " + id + " foi interrompido.");
            Thread.currentThread().interrupt();
        }
    }
}