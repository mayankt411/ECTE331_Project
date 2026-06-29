import java.util.concurrent.Semaphore;

/**
 * ThreadB implements the execution sequence for Thread B in the
 * ECTE331 thread synchronisation problem.
 *
 * <p>Thread B executes three functions in order: FuncB1, FuncB2, FuncB3.
 * FuncB2 depends on Thread A completing FuncA1, and FuncB3 depends on
 * Thread A completing FuncA2.</p>
 *
 * <p>Synchronisation is implemented using binary {@link Semaphore} objects
 * initialised to 0 permits. Thread B signals Thread A after each of its
 * own completions, and acquires the appropriate semaphore before
 * executing functions that depend on Thread A's output. No active waiting
 * or {@code Thread.sleep} is used.</p>
 *
 * <p>Dependency diagram for Thread B:</p>
 * <pre>
 *   FuncB1 --> FuncB2 (waits on semA1) --> [signal semB2] --> FuncB3 (waits on semA2) --> [signal semB3]
 * </pre>
 *
 * @author mayankt411 (ECTE331 Student)
 */
public class ThreadB implements Runnable {

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
     * Constructs ThreadB with references to all shared state and semaphores.
     *
     * @param shared the shared variable object
     * @param semA1  binary semaphore for waiting on A1 completion
     * @param semB2  binary semaphore for signalling B2 completion
     * @param semA2  binary semaphore for waiting on A2 completion
     * @param semB3  binary semaphore for signalling B3 completion
     */
    public ThreadB(SharedVariables shared,
                   Semaphore semA1, Semaphore semB2,
                   Semaphore semA2, Semaphore semB3) {
        this.shared = shared;
        this.semA1  = semA1;
        this.semB2  = semB2;
        this.semA2  = semA2;
        this.semB3  = semB3;
    }

    /**
     * Executes FuncB1, FuncB2, and FuncB3 in sequence with semaphore-based
     * synchronisation to enforce the execution dependency shown in Figure 2.1.
     */
    @Override
    public void run() {
        try {
            // --- FuncB1: B1 = sum(0..250) ---
            // No dependency; can run immediately in parallel with FuncA1
            funcB1();

            // --- FuncB2: B2 = A1 + sum(0..200) ---
            // Must wait until Thread A has computed A1
            semA1.acquire();
            funcB2();

            // Signal Thread A that B2 is ready (Thread A needs B2 for FuncA2)
            semB2.release();

            // --- FuncB3: B3 = A2 + sum(0..400) ---
            // Must wait until Thread A has computed A2
            semA2.acquire();
            funcB3();

            // Signal Thread A that B3 is ready (Thread A needs B3 for FuncA3)
            semB3.release();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread B was interrupted: " + e.getMessage());
        }
    }

    /**
     * FuncB1: Computes B1 = sum from i=0 to 250.
     * No prior dependency required.
     */
    private void funcB1() {
        shared.B1 = MathUtility.sumTo(250);
    }

    /**
     * FuncB2: Computes B2 = A1 + sum from i=0 to 200.
     * Requires A1 to be computed by Thread A first.
     */
    private void funcB2() {
        shared.B2 = shared.A1 + MathUtility.sumTo(200);
    }

    /**
     * FuncB3: Computes B3 = A2 + sum from i=0 to 400.
     * Requires A2 to be computed by Thread A first.
     */
    private void funcB3() {
        shared.B3 = shared.A2 + MathUtility.sumTo(400);
    }
}
