package qj.util.funct;

/**
 * Represent a function that accept one parameter and return String value
 * @param <A> The only parameter
 */
public interface SF1<A> extends F1<A, String> {
    /**
     * Evaluate or execute the function
     * @param obj The parameter
     * @return Result of execution
     */
	String e(A obj);
}
