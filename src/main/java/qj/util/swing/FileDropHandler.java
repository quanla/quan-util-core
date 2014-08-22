package qj.util.swing;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.TransferHandler;

import qj.util.funct.P1;

public class FileDropHandler extends TransferHandler {

	final P1<List<File>> action;
	
    public FileDropHandler(P1<List<File>> action) {
		this.action = action;
	}

	public boolean canImport(TransferSupport supp) {
        /* for the demo, we'll only support drops (not clipboard paste) */
        if (!supp.isDrop()) {
            return false;
        }

        /* return true if and only if the drop contains a list of files */
        return supp.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    @SuppressWarnings("unchecked")
	public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        /* fetch the Transferable */
        Transferable t = support.getTransferable();

        try {
            /* fetch the data from the Transferable */
        	List<File> fileList = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);

			action.e(fileList);
        } catch (UnsupportedFlavorException e) {
            return false;
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}
