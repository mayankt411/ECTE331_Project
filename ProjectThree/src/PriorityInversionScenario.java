import java.util.concurrent.CountDownLatch;

/**
 * Orchestrator for demonstrating Priority Inversion and Protocols.
 */
public class PriorityInversionScenario {
    public static volatile boolean isPreemptorActive = false;
    public static volatile int preemptorPriority = -1;

    public static long runScenario(MotorController controller, boolean muteLogs) throws InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(3);
        CountDownLatch lockAcquiredLatch = new CountDownLatch(1);

        SystemLoggerThread lowThread = new SystemLoggerThread(controller, startLatch, finishLatch, lockAcquiredLatch);
        MotionPlannerThread mediumThread = new MotionPlannerThread(null, finishLatch); // null startLatch so we control it manually
        SafetyMonitorThread highThread = new SafetyMonitorThread(controller, null, finishLatch); // null startLatch so we control it manually

        if (!muteLogs) RoboticArmLogger.log("Main", "Starting Low Priority Thread (SystemLogger)");
        lowThread.start();
        startLatch.countDown(); // Let lowThread start

        // Wait until Low Priority thread acquires the lock
        lockAcquiredLatch.await();

        if (!muteLogs) RoboticArmLogger.log("Main", "Starting High Priority Thread (SafetyMonitor)");
        highThread.start();

        // Give High Priority thread a tiny bit of time to get blocked on the lock
        Thread.sleep(10); 

        if (!muteLogs) RoboticArmLogger.log("Main", "Starting Medium Priority Thread (MotionPlanner)");
        mediumThread.start();

        finishLatch.await();
        if (!muteLogs) RoboticArmLogger.log("Main", "Scenario finished.");
        
        return highThread.getWaitTime();
    }
}
