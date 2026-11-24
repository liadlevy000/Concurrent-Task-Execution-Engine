package scheduling;

import memory.SharedVector;
import memory.VectorOrientation;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class TiredThreadTest {
    Runnable transpose_task = () -> {
        SharedVector sharedVector = new SharedVector(new double[]{1,2,3}, VectorOrientation.ROW_MAJOR);
        sharedVector.transpose();
    };
    Runnable negate_task = () -> {
        SharedVector sharedVector = new SharedVector(new double[]{1,2,3}, VectorOrientation.COLUMN_MAJOR);
        sharedVector.negate();
    };


    @Test
    void newTask_TaskNull(){
        TiredThread thread = new TiredThread(1,0.7);
        assertThrows(IllegalArgumentException.class, () -> thread.newTask(null));
    }

    @Test
    void new_Task_Not_Alive(){
        TiredThread thread = new TiredThread(1,0.7);
        thread.shutdown();
        assertThrows(IllegalStateException.class, () -> thread.newTask(transpose_task));
    }

    @Test
    void new_Task_Busy(){
        TiredThread thread = new TiredThread(1,0.7);
        thread.setBusy(true);
        assertThrows(IllegalStateException.class, () -> thread.newTask(transpose_task));
    }

    @Test
    void new_Task_Two_Tasks(){
        TiredThread thread = new TiredThread(1,0.7);
        thread.newTask(transpose_task);
        assertThrows(IllegalStateException.class, () -> thread.newTask(negate_task));
    }

    @Test
    void new_Task_One_Task_Not_ThrowingException(){
        TiredThread thread = new TiredThread(1,0.7);
        assertDoesNotThrow(() -> thread.newTask(transpose_task));
    }

    @Test
    void tiredThread_executesTransposeTaskSuccessfully() throws Exception {
        SharedVector v =
                new SharedVector(new double[]{1, 2, 3}, VectorOrientation.ROW_MAJOR);

        TiredThread t = new TiredThread(1, 0.7);
        t.start();

        CountDownLatch done = new CountDownLatch(1);

        t.newTask(() -> {
            v.transpose();
            done.countDown();
        });

        assertTrue(done.await(1, TimeUnit.SECONDS),
                "Task did not finish");

        assertEquals(VectorOrientation.COLUMN_MAJOR, v.getOrientation());

        t.shutdown();
        t.join(1000);
        assertFalse(t.isAlive());
    }

    @Test
    void compareTo_Fatigue_Unique() {
        TiredThread thread1 = new TiredThread(1,0.7);
        TiredThread thread2 = new TiredThread(2,0.7);
        thread1.setTimeUsed(1000);
        thread2.setTimeUsed(2000);

        assertTrue(thread1.compareTo(thread2) < 0);

    }

    @Test
    void compareTo_Fatigue_Same() {
        TiredThread thread1 = new TiredThread(1,0.7);
        TiredThread thread2 = new TiredThread(2,0.7);
        thread1.setTimeUsed(1000);
        thread2.setTimeUsed(1000);

        assertTrue(thread1.compareTo(thread2) < 0);
    }
}
