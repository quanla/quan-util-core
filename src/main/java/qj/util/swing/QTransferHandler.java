package qj.util.swing;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.TransferHandler;

import qj.util.funct.P1;

public class QTransferHandler<A> extends TransferHandler {

	final P1<A> action;
    DataFlavor flavor;
	
    public QTransferHandler(P1<A> action, DataFlavor flavor) {
		this.action = action;
		this.flavor = flavor;
	}

	public boolean canImport(TransferSupport supp) {
		return supp.isDataFlavorSupported(flavor);
    }

    @SuppressWarnings("unchecked")
	public boolean importData(TransferSupport support) {
//        if (!canImport(support)) {
//            return false;
//        }

        /* fetch the Transferable */
        Transferable t = support.getTransferable();

        try {
            /* fetch the data from the Transferable */
        	A a = (A) t.getTransferData(flavor);

			action.e(a);
        } catch (UnsupportedFlavorException e) {
            return false;
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}
