/**
 * MathUtility provides a static utility method for computing the sum
 * of integers from 0 to n using a loop structure.
 *
 * <p>This class is used by both Thread A and Thread B to evaluate
 * their respective function expressions as part of the ECTE331
 * thread synchronisation project.</p>
 *
 * @author mayankt411 (ECTE331 Student)
 */
public class MathUtility {

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class and should not be instantiated.
     */
    private MathUtility() {}

    /**
     * Computes the sum of integers from 0 to n (inclusive) using a loop.
     *
     * <p>Mathematically equivalent to n * (n + 1) / 2, but implemented
     * iteratively as required by the project specification.</p>
     *
     * @param n the upper bound of the summation (inclusive), must be &gt;= 0
     * @return the sum of all integers from 0 to n
     */
    public static long sumTo(int n) {
        long sum = 0;
        for (int i = 0; i <= n; i++) {
            sum += i;
        }
        return sum;
    }
}
