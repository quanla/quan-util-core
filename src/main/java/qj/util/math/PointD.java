package qj.util.math;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.Serializable;

import qj.util.funct.F1;

public class PointD implements Serializable {

	public double x;
	public double y;
	public PointD(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "[x=" + x + ", y=" + y + "]";
	}

	public Point round() {
		return new Point(
				(int)Math.round(x), 
				(int)Math.round(y) 
				);
	}

	public double distance(PointD from) {
		return Math.sqrt(Math.pow(from.x - x, 2) + Math.pow(from.y - y, 2));
	}

	public double distance(int x, int y) {
		return Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2));
	}
	public double distance(double x, double y) {
		return Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2));
	}

	public Point toInt() {
		return new Point((int) x,(int) y);
	}

	public Point2D.Double toAwt() {
		return new Point2D.Double(x, y);
	}

	public static PointD fromAwt(Point2D.Double p) {
		return new PointD(p.x, p.y);
	}
	public static final F1<java.awt.Point, PointD> fromPointAwt = new F1<java.awt.Point, PointD>() {public PointD e(java.awt.Point p) {
		return new PointD(p.x, p.y);
	}};
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = java.lang.Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = java.lang.Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PointD other = (PointD) obj;
		if (java.lang.Double.doubleToLongBits(x) != java.lang.Double
				.doubleToLongBits(other.x))
			return false;
		if (java.lang.Double.doubleToLongBits(y) != java.lang.Double
				.doubleToLongBits(other.y))
			return false;
		return true;
	}
	
}
