package qj.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

import javax.imageio.ImageIO;

import qj.util.funct.F1;
import qj.util.funct.Fs;
import qj.util.funct.P1;
import qj.util.math.Color;
import qj.util.math.Dimension;
import qj.util.math.Point;

/**
 * Red Green Blue
 * @author anhquan.le
 *
 */
public class ImageUtil {
	//Transparent color
	public static Color TRANS_COLOR = new Color(254, 254, 254);
	
	/**
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage getImage(InputStream inputStream) {
		try {
//			ImageInputStream i;
			return ImageIO.read(inputStream);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static BufferedImage getImage(File path) {
		try {
			return ImageIO.read(path);
		} catch (IOException e) {
//			System.out.println(path.exists());
			throw new RuntimeException(path.toString(), e);
		}
	}
	
	public static Image getImage(String url) {
		InputStream in = null;
		try {
			try {
				in = new URL(url).openConnection().getInputStream();
				return getImage(in);
			} finally {
				if (in!=null)
					in.close();
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Assume that img1, img2 sizes are equals
	 * @param img1
	 * @param img2
	 * @return
	 */
	public static double compareImage(BufferedImage img1, BufferedImage img2) {
		Raster data1 = img1.getRaster();
		Raster data2 = img2.getRaster();
		int width = data1.getWidth();
		int height = data2.getHeight();
		double[] p1 = new double[3];
		double[] p2 = new double[3];
		double result = 0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				data1.getPixel(x, y, p1);
				data2.getPixel(x, y, p2);
				
				result += Math.abs(comparePixel(p1, p2));
			}
		}
		return result;
	}

	/**
	 * Assume that img1, img2 sizes are equals
	 * @param targetImg
	 * @param srcImg
	 * @param transColor 
	 * @return
	 */
	public static boolean equalsImage(BufferedImage targetImg, BufferedImage srcImg, int offsetX, int offsetY, java.awt.Color transColor, Integer diff) {
		Raster data1 = targetImg.getRaster();
		Raster data2 = srcImg.getRaster();
		int width = data1.getWidth();
		int height = data1.getHeight();
		int[] p1 = new int[4];
		int[] p2 = new int[4];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				data1.getPixel(x, y, p1);
				
				if (transColor!=null && 
						p1[0]==transColor.getRed() &&
						p1[1]==transColor.getGreen() &&
						p1[2]==transColor.getBlue()
						) {
					continue;
				}
				
				data2.getPixel(offsetX + x, offsetY + y, p2);
				

				if (!same(p1, p2, diff)) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	public static boolean same(int[] p1, int[] p2, Integer diff) {
		if (diff==null || diff == 0) {
			return p1[0] == p2[0] &&
					p1[1] == p2[1] &&
					p1[2] == p2[2];
		}
		int diffI = diff;
		return true
				&& Math.abs(p1[0] - p2[0]) < diffI
				&& Math.abs(p1[1] - p2[1]) < diffI
				&& Math.abs(p1[2] - p2[2]) < diffI
				&& Math.abs(total(p1) - total(p2)) < diffI * 2;
	}
	
	public static boolean same(Color c1, Color c2, Integer diff) {
		if (diff==null || diff == 0) {
			return c1.getRed() == c2.getRed() &&
					c1.getGreen() == c2.getGreen() &&
					c1.getBlue() == c2.getBlue();
		}
		int diffI = diff;
		return true
				&& Math.abs(c1.getRed() - c2.getRed()) < diffI
				&& Math.abs(c1.getGreen() - c2.getGreen()) < diffI
				&& Math.abs(c1.getBlue() - c2.getBlue()) < diffI
				&& Math.abs(total(c1) - total(c2)) < diffI * 2;
	}

	private static int total(int[] p) {
		return p[0] + p[1] + p[2];
	}
	public static int total(Color c) {
		return c.getRed() + c.getGreen() + c.getBlue();
	}

//	private static boolean equals(WritableRaster r, WritableRaster rasterSrc, int x1, int y1) {
//		int[] b1 = new int[4];
//		int[] b2 = new int[4];
//	
//		for (int y = 0; y < r.getHeight(); y++) {
//			for (int x = 0; x < r.getWidth(); x++) {
//				r.getPixel(x, y, b1);
//				rasterSrc.getPixel(x + x1, y + y1, b2);
//				if (!Arrays.equals(b1, b2)) {
//					return false;
//				}
//			}
//		}
//		return true;
//	}
	
	/**
	 * Assume that img1, img2 sizes are equals
	 * @param img1
	 * @param img2
	 * @return
	 */
	public static boolean equalsImage(byte[][] data1, BufferedImage img2, int offsetX, int offsetY) {
		int upperThreadHold = 0;
		int lowerThreadHold = 0;
		
		return equalsImage(data1, img2, offsetX, offsetY, upperThreadHold, lowerThreadHold);
	}
	
	/**
	 * Assume that img1, img2 sizes are equals
	 * @param img1
	 * @param img2
	 * @return
	 */
	public static boolean equalsImageI(int[][] data1, BufferedImage img2, int offsetX, int offsetY) {
		int upperThreadHold = 0;
		int lowerThreadHold = 0;
		
		return equalsImageI(data1, img2, offsetX, offsetY, upperThreadHold, lowerThreadHold);
	}
	public static boolean equalsImageI(int[][] data1, BufferedImage img2, int offsetX, int offsetY, int upperThreadHold, int lowerThreadHold) {
		Raster data2 = img2.getRaster();
		int width = data1[0].length / 3;
		int height = data1.length;
//		System.out.println(width);
//		System.out.println(height);
		if (data2.getWidth() - offsetX < width
				|| data2.getHeight() - offsetY < height) {
//			System.out.println("!");
			return false;
		}
		
		double[] p2 = new double[3];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int red 	= data1[y][x * 3 + 0];
				int green 	= data1[y][x * 3 + 1];
				int blue 	= data1[y][x * 3 + 2];
				
				// Transparent pixel
				if (red == TRANS_COLOR.getRed()
						&& green == TRANS_COLOR.getGreen()
						&& blue == TRANS_COLOR.getBlue()) {
					continue;
				}
				
				data2.getPixel(offsetX + x, offsetY + y, p2);
				if (red > p2[0] + upperThreadHold || red < p2[0] - lowerThreadHold
						|| green > p2[1] + upperThreadHold || green < p2[1] - lowerThreadHold
						|| blue > p2[2] + upperThreadHold || blue < p2[2] - lowerThreadHold) {
//					System.out.println("!");
					return false;
				}
			}
		}
		return true;
	}

	public static boolean equalsImage(byte[][] data1, BufferedImage img2, int offsetX, int offsetY, int upperThreadHold, int lowerThreadHold) {
		Raster data2 = img2.getRaster();
		int width = data1[0].length / 3;
		int height = data1.length;
//		System.out.println(width);
//		System.out.println(height);
		if (data2.getWidth() - offsetX < width
				|| data2.getHeight() - offsetY < height) {
//			System.out.println("!");
			return false;
		}
		
		double[] p2 = new double[3];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int red 	= data1[y][x * 3 + 0] + 128;
				int green 	= data1[y][x * 3 + 1] + 128;
				int blue 	= data1[y][x * 3 + 2] + 128;
				
				// Transparent pixel
				if (red == TRANS_COLOR.getRed()
						&& green == TRANS_COLOR.getGreen()
						&& blue == TRANS_COLOR.getBlue()) {
					continue;
				}
				
				data2.getPixel(offsetX + x, offsetY + y, p2);
				if (red > p2[0] + upperThreadHold || red < p2[0] - lowerThreadHold
						|| green > p2[1] + upperThreadHold || green < p2[1] - lowerThreadHold
						|| blue > p2[2] + upperThreadHold || blue < p2[2] - lowerThreadHold) {
//					System.out.println("!");
					return false;
				}
			}
		}
		return true;
	}

	public static boolean equalsImage(byte[][] data1, BufferedImage img2, int offsetX, int offsetY, Color transColor) {
		Raster data2 = img2.getRaster();
		int width = data1[0].length / 3;
		int height = data1.length;
//		System.out.println(width);
//		System.out.println(height);
		if (data2.getWidth() - offsetX < width
				|| data2.getHeight() - offsetY < height) {
//			System.out.println("!");
			return false;
		}
		int lowerThreadHold = 0;
		int upperThreadHold = 0;
		
		int[] p2 = new int[3];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int red 	= data1[y][x * 3 + 0] + 128;
				int green 	= data1[y][x * 3 + 1] + 128;
				int blue 	= data1[y][x * 3 + 2] + 128;
				
				// Transparent pixel
				if (red == transColor.getRed()
						&& green == transColor.getGreen()
						&& blue == transColor.getBlue()) {
					continue;
				}
				
				data2.getPixel(offsetX + x, offsetY + y, p2);
				if (red > p2[0] + upperThreadHold || red < p2[0] - lowerThreadHold
						|| green > p2[1] + upperThreadHold || green < p2[1] - lowerThreadHold
						|| blue > p2[2] + upperThreadHold || blue < p2[2] - lowerThreadHold) {
//					System.out.println("!");
					return false;
				}
			}
		}
		return true;
	}
	
	public static Point indexOf(BufferedImage target, BufferedImage src) {
		return indexOf(target, src, null);
	}
	
	public static Point indexOf(BufferedImage target, BufferedImage src, Integer tolerance) {
		return indexOf(target, src, null, tolerance);
	}
	
	public static Point indexOf(BufferedImage target, BufferedImage src, java.awt.Color transColor, Integer tolerance) {
		int widthDiff = src.getWidth() - target.getWidth();
		int heightDiff = src.getHeight() - target.getHeight();
		for (int offsetX = 0; offsetX <= widthDiff; offsetX++) {
			for (int offsetY = 0; offsetY <= heightDiff; offsetY++) {
				
				if (equalsImage(target, src, offsetX, offsetY, transColor, tolerance)) {
					return new Point(offsetX, offsetY);
				}
			}
		}
		return null;
	}

	public static Point indexOf(byte[][] target, BufferedImage src) {
		int tolerance = 10;
		return indexOf(target, src, tolerance);
	}

	public static Point indexOf(byte[][] target, BufferedImage src,
			int tolerance) {
		int widthDiff = src.getWidth() - target[0].length / 3;
		int heightDiff = src.getHeight() - target.length;
		for (int offsetY = 0; offsetY <= heightDiff; offsetY++) {
//			System.out.println("offsetY=" + offsetY);
			for (int offsetX = 0; offsetX <= widthDiff; offsetX++) {
//				System.out.println("offsetX=" + offsetX);
				if (equalsImage(target, getData(src), offsetX, offsetY, tolerance)) {
					return new Point(offsetX, offsetY);
				}
			}
		}
		return null;
	}

	public static Point indexOf(byte[][] target, BufferedImage src,
			Color transColor) {
		int widthDiff = src.getWidth() - target[0].length / 3;
		int heightDiff = src.getHeight() - target.length;
		for (int offsetY = 0; offsetY <= heightDiff; offsetY++) {
			for (int offsetX = 0; offsetX <= widthDiff; offsetX++) {
				if (equalsImage(target, src, offsetX, offsetY, transColor)) {
					return new Point(offsetX, offsetY);
				}
			}
		}
		return null;
	}

	public static Point indexOf2(byte[][] target, BufferedImage src, Color c) {
		int tolerance = 10;
		
		return indexOf2(target, src, c, tolerance);
	}

	public static Point indexOf2(byte[][] target, BufferedImage src, Color c,
			int tolerance) {
		Random random = new Random();
		int rate = 1;
		int stepX = target[0].length / 3 / 4;
		int stepY = target.length / 4;
		stepX = stepX == 0 ? 1:stepX;
		stepY = stepY == 0 ? 1:stepY;
		int startX = random.nextInt(stepX);
		int startY = random.nextInt(stepY);
		int width = src.getWidth();
		int height = src.getHeight();
		int[] p = new int[3];
		for (int y = startY; y < height; y+=stepY) {
			for (int x = startX; x < width; x+=stepX) {
				// Get point
				src.getRaster().getPixel(x, y, p);
				
				// If in color range : 8, debug
				if (p[0] > c.getRed() - (tolerance + 1) && p[0] < c.getRed() + (tolerance + 1)
						&& p[1] > c.getGreen() - (tolerance + 1) && p[1] < c.getGreen() + (tolerance + 1)
						&& p[2] > c.getBlue() - (tolerance + 1) && p[2] < c.getBlue() + (tolerance + 1)) {
					int sampleX = Math.max(x - target[0].length / 3 * rate - 1, 0);
					int sampleY = Math.max(y - target.length * rate - 1, 0);
					byte[][] sample = getData(src, new Rectangle(sampleX, sampleY, target[0].length / 3 * rate * 2 + 2, target.length * rate * 2 + 2));
					
					// Make more detail search by target width * 2, height * 2
					Point p2 = indexOf(target, sample, tolerance);
//					DesktopUI.alert2(sample);
//					DesktopUI.alertS("" + p2);
					if (p2 == null) {
						continue;
					}
					p2.x += sampleX;
					p2.y += sampleY;
					return p2;
				}
			}
		}
		return null;
	}
	
	public static Point indexOf(byte[][] target, byte[][] src, int tolerance) {
		int widthDiff = src[0].length / 3 - target[0].length / 3;
		int heightDiff = src.length - target.length;
		for (int offsetY = 0; offsetY <= heightDiff; offsetY++) {
			for (int offsetX = 0; offsetX <= widthDiff; offsetX++) {
				if (equalsImage(target, src, offsetX, offsetY, tolerance)) {
					return new Point(offsetX, offsetY);
				}
			}
		}
		return null;
	}

	public static boolean equalsImage(byte[][] data1, byte[][] data2,
			int offsetX, int offsetY, int tolerance) {
		Color transColor = TRANS_COLOR;
		return equalsImage(data1, data2, offsetX, offsetY, tolerance,
				transColor);
	}

	public static boolean equalsImageI(int[][] data1, int[][] data2,
			int offsetX, int offsetY, int tolerance) {
		Color transColor = TRANS_COLOR;
		return equalsImageI(data1, data2, offsetX, offsetY, tolerance,
				transColor);
	}

	public static boolean equalsImage(byte[][] data1, byte[][] data2,
			int offsetX, int offsetY, int tolerance, Color transColor) {
		int width = data1[0].length / 3;
		int height = data1.length;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int red 	= data1[y][x * 3 + 0] + 128;
				int green 	= data1[y][x * 3 + 1] + 128;
				int blue 	= data1[y][x * 3 + 2] + 128;
				
				// Transparent pixel
				if (transColor != null
						&& red == transColor.getRed()
						&& green == transColor.getGreen()
						&& blue == transColor.getBlue()) {
					continue;
				}

				if (offsetY + y >= data2.length) {
					return false;
				}
				int y1 = offsetY + y;
				if (offsetX + x >= data2[y1].length / 3 ) {
					return false;
				}
				
				int x1 = (offsetX + x) * 3;
				int r2 = data2[y1][x1 + 0] + 128;
				int g2 = data2[y1][x1 + 1] + 128;
				int b2 = data2[y1][x1 + 2] + 128;
				if (red - 1 - tolerance > r2 || red + 1 + tolerance < r2
						|| green - 1 - tolerance > g2 || green + 1 + tolerance < g2
						|| blue - 1 - tolerance > b2 || blue + 1 + tolerance < b2) {
//					System.out.println("red=" + red);
//					System.out.println("green=" + green);
//					System.out.println("blue=" + blue);
//					System.out.println("red2=" + red2);
//					System.out.println("green2=" + green2);
//					System.out.println("blue2=" + blue2);
					return false;
				}
			}
		}
		return true;
	}

	public static boolean equalsImageI(int[][] data1, int[][] data2,
			int offsetX, int offsetY, int tolerance, Color transColor) {
		int width = data1[0].length / 3;
		int height = data1.length;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int red 	= data1[y][x * 3 + 0];
				int green 	= data1[y][x * 3 + 1];
				int blue 	= data1[y][x * 3 + 2];
				
				// Transparent pixel
				if (transColor != null
						&& red == transColor.getRed()
						&& green == transColor.getGreen()
						&& blue == transColor.getBlue()) {
					continue;
				}

				if (offsetY + y >= data2.length) {
					return false;
				}
				int y1 = offsetY + y;
				if (offsetX + x >= data2[y1].length / 3 ) {
					return false;
				}
				
				int x1 = (offsetX + x) * 3;
				int r2 = data2[y1][x1 + 0];
				int g2 = data2[y1][x1 + 1];
				int b2 = data2[y1][x1 + 2];
				if (red - 1 - tolerance > r2 || red + 1 + tolerance < r2
						|| green - 1 - tolerance > g2 || green + 1 + tolerance < g2
						|| blue - 1 - tolerance > b2 || blue + 1 + tolerance < b2) {
//					System.out.println("red=" + red);
//					System.out.println("green=" + green);
//					System.out.println("blue=" + blue);
//					System.out.println("red2=" + red2);
//					System.out.println("green2=" + green2);
//					System.out.println("blue2=" + blue2);
					return false;
				}
			}
		}
		return true;
	}

	private static byte[][] getData(BufferedImage im, Rectangle rect) {
		Raster data = im.getRaster();
		double[] p = new double[4];
		int height = Math.min(rect.height, im.getHeight() - rect.y);
		int width = Math.min(rect.width, im.getWidth() - rect.x);
		byte[][] ret = new byte[height][width * 3];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				data.getPixel(rect.x + x, rect.y + y, p);
				ret[y][x * 3 + 0] = (byte) (p[0] - 128);
				ret[y][x * 3 + 1] = (byte) (p[1] - 128);
				ret[y][x * 3 + 2] = (byte) (p[2] - 128);
			}
		}
		return ret;
	}
	private static java.awt.Color[][] getDataAwt(BufferedImage im, Rectangle rect) {
		Raster data = im.getRaster();
		double[] p = new double[4];
		int height = Math.min(rect.height, im.getHeight() - rect.y);
		int width = Math.min(rect.width, im.getWidth() - rect.x);
		java.awt.Color[][] ret = new java.awt.Color[height][width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				data.getPixel(rect.x + x, rect.y + y, p);
				ret[y][x] = new java.awt.Color((int)p[0], (int)p[1], (int)p[2]);
			}
		}
		return ret;
	}

	private static int[][] getDataI(BufferedImage im, Rectangle rect) {
		Raster data = im.getRaster();
		double[] p = new double[4];
		int height = Math.min(rect.height, im.getHeight() - rect.y);
		int width = Math.min(rect.width, im.getWidth() - rect.x);
		int[][] ret = new int[height][width * 3];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				data.getPixel(rect.x + x, rect.y + y, p);
				ret[y][x * 3 + 0] = (int) (p[0]);
				ret[y][x * 3 + 1] = (int) (p[1]);
				ret[y][x * 3 + 2] = (int) (p[2]);
			}
		}
		return ret;
	}

	public static byte[][] getData(BufferedImage im) {
		return getData(im, new Rectangle(0, 0, im.getWidth(), im.getHeight()));
	}

	public static Collection<byte[][]> getDatas(Collection<BufferedImage> images) {
		return Cols.yield(images, new F1<BufferedImage, byte[][]>() {public byte[][] e(BufferedImage obj) {
			return getData(obj);
		}});
	}

	public static java.awt.Color[][] getDataAwt(BufferedImage im) {
		return getDataAwt(im, new Rectangle(0, 0, im.getWidth(), im.getHeight()));
	}
	public static int[][] getDataI(BufferedImage im) {
		return getDataI(im, new Rectangle(0, 0, im.getWidth(), im.getHeight()));
	}
	
