package qj.tool.graph;

public class Link<V> {
	public Link(double weight, Node<V> node) {
		this.weight = weight;
		this.node = node;
	}
	double weight;
	Node<V> node;
}
