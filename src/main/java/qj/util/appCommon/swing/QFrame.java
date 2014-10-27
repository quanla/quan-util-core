package qj.util.appCommon.swing;

import qj.util.SystemUtil;
import qj.util.funct.P0;

import javax.swing.*;
import java.awt.*;

public class QFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	static {
		setLookAndFeel();
	}

	public static void setLookAndFeel() {
		try {
            if (SystemUtil.isWindows()) {
				JFrame.setDefaultLookAndFeelDecorated(true);
				JDialog.setDefaultLookAndFeelDecorated(true);
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				
            } else {
            	// To fix GTK Theme "pixmap" error messages:
//            	sudo apt-get install gtk2-engines-pixbuf
            	
				JFrame.setDefaultLookAndFeelDecorated(true);
				JDialog.setDefaultLookAndFeelDecorated(true);
//                UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				
				
//			    //
//			    // sets the default font for all Swing components.
//			    // ex. 
//			    //  setUIFont (new javax.swing.plaf.FontUIResource
//			    //   ("Serif",Font.ITALIC,12));
//			    //
//				java.util.Enumeration keys = UIManager.getDefaults().keys();
//				while (keys.hasMoreElements()) {
//					Object key = keys.nextElement();
//					Object value = UIManager.get(key);
//					if (value instanceof javax.swing.plaf.FontUIResource) {
//						// new Font("Arial")
////						Font f = new Font;
//						UIManager.put(key, ((Font)value).deriveFont(7f));
//					}
//				}
                
            }
        } catch (Exception ex) {
			System.out.println("Failed loading L&F: ");
			System.out.println(ex);
		}
	}

    public QFrame() throws HeadlessException {
    }

    public QFrame(String title) throws HeadlessException {
        super(title);
    }

    public static void repack(Window win) {
        win.setMinimumSize(null);
        win.pack();
        win.setMinimumSize(win.getSize());
    }
}
