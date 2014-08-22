package qj.util;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.TextUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.*;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.undo.UndoManager;

import qj.ui.DesktopUI4;
import qj.util.bean.GetSet;
import qj.util.funct.*;
import qj.util.math.Range;
import qj.util.swing.FileDropHandler;
import qj.util.swing.MoveMouseListener;
import qj.util.swing.QTransferHandler;

public class SwingUtil extends SwingUtil4 {

	public static Color ORANGE2 = new Color(240, 216, 168);
	
	public static void showLoadingIndicator(final Window window) {
		P1<Component> glassPaneSetter = glassPaneSetter(window);
		
		LoadingIndicator loadingIndicator = new LoadingIndicator();
		
		glassPaneSetter.e(loadingIndicator);
		loadingIndicator.repaint();
	}
	
	public static class LoadingIndicator extends JComponent {
		long start = System.currentTimeMillis();
		
		public void paint(Graphics g) {
			System.out.println(getSize());
		}
	}
	
	public static P1<Component> glassPaneSetter(final Window window) {
		return new P1<Component>() {public void e(Component glassPane) {
			if (window instanceof JDialog) {
				((JDialog)window).setGlassPane(glassPane);
			} else if (window instanceof JFrame) {
				((JFrame)window).setGlassPane(glassPane);
			} else {
				throw new RuntimeException("Unsupported");
			}
		}};
	}
	public static void hideLoadingIndicator(Window window) {
		
	}
	
	public static P1<P0> asyncF = new P1<P0>() {public void e(P0 obj) {
		SwingUtilities.invokeLater(Fs.runnable(obj));
	}};
	
    public static void onChange(final JTextComponent tf, final P0 p0) {
        if (p0 != null) {
			onChange(tf, Fs.<String,Boolean>f1(p0, true));
		}
    }
	
    public static void onChange(final JTextComponent tf, final P1<String> p1) {
        if (p1 != null) {
			onChange(tf, Fs.f1(p1, true));
		}
    }

	public static F1<JTextComponent, String> getTextF = new F1<JTextComponent, String>() {public String e(JTextComponent tf) {
		return tf.getText();
	}};
	
    public static void onChange(final JTextComponent tf, final F1<String, Boolean> p1) {
    	onChange(tf, Fs.f0(getTextF, tf), addAL(tf), p1);
    }
    
    public static P1<ActionListener> addAL(final Component comp) {
    	if (comp instanceof JTextField) {
    		return new P1<ActionListener>() {public void e(ActionListener obj) {
				((JTextField)comp).addActionListener(obj);
			}};
    	} else if (comp instanceof JComboBox) {
    		return new P1<ActionListener>() {public void e(ActionListener obj) {
				((JComboBox)comp).addActionListener(obj);
			}};
    	} else {
    		return null;
    	}
    }

    public static void onChange(final JComponent tf, final F0<String> getTextF, P1<ActionListener> addAL, final F1<String, Boolean> p1) {
        final String[] lastVal = {getTextF.e()};
        if (addAL != null) {
			addAL.e(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String text = getTextF.e();
					if (!ObjectUtil.equals(lastVal[0],text)) {
						p1.e(text);
						lastVal[0] = text;
					}
				}
			});
		}
		tf.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
            }

            public void focusLost(FocusEvent e) {
                String text = getTextF.e();
                if (!ObjectUtil.equals(lastVal[0], text)) {
                    if (p1.e(text)) {
                        lastVal[0] = text;
                    } else {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                tf.requestFocus();
                                if (tf instanceof JTextField) {
									((JTextField)tf).selectAll();
								}
                            }
                        });
                    }
                }
            }
        });
    }

    public static JButton btn(String title, P0 action) {
        return btn(title, null, action);
    }

	public static JButton btn(String title, Color color, P0 action) {
		JButton button = new JButton(title);
		if (color != null) {
			button.setForeground(color);
		}
		if (action != null) {
			button.addActionListener(SwingUtil.toActionListener(action));
		} else {
			button.setEnabled(false);
		}
		
		button.setPreferredSize(new Dimension(80, 25));
		
		
		return button;
	}

	public static JButton btn(String title, final F0<P0> actionF) {
		return btn(title, null, actionF);
	}

	public static JButton btn(final String title, Color color, final F0<P0> actionF) {
		final JButton button = new JButton("Please wait...");
		
		if (color != null) {
			button.setForeground(color);
		}
		button.setEnabled(false);
		
		ThreadUtil.run(new Runnable() {public void run() {
			P0 action = actionF.e();
			button.setText(title);
			if (action != null) {
				button.setEnabled(true);
				button.addActionListener(SwingUtil.toActionListener(action));
			}
		}});
		
		return button;
	}
	
    public static JButton btn(String title, char mne, P0 action) {
        return btn(title, mne, null, action);
    }

	public static JButton btn(String title, char mne, String tooltip, P0 action) {
		JButton button = new JButton(title);
        button.addActionListener(SwingUtil.toActionListener(action));
        button.setMnemonic(mne);
		button.setToolTipText(tooltip);
		return button;
	}

    public static void onMouseRelease(Component component, final P0 action) {
        component.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    action.e();
                }
            }
        });
    }
    public static void onMousePressed(Component component, final P0 action) {
    	onMousePressed(component, Fs.<Point>p1(action));
    }
    public static void onMousePressed(Component component, final P1<Point> action) {
        component.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    action.e(e.getPoint());
                }
            }
        });
    }

    public static void onMouseDragged(Component component, final P1<Point> action) {
        component.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				action.e(e.getPoint());
			}
        });
    }

    public static void setSelection(Range range, JTextComponent tc) {
        tc.setSelectionStart(range.getFrom() + 1);
        tc.setSelectionEnd(range.getTo() + 1);
    }

    public static Range getSelection(JEditorPane tf) {
        return new Range(tf.getSelectionStart() - 1, tf.getSelectionEnd() - 1);
    }

    public static void asyncRun(P0 action) {
        SwingUtilities.invokeLater(Fs.runnable(action));
    }

    public static void add(String title, final P0 onChoose, final JList list, final Object... commands) {
    	
        final DefaultListModel listModel = (DefaultListModel) list.getModel();
        final AtomicInteger index = new AtomicInteger(listModel.getSize());

        listModel.addElement(title);
        final ListSelectionListener selectionListener = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && index.get() == list.getSelectedIndex()) {
                    onChoose.e();
                }
            }
        };
        list.addListSelectionListener(selectionListener);

        listModel.addListDataListener(new ListDataListener() {
            public void intervalAdded(ListDataEvent e) {
                if (e.getIndex1() < index.get()) {
                    index.getAndAdd(e.getIndex1() - e.getIndex0());
                }
            }

            public void intervalRemoved(ListDataEvent e) {
                if (e.getIndex1() < index.get()) {
                    index.getAndAdd(e.getIndex0() - e.getIndex1());
                } else if (e.getIndex0() <= index.get()) {
                    list.removeListSelectionListener(selectionListener);
                    final ListDataListener me = this;
                    asyncRun(new P0() {
                        public void e() {
                            listModel.removeListDataListener(me);
                        }
                    });
                }
            }

            public void contentsChanged(ListDataEvent e) {
            }
        });
        

    	Map<String, P0> commandMap = Cols.<String,P0>map(commands);
    	if (commandMap==null || !commandMap.isEmpty()) {
			onPopup(list, new F0<JPopupMenu>() {public JPopupMenu e() {
                if (index.get() == list.getSelectedIndex()) {
                    JPopupMenu menu = new JPopupMenu();
            		for (int i = 0; i < commands.length; i+=2) {
        				String 	name 	= (String) commands[i];
        				P0 		action 	= (P0) commands[i+1];
        				menu.add(menuItem(name, action));
        			}
					return menu;
                }
                return null;
			}});
    	}
    }

    public static GridBagConstraints defaultConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.ipadx = 3;
        constraints.ipady = 2;
        constraints.weightx = 1;
