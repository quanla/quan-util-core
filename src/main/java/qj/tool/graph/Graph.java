package qj.tool.graph;

import java.util.HashMap;
import java.util.LinkedList;

import qj.util.funct.P1;
import qj.util.funct.P2;

public class Graph<V> {
	public P2<V,P2<Double,V>> resolve;
	public static void main(String[] args) {
	}
	
	public void resolveI(final P2<V,P2<Integer,V>> resolve) {
		this.resolve = new P2<V, P2<Double,V>>() {public void e(V val, P2<Double, V> addLink) {
			assert addLink != null;
			resolve.e(val, new P2<Integer, V>() {public void e(Integer weightI, V newV) {
				assert weightI != null;
				addLink.e(weightI.doubleValue(), newV);
			}});
		}};
	}
	
	public Node<V> build(V v) {
		int[] totalLinks = {0};
		LinkedList<Node<V>> unresolveds = new LinkedList<>();
		
		Node<V> firstNode = new Node<>(v);
		unresolveds.add(firstNode);
		
		HashMap<V, Node<V>> nodes = new HashMap<V,Node<V>>();
		
		P1<Node<V>> resolveF = new P1<Node<V>>() {public void e(Node<V> node) {
			LinkedList<Link<V>> links = new LinkedList<>();
			resolve.e(node.val, new P2<Double, V>() {public void e(Double weight, V newV) {
				Node<V> newNode = nodes.get(newV);
				
				if (newNode==null) {
					newNode = new Node<>(newV);
					unresolveds.add(newNode);
					totalLinks[0] ++;
					nodes.put(newV, newNode);
				}
				links.add(new Link<V>(weight, newNode));
			}});
			node.links = links;
		}};
		
		for (;unresolveds.size() > 0;) {
			Node<V> unresolved = unresolveds.removeFirst();
			resolveF.e(unresolved);
		}
		
		return firstNode;
	}
	public double longestPath(Node<V> startNode) {
		return longestPath(startNode, new HashMap<V,Double>());
	}
	public double longestPath(Node<V> startNode, HashMap<V, Double> cache) {
		
		double max = 0;
		for (Link<V> link : startNode.links) {
			Double next = cache.get(link.node.val);
			if (next == null) {
				next = longestPath(link.node, cache);
				cache.put(link.node.val, next);
			}
			max = Math.max(max, link.weight + next);
		}
		return max;
	}
}
