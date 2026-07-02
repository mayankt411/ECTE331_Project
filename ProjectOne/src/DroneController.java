import java.util.ArrayList;
import java.util.List;

/**
 * DroneController handles Triple Modular Redundancy (TMR) majority voting,
 * reliability checking, fallback mechanisms, and triggers safe mode actions.
 * 
 * @author Mayan (ECTE331 Student)
 */
public class DroneController {
    private int previousAltitude;
    private int consecutiveFailures;
    private final LoggerUtility logger;

    /**
     * Initializes the controller with a default baseline altitude.
     * 
     * @param initialAltitude The initial valid altitude of the drone
     * @param logger The logging utility to use
     */
    public DroneController(int initialAltitude, LoggerUtility logger) {
        this.previousAltitude = initialAltitude;
        this.consecutiveFailures = 0;
        this.logger = logger;
    }

    /**
     * Performs majority voting on the three sensor readings, identifies faulty/corrupted/outlier
     * sensors, tracks system reliability, and updates or falls back on the altitude.
     * 
     * @param sensorAVal The reading from Sensor A (null if failed)
     * @param sensorBVal The reading from Sensor B (null if failed)
     * @param sensorCVal The reading from Sensor C (null if failed)
     * @return The decided altitude value
     * @throws SystemReliabilityException if two consecutive reliability failures occur
     */
    public int processReadings(Integer sensorAVal, Integer sensorBVal, Integer sensorCVal) throws SystemReliabilityException {
        logger.log("INPUT", String.format("Sensor A: %s, Sensor B: %s, Sensor C: %s", 
                formatVal(sensorAVal), formatVal(sensorBVal), formatVal(sensorCVal)));

        List<String> outliers = new ArrayList<>();
        List<Integer> validReadings = new ArrayList<>();
        List<String> validSensorNames = new ArrayList<>();

        // 1. Identify sensor failures (nulls) and corrupted (out-of-bounds) readings
        checkSensorStatus("Sensor A", sensorAVal, validReadings, validSensorNames, outliers);
        checkSensorStatus("Sensor B", sensorBVal, validReadings, validSensorNames, outliers);
        checkSensorStatus("Sensor C", sensorCVal, validReadings, validSensorNames, outliers);

        // 2. Reliability Rule Check 1: Do we have at least 2 valid sensor readings?
        if (validReadings.size() < 2) {
            handleReliabilityFailure("Fewer than 2 valid sensor readings exist. Outliers: " + outliers);
            return previousAltitude;
        }

        // 3. Majority Voting Decision
        Integer decision = null;
        if (validReadings.size() == 3) {
            int a = validReadings.get(0);
            int b = validReadings.get(1);
            int c = validReadings.get(2);

            if (a == b) {
                decision = a;
                if (a != c) outliers.add("Sensor C");
            } else if (a == c) {
                decision = a;
                outliers.add("Sensor B");
            } else if (b == c) {
                decision = b;
                outliers.add("Sensor A");
            }
        } else {
            // Exactly 2 valid readings
            int a = validReadings.get(0);
            int b = validReadings.get(1);
            if (a == b) {
                decision = a;
            }
        }

        // 4. Handle Decision and Fallback
        if (decision != null) {
            // Majority found
            previousAltitude = decision;
            consecutiveFailures = 0; // Reset consecutive failures on success
            
            if (!outliers.isEmpty()) {
                logger.log("OUTLIER_DETECTION", "Outlier sensors detected: " + outliers);
            }
            logger.log("MAJORITY_DECISION", "Majority vote successful. Altitude set to: " + decision + " m");
            return decision;
        } else {
            // Reliability Rule Check 2: All valid outputs differ (no majority found)
            logger.log("OUTLIER_DETECTION", "All sensor outputs differ. Outliers: " + validSensorNames);
            handleReliabilityFailure("No majority found among sensor readings. All outputs differ.");
            logger.log("FALLBACK_DECISION", "Falling back to previous altitude: " + previousAltitude + " m");
            return previousAltitude;
        }
    }

    private void checkSensorStatus(String name, Integer val, List<Integer> validReadings, List<String> validSensorNames, List<String> outliers) {
        if (val == null) {
            logger.log("SENSOR_FAILURE", name + " failed (exception thrown)");
            outliers.add(name);
        } else if (val < 0 || val > 200) {
            logger.log("CORRUPTED_READING", name + " produced corrupted reading: " + val + " m");
            outliers.add(name);
        } else {
            validReadings.add(val);
            validSensorNames.add(name);
        }
    }

    private void handleReliabilityFailure(String message) throws SystemReliabilityException {
        consecutiveFailures++;
        logger.log("RELIABILITY_FAILURE", String.format("Reliability failure occurred (%d/2 consecutive). Detail: %s", consecutiveFailures, message));
        
        if (consecutiveFailures >= 2) {
            logger.log("SAFE_MODE_ACTIVATION", "CRITICAL: Two consecutive failures reached. Activating SAFE MODE!");
            throw new SystemReliabilityException("System entered SAFE MODE due to critical reliability failures.");
        }
    }

    private String formatVal(Integer val) {
        return (val == null) ? "FAILED" : val + "m";
    }

    /**
     * Gets the previous valid altitude.
     * 
     * @return current internal altitude
     */
    public int getAltitude() {
        return previousAltitude;
    }
}
