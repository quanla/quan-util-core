package qj.util;

import java.util.Collection;
import java.util.HashMap;

import qj.util.funct.F1;

public class Caches {
	public static class Cacher<A,B> {
		HashMap<A, B> map = new HashMap<A, B>();
		public F1<A,B> f;
		public Collection<B> values() {
			return map.values();
		}
	}
	
	public static <A,Val> Cacher<A,Val> cache(final F1<A,Val> f) {
		final Cacher<A, Val> cacher = new Cacher<A, Val>();
		cacher.f = new F1<A, Val>() {public Val e(A obj) {
			Val val = cacher.map.get(obj);
			if (val!=null) {
				return val;
			} else if (cacher.map.containsKey(val)) {
				return null;
			} else {
				val = f.e(obj);
				cacher.map.put(obj, val);
				return val;
			}
		}};
		return cacher;
	}
	public static void main(String[] args) {
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("q", null);
		System.out.println(map.containsKey("q"));
	}
}
