package scheduling;

import memory.SharedVector;
import memory.VectorOrientation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TiredExecutorTest {

    @Test
    void submit_Null() throws InterruptedException {
        TiredExecutor executor = new TiredExecutor(2);
        try {
            assertThrows(IllegalArgumentException.class,
                    () -> executor.submit(null));
        } finally {
            executor.shutdown();
        }
    }

    @Test
    void submit_runsTask() throws InterruptedException {
        TiredExecutor executor = new TiredExecutor(2);
        try {
            CountDownLatch done = new CountDownLatch(1);

            executor.submit(done::countDown);

            assertTrue(done.await(1, TimeUnit.SECONDS),
                    "Task did not run");
        } finally {
            executor.shutdown();
        }
    }


    @Test
    void executor_runs_mixed_vector_tasks() throws InterruptedException {
        TiredExecutor executor = new TiredExecutor(4);
        try {
            SharedVector v1 =
                    new SharedVector(new double[]{1,2,3}, VectorOrientation.ROW_MAJOR);
            SharedVector v2 =
                    new SharedVector(new double[]{4,5,6}, VectorOrientation.COLUMN_MAJOR);

            CountDownLatch done = new CountDownLatch(2);

            executor.submit(() -> {
                v1.transpose();
                done.countDown();
            });

            executor.submit(() -> {
                v2.negate();
                done.countDown();
            });

            assertTrue(done.await(1, TimeUnit.SECONDS));
            assertEquals(VectorOrientation.COLUMN_MAJOR, v1.getOrientation());
            double[] expected_v2 = new double[]{-4,-5,-6};
            for(int i = 0; i<v2.length(); i++){
                assertEquals(expected_v2[i],v2.get(i));
            }
        } finally {
            executor.shutdown();
        }
    }

    @Test
    void submit_manyTasks_allRun() throws InterruptedException {
        TiredExecutor executor = new TiredExecutor(4);
        try {
            int N = 200;
            CountDownLatch done = new CountDownLatch(N);
            AtomicInteger counter = new AtomicInteger(0);

            for (int i = 0; i < N; i++) {
                executor.submit(() -> {
                    counter.incrementAndGet();
                    done.countDown();
                });
            }

            assertTrue(done.await(3, TimeUnit.SECONDS), "Not all tasks finished");
            assertEquals(N, counter.get(), "Some tasks were lost or duplicated");
        } finally {
            executor.shutdown();
        }
    }


    @Test
    void submitAll_waitsForCompletion() throws InterruptedException {
        TiredExecutor executor = new TiredExecutor(3);
        try {
            int N = 50;
            AtomicInteger counter = new AtomicInteger(0);
            List<Runnable> tasks = new ArrayList<>();

            for (int i = 0; i < N; i++) {
                tasks.add(() -> {
                    counter.incrementAndGet();
                    try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                });
            }

            executor.submitAll(tasks);
            assertEquals(N, counter.get(), "submitAll returned before all tasks completed");
        } finally {
            executor.shutdown();
        }
    }


    @Test
    void taskThrows_executorStillWorks() throws InterruptedException {
        TiredExecutor executor = new TiredExecutor(2);
        try {
            CountDownLatch done = new CountDownLatch(1);

            executor.submit(() -> { throw new RuntimeException("boom"); });
            executor.submit(done::countDown);

            assertTrue(done.await(1, TimeUnit.SECONDS), "Executor got stuck after exception");
        } finally {
            executor.shutdown();
        }
    }


    @Test
    void getWorkerReport_containsAllWorkers() throws InterruptedException {
        int numThreads = 4;
        TiredExecutor executor = new TiredExecutor(numThreads);
        try {
            CountDownLatch done = new CountDownLatch(20);
            for (int i = 0; i < 20; i++) {
                executor.submit(done::countDown);
            }
            assertTrue(done.await(1, TimeUnit.SECONDS));

            String report = executor.getWorkerReport();
            assertNotNull(report);
            assertFalse(report.isBlank());

            for (int i = 0; i < numThreads; i++) {
                assertTrue(report.contains("Worker number:" + i),
                        "Report missing worker " + i);
            }
        } finally {
            executor.shutdown();
        }
    }
}
