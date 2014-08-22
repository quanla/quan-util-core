package qj.tool.path;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import qj.util.Cols;
import qj.util.funct.Douce;
import qj.util.funct.F1;
import qj.util.funct.P1;


public class Node1<V> {
	public LinkedList<Node1<V>> next;
	public LinkedList<Node1<V>> prev;
	
	public static <V> F1<Node1<V>, V> valF() {return new F1<Node1<V>, V>() {public V e(Node1<V> obj) {
		return obj.val;
	}};}
	
	@SuppressWarnings("unchecked")
	public static <V> F1<Node1<V>, Iterable<Node1<V>>> surroundF() { return new F1<Node1<V>, Iterable<Node1<V>>>() {public Iterable<Node1<V>> e(Node1<V> obj) {
		return Cols.sequence(obj.next, obj.prev);
	}};}

	public V val;
	public Node1(V val) {
		this.val = val;
	}
	public void addNext(Node1<V> next) {
		this.next = (LinkedList<Node1<V>>) Cols.addList(next, this.next);
		next.prev = (LinkedList<Node1<V>>) Cols.addList(this, next.prev);
	}
	
	public static <V> void cut(Node1<V> node1, Node1<V> node2) {
		if (Cols.isNotEmpty(node1.next) && node1.next.remove(node2)) {
			node2.prev.remove(node1);
		} else if (Cols.isNotEmpty(node1.prev) && node1.prev.remove(node2)) {
			node2.next.remove(node1);
		}
	}
	
	/**
	 * @param targetNodes
	 * @return 
	 */
	public static <V> List<Node1<V>> cutEast(Collection<Node1<V>> targetNodes) {
		F1<Node1<V>, Collection<Node1<V>>> forwardF = Node1.<V>nextF();
		
		return cutPack(targetNodes, forwardF);
	}
	
	/**
	 * @param targetNodes
	 * @return 
	 */
	public static <V> List<Node1<V>> cutWest(Collection<Node1<V>> targetNodes) {
		F1<Node1<V>, Collection<Node1<V>>> forwardF = Node1.<V>prevF();
		
		return cutPack(targetNodes, forwardF);
	}
	private static <V> List<Node1<V>> cutPack(Collection<Node1<V>> targetNodes,
			F1<Node1<V>, Collection<Node1<V>>> forwardF) {
		// Identify pairs
		Collection<Douce<Node1<V>, Node1<V>>> pairs = identifyForeigns(targetNodes, forwardF);
		
		// Cut them
		for (Douce<Node1<V>, Node1<V>> pair : pairs) {
			cut(pair.get1(), pair.get2());
		}
		
		// Return
		return Cols.yield(pairs, Douce.<Node1<V>,Node1<V>>get2F());
	}
	
	public static <V> Collection<Douce<Node1<V>,Node1<V>>> identifyForeigns(
			Collection<Node1<V>> nodes, 
			F1<Node1<V>,Collection<Node1<V>>> forwardF) {
		LinkedList<Douce<Node1<V>, Node1<V>>> ret = new LinkedList<Douce<Node1<V>,Node1<V>>>();
		LinkedList<Node1<V>> pointerToOris = new LinkedList<Node1<V>>( nodes );
		
		for (Node1<V> node : nodes) {
			Collection<Node1<V>> forwards = forwardF.e(node);
			if (forwards==null) continue;
			
			for (Node1<V> node1 : forwards) {
				if (!Cols.containsPointer(node1, pointerToOris)) {
					// This is a foreign node
					ret.add(new Douce<Node1<V>, Node1<V>>(node, node1));
				}
			}
		}
		return ret;
	}
	
	
	public static class RemoveResult {
		
	}
	
	public void remove() {
		// Remove this from this.next's prev and this.prev's next
		Cols.each(this.next, new P1<Node1<V>>() {public void e(Node1<V> next) {
			next.prev.remove(Node1.this);
		}});
		Cols.each(this.prev, new P1<Node1<V>>() {public void e(Node1<V> prev) {
			prev.next.remove(Node1.this);
		}});
		
		// Clear this's next and prev
		if (Cols.isNotEmpty(this.next)) this.next.clear();
		if (Cols.isNotEmpty(this.prev)) this.prev.clear();
	}

