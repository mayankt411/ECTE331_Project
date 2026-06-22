/**
 * SystemReliabilityException is thrown when the drone navigation system's 
 * reliability thresholds are violated, forcing a transition to SAFE MODE.
 * 
 * @author Mayan (ECTE331 Student)
 */
public class SystemReliabilityException extends Exception {
    /**
     * Constructs a new SystemReliabilityException with a detailed message.
     * @param message Detailed message explaining the reliability failure
     */
    public SystemReliabilityException(String message) {
        super(message);
    }
}
