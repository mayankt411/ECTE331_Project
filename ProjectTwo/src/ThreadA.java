import java.util.concurrent.Semaphore;

/**
 * ThreadA implements the execution sequence for Thread A in the
 * ECTE331 thread synchronisation problem.
 *
 * <p>Thread A executes three functions in order: FuncA1, FuncA2, FuncA3.
 * Its execution is partially dependent on Thread B completing FuncB2
 * and FuncB3 before Thread A can proceed with FuncA2 and FuncA3
 * respectively.</p>
 *
 * <p>Synchronisation is implemented using binary {@link Semaphore} objects
 * initialised to 0 permits. This blocks Thread A at the required
 * dependency points until Thread B signals completion. No active waiting
 * or {@code Thread.sleep} is used.</p>
 *
 * <p>Dependency diagram for Thread A:</p>
 * <pre>
 *   FuncA1 --> [signal semA1] --> FuncA2 (waits on semB2) --> [signal semA2] --> FuncA3 (waits on semB3)
 * </pre>
 *
 * @author mayankt411 (ECTE331 Student)
 */
public class ThreadA implements Runnable {

    /** Shared variables updated by this thread. */
    private final SharedVariables shared;

    /**
     * Semaphore signalled by Thread A after FuncA1 completes.
     * Thread B acquires this before starting FuncB2.
     */
    private final Semaphore semA1;

    /**
     * Semaphore signalled by Thread B after FuncB2 completes.
     * Thread A acquires this before starting FuncA2.
     */
    private final Semaphore semB2;

    /**
     * Semaphore signalled by Thread A after FuncA2 completes.
     * Thread B acquires this before starting FuncB3.
     */
    private final Semaphore semA2;

    /**
     * Semaphore signalled by Thread B after FuncB3 completes.
     * Thread A acquires this before starting FuncA3.
     */
    private final Semaphore semB3;

    /**
     * Constructs ThreadA with references to all shared state and semaphores.
     *
     * @param shared the shared variable object
     * @param semA1  binary semaphore for signalling A1 completion
     * @param semB2  binary semaphore for waiting on B2 completion
     * @param semA2  binary semaphore for signalling A2 completion
     * @param semB3  binary semaphore for waiting on B3 completion
     */
    public ThreadA(SharedVariables shared,
                   Semaphore semA1, Semaphore semB2,
                   Semaphore semA2, Semaphore semB3) {
        this.shared = shared;
        this.semA1  = semA1;
        this.semB2  = semB2;
        this.semA2  = semA2;
        this.semB3  = semB3;
    }

    /**
     * Executes FuncA1, FuncA2, and FuncA3 in sequence with semaphore-based
     * synchronisation to enforce the execution dependency shown in Figure 2.1.
     */
    @Override
    public void run() {
        try {
            // --- FuncA1: A1 = sum(0..500) ---
            // No dependency; can run immediately
            funcA1();

            // Signal Thread B that A1 is ready (Thread B needs A1 for FuncB2)
            semA1.release();

            // --- FuncA2: A2 = B2 + sum(0..300) ---
            // Must wait until Thread B has computed B2
            semB2.acquire();
            funcA2();

            // Signal Thread B that A2 is ready (Thread B needs A2 for FuncB3)
            semA2.release();

            // --- FuncA3: A3 = B3 + sum(0..400) ---
            // Must wait until Thread B has computed B3
            semB3.acquire();
            funcA3();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread A was interrupted: " + e.getMessage());
        }
    }

    /**
     * FuncA1: Computes A1 = sum from i=0 to 500.
     * No prior dependency required.
     */
    private void funcA1() {
        shared.A1 = MathUtility.sumTo(500);
    }

    /**
     * FuncA2: Computes A2 = B2 + sum from i=0 to 300.
     * Requires B2 to be computed by Thread B first.
     */
    private void funcA2() {
        shared.A2 = shared.B2 + MathUtility.sumTo(300);
    }

    /**
     * FuncA3: Computes A3 = B3 + sum from i=0 to 400.
     * Requires B3 to be computed by Thread B first.
     */
    private void funcA3() {
        shared.A3 = shared.B3 + MathUtility.sumTo(400);
    }
}
