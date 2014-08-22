package qj.util.math;

import java.util.LinkedList;
import java.util.List;

import qj.util.funct.F1;
import qj.util.funct.P1;

public class Matrix2 {

	/**
	 * Get 8 cells around
	 * @param point
	 * @param size
	 * @param p1
	 */
	public static void eachAround(Point point, Dimension size,
			P1<Point> p1) {
		eachAround4(point, size, p1);
		
		// North west
		if (point.x > 0 && point.y > 0) {
			p1.e(new Point(point.x - 1, point.y - 1));
		}
		
		
		if (point.x < size.width - 1 && point.y < size.height - 1) {
			p1.e(new Point(point.x + 1, point.y + 1));
		}
		
		if (point.x < size.width - 1 && point.y > 0) {
			p1.e(new Point(point.x + 1, point.y - 1));
		}
		if (point.x > 0 && point.y < size.height - 1) {
			p1.e(new Point(point.x - 1, point.y + 1));
		}
	}

	public static void eachAround4(Point point, Dimension size, P1<Point> p1) {
		// West
		if (point.x > 0) {
			p1.e(new Point(point.x - 1, point.y));
		}
		
		// North
		if (point.y > 0) {
			p1.e(new Point(point.x    , point.y - 1));
		}
		if (point.x < size.width - 1) {
			p1.e(new Point(point.x + 1, point.y));
		}
		if (point.y < size.height - 1) {
			p1.e(new Point(point.x    , point.y + 1));
		}
	}

	public static LinkedList<Point> getMaxes(Dimension size,
			final F1<Point, Integer> valueF) {
		final int[] max = {Integer.MIN_VALUE};
		final LinkedList<Point> collecteds = new LinkedList<Point>();
		
		each(size, new F1<Point,Boolean>() {public Boolean e(Point p) {
			Integer val = valueF.e(p);
			
			if (val == null) {
				return false;
			}
			
			if (val > max[0]) {
				collecteds.clear();
				max[0] = val;
				collecteds.add(p);
			} else if (val == max[0]) {
				collecteds.add(p);
			} else {
				// Lower
				;
			}
			return false;
		}});
		
		return collecteds;
	}

	/**
	 * 
	 * @param size
	 * @param f1 return true to interrupt
	 */
	public static boolean each(Dimension size, F1<Point, Boolean> f1) {
		for (int y = 0; y < size.height; y++) {
			for (int x = 0; x < size.width; x++) {
				Point p = new Point(x, y);
				if (f1.e(p)) {
					return true;
				}
			}
		}
		return false;
	}
	
}
