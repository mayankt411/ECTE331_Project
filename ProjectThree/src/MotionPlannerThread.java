import java.util.concurrent.CountDownLatch;

/**
 * Medium Priority Thread (Priority 5) - Sends movement commands.
 * In the priority inversion scenario, this thread preempts the low priority thread.
 */
public class MotionPlannerThread extends Thread {
    private final CountDownLatch startLatch;
    private final CountDownLatch finishLatch;

    public MotionPlannerThread(CountDownLatch startLatch, CountDownLatch finishLatch) {
        super("MotionPlanner");
        this.startLatch = startLatch;
        this.finishLatch = finishLatch;
        setPriority(5); // Medium Priority
    }

    @Override
    public void run() {
        try {
            if (startLatch != null) startLatch.await();
            
            RoboticArmLogger.log(getName(), "Started execution. Preempting lower priority threads...");
            
            PriorityInversionScenario.isPreemptorActive = true;
            PriorityInversionScenario.preemptorPriority = getPriority();
            
            // Simulate long running task that doesn't need the MotorController
            // but keeps the CPU busy, thus preventing the Low priority thread from running
            for (int i = 0; i < 10; i++) {
                Thread.sleep(20); 
            }
            
            PriorityInversionScenario.isPreemptorActive = false;
            
            RoboticArmLogger.log(getName(), "Finished execution.");
            
            if (finishLatch != null) finishLatch.countDown();
            
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}
