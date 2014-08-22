package qj.tool.swing;

import javax.swing.undo.UndoManager;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CannotRedoException;
import javax.swing.event.UndoableEditEvent;
import java.util.List;
import java.util.ArrayList;

public class TrackChangeUndoManager extends UndoManager {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int savePos = 0;
    private int currentPos = 0;
    private List listeners = new ArrayList(1);

    public void saved() {
//    	System.out.println("saved() called");
        savePos = currentPos;
        checkToAnounceSavedStatusChange();
    }

    public void undoableEditHappened(UndoableEditEvent e) {
        super.undoableEditHappened(e);
//    	System.out.println("undoableEditHappened() called");

        // We come to place that no undo or redo can make back to save pos. unless they do save.
        if (savePos>-1 && currentPos<savePos)
            savePos = -1;

        currentPos++;
        checkToAnounceSavedStatusChange();
    }

    public void addSavedStatusListener(ISavedStatusListener listener) {
        listeners.add(listener);
    }

    private boolean beingChanged = false;
    private void checkToAnounceSavedStatusChange() {
        if (beingChanged !=isChanged()) {
            beingChanged =!beingChanged;
//            System.out.println("Announce change to " + beingChanged + "; savePos=" + savePos + ";curPos=" + currentPos);
            for (int i = 0; i < listeners.size(); i++) {
                ((ISavedStatusListener) listeners.get(i)).savedStatusChanged();
            }
        }
    }

    public synchronized void undo() throws CannotUndoException {
    	if (super.canUndo()) {
	        super.undo();
	        currentPos--;
	        checkToAnounceSavedStatusChange();
    	}
    }

    public synchronized void redo() throws CannotRedoException {
        if (super.canRedo()) {
        	super.redo();
            currentPos++;
            checkToAnounceSavedStatusChange();
        }
    }

    public boolean isChanged() {
        return (savePos!=currentPos);
    }
}
