package qj.util.swing;

import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import qj.util.SwingUtil;
import qj.util.funct.P0;

public class SwingLists {
	public static JList createList(ListItem... items) {
		return createList(Arrays.asList(items));
	}
	public static JList createList(List<ListItem> items) {
		JList list = new JList(new DefaultListModel());
		for (ListItem listItem : items) {
			SwingUtil.add(listItem.name, listItem.defaultAction, list, listItem.commands);
		}
		return list;
	}
	
	public static class ListItem {
		String name;
		P0 defaultAction;
		Object[] commands;
		public ListItem(String name, P0 defaultAction, Object[] commands) {
			this.name = name;
			this.defaultAction = defaultAction;
			this.commands = commands;
		}
		public ListItem(String name, P0 defaultAction) {
			this.name = name;
			this.defaultAction = defaultAction;
		}
	}
}
