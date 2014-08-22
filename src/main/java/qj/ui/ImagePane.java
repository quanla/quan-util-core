package qj.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class ImagePane extends JComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4863949297772162077L;
	
	public Image image;
	
	public Color bgColor = Color.WHITE;
	
	public ImagePane() {
	}
	public ImagePane(Image image) {
		setImage(image);
	}
	public ImagePane(Image image, Color bgColor) {
		this.bgColor = bgColor;
		setImage(image);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		if (image != null) {
			g.drawImage(image, 0, 0, image.getWidth(null), image
					.getHeight(null), Color.WHITE, null);
		} else {
			g.setColor(bgColor);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	public void setImage(Image image) {
		this.image = image;
		if (image != null) {
			this.setSize(image.getWidth(null), image.getHeight(null));
			this.setPreferredSize(new Dimension(image.getWidth(null), image
					.getHeight(null)));
//		} else {
//			this.setSize(0, 0);
//			this.setPreferredSize(new Dimension(0, 0));
		}
		this.repaint();
	}

	public Image getImage() {
		return image;
	}
	
}
