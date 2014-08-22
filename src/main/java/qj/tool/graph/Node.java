package qj.tool.graph;

import java.util.Collection;

public class Node <V> {
	V val;
	Collection<Link<V>> links;
	public Node(V val) {
		this.val = val;
	}
}
