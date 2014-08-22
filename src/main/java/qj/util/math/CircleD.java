package qj.util.math;

public class CircleD {
	public PointD center;
	public double r;
	public CircleD(double centerX, double centerY, double r) {
		this.center = new PointD(centerX, centerY);
		this.r = r;
	}
	public boolean isInside(PointD p) {
		return p.distance(center) <= r;
	}
	public ArcD arc(PointD from, PointD to) {
		return new ArcD(this, Angle.cal(center, new PointD(center.x, center.y - 1), from), Angle.cal(center, from, to));
		
	}
	public double area() {
		return r*r*Math.PI;
	}
	@Override
	public String toString() {
		return "CircleD [center=" + center + ", r=" + r + "]";
	}
}