//	public static void main(String[] args) throws AWTException {
//		System.out.println((byte)127d);
//	}

	public static byte[][] absolute(int level, byte[][] data) {

		for (int y = 0; y < data.length; y++) {
			for (int x = 0; x < data[0].length / 3; x++) {
				if (data[y][x * 3] + data[y][x * 3 + 1] + data[y][x * 3 + 2] < level) {
					data[y][x * 3 + 0] = -128;
					data[y][x * 3 + 1] = -128;
					data[y][x * 3 + 2] = -128;
				} else {
					data[y][x * 3 + 0] = 127;
					data[y][x * 3 + 1] = 127;
					data[y][x * 3 + 2] = 127;
				}
			}
		}
		return data;
	}

	public static byte[][] filterColor(byte[][] data, Color color) {
		Color destColor = TRANS_COLOR;
		
		return filterColor(data, color, destColor);
	}

	public static byte[][] filterColor(byte[][] data, Color color,
			Color destColor) {
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		
		for (int y = 0; y < data.length; y++) {
			for (int x = 0; x < data[0].length / 3; x++) {
				if (data[y][x * 3] + 128 == red
						&& data[y][x * 3 + 1] + 128 == green
						&& data[y][x * 3 + 2] + 128 == blue) {
					data[y][x * 3 + 0] = (byte) (destColor.getRed() - 128);
					data[y][x * 3 + 1] = (byte) (destColor.getGreen() - 128);
					data[y][x * 3 + 2] = (byte) (destColor.getBlue() - 128);
				}
			}
		}
		return data;
	}

	public static double comparePixel(double[] p1, double[] p2) {
		double result = 0;
		for (int i = 0; i < p1.length; i++) {
			result += Math.abs(p2[i] - p1[i]);
		}
		return result;
	}

	public static int comparePixel(int[] p1, int[] p2) {
		int result = 0;
		for (int i = 0; i < p1.length; i++) {
			result += Math.abs(p2[i] - p1[i]);
		}
		return result;
	}

	public static byte[][] absolute(int absoluteLevel,
			BufferedImage im) {
		return absolute(absoluteLevel, getData(im));
	}

	public static Point indexOf(byte[][] target, byte[][] src) {
		int widthDiff = src[0].length / 3 - target[0].length / 3;
		int heightDiff = src.length - target.length;
		for (int offsetY = 0; offsetY <= heightDiff; offsetY++) {
			for (int offsetX = 0; offsetX <= widthDiff; offsetX++) {
				if (equalsImage(target, src, offsetX, offsetY)) {
					return new Point(offsetX, offsetY);
				}
			}
		}
		return null;
	}
	public static void eachIndexOf(byte[][] target, byte[][] src, P1<Point> p1) {
		int tolerance = 40;
		int widthDiff = src[0].length / 3 - target[0].length / 3;
		int heightDiff = src.length - target.length;
		for (int offsetY = 0; offsetY <= heightDiff; offsetY++) {
			for (int offsetX = 0; offsetX <= widthDiff; offsetX++) {
				if (equalsImage(target, src, offsetX, offsetY, tolerance)) {
					p1.e(new Point(offsetX, offsetY));
					offsetX+=target[0].length / 3;
					offsetY+=target.length;
				}
			}
		}
	}
	
	public static Collection<Point> allIndexOf(byte[][] target, byte[][] src) {
		LinkedList<Point> ret = new LinkedList<Point>();
		P1<Point> p1 = Fs.store(ret);
		eachIndexOf(target,src,p1);
		return ret;
	}

	public static Collection<Point> allIndexOf(byte[][] target, BufferedImage src,
			int tolerance) {
		LinkedList<Point> ret = new LinkedList<Point>();
		int widthDiff = src.getWidth() - target[0].length / 3;
		int heightDiff = src.getHeight() - target.length;
		byte[][] data = getData(src);
		for (int offsetY = 0; offsetY <= heightDiff; offsetY++) {
			System.out.println("offsetY=" + offsetY);
			for (int offsetX = 0; offsetX <= widthDiff; offsetX++) {
				System.out.println("offsetX=" + offsetX);
				if (equalsImage(target, data, offsetX, offsetY, tolerance)) {
					ret.add(new Point(offsetX, offsetY));
				}
			}
		}
		return ret;
	}
	/**
	 * Assume that img1, img2 sizes are equals
	 * No tolerance
	 * @param img1
	 * @param img2
	 * @return
	 */
	public static boolean equalsImage(byte[][] data1, byte[][] data2, int offsetX, int offsetY) {
		Color transColor = TRANS_COLOR;
		return equalsImage(data1, data2, offsetX, offsetY, transColor);
	}
	
	/**
	 * Assume that img1, img2 sizes are equals
	 * No tolerance
	 * @param img1
	 * @param img2
	 * @return
	 */
	public static boolean equalsImageI(int[][] data1, int[][] data2, int offsetX, int offsetY) {
		Color transColor = TRANS_COLOR;
		return equalsImageI(data1, data2, offsetX, offsetY, transColor);
	}

