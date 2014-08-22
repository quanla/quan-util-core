package qj.util.math;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import qj.util.funct.Fs;
import qj.util.funct.P1;

public class RectangleD implements Serializable {

	public double width;
	public double height;
	public double x;
	public double y;

	public RectangleD(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		if (width < 0) {
			throw new RuntimeException("Width < 0");
		}
		if (height < 0) {
			throw new RuntimeException("Height < 0");
		}
		this.width = width;
		this.height = height;
	}

	public boolean contains(PointD point) {
		return 
				point.x > x &&
				point.x <= x + width &&
				point.y > y &&
				point.y <= y + height
				;
	}

	@Override
	public String toString() {
		return "Rectangle [x=" + x + ", y=" + y + ", width=" + width
				+ ", height=" + height + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RectangleD other = (RectangleD) obj;
		if (height != other.height)
			return false;
		if (width != other.width)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public RectangleD shiftX(int i) {
		return new RectangleD(x + i, y, width, height);
	}

	public PointD nearestCorner(final PointD p) {
		final double[] nearestDistance = {Double.MAX_VALUE};
		final PointD[] ret = {null};
		eachCorner(new P1<PointD>() {public void e(PointD corner) {
			double distance = corner.distance(p);
			if (distance < nearestDistance[0]) {
				nearestDistance[0] = distance;
				ret[0] = corner;
			}
		}});
		return ret[0];
	}
	public PointD farthestCorner(final PointD p) {
		final double[] farthestDistance = {Double.MIN_VALUE};
		final PointD[] ret = {null};
		eachCorner(new P1<PointD>() {public void e(PointD corner) {
			double distance = corner.distance(p);
			if (distance > farthestDistance[0]) {
				farthestDistance[0] = distance;
				ret[0] = corner;
			}
		}});
		return ret[0];
	}
	
	public void eachCorner(P1<PointD> p) {
		p.e(new PointD(x        , y));
		p.e(new PointD(x + width, y));
		p.e(new PointD(x        , y + height));
		p.e(new PointD(x + width, y + height));
	}

	public Collection<PointD> getCorners() {
		LinkedList<PointD> ret = new LinkedList<PointD>();
		eachCorner(Fs.store(ret));
		return ret;
	}
}
