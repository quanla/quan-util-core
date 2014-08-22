package qj.util.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PolygonD {
	final List<PointD> corners;

	public PolygonD(PointD... ps) {
		corners = Arrays.asList(ps);
	}

	/**
	 * Only work if corners are aligned
	 * @return
	 */
	public double area() {
		ArrayList<PointD> cs = new ArrayList<PointD>(corners);
		PointD p0 = cs.get(0);
		double total = 0;
		for (int i = 1; i < cs.size() - 1; i++) {
			PointD p1 = cs.get(i);
			PointD p2 = cs.get(i + 1);
			
			total += new TriangleD(p0, p1, p2).area();
		}
		return total;
	}
}
