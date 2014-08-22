package qj.util;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

public class DesktopUtil4 {
	public static boolean mouseUsedByBot = false;
	
	public static Point getMousePos() {
	    return MouseInfo.getPointerInfo().getLocation();
	}
	
	public static void doubleClick(Point clickPoint) {
		Robot robot;
		try {
			robot = new Robot();
			Point loc = MouseInfo.getPointerInfo().getLocation();
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON2_MASK);
			robot.setAutoDelay(10);

			mouseUsedByBot = true;
			robot.mouseMove(clickPoint.x, clickPoint.y);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseMove(clickPoint.x, clickPoint.y);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			
			robot.mouseMove(clickPoint.x, clickPoint.y);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseMove(clickPoint.x, clickPoint.y);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			robot.mouseMove(loc.x, loc.y);
			mouseUsedByBot = false;
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	protected static int autoDelay = 30;
	public static Robot robot;
	static {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			throw new RuntimeException(e);
		}
		
	}

	public static void click() {
		click((Point)null);
	}

	public static void click(Point clickPoint) {
		Point loc = MouseInfo.getPointerInfo().getLocation();
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON2_MASK);
		mouseUsedByBot = true;
		
		if (clickPoint!=null) {
			//			ThreadUtil.sleep_force(60);
			robot.mouseMove(clickPoint.x, clickPoint.y);
		}
		ThreadUtil4.sleep(autoDelay * 2);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		ThreadUtil4.sleep(autoDelay);
//			robot.mouseMove(clickPoint.x, clickPoint.y);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		ThreadUtil4.sleep(autoDelay * 2);
		robot.mouseMove(loc.x, loc.y);
		ThreadUtil4.sleep(autoDelay * 2);
		mouseUsedByBot = false;
	}
	static int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
	public static int getScreenHeight() {
		return screenHeight;
	}
	static int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static int getScreenWidth() {
		return screenWidth;
	}
	private static boolean usingMouse = false;
	private static Runnable METHOD_CHECK;
	
	/**
	 * Use this simple one to detect if mouse is moved
	 * @return
	 */
	public static boolean isMouseUsed() {
		if (METHOD_CHECK==null) {
			METHOD_CHECK = new Runnable() {
				public void run() {
					Point lastLocation = MouseInfo.getPointerInfo().getLocation();
					Point location;
					while (true) {
						ThreadUtil4.sleep(100);
						
						if (mouseUsedByBot) {
							ThreadUtil4.sleep(autoDelay * 3);
							continue;
						}
						
						location = MouseInfo.getPointerInfo().getLocation();
						if (location.x == lastLocation.x
								&& location.y == lastLocation.y) {
							usingMouse = false;
						} else {
							usingMouse = true;
							ThreadUtil4.sleep(2000);
						}
						lastLocation = location;
					}
				}
			};
			Thread t = new Thread(METHOD_CHECK);
			t.setDaemon(true);
			t.setPriority(1);
			t.start();
			return false;
		} else {
			return usingMouse;
		}
	}
	
	public static void setAutoDelay(int autoDelay) {
		DesktopUtil4.autoDelay = autoDelay;
	}

    /**
     * Press a keyboard key
     * @param key
     */
	public static void type(int key) {
		Robot robot;
		try {
			robot = new Robot();
			robot.keyPress(key);
			ThreadUtil4.sleep(autoDelay * 2);
			robot.keyRelease(key);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	public static Point getScreenCenter() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return new Point(screenSize.width / 2, screenSize.height / 2);
	}
	public static void pressAltTab() {
		Robot robot;
		try {
			robot = new Robot();
			ThreadUtil4.sleep(autoDelay * 2);
			robot.keyPress(KeyEvent.VK_ALT);
			ThreadUtil4.sleep(autoDelay * 2);
			robot.keyPress(KeyEvent.VK_TAB);
			ThreadUtil4.sleep(autoDelay * 2);
			robot.keyRelease(KeyEvent.VK_TAB);
			ThreadUtil4.sleep(autoDelay * 2);
			robot.keyRelease(KeyEvent.VK_ALT);
			ThreadUtil4.sleep(autoDelay * 2);
		} catch (AWTException e) {
			e.printStackTrace();
		}

	}
	public static Rectangle getScreenRect() {
		return new Rectangle(0, 0, getScreenWidth(), getScreenHeight());
	}
	public static void click(int x, int y) {
		click(new Point(x, y));
	}
}
