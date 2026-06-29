/**
 * SharedVariables holds all shared mutable state between Thread A and Thread B.
 *
 * <p>Variables A1, A2, A3 are updated by Thread A; B1, B2, B3 are updated by Thread B.
 * These are declared as {@code volatile} to ensure memory visibility across threads,
 * since synchronisation is managed externally via semaphores rather than
 * through synchronised blocks on these variables directly.</p>
 *
 * @author mayankt411 (ECTE331 Student)
 */
public class SharedVariables {

    /**
     * Constructs a new SharedVariables instance with all values initialised to zero.
     */
    public SharedVariables() {}

    /** Result of FuncA1: sum from 0 to 500. Updated by Thread A. */
    public volatile long A1 = 0;

    /** Result of FuncA2: B2 + sum from 0 to 300. Updated by Thread A. */
    public volatile long A2 = 0;

    /** Result of FuncA3: B3 + sum from 0 to 400. Updated by Thread A. */
    public volatile long A3 = 0;

    /** Result of FuncB1: sum from 0 to 250. Updated by Thread B. */
    public volatile long B1 = 0;

    /** Result of FuncB2: A1 + sum from 0 to 200. Updated by Thread B. */
    public volatile long B2 = 0;

    /** Result of FuncB3: A2 + sum from 0 to 400. Updated by Thread B. */
    public volatile long B3 = 0;

    /**
     * Resets all shared variables back to zero.
     * Called at the start of each iteration in the verification loop.
     */
    public void reset() {
        A1 = 0;
        A2 = 0;
        A3 = 0;
        B1 = 0;
        B2 = 0;
        B3 = 0;
    }
}
