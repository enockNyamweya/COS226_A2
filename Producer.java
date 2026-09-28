import java.util.concurrent.atomic.AtomicInteger;

public class Producer extends Thread {
    private final ConcurrentQueue<Job> queue;
    private final int jobsToProduce;
    
    // Shared counter to generate thread-safe, globally unique IDs across all producers
    private static final AtomicInteger JOB_COUNTER = new AtomicInteger(1);

    public Producer(ConcurrentQueue<Job> queue, int jobsToProduce) {
        this.queue = queue;
        this.jobsToProduce = jobsToProduce;
    }

    @Override
    public void run() {
        for (int i = 0; i < jobsToProduce; i++) {
            // Generates a unique ID and passes it to the Job constructor
            int jobId = JOB_COUNTER.getAndIncrement();
            Job job = new Job(jobId);
            
            // Place job into the shared queue
            queue.enq(job);
        }
    }
}
