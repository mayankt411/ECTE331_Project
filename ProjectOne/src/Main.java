import java.util.Random;

/**
 * Main application class ECTE331 project simulation loop.
 * Simulates drone flight and handles custom exceptions and reliability failures.
 * 
 * @author Mayan (ECTE331 Student)
 */
public class Main {
    /**
     * Private constructor; this class is not meant to be instantiated.
     * All logic runs from the static main method.
     */
    private Main() {}
    /**
     * Main entry point of the simulation. Runs the simulation loop
     * inside a try-catch block to gracefully enter SAFE MODE.
     * 
     * @param args CLI arguments (optional first argument to configure custom log suffix)
     */
    public static void main(String[] args) {
        // Dynamic log filename support
        String logFileName = "log.txt";
        if (args.length > 0) {
            logFileName = "log_" + args[0] + ".txt";
        } else {
            // Append random suffix to separate runs
            int suffix = new Random().nextInt(1000);
            logFileName = "log_" + suffix + ".txt";
        }

        LoggerUtility logger = new LoggerUtility(logFileName);
        logger.log("SYSTEM_START", "Drone Navigation Simulation started. Log file: " + logFileName);

        Sensor sensorA = new Sensor("Sensor A");
        Sensor sensorB = new Sensor("Sensor B");
        Sensor sensorC = new Sensor("Sensor C");

        // Start with a baseline altitude of 100 meters, which is safely in [0:200]
        DroneController controller = new DroneController(100, logger);

        // Simulation parameters
        int baselineAltitude = 100;
        int sensorRange = 10; // Fluctuation range
        int totalSteps = 25; // Run up to 25 cycles or until SAFE MODE triggers

        try {
            logger.log("FLIGHT_LOG", "Starting flight plan simulation...");
            for (int step = 1; step <= totalSteps; step++) {
                logger.log("FLIGHT_LOG", "--- Simulation Step " + step + " ---");
                
                // Let altitude climb slightly as drone progresses
                baselineAltitude = 100 + (step % 10) * 5; 

                Integer rA = null;
                Integer rB = null;
                Integer rC = null;

                // Read Sensor A
                try {
                    rA = sensorA.readSensor(baselineAltitude, sensorRange);
                } catch (SensorReadException e) {
                    logger.log("SENSOR_FAILURE", "Sensor A read failure: " + e.getMessage());
                }

                // Read Sensor B
                try {
                    rB = sensorB.readSensor(baselineAltitude, sensorRange);
                } catch (SensorReadException e) {
                    logger.log("SENSOR_FAILURE", "Sensor B read failure: " + e.getMessage());
                }

                // Read Sensor C
                try {
                    rC = sensorC.readSensor(baselineAltitude, sensorRange);
                } catch (SensorReadException e) {
                    logger.log("SENSOR_FAILURE", "Sensor C read failure: " + e.getMessage());
                }

                // Controller processes the readings
                int decidedAltitude = controller.processReadings(rA, rB, rC);
                logger.log("STATUS", "Current Drone Decided Altitude: " + decidedAltitude + " m");
                
                // Sleep for brief interval to simulate real-time operations
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
            logger.log("SYSTEM_STOP", "Flight simulation completed successfully without entering SAFE MODE.");
        } catch (SystemReliabilityException e) {
            logger.log("SAFE_MODE_ACTIVE", "CRITICAL SYSTEM STATE: Catch block triggered SAFE MODE transition.");
            logger.log("SAFE_MODE_ACTIVE", "Exception message: " + e.getMessage());
            logger.log("SYSTEM_STOP", "System shut down safely and stopped execution.");
        }
    }
}
