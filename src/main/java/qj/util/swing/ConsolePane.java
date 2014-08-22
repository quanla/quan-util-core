package qj.util.swing;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.*;

import qj.util.SwingUtil;
import qj.util.funct.P0;
import qj.util.funct.P1;

public class ConsolePane extends JTextPane {
	
	public ConsolePane() {
		SwingUtil.console(this);
	}
	
	public P1<String> createErrOut() {
		final Style style = this.addStyle("Red", null);
		StyleConstants.setForeground(style, 
//				new Color(255, 160, 160)
				Color.red
		);
		return createOut(style);
	}

	private void write(String s, AttributeSet attr) {
		SwingUtil.trimTALength(this);
		Document document = this.getDocument();
		try {
			document.insertString(document.getLength(), s, attr);
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}
	}

	public P1<String> createNormalOut() {
		return createOut(null);
	}

	private P1<String> createOut(final Style style) {
		return new P1<String>() {public void e(final String s) {
			SwingUtil.asyncRun(new P0() {public void e() {
				write(s, style);
			}});
		}};
	}
}
