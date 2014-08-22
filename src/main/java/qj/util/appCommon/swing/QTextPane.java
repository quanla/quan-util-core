package qj.util.appCommon.swing;

import javax.swing.JTextPane;

import qj.tool.swing.TrackChangeUndoManager;

public class QTextPane extends JTextPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TrackChangeUndoManager undoManager = new TrackChangeUndoManager();
	private QFindDialog findDialog;
	public QTextPane() {
		getDocument().addUndoableEditListener(undoManager);
	}
	
	public void showFindDialog() {
		if (findDialog==null) {
			findDialog = new QFindDialog(this);
		}
		findDialog.setVisible(true);
		
	}

	public TrackChangeUndoManager getUndoManager() {
		return undoManager;
	}

	public void setUndoManager(TrackChangeUndoManager undoManager) {
		this.undoManager = undoManager;
	}
	
	

//	public void setText(String text) {
//		try {
//			StyledDocument doc = this.getStyledDocument();
//			doc.remove(0, doc.getLength());
//			doc.insertString(0, text, null);
//			this.updateUI();
//		} catch (BadLocationException e) {
//			e.printStackTrace();
//		}
//	}

//	public String getText() {
//		Document doc = getDocument();
//		try {
//			return doc.getText(0, doc.getLength());
//		} catch (BadLocationException e) {
//			e.printStackTrace();
//			return "";
//		}
//	}
	
	
}
