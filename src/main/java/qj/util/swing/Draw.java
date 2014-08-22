package qj.util.swing;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;

public class Draw {
	final JComponent component;
	public Draw(Graphics g, Rectangle rectangle, JComponent component) {
		this.component = component;
		brush = new Brush(g);
		brush.draw = this;
		bound = new Rectangle(0, 0, component.getWidth(), component.getHeight());
	}
	public Brush brush;
	public Rectangle bound;
	// Current color check
}