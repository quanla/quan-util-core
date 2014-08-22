package qj.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

public class DataImagePane extends Component {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4863949297772162077L;
	
	private byte[][] dataImage;
	public DataImagePane(byte[][] dataImage) {
		setImage(dataImage);
		this.setSize(dataImage[0].length / 3, dataImage.length);
		this.setPreferredSize(new Dimension(dataImage[0].length / 3, dataImage.length));
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		for (int y = 0; y < dataImage.length; y++) {
			for (int x = 0; x < dataImage[0].length / 3; x++) {
				int red 	= dataImage[y][x * 3 + 0] + 128;
				int green 	= dataImage[y][x * 3 + 1] + 128;
				int blue 	= dataImage[y][x * 3 + 2] + 128;
				g.setColor(new Color(red, green, blue));
				g.fillRect(x, y, 1, 1);
			}
		}
	}

	public void setImage(byte[][] dataImage) {
		this.dataImage = dataImage;
		this.repaint();
	}
	
}
