package qj.util.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by QuanLA
 * Date: Mar 13, 2006
 * Time: 4:46:23 PM
 */
public class MathUtil4 {
    /**
     * Get min reject -1
     * @param i1
     * @param i2
     * @return min reject -1
     */
    public static int minNotMinus1(int i1, int i2) {
        if (i1 == -1) {
            if (i2 > -1)
                return i2;
            else
                return -1;
        } else {
            if (i2 > -1 && i2 < i1)
                return i2;
            else
                return i1;
        }
    }
    
    /**
     * Get min reject -1
     * @param i1
     * @param i2
     * @return min reject -1
     */
    public static double minNot0(double i1, double i2) {
        if (i1 == 0) {
            if (i2 > 0)
                return i2;
            else
                return 0;
        } else {
            if (i2 > 0 && i2 < i1)
                return i2;
            else
                return i1;
        }
    }

	/**
	 * ensure min <= value < max
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */
	public static double ensureRange(double value, int min, int max) {
		if (value >= max) {
			value -= min;
			max -= min;
			
			value %= max;
			return value + min;
		} else if (value < min) {
			value -= min;
			max -= min;
			
			value+= max;
			
			value %= max;
			return value + min;
			
		} else
			return value;
	}

	public static double max(double no1, double no2, double no3) {
		return Math.max(Math.max(no1, no2), no3);
	}
	public static Integer max(int no1, int no2, int no3, int no4) {
		return Math.max(Math.max(Math.max(no1, no2), no3), no4);
	}

	public static double minNot0(double no1, double no2, double no3) {
		return minNot0(minNot0(no1, no2), no3);
	}

	public static int sqr(int i) {
		return i*i;
	}

	public static float magnify(float val, float[] pos, float[] rates) {
		float rate = rates[rates.length - 1];
		for (int i = pos.length - 1; i > -1; i--) {
			float element = pos[i];
			if (val < element) {
				rate = rates[i];
			}
		}
		return val * rate;
	}
}
