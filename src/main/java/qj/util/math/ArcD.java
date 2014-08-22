package qj.util.math;

public class ArcD {
	CircleD circle;
	double from;
	double to;
	public ArcD(CircleD circle, double from, double length) {
		this.circle = circle;
		this.from = from;
		this.to = from + length;
		if (length < 0) {
			throw new RuntimeException("Length must be > 0 : " + length);
		}
	}
	public double areaMoon() {
		double triangleArea = .5* Math.pow(circle.r, 2) * Math.sin(to - from);
		double area = area();
		return area - triangleArea;
	}
	public double area(){
		return circle.area() * (to-from) / 2/Math.PI;
	}
	@Override
	public String toString() {
		return "ArcD [circle=" + circle + ", from=" + from + ", to=" + to + "]";
	}
}
