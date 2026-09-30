
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

public class BoundedQueue<T> implements ConcurrentQueue<T> {

    // By Patrick:
    /*
     * 1. Enquing and dequiuig can safely happen at the same time since we are
     * operating on opositing ends of the queue
     * 2. Enqueue happens at the rear while dequeue happens at the front
     * 3. so we need to have 2 locks in order to allow for this functionality
     * 
     * 4. the queue is, hence the limit
     * 5. Everything else is self explainatory meyn
     */
    ReentrantLock enqLock, deqLock;
    Condition notEmptyCondition, notFullCondition;
    AtomicInteger size;

    volatile Node head, tail;
    int capacity;

    public BoundedQueue(int _capacity) {
        this.capacity = _capacity;
        this.head = new Node(null);
        this.tail = this.head;
        this.size = new AtomicInteger(0);

        // Enqueue
        this.enqLock = new ReentrantLock();
        this.notFullCondition = this.enqLock.newCondition();

        // Dequeue
        this.deqLock = new ReentrantLock();
        this.notEmptyCondition = this.deqLock.newCondition();
    }

    @Override
    public void enq(T item) {
        boolean wakeDequeuers = false;
        this.enqLock.lock();

        try {
            while (this.size.get() == this.capacity)
                this.notFullCondition.await();

            Node e = new Node(item);
            this.tail.next = e;
            this.tail = e;

            // the queue is no longer ampty so dequeuers can dequeue from the queue
            // hence they must be informed typeshi
            if (this.size.getAndIncrement() == 0)
                wakeDequeuers = true;

        } catch (InterruptedException e) {

        } finally {
            this.enqLock.unlock();
        }

        // do we have to inform anybody who tried to dequeue when the queue ws empty
        // that the queue is no longer empty
        // i.e wakeDequeuer == true?
        if (wakeDequeuers) {
            this.deqLock.lock();
            try {
                this.notEmptyCondition.signalAll();
            } finally {
                this.deqLock.unlock();
            }
        }
    }

    @Override
    public T deq() {
        T result = null;
        boolean wakeEnqueuers = false;

        this.deqLock.lock();

        try {
            while (this.size.get() == 0)
                this.notEmptyCondition.await();

            result = this.head.next.value;
            this.head = this.head.next;

            if (this.size.getAndDecrement() == capacity)
                wakeEnqueuers = true;
        } catch (InterruptedException e) {
        } finally {
            this.deqLock.unlock();
        }

        if (wakeEnqueuers) {
            this.enqLock.lock();
            try {
                this.notFullCondition.signalAll();

            } finally {
                this.enqLock.unlock();
            }
        }

        return result;
    }

    protected class Node {
        public T value;
        public volatile Node next;

        public Node(T x) {
            this.value = x;
            this.next = null;
        }
    }
}
