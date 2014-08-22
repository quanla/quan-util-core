package qj.util.math;

public class TriangleD {

	private PointD p1;
	private PointD p2;
	private PointD p3;

	public TriangleD(PointD p1, PointD p2, PointD p3) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}

	public double area() {
		double a = p1.distance(p2);
		double b = p2.distance(p3);
		double c = p3.distance(p1);
		double p = (a + b + c) / 2;
		
		return Math.sqrt( p*(p-a)*(p-b)*(p-c) );
	}
	
}
