package qj.util.col;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import qj.util.Cols;

public class TreeCache <A> {
	HashMap<A,Collection<Tree<A>>> map = new HashMap<A, Collection<Tree<A>>>();
	public static <V> TreeCache<V> cache(Tree<V> tree) {
		TreeCache<V> treeCache = new TreeCache<V>();
		
		addRecursive(tree, treeCache);
		
		return treeCache;
	}

	private static <V> void addRecursive(Tree<V> tree, TreeCache<V> treeCache) {
		add(tree,treeCache.map);
		
		if (tree.downs != null) {
			for (Tree<V> down : tree.downs) {
				addRecursive(down, treeCache);
			}
		}
	}
	
	public static <V> void add(Tree<V> tree, HashMap<V,Collection<Tree<V>>> map) {
		Collection<Tree<V>> col = map.get(tree.value);
		if (col == null) {
			col = new LinkedList<Tree<V>>();
			map.put(tree.value, col);
		}
		
		col.add(tree);
	}

	public boolean containsAny(A val, Collection<A> provideds) {
		if (Cols.isEmpty(provideds)) {
			return false;
		}
		if (provideds.contains(val)) {
			return true;
		}

		for (A provided : provideds) {
			Collection<Tree<A>> trees = map.get(provided);
			if (trees==null) {
				continue;
			}
			for (Tree<A> tree : trees) {
				if (containsAny(val, tree) ) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean containsAny(A val, Tree<A> tree) {
		// Check self
		if (tree.value.equals(val)) {
			return true;
		}
		// Check ups
		if (checkUp(val, tree)) {
			return true;
		}
		
		// Check downs
		if (checkDown(val, tree)) {
			return true;
		}
		
		return false;
	}

	private boolean checkUp(A val, Tree<A> tree) {
		if (tree.up == null) {
			return false;
		}
		if (tree.up.value.equals(val)) {
			return true;
		}
		
		return checkUp(val,tree.up);
	}

	private boolean checkDown(A val, Tree<A> tree) {
		if (tree.downs == null) {
			return false;
		}
		
		for (Tree<A> down : tree.downs) {
			if (down.value.equals(val)
					|| checkDown(val, down)) {
				return true;
			}
		}
		return false;
	}
}