//        constraints.weighty = 1;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.BOTH;
        return constraints;
    }

    public static void onRemove(final JList list, final P1<Integer> p1) {
    	onKeyDown(list, KeyEvent.VK_DELETE, new P0() {public void e() {
			int index = list.getSelectedIndex();
            p1.e(index);
            ((DefaultListModel)list.getModel()).removeElementAt(index);
		}});
    }

    public static P0 updateF(final F0<String> elementF, final DefaultListModel listModel, final int index) {
        return new P0() {
            public void e() {
                listModel.setElementAt(elementF.e(), index);
            }
        };
    }

    public static JCheckBox checkbox(boolean checked, final P1<Boolean> onChange) {
        final JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(checked);
        onChange(checkBox, onChange);
		return checkBox;
    }
	public static void onChange(final JCheckBox checkBox,
			final P1<Boolean> onChange) {
		if (onChange != null) {
			checkBox.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					onChange.e(checkBox.isSelected());
				}
			});
		}
	}

	public static JCheckBox checkboxPref(String key, boolean def) {
        final Class<?> callerClass = LangUtil.getTraceClass(1);
        return checkbox(PrefUtil.getBoolean(key, def, callerClass), 
				PrefUtil.setBooleanF(key, callerClass));
	}
    

	public static class RowAdder {
		private final Container container;
		
        public final GridBagConstraints constraints = SwingUtil.defaultConstraints();

		private int rowSpace;

		private int padding;
        {
            constraints.gridx = -1;
            constraints.gridy = -1;
        }
        
        public RowAdder(Container container) {
			this.container = container;
		}

		public void whole(Component component) {
			if (component instanceof JTextArea) {
				component = getScrollPane((JTextArea)component);
			} else if (component instanceof JEditorPane) {
				component = scrollPane((JEditorPane)component);
			}
            cell(component, 0);
        }

		public P0 wholeF(final Component component) {
			return new P0() {public void e() {
				whole(component);
			}};
        }

        public void remove() {
            container.remove(container.getComponentCount() - 1);
            constraints.gridx --;
        }

        public int getComponentCount() {
            return container.getComponentCount();
        }

        public void row(String title, final JTextArea ta) {
        	row(title, getScrollPane(ta));
        }

        public void row(String title, final Component comp) {
            JLabel label = new JLabel(title);
            cell(label, 1);
            label.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    asyncRun(new P0() {
                        public void e() {
                            comp.requestFocus();
                        }
                    });
                }

                public void mousePressed(MouseEvent e) {
                }

                public void mouseReleased(MouseEvent e) {
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                }
            });
            cell(comp, 2);
        }
        public void cell(Component comp, Integer index) {
            if (constraints.gridy == -1) {
                constraints.gridy = 0;
            }

            if (index == 1) {
                constraints.weightx = 0.1;
            } else {
                constraints.weightx = 1;
            }

            if (index > 0) {
                if (constraints.gridx >= index - 1) {
                    // Blocked
                    constraints.gridy++;
                }
                constraints.gridx = index - 1;
                container.add(comp, constraints);
            } else {
                constraints.gridy++;
                constraints.gridwidth = 2;
                constraints.gridx = 0;
                container.add(comp, constraints);
                constraints.gridx = 1; 
                constraints.gridwidth = 1;
            }
            
            if (container instanceof JPanel) {
                ((JPanel)container).updateUI();
            }
        }

		public GridBagConstraints getConstraints() {
			return constraints;
		}

		public void setWeightY(int y) {
			constraints.weighty = y;
		}

		public void blankRow(int size) {
			JPanel panel = new JPanel();
			panel.setPreferredSize(new Dimension(0, size));
			whole(panel);
		}

		public void setPadding(int padding) {
        	constraints.insets.top = padding;
            constraints.insets.bottom = padding;
            constraints.insets.left = padding;
            constraints.insets.right = padding;
		}
    }

    public static RowAdder rowAdder(final Container container) {
        container.setLayout(new GridBagLayout());
        return new RowAdder(container);
    }

	public static JComboBox combobox(String value, List<String> vals, F1<String, Boolean> onChange) {
		JComboBox cb = new JComboBox();
		cb.setMinimumSize(new Dimension(100, 10));
//		cb.setEditable(true);
		for (String val : vals) {
			cb.addItem(val);
		}
		if (value != null) {
			cb.setSelectedItem(value);
		}
		if (onChange != null) {
			SwingUtil.onChange(cb, onChange);
		}
		return cb;
	}
	
	/**
	 * params: Label, P0...
	 * @param string
	 * @param params
	 * @return
	 */
	public static JComboBox combobox(String placeHolder, Object... params) {
		JComboBox cb = new JComboBox();
		
		cb.addItem(placeHolder);
		for (int i = 0; i < params.length; i+=2) {
			cb.addItem(params[i]);
			
		}
		final Map<String, P0> map = Cols.map(params);
		SwingUtil.onChange(cb, new F1<String,Boolean>() {public Boolean e(String label) {
			map.get(label).e();
			return true;
		}});
		return cb;
	}

	public static void onChange(final JComboBox cb, F1<String, Boolean> onChange) {
		onChange(cb, new F0<String>() {public String e() {
			return (String) cb.getSelectedItem();
		}}, new P1<ActionListener>() {public void e(ActionListener obj) {
			cb.addActionListener(obj);
		}}, onChange);
	}
    public static JMenuItem menuItem(String title, char mnemonic, P0 p0) {
    	
    	String tooltip = null;
    	
    	JMenuItem menuItem = menuItem(title, null, p0);
    	menuItem.setMnemonic(mnemonic);
		menuItem.setToolTipText(tooltip);
		return menuItem;
    }
    public static JMenuItem menuItem(String title, P0 p0) {
		return menuItem(title, null, p0);
    }

    public static JMenuItem menuItem(String title, String accelerator, P0 p0) {
        JMenuItem ret = new JMenuItem(title);
        if (p0!=null) {
        	ret.addActionListener(SwingUtil.toActionListener(p0));
        } else {
        	ret.setEnabled(false);
        }
        if (accelerator != null) {
			ret.setAccelerator(KeyStroke.getKeyStroke(accelerator));
		}
		return ret;
    }
    public static MenuItem menuItemAwt(String title, P0 p0) {
    	MenuItem ret = new MenuItem(title);
    	if (p0!=null) {
    		ret.addActionListener(SwingUtil.toActionListener(p0));
    	} else {
    		ret.setEnabled(false);
    	}
//    	if (accelerator != null) {
//    		ret.setShortcut(new MenuShortcut .getKeyStroke(accelerator));
//    	}
    	return ret;
    }
    public static JCheckBoxMenuItem menuItemCB(String title, String accelerator, final P1<Boolean> onChange) {
    	final JCheckBoxMenuItem ret = new JCheckBoxMenuItem(title);
        ret.addActionListener(SwingUtil.toActionListener(new P0() {public void e() {
        	onChange.e(ret.isSelected());
		}}));
        if (accelerator != null) {
			ret.setAccelerator(KeyStroke.getKeyStroke(accelerator));
		}
		return ret;
    }

//    public static JTextField tfPref(String key, int cols) {
//    	String def = "";
//		return tfPref(key, cols, def);
//    }

	public static JTextField tfPref(String key, int cols, String def) {
        final Class<?> callerClass = LangUtil.getTraceClass(1);
		return tf(PrefUtil.getString(key, def, callerClass), 
    			cols, 
				PrefUtil.setF(key, callerClass));
	}
	
