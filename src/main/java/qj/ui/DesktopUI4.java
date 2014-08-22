package qj.ui;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;

import qj.util.SchedulerUtil;
import qj.util.SwingUtil;
import qj.util.SwingUtil4;
import qj.util.ThreadUtil;
import qj.util.ThreadUtil4;
import qj.util.funct.P0;

public class DesktopUI4 {

	public static int DEFAULT_REPEATING_DELAY = 200;
	public static int DEFAULT_KEY_RATE = 30;
	
	public static String prompt2() {
		JTextArea ta = new JTextArea(10, 20);
		alert(new JScrollPane(ta));
		return ta.getText();
	}
	
	public static void alert2(String str) {
		JTextArea ta = new JTextArea(20, 40);
		ta.setText(str);
		ta.selectAll();
//		Clipboard4.copy(str);
		
		alert(new JScrollPane(ta));
	}
	private static JLabel label;
	private static JFrame STR_FRAME;
	public static void show(String str) {
		JTextArea ta = new JTextArea(10, 20);
		ta.setText(str);
		ta.selectAll();

		if (STR_FRAME == null) {
			STR_FRAME = createFrame();
			
			label = new JLabel(str);
			STR_FRAME.getContentPane().add(label, BorderLayout.CENTER);
			STR_FRAME.pack();
		} else
			label.setText(str);
	}
	public static Rectangle promptRect() {
		alert("First point");
		Point p1 = MouseInfo.getPointerInfo().getLocation();
		alert("Second point");
		Point p2 = MouseInfo.getPointerInfo().getLocation();
		Rectangle rectangle = SwingUtil4.getRectangle(p1, p2);
		return rectangle;
	}
	public static Point promptPoint() {
		String str = "Select point on desktop";
		return promptPoint(str);
	}
	public static Point promptPoint(String str) {
		alert(str);
		Point p1 = MouseInfo.getPointerInfo().getLocation();
		return p1;
	}
	public static void alert(String message) {
		JOptionPane.showMessageDialog(null, message);
	}
	public static void alert(String message, Component parent) {
		JOptionPane.showMessageDialog(parent, message);
	}

	public static void alert(Component message) {
		JOptionPane.showMessageDialog(null, message);
	}

	public static String prompt(Image image) {
		return JOptionPane.showInputDialog(new ImagePane(image));
	}

	public static String prompt(String question) {
		String initialSelectionValue = "";
		return prompt(question, initialSelectionValue);
	}

	public static String prompt(String question, String initialSelectionValue) {
		return JOptionPane.showInputDialog(question, initialSelectionValue);
	}
	
	public static boolean confirm(String message) {
        return confirm(message, null);
    }

    public static boolean confirm(String message, Component comp) {
        return JOptionPane.showConfirmDialog(comp, message,
                                 UIManager.getString("OptionPane.titleText"),
                                 JOptionPane.OK_CANCEL_OPTION) == JOptionPane.YES_OPTION ? true : false;
    }

    @SuppressWarnings("unchecked")
	public static void alert(Collection col) {
		JTable table = new JTable(col.size(), 1);
		int i = 0;
		for (Iterator iter = col.iterator(); iter.hasNext(); i++) {
			String o = (String) iter.next();
			table.setValueAt(o, i, 0);
		}
//		table.setEnabled(false);
		alert(table);
	}

	@SuppressWarnings("unchecked")
	public static void alert(ResultSet rs) {
		if (rs==null) {
			alert("null");
			return;
		}

		try {
			Vector vCols = new Vector();
			Vector vRows = new Vector();
			
			ResultSetMetaData meta;
				meta = rs.getMetaData();
			
			int columnCount = meta.getColumnCount();
			for (int i=1;i<=columnCount;i++) {
				vCols.add(meta.getColumnLabel(i));
			}
	
			int count = 0;
			while (rs.next()) {
				Vector row = new Vector();
				for (int i=1;i<=columnCount;i++) {
					if (meta.getColumnType(i)!=Types.BLOB) {
						row.add(rs.getString(i));
					} else {
						row.add("(( BLOB ))");
					}
				}
	
				count++;
				vRows.add(row);
			}
	
			JTable result = new JTable(vRows, vCols);
			result.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			JScrollPane sp = new JScrollPane(result,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
					);
			alert(sp);
		} catch (SQLException e) {
			alert(e.toString());
		}
	}

