import java.util.concurrent.CountDownLatch;

/**
 * Shared Resource: Motor Controller with Priority Ceiling Protocol.
 * Task 5.
 */
public class MotorControllerCeiling extends MotorController {
    
    // Ceiling priority is set to the highest priority thread that accesses the resource
    private static final int CEILING_PRIORITY = 8; // Safety Monitor Priority
    
    @Override
    public synchronized void accessResource(String threadName, int workUnits, CountDownLatch lockAcquiredLatch) {
        Thread current = Thread.currentThread();
        int originalPriority = current.getPriority();
        
        // Priority Ceiling Protocol: raise priority immediately upon entering the critical section
        if (originalPriority < CEILING_PRIORITY) {
            RoboticArmLogger.log(threadName, "Priority Ceiling applied: boosting priority from " + originalPriority + " to " + CEILING_PRIORITY);
            current.setPriority(CEILING_PRIORITY);
        }
        
        RoboticArmLogger.log(threadName, "Acquired MotorController");
        
        if (lockAcquiredLatch != null) {
            lockAcquiredLatch.countDown();
        }
        
        try {
            for (int i = 0; i < workUnits; i++) {
                // Simulate work
                Thread.sleep(20);
                
                // Simulate preemption effect
                if (PriorityInversionScenario.isPreemptorActive && PriorityInversionScenario.preemptorPriority > current.getPriority()) {
                    RoboticArmLogger.log(threadName, "Preempted by medium priority thread... delaying");
                    Thread.sleep(20);
                }
            }
        } catch (InterruptedException e) {
            current.interrupt();
        } finally {
            if (current.getPriority() != originalPriority) {
                RoboticArmLogger.log(threadName, "Restoring original priority to " + originalPriority);
                current.setPriority(originalPriority);
            }
            RoboticArmLogger.log(threadName, "Released MotorController");
        }
    }
}
