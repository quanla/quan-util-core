package qj.util.math;

import java.util.ArrayList;
import java.util.Date;

import qj.util.Cols;
import qj.util.funct.F1;

public class RangeMap<K extends Comparable<K>, V > {
//	public static void main(String[] args) {
//		RangeMap<Integer, String> map = new RangeMap<Integer, String>();
//		
//		map.put(0, 5, "a");
//		map.put(6, 10, "b");
//		System.out.println("a=" + map.get(0));
//		System.out.println("a=" + map.get(1));
//		System.out.println("a=" + map.get(5));
//		System.out.println("b=" + map.get(6));
//		System.out.println("b=" + map.get(7));
//		System.out.println("b=" + map.get(10));
//		System.out.println("n=" + map.get(11));
//		System.out.println("n=" + map.get(12));
//	}

	ArrayList<Entry<K, V>> entries = new ArrayList<Entry<K,V>>();
	private void put(K kFrom, K kTo, V value) {
		int insertPos = Cols.searchIndexedBinary(entries, Entry.<K,V>fromF(), kFrom);
		insertPos = -insertPos-1;
		
		Entry<K, V> entry = new Entry<K, V>(kFrom, kTo, value);
		entries.add(insertPos, entry);
	}
	
	public V get(K key) {
		int pos = Cols.searchIndexedBinary(entries, Entry.<K,V>fromF(), key);
		if (pos < 0) {
			pos = -pos - 2;
		}
		
		Entry<K, V> entry = entries.get(pos);
		if (entry.to.compareTo(key) >= 0) {
			return entry.value;
		} else {
			return null;
		}
	}
	
	public static class Entry<K extends Comparable<K>, V> {
		K from;
		K to;
		V value;
		
		public Entry(K from, K to, V value) {
			this.from = from;
			this.to = to;
			this.value = value;
		}

		public static <K extends Comparable<K>,V> F1<Entry<K,V>, K> fromF() { return new F1<Entry<K,V>, K>() {public K e(Entry<K, V> obj) {
			return obj.from;
		}};}
	}
}