//	public static void main(String[] args) {
//		
//	}
	
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
	public static boolean equalsImage(byte[][] data1, byte[][] data2,
			int offsetX, int offsetY, Color transColor) {
		int width = data1[0].length / 3;
		int height = data1.length;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int red 	= data1[y][x * 3 + 0] + 128;
				int green 	= data1[y][x * 3 + 1] + 128;
				int blue 	= data1[y][x * 3 + 2] + 128;
				
				// Transparent pixel
				if (red == transColor.getRed()
						&& green == transColor.getGreen()
						&& blue == transColor.getBlue()) {
					continue;
				}
				
				if (red != data2[offsetY + y][(offsetX + x) * 3 + 0] + 128
						|| green != data2[offsetY + y][(offsetX + x) * 3 + 1] + 128
						|| blue != data2[offsetY + y][(offsetX + x) * 3 + 2] + 128) {
					return false;
				}
			}
		}
		return true;
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
	public static boolean equalsImageI(int[][] data1, int[][] data2,
			int offsetX, int offsetY, Color transColor) {
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
				
				if (red != data2[offsetY + y][(offsetX + x) * 3 + 0]
						|| green != data2[offsetY + y][(offsetX + x) * 3 + 1]
						|| blue != data2[offsetY + y][(offsetX + x) * 3 + 2]) {
					return false;
				}
			}
		}
		return true;
	}

	public static byte[][] magnify(int rate, byte[][] data) {
		byte[][] ret = new byte[data.length * rate][data[0].length * rate];
		for (int y = 0; y < data.length; y++) {
			for (int x = 0; x < data[0].length / 3; x++) {
				byte red 	= data[y][x * 3 + 0];
				byte green	= data[y][x * 3 + 1];
				byte blue	= data[y][x * 3 + 2];
				for (int i = 0; i < rate; i++) {
					for (int j = 0; j < rate; j++) {
						ret[y * rate + i][(x * rate + j) * 3 + 0] = red;
						ret[y * rate + i][(x * rate + j) * 3 + 1] = green;
						ret[y * rate + i][(x * rate + j) * 3 + 2] = blue;
					}
				}
			}
		}
		return ret;
	}
	
	public static int[][] getData2(BufferedImage image) {
		return null;
//		Raster data = im.getRaster();
//		int width 	= data.getWidth();
//		int height 	= data.getHeight();
//		double[] p = new double[3];
//		byte[][] ret = new byte[height][width * 3];
//		for (int y = 0; y < height; y++) {
//			for (int x = 0; x < width; x++) {
//				data.getPixel(x, y, p);
//				ret[y][x * 3 + 0] = (byte) (p[0] - 128);
//				ret[y][x * 3 + 1] = (byte) (p[1] - 128);
//				ret[y][x * 3 + 2] = (byte) (p[2] - 128);
//			}
//			
//		}
//		return ret;
	}

	public static byte[][] createImageData(int width, int height, Color color) {
		return new byte[height][width * 3];
	}

	public static File saveImage(BufferedImage image) {
		File dir = new File(".");
		return saveImage(image, dir);
	}

	public static File saveImage(BufferedImage image, File dir) {
		dir.mkdirs();
		File newFile = newFile(dir);
		writeToFile(image, newFile);
		return newFile;
	}

	public static void writeToFile(BufferedImage image, File file) {
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] serialize(BufferedImage image) {
		return serialize(image, null);
	}

	public static byte[] serialize(BufferedImage image, String imageFormat) {
		if (imageFormat==null) {
			imageFormat = "png";
		}
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(image, imageFormat, out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(Arrays.toString(ImageIO.getWriterFormatNames()));
		ThreadUtil.sleep(2000);
//		BufferedImage img = DesktopUtil.capture();
		BufferedImage img = scale(DesktopUtil.capture(), 0.5);
//		byte[] bytes = serialize(img, "jpg");
		byte[] bytes = serialize(img, "png");
		System.out.println(bytes.length);
//		DesktopUI4.alert(new ImagePane(scale(deserialize(bytes), 10)));
		;
	}

	public static void writeImage(BufferedImage image, OutputStream out) {
		try {
			ImageIO.write(image, "png", out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static File newFile(File dir) {
		for (int i = 0;;i++) {
			File file = new File(dir, "P" + new DecimalFormat("000").format(i) + ".png");
			if (!file.exists()) {
				return file;
			}
		}
	}


	public static int getSameColorLength(BufferedImage im) {
		Raster data = im.getRaster();
		int[] sample = new int[3];
		int[] p = new int[3];
		data.getPixel(0, 0, sample);
		int width = data.getWidth();
		int height = im.getHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				data.getPixel(x, y, p);
				
				for (int j = 0; j < sample.length; j++) {
					if (Math.abs(p[j] - sample[j]) > 5)
						return x;
				}

				sample[0] = p[0];
				sample[1] = p[1];
				sample[2] = p[2];
			}
		}
		return width;
	}

	/**
	 * 
	 * @param color
	 * @param d diameter
	 * @param im
	 * @return
	 */
	public static Point findSpot(Color color, int d, BufferedImage im) {
		byte[][] data = getData(im);
//		DesktopUI.show(im);
		
		for (int y = 0; y < data.length; y+=d/2) {
			for (int x = 0; x < data[0].length / 3 - d/2; x+=d/2) {
//				System.out.println("Checking " + x + ", " + y);
				if (inRange(getColor(x, y, data), color)
						&& inRange(getColor(x + d/2	, y			, data), color)
						&& inRange(getColor(x		, y + d/2	, data), color)
						&& inRange(getColor(x + d/2	, y + d/2	, data), color)
						&& inRange(data, color, x, y, d/2, d/2)) {
					return new Point(x + d/4, y + d/4);
				}
			}
		}
		return null;
	}
	
	private static boolean inRange(byte[][] data, Color color, int offsetX,
			int offsetY, int width, int height) {
		for (int y = offsetY; y < data.length && y < offsetY + height; y++) {
			for (int x = offsetX; x < data[0].length / 3 && x < offsetX + width; x++) {
				if (!inRange(getColor(x, y, data), color))
					return false;
			}
		}
		return true;
	}

	private static boolean inRange(Color color, Color color2) {
		int highThreadHold = 5;
		int lowThreadHold = 40;
		
		return inRange(color, color2, highThreadHold, lowThreadHold);
	}

	public static boolean inRange(Color color, Color color2,
			int highThreadHold, int lowThreadHold) {
		if (color== null || color2 == null)
			return false;
		boolean ret = (color.getRed() < color2.getRed() + highThreadHold && color.getRed() > color2
				.getRed() - lowThreadHold)
				&& (color.getGreen() < color2.getGreen() + highThreadHold && color
						.getGreen() > color2.getGreen() - lowThreadHold)
				&& (color.getBlue() < color2.getBlue() + highThreadHold && color.getBlue() > color2
						.getBlue() - lowThreadHold);
		return ret;
	}

	public static Color getColor(int x, int y, byte[][] im) {
		if (y >= im.length || x >= im[0].length / 3)
			return null;
		
		int red 	= im[y][x * 3 + 0] + 128;
		int green 	= im[y][x * 3 + 1] + 128;
		int blue 	= im[y][x * 3 + 2] + 128;
		
		return new Color(red, green, blue);
	}

	public static void removeSalt(byte[][] im) {
		byte[][] clone = clone(im);

		Color fillColor = null;
		for (int y = 0; y < im.length; y++) {
			for (int x = 0; x < im[0].length / 3; x++) {
				fillColor = null;
				// If black or white then fill with red and compare
				if (im[y][x * 3 + 0] == -128
				        && im[y][x * 3 + 1] == -128
				        && im[y][x * 3 + 2] == -128)
					fillColor = Color.DARK_GRAY;
				else if (im[y][x * 3 + 0] == 127
						&& im[y][x * 3 + 1] == 127
						&& im[y][x * 3 + 2] == 127)
					fillColor = Color.LIGHT_GRAY;
				
				if (fillColor!=null) {
					fill(Color.RED, x, y, clone);
					
					if (compare(clone, im) < 20) {
						if (fillColor == Color.DARK_GRAY)
							fillColor = Color.LIGHT_GRAY;
						else
							fillColor = Color.DARK_GRAY;
					}
					
					fill(fillColor, x, y, im);
					
					copy(clone, im);
				}
			}
		}
		clone = null;
		filterColor(im, Color.DARK_GRAY, Color.BLACK);
		filterColor(im, Color.LIGHT_GRAY, Color.WHITE);
	}
	
	public static int compare(byte[][] im1, byte[][] im2) {
		int ret = 0;
		for (int y = 0; y < im1.length; y++) {
			for (int x = 0; x < im1[0].length / 3; x++) {
				if (im1[y][x * 3 + 0] != im2[y][x * 3 + 0]
				        || im1[y][x * 3 + 1] != im2[y][x * 3 + 1]
				        || im1[y][x * 3 + 2] != im2[y][x * 3 + 2])
					++ret;
			}
		}
		return ret;
	}

	public static byte[][] clone(byte[][] im) {
		byte[][] clone = new byte[im.length][im[0].length];
		
		copy(clone, im);
		
		return clone;
	}
	
	public static void copy(byte[][] target, byte[][] src) {
		
		for (int y = 0; y < target.length; y++) {
			for (int x = 0; x < target[0].length; x++) {
				target[y][x] = src[y][x];
			}
		}
	}
	
	private static HashMap colorMap = new HashMap();
	private static Color getColor(byte r, byte g, byte b) {
		String key = ("" + r + "," + g + "," + b).intern();
		
		Color ret = (Color) colorMap.get(key);
		if (ret==null) {
			ret = new Color(r + 128, g + 128, b + 128);
			colorMap.put(key, ret);
		}
		return ret;
	}
	
	private static void fill(Color color, int x, int y, byte[][] im) {
		Color oldColor = getColor(im[y][x * 3 + 0], im[y][x * 3 + 1], im[y][x * 3 + 2]);
		im[y][x * 3 + 0] = (byte) (color.getRed()   - 128);
		im[y][x * 3 + 1] = (byte) (color.getGreen() - 128);
		im[y][x * 3 + 2] = (byte) (color.getBlue()  - 128);
		int x1;
		int y1;
		for (int i = 0; i < 4; i++) {
			if (i==0) {
				x1 = x - 1;
				y1 = y;
			} else if (i == 1) {
				x1 = x + 1;
				y1 = y;
			} else if (i == 2) {
				x1 = x;
				y1 = y - 1;
			} else {
				x1 = x;
				y1 = y + 1;
			}
			if (shouldErase(oldColor, x1, y1, im))
				fill(color, x1, y1, im);
		}
	}

	private static boolean shouldErase(Color color, int x, int y, byte[][] im) {
		
		return !(x < 0 || x >= im[0].length / 3
			|| y < 0 || y >= im   .length
			|| im[y][x * 3 + 0] + 128 != color.getRed()
			|| im[y][x * 3 + 1] + 128 != color.getGreen()
			|| im[y][x * 3 + 2] + 128 != color.getBlue())
			;
	}

	public static boolean compare(BufferedImage p1, BufferedImage p2) {
		if (p1 == null) {
			return p2==null;
		} else if (p2==null) {
			return false;
		}

		Raster r1 = p1.getRaster();
		Raster r2 = p2.getRaster();
		
		return compare(r1, r2);
	}

	public static boolean compare(Raster r1, Raster r2) {
		if (r1.getWidth() != r2.getWidth() ||
				r1.getHeight() != r2.getHeight()
				) {
			return false;
		}
		
		int[] b1 = new int[4];
		int[] b2 = new int[4];
	
		for (int y = 0; y < r1.getHeight(); y++) {
			for (int x = 0; x < r1.getWidth(); x++) {
				r1.getPixel(x, y, b1);
				r2.getPixel(x, y, b2);
				if (!Arrays.equals(b1, b2)) {
					return false;
				}
			}
		}
		return true;
	}

	public static BufferedImage deserialize(byte[] content) {
		try {
			return ImageIO.read(new ByteArrayInputStream(content));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static BufferedImage scale(BufferedImage image, double scale) {
		int scaledWidth = (int)Math.ceil(image.getWidth() * scale);
		int scaledHeight = (int)Math.ceil(image.getHeight() * scale);
		BufferedImage scaledBI = new BufferedImage(
				scaledWidth, 
				scaledHeight, 
				BufferedImage.TYPE_INT_RGB);
    	Graphics2D g = scaledBI.createGraphics();
    	g.drawImage(image, 0, 0, scaledWidth, scaledHeight, null); 
    	g.dispose();
    	return scaledBI;
	}

	public static BufferedImage subImageForced(qj.util.math.Rectangle rect,
			BufferedImage img, Color fillColor) {
		BufferedImage bufferedImage = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bufferedImage.createGraphics();
		g.setColor(fillColor.toAwt());
		g.fillRect(0, 0, rect.width, rect.height);
		g.drawImage(img, -rect.x, -rect.y, null);
		return bufferedImage;
	}
	
	public static BufferedImage subImage(qj.util.math.Rectangle rect,
			BufferedImage img) {
		int x2 = rect.x + rect.width;
		x2 = Math.min(x2, img.getWidth());
    	return img.getSubimage(rect.x, rect.y, x2 - rect.x, rect.height);
	}

	public static boolean[][] selectColor(java.awt.Color color, BufferedImage img) {
		boolean[][] select = new boolean[img.getHeight()][img.getWidth()];
		WritableRaster raster = img.getRaster();
		int[] colors = new int[4];
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				raster.getPixel(x, y, colors);
				if (
						color.getRed() == colors[0] &&
						color.getGreen() == colors[1] &&
						color.getBlue() == colors[2]
						) {
					select[y][x] = true;
				}
			}
		}
		return select;
	}

	public static void fillSelection(
			java.awt.Color color,
			boolean[][] selection, 
			BufferedImage img) {
		Graphics2D g = img.createGraphics();
		for (int y = 0; y < selection.length; y++) {
			for (int x = 0; x < selection[0].length; x++) {
				if (selection[y][x]) {
					g.setColor(color);
					g.fillRect(x, y, 1, 1);
				}
			}
		}
		g.dispose();
	}

	public static Color getColor(int x, int y, BufferedImage img) {
        if ((x < 0) || (y < 0) || (x >= img.getWidth()) || (y >= img.getHeight())) {
            return null;
        }

        int[] is = new int[4];
		img.getRaster().getPixel(x, y, is);
		return new Color(is[0], is[1], is[2]);
	}
	public static Color getColor(Point p, BufferedImage img) {
		return getColor(p.x, p.y, img);
	}

	public static Dimension getSize(BufferedImage mapImg) {
		return new Dimension(mapImg.getWidth(), mapImg.getHeight());
	}

	public static void drawHLine(int x, int y, int width, Color color,
			BufferedImage mapImg) {
		// TODO Auto-generated method stub
		
	}
	
	public static ImageWrapper wrap(BufferedImage img) {
		return new ImageWrapper(img);
	}

	public static class ImageWrapper {

		public final BufferedImage img;

		public ImageWrapper(BufferedImage img) {
			this.img = img;
		}

		public Color getColor(int x, int y) {
			return ImageUtil.getColor(x, y, img);
		}

		public int getWidth() {
			return img.getWidth();
		}

		public Dimension getSize() {
			return ImageUtil.getSize(img);
		}

		public int getHeight() {
			return img.getHeight();
		}
		
		
		public boolean same(qj.util.math.Rectangle rect, final F1<Color, Boolean> colorF) {
			final boolean[] ret = {true};
			each(rect, new F1<Color,Boolean>() {public Boolean e(Color c) {
				boolean correctColor = colorF.e(c);
				if (!correctColor) {
					ret[0] = false;
					return true; // Interrupted
				}
				return false;
			}});
			return ret[0];
		}
		
		private void each(qj.util.math.Rectangle rect, F1<Color, Boolean> f1) {
			for (int y = 0; y < rect.height; y++) {
				for (int x = 0; x < rect.width; x++) {
					if (f1.e(getColor(x + rect.x, y + rect.y))) {
						break;
					}
				}
			}
		}

	}

	public static void replaceColor(qj.util.math.Rectangle rectangle,
			java.awt.Color white, BufferedImage img) {
		final Graphics2D g = img.createGraphics();
		rectangle.eachPoint(new F1<Point, Boolean>() {public Boolean e(Point p) {
			g.fillRect(p.x, p.y, 1, 1);
			return false;
		}});
		g.dispose();
	}
	
}