//    public static JTextArea taPref(String key, int cols) {
//    	String def = "";
//		return taPref(key, cols, def);
//    }

	public static JTextArea taPref(String key, int cols, String def) {
		int rows = 10;
        return taPref(key, cols, rows, def);
	}

	public static JTextArea taPref(String key, int cols, int rows, String def) {
		final Class<?> callerClass = LangUtil.getTraceClass(1);
		return ta(PrefUtil.getString(key, def, callerClass), 
				cols, rows,
				PrefUtil.setF(key, callerClass));
	}
	public static JTextArea ta(String value, int cols, P1<String> onChange) {
		int rows = 10;
		return ta(value, cols, rows, onChange);
	}

	public static JTextArea ta(String value, int cols, int rows,
			P1<String> onChange) {
		JTextArea ta = new JTextArea(value, rows, cols);
		if (onChange != null) {
			onChange(ta, onChange);
		}
		return ta;
	}

	public static JTextField tf(String value, int cols) {
		return tf(value, cols, null);
	}
	public static JTextField tf(String value, int cols, P1<String> onChange) {
		JTextField tf = new JTextField(value, cols);
		if (onChange != null) {
			onChange(tf, onChange);
		}
		supportUndo(tf);
		return tf;
	}
	
	public static void onKeyDown(Component comp, final P1<Integer> p1) {
		onKeyDown1(comp, new P1<KeyEvent>() {public void e(KeyEvent e) {
			p1.e(e.getKeyCode());
		}});
	}

	public static void onKeyDown1(Component comp, final P1<KeyEvent> p1) {
		comp.addKeyListener(new KeyAdapter() {
			
//			@Override
//			public void keyTyped(KeyEvent e) {
////				System.out.println(1);
//			}
//
//			@Override
//			public void keyReleased(KeyEvent e) {
////				System.out.println(21);
//			}

			public void keyPressed(KeyEvent e) {
				p1.e(e);
			}
		});
	}


	public static void onKeyDown(Component comp, final int key, final P0 p0) {
		comp.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
                if (key == e.getKeyCode()) {
                	p0.e();
                	e.consume();
                }
			}
		});
	}

	public static void onKeyUp(Component comp, final int key, final P0 p0) {
		comp.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
                if (key == e.getKeyCode()) {
                	p0.e();
                	e.consume();
                }
			}
		});
	}
	
	public static void onKeyUp(Component comp, final P0 p0) {
		comp.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
                if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED
                		&& !e.isAltDown()
                		&& !e.isControlDown()
//                		&& !e.isShiftDown()
                		) {
                	p0.e();
                }
			}
		});
	}
	public static void onAnyKeyDown(Component comp, final P0 p0) {
		comp.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
            	p0.e();
			}
		});
	}
	public static void onAnyKeyUp(Component comp, final P0 p0) {
		comp.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
            	p0.e();
			}
		});
	}
	public static void onTextEdit(Component comp, final P0 p0) {
		onKeyUp(comp, new P1<KeyEvent>() {public void e(KeyEvent e) {
			
			if (isNonEditKey(e.getKeyCode())) {
				return;
			}
			
			if (e.isAltDown() || e.isConsumed()) {
				return;
			}
			if ( e.isControlDown()) {
				if (
						e.getKeyCode() != KeyEvent.VK_X
						&& e.getKeyCode() != KeyEvent.VK_Z
						&& e.getKeyCode() != KeyEvent.VK_Y
						&& e.getKeyCode() != KeyEvent.VK_V
						) {
					return;
				}
			}
//				System.out.println(1);
        	p0.e();
		}});
	}

	public static void onKeyUp(Component comp, final P1<KeyEvent> p1) {
		comp.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				p1.e(e);
			}
		});
	}

	public static void onDoubleClick(Component comp, final P0 p0) {
		comp.addMouseListener(new MouseListener() {
			
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					p0.e();
				}
			}
			
			public void mouseReleased(MouseEvent e) {
			}
			
			public void mousePressed(MouseEvent e) {
			}
			
			public void mouseExited(MouseEvent e) {
			}
			
			public void mouseEntered(MouseEvent e) {
			}
		});
	}

	public static void show(Window frame) {
//		if (frame instanceof JFrame) {
////			((JFrame)frame).getContentPane().pa
//		} else {
		if (frame.getPreferredSize() == null) {
			frame.pack();
		} else {
			frame.setSize(frame.getPreferredSize());
		}
		Point location = frame.getLocation();
		if (location==null || (location.x==0 && location.y ==0)) {
			SwingUtil.centerlize(frame);
		}
//		}
		frame.setVisible(true);
	}

	public static OutputStream out(final JTextArea ta) {
		return new OutputStream() {
			
			public void write(int b) throws IOException {
				ta.append(String.valueOf((char) b));
			}
		};
		
	}
	public static PrintStream printStream(final JTextArea ta) {
		return new PrintStream(out(ta)) {public void println(String line) {
			ta.append(line + "\n");
		}};
		
	}

	public static void readToTA(final InputStream in, final JTextArea ta) {
		final P1<String> writeF = writeF(ta);
		
		IOUtil.readInputStreamToP1(in, writeF);
	}
	public static P1<String> writeF(final JTextArea ta) {
		return SwingUtil.async(new P1<String>() {public void e(final String text) {
		    boolean atEnd = trimTALength(ta);
			
			ta.append(text);
			if (atEnd) {
				ta.setCaretPosition(ta.getDocument().getLength());
			}
		}});
	}
	public static void onZoomIn(Component comp, final P0 p0) {
		onZoomIn(comp, Fs.<Point>p1(p0));
	}

	public static void onZoomIn(Component comp, final P1<Point> p1) {
		comp.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				if ((SystemUtil.isMac() ? e.isMetaDown() : e.isControlDown())
						&& !e.isAltDown() 
						&& !e.isShiftDown() 
						&& e.getWheelRotation() < 0 //(SystemUtil.isMac() ? e.getWheelRotation() > 0 : e.getWheelRotation() < 0)
						) {
					e.consume();
					p1.e(e.getPoint());
				}
			}
		});
	}
	public static void onZoomOut(Component comp, final P0 p0) {
		onZoomOut(comp, Fs.<Point>p1(p0));
	}

	public static void onZoomOut(Component comp, final P1<Point> p1) {
		comp.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				if ((SystemUtil.isMac() ? e.isMetaDown() : e.isControlDown())
						&& !e.isAltDown() 
						&& !e.isShiftDown()
						&& e.getWheelRotation() > 0 // (SystemUtil.isMac() ? e.getWheelRotation() < 0 : e.getWheelRotation() > 0)
						) {
					e.consume();
					p1.e(e.getPoint());
				}
			}
		});
	}

	public final static int CONSOLE_FONT_STYLE = Font.PLAIN;

	public static JScrollPane getScrollPane(final JTextArea ta) {
    	int rows = ta.getRows();
    	if (rows < 1) {
    		rows = StringUtil.countLine(ta.getText());
    	}
    	int columns = ta.getColumns();
    	if (columns < 1) {
    		String longestLine = StringUtil.longestLine(ta.getText());
			if (longestLine != null) {
				columns = longestLine.length();
			} else {
				columns = 10;
			}
    	}
		return getScrollPane(ta, columns * 8, rows * 20);
	}
	public static JScrollPane scrollPane(final JComponent comp) {
		return getScrollPane(comp, comp.getWidth(), comp.getHeight());
	}
	
	public static abstract class QScrollPane extends JScrollPane {
		public QScrollPane(Component view) {
			super(view);
		}

		public abstract void addLineNums();
	}

	private static JScrollPane getScrollPane(final JComponent comp, int width, int height) {
		final Font[] font = {
				new Font(SwingUtil.fontFixWidth(), CONSOLE_FONT_STYLE, 13)
		};

		JScrollPane scrollPane = new JScrollPane(comp);
		
		P0 zoomInF = new P0() {public void e() {
			font[0] = new Font(font[0].getFontName(), font[0].getStyle(), font[0].getSize() + 1);
			comp.setFont(font[0]);
		}};
		P0 zoomOutF = new P0() {public void e() {
			font[0] = new Font(font[0].getFontName(), font[0].getStyle(), font[0].getSize() - 1);
			comp.setFont(font[0]);
		}};
		
		onZoomIn(scrollPane, zoomInF);
		onZoomOut(scrollPane, zoomOutF);
		
		scrollPane.getVerticalScrollBar().setUnitIncrement(12);
		
		return scrollPane;
	}

	/**
	 * Center Up Down Left Right
	 * @param comp
	 * @return
	 */
	public static JPanel panel_border(final JComponent... comp) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		if (comp.length < 1) {return panel;}
		panel.add(comp[0], BorderLayout.CENTER);
		if (comp.length < 2) {return panel;}
		if (comp[1] != null) {
			panel.add(comp[1], BorderLayout.NORTH);
		}
		if (comp.length < 3) {return panel;}
		if (comp[2] != null) {
			panel.add(comp[2], BorderLayout.SOUTH);
		}
		if (comp.length < 4) {return panel;}
		if (comp[3] != null) {
			panel.add(comp[3], BorderLayout.WEST);
		}
		if (comp.length < 5) {return panel;}
		if (comp[4] != null) {
			panel.add(comp[4], BorderLayout.EAST);
		}
		return panel;
	}

	public static JTextComponent console(JTextComponent ta) {
		ta.setFont(new Font(
				SwingUtil.fontFixWidth()
				, CONSOLE_FONT_STYLE, SystemUtil.isWindows() ? 12 : 10));
		ta.setBackground(Color.black);
		ta.setForeground(Color.white);
		ta.setSelectionColor(Color.blue);
		ta.setCaretColor(null);
		ta.setEditable(false);
		return ta;
	}

	public static JButton browseButton(
			final P1<String> p1, 
			final F0<String> def) {
		return browseButton(p1, def, false, true);
	}
	public static JButton browseButton(
			final P1<String> p1, 
			final F0<String> def,
			final boolean dir, final boolean file) {
	    JButton btn = new JButton("Browser");
	    btn.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
	        showFileChooser(p1, def.e(), dir, file);
	    }});
	    return btn;
	}

	public static JPanel fileSelectorPanel(final JTextComponent tf) {
		return fileSelectorPanel(tf, false, true);
	}
	public static JPanel fileSelectorPanel(
			final JTextComponent tf,
			boolean selectFolder, boolean selectFile
			) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
	    panel.add(tf, BorderLayout.CENTER);
	    panel.add(browseButton(new P1<String>() {
			public void e(String obj) {
				tf.requestFocus();
				tf.setText(obj);
			}
		}, new F0<String>() {public String e() {
	        return tf.getText();
	    }},selectFolder, selectFile), BorderLayout.EAST);
		return panel;
	}

	/**
	 * Flow layout
	 * @param comps
	 * @return
	 */
	public static JPanel panel(Object... comps) {
		JPanel panel = new JPanel();
		for (Object component : comps) {
			Component comp = null;
			if (component instanceof String) {
				comp = new JLabel((String) component);
			} else {
				comp = (Component) component;
			}
			panel.add(comp);
		}
		return panel;
	}
	/**
	 * Flow layout
	 * @param comps
	 * @return
	 */
	public static JPanel panel(Component... comps) {
		JPanel panel = new JPanel();
		for (Component component : comps) {
			panel.add(component);
		}
		return panel;
	}
	/**
	 * Flow layout
	 * @param comps
	 * @return
	 */
	public static JPanel panel_left(Component... comps) {
		return panel_flow(FlowLayout.LEFT, comps);
	}
	public static JPanel panel_right(Component... comps) {
		return panel_flow(FlowLayout.RIGHT, comps);
	}
	public static JPanel panel_flow(int align, Component... comps) {
		JPanel panel = new JPanel();
		FlowLayout layout = (FlowLayout) panel.getLayout();
		layout.setAlignment(align);
		for (Component component : comps) {
			panel.add(component);
		}
		return panel;
	}

	public static CounterLabel counterLabel() {
		return new CounterLabel();
	}
    public static final class CounterLabel extends JLabel {
    	int value = 0;
    	
    	public CounterLabel() {
    		super("        ");
    	}
    	
		public void reset() {
			value = 0;
			setText("");
		}

		public void increase() {
			setText("  " + ++value);
		}
    	
	}

	public static F0<String> getTextF(JTextComponent tfFile) {
		return Fs.f0(getTextF, tfFile);
	}

	/**
	 * Border layout
	 * @param title
	 * @param frame
	 * @return
	 */
	public static JDialog dialog(String title, final Window frame) {
		F1<Window, P0> onEsc = disposeFF;
		
		return dialog(title, frame, onEsc);
	}
	
	public static JDialog dialog(Component comp, String title, Window window) {
		final JDialog dialog = dialog(title, window);
		dialog.setModal(false);
		RowAdder rowAdder = rowAdder(dialog);

		rowAdder.constraints.weighty = 1;
		
		rowAdder.whole(comp);
		
		return dialog;
	}
	
	public static JFrame frame(String string) {
		JFrame frame = new JFrame(string);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		return frame;
	}
	
	public static JDialog dialog(String title, final Window frame,
			F1<Window, P0> onEsc) {
		final JDialog dialog = dialogRaw(title, frame,ModalityType.APPLICATION_MODAL);
		
		// Setup escape
		final P0 disposeF = onEsc.e(dialog);
		
		onKeyDown_recursive(dialog, "ESCAPE", disposeF);
		return dialog;
	}

	public static JDialog dialogStrong(String title, final Window frame) {
		final JDialog dialog = dialogRaw(title, frame);
		
		onKeyDown_recursive(dialog, "control W", disposeFF.e(dialog));
		return dialog;
	}

	
	public static void onKeyDown_recursive(
			Container dialog,
			final String key,
			final P0 p0) {

		final P1<Component> p1 = new P1<Component>() {public void e(Component c) {
			onKeyDown(c, key, p0);
//			System.out.println(c);
		}};

		P1<Component> p2 = new P1<Component>() {public void e(Component c) {
			if (c instanceof Container) {
				Container cont = (Container) c;
				onComponentAdded(cont, new P1<Component>() {public void e(Component obj) {
					p1.e(obj);
					if (obj instanceof Container) {
						eachChild((Container) obj, this);
					}
				}});
				
			}
		}};
		
		// TODO change to each
		p1.e(dialog);
		eachChild(dialog, p1);
		p2.e(dialog);
		eachChild(dialog, p2);
	}

	public static void each(Component comp, P1<Component> p1) {
		p1.e(comp);
		if (comp instanceof Container) {
			eachChild((Container) comp, p1);
		}
	}
	
	private static void eachChild(Container cont, P1<Component> p1) {
		// Get the Container's array of children Components.
		Component[] children = cont.getComponents();

		// For every child repeat the above operation.
		for (int i = 0; i < children.length; i++) {
			Component component = children[i];
			p1.e(component);
			if (component instanceof Container) {
				eachChild((Container) component, p1);
			}
		}
	}
	
	public static Container getContentPane(Container dialog) {
		return dialog instanceof JFrame ?  ((JFrame)dialog).getContentPane() : ((JDialog)dialog).getContentPane();
	}

	public static JDialog dialogRaw(String title, final Window frame) {
		ModalityType modal = ModalityType.MODELESS;
		return dialogRaw(title, frame, modal);
	}
	private static JDialog dialogRaw(String title, final Window frame,
			ModalityType modal) {
		final JDialog dialog = new JDialog(frame, modal);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setTitle(title);
		return dialog;
	}
	
	private static void onComponentAdded(Container container, final P1<Component> f) {
		container.addContainerListener(new ContainerListener() {
			public void componentRemoved(ContainerEvent e) {}
			
			public void componentAdded(ContainerEvent e) {
				f.e(e.getChild());
			}
		});
	}
	
	public static void lineBorder(JComponent comp) {
		comp.setBorder(BorderFactory.createLineBorder(Color.BLUE));
	}

	public static void clearAllRows(JTable table) {
		DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
		int rowCount = tableModel.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			tableModel.removeRow(rowCount - i -1);
		}
	}

	public static F0<String> getTextF(final JComboBox cb) {
		return new F0<String>() {public String e() {
			return (String) cb.getSelectedItem();
		}};
	}

	public static P1<String> selectF(final JTextArea ta) {
		return new P1<String>() {public void e(String str) {
			String content = ta.getText();
			int index = content.indexOf(str);
			if (index > -1) {
				ta.setCaretPosition(index);
				ta.setSelectionStart(index);
				ta.setSelectionEnd(index + str.length());
			}
		}};
	}

	public static void onChange(final JTable table, final P1<Object[]> p1) {
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = table.getSelectedRow();
                if (selectedIndex > -1) {
                	Object[] rowData = getData(selectedIndex, table);
					p1.e(rowData);
                }
            }
		}});
	}


	public static Object[] getData(int index, final JTable table) {
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		Object[] rowData = new Object[model.getColumnCount()];
		for (int i = 0; i < rowData.length; i++) {
			rowData[i] = model.getValueAt(index, i);
		}
		return rowData;
	}

	public static P1<String> setTextF(final JLabel label) {
		return new P1<String>() {public void e(String obj) {
			label.setText(obj);
//			label.repaint();
		}};
	}
	public static P1<String> setTextF(final JTextComponent ta) {
		return new P1<String>() {public void e(String obj) {
			ta.setText(obj);
//			label.repaint();
		}};
	}

	static String fontFixWidth = null;
	static List<String> linuxFixedWidthFonts = Arrays.asList(
			"Courier New",
			"Monospaced",
			"DejaVu Sans Mono",
			"Tlwg Typist",
			"Tlwg Typo",
			"TlwgMono"
			);
	public static String fontFixWidth() {

		if (fontFixWidth != null) {
			return fontFixWidth;
		}
		
		if (SystemUtil.isWindows()) {
			fontFixWidth = "Courier New";
			return fontFixWidth;
		} else if (SystemUtil.isMac()) {
			fontFixWidth = "Monaco";
			return fontFixWidth;
		} else {
			java.awt.GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			List<String> fonts = Arrays.asList(ge.getAvailableFontFamilyNames());
			for (String font : linuxFixedWidthFonts) {
				if (fonts.contains(font)) {
					fontFixWidth = font;
					return fontFixWidth;
				}
			}
			return null;
		}
	}

	public static void showFileChooser(final P1<String> p1,
			final String def, final boolean dir, final boolean file) {
		
		showFileChooser(p1, new P1<JFileChooser>() {public void e(JFileChooser fc) {
			if (dir && file) {
			    fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			} else if (dir) {
			    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			} else {
			    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			}
			if (def != null) {
			    fc.setSelectedFile(new File(def));
			}
		}});
		
	}

	public static void showFileChooser(final P1<String> p1,
			final P1<JFileChooser> decorF) {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("File");
		
		decorF.e(fc);
		int returnVal = fc.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
		    String path = fc.getSelectedFile().getPath();
		    p1.e(path);
		}
	}
	
	public static JPanel panel_vertical(Component... components) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(components.length, 1));
		for (Component component : components) {
			panel.add(component);
		}
		return panel;
	}
	
	public static F1<Window, P0> disposeFF = new F1<Window, P0>() {public P0 e(Window window) {
		return disposeF(window);
	}};

	public static F1<Window, P0> hideFF = new F1<Window, P0>() {public P0 e(final Window obj) {
		return new P0(){public void e() {
			obj.setVisible(false);
		}};
	}};

	public static P0 disposeF(final Window window) {
		return new P0() {public void e() {
			for (WindowListener windowListener : window.getWindowListeners()) {
				windowListener.windowClosing(null);
			}
			window.dispose();
		}};
	}

	public static JTable table(String... cols) {
		JTable ret = new JTable(new DefaultTableModel( new Object[0][], cols));

		TableColumnModel columnModel = ret.getColumnModel();
		
		for (Enumeration<TableColumn> enu = columnModel.getColumns();enu.hasMoreElements();) {
			TableColumn tableColumn = enu.nextElement();
			tableColumn.setCellRenderer(new DefaultTableCellRenderer(){public Component getTableCellRendererComponent(JTable table,
						Object value, boolean isSelected, boolean hasFocus,
						int row, int column) {
				DefaultTableCellRenderer comp = (DefaultTableCellRenderer) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (value instanceof JLabel) {
					JLabel label = (JLabel) value;
					comp.setText(label.getText());
					if (!isSelected) {
						comp.setBackground(label.getBackground());
						comp.setForeground(label.getForeground());
					}
				}
				return comp;
			}});
			tableColumn.setCellEditor(new DefaultCellEditor(new JTextField()){

				@Override
				public Component getTableCellEditorComponent(JTable table,
						Object value, boolean isSelected, int row, int column) {
					JTextField comp = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
					if (value instanceof JLabel) {
						JLabel label = (JLabel) value;
						comp.setText(label.getText());
					}
					return comp;
				}
				
			});
		}
		
		return ret;
	}

	public static void onPopup(final JTextComponent comp, final F1<Range,JPopupMenu> menuF) {
    	comp.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                	int start = comp.getSelectionStart();
                	int end = comp.getSelectionEnd();
                	
//                	System.out.println(start);
//                	System.out.println(end);
                	
                	// -1 because of JTextEditor
					JPopupMenu menu = menuF.e(
							comp.getClass().equals(JEditorPane.class) ?
							new Range(start - 1, end - 1) :
							new Range(start, end)
							);
					
					if (menu != null) {
						menu.show(comp, e.getX(), e.getY());
					}
                }
            }

        });
    }

	public static void onPopup(final JTable comp, final F1<Collection<Integer>,JPopupMenu> menuF) {
    	comp.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                	
					JPopupMenu menu = menuF.e(Cols.asList(comp.getSelectedRows()));
					
					if (menu != null) {
						menu.show(comp, e.getX(), e.getY());
					}
                }
            }

        });
    }

	public static void onClick(Component label, final P0 p0) {
		onClick(label, Fs.<Point>p1(p0));
	}
	public static void onClick(Component label, final P1<Point> p1) {
		label.addMouseListener(new MouseAdapter() {public void mouseClicked(MouseEvent e) {
			p1.e(e.getPoint());
		}});
	}

	public static JPopupMenu popup(List<JMenuItem> list) {
		if (Cols.isEmpty(list)) {
			return null;
		}
		JPopupMenu ret = new JPopupMenu();
		for (JMenuItem item : list) {
			ret.add(item);
		}
		return ret;
	}

	public static JTextPane textPane(String text) {
		JTextPane ta = new JTextPane();
		
		ta.setText(text);
		
		supportUndo(ta);
		
		return ta;
	}

	public static JTextArea textArea(String text) {
		JTextArea ta = new JTextArea(text);
		
		supportUndo(ta);
		
		return ta;
	}

	public static void supportUndo(final JTextComponent tc) {
		final UndoManager undoManager = new UndoManager();
		onContentEdit(tc.getDocument(), new P1<UndoableEditEvent> () {public void e(UndoableEditEvent e) {
			if (e.getEdit().getPresentationName().startsWith("st")) {
				return;
			}
			
			undoManager.addEdit(e.getEdit());
		}});
		
		tc.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
                if (e.isControlDown()) {
					if ('Z' == e.getKeyCode()) {
						if (undoManager.canUndo()) {
							undoManager.undo();
						}
					} else if ('Y' == e.getKeyCode()) {
						if (undoManager.canRedo()) {
							undoManager.redo();
						}
					}
				}
			}
		});
	}


	public static void onContentEdit(Document document, final P0 p) {
		onContentEdit(document, new P1<UndoableEditEvent>() {
			
			public void e(UndoableEditEvent e) {
				if (e.getEdit().getPresentationName().startsWith("st")) {
					return;
				}
				
				p.e();
			}
		});
	}
	
	public static void onContentEdit(Document document,
			final P1<UndoableEditEvent> p1) {
		document.addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent e) {
				p1.e(e);
			}
		});
	}

	public static TransferHandler dropHandler(
			P1<List<File>> p1) {
		return new FileDropHandler(p1);
	}

	public static Component checkboxInline(String title, boolean checked,
			final P1<Boolean> update) {
		
		final JCheckBox cb = checkbox(checked, update);
		JLabel label = new JLabel(title);
		onClick(label, new P0() {public void e() {
			cb.requestFocus();
			boolean selected = !cb.isSelected();
			cb.setSelected(selected);
			update.e(selected);
		}});
		return panel(label, cb);
	}

	//	final Highlighter.HighlightPainter errorHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.red);
