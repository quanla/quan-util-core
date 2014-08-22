package qj.util.cache;

import java.util.HashMap;
import java.util.Map;

import qj.util.funct.F1;

/**
 * 
 * @author QuanLA
 *
 * @param <A> from
 * @param <T> to
 */
public class Cache<A, T> {
	private final F1<A, T> func;

	private Map<A, T> data = new HashMap<A, T>();

	public Cache(F1<A, T> func) {
		this.func = func;
	}
	
	public T get(A a) {
		T t = data.get(a);
		if (t == null) {
			t = func.e(a);
			data.put(a, t);
		}
		return t;
	}

	public Map<A, T> getData() {
		return data;
	}
}
