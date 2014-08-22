package qj.util.swing;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.LinkedList;

import qj.util.ImageUtil;
import qj.util.MathUtil;
import qj.util.ObjectUtil;
import qj.util.funct.F1;
import qj.util.funct.F2;
import qj.util.math.Color;
import qj.util.math.Dimension;
import qj.util.math.Point;
import qj.util.math.Rectangle;

public class ImgCond {
	
	public final LinkedList<F1<F2<Integer, Integer, Color>, Boolean>> conds;
	private final Dimension size;

	public ImgCond(LinkedList<F1<F2<Integer, Integer, Color>, Boolean>> conds, Dimension size) {
		this.conds = conds;
		this.size = size;
	}
	public ImgCond(Dimension size) {
		this(new LinkedList<F1<F2<Integer, Integer, Color>, Boolean>>(), size);
	}
	
	public void add(final int x,final int y, final F1<Color,Boolean> f) {
		conds.add(new F1<F2<Integer,Integer,Color>, Boolean>() {public Boolean e(F2<Integer, Integer, Color> colorF) {
			return f.e(colorF.e(x, y));
		}});
	}

	public static ImgCond fromImg(BufferedImage img,Color trans,final int tolerance) {
		LinkedList<F1<F2<Integer,Integer,Color>,Boolean>> conds = new LinkedList<F1<F2<Integer,Integer,Color>,Boolean>>();
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				final Color cCond = ImageUtil.getColor(x, y, img);
				if (cCond.equals(trans)) {
					continue;
				}
				
				final int x1=x;
				final int y1=y;
				conds.add(new F1<F2<Integer,Integer,Color>, Boolean>() {public Boolean e(F2<Integer, Integer, Color> f) {
					Color c = f.e(x1, y1);
					return same(c, cCond, tolerance);
				}});
			}
		}
		return new ImgCond(conds,ImageUtil.getSize(img));
	}

	public int getWidth() {
		return size.width;
	}
	public int getHeight() {
		return size.height;
	}

	public boolean check(F2<Integer, Integer, Color> f2) {
		for (F1<F2<Integer, Integer, Color>, Boolean> cond : conds) {
			if (!cond.e(f2)) {
				return false;
			}
		}
		return true;
	}

	public static F1<Color, Boolean> sameF(final Color borderColor) {
		return new F1<Color,Boolean>() {public Boolean e(Color obj) {
			return ObjectUtil.equals(borderColor, obj);
		}};
	}

	public static F1<Color, Boolean> sameF(final Color borderColor,
			final int tolerance) {
		return new F1<Color,Boolean>() {public Boolean e(Color obj) {
			return same(borderColor, obj, tolerance)
					;
		}};
	}

	public static int total(Color target) {
		if (target==null) {
			return 0;
		}
		return target.getRed() + target.getGreen() + target.getBlue();
	}

	public static boolean same(Color target, Color sample, Integer diff) {
		if (target==null) {
			return sample==null;
		} else if (sample==null) {
			return false;
		}
		
		if (diff==null) {
			return target.equals(sample);
		}
		
//		System.out.println("total target=" + total(target));
//		System.out.println("total sample=" + total(sample));
		return true

		&& Math.abs(target.getRed() - sample.getRed()) < diff
		&& Math.abs(target.getGreen() - sample.getGreen()) < diff
		&& Math.abs(target.getBlue() - sample.getBlue()) < diff
		&& Math.abs(total(target) - total(sample)) < diff * 2;
	}


	public static Integer diff(Color target, Color sample) {
		if (target==null || sample == null) {
			return null;
		}
		
		return MathUtil.max(
				Math.abs(target.getRed() - sample.getRed()), 
				Math.abs(target.getGreen() - sample.getGreen()),
				Math.abs(target.getBlue() - sample.getBlue()),
				Math.abs(total(target) - total(sample)) / 2
				);
	}

	public static BufferedImage compare(BufferedImage targetImg,
			BufferedImage srcImg, Point offset, java.awt.Color transColor, Integer tolerance) {
		BufferedImage compare = new BufferedImage(targetImg.getWidth(), targetImg.getHeight(), BufferedImage.TYPE_INT_RGB);
//		Graphics2D g = compare.createGraphics();
		WritableRaster compareRaster = compare.getRaster();
		
		WritableRaster targetRaster = targetImg.getRaster();
		WritableRaster srcRaster = srcImg.getRaster();

		int[] p1 = new int[4];
		int[] p2 = new int[4];
		
		for (int x = 0; x < compare.getWidth(); x++) {
			for (int y = 0; y < compare.getHeight(); y++) {
				targetRaster.getPixel(x, y, p1);
				srcRaster   .getPixel(x + offset.x, y + offset.y, p2);

				if (equals(p1, p2, transColor, tolerance)) {
					compareRaster.setPixel(x, y, p1);
				} else {
					compareRaster.setPixel(x, y, new int[] {0,255,255});
				}
			}
		}
		
		return compare;
	}
	
	public static boolean equals(int[] p1, int[] p2, java.awt.Color transColor, Integer diff) {
		if (transColor!=null && 
				p1[0]==transColor.getRed() &&
				p1[1]==transColor.getGreen() &&
				p1[2]==transColor.getBlue()
				) {
			return true;
		}

		

		return ImageUtil.same(p1, p2, diff);
	}
	public static F2<Integer, Integer, Color> getColorF(final BufferedImage img) {
		final Dimension size = new Dimension(img.getWidth(), img.getHeight());
		return new F2<Integer, Integer, Color>() {public Color e(Integer x, Integer y) {
			if (x < 0 || x >= size.width) {
				return null;
			}
			if (y < 0 || y >= size.height) {
				return null;
			}
			return ImageUtil.getColor(x, y, img);
		}};
	}
	public void addRectCheck(final Rectangle rectangle, final F1<Color, Boolean> checkF) {
		conds.add(new F1<F2<Integer,Integer,Color>, Boolean>() {public Boolean e(final F2<Integer, Integer, Color> getColorF) {
			final boolean[] check = {true};
			rectangle.eachPoint(new F1<Point,Boolean>() {public Boolean e(Point p) {
				Color color = getColorF.e(p.x, p.y);
				if (!checkF.e(color)) {
					check[0] = false;
					return true;
				}
				return false;
			}});
			return check[0];
		}});
	}
	public Point findIn(final F2<Integer, Integer, Color> getColorF,
			Rectangle rect) {
		
		final Point[] found = {null};
		rect.eachPoint(new F1<Point, Boolean>() {public Boolean e(final Point p) {
			boolean check = ImgCond.this.check(new F2<Integer, Integer, Color>() {public Color e(Integer x, Integer y) {
				return getColorF.e(x + p.x, y + p.y);
			}});
			if (check) {
				found[0] = p;
				return true;
			}
			return false;
		}});
		return found[0];
	}
	public Point indexIn(final BufferedImage img) {
		int widthDiff = img.getWidth() - getWidth();
		int heightDiff = img.getHeight() - getHeight();
		for (int offsetY = 0; offsetY <= heightDiff; offsetY++) {
			for (int offsetX = 0; offsetX <= widthDiff; offsetX++) {
				final int offsetX1 = offsetX;
				final int offsetY1 = offsetY;
				if (check(new F2<Integer, Integer, Color>() {public Color e(Integer x1, Integer y1) {
					return ImageUtil.getColor(offsetX1 + x1, offsetY1 + y1, img);
				}})) {
					return new Point(offsetX, offsetY);
				}
			}
		}
		return null;
	}
	
}
