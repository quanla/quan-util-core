package qj.util.funct;

/**
 * Represent a function that accept one parameter and return value
 * @param <A> The only parameter
 * @param <T> The return value
 */
public interface BF1<A> {
    /**
     * Evaluate or execute the function
     * @param obj The parameter
     * @return Result of execution
     */
	boolean e(A obj);
}
