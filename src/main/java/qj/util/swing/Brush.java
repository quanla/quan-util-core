package qj.util.swing;

import java.awt.*;
import java.awt.image.BufferedImage;

import qj.util.swing.MemImageAwt;

public class Brush {
	public final Graphics graphics;
	public Brush(Graphics graphics) {
		this.graphics = graphics;
	}
	
	Draw draw;
	public void drawImage(MemImageAwt image, int x1, int y1, double magnify) {
		if (image != null && magnify > 0) {
			image = image.magnify(magnify);
			int fromX = Math.max(
					draw.bound.x - x1,
					0
					);
			
			int fromY = Math.max(
					draw.bound.y - y1,
					0
					);
			
			int toX = Math.min(
					draw.bound.x + draw.bound.width - x1, 
					image.getWidth()
					);
			int toY = Math.min(
					draw.bound.y + draw.bound.height - y1, 
					image.getHeight()
					);
			
			if (
					fromX >= toX ||
					fromY >= toY
					) {
				return;
			}
			
			BufferedImage img = image.subImage(fromX, fromY, toX - fromX, toY - fromY);
			graphics.drawImage(img, Math.max(x1,0), Math.max(y1,0), draw.component);
		}
	}
	
	public void drawImage(BufferedImage image, int x, int y, double magnify) {
		graphics.drawImage(image, x, y, (int)(image.getWidth() * magnify), (int)(image.getHeight() * magnify), draw.component);
	}
	public void drawRect(int x, int y, int width, int height, Color c) {
		graphics.setColor(c);
		
		graphics.drawRect(x, y, width, height);
	}
	public void drawDashRect(Rectangle rect, Color c) {
		graphics.setColor(c);

		drawDashHorLine(rect.x, rect.width, rect.y);
		drawDashHorLine(rect.x, rect.width, rect.y + rect.height);

		drawDashVerLine(rect.x, rect.y, rect.height );
		drawDashVerLine(rect.x + rect.width, rect.y, rect.height );
	}

	public void drawDashHorLine(int x, int width, int y) {
		int i = 0;
		for (; i < width - 3; i+= 7) {
			graphics.drawLine(x + i, y, x + i + 3, y);
		}
		if (i < width) {
			graphics.drawLine(x + i, y, x + width, y);
		}
	}

	public void drawDashVerLine(int x, int y, int height) {
		int i = 0;
		for (; i < height - 3; i+= 7) {
			graphics.drawLine(x, y + i, x, y + i + 3);
		}
		if (i < height) {
			graphics.drawLine(x, y + i, x, y + height);
		}
	}
}