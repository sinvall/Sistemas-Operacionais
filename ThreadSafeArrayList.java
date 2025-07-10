import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Esta classe implementa uma lista baseada em ArrayList que é segura para uso
 * concorrente por múltiplas threads (thread-safe).
 *
 * Ela utiliza um ReadWriteLock para otimizar o desempenho, permitindo que
 * múltiplas threads leiam a lista simultaneamente, enquanto garante que as
 * operações de escrita (add, remove) sejam exclusivas.
 */
public class ThreadSafeArrayList<E> {

    // A lista interna que vamos proteger. É o nosso "recurso compartilhado".
    private final List<E> list = new ArrayList<>();

    // O nosso sistema inteligente de travas (locks).
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    /**
     * Adiciona um elemento à lista. Esta é uma operação de ESCRITA.
     * Portanto, devemos usar a trava de escrita (writeLock) para acesso exclusivo.
     *
     * @param element O elemento a ser adicionado.
     */
    public void add(E element) {
        writeLock.lock(); // Adquire a trava de escrita.
        try {
            // Seção Crítica: Apenas uma thread pode estar aqui por vez.
            list.add(element);
        } finally {
            // É CRUCIAL liberar a trava no bloco finally para garantir que,
            // mesmo em caso de erro, a trava seja liberada.
            writeLock.unlock();
        }
    }

    /**
     * Remove um elemento da lista pelo índice. Esta também é uma operação de ESCRITA.
     *
     * @param index O índice do elemento a ser removido.
     * @return O elemento que foi removido.
     */
    public E remove(int index) {
        writeLock.lock(); // Adquire a trava de escrita.
        try {
            return list.remove(index);
        } finally {
            writeLock.unlock(); // Libera a trava de escrita.
        }
    }

    /**
     * Obtém um elemento pelo seu índice. Esta é uma operação de LEITURA.
     * Usamos a trava de leitura (readLock) para permitir acesso simultâneo.
     *
     * @param index O índice do elemento a ser retornado.
     * @return O elemento na posição especificada.
     */
    public E get(int index) {
        readLock.lock(); // Adquire a trava de leitura.
        try {
            // Múltiplas threads podem estar lendo aqui ao mesmo tempo.
            return list.get(index);
        } finally {
            readLock.unlock(); // Libera a trava de leitura.
        }
    }

    /**
     * Retorna o número de elementos na lista. Esta é uma operação de LEITURA.
     *
     * @return o tamanho da lista.
     */
    public int size() {
        readLock.lock(); // Adquire a trava de leitura.
        try {
            return list.size();
        } finally {
            readLock.unlock(); // Libera a trava de leitura.
        }
    }
}