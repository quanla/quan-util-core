package qj.util.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import qj.util.Cols;
import qj.util.funct.F1;
import qj.util.graph.TierGraph.Node;

public class TierGraph<T> {
	public ArrayList<Set<Node<T>>> tiers;

	public TierGraph(ArrayList<Set<Node<T>>> tiers) {
		this.tiers = tiers;
	}

	static class Node<T> {
		T val;
		Set<Node<T>> ups = new HashSet<Node<T>>();
		Set<Node<T>> downs = new HashSet<Node<T>>();
		
		public Node(T val) {
			this.val = val;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((val == null) ? 0 : val.hashCode());
			return result;
		}
		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (val == null) {
				if (other.val != null)
					return false;
			} else if (!val.equals(other.val))
				return false;
			return true;
		}
		@Override
		public String toString() {
			return String.valueOf(val);
		}

		public static <T> F1<Node<T>, T> valF() {
			return new F1<Node<T>, T>() {public T e(Node<T> obj) {
				return obj.val;
			}};
		}
	}

	public List<T> getDownward(T... nodes) {
		HashSet<T> queue = new HashSet<T>(Arrays.asList(nodes));
		LinkedList<T> ret = new LinkedList<T>();
		for (Set<Node<T>> set : tiers) {
			for (Node<T> node : set) {
				if (queue.contains(node.val)) {
					queue.remove(node.val);
					queue.addAll(Cols.yield(node.downs, Node.<T>valF()));
					ret.add(node.val);
				}
			}
		}
		ret.addAll(queue);
		return ret;
	}
}
