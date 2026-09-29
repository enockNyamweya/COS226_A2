
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeQueue<T> {
    AtomicReference<Node> head, tail;

    public LockFreeQueue() {
    }

    public void enq(T item) {
        Node e = new Node(item);

        // enqueuing alwsys happens because the queue is unbounded
        while (true) {
            Node last = this.tail.get();
            Node next = last.next.get();

            if (last == this.tail.get()) {
                if (next == null) {
                    if (last.next.compareAndSet(next, e)) {
                        this.tail.compareAndSet(last, e);
                        return;
                    }
                } else {
                    this.tail.compareAndSet(last, next);
                }
            }
        }
    }

    public T deq() throws EmptyException {
        // must throw an exception if the que is empty
        while (true) {
            Node first = this.head.get();
            Node last = this.tail.get();
            Node next = first.next.get();

            if (first == this.head.get()) {
                if (first == last) {
                    if (next == null) {
                        throw new EmptyException();
                    }

                    this.tail.compareAndSet(last, next);
                } else {
                    T value = next.value;
                    if (this.head.compareAndSet(first, next))
                        return value;
                }

            }

        }
    }

    protected class Node {
        T value;
        AtomicReference<Node> next;

        public Node(T value) {
            this.value = value;
            this.next = new AtomicReference<Node>(null);
        }
    }
}