	private static JFrame CB_FRAME;
	private static JFrame IMG_FRAME;
	private static ImagePane ip;
	public static void show(Image i) {
//		if (i==null) {
//			return;
//		}
		
		if (IMG_FRAME == null) {
			IMG_FRAME = createFrame();
			
			ip = new ImagePane(i);
			IMG_FRAME.getContentPane().add(ip, BorderLayout.CENTER);
			IMG_FRAME.pack();
		} else
			ip.setImage(i);
	}
	private static JFrame createFrame() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setVisible(true);
		SwingUtil.centerlize(frame);
		return frame;
	}

	private static DataImagePane dip;
	public static void show(byte[][] i) {
		if (IMG_FRAME == null) {
			IMG_FRAME = createFrame();
			
			dip = new DataImagePane(i);
			IMG_FRAME.getContentPane().add(dip, BorderLayout.CENTER);
			IMG_FRAME.pack();
		} else
			dip.setImage(i);
	}
	
	private static JCheckBox checkBox = new JCheckBox("Check", true);
	public static void showChecker() {
		if (CB_FRAME == null || CB_FRAME.isVisible()) {
			CB_FRAME = new JFrame();
			CB_FRAME.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			CB_FRAME.setLayout(new BorderLayout());
			SwingUtil.centerlize(CB_FRAME);
			
			CB_FRAME.getContentPane().add(checkBox, BorderLayout.CENTER);
			CB_FRAME.pack();
		}
		
		CB_FRAME.setVisible(true);
		
	}

	public static boolean isCheckerChecked() {
		return checkBox.isSelected();
	}

	public static boolean isCheckerFrameShown() {
		return CB_FRAME == null || CB_FRAME.isVisible();
	}

	public static boolean isStrFrameShown() {
		return STR_FRAME == null || STR_FRAME.isVisible();
	}
	
	public static void setChecker(boolean checked) {
		checkBox.setSelected(checked);
	}
	
	public static void centerOnScreen(Window window) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = window.getSize();
		int desktopWidth = screenSize.width;

        // To the left (hack for Ngoc's dual display)
        if (desktopWidth > 1600) {
        	desktopWidth += 1600;
        }
		window.setLocation((desktopWidth - size.width) / 2, (screenSize.height - size.height) / 2);
	}
	
	public static void alert() {
		alert((String)null);
	}

	private static Runnable BEEP = new Runnable() {
		public void run() {
			while (Thread.currentThread().getName().equals("Run")) {
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				toolkit.beep();
				ThreadUtil4.sleep(200);
				toolkit.beep();
				ThreadUtil4.sleep(500);
			}
		}
	};
	private static Thread beepThread;
	public static void startBeep() {
		beepThread = new Thread(BEEP, "Run");
		beepThread.start();
	}
	
	public static void stopBeep() {
		beepThread.setName("Stop");
	}
	public static void alert2(byte[][] dataImage) {
		alert(new DataImagePane(dataImage));
	}

	
	public static void toBackChecker() {
		CB_FRAME.toBack();
	}
	public static void beep(int num) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		for (int i = 0; i < num; i++) {
			if (i>0) {
				ThreadUtil4.sleep(300);
			}
			toolkit.beep();
		}
	}

	public static void beepNonStop() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		for (;;) {
			ThreadUtil4.sleep(300);
			toolkit.beep();
		}
	}
	public static void main(String[] args) {
		beep(3);
	}
	
	/**
	 * ^: Ctrl
	 * `: Alt
	 * ~: Shift
	 * @param str
	 */
	public static void type(String str) {
		type(str, DEFAULT_KEY_RATE);
	}
	/**
	 * ^: Ctrl
	 * `: Alt
	 * ~: Shift
	 */
	public static void type(String str, int keyRate) {
		char[] arrC = str.toUpperCase().toCharArray();
		CHARS:
		for (int i = 0; i < arrC.length; i++) {
			if (i > 0) {
				ThreadUtil4.sleep(keyRate);
			}
			int c = arrC[i];
			int specialKey=-1;
			switch (c) {
			case '^': specialKey = KeyEvent.VK_CONTROL; break;
			case '`': specialKey = KeyEvent.VK_ALT; break;
			case '~': specialKey = KeyEvent.VK_SHIFT; break;
			case '!': ThreadUtil4.sleep(500); continue CHARS;
			}
			
			if (specialKey > -1) {
				char key = arrC[++i];
				combo(specialKey, key, keyRate);
			} else {
				type(c, keyRate);
			}
		}
	}
	
	
	/**
	 * If the key is :, return ;
	 * @return
	 */
	public static Character unshiftKey(char c) {

		switch (c) {
		case '~': return '`';
		case '!': return '1';
		case '@': return '2';
		case '#': return '3';
		case '$': return '4';
		case '%': return '5';
		case '^': return '6';
		case '&': return '7';
		case '*': return '8';
		case '(': return '9';
		case ')': return '0';
		case '_': return '-';
		case '+': return '=';
		case '|': return '\\';
		case '{': return '[';
		case '}': return ']';
		case ':': return ';';
		case '"': return '\'';
		case '<': return ',';
		case '>': return '.';
		case '?': return '/';
		
		default: return null;
		}
	}
	
	public static void combo(int specialKey, int key, int sleepTime) {
		ROBOT.keyPress(specialKey);
		ThreadUtil.sleep(sleepTime);
		
		type(key, sleepTime);
		
		ThreadUtil.sleep(sleepTime);
		ROBOT.keyRelease(specialKey);
	}

	public static void ctrl(int key) {
		combo(new int[] {KeyEvent.VK_CONTROL}, key);
	}
	public static void alt(int key) {
		combo(new int[] {KeyEvent.VK_ALT}, key);
	}

	public static void combo(int[] specialKeys, int key) {
		for (int k : specialKeys) {
			ROBOT.keyPress(k);
		}
		ThreadUtil.sleep(DEFAULT_KEY_RATE);
		
		type(key, DEFAULT_KEY_RATE);
		
		ThreadUtil.sleep(DEFAULT_KEY_RATE);
		for (int k : specialKeys) {
			ROBOT.keyRelease(k);
		}
	}

	public static P0 comboF(final int[] specialKeys, final int key) {
		return new P0() {public void e() {
			combo(specialKeys, key);
		}};
	}
	
	public static void main1(String[] args) {
		combo(KeyEvent.VK_ALT, KeyEvent.VK_TAB, DEFAULT_KEY_RATE);
	}

	public static Robot ROBOT;
	static {
		try {
			ROBOT = new Robot();
		} catch (AWTException e) {
			throw new AssertionError(e);
		}
	}
	public static void type(int c) {
		type(c, DEFAULT_KEY_RATE);
	}

	public static P0 typeF(final int c) {
		return new P0() {public void e() {
			type(c, DEFAULT_KEY_RATE);
		}};
	}
	public static void type(int c, int sleepTime) {
		try {
			ROBOT.keyPress(c);
			ThreadUtil.sleep(sleepTime);
			ROBOT.keyRelease(c);
//			ThreadUtil.sleep(sleepTime);
		} catch (RuntimeException e) {
			System.out.println((char)c + ", " + c);
			throw e;
		}
	}
	static class KeyRepeater implements Runnable {
		int c;
		boolean run = true;
		public void run() {
			while (run) {
				type(c);
			}
		}
	};
	static KeyRepeater keyRepeater = new KeyRepeater();
	public static void keyPress(final int c, boolean repeating) {
		keyPress(c, repeating ? DEFAULT_REPEATING_DELAY : -1);
	}
	
	public static void keyPress(final int c, int repeatDelay) {
		ROBOT.keyPress(c);
		
		if (repeatDelay > -1) {
			keyRepeater.c = c;
			keyRepeater.run = true;
			SchedulerUtil.schedule(repeatDelay, keyRepeater);
		}
	}
	public static void keyPress(int c) {
		ROBOT.keyPress(c);
	}
	public static void keyRelease(int c) {
		ROBOT.keyRelease(c);
		
		if (keyRepeater.run && keyRepeater.c == c) {
			keyRepeater.run = false;
			SchedulerUtil.cancel();
		}
	}
	public static void mouseRight(int adjustment) {
		Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
		ROBOT.mouseMove(mouseLoc.x + adjustment, mouseLoc.y);
//		System.out.println(MouseInfo.getPointerInfo().getLocation());
	}
	public static void mouseUp(int adjustment) {
		Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
		ROBOT.mouseMove(mouseLoc.x, mouseLoc.y + adjustment);
	}
	public static void mouse(int i) {
		ROBOT.mousePress(i);
		ThreadUtil4.sleep(DEFAULT_KEY_RATE);
		ROBOT.mouseRelease(i);
	}
	public static void mouseMove(Point mouseLoc) {
		ROBOT.mouseMove(mouseLoc.x, mouseLoc.y);
	}
	public static void mousePress(int button) {
		if (button == 1) {
			ROBOT.mousePress(InputEvent.BUTTON1_MASK);
		} else if (button == 2) {
			ROBOT.mousePress(InputEvent.BUTTON2_MASK);
		} else if (button == 3) {
			ROBOT.mousePress(InputEvent.BUTTON3_MASK);
		}
	}
	public static void mouseRelease(int button) {
		if (button == 1) {
			ROBOT.mouseRelease(InputEvent.BUTTON1_MASK);
		} else if (button == 2) {
			ROBOT.mouseRelease(InputEvent.BUTTON2_MASK);
		} else if (button == 3) {
			ROBOT.mouseRelease(InputEvent.BUTTON3_MASK);
		}
	}
	public static Point mouseLoc() {
		return MouseInfo.getPointerInfo().getLocation();
	}
	public static void mouseClick(int button) {
		mousePress(button);
		ThreadUtil4.sleep(20);
		mouseRelease(button);
	}
	public static void mouseMove(Point2D point) {
		mouseMove(new Point((int)point.getX(), (int)point.getY()));
	}

	public static void mouseShake() {
		Point loc = mouseLoc();
//		System.out.println(loc);
		mouseMove(new Point(loc.x == 0 ? loc.x + 3 : loc.x - 3, loc.y));
		ThreadUtil4.sleep(100);
		mouseMove(loc);
	}

	public static void mouseWheel(int i) {
		ROBOT.mouseWheel(i);
	}
}
