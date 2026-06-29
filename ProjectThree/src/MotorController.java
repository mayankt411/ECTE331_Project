import java.util.concurrent.CountDownLatch;

/**
 * Shared Resource: Motor Controller (Baseline - No Protocol)
 * Tasks 1, 2, 3.
 */
public class MotorController {
    
    public synchronized void accessResource(String threadName, int workUnits) {
        accessResource(threadName, workUnits, null);
    }
    
    // Simulate lock acquisition using synchronized block
    public synchronized void accessResource(String threadName, int workUnits, CountDownLatch lockAcquiredLatch) {
        RoboticArmLogger.log(threadName, "Acquired MotorController");
        if (lockAcquiredLatch != null) {
            lockAcquiredLatch.countDown();
        }
        
        try {
            for (int i = 0; i < workUnits; i++) {
                // Simulate work
                Thread.sleep(20);
                
                // Simulate preemption effect for Priority Inversion (Task 3)
                // If a medium priority thread (preemptor) is active, it delays the execution
                if (PriorityInversionScenario.isPreemptorActive && PriorityInversionScenario.preemptorPriority > Thread.currentThread().getPriority()) {
                    RoboticArmLogger.log(threadName, "Preempted by medium priority thread... delaying");
                    Thread.sleep(20); // Penalty delay
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            RoboticArmLogger.log(threadName, "Released MotorController");
        }
    }
}
