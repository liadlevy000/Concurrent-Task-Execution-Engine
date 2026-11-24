package scheduling;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        // TODO
        workers = new TiredThread[numThreads];
        for(int i = 0; i<numThreads; i++) {
            workers[i] = new TiredThread(i,0.5 + Math.random());
            idleMinHeap.add(workers[i]);
            workers[i].start();
        }
    }

    public void submit(Runnable task) {
        // TODO
        if(task == null) {
            throw new IllegalArgumentException("task is null");
        }
        try { //firstly, we try to take the least fatigue thread
            final TiredThread t = idleMinHeap.take();
            inFlight.incrementAndGet();
            Runnable wrapper = new Runnable() {// wrapper for the task in order to be able to return the thread to the queue
                 @Override
                 public void run(){
                     long start = System.nanoTime();
                     try {
                         task.run();

                     } finally {
                         if(inFlight.decrementAndGet() == 0) {
                             synchronized (TiredExecutor.this) {
                                 TiredExecutor.this.notifyAll();
                             }
                         }
                         long end = System.nanoTime();
                         t.setTimeUsed(end - start);
                         t.setIdleStartTime(end);
                         t.setBusy(false);
                         idleMinHeap.add(t);
                     }
                }
            };
             try{ // trying to implement a new task for our current thread
                 t.newTask(wrapper);
             } catch (IllegalStateException e) { // if throws exception we return the queue to the original state
                 idleMinHeap.add(t);
                 inFlight.decrementAndGet();
                 throw e;
             }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }

    public void submitAll(Iterable<Runnable> tasks) {
        // TODO: submit tasks one by one and wait until all finish
        for(Runnable task : tasks) { // submit the task
            submit(task);
        }
        synchronized (TiredExecutor.this){ // synchronized for waiting
            while(inFlight.get() > 0) {
                try {
                    TiredExecutor.this.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    public void shutdown() throws InterruptedException {
        // TODO
        //wait for all in-flight tasks to finish
        synchronized (this) {
            while (inFlight.get() > 0) {
                this.wait();
            }
        }
        //ask threads to stop
        for (TiredThread t : workers) {
            t.shutdown();
        }
        // wait for threads to terminate
        for (TiredThread t : workers) {
            t.join();
        }
    }

    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
        StringBuilder report = new StringBuilder();
        double avg_fatigue = 0;
        double fairness = 0;
        for(TiredThread t : workers) {
            avg_fatigue += t.getFatigue();
        }
        avg_fatigue /= workers.length;
        for(int i = 0; i<workers.length; i++) {
            int id = workers[i].getWorkerId();
            double fatigue = workers[i].getFatigue();
            boolean busy = workers[i].isBusy();
            long timeUsed = workers[i].getTimeUsed();
            long timeIdle = workers[i].getTimeIdle();
            report.append("\n Worker number:").append(id).append("\n")
                    .append("Fatigue:").append(fatigue).append("\n")
                    .append("Is busy:").append(busy).append("\n")
                    .append("Time used:").append(timeUsed).append("\n")
                    .append("Time idle:").append(timeIdle).append("\n");
            fairness += Math.pow(fatigue-avg_fatigue,2);
        }
        report.append("\nFairness:").append(fairness).append("\n");
        return report.toString();
    }



}
