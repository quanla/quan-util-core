package qj.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;

import qj.util.ThreadUtil4;
import qj.util.appCommon.swing.QFrame;
import qj.util.funct.F0;

public class DesktopUI {
	public static int DEFAULT_KEY_RATE = 40;

	public static void alert(Component message) {
		JOptionPane.showMessageDialog(null, message);
	}
	public static void alert(String message) {
		JOptionPane.showMessageDialog(null, message);
	}
	public static void alert2(String str) {
		JTextArea ta = new JTextArea(20, 40);
		ta.setText(str);
		ta.selectAll();
//		Clipboard4.copy(str);
		
		alert(new JScrollPane(ta));
	}
	public static boolean confirm(String message) {
        return confirm(message, null);
    }

    public static boolean confirm(String message, Component comp) {
        return JOptionPane.showConfirmDialog(comp, message,
                                 UIManager.getString("OptionPane.titleText"),
                                 JOptionPane.OK_CANCEL_OPTION) == JOptionPane.YES_OPTION ? true : false;
    }
    /**
     * Async alert
     * @param comp
     * @return 
     */
    public static QFrame alert3(Component comp) {
        final QFrame frame = new QFrame("Alert");
        Container cp = frame.getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(comp, BorderLayout.CENTER);
        JButton btn = new JButton("Ok");
        btn.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
            frame.setVisible(false);
            frame.dispose();
        }});
        cp.add(btn, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setMinimumSize(new Dimension(200, 100));
        frame.pack();
        DesktopUI.centerOnScreen(frame);
        frame.setVisible(true);
        frame.toFront();
        return frame;
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
	public static String prompt(String question) {
		String initialSelectionValue = "";
		return prompt(question, initialSelectionValue);
	}
	public static String prompt(String question, String initialSelectionValue) {
		return JOptionPane.showInputDialog(question, initialSelectionValue);
	}
	

	public static Robot ROBOT;
	static {
		try {
			ROBOT = new Robot();
		} catch (AWTException e) {
			throw new AssertionError(e);
		}
	}
	public static void mouseMove(Point mouseLoc) {
		ROBOT.mouseMove(mouseLoc.x, mouseLoc.y);
	}


	public static BufferedImage capture(Rectangle rectangle) {
		return ROBOT.createScreenCapture(rectangle);
	}
	public static void mouseClick(int button) {
		mousePress(button);
		ThreadUtil4.sleep(20);
		mouseRelease(button);
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
	
	

	public static void typeRaw(String str, F0<Boolean> interrupted) {
		int keyRate = DEFAULT_KEY_RATE;
		for (int i = 0; i < str.length(); i++) {
			char targetChar = str.charAt(i);
			
			if (interrupted != null && interrupted.e()) return;
			
			char upperCase = Character.toUpperCase(targetChar);
			if (targetChar != upperCase) {
				type(upperCase, keyRate);
			} else if (targetChar != Character.toLowerCase(targetChar)) {
				combo(KeyEvent.VK_SHIFT, targetChar, keyRate);
			} else if (targetChar >= '0' && targetChar<='9') {
				type(targetChar, keyRate);
			} else {
				Integer unshiftKey = unshiftKey(targetChar);
				if (unshiftKey==null) {
					type(targetChar, keyRate);
				} else {
					combo(KeyEvent.VK_SHIFT, unshiftKey, keyRate);
				}
			}
		}
	}
	
	public static void type(int c, int sleepTime) {
		try {
			if (c == '\'') {
				c = KeyEvent.VK_QUOTE;
			}
			
			ROBOT.keyPress(c);
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
			}
			ROBOT.keyRelease(c);
		} catch (RuntimeException e) {
			System.out.println((char)c + ", " + c);
			throw e;
		}
	}
	public static void combo(int specialKey, int key, int sleepTime) {
		ROBOT.keyPress(specialKey);
		type(key, sleepTime);
		ROBOT.keyRelease(specialKey);
	}
	

	
	/**
	 * If the key is :, return ;
	 * @return
	 */
	public static Integer unshiftKey(char c) {

		switch (c) {
		case '~': return Integer.valueOf('`');
		case '!': return Integer.valueOf('1');
		case '@': return Integer.valueOf('2');
		case '#': return Integer.valueOf('3');
		case '$': return Integer.valueOf('4');
		case '%': return Integer.valueOf('5');
		case '^': return Integer.valueOf('6');
		case '&': return Integer.valueOf('7');
		case '*': return Integer.valueOf('8');
		case '(': return Integer.valueOf('9');
		case ')': return Integer.valueOf('0');
		case '_': return Integer.valueOf('-');
		case '+': return Integer.valueOf('=');
		case '|': return Integer.valueOf('\\');
		case '{': return Integer.valueOf('[');
		case '}': return Integer.valueOf(']');
		case ':': return Integer.valueOf(';');
		case '"': return KeyEvent.VK_QUOTE;
		case '<': return Integer.valueOf(',');
		case '>': return Integer.valueOf('.');
		case '?': return Integer.valueOf('/');
		
		default: return null;
		}
	}
}
