import java.util.Random;

/**
 * Simulates a single physical altitude sensor on the drone.
 * Incorporates random behavior logic to simulate healthy readings,
 * hardware failures (exceptions), and corrupted readings (out-of-bound values).
 * 
 * @author Mayan (ECTE331 Student)
 */
public class Sensor {
    private final String sensorId;
    private final Random random;

    /**
     * Constructs a Sensor with a specific ID.
     * 
     * @param sensorId The unique identifier for this sensor (e.g., "Sensor A")
     */
    public Sensor(String sensorId) {
        this.sensorId = sensorId;
        this.random = new Random();
    }

    /**
     * Reads the current simulated altitude from the sensor.
     * Simulates failures, corruption, and valid readings based on a probability distribution.
     * 
     * @param baselineValue The true baseline altitude of the drone
     * @param range The range of random fluctuation for a valid reading
     * @return An integer altitude reading in the range [0:200] meters if valid
     * @throws SensorReadException if a simulated hardware failure occurs (0-14% chance)
     */
    public int readSensor(int baselineValue, int range) throws SensorReadException {
        int chance = random.nextInt(100); // Generate 0 to 99 inclusive

        if (chance < 15) {
            // 0-14: Sensor failure (exception thrown)
            throw new SensorReadException("Hardware failure detected in " + sensorId);
        } else if (chance < 30) {
            // 15-29: Corrupted reading (out of bounds, e.g., negative or > 200)
            // Generate a value outside [0:200]
            if (random.nextBoolean()) {
                return -random.nextInt(50) - 1; // Negative value [-50 to -1]
            } else {
                return 201 + random.nextInt(50); // Value greater than 200 [201 to 250]
            }
        } else {
            // 30-99: Valid reading [0:200]
            // baselineValue + random.nextInt(range)
            int val = baselineValue + (random.nextInt(range) - (range / 2));
            // Keep it bounded to valid range for safety, but typically it is [0:200]
            if (val < 0) val = 0;
            if (val > 200) val = 200;
            return val;
        }
    }

    /**
     * Gets the sensor identifier.
     * 
     * @return the sensor ID string
     */
    public String getSensorId() {
        return sensorId;
    }
}
