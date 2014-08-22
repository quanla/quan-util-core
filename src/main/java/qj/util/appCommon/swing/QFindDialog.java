package qj.util.appCommon.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import qj.tool.findReplace.FindReplace;
import qj.util.DesktopUtil4;

public class QFindDialog extends JDialog implements ActionListener {
	private static final int WIDTH = 300;
	private static final int HEIGHT = 277;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextPane mother;
	
	private JPanel jContentPane = null;
	private JPanel jPanelEast = null;
	private JPanel jPanelCenter = null;
	private JButton btnFind = null;
	private JButton btnReplace = null;
	private JButton btnReplaceAll = null;
	private JLabel jLabelEmpty = null;
	private JButton btnClose = null;
	private JLabel lbFind = null;
	private JComboBox cbFind = null;
	private JLabel lbReplaceWith = null;
	private JComboBox cbReplace = null;
	private JPanel panelChoices = null;
	private JPanel jPanel1 = null;

	public QFindDialog(JTextPane mother) {
		super((Frame) mother.getTopLevelAncestor());
		this.mother = mother;
		
		initialize();
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==btnFind) {
			// Find with regex.
			FindReplace.find((String) cbFind.getSelectedItem(), mother, true);
		} else if (ae.getSource()==btnReplace) {
			// replace with regex.
			FindReplace.replace((String) cbFind.getSelectedItem(), (String) cbReplace.getSelectedItem(), mother, true);
		} else if (ae.getSource()==btnReplaceAll) {
			// replaceAll with regex.
			FindReplace.replaceAll((String) cbFind.getSelectedItem(), (String) cbReplace.getSelectedItem(), mother, true);
		} else if (ae.getSource() == btnClose)
			this.setVisible(false);
	}
	
	public void setFind(String text) {
		cbFind.setSelectedItem(text);
	}
	
	public void setReplace(String text) {
		cbReplace.setSelectedItem(text);
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(WIDTH, HEIGHT);
		this.setResizable(false);
		this.setTitle("Find/Replace");
		this.setContentPane(getJContentPane());
		
		int x = Integer.parseInt(Preferences.userNodeForPackage(QFindDialog.class).get("locationX", "" + (DesktopUtil4.getScreenWidth() / 2 - WIDTH / 2)));
		int y = Integer.parseInt(Preferences.userNodeForPackage(QFindDialog.class).get("locationY", "" + (DesktopUtil4.getScreenWidth() / 2 - HEIGHT / 2)));
		this.setLocation(x,y);
	}

	public void dispose() {
		// Memoir
		Preferences.userNodeForPackage(QFindDialog.class).put("locationX", "" + this.getX());
		Preferences.userNodeForPackage(QFindDialog.class).put("locationY", "" + this.getY());
		
		super.dispose();
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJPanelEast(), java.awt.BorderLayout.EAST);
			jContentPane.add(getJPanelCenter(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanelEast	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelEast() {
		if (jPanelEast == null) {
			jLabelEmpty = new JLabel();
			jLabelEmpty.setText("");
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(8);
			gridLayout.setColumns(1);
			jPanelEast = new JPanel();
			jPanelEast.setLayout(gridLayout);
			jPanelEast.add(getBtnFind(), null);
			jPanelEast.add(getBtnReplace(), null);
			jPanelEast.add(getBtnReplaceAll(), null);
			jPanelEast.add(jLabelEmpty, null);
			jPanelEast.add(getBtnClose(), null);
		}
		return jPanelEast;
	}

	/**
	 * This method initializes jPanelCenter	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelCenter() {
		if (jPanelCenter == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.weightx = 5.0D;
			gridBagConstraints4.weighty = 5.0D;
			gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints4.gridy = 4;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 3;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.gridx = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints2.ipadx = 0;
			gridBagConstraints2.gridy = 2;
			lbReplaceWith = new JLabel();
			lbReplaceWith.setText("Replace with");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.gridwidth = 1;
			gridBagConstraints1.gridx = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.gridy = 0;
			lbFind = new JLabel();
			lbFind.setText("Find");
			jPanelCenter = new JPanel();
			jPanelCenter.setLayout(new GridBagLayout());
			jPanelCenter.add(lbFind, gridBagConstraints);
			jPanelCenter.add(getCbFind(), gridBagConstraints1);
			jPanelCenter.add(lbReplaceWith, gridBagConstraints2);
			jPanelCenter.add(getCbReplace(), gridBagConstraints3);
			jPanelCenter.add(getPanelChoices(), gridBagConstraints4);
		}
		return jPanelCenter;
	}

	/**
	 * This method initializes btnFind	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnFind() {
		if (btnFind == null) {
			btnFind = new JButton();
			btnFind.setText("Find Next");
			btnFind.addActionListener(this);
			btnFind.setMnemonic(java.awt.event.KeyEvent.VK_F);
		}
		return btnFind;
	}

	/**
	 * This method initializes btnReplace	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnReplace() {
		if (btnReplace == null) {
			btnReplace = new JButton();
			btnReplace.setText("Replace");
			btnReplace.addActionListener(this);
			btnReplace.setMnemonic(java.awt.event.KeyEvent.VK_R);
		}
		return btnReplace;
	}

	/**
	 * This method initializes btnReplaceAll	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnReplaceAll() {
		if (btnReplaceAll == null) {
			btnReplaceAll = new JButton();
			btnReplaceAll.addActionListener(this);
			btnReplaceAll.setMnemonic(java.awt.event.KeyEvent.VK_A);
			btnReplaceAll.setText("Replace All");
		}
		return btnReplaceAll;
	}

	/**
	 * This method initializes btnClose	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnClose() {
		if (btnClose == null) {
			btnClose = new JButton();
			btnClose.setMnemonic(java.awt.event.KeyEvent.VK_C);
			btnClose.setText("Close");
			btnClose.addActionListener(this);
		}
		return btnClose;
	}

	/**
	 * This method initializes cbFind	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getCbFind() {
		if (cbFind == null) {
			cbFind = new JComboBox();
			cbFind.setEditable(true);
		}
		return cbFind;
	}

	/**
	 * This method initializes cbReplace	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getCbReplace() {
		if (cbReplace == null) {
			cbReplace = new JComboBox();
			cbReplace.setEditable(true);
		}
		return cbReplace;
	}

	/**
	 * This method initializes panelChoices	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPanelChoices() {
		if (panelChoices == null) {
			GridLayout gridLayout1 = new GridLayout();
			gridLayout1.setRows(6);
			gridLayout1.setColumns(1);
			panelChoices = new JPanel();
			panelChoices.setLayout(gridLayout1);
			panelChoices.add(getChoicePane1(), null);
		}
		return panelChoices;
	}

	/**
	 * This method initializes jSplitPane1
	 * 	
	 * @return javax.swing.JSplitPane
	 */
	private JPanel getChoicePane1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
		}
		return jPanel1;
	}

}  //  @jve:decl-index=0:visual-constraint="199,1"
