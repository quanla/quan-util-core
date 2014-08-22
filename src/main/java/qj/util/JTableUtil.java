package qj.util;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import qj.util.funct.P0;
import qj.util.funct.P1;

public class JTableUtil {

	public static P0 deleteRowF(final JTable table) {
		return new P0() {public void e() {
			DefaultTableModel model = (DefaultTableModel)table.getModel();
			model.removeRow(table.getSelectedRow());
		}};
	}

	public static P0 addRowF(final JTable table) {
		return new P0() {public void e() {
			((DefaultTableModel)table.getModel()).addRow(new Object[table.getColumnCount()]);
		}};
	}

	public static void addRow(JTable table, Object... data ) {
		((DefaultTableModel)table.getModel()).addRow(data);
	}

	public static void insertRow(final JTable table, final int index, Object... data ) {
		((DefaultTableModel)table.getModel()).insertRow(index,data);
	}

	public static void addRow(final JTable table, final P0 onSelect, Object... data ) {
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		model.addRow(data);
		final int index = model.getRowCount() - 1;
	
		onChangeSelection(table, new P1<Integer>() {public void e(Integer selectedIndex) {
			if (selectedIndex == index) {
				onSelect.e();
			}
		}});
	}

	public static void onChangeSelection(final JTable table,
			final P1<Integer> onChangeSelection) {
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {public void valueChanged(ListSelectionEvent e) {
	        if (!e.getValueIsAdjusting()) {
	            int selectedIndex = table.getSelectedRow();
	            onChangeSelection.e(selectedIndex);
	        }
		}});
	}

	public static void removeAllRows(JTable table) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		for (int i = table.getRowCount() - 1; i > -1; i--) {
			model.removeRow(i);
		}
	}

	public static void removeRows(int[] selectedRows, final JTable successTable) {
		DefaultTableModel model = (DefaultTableModel) successTable.getModel();
		for (int i = selectedRows.length - 1; i > -1; i--) {
			model.removeRow(selectedRows[i]);
		}
	}

	public static void setColumnWidths(JTable tbl, Integer... widths) {
		TableColumnModel columnModel = tbl.getColumnModel();
		for (int i = 0; i < widths.length; i++) {
			if (widths[i] > -1) {
				columnModel.getColumn(i).setPreferredWidth(widths[i]);
			}
		}
	}

	public static void setColumnMaxWidths(JTable tbl, Integer... widths) {
		TableColumnModel columnModel = tbl.getColumnModel();
		for (int i = 0; i < widths.length; i++) {
			if (widths[i] > -1) {
				columnModel.getColumn(i).setMaxWidth(widths[i]);
			}
		}
	}

}
