package qj.util;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import qj.util.funct.F0;
import qj.util.funct.Fs;
import qj.util.funct.P0;
import qj.util.funct.P1;
import qj.util.math.Point;

public class DesktopUtil extends DesktopUtil4 {

	public static P0 openTextF(final String path) {
		return openTextF(Fs.f0(path));
	}
	public static P0 openTextF(final F0<String> pathF) {
		return new P0() {
			public void e() {
//				try {
//					Desktop.getDesktop().open(new File(path));
//				} catch (IOException e1) {
//					throw new RuntimeException(e1);
//				}
				openText(pathF);
			}};
	}
	
	public static class Clickr {
		java.awt.Point loc = MouseInfo.getPointerInfo().getLocation();
		{
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON2_MASK);
			mouseUsedByBot = true;
		}
		public P1<Point> click = new P1<Point>() {public void e(Point point) {
			robot.mouseMove(point.x, point.y);
			ThreadUtil4.sleep(autoDelay * 2);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			ThreadUtil4.sleep(autoDelay);
		}};
		
		public void finish() {
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			ThreadUtil4.sleep(autoDelay * 2);
			robot.mouseMove(loc.x, loc.y);
			ThreadUtil4.sleep(autoDelay * 2);
			mouseUsedByBot = false;
		}
	}

	public static Color getPixelColor(java.awt.Point p) {
		return robot.getPixelColor(p.x, p.y);
	}

	public static void click(Point... clickPoints) {
		click(Arrays.asList(clickPoints));
	}

	public static void click(Collection<Point> clickPoints) {
		if (Cols.isEmpty(clickPoints)) {
			return;
		}
		java.awt.Point loc = MouseInfo.getPointerInfo().getLocation();
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON2_MASK);
		mouseUsedByBot = true;
		
		for (Point point : clickPoints) {
			robot.mouseMove(point.x, point.y);
			ThreadUtil4.sleep(autoDelay * 2);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			ThreadUtil4.sleep(autoDelay);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
//				robot.mouseMove(clickPoint.x, clickPoint.y);
		}
//		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		ThreadUtil4.sleep(autoDelay * 2);
		robot.mouseMove(loc.x, loc.y);
		ThreadUtil4.sleep(autoDelay * 2);
		mouseUsedByBot = false;
	}
	
	public static void openText(final F0<String> pathF) {
		String path = pathF.e();
		openText(path);
	}
	public static void openText(String path) {
//		if (!file.exists()) {
//			
//		}
		String textEditor = getTextEditor();
		if (textEditor != null) {
			ProcessUtil.exec(textEditor + " " + path, false);
		} else {
			try {
				File file = new File(path);
				Desktop.getDesktop().edit(file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	protected static String getTextEditor() {
		if (SystemUtil.isWindows()) {
			List<String> list = Arrays.asList(
					"c:/Program Files/EmEditor/EmEditor.exe",
					"c:/Program Files/TextPad/TextPad.exe",
					"c:/Program Files/Notepad++/notepad++.exe"
					);
			for (String string : list) {
				if (new File(string).exists()) {
					return string;
				}
			}
			return null;
		} else if (SystemUtil.isMac()) {
			return null;
		} else {
			return "gedit";
		}
	}
	public static void open(String path) {
		if (path.startsWith("http")) {
			try {
				Desktop.getDesktop().browse(new URI(path));
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
			return;
		}
		File file = new File(path);
		
		open(file);
	}
	public static void open(File file) {
		//		if (file.isDirectory()) {
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public static Dimension getScreenDimension() {
		return new Dimension(getScreenWidth(), getScreenHeight());
	}
	public static qj.util.math.Dimension getScreenDimension2() {
		return new qj.util.math.Dimension(getScreenWidth(), getScreenHeight());
	}
	public static BufferedImage capture() {
		return capture((java.awt.Rectangle)null);
	}
	public static BufferedImage capture(java.awt.Rectangle rect) {
		if (rect==null) {
			rect= new java.awt.Rectangle(getScreenDimension());
		}
		return robot.createScreenCapture(rect);
	}
	public static BufferedImage capture(qj.util.math.Rectangle rect) {
		return capture(rect == null ? null : rect.toAwt());
	}
	public static Point getMousePos2() {
		java.awt.Point p = getMousePos();
		return new Point(p.x,p.y);
	}
	
	static HashMap<Integer, P0> map = new HashMap<Integer, P0>();
	public static void keyPressRepeat(final int key) {
		P0 p0 = map.remove(key);
		if (p0!=null) {
			p0.e();
		}
		
		final boolean[] interrupted = {false};
		ThreadUtil.run(new P0() {public void e() {
			robot.keyPress(key);
			ThreadUtil.sleep(440);
			while (!interrupted[0]) {
				robot.keyPress(key);
				ThreadUtil.sleep(23);
			}
		}});
		
		map.put(key, new P0() {public void e() {
			interrupted[0] = true;
		}});
	}

	public static void keyRelease(final int key) {
		P0 p0 = map.remove(key);
		if (p0!=null) {
			p0.e();
		}
		
		robot.keyRelease(key);
	}
	public static void browse(String url) {
		if (SystemUtil.isWindows() || SystemUtil.isMac()) {
			try {
				Desktop.getDesktop().browse(new URI(url));
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		} else {
			ProcessUtil.exec("firefox " + url);
		}
	}
	
	public static boolean clickOn(BufferedImage img) {
		BufferedImage capture = capture();
		Point index = ImageUtil.indexOf(img, capture);
		if (index!=null) {
			Point clickOn = new Point(index.x + img.getWidth() / 2, index.y + img.getHeight() / 2);
			click(clickOn);
			return true;
		} else {
			return false;
		}
	}
}
