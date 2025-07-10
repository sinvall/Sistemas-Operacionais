public class Cliente implements Runnable {
    
    private final int id;

    public Cliente(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("CHEGOU: Cliente " + id + " chegou na barbearia.");
        
        // Tenta adicionar a si mesmo na fila de espera.
        boolean conseguiuLugar = Barbearia.filaDeEspera.offer(this.id);
        
        if (conseguiuLugar) {
            // CORREÇÃO: A mensagem agora é mais simples e não tenta adivinhar
            // o estado da fila, evitando a condição de corrida no log.
            System.out.println("ESPERANDO: Cliente " + id + " conseguiu um lugar na fila de espera.");
        } else {
            System.out.println("FOI EMBORA: Barbearia cheia! Cliente " + id + " foi embora.");
        }
        // A thread do cliente encerra sua execução aqui em ambos os casos.
    }
}
