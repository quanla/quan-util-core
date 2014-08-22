package qj.util.appCommon.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import qj.util.ArrayUtil;
import qj.util.Cols;
import qj.util.LangUtil;
import qj.util.PrefUtil;
import qj.util.SwingUtil;
import qj.util.funct.Fs;
import qj.util.funct.P0;

/**
 * Border layout
 * @author QuanLA
 *
 */
@SuppressWarnings("rawtypes")
public class AppFrame extends StatusFrame implements ActionListener, WindowListener {
	public static final String DEFAULT_HEIGHT = "defaultHeight";
	public static final String DEFAULT_WIDTH = "defaultWidth";
	/**
	 * 
	 */
	private static final long serialVersionUID = 361115321221350436L;
	protected final JMenuItem MI_EXIT = new JMenuItem("Exit",
			KeyEvent.VK_X);
	protected final JMenuItem MI_OPEN = new JMenuItem("Open",
			KeyEvent.VK_O);
	protected final JMenuItem MI_SAVE = new JMenuItem("Save",
			KeyEvent.VK_S);
	protected final JMenuItem MI_NEW = new JMenuItem("New",
			KeyEvent.VK_N);

    P0 onClosed = Fs.p0();
    P0 onMoved = Fs.p0();
	private Class caller;
	private String initTitle;
	int defaultWidth = 400;
	int defaultHeight = 400;

	public AppFrame() {
		this(null, false, LangUtil.getTraceClass(1));
	}

	/**
	 * "title", "Tit",
	 * "hideOnClose", false,
	 * "defaultWidth", 700,
	 * "defaultHeight", 700
	 * @param props
	 */
	public AppFrame(Object... props) {
//		this(null, false, LangUtil.getTraceClass(1));
		Map map = Cols.toMap(props);

		Boolean hideOnClose = (Boolean)map.get("hideOnClose");
		init((String)map.get("title"), hideOnClose == null ? false : hideOnClose, LangUtil.getTraceClass(1));
		defaultWidth 	= getInt(DEFAULT_WIDTH, map);
		defaultHeight 	= getInt(DEFAULT_HEIGHT, map);
	}

	public int getInt(String key, Map map) {
		Integer obj = (Integer) map.get(key);
		return obj==null ? 0 : obj;
	}

	public AppFrame(String title) {
		init(title, false, LangUtil.getTraceClass(1));
	}
	public AppFrame(String title, boolean hideOnClose) {
		init(title, hideOnClose, LangUtil.getTraceClass(1));
	}

	protected void init(String title, boolean hideOnClose, Class caller) {
		this.setTitle(title);
		this.initTitle = title;
		this.caller = caller;
		
		if (hideOnClose) {
			this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		} else {
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);// Safer to use in other app
		}
		MI_OPEN.addActionListener(this);
		MI_OPEN.setAccelerator(KeyStroke.getKeyStroke("control O"));
		MI_SAVE.addActionListener(this);
		MI_SAVE.setAccelerator(KeyStroke.getKeyStroke("control S"));
		MI_NEW.addActionListener(this);
		MI_NEW.setAccelerator(KeyStroke.getKeyStroke("control N"));
		MI_EXIT.addActionListener(this);
		MI_EXIT.setAccelerator(KeyStroke.getKeyStroke("control W"));
		this.addWindowListener(this);
		this.addComponentListener(new ComponentAdapter() {public void componentMoved(ComponentEvent e) {
            onMoved.e();
        }});

	}
	
	@Override
	public void setVisible(boolean b) {

		Dimension d = PrefUtil.getObject1("frameSize." + initTitle, caller, null);
		if (d != null) {
			setSize(d);
        } else {
			setSize(defaultWidth,defaultHeight);
        }
		
		Point p = PrefUtil.getObject1("frameLocation." + initTitle, caller, null);
		if (p != null) {
			setLocation(p);
		} else {
			SwingUtil.centerlize(this);
		}
		
		if (PrefUtil.getBoolean("frameMaximized." + initTitle, caller)) {
			setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		}
		
		super.setVisible(b);
	}

	protected void onLoad() {
	}

	protected void onUnload() {
	}

	public void onClosed(P0 onClosed) {
        this.onClosed = Fs.sequel(this.onClosed, onClosed);
	}

	public final void windowActivated(WindowEvent arg0) {
	}

	public final void windowClosed(WindowEvent arg0) {
	}

    public void onMoved(P0 onMoved) {
        this.onMoved = Fs.sequel(this.onMoved, onMoved);
    }

	public final void windowClosing(WindowEvent we) {

//		System.out.println(caller);
		PrefUtil.setObject("frameLocation." + initTitle, this.getLocation(), caller);

		boolean maximized = isMaximized();
		PrefUtil.setBoolean("frameMaximized." + initTitle, maximized, caller);
		if (!maximized) {
			PrefUtil.setObject("frameSize." + initTitle, this.getSize(), caller);
		}

		onUnload();
		
		onClosed.e();
		
//		ThreadUtil.attempt(1000, new F0<Boolean>() {public Boolean e() {
//			return true;
//		}});
	}

	public boolean isMaximized() {
		return (this.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0;
	}

	public final void windowDeactivated(WindowEvent arg0) {
	}

	public final void windowDeiconified(WindowEvent arg0) {
	}

	public final void windowIconified(WindowEvent arg0) {
	}

	public final void windowOpened(WindowEvent arg0) {
		onLoad();
	}

	public static final int MENU_FILE = 0;
	public static final int MENU_EDIT = 1;
	public static final int MENU_VIEW = 2;
	public static final int MENU_TOOL = 3;
	public static final int MENU_HELP = 4;
	private final HashMap<String, JMenu> menus = new HashMap<String, JMenu>();

	public JMenu getMenu(String name) {
		JMenu menu = menus.get(name);
		if (menu != null)
			return menu;

		menu = new JMenu(name);
		JMenuBar menuBar2 = this.getJMenuBar();
		if (menuBar2 == null) {
			menuBar2 = new JMenuBar();
			this.setJMenuBar(menuBar2);
		}

		menus.put(name, menu);
		menuBar2.add(menu);
		return menu;
	}
	
	public JMenu getMenu(String name, char mnemonic) {
		JMenu menu = getMenu(name);
		menu.setMnemonic(mnemonic);
		return menu;
	}

	public JMenu getMenu(int intMenu) {
		JMenu menu;
		switch (intMenu) {
		case MENU_FILE:
			menu = getMenu("File");
			menu.setMnemonic('F');
			return menu;
		case MENU_EDIT:
			menu = getMenu("Edit");
			menu.setMnemonic('E');
			return menu;
		case MENU_VIEW:
			menu = getMenu("View");
			menu.setMnemonic('V');
			return menu;
		case MENU_TOOL:
			menu = getMenu("Tool");
			menu.setMnemonic('T');
			return menu;
		case MENU_HELP:
			menu = getMenu("Help");
			menu.setMnemonic('H');
			return menu;
		default:
			throw new AssertionError();
		}
	}

	public final void actionPerformed(ActionEvent e) {
		if (e.getSource() == MI_EXIT) {
			this.windowClosing(null);

			this.dispose();
			System.exit(0);
		} else if (e.getSource() == MI_OPEN) {
			onOpen();
		} else if (e.getSource() == MI_SAVE) {
			onSave();
		} else
			onActionPerformed(e);
	}

	protected void onActionPerformed(ActionEvent e) {
	}

	/**
	 * When user click on open menu item
	 * 
	 */
	protected void onOpen() {
	}

	/**
	 * When user click on save menu item
	 * 
	 */
	protected void onSave() {
	}
}