package qj.util;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import qj.util.swing.SwingLists;

public class SwingUtil4 {
	public static Point getScreenCenterLocation(Window w) {
        Dimension size = w.getSize();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        double desktopHeight = toolkit.getScreenSize().getHeight();
        double desktopWidth = toolkit.getScreenSize().getWidth();

        // To the left (hack for Ngoc's dual display)
        if (desktopWidth > 1600) {
        	desktopWidth += 1600;
        }
        Point location = new Point();
        location.x = (int)(desktopWidth - size.getWidth()) / 2;
        location.y = (int)(desktopHeight - size.getHeight()) / 2;
        return location;
    }
    
    public static void main(String[] args) {

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        double desktopHeight = toolkit.getScreenSize().getHeight();
        double desktopWidth = toolkit.getScreenSize().getWidth();
        
        System.out.println("desktopHeight=" + desktopHeight);
        System.out.println("desktopWidth=" + desktopWidth);
	}
    
    public static Rectangle getRectangle(Point p1, Point p2) {
		return new Rectangle(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y));
	}
	
	public static Point getRectCenterPoint(Rectangle r) {
		return new Point(r.x + r.width / 2, r.y + r.height/2);
	}

	public static Point getCenterPoint() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		double desktopHeight = toolkit.getScreenSize().getHeight(); 
		double desktopWidth = toolkit.getScreenSize().getWidth(); 
		return new Point((int)desktopWidth / 2, (int)desktopHeight / 2);
	}

	public static double getDistance(Point p1, Point p2) {
		double x = p1.getX() - p2.getX();
		double y = p1.getY() - p2.getY();
		return Math.sqrt(x*x + y*y);
	}

	public static Point getDirectionPoint(double bearing, Point posFrom, int length) {
		int x = (int) (Math.sin(bearing) * length);
		int y = (int) (Math.cos(bearing) * length);
		return new Point(posFrom.x + x, posFrom.y + y);
	}
//	public static void main(String[] args) {
//		double bearing = Math.toRadians(90);
//		Point posFrom = new Point(0, 0);
//		System.out.println(getDirectionPoint(bearing, posFrom, 150));
//	}
	public static double getBearing(Point posTo, Point posFrom) {
		double x = posTo.getX() - posFrom.getX();
		double y = posTo.getY() - posFrom.getY();
		
		return Math.atan2(x, y);
	}

    public static void closeOnEscape(final JFrame frame) {
        KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        Action actionListener = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                frame.setVisible(false);
                frame.dispose();
            }
        };
        JRootPane rootPane = frame.getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", actionListener);
    }

    public static void focusSelect(final JTextComponent tf) {
		tf.addFocusListener(new FocusAdapter() {public void focusGained(FocusEvent e) {
			tf.selectAll();
		}});
	}

}
