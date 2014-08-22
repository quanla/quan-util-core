package qj.util.funct;

public class PredicateUtil {
    public static F0<Boolean> trueFalse() {
        final boolean[] ret = {false};
        return new F0<Boolean>() {
            public Boolean e() {
                ret[0] = !ret[0];
                return ret[0];
            }
        };
    }

    public static F0<Boolean> not(final F0<Boolean> test) {
        return new F0<Boolean>() {
            public Boolean e() {
                return !test.e();
            }
        };
    }
}
