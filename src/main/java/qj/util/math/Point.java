package qj.util.math;

import java.io.Serializable;
import java.util.ArrayList;

import qj.util.funct.F1;

public class Point implements Serializable {

	public static final F1<Point, PointD> toPointD = new F1<Point, PointD>() {public PointD e(Point p) {
		return new PointD(p.x, p.y);
	}};
	public int x;
	public int y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Point clone() {
		return new Point(x, y);
	}

	public java.awt.Point toAwt() {
		return new java.awt.Point(x,y);
	}

	public static Point convert(java.awt.Point awtPoint) {
		return new Point(awtPoint.x,awtPoint.y);
	}

	public static ArrayList<java.awt.Point> toAwt(ArrayList<Point> list) {
		ArrayList<java.awt.Point> ret = new ArrayList<java.awt.Point>(list.size());
		for (Point point : list) {
			ret.add(point.toAwt());
		}
		return ret;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
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
		Point other = (Point) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[x=" + x + ", y=" + y + "]";
	}

	public double distance(PointD from) {
		return Math.sqrt(Math.pow(from.x - x, 2) + Math.pow(from.y - y, 2));
	}
	public double distance(Point from) {
		return Math.sqrt(Math.pow(from.x - x, 2) + Math.pow(from.y - y, 2));
	}

	public Point move(Point point) {
		return new Point(this.x + point.x, this.y + point.y);
	}

	public Point negative() {
		return new Point(-x, -y);
	}
}
