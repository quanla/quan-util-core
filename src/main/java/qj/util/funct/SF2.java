package qj.util.funct;

/**
 * Represent a function that accept 2 parameters and return String value
 */
public interface SF2<A, B> extends F2<A, B, String> {
    /**
     * Evaluate or execute the function
     * @param a The 1st parameter
     * @param b The 2nd parameter
     * @return Result of execution
     */
	String e(A a, B b);
}