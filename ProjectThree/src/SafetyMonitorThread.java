import java.util.concurrent.CountDownLatch;

/**
 * High Priority Thread (Priority 8) - Detects emergency conditions and stops the arm.
 */
public class SafetyMonitorThread extends Thread {
    private final MotorController controller;
    private final CountDownLatch startLatch;
    private final CountDownLatch finishLatch;
    private long waitTime = 0;

    public SafetyMonitorThread(MotorController controller, CountDownLatch startLatch, CountDownLatch finishLatch) {
        super("SafetyMonitor");
        this.controller = controller;
        this.startLatch = startLatch;
        this.finishLatch = finishLatch;
        setPriority(8); // High Priority
    }

    @Override
    public void run() {
        try {
            if (startLatch != null) startLatch.await();
            
            RoboticArmLogger.log(getName(), "Started execution. Attempting to acquire MotorController...");
            long startTime = System.currentTimeMillis();
            
            controller.accessResource(getName(), 4); // 4 work units
            
            waitTime = System.currentTimeMillis() - startTime;
            RoboticArmLogger.log(getName(), "Finished execution. Wait/Total time: " + waitTime + " ms");
            
            if (finishLatch != null) finishLatch.countDown();
            
        } catch (InterruptedException e) {
            interrupt();
        }
    }
    
    public long getWaitTime() {
        return waitTime;
    }
}
