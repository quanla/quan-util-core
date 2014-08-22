package qj.util;

import qj.util.funct.F0;
import qj.util.funct.F1;
import qj.util.math.Color;
import qj.util.math.PointD;


/**
 * Created by QuanLA
 * Date: Apr 5, 2006
 * Time: 5:46:44 PM
 */
public class ObjectUtil {
	public static boolean notEquals(Object o1, Object o2) {
		return !equals(o1, o2);
	}
	public static boolean equals(Object o1, Object o2) {
		return o1==null ? o2 == null : (o1 == o2 || o1.equals(o2));
	}
    public static boolean equals(byte[] b1, byte[] b2) {
        if (b1==null)
            return false;
        else if (b2==null)
            return false;

        if (b1.length!= b2.length) {
            return false;
        } else {
            for (int i = 0; i < b1.length; i++) {
                if (b1[i]!=b2[i])
                    return false;
            }

            return true;
        }
    }
    public static boolean equals(boolean[] b1, boolean[] b2) {
        if (b1==null)
            return false;
        else if (b2==null)
            return false;

        if (b1.length!= b2.length) {
            return false;
        } else {
            for (int i = 0; i < b1.length; i++) {
                if (b1[i]!=b2[i])
                    return false;
            }

            return true;
        }
    }
    
    public static boolean equals(boolean[][] b1, boolean[][] b2) {
        if (b1==null)
            return false;
        else if (b2==null)
            return false;

        if (b1.length != b2.length) {
            return false;
//        } else if (b1.length == 0) {
//        	return true;
        } else {
            for (int i = 0; i < b1.length; i++) {
                if (!equals(b1[i],b2[i]))
                    return false;
            }

            return true;
        }
    }
    
    public static <A> F1<A,String> toStringF() {
    	return new F1<A, String>() {public String e(A obj) {
    		if (obj==null) {
    			return null;
    		}
			return obj.toString();
		}};
    }
	public static boolean equalsOne(Object target, Object... samples) {
		for (Object object : samples) {
			if (equals(target, object)) {
				return true;
			}
		}
		return false;
	}
	public static <A> F1<A,Boolean> equalsF(final A a) {
		return new F1<A, Boolean>() {public Boolean e(A a1) {
			return ObjectUtil.equals(a, a1);
		}};
	}
}
