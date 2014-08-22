package qj.util.col;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import qj.util.MathUtil;
import qj.util.funct.F0;
import qj.util.funct.F1;
import qj.util.funct.Fs;
import qj.util.funct.P1;

@SuppressWarnings("unchecked")
public class KeyedBinaryTree<K extends Comparable<K>,V> 
		implements Collection<V> {
	private F1<V, K> keyF;
	Node root;
	class Node {
		V value;
		Node(Node top, V value) {
			this.top = top;
			this.value = value;
		}
		Node top;
		Node left;
		Node right;
	}

	public KeyedBinaryTree(F1<V,K> keyF) {
		this.keyF = keyF;
	}
	
	public static void main(String[] args) {
		KeyedBinaryTree<String, String> tree = new KeyedBinaryTree<String, String>(Fs.<String>f1());
		tree.add("d");
		
		
		
		for (String string : tree) {
			System.out.println("AA " + string);
		}
		System.out.println(tree.size());
		
	}
	
	void eachValue(P1<V> p1) {
		if (root == null) {
			return;
		}
		eachValue(root, p1);
	}
	void eachValue(Node node, P1<V> p1) {
		if (node.left != null) {
			eachValue(node.left, p1);
		}
		p1.e(node.value);
		
		if (node.right != null) {
			eachValue(node.right, p1);
		}
	}

	@Override
	public int size() {
		F0<Integer> counter = MathUtil.counter(-1);
		eachValue(Fs.<V>p1(Fs.p0(counter)));
		return counter.e();
	}

	@Override
	public boolean isEmpty() {
		return root == null;
	}

	@Override
	public boolean contains(Object o) {
		return findNode(keyF.e((V)o)) != null;
	}

	@Override
	public Iterator<V> iterator() {
		final AtomicReference<Node> nodeRef = new AtomicReference<Node>(leftMostNode(root));
		return new Iterator<V>() {

			@Override
			public boolean hasNext() {
				return nodeRef.get() != null;
			}

			@Override
			public V next() {
				Node node = nodeRef.get();
				V value = node.value;
				nodeRef.set( nextNode(node) );
				return value;
			}

			@Override
			public void remove() {
				throw new RuntimeException("Unsupported");
			}
		};
	}

	private Node leftMostNode(Node node) {
		if (node.left != null) {
			return leftMostNode(node.left);
		} else {
			return node;
		}
	}
	private Node nextNode(Node node) {
		if (node.right != null) {
			return node.right;
		} else if (node.top != null) {
			return node.top;
		} else {
			return null;
		}
	}

	@Override
	public boolean add(V e) {
		// TODO Batch balance
		if (root == null) {
			root = new Node(null, e);
			return true;
		} else {
			add(e, root);
			return true;
		}
		
	}

	private void add(V e, Node node) {
		int compareTo = keyF.e(e).compareTo(keyF.e(node.value));
		if (compareTo < 0) {
			if (node.left == null) {
				node.left = new Node(node, e);
			} else {
				add(e, node.left);
			}
		} else if (compareTo == 0) {
			throw new RuntimeException("Key already existed [" + keyF.e(e) + "]");
		} else {
			if (node.right == null) {
				node.right = new Node(node, e);
			} else {
				add(e, node.right);
			}
		}
	}

	@Override
	public boolean remove(Object o) {
		Node node = findNode(keyF.e((V)o));
		if (node != null) {
			removeNode(node);
		}
		return true;
	}

	private void removeNode(Node node) {
		// TODO Auto-generated method stub
		
	}

	private Node findNode(K key) {
		if (root == null) {
			return null;
		}
		return findNode(key, root);
	}

	private Node findNode(K key, Node node) {
		int compareTo = key.compareTo(keyF.e(node.value));
		if (compareTo < 0) {
			if (node.left == null) {
				return null;
			} else {
				return findNode(key, node.left);
			}
		} else if (compareTo == 0) {
			return node;
		} else {
			if (node.right == null) {
				return null;
			} else {
				return findNode(key, node.right);
			}
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends V> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		root = null;
		root.top = null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}
}
