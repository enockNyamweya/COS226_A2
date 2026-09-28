public class Main {
    private static final int TOTAL_JOBS = 12000;
    private static final int QUEUE_CAPACITY = 5;
    private static final int[][] CONFIGURATIONS = {
        {4,4}, {6,2}, {2,6}
    };

    void runExperiment(ConcurrentQueue<Job> queue, int producers, int consumers) throws InterruptedException {
        int jobsPerProducer = TOTAL_JOBS / producers;

        Thread[] prodThreads = new Thread[producers];
        Thread[] consThreads = new Thread[consumers];

        for (int i = 0; i < prodThreads.length; i++) {
            prodThreads[i] = new Producer(...);
        }

        for (int i = 0; i < consThreads.length; i++) {
            consThreads[i] = new Consumer(...);
        }

        for (Thread thread : prodThreads) {
            thread.start();
        }

        for (Thread thread : consThreads) {
            thread.start();
        }

        for (Thread thread : prodThreads) {
            thread.join();
        }

        for (Thread thread : consThreads) {
            thread.join();
        }
    }
    public static void main(String[] args) throws InterruptedException {
        BoundedQueue<Job> bq = new BoundedQueue<Job>(QUEUE_CAPACITY);
        LockFreeQueue<Job> lfq = new LockFreeQueue<Job>();
        for (int i = 0; i < CONFIGURATIONS.length; i++) {
            int producers = CONFIGURATIONS[i][0];
            int consumers = CONFIGURATIONS[i][1];

            runExperiment(bq, producers, consumers);
            runExperiment(lfq, producers, consumers);
        }
    }
}
