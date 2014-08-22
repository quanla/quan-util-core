package qj.util.math;

public class Color {

	private final int _red;
	private final int _green;
	private final int _blue;

	public Color(int red, int green, int blue) {
		this._red = red;
		this._green = green;
		this._blue = blue;
	}

	public static final Color black = new Color(0,0,0);
	public static final Color white = new Color(255,255,255);
	public static final Color red = new Color(255,0,0);
	public static final Color green = new Color(0,255,0);
	public static final Color blue = new Color(0,0,255);
	public static final Color BLACK = black;
	public static final Color WHITE = white;
	public static final Color DARK_GRAY = new Color(64,64,64);
	public static final Color LIGHT_GRAY = new Color(192,192,192);
	public static final Color RED = red;

	public static void main(String[] args) {
		System.out.println(java.awt.Color.RED);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _blue;
		result = prime * result + _green;
		result = prime * result + _red;
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
		Color other = (Color) obj;
		if (_blue != other._blue)
			return false;
		if (_green != other._green)
			return false;
		if (_red != other._red)
			return false;
		return true;
	}

	public int getGreen() {
		return _green;
	}

	public int getRed() {
		return _red;
	}

	public int getBlue() {
		return _blue;
	}
	public java.awt.Color toAwt() {
		return new java.awt.Color(_red,_green,_blue);
	}
	@Override
	public String toString() {
		return "Color [" + _red + "," + _green + ","
				+ _blue + "]";
	}
	
	
}
