package qj.util.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import qj.util.Cols;
import qj.util.graph.TierGraph.Node;

public class TierGraphBuilder<T> {
	HashMap<T, Collection<T>> links = new HashMap<T, Collection<T>>();
	
	public static void main(String[] args) {
		TierGraphBuilder<String> builder = new TierGraphBuilder<String>();
		builder.link("A", 
				"C","E");
		builder.link("C", 
				"D");
		builder.link("D", 
				"E");
		
		TierGraph<String> graph = builder.build();
		System.out.println(graph.getDownward("A"));
	}

	public TierGraph<T> build() {
		HashMap<T, Node<T>> index = buildIndex();
		
		HashSet<Node<T>> waitings = new HashSet<Node<T>>(index.values());
		
		ArrayList<Set<Node<T>>> tiers = new ArrayList<Set<Node<T>>>();
		for (;;) {
			Set<Node<T>> nextTier = buildNextTier(waitings, tiers);
			if (nextTier.isEmpty()) {
				if (!waitings.isEmpty()) {
					throw new RuntimeException("These nodes have cross references: " + waitings.toString());
				} else {
					break;
				}
			}
			tiers.add(nextTier);
		}
		
//		System.out.println(Cols.toString(tiers));
		
		return new TierGraph<T>(tiers);
	}

	private Set<Node<T>> buildNextTier(HashSet<Node<T>> waitings,
			ArrayList<Set<Node<T>>> tiers) {
		HashSet<Node<T>> ret = new HashSet<Node<T>>();
		for (Node<T> node : new HashSet<Node<T>>(waitings)) {
			if (containsAll(node.ups, tiers)) {
				ret.add(node);
				waitings.remove(node);
			}
		}
		return ret;
		
	}

	private boolean containsAll(Set<Node<T>> ups, ArrayList<Set<Node<T>>> tiers) {
		if (ups.isEmpty()) {
			return true;
		}
		if (tiers.isEmpty()) {
			return false;
		}
		nextNode:
		for (Node<T> node : ups) {
			for (Set<Node<T>> tier : tiers) {
				if (tier.contains(node)) {
//					System.out.println("Node is contained: " + node + " in tier: " + tier);
					continue nextNode;
				}
			}
			return false;
		}
		return true;
	}

	private HashMap<T, Node<T>> buildIndex() {
		HashMap<T, Node<T>> index = new HashMap<T, Node<T>>();
		for (Entry<T, Collection<T>> entry : links.entrySet()) {
			Node<T> nodeUp = get(entry.getKey(), index);
			for (T down : entry.getValue()) {
				Node<T> nodeDown = get(down, index);
				link(nodeUp, nodeDown);
			}
		}
		return index;
	}

	private void link(Node<T> nodeUp, Node<T> nodeDown) {
		nodeUp.downs.add(nodeDown);
		nodeDown.ups.add(nodeUp);
	}

	private Node<T> get(T key, HashMap<T, Node<T>> index) {
		Node<T> node = index.get(key);
		if (node == null) {
			node = new Node<T>(key);
			index.put(key, node);
		}
		
		return node;
	}

	public void link(T up, T... downs) {
		links.put(up, Arrays.asList(downs));
	}
}