//	public static final Highlighter.HighlightPainter errorHighlightPainter = highlightPainter.e(Color.red);
//	public static final Highlighter.HighlightPainter warningHighlightPainter = highlightPainter.e(Color.orange);

	public static F1<Color,HighlightPainter> errHighlightPainter = new F1<Color,HighlightPainter>() {
		public HighlightPainter e(final Color color) {
			return new Highlighter.HighlightPainter() {
				/**
			     * Paints a highlight.
			     *
			     * @param g the graphics context
			     * @param offs0 the starting model offset >= 0
			     * @param offs1 the ending model offset >= offs1
			     * @param bounds the bounding box for the highlight
			     * @param c the editor
			     */
			    public void paint(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c) {
			    
				    try {
						// --- determine locations ---
						TextUI mapper = c.getUI();
						Rectangle p0 = mapper.modelToView(c, offs0);
						Rectangle p1 = mapper.modelToView(c, offs1);
					
						// --- render ---
						g.setColor(color);
						if (p0.y == p1.y) {
						    // same line, render a rectangle
						    Rectangle r = p0.union(p1);
		//			    g.fillRect(r.x, r.y, r.width, r.height);
						    
						    drawErrLine(r, g);
						    
		//			    g.fillRect(r.x, r.y + r.height - 1, r.width, 1);
						} else {
							Rectangle alloc = bounds.getBounds();
						    // different lines
						    int p0ToMarginWidth = alloc.x + alloc.width - p0.x;
						    drawErrLine(new Rectangle(p0.x, p0.y, p0ToMarginWidth, p0.height), g);
						    if ((p0.y + p0.height) != p1.y) {
								drawErrLine(new Rectangle(alloc.x, p0.y + p0.height, alloc.width, 
										   p1.y - (p0.y + p0.height)), g);
						    }
							drawErrLine(new Rectangle(alloc.x, p1.y, (p1.x - alloc.x), p1.height), g);
						}
				    } catch (BadLocationException e) {
				    	// can't render
				    } catch (Exception e) {
				    	// can't render
				    }
				}

				public void drawErrLine(Rectangle r, Graphics g) {
					for (int i = 0; i < r.width; i++) {
					    int j = i%4;
					    if (j == 3) j=1;
						g.fillRect(r.x + i, r.y + r.height - 1 - j, 1, 1);
					}
				}
			};
		}
	}; 
	
	
	



	public static P0 changeLines(final JTextPane tp, final F1<String, String> f, final P0 afterF) {
		return new P0(){public void e() {
			Range range = new Range(tp.getSelectionStart(), tp.getSelectionEnd());
			
			String text = tp.getText().replaceAll("\r?\n", "\n");
			Range lineRange = StringUtil.lineRange(range, text);
			
			String str = text.substring(lineRange.getFrom(), lineRange.getTo());
			
			tp.setSelectionStart(lineRange.getFrom());
			tp.setSelectionEnd(lineRange.getTo());
	
			
			String newString = f.e(str);
			tp.replaceSelection(newString);
			
			tp.setCaretPosition(lineRange.getFrom());
			
//			tp.setSelectionStart(range.getFrom());
//			tp.setSelectionEnd(range.getFrom() + newString.length() - lineRange.size());
			
			afterF.e();
		}};
	}

	public static void onDrop(P1<List<File>> acceptNewFcpsF, JComponent tp) {
		tp.setTransferHandler(dropHandler(acceptNewFcpsF));
	}
	public static <A> void onTransfer(P1<A> action, DataFlavor flavor, JComponent comp) {
		comp.setTransferHandler(new QTransferHandler<A>(action, flavor));
	}
	public static void onDrop(P1<List<File>> acceptNewFcpsF, JDialog tp) {
		tp.setTransferHandler(dropHandler(acceptNewFcpsF));
	}

	public static void onDrop(P1<List<File>> acceptNewFcpsF, JFrame frame) {
		frame.setTransferHandler(dropHandler(acceptNewFcpsF));
	}

	public static void onMouseMove(final JComponent comp, final P1<Point> p1) {
		comp.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				Point point = e.getPoint();
				p1.e(point);
			}
		});
	}

	
	
	
	
	public static void supportFindNReplace(JTextComponent tp) {
		supportFind(tp);
	}
	
	public static void supportFind(final JTextComponent tp) {
		
		final JTextField tfFind = tf("", 30, null);
		final boolean[] regex = {false};
		Component cbRegex = checkboxInline("Regular Expression", regex[0], Fs.update(regex));
		
		final Matcher[] matcher = new Matcher[] {null};
		final P3<Integer, Boolean, Boolean> findAndMark3 = findAndMark(getTextF(tfFind), tp, matcher);
		final P2<Integer, Boolean> findAndMark = new P2<Integer, Boolean>() {public void e(Integer a, Boolean b) {
			findAndMark3.e(a, b, regex[0]);
		}};

		// Auto find forward
		onKeyUp(tfFind, new P0() {public void e() {
			findAndMark.e(tp.getSelectionStart(), true);
		}});

		final P0 findForward = new P0(){public void e() {
			findAndMark.e(tp.getSelectionEnd(), true);
		}};
		P0 findBackward = new P0() {public void e() {
			findAndMark.e(tp.getSelectionStart() - 1, false);
		}};

		JButton btnFind = btn("Find", 'F', findForward);
		onKeyDown(tp, "F3", findForward);
		onKeyDown(tp, "shift F3", findBackward);
		JButton btnFindBack = btn("Find Backward", findBackward);
		
		final Window frame = null;
		final JDialog dialog = dialog("Find/Replace", frame, hideFF);
		onKeyDown(tp, "control F", new P0(){public void e() {
			String selectedText = tp.getSelectedText();
			if (StringUtil.isNotEmpty(selectedText)) {
				tfFind.setText(selectedText);
			}
			tfFind.requestFocus();
			tfFind.selectAll();
			show(dialog);
		}});
		

		final JTextField tfReplace = tf("", 30);
		dialog.getRootPane().setDefaultButton(btnFind);
		
		
		JPanel panel1 = new JPanel();
		RowAdder rowAdder = rowAdder(panel1);
		rowAdder.row("Find", tfFind);
		rowAdder.row("Replace", tfReplace);
		rowAdder.row("", cbRegex);
		
		dialog.add(panel1, BorderLayout.CENTER);
		dialog.add(panel_vertical(
				btnFind, 
				btnFindBack,
				btn("Replace", 'R', new P0() {public void e() {
					if (regex[0]) {
						if (matcher[0] != null) {
							String replace = matcher[0].pattern().matcher(matcher[0].group()).replaceAll(tfReplace.getText());
							tp.replaceSelection(replace);
							findForward.e();
						}
					} else {
						tp.replaceSelection(tfReplace.getText());
						findForward.e();
					}
				}})), 
				BorderLayout.EAST);
	}

	public static void onKeyDown(Component comp, final String keyStrokeStr,
			final P0 action) {
		final KeyStroke keyStroke = getKeyStroke(keyStrokeStr);
		if (keyStroke==null) {
			throw new IllegalArgumentException("Unknown key stroke: " + keyStrokeStr);
		}
		comp.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
                if (keyStroke.getKeyCode() == e.getKeyCode()
                		&& (e.isControlDown() == ((keyStroke.getModifiers() & InputEvent.CTRL_MASK) > 0))
                		&& (e.isAltDown() == ((keyStroke.getModifiers() & InputEvent.ALT_MASK) > 0))
                		&& (e.isShiftDown() == ((keyStroke.getModifiers() & InputEvent.SHIFT_MASK) > 0))
                		&& (e.isMetaDown() == ((keyStroke.getModifiers() & InputEvent.META_MASK) > 0))
                		) {
                	e.consume();
                	action.e();
                }
			}
		});
	}
	
	private static KeyStroke getKeyStroke(String keyStrokeStr) {
		if (SystemUtil.isMac()) {
			return KeyStroke.getKeyStroke(keyCode(keyStrokeStr), keyStrokeModMac(keyStrokeStr), keyStrokeStr.toLowerCase().contains("released"));
		} else {
			return KeyStroke.getKeyStroke(keyStrokeStr);
		}
	}
	private static int keyCode(String keyStrokeStr) {
		String code = StringUtil.getLastWord(keyStrokeStr);
		Field field = ReflectUtil.getField("VK_" + code.toUpperCase(), KeyEvent.class);
		if (field== null) {
			throw new IllegalArgumentException("Unknown key code " + code);
		}
		return ReflectUtil.getFieldValue(field, null);
	}
	private static int keyStrokeModMac(String keyStrokeStr) {
		keyStrokeStr = keyStrokeStr.toLowerCase();
		int mod = 0;
		if (keyStrokeStr.contains("ctrl") || keyStrokeStr.contains("control")) {
			mod = mod | InputEvent.CTRL_MASK;
		}
		if (keyStrokeStr.contains("alt")) {
			mod = mod | Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		}
		if (keyStrokeStr.contains("meta")) {
			mod = mod | InputEvent.ALT_MASK;
		}
		return mod;
	}
	public static P3<Integer, Boolean, Boolean> findAndMark(final F0<String> findStrF,
			final JTextComponent tp, final Matcher[] matcher) {
		return new P3<Integer, Boolean, Boolean>() {public void e(final Integer fromPos, Boolean forward, Boolean regex) {
			matcher[0]= null;
			
			if (fromPos == -1 && !forward) {
				return;
			}
			String allStr = tp.getText().replaceAll("\r?\n", "\n");
//			
			String findStr = findStrF.e();
			
			
			if (regex) {
				try {
					if (forward) {
						Matcher matcher2 = Pattern.compile(findStr).matcher(allStr);
						if (matcher2.find(fromPos)) {
							matcher[0] = matcher2;
						}
					} else {
						matcher[0] = RegexUtil.lastFound(Pattern.compile(findStr), fromPos, allStr);
					}
					
					if (matcher[0] != null) {
						tp.setSelectionStart(matcher[0].start());
						tp.setSelectionEnd(matcher[0].end());
					}
				} catch (PatternSyntaxException e) {
					// Normal
//					System.out.println("?");
				}
				
				
			} else {
				int found = forward ? allStr.indexOf(findStr, fromPos) :
					allStr.lastIndexOf(findStr, fromPos)
					;
				if (found > -1) {
					tp.setSelectionStart(found);
					tp.setSelectionEnd(found + findStr.length());
				}
			}
		}};
	}
	
	public static P1<Boolean> enableF(final Component comp) {
		return new P1<Boolean>() {public void e(Boolean enab) {
			comp.setEnabled(enab);
		}};
	}

	public static P0 enableF(final Component comp, boolean enabled) {
		return Fs.p0(enableF(comp), enabled);
	}

	public static void onCaretChanged(final JTextPane tp, final P1<Integer> p1) {
		tp.addCaretListener(new CaretListener() {public void caretUpdate(CaretEvent e) {
//			System.out.println(
//					"Thread: " + Thread.currentThread() + 
//					", Dot: " + e.getDot() + 
//					", Mark: " + e.getMark() +
//					", Source: " + e.getSource()
//					);
			p1.e(e.getDot());
		}});
	}
	

	public static void onCaretChanged(final JTextPane tp, final P0 p0) {
		onCaretChanged(tp, Fs.<Integer>p1(p0));
	}
	
	public static P0 async(final P0 p0) {
		return new P0() {public void e() {
			SwingUtilities.invokeLater(Fs.runnable(p0));
		}} ;
	}
	public static <A> P1<A> async(final P1<A> p1) {
		return new P1<A>() {public void e(final A a) {
			SwingUtilities.invokeLater(new Runnable() {public void run() {
				p1.e(a);
			}});
		}};
	}

	private static boolean isNonEditKey(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_UP:
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_CAPS_LOCK:
		case KeyEvent.VK_CONTROL:
		case KeyEvent.VK_ALT:
		case KeyEvent.VK_SHIFT:
		case KeyEvent.VK_INSERT:
		case KeyEvent.VK_HOME:
		case KeyEvent.VK_END:
		case KeyEvent.VK_PAGE_DOWN:
		case KeyEvent.VK_PAGE_UP:
		case KeyEvent.VK_PRINTSCREEN:
		case KeyEvent.VK_NUM_LOCK:
		case KeyEvent.VK_ESCAPE:
			return true;
		}
		if (keyCode >= KeyEvent.VK_F1 && keyCode <= KeyEvent.VK_F12) {
			return true;
		}
		return false;
	}

	public static void onPopup(final Component comp, final F0<JPopupMenu> f0) {
		onPopup(comp, Fs.<MouseEvent,JPopupMenu>f1(f0));
	}
	public static void onPopup(final Component comp, final F1<MouseEvent,JPopupMenu> f1) {
		comp.addMouseListener(new MouseAdapter() {
	        public void mouseReleased(MouseEvent e) {
	            if (e.getButton() == MouseEvent.BUTTON3) {
	            	// -1 because of JTextEditor
					JPopupMenu menu = f1.e(e);
					
					if (menu != null) {
						menu.show(comp, e.getX(), e.getY());
					}
	            }
	        }
	
	    });
	}

	public static void removeHightlights(
			final Collection<Highlighter.HighlightPainter> sample,
			Highlighter highlighter) {
		for (Highlight highlight : highlighter.getHighlights()) {
			if (sample.contains(highlight.getPainter())) {
				highlighter.removeHighlight(highlight);
			}
		}
	
	}

	public static void removeHightlights(
			final Highlighter.HighlightPainter sample,
			Highlighter highlighter) {
		for (Highlight highlight : highlighter.getHighlights()) {
			if (sample == highlight.getPainter()) {
				highlighter.removeHighlight(highlight);
			}
		}
	
	}

	public static void onLostFocus(Component comp, final P0 p0) {
		comp.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				p0.e();
			}
			
			public void focusGained(FocusEvent e) {
			}
		});
	}

	public static void onMouseDoubleClick(Component comp, final P0 p0) {
		onMouseDoubleClick(comp, Fs.<Point>p1(p0));
	}
	public static void onMouseDoubleClick(Component comp, final P1<Point> p1) {
		comp.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					p1.e(e.getPoint());
				}
			}
		});
	}

	public static void onMouseClick(Component comp, final P1<Point> p1) {
		comp.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					p1.e(e.getPoint());
				}
			}
		});
	}

	public static void centerlizeCaret(JTextPane tp, JScrollPane scrollPane) {
		try {
			Point caretPosition = MathUtil.center(tp.modelToView(tp.getCaretPosition()));
//			Point caretPosition = tp.getCaret().getMagicCaretPosition();
//			Rectangle rect = scrollPane.getVisibleRect();
			JViewport viewport = scrollPane.getViewport();
			Rectangle rect = viewport.getViewRect();
//			viewport.set
			System.out.println("caretPosition=" + caretPosition + ", rect=" + rect);
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}
	}

	public static void onMouseStay(Component comp, final P1<Point> p1, final P0 onLeave) {
		final java.util.Timer timer = new java.util.Timer();
		final TimerTask[] task = {null};
		final boolean[] active = {false};
		MouseAdapter adapter = new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				mouseMoved(e);
			}
	
			public void mouseExited(MouseEvent e) {
				if (task[0] != null) {
					task[0].cancel();
				}
				if (active[0]) {
					onLeave.e();
					active[0] = false;
				}
			}
	
			public void mouseMoved(final MouseEvent e) {
				if (active[0]) {
					onLeave.e();
					active[0] = false;
				}
				
				if (task[0] != null) {
					task[0].cancel();
				}
				task[0] = new TimerTask() {public void run() {
					p1.e(e.getPoint());
				}};
				timer.schedule(task[0], 100);
			}
		};
		comp.addMouseListener(adapter);
		comp.addMouseMotionListener(adapter);
	}

	public static P0 gotoLine(final JTextPane tp, final Window window) {
			return new P0() {public void e() {
				// UT
				final JDialog dialog = dialog("Go to line", window);
				dialog.setModal(true);
				RowAdder rowAdder = rowAdder(dialog);
				
				final JTextField tfLine = new JTextField("" + (tp.getDocument().getDefaultRootElement().getElementIndex(tp.getCaretPosition()) + 1), 30);
				tfLine.selectAll();
				rowAdder.row("Line", tfLine);
				JButton btnGo = btn("Go", new P0() {public void e() {
					// UT
					dialog.dispose();
					int index = StringUtil.indexOfHappenNum('\n', Integer.parseInt(tfLine.getText()) - 1, tp.getText());
					if (index == -1) {
					} else if (index == 0) {
						tp.setCaretPosition(index);
					} else {
						tp.setCaretPosition(index + 1);
					}
				}});
//				tp.getDocument().addUndoableEditListener(null)
				dialog.getRootPane().setDefaultButton(btnGo);
				rowAdder.whole(panel(btnGo));
				show(dialog);
			}};
		}

	public static void onViewPortChanged(JScrollPane scrollPane, final P0 p0) {
		scrollPane.getViewport().addChangeListener(new ChangeListener() {public void stateChanged(ChangeEvent e) {
			p0.e();
		}});
	}

	public static String getText(final JTextPane tp, final F0<Boolean> interrupted) {
	
		Document doc = tp.getDocument();
	    try {
			int length = doc.getLength();
			EditorKit editorKit = tp.getUI().getEditorKit(tp);
	
		    StringWriter out = new StringWriter();
			// 524288
			// 262144
			int index = 0;
			while (length > 0) {
				if (interrupted.e())return null;
				
				int l;
				if (length > 524288) {
					l = 524288;
				} else {
					l = length;
				}
				editorKit.write(out, doc, index, l);
				length -= l;
				index+=l;
				
			}
			return out.toString();
	    } catch (BadLocationException e) {
			throw new RuntimeException(e);
	    } catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static class MenuAdder {
		private final JMenu menu;
		private ArrayList<Range> ranges = new ArrayList<Range>();
		int index = 0;
		public MenuAdder(JMenu menu) {
			this.menu = menu;
			index = menu.getItemCount();
		}
		
		public void add(JMenuItem menuItem) {
			index++;
			menu.add(menuItem);
		}
		
		public void addSeparator() {
			index++;
			menu.addSeparator();
		}
		
		public P1<Collection<JMenuItem>> createItemsSetter() {
			final Range range = new Range(index, index);
			ranges.add(range);
			return new P1<Collection<JMenuItem>>() {public void e(Collection<JMenuItem> col) {
				// Remove current items
				for (int i = 0; i < range.size(); i++) {
					menu.remove(range.getFrom());
				}
				range.setTo(range.getFrom());
				
				if (col != null) {
					for (JMenuItem item : col) {
						menu.insert(item, range.getTo());
						range.setTo(range.getTo() + 1);
					}
				}
				
				// Update other ranges
			}};
		}
	}
	
	/**
	 * TODO doc
	 * @param menu
	 * @return
	 */
	public static MenuAdder menuAdder(JMenu menu) {
		return new MenuAdder(menu);
	}

	/**
	 * Will not be invoked if frame.setVisible(false) || frame.dispose()
	 * @param frame
	 * @param p0
	 */
	public static void onClose(Window frame, final P0 p0) {
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				p0.e();
			}
		});
	}
	public static JPanel panel_horizontal(JComponent... comps) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, comps.length));
		for (JComponent c : comps) {
			panel.add(c);
		}
		return panel;
	}
	public static boolean trimTALength(final JTextComponent ta) {
		boolean atEnd = false;
		int length = ta.getDocument().getLength();
		
		int caretPosition = ta.getCaretPosition();
		try {
			if (length > 800000) {
				int subtracted = length/2;
				int newLength = length - subtracted;
				ta.setText(ta.getText(subtracted, newLength));
				caretPosition = Math.max(0, caretPosition - subtracted);
				
				length = newLength;
				ta.setCaretPosition(caretPosition);
			}
		} catch (BadLocationException e) {
		}
		if (length == caretPosition) {
			atEnd = true;
		}
		return atEnd;
	}
	public static void frameMover(JComponent comp) {
		MoveMouseListener mml = new MoveMouseListener(comp);
		comp.addMouseListener(mml);
		comp.addMouseMotionListener(mml);
	}
	public static void onResize(final JFrame frame, final P1<Dimension> p1) {
		frame.addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent e) {
				p1.e(frame.getSize());
			}
		});
	}
	public static void onReady(Window window, final P0 p0) {
		window.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				p0.e();
			}
		});
	}
	public static void centerlize(Window w) {
	    Point location = SwingUtil4.getScreenCenterLocation(w);
		w.setLocation(location);
	}
	public static P1<JComponent> setCompF(final Container container) {
		final CardLayout cardLayout = new CardLayout();
		container.setLayout(cardLayout);
		
		final IdentityHashMap<Object, String> nameMap = new IdentityHashMap<Object, String>();
		final int[] count = {0};
		return new P1<JComponent>() {public void e(JComponent obj) {
			String name = nameMap.get(obj);
			if (name==null) {
				name = "blah" + count[0]++;
				container.add(obj, name );
				nameMap.put(obj, name);
			}
			cardLayout.show(container, name);
//			container.revalidate();
		}};
	}
	public static void onFocus(Component comp, final P0 p0) {
		if (comp instanceof Window) {

			((Window)comp).addWindowFocusListener(new WindowFocusListener() {
				public void windowLostFocus(WindowEvent e) {
				}
				
				@Override
				public void windowGainedFocus(WindowEvent e) {
					p0.e();
				}
			});
		} else {
			comp.addFocusListener(new FocusListener() {
				public void focusLost(FocusEvent e) {
				}
				public void focusGained(FocusEvent e) {
					p0.e();
				}
			});
		}
	}
	public static void supportHyperlink(final JEditorPane ep) {
		ep.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent evt) {
				if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					String url = evt.getURL().toString();
					DesktopUtil.browse(url);
				}
			}
		});
	}
	public static void repaint(final Component comp) {
		SwingUtilities.invokeLater(new Runnable() {public void run() {
			comp.repaint();
		}});
	}
	public static void onDrag(Component comp, final P1<Point> startF, final P2<Point, Point> dragF) {
		final AtomicReference<P0> onMouseReleased = new AtomicReference<P0>();
		final AtomicReference<P1<Point>> onMouseDragged = new AtomicReference<P1<Point>>();
		onMousePressed(comp, new P1<Point>() {public void e(final Point initPoint) {
			if (startF!=null) {
				startF.e(initPoint);
			}
			onMouseDragged.set(Fs.p1(dragF, initPoint));
			onMouseReleased.set(new P0() {public void e() {
				onMouseDragged.set(null);
				onMouseReleased.set(null);
			}});
		}});
	
		onMouseDragged(comp, Fs.atomicP1(onMouseDragged));
		onMouseRelease(comp, Fs.atomicP0(onMouseReleased));
	}
	public static void onDrag(Component comp, final P1<Point> newLocationF, final F0<Point> currentLocationF) {
		final Point[] startP = {null};
		onDrag(comp, new P1<Point>() {public void e(Point initPoint) {
			startP[0] = currentLocationF.e();
		}}, new P2<Point, Point>() {public void e(Point p, Point initPoint) {
			newLocationF.e(new Point(
					startP[0].x + p.x - initPoint.x,
					startP[0].y + p.y - initPoint.y
					));
		}});
	}
	public static Rectangle bound(Point... points) {
		int smallestX = Integer.MAX_VALUE;
		int smallestY = Integer.MAX_VALUE;
		int largestX = Integer.MIN_VALUE;
		int largestY = Integer.MIN_VALUE;
		for (Point point : points) {
			if (point==null) {
				continue;
			}
			if (point.x > largestX) largestX = point.x;
			if (point.y > largestY) largestY = point.y;
			if (point.x < smallestX) smallestX = point.x;
			if (point.y < smallestY) smallestY = point.y;
		}
		return new Rectangle(smallestX, smallestY, largestX - smallestX, largestY - smallestY);
	}
	public static StartStopPanel startStopPanel(final F0<P0> startF) {
		StartStopPanel panel = new StartStopPanel();
		panel.setLayout(new FlowLayout());
	
		final JButton startBtn = new JButton("Start");
		if (startF==null) {
			startBtn.setEnabled(false);
		}
		final JButton stopBtn = new JButton("Stop");
		stopBtn.setEnabled(false);
	
		final P0[] stopF = {null};
		panel.stopF = stopF;
		P0 startF1 = new P0() {public void e() {
			startBtn.setEnabled(false);
			stopBtn.setEnabled(true);
			
			final P0 stopF1 = startF.e();
	
			stopF[0] = new P0() {public void e() {
				startBtn.setEnabled(true);
				stopBtn.setEnabled(false);
				
				stopF1.e();
			}};
		}};
		startBtn.addActionListener(SwingUtil.toActionListener(startF1));
		stopBtn.addActionListener(SwingUtil.toActionListener(Fs.invokeRef(stopF)));
			
		panel.add(startBtn);
		panel.add(stopBtn);
		return panel;
	}
	public static class StartStopPanel extends JPanel {
		public P0[] stopF;

		public void stop() {
			if (stopF[0] != null) {
				stopF[0].e();
			}
		}
		public void link(Window window) {
			if (window!=null) {
				onClose(window, Fs.invokeRef(stopF));
			}
		}
	}

	public static P0 selectAllF(final JTextField tb) {
		return new P0() {public void e() {
			tb.selectAll();
		}};
	}
	public static Component dialogControls(Component... comps) {
		return panel_vertical(new JSeparator(), panel(comps));
	}
	public static void onAction(final AbstractButton btn, final P0 action) {
		btn.addActionListener(toActionListener(ThreadUtil.async(new P0() {public void e() {
			btn.setEnabled(false);
			action.e();
			btn.setEnabled(true);
		}})));
	}
	
	public static void main(String[] args) {
		JTextField tf = new JTextField();
		onlyLowerAlphaNumeric(tf);
		DesktopUI4.alert(tf);
	}
	public static void onlyLowerAlphaNumeric(final JTextField tf) {
		onChange(tf, new P1<String>() {public void e(final String str) {
			asyncRun(new P0() {public void e() {
				String newVal = str.replaceAll("\\W+", "").toLowerCase();
//				System.out.println(newVal);
				tf.setText(newVal);
			}});
		}});
	}
	public static ActionListener toActionListener(final P0 f) {
	    return new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            f.e();
	        }
	    };
	}
	public static Rectangle expand(Rectangle rect, int amount) {
		return new Rectangle(
				rect.x - amount,
				rect.y - amount,
				rect.width + amount*2,
				rect.height + amount*2
				);
	}
	public static void draw(BufferedImage image, Rectangle rectangle,
			BufferedImage backgroundImg) {
		Graphics2D g = backgroundImg.createGraphics();
		if (rectangle == null) {
			g.drawImage(image, 0, 0, null);
		} else {
			g.drawImage(image, rectangle.x, rectangle.y, null);
		}
		g.dispose();
	}
	public static P0 setVisibleF(final Window w, final boolean visible) {
		return new P0() {public void e() {
			w.setVisible(visible);
		}};
	}
	public static void onMouseHover(Component comp, final P1<Boolean> p1) {
		comp.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				p1.e(false);
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				p1.e(true);
			}
		});

	}
	public static Color alpha(int alpha, Color color) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}
	public static GetSet<Integer> getSetIndex(final JComboBox combobox) {
		return new GetSet<Integer>(
				new F0<Integer>() {public Integer e() {
					return combobox.getSelectedIndex();
				}},
				new P1<Integer>() {public void e(Integer obj) {
					combobox.setSelectedIndex(obj);
				}}
				);
	}
}
