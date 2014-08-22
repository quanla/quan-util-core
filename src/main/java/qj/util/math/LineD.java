package qj.util.math;

import qj.util.funct.Douce;

public class LineD {

	private PointD p1;
	private PointD p2;

	public LineD(double x1, double y1, double x2, double y2) {
		this.p1 = new PointD(x1, y1);
		this.p2 = new PointD(x2, y2);
	}
	public LineD(PointD p1, PointD p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	/**
	 * Only work if p1 inside circle and p2 outside
	 * @param circle
	 */
	public PointD meet1(CircleD circle) {
		// Find alpha
		double a = circle.center.distance(p2);
		double b = circle.center.distance(p1);
		double c = length();
//		System.out.println((a*a + b*b - c*c)/ 2*a*b);
		double alpha = Math.acos((c*c + b*b - a*a)/ 2/c/b);
		if (b >= a) {
			throw new RuntimeException("Unsupport if p2 inside circle");
		}
		
//		System.out.println("line=" + this);
//		System.out.println("circle=" + circle);
//		System.out.println("alpha=" + alpha);
//		System.out.println("a=" + a);
//		System.out.println("b=" + b);
//		System.out.println("c=" + c);
//		System.out.println(a*a - Math.pow(Math.sin(alpha)*b, 2));
		// Find meet
		
		double meetToA = b*Math.cos(alpha) + Math.sqrt(Math.pow(circle.r, 2) - Math.pow(Math.sin(alpha)*b, 2));
//		System.out.println("meetToA=" + meetToA + " need to be > 0");
		return moveP1toP2(meetToA);
	}

	private PointD moveP1toP2(double distance) {
		double alpha = Math.atan((p2.y-p1.y) / (p2.x - p1.x)) + (p2.x < p1.x ? Math.PI : 0);
		return new PointD(p1.x + Math.cos(alpha) * distance, p1.y + Math.sin(alpha) * distance);
	}

	private double length() {
		return p1.distance(p2);
	}
	
	@Override
	public String toString() {
		return p1 + ", " + p2;
	}
	
	public PointD meet(LineD lineD) {
		PointD meetEquation = meetEquation(lineD);
		if (meetEquation==null) {
			return null;
		}
		
		if (!checkInsideByLength(meetEquation)) {
			return null;
		}
		if (!lineD.checkInsideByLength(meetEquation)) {
			return null;
		}
		
		return meetEquation;
	}
	
	private boolean checkInsideByLength(PointD p) {
		double length = length();
		if (p.distance(p1) > length) {
			return false;
		}
		if (p.distance(p2) > length) {
			return false;
		}
		return true;
	}
	/**
	 * No restriction for meet point inside line
	 * @param lineD
	 * @return
	 */
	public PointD meetEquation(LineD lineD) {
		Douce<Double, Double> e1 = this.toEquation();
		Douce<Double, Double> e2 = lineD.toEquation();
		
		Double a1 = e1.get1();
		Double a2 = e2.get1();
		Double b1 = e1.get2();
		Double b2 = e2.get2();
		
		if (a1 == a2) {
			return null;
		}
		
		if (Double.isInfinite(a1)) {
			return new PointD( b1, a2*b1 + b2);
		}
		if (Double.isInfinite(a2)) {
			return new PointD( b2, a1*b2 + b1);
		}
		
		double x = -( b1-b2 )/ (a1 - a2);
		double y = a1*x + b1;
		
		return new PointD(x, y);
	}
	
	private Douce<Double,Double> toEquation() {
		if (p1.x == p2.x && p1.y != p2.y) {
			return new Douce(Double.POSITIVE_INFINITY, p1.x);
		}
		
		double a = (p1.y - p2.y) / (p1.x - p2.x);
		double b = p1.y - a*p1.x;
		
		return new Douce<Double, Double>(a, b);
	}
}
