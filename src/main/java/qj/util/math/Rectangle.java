package qj.util.math;

import java.io.Serializable;

import qj.util.StringUtil;
import qj.util.funct.F1;

public class Rectangle implements Serializable {

	public int width;
	public int height;
	public int x;
	public int y;

	public Rectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	public Rectangle(double x, double y, double width, double height) {
		this.x = (int) Math.round(x);
		this.y = (int) Math.round(y);
		this.width = (int) Math.round(width);
		this.height = (int) Math.round(height);
	}

	public Rectangle(Point point, Dimension dimension) {
		this.x = point.x;
		this.y = point.y;
		this.width = dimension.width;
		this.height = dimension.height;
	}

	public Dimension getSize() {
		return new Dimension(width, height);
	}

	public java.awt.Rectangle toAwt() {
		return new java.awt.Rectangle(x,y,width,height);
	}

	public boolean contains(Point point) {
		return contains(point.x, point.y);
	}
	public boolean contains(int x1, int y1) {
		return x1 >= x && x1 < x + width && y1 >= y && y1 < y + height;
	}

	public void setBounds(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public String toString() {
		return "Rectangle [x=" + x + ", y=" + y + ", width=" + width
				+ ", height=" + height + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + width;
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
		Rectangle other = (Rectangle) obj;
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

	public Rectangle shiftX(int i) {
		return new Rectangle(x + i, y, width, height);
	}

	public static Rectangle byBoundary(int x, int y, int x1, int y1) {
		return new Rectangle(x, y, x1 - x, y1 - y);
	}

	public static Rectangle parse(String string) {
		if (StringUtil.isEmpty(string)) {
			return null;
		}
		String[] split = string.trim().split("\\s*,\\s*");
		int x2 = Integer.parseInt(split[0]);
		int y2 = Integer.parseInt(split[1]);
		int width2 = Integer.parseInt(split[2]);
		int height2 = Integer.parseInt(split[3]);
		return new Rectangle(x2, y2, width2, height2);
	}

	public String toStringMin() {
		return x + "," + y + "," + width + "," + height;
	}

	public static Rectangle fromAwt(java.awt.Rectangle rect) {
		return new Rectangle(
				rect.x,
				rect.y,
				rect.width,
				rect.height
				);
	}

	public Rectangle inBound(int x, int y, int w, int h) {
		int x1 = Math.max(x, this.x);
		int y1 = Math.max(y, this.y);
		int x2 = Math.min(x + w, this.x + this.width);
		int y2 = Math.min(y + h, this.y + this.height);
		return byBoundary(x1, y1, x2, y2);
	}

	/**
	 * 
	 * @param f1 returns interrupted
	 */
	public void eachPoint(F1<Point, Boolean> f1) {
		L1:
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				boolean interrupted = f1.e(new Point(this.x + x, this.y + y));
				if (interrupted) {
					break L1;
				}
			}
		}
	}

	public Rectangle move(Point vector) {
		Rectangle r = new Rectangle(x, y, width, height);
		r.x += vector.x;
		r.y += vector.y;
		return r;
	}

	public Rectangle expandRight(int right) {
		return new Rectangle(x, y, width + right, height);
	}

	public Rectangle expandDown(int down) {
		return new Rectangle(x, y, width, height + down);
	}

	public Point getStartPoint() {
		return new Point(x, y);
	}
}
