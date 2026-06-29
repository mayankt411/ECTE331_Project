import java.util.concurrent.CountDownLatch;

/**
 * Low Priority Thread (Priority 2) - Records system activity.
 */
public class SystemLoggerThread extends Thread {
    private final MotorController controller;
    private final CountDownLatch startLatch;
    private final CountDownLatch finishLatch;
    private final CountDownLatch lockAcquiredLatch;

    public SystemLoggerThread(MotorController controller, CountDownLatch startLatch, CountDownLatch finishLatch, CountDownLatch lockAcquiredLatch) {
        super("SystemLogger");
        this.controller = controller;
        this.startLatch = startLatch;
        this.finishLatch = finishLatch;
        this.lockAcquiredLatch = lockAcquiredLatch;
        setPriority(2); // Low Priority
    }

    @Override
    public void run() {
        try {
            if (startLatch != null) startLatch.await();
            
            RoboticArmLogger.log(getName(), "Started execution. Attempting to acquire MotorController...");
            
            controller.accessResource(getName(), 8, lockAcquiredLatch); 
            
            RoboticArmLogger.log(getName(), "Finished execution.");
            
            if (finishLatch != null) finishLatch.countDown();
            
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}
