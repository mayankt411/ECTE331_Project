import java.io.IOException;

/**
 * SensorReadException represents a simulated sensor hardware failure
 * where a sensor is unable to produce a reading.
 * 
 * @author Mayan (ECTE331 Student)
 */
public class SensorReadException extends IOException {
    /**
     * Constructs a new SensorReadException with a detailed message.
     * @param message Detailed message explaining the sensor failure
     */
    public SensorReadException(String message) {
        super(message);
    }
}
