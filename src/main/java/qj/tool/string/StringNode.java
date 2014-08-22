package qj.tool.string;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StringNode {
	private String value;
	private List subNodes;
	public List getSubNodes() {
		return subNodes;
	}
	public void setSubNodes(List subNodes) {
		this.subNodes = subNodes;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public List flatUp() {
		List list = new ArrayList();
		list.add(value);
		for (Iterator iterator = subNodes.iterator(); iterator.hasNext();) {
			StringNode node = (StringNode) iterator.next();
			
			list.addAll(node.flatUp());
		}
		return list;
	}

}
