package qj.util.cache;

import java.util.ArrayList;

import qj.util.funct.F2;

public class Cache2<A, B, T> {
	private final F2<A, B, T> func;

	ArrayList<Holder> holders = new ArrayList<Holder>();

	public Cache2(F2<A, B, T> func) {
		this.func = func;
	}
	
	public T get(A a, B b) {
		for (Holder holder : holders) {
			if (holder.a.equals(a)
					&& holder.b.equals(b)) {
				return holder.t;
			}
		}
		
		T t = func.e(a, b);
		holders.add(new Holder(a, b, t));
		return t;
	}
	
	private class Holder {
		A a;
		B b;
		T t;
		public Holder(A a, B b, T t) {
			this.a = a;
			this.b = b;
			this.t = t;
		}
	}
}
