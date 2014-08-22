package qj.util.swing;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoableEditWrapper implements UndoableEdit {
	
	public UndoableEditWrapper(UndoableEdit target) {
		
	}
	
	public void undo() throws CannotUndoException {
		
	}

	public boolean canUndo() {
		// TODO Auto-generated method stub
		return false;
	}

	public void redo() throws CannotRedoException {
		// TODO Auto-generated method stub
		
	}

	public boolean canRedo() {
		// TODO Auto-generated method stub
		return false;
	}

	public void die() {
		// TODO Auto-generated method stub
		
	}

	public boolean addEdit(UndoableEdit anEdit) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean replaceEdit(UndoableEdit anEdit) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSignificant() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getPresentationName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUndoPresentationName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRedoPresentationName() {
		// TODO Auto-generated method stub
		return null;
	}

}
