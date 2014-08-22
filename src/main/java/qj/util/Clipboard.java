package qj.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import qj.util.funct.P0;
import qj.util.funct.P1;

public class Clipboard extends Clipboard4 {
//	public static void onChange(final F1<String, String> p) {
//		ClipboardObserver.getInstance().addListener(new ClipboardTextChangeListener() {
//			public String clipboardUpdated(String str) {
//				return p.e(str);
//			}
//		});
//	}
	public static void onChangeString(final P1<String> p) {
		listeners.add(new P1<Object>() {
			public void e(Object obj) {
				if (obj instanceof String) {
					p.e((String) obj);
				} else {
					p.e(null);
				}
			}
		});
	}
	public static void onChangeImage(final P1<Image> p) {
		listeners.add(new P1<Object>() {
			public void e(Object obj) {
				if (obj instanceof Image) {
					p.e((Image) obj);
				} else {
					p.e(null);
				}
			}
		});
	}
	
	static Object oldClipboardObj = null;
	
	static {
		final boolean[] firstRun = {true};
		ThreadUtil.run(new P0() {public void e() {
			java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			
			for (;;) {
				Transferable contents = clipboard.getContents(null);
				Object clipboardObj = getData(contents);
				
				if (firstRun[0]) {
					firstRun[0] = false;
				} else {
					if (!ObjectUtil.equals(oldClipboardObj, clipboardObj)) {
						for (P1<Object> p1 : listeners) {
							p1.e(clipboardObj);
						}
						oldClipboardObj = clipboardObj;
					}
				}
				
				ThreadUtil.sleep(300);
			}
		}});
	}
	
	public static Object getData() {
		return oldClipboardObj;
	}
	
	static ArrayList<P1<Object>> listeners = new ArrayList<P1<Object>>();
	public static void onChange(P1<Object> p1) {
		listeners.add(p1);
	}
	
	public static Object getData(Transferable contents) {
		Object clipboardObj = null;
		try {
			clipboardObj = (String) contents.getTransferData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException e) {
			try {
				clipboardObj = contents.getTransferData(DataFlavor.javaFileListFlavor);
			} catch (UnsupportedFlavorException e1) {
				try {
					clipboardObj = contents.getTransferData(DataFlavor.imageFlavor);
				} catch (UnsupportedFlavorException e2) {
					throw new RuntimeException(e2);
				} catch (IOException e2) {
					throw new RuntimeException(e2);
				}
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return clipboardObj;
	}
}
