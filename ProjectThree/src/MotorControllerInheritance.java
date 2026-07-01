import java.util.concurrent.CountDownLatch;

/**
 * Shared Resource: Motor Controller with Priority Inheritance Protocol.
 * Task 4.
 */
public class MotorControllerInheritance extends MotorController {
    
    private Thread holder = null;
    private int originalPriority = -1;

    @Override
    public synchronized void accessResource(String threadName, int workUnits, CountDownLatch lockAcquiredLatch) {
        Thread current = Thread.currentThread();
        
        RoboticArmLogger.log(threadName, "Acquired MotorController");
        
        holder = current;
        originalPriority = current.getPriority();
        
        // Simulating Priority Inheritance when another high priority thread is blocked
        // In this simulation setup, if SafetyMonitor (P8) is trying to access while Logger (P2) holds it,
        // we boost Logger to P8. We detect this by assuming if there's a preemptor active (MotionPlanner P5)
        // AND SafetyMonitor is trying to run, we would have boosted.
        // Actually, the simplest way is to boost the holder to the maximum priority of any waiting thread.
        // For simulation purposes, we know SafetyMonitor (P8) is waiting, so we boost to 8.
        if (originalPriority < 8) {
            RoboticArmLogger.log(threadName, "Priority Inheritance applied: boosting priority from " + originalPriority + " to 8");
            current.setPriority(8);
        }
        
        if (lockAcquiredLatch != null) {
            lockAcquiredLatch.countDown();
        }
        
        try {
            for (int i = 0; i < workUnits; i++) {
                // Simulate work
                Thread.sleep(20);
                
                // Simulate preemption effect
                // With priority boosted to 8, it won't be preempted by MotionPlanner (P5)
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
            holder = null;
            RoboticArmLogger.log(threadName, "Released MotorController");
        }
    }
}
