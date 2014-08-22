package qj.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class ImageUtilAwt {

	public static Point indexOf(BufferedImage target, BufferedImage src, Color transColor) {
		int widthDiff = src.getWidth() - target.getWidth();
		int heightDiff = src.getHeight() - target.getHeight();
		for (int offsetX = 0; offsetX <= widthDiff; offsetX++) {
			for (int offsetY = 0; offsetY <= heightDiff; offsetY++) {
				
				if (ImageUtil.equalsImage(target, src, offsetX, offsetY, transColor, null)) {
					return new Point(offsetX, offsetY);
				}
			}
		}
		return null;
	}


	public static BufferedImage magnify(double magnify, BufferedImage img1) {
		
		BufferedImage img = new BufferedImage((int) (img1.getWidth()*magnify), (int) (img1.getHeight()*magnify), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		int pixelSize = (int) (Math.ceil(magnify));
		for (int y = 0; y < img1.getHeight(); y++) {
			for (int x = 0; x < img1.getWidth(); x++) {
				g.setColor(ImageUtilAwt.getColor(x,y,img1));
				g.fillRect((int) (x * magnify), (int) (y * magnify), pixelSize, pixelSize);
			}
		}
		return img;
	}


	private static Color getColor(int x, int y, BufferedImage img) {
		int[] arr = new int[3];
		img.getRaster().getPixel(x, y, arr);
		
		return new Color(arr[0], arr[1], arr[2]);
	}
}
