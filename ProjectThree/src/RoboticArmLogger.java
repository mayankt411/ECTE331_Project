import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe logger for the robotic arm system.
 */
public class RoboticArmLogger {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final Lock lock = new ReentrantLock();

    public static void log(String threadName, String action) {
        lock.lock();
        try {
            String timestamp = LocalDateTime.now().format(formatter);
            System.out.printf("[%s] [%-15s] %s%n", timestamp, threadName, action);
        } finally {
            lock.unlock();
        }
    }
}