	public static <V> F1<Node1<V>,Collection<Node1<V>>> nextF() { return new F1<Node1<V>, Collection<Node1<V>>>() {public Collection<Node1<V>> e(Node1<V> obj) {
		return obj.next;
	}};}
	public static <V> F1<Node1<V>,Collection<Node1<V>>> prevF() { return new F1<Node1<V>, Collection<Node1<V>>>() {public Collection<Node1<V>> e(Node1<V> obj) {
		return obj.prev;
	}};}
	@Override
	public String toString() {
		return "Node1 (" + val + ")";
	}
	/**
	 * 
	 * @param list
	 * @return The first node
	 */
	public static <V> Node1<V> sew(List<Node1<V>> list) {
		Node1<V> start = null;
		Node1<V> prev = null;
		for (Node1<V> node : list) {
			if (start == null) {
				start = node;
			}
			if (prev != null) {
				prev.addNext(node);
			}
			prev = node;
		}
		return start;
	}
	public static <V> LinkedList<Node1<V>> toEnd(Node1<V> node) {
		LinkedList<Node1<V>> ret = new LinkedList<Node1<V>>();
		LinkedList<Node1<V>> visiteds = new LinkedList<Node1<V>>();
		
		
		LinkedList<Node1<V>> stack = new LinkedList<Node1<V>>();
		stack.add(node);
		
		while (true) {
			if (stack.isEmpty()) {
				break;
			}
			node = stack.removeFirst();
			while (true) {
				if (Cols.containsPointer(node, visiteds)) {
					break;
				}
				visiteds.add(node);
				if (Cols.isEmpty(node.next)) {
					ret.add(node);
					break;
				}
				if (node.next.size() > 1) {
					stack.addAll(node.next.subList(1, node.next.size()));
				}
				node = Cols.getSingle(node.next);
			}
		}
		return ret;
	}
	public static <V> Collection<Node1<V>> filter(Collection<Node1<V>> nodes, final F1<Node1<V>, Boolean> filterF) {
		final LinkedList<Node1<V>> visiteds = new LinkedList<Node1<V>>();
		
		final LinkedList<Node1<V>> ret = new LinkedList<Node1<V>>();
		for (Node1<V> node : nodes) {
			each(node,Node1.<V>nextF(), new F1<Node1<V>,Boolean>() {public Boolean e(Node1<V> node) {
				if (Cols.containsPointer(node, visiteds)) {
					return true;
				}
				visiteds.add(node);
				if (filterF.e(node)) {
					ret.add(node);
				}
				return false;
			}});
			
		}
		return ret;
	}
	public static <V> Boolean each(Node1<V> nodeStart, final F1<Node1<V>,Collection<Node1<V>>> digF, final F1<Node1<V>,Boolean> p) {
		p.e(nodeStart);
		Iterable<Node1<V>> iter = digF.e(nodeStart);
		if (iter==null) {
			return false;
		}
		return Cols.each(iter, new F1<Node1<V>,Boolean>() {public Boolean e(Node1<V> obj) {
			return each(obj, digF, p);
		}});
	}
	public void addNextAll(List<Node1<V>> nodes) {
		for (Node1<V> node : nodes) {
			addNext(node);
		}
	}
	public static <V> void addNextAll(List<Node1<V>> froms, List<Node1<V>> tos) {
		for (Node1<V> from : froms) {
			for (Node1<V> to : tos) {
				from.addNext(to);
			}
		}
	}
	public static <V> void replaceNodes(List<Node1<V>> from,
			List<Node1<V>> to) {
		List<Node1<V>> westNodes = cutWest(from);
		List<Node1<V>> eastNodes = cutEast(from);
		
		sew(to);
		
		for (Node1<V> westNode : westNodes) {
			westNode.addNext(to.get(0));
		}
		
		to.get(to.size() - 1).addNextAll(eastNodes);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((val == null) ? 0 : val.hashCode());
		return result;
	}
}