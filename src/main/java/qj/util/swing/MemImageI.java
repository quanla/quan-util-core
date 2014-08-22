package qj.util.swing;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collection;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import qj.util.ImageUtil;
import qj.util.funct.*;
import qj.util.math.*;

public class MemImageI implements Serializable {

	public final int[][] data;
	
	public static MemImageI EMPTY = new MemImageI(new int[0][0]);

	public MemImageI(int[][] data) {
		this.data = data;
	}

	public MemImageI(BufferedImage image) {
		this(ImageUtil.getDataI(image));
	}

	public Point indexOf(ImgCond cond) {
		int widthDiff = getWidth() - cond.getWidth();
		int heightDiff = getHeight() - cond.getHeight();
		for (int offsetY = 0; offsetY <= heightDiff; offsetY++) {
			for (int offsetX = 0; offsetX <= widthDiff; offsetX++) {
				final int offsetX1 = offsetX;
				final int offsetY1 = offsetY;
				if (cond.check(new F2<Integer, Integer, Color>() {public Color e(Integer x1, Integer y1) {
					return getColor(offsetX1 + x1, offsetY1 + y1);
				}})) {
					return new Point(offsetX, offsetY);
				}
			}
		}
		return null;
	}

	public Point indexOf(int[][] target) {
		int widthDiff = getWidth() - target[0].length / 3;
		int heightDiff = getHeight() - target.length;
		for (int offsetY = 0; offsetY <= heightDiff; offsetY++) {
			for (int offsetX = 0; offsetX <= widthDiff; offsetX++) {
				if (ImageUtil.equalsImageI(target, this.data, offsetX, offsetY)) {
					return new Point(offsetX, offsetY);
				}
			}
		}
		return null;
	}
	public Point indexOf(MemImageI target, F2<Color,Color,Boolean> colorF) {
		int widthDiff = getWidth() - target.getWidth();
		int heightDiff = getHeight() - target.getHeight();
		for (int offsetY = 0; offsetY <= heightDiff; offsetY++) {
			for (int offsetX = 0; offsetX <= widthDiff; offsetX++) {
				if (equalsImage(target, offsetX, offsetY, colorF)) {
					return new Point(offsetX, offsetY);
				}
			}
		}
		return null;
	}

	public boolean equalsImage(MemImageI target,
			int offsetX, int offsetY, F2<Color,Color,Boolean> colorF) {
		int width = target.getWidth();
		int height = target.getHeight();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (offsetY + y >= getHeight()) {
					return false;
				}
				int y1 = offsetY + y;
				if (offsetX + x >= getWidth() ) {
					return false;
				}
				
				if (!colorF.e(target.getColor(x, y), getColor(offsetX + x, y1))) {
					return false;
				}
			}
		}
		return true;
	}


	public boolean equals(MemImageI target) {
		return ImageUtil.equalsImageI(target.data, this.data, 0, 0);
	}
	public boolean equals(MemImageI target, int tolerance) {
		return ImageUtil.equalsImageI(target.data, this.data, 0, 0, tolerance);
	}
	
	public Point indexOf(int[][] target, int tolerance) {
		Color transColor = Color.white;
		return indexOf(target, tolerance, transColor);
	}

	public Point indexOf(int[][] target, int tolerance, Color transColor) {
		int widthDiff = getWidth() - target[0].length / 3;
		int heightDiff = getHeight() - target.length;
		for (int offsetY = 0; offsetY <= heightDiff; offsetY++) {
			for (int offsetX = 0; offsetX <= widthDiff; offsetX++) {
				if (ImageUtil.equalsImageI(target, this.data, offsetX, offsetY, tolerance, transColor)) {
					return new Point(offsetX, offsetY);
				}
			}
		}
		return null;
	}
	
	public boolean equals(MemImageI target,int tolerance, Color transColor) {
		return ImageUtil.equalsImageI(target.data, this.data, 0, 0, tolerance, transColor);
	}

	public Point indexOfUncolor(int[][] target, Color uncolor) {
		int widthDiff = getWidth() - target[0].length / 3;
		int heightDiff = getHeight() - target.length;
		for (int offsetY = 0; offsetY <= heightDiff; offsetY++) {
			for (int offsetX = 0; offsetX <= widthDiff; offsetX++) {
				if (uncolorImage(target, this.data, offsetX, offsetY, Color.black, Color.white)) {
					return new Point(offsetX, offsetY);
				}
			}
		}
		return null;
	}


	/**
	 * No tolerance
	 * 
	 * @param data1
	 * @param data2
	 * @param offsetX
	 * @param offsetY
	 * @param transColor
	 * @return
	 */
	public static boolean uncolorImage(int[][] data1, int[][] data2,
			int offsetX, int offsetY, Color uncolor, Color transColor) {
		int width = data1[0].length / 3;
		int height = data1.length;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int red 	= data1[y][x * 3 + 0];
				int green 	= data1[y][x * 3 + 1];
				int blue 	= data1[y][x * 3 + 2];
				
				// Transparent pixel
				if (red == transColor.getRed()
						&& green == transColor.getGreen()
						&& blue == transColor.getBlue()) {
					continue;
				}
				
				if (uncolor.getRed() == data2[offsetY + y][(offsetX + x) * 3 + 0]
						&& uncolor.getGreen() == data2[offsetY + y][(offsetX + x) * 3 + 1]
						&& uncolor.getBlue() == data2[offsetY + y][(offsetX + x) * 3 + 2]) {
					return false;
				}
			}
		}
		return true;
	}

	public Rectangle findRectInner(Point point, final F1<Color,Boolean> colorF) {
		
		int top = findHorizontalLineTop(point,colorF);
		if (top==-1) {
			return null;
		}
		int right = findHorizontalLineRight(point,colorF);
		if (right==-1 || !verifyCornerUpRight(top, right, point, colorF)) {
			return null;
		}
		
		int down = findHorizontalLineDown(point,colorF);
		if (down==-1 || !verifyCornerDownRight(down, right, point, colorF)) {
			return null;
		}
		int left = findHorizontalLineLeft(point,colorF);
		if (left==-1 || !verifyCornerDownLeft(down, left, point, colorF)
				 || !verifyCornerTopLeft(top, left, point, colorF)) {
			return null;
		}
		return new Rectangle(left, top, right - left, down - top);
	}

	private boolean verifyCornerUpRight(int top, int right, Point point,
			F1<Color, Boolean> colorF) {
		return //colorF.e(getColor(right, top))
		colorF.e(getColor(right - 1, top))
		&& colorF.e(getColor(right, top + 1))
		&& !colorF.e(getColor(right - 1, top + 1))
		;
	}
	private boolean verifyCornerDownRight(int down, int right, Point point,
			F1<Color, Boolean> colorF) {
		//colorF.e(getColor(right, down))
		return colorF.e(getColor(right - 1, down))
		&& colorF.e(getColor(right, down - 1))
//		&& !colorF.e(getColor(right - 1, down - 1))
		;
	}
	private boolean verifyCornerDownLeft(int down, int left, Point point,
			F1<Color, Boolean> colorF) {
		return //colorF.e(getColor( left, down))
		colorF.e(getColor( left + 1, down))
		&& colorF.e(getColor( left, down - 1))
		&& !colorF.e(getColor( left + 1, down - 1))
		;
	}
	private boolean verifyCornerTopLeft(int top, int left, Point point,
			F1<Color, Boolean> colorF) {
		return //colorF.e(getColor( left, top))
		colorF.e(getColor( left + 1, top))
		&& colorF.e(getColor( left, top + 1))
		&& !colorF.e(getColor( left + 1, top + 1))
		;
	}

	private int findHorizontalLineTop(Point point, final F1<Color,Boolean> colorF) {
		
		for (int i = point.y - 1; i > -1; i--) {
			if (colorF.e(getColor(point.x, i))
					&& colorF.e(getColor(point.x + 1, i)) && !colorF.e(getColor(point.x + 1, i + 1))
					&& colorF.e(getColor(point.x - 1, i)) && !colorF.e(getColor(point.x - 1, i + 1))
					) {
				return i;
			}
		}
		return -1;
	}
	private int findHorizontalLineDown(Point point, final F1<Color,Boolean> colorF) {
		for (int i = point.y + 1; i < getHeight(); i++) {
			if (colorF.e(getColor(point.x, i))
					&& colorF.e(getColor(point.x + 1, i)) && !colorF.e(getColor(point.x + 1, i - 1))
					&& colorF.e(getColor(point.x + 2, i)) && !colorF.e(getColor(point.x + 2, i - 1))
					&& colorF.e(getColor(point.x - 1, i)) && !colorF.e(getColor(point.x - 1, i - 1))
					&& colorF.e(getColor(point.x - 2, i)) && !colorF.e(getColor(point.x - 2, i - 1))
					) {
				return i;
			}
		}
		return -1;
	}
	private int findHorizontalLineLeft(Point point, final F1<Color,Boolean> colorF) {
		for (int i = point.x; i > -1; i--) {
			if (colorF.e(getColor(i, point.y))
					&& colorF.e(getColor(i, point.y + 1)) && !colorF.e(getColor(i + 1, point.y + 1))
					&& colorF.e(getColor(i, point.y - 1)) && !colorF.e(getColor(i + 1, point.y + 1))
					) {
				return i;
			}
		}
		return -1;
	}
	public int findHorizontalLineLeft1(Point point, final F1<Color,Boolean> colorF) {
		for (int i = point.x; i > -1 ; i--) {
			Color color = getColor(i, point.y);
//			System.out.println(color);
			if (color==null) {
				return -1;
			}
			if (colorF.e(color)) { //  || colorF.e(getColor(i - 1, point.y - 5 + j))
				return i - 1;
			}
		}
		return -1;
	}
	private int findHorizontalLineRight(Point point, final F1<Color,Boolean> colorF) {
		for (int i = point.x; i < getWidth(); i++) {
			if (colorF.e(getColor(i, point.y))
					&& colorF.e(getColor(i, point.y + 1)) && !colorF.e(getColor(i - 1, point.y + 1))
					&& colorF.e(getColor(i, point.y - 1)) && !colorF.e(getColor(i - 1, point.y - 1))
					) {
				return i;
			}
		}
		return -1;
	}

	public int getWidth() {
		if (data==null || data.length == 0 || data[0] == null ) {
			return 0;
		}
		return data[0].length / 3;
	}
	public int getHeight() {
		return data.length;
	}

//	private boolean sameColor(int x, int y, Color color) {
//		if (x >= getWidth()) {
//			return false;
//		}
//		if (y >= getHeight()) {
//			return false;
//		}
//		boolean b = data[y][x * 3] == color.getRed()
//			&& data[y][x * 3 + 1] == color.getGreen()
//			&& data[y][x * 3 + 2] == color.getBlue();
////		if (b) {
////			System.out.println("true");
////		}
//		return b;
//	}

	public MemImageI subImage(Rectangle rect) {
		int width = Math.min(rect.x + rect.width, getWidth()) - rect.x;
		int height = Math.min(rect.y + rect.height, getHeight()) - rect.y;
		if (width <0 || height < 0) {
			return EMPTY;
		}
		int[][] data = new int[height][width*3];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				data[y][x*3] = this.data[y + rect.y][(x + rect.x)*3]; 
				data[y][x*3 + 1] = this.data[y + rect.y][(x + rect.x)*3 + 1]; 
				data[y][x*3 + 2] = this.data[y + rect.y][(x + rect.x)*3 + 2]; 
			}
		}
		
		return new MemImageI(data);
	}


	public MemImageI subImage(boolean[][] mines) {
		int height = getHeight();
		int width = getWidth();
		int[][] data = new int[height][width*3];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (mines[y][x]) {
					data[y][x*3] = this.data[y][x*3]; 
					data[y][x*3 + 1] = this.data[y][x*3 + 1]; 
					data[y][x*3 + 2] = this.data[y][x*3 + 2]; 
				} else {
					data[y][x*3] = 255; 
					data[y][x*3 + 1] = 255; 
					data[y][x*3 + 2] = 255;
				}
			}
		}
		
		return new MemImageI(data);
	}

	public Color getColor(int x, int y) {
//		if (x >= getWidth() || x < 0) {
//			return null;
//		}
//		if (y >= getHeight() || y < 0) {
//			return null;
//		}
		return new Color(data[y][x*3],data[y][x*3 + 1],data[y][x*3 + 2]);
	}
	
	public void setColor(int x, int y, Color c) {
		if (x >= getWidth() || x < 0) {
			return ;
		}
		if (y >= getHeight() || y < 0) {
			return ;
		}
		data[y][x*3] = (byte) (c.getRed());
		data[y][x*3 +1] = (byte) (c.getGreen());
		data[y][x*3 +2] = (byte) (c.getBlue());
	}

	public Collection<Point> allIndexOf(int[][] target) {
//		int tolerance = 0;
		LinkedList<Point> ret = new LinkedList<Point>();
		int widthDiff = this.data[0].length / 3 - target[0].length / 3;
		int heightDiff = this.data.length - target.length;
		for (int offsetY = 0; offsetY <= heightDiff; offsetY++) {
			for (int offsetX = 0; offsetX <= widthDiff; offsetX++) {
				if (inRange(offsetX, offsetY, ret, target[0].length / 3, target.length)) {
					continue;
				}
				if (ImageUtil.equalsImageI(target, this.data, offsetX, offsetY, Color.white)) {
					ret.add(new Point(offsetX, offsetY));
					offsetX+=target[0].length / 3;
//					offsetY+=1;
				}
			}
		}
		return ret;

	}
	public Collection<Point> allIndexOf(MemImageI target, F2<Color,Color,Boolean> colorF) {
//		int tolerance = 0;
		LinkedList<Point> ret = new LinkedList<Point>();
		int width = target.getWidth();
		int widthDiff = this.data[0].length / 3 - width;
		int height = target.getHeight();
		int heightDiff = this.data.length - height;
		for (int offsetY = 0; offsetY <= heightDiff; offsetY++) {
			for (int offsetX = 0; offsetX <= widthDiff; offsetX++) {
				if (inRange(offsetX, offsetY, ret, width, height)) {
					continue;
				}
				if (equalsImage(target, offsetX, offsetY, colorF)) {
					ret.add(new Point(offsetX, offsetY));
					offsetX+=width;
				}
			}
		}
		return ret;
	}

	private boolean inRange(int x1, int y1, Collection<Point> points, int w, int h) {
		for (Point point : points) {
			if (x1 >= point.x && y1 >= point.y
					&& x1 < point.x + w && y1 < point.y + h) {
				return true;
			}
		}
		return false;
	}

	public int[][] getData() {
		return data;
	}

	public Dimension getSize() {
		return new Dimension(getWidth(), getHeight());
	}

	public Color getColor(Point point) {
		return getColor(point.x, point.y);
	}

	public BufferedImage toImage() {
		final BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
//        g.setComposite(AlphaComposite.Src);
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				Color color = getColor(x,y);
//				System.out.println(color);
				g.setColor(color.toAwt());
				g.fillRect(x, y, 1, 1);
			}
		}
		g.dispose();
		return img;
	}

	public MemImageI replaceColor(Color toColor, F1<Color, Boolean> colorF) {
		MemImageI t = duplicate();
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				if (colorF.e(t.getColor(x,y))) {
					t.setColor(x, y, toColor);
				}
			}
		}
		return t;
	}

	public MemImageI duplicate() {
		return subImage(new Rectangle(0,0,getWidth(),getHeight()));
	}

	public void drawHLine(int x, int y, int width, Color c) {
		for (int x1 = 0; x1 < width; x1++) {
			setColor(x1 + x, y, c);
		}
	}

	public void eachColor(P1<Color> p) {
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				p.e(getColor(x, y));
			}
		}
		eachColor(Fs.<Color,Point>p2(p));
	}
	public void eachColor(P2<Color,Point> p) {
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				p.e(getColor(x, y), new Point(x,y));
			}
		}
	}
	public boolean anyColor(F1<Color,Boolean> f) {
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				if (f.e(getColor(x, y))) {
					return true;
				}
			}
		}
		return false;
	}

	public static MemImageI load(String path) {
		File inputFile = new File(path);
		return load(inputFile);
	}

	public static MemImageI load(File inputFile) {
		try {
			return new MemImageI(ImageIO.read(inputFile));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public BufferedImage toImage(double ratio) {
		BufferedImage img = new BufferedImage((int)(getWidth() * ratio), (int)(getHeight() * ratio), BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = img.createGraphics();

		int ceil = (int) Math.ceil(ratio);
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				g.setColor(getColor(x, y).toAwt());
				g.fillRect((int)(x * ratio), (int)(y * ratio), ceil, ceil);
			}
		}
		g.dispose();
		return img;
	}

	public MemImageI transform(F2<Point, Color, Color> f2) {
		MemImageI duplicate = duplicate();
		duplicate.replaceColor(f2);
		return duplicate;
	}

	public void replaceColor(F2<Point, Color, Color> f2) {
		for (int y = 0; y < this.getHeight(); y++) {
			for (int x = 0; x < this.getWidth(); x++) {
				Point p = new Point(x,y);
				Color newColor = f2.e(p, this.getColor(p));
				if (newColor!=null) {
					this.setColor(x, y, newColor);
				}
			}
		}
	}


	public static boolean[][] toBoolean(BufferedImage image, Color c) {
		MemImageI img = new MemImageI(image);
		boolean[][] ret = new boolean[img.getHeight()][img.getWidth()];
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				if (!c.equals(img.getColor(x, y))) {
					ret[y][x] = true;
				}
			}
		}
		return ret;
	}

	public boolean[][] check(F1<Color, Boolean> f1) {
		boolean[][] ret = newBooleanData();
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				ret[y][x] = f1.e(getColor(x, y));
			}
		}
		return ret;
	}

	public boolean[][] newBooleanData() {
		return new boolean[getHeight()][getWidth()];
	}

	public void each(boolean[][] dat, P2<Color, Point> p2) {
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				if (dat[y][x]) {
					p2.e(getColor(x, y), new Point(x,y));
				}
			}
		}
	}
}
