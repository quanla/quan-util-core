package qj.util.math;

public class Angle {

	public static double cal(PointD o, PointD p1, PointD p2) {
		double a = p1.distance(p2);
		double b = p1.distance(o);
		double c = p2.distance(o);
		return Math.acos((c*c + b*b - a*a)/ 2/c/b);
	}
}
