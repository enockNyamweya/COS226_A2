public class Consumer extends Thread {
    private final ConcurrentQueue<Job> queue;
    private final int jobsToConsume;
    private int jobsProcessed;

    public Consumer(ConcurrentQueue<Job> queue, int jobsToConsume) {
        this.queue = queue;
        this.jobsToConsume = jobsToConsume;
        this.jobsProcessed = 0;
    }

    @Override
    public void run() {
        // Continue until the required workload has been completed
        while (jobsProcessed < jobsToConsume) {
            Job job = queue.deq();
            
            // The lock-free queue may return null if it is empty, 
            // so we must handle this gracefully by only counting valid jobs.
            if (job != null) {
                jobsProcessed++;
            }
        }
    }
    
    public int getJobsProcessed() {
        return jobsProcessed;
    }
}
