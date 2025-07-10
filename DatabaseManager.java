import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Esta classe gerencia o acesso a um recurso de banco de dados simulado.
 *
 * Regras de Acesso:
 * 1.  Leitura (Read): Permite até 10 leituras simultâneas. A 11ª tentativa
 * será bloqueada.
 * 2.  Escrita (Create, Update, Delete): Permite apenas 1 operação de escrita
 * por vez.
 * 3.  Exclusão Mútua: Nenhuma operação de leitura pode ocorrer durante uma
 * operação de escrita, e vice-versa.
 */
public class DatabaseManager {

    // Semáforo para limitar o número de leitores concorrentes a 10.
    private final Semaphore readPermits = new Semaphore(10);

    // ReadWriteLock para garantir a exclusão mútua entre leitores e escritores.
    // Múltiplos leitores podem obter o lock de leitura, mas apenas um escritor
    // pode obter o lock de escrita (que bloqueia todos os outros).
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock(true); // 'true' para modo justo
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    // Simula a tabela do banco de dados com uma lista de strings.
    private final List<String> data = new ArrayList<>();

    public DatabaseManager() {
        // Populando o banco com alguns dados iniciais.
        data.add("Registro 1");
        data.add("Registro 2");
        data.add("Registro 3");
    }

    // --- OPERAÇÕES CRUD ---

    /**
     * Cria (insere) um novo registro no banco de dados.
     * Operação de ESCRITA.
     * @param record O registro a ser adicionado.
     */
    public void create(String record) throws InterruptedException {
        writeLock.lock(); // Adquire o lock de escrita exclusivo.
        try {
            System.out.println("--> " + Thread.currentThread().getName() + " obteve LOCK DE ESCRITA para CREATE.");
            // Simula o tempo da operação de escrita.
            TimeUnit.SECONDS.sleep(2);
            data.add(record);
            System.out.println("    " + Thread.currentThread().getName() + " criou o registro: '" + record + "'.");
        } finally {
            System.out.println("<-- " + Thread.currentThread().getName() + " liberou LOCK DE ESCRITA.");
            writeLock.unlock(); // Libera o lock.
        }
    }

    /**
     * Lê um registro do banco de dados pelo índice.
     * Operação de LEITURA.
     * @param index O índice do registro a ser lido.
     * @return O registro na posição especificada.
     */
    public String read(int index) throws InterruptedException {
        readPermits.acquire(); // Tenta adquirir um dos 10 permits de leitura. Bloqueia se não houver.
        readLock.lock(); // Adquire o lock de leitura.
        try {
            System.out.println("-> " + Thread.currentThread().getName() + " obteve LOCK DE LEITURA. Permits restantes: " + readPermits.availablePermits());
            // Simula o tempo da operação de leitura.
            TimeUnit.MILLISECONDS.sleep(500);
            String record = data.get(index % data.size()); // Modulo para evitar erros de índice.
            System.out.println("   " + Thread.currentThread().getName() + " leu o registro: '" + record + "'.");
            return record;
        } finally {
            System.out.println("<- " + Thread.currentThread().getName() + " liberou LOCK DE LEITURA.");
            readLock.unlock(); // Libera o lock de leitura.
            readPermits.release(); // Libera o permit de leitura.
        }
    }

    /**
     * Atualiza um registro existente no banco de dados.
     * Operação de ESCRITA.
     * @param index O índice do registro a ser atualizado.
     * @param newRecord O novo valor para o registro.
     */
    public void update(int index, String newRecord) throws InterruptedException {
        writeLock.lock(); // Adquire o lock de escrita exclusivo.
        try {
            System.out.println("--> " + Thread.currentThread().getName() + " obteve LOCK DE ESCRITA para UPDATE.");
            // Simula o tempo da operação de escrita.
            TimeUnit.SECONDS.sleep(2);
            data.set(index, newRecord);
            System.out.println("    " + Thread.currentThread().getName() + " atualizou o registro no índice " + index + " para '" + newRecord + "'.");
        } finally {
            System.out.println("<-- " + Thread.currentThread().getName() + " liberou LOCK DE ESCRITA.");
            writeLock.unlock(); // Libera o lock.
        }
    }

    /**
     * Deleta um registro do banco de dados.
     * Operação de ESCRITA.
     * @param index O índice do registro a ser deletado.
     */
    public void delete(int index) throws InterruptedException {
        writeLock.lock(); // Adquire o lock de escrita exclusivo.
        try {
            System.out.println("--> " + Thread.currentThread().getName() + " obteve LOCK DE ESCRITA para DELETE.");
            // Simula o tempo da operação de escrita.
            TimeUnit.SECONDS.sleep(2);
            String removed = data.remove(index);
            System.out.println("    " + Thread.currentThread().getName() + " deletou o registro: '" + removed + "'.");
        } finally {
            System.out.println("<-- " + Thread.currentThread().getName() + " liberou LOCK DE ESCRITA.");
            writeLock.unlock(); // Libera o lock.
        }
    }

    /**
     * Retorna o tamanho atual da lista de dados de forma segura.
     * @return o número de registros no banco.
     */
    public int getDataSize() {
        readLock.lock(); // Usa o lock de leitura para garantir consistência.
        try {
            return data.size();
        } finally {
            readLock.unlock();
        }
    }
}
