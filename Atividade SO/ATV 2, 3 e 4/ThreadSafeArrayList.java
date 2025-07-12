import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeArrayList<E> {

   
    private final List<E> list = new ArrayList<>();

    
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
        writeLock.lock(); 
        try {
            return list.remove(index);
        } finally {
            writeLock.unlock(); 
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
        readLock.lock(); 
        try {
            
            return list.get(index);
        } finally {
            readLock.unlock(); 
        }
    }

    /**
     * Retorna o número de elementos na lista. Esta é uma operação de LEITURA.
     *
     * @return o tamanho da lista.
     */
    public int size() {
        readLock.lock(); 
        try {
            return list.size();
        } finally {
            readLock.unlock(); 
        }
    }
}
