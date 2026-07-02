import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Handles logging of events to both the log file and the console.
 * Prepend all file log entries with a date-time stamp.
 * 
 * @author Mayan (ECTE331 Student)
 */
public class LoggerUtility {
    private final String logFileName;
    private final DateTimeFormatter formatter;

    /**
     * Constructs a LoggerUtility.
     * 
     * @param logFileName The name of the file to write logs to.
     */
    public LoggerUtility(String logFileName) {
        this.logFileName = logFileName;
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    }

    /**
     * Logs an event to the log file and prints to the standard console.
     * 
     * @param eventType The category of the event (e.g., "INFO", "FAILURE")
     * @param message The detailed message to be logged
     */
    public void log(String eventType, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = String.format("[%s] [%s] %s", timestamp, eventType, message);
        
        // Print to console
        System.out.println(logEntry);

        // Write to file
        try (FileWriter fw = new FileWriter(logFileName, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(logEntry);
        } catch (IOException e) {
            System.err.println("CRITICAL: Failed to write to log file: " + e.getMessage());
        }
    }
}
