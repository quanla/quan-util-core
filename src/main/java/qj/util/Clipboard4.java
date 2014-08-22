package qj.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import qj.ui.DesktopUI4;

public class Clipboard4 {
	/**
	 * 
	 * @param str
	 */
	public static void copy(String str) {
		java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		ThreadUtil4.sleep(20);
		clipboard.setContents(new StringSelection(str), null);
		ThreadUtil4.sleep(100);
	}
	
	public static void copy(final Image im) {
		java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		clipboard.setContents(new Transferable() {

			public Object getTransferData(DataFlavor flavor)
					throws UnsupportedFlavorException, IOException {
				return im;
			}

			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] {DataFlavor.imageFlavor};
			}

			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return true;
			}
			
		}, null);
	}

	public static BufferedImage getImage() {
		java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		try {
			Transferable contents = clipboard.getContents(null);
//			ClipboardTransferable content = (ClipboardTransferable)clipboard.getContents(null);
			return (BufferedImage) contents.getTransferData(DataFlavor.imageFlavor);
		} catch (Exception e) {
			return null;
		}
	}

	public static String getText() {
		java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		try {
			Transferable contents = clipboard.getContents(null);
			return (String) contents.getTransferData(DataFlavor.stringFlavor);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void main(String[] args) {
//		DesktopUI.alertS(getText());
		getFiles();
	}

	public static List getFiles() {
		java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		try {
			Transferable contents = clipboard.getContents(null);
//			ClipboardTransferable content = (ClipboardTransferable)clipboard.getContents(null);
			return ((List)contents.getTransferData(DataFlavor.javaFileListFlavor));
		} catch (Exception e) {
			return null;
		}
	}

	public static void paste() {
		DesktopUI4.ROBOT.keyPress(SystemUtil.isMac() ? KeyEvent.VK_META : KeyEvent.VK_CONTROL);
		DesktopUI4.ROBOT.keyPress(KeyEvent.VK_V);
		ThreadUtil4.sleep(30);
		DesktopUI4.ROBOT.keyRelease(KeyEvent.VK_V);
		DesktopUI4.ROBOT.keyRelease(SystemUtil.isMac() ? KeyEvent.VK_META : KeyEvent.VK_CONTROL);
		ThreadUtil4.sleep(60);
	}

	public static void type(String str) {
		String tmp = getText();
		copy(str);
		paste();
		if (tmp!=null) {
			copy(tmp);
		}
	}

	public static String copy() {
		DesktopUI4.ROBOT.keyPress(SystemUtil.isMac() ? KeyEvent.VK_META : KeyEvent.VK_CONTROL);
		DesktopUI4.ROBOT.keyPress(KeyEvent.VK_C);
		ThreadUtil4.sleep(30);
		DesktopUI4.ROBOT.keyRelease(KeyEvent.VK_C);
		DesktopUI4.ROBOT.keyRelease(SystemUtil.isMac() ? KeyEvent.VK_META : KeyEvent.VK_CONTROL);
		return getText();
	}

	public static void clear() {
		copy("");
	}
}
