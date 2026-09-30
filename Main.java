public class Main {
    private static final int TOTAL_JOBS = 12000;
    private static final int QUEUE_CAPACITY = 5;
    private static final int[][] CONFIGURATIONS = {
        {4,4}, {6,2}, {2,6}
    };

    static void runExperiment(ConcurrentQueue<Job> queue, int producers, int consumers) throws InterruptedException {
        int jobsPerProducer = TOTAL_JOBS / producers;
        int jobsPerConsumer = TOTAL_JOBS / consumers;

        Thread[] prodThreads = new Thread[producers];
        Thread[] consThreads = new Thread[consumers];

        for (int i = 0; i < prodThreads.length; i++) {
            prodThreads[i] = new Producer(queue, jobsPerProducer);
        }

        for (int i = 0; i < consThreads.length; i++) {
            consThreads[i] = new Consumer(queue, jobsPerConsumer);
        }

        long startTime = System.nanoTime();

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

        int totalProcessed = 0;

        for (int i = 0; i < consThreads.length; i++) {
            Consumer cons = (Consumer) consThreads[i];
            totalProcessed += cons.getJobsProcessed();
        }

        System.out.println("Produced: " + TOTAL_JOBS);
        System.out.println("Processed: " + totalProcessed);

        long endTime = System.nanoTime();

        double execTime = (endTime - startTime) / 1000000000.0;
        double throughput = TOTAL_JOBS / execTime;

        System.out.println("Producers: " + producers + ", Consumers: " + consumers + ", Time: " + execTime + " seconds, Throughput: " + throughput + " jobs/s");
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < CONFIGURATIONS.length; i++) {
            int producers = CONFIGURATIONS[i][0];
            int consumers = CONFIGURATIONS[i][1];

            System.out.println();
            System.out.println("Configuration: " + producers + " Producers / " + consumers + " Consumers");

            ConcurrentQueue<Job> bq = new BoundedQueue<Job>(QUEUE_CAPACITY);
            System.out.println("Bounded Queue:");
            runExperiment(bq, producers, consumers);

            ConcurrentQueue<Job> lfq = new LockFreeQueue<Job>();
            System.out.println("Lock-Free Queue:");
            runExperiment(lfq, producers, consumers);
        }
    }
}
