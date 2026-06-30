import java.util.concurrent.Semaphore;

/**
 * SyncMain is the main application entry point for ECTE331 Project Part 2:
 * Thread Synchronisation and Communication.
 *
 * <p>This class creates Thread A and Thread B, initialises all semaphores,
 * and runs the threads for a configurable high number of iterations to
 * verify that the synchronisation is correct and deterministic regardless
 * of the OS thread scheduler's behaviour.</p>
 *
 * <h2>Expected Final Values (computed analytically):</h2>
 * <ul>
 *   <li>A1 = 125,250</li>
 *   <li>B1 = 31,375</li>
 *   <li>B2 = 145,350</li>
 *   <li>A2 = 190,500</li>
 *   <li>B3 = 270,700</li>
 *   <li>A3 = 350,900</li>
 * </ul>
 *
 * @author mayankt411 (ECTE331 Student)
 */
public class SyncMain {

    /**
     * Private constructor; this class is not meant to be instantiated.
     * All logic runs from the static main method.
     */
    private SyncMain() {}

    // Expected correct values computed analytically
    private static final long EXPECTED_A1 = 125250L;
    private static final long EXPECTED_B1 = 31375L;
    private static final long EXPECTED_B2 = 145350L;
    private static final long EXPECTED_A2 = 190500L;
    private static final long EXPECTED_B3 = 270700L;
    private static final long EXPECTED_A3 = 350900L;

    /**
     * Main entry point. Runs the two cooperating threads for a high number
     * of iterations and verifies that the final variable values are always
     * consistent with the analytically computed expected results.
     *
     * <p>A high iteration count (e.g., 10,000) is used to expose any race
     * conditions that might only manifest under specific scheduling conditions.
     * If all iterations produce the correct values, the synchronisation is
     * considered verified.</p>
     *
     * @param args command-line arguments (not used)
     * @throws InterruptedException if the main thread is interrupted while waiting
     */
    public static void main(String[] args) throws InterruptedException {
        final int ITERATIONS = 10_000;
        int passCount = 0;
        int failCount = 0;

        System.out.println("=== ECTE331 Part 2: Thread Synchronisation Verification ===");
        System.out.printf("Running %,d iterations to verify correctness...%n%n", ITERATIONS);

        for (int iter = 1; iter <= ITERATIONS; iter++) {
            // Initialise fresh shared state for each iteration
            SharedVariables shared = new SharedVariables();

            /*
             * Semaphores are initialised with 0 permits (binary semaphores).
             * A thread calling acquire() on a semaphore with 0 permits will
             * block (without busy-waiting) until another thread calls release().
             * This is the correct non-busy-wait mechanism as required.
             */
            Semaphore semA1 = new Semaphore(0); // A releases after FuncA1; B waits before FuncB2
            Semaphore semB2 = new Semaphore(0); // B releases after FuncB2; A waits before FuncA2
            Semaphore semA2 = new Semaphore(0); // A releases after FuncA2; B waits before FuncB3
            Semaphore semB3 = new Semaphore(0); // B releases after FuncB3; A waits before FuncA3

            // Create and start both threads
            Thread threadA = new Thread(new ThreadA(shared, semA1, semB2, semA2, semB3), "Thread-A");
            Thread threadB = new Thread(new ThreadB(shared, semA1, semB2, semA2, semB3), "Thread-B");

            threadA.start();
            threadB.start();

            // Wait for both threads to finish before verifying results
            threadA.join();
            threadB.join();

            // Verify the computed values against the expected analytical results
            boolean pass = (shared.A1 == EXPECTED_A1)
                        && (shared.B1 == EXPECTED_B1)
                        && (shared.B2 == EXPECTED_B2)
                        && (shared.A2 == EXPECTED_A2)
                        && (shared.B3 == EXPECTED_B3)
                        && (shared.A3 == EXPECTED_A3);

            if (pass) {
                passCount++;
            } else {
                failCount++;
                // Print diagnostic information on failure
                System.err.printf("[FAIL] Iteration %d produced incorrect results:%n", iter);
                System.err.printf("  A1=%d (expected %d)%n", shared.A1, EXPECTED_A1);
                System.err.printf("  B1=%d (expected %d)%n", shared.B1, EXPECTED_B1);
                System.err.printf("  B2=%d (expected %d)%n", shared.B2, EXPECTED_B2);
                System.err.printf("  A2=%d (expected %d)%n", shared.A2, EXPECTED_A2);
                System.err.printf("  B3=%d (expected %d)%n", shared.B3, EXPECTED_B3);
                System.err.printf("  A3=%d (expected %d)%n", shared.A3, EXPECTED_A3);
            }

            // Print a sample result from the first iteration to show the values
            if (iter == 1) {
                System.out.println("--- Sample Output from Iteration 1 ---");
                System.out.printf("  A1 = %,d  (expected: %,d)%n", shared.A1, EXPECTED_A1);
                System.out.printf("  B1 = %,d  (expected: %,d)%n", shared.B1, EXPECTED_B1);
                System.out.printf("  B2 = %,d  (expected: %,d)%n", shared.B2, EXPECTED_B2);
                System.out.printf("  A2 = %,d  (expected: %,d)%n", shared.A2, EXPECTED_A2);
                System.out.printf("  B3 = %,d  (expected: %,d)%n", shared.B3, EXPECTED_B3);
                System.out.printf("  A3 = %,d  (expected: %,d)%n", shared.A3, EXPECTED_A3);
                System.out.println();
            }
        }

        // Final verification summary
        System.out.println("=== Verification Summary ===");
        System.out.printf("Total Iterations : %,d%n", ITERATIONS);
        System.out.printf("PASSED           : %,d%n", passCount);
        System.out.printf("FAILED           : %,d%n", failCount);
        if (failCount == 0) {
            System.out.println("Result: ALL ITERATIONS PASSED. Synchronisation is CORRECT.");
        } else {
            System.out.println("Result: SOME ITERATIONS FAILED. Synchronisation has ERRORS.");
        }
    }
}
