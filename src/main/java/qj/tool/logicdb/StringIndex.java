package qj.tool.logicdb;

import java.util.ArrayList;
import java.util.List;

import qj.util.Cols;
import qj.util.funct.F1;
import qj.util.funct.P1;

public class StringIndex<V> {
	public F1<V, String> keyF;
	List<V> list = new ArrayList<V>();

	public StringIndex(F1<V, String> keyF) {
		this.keyF = keyF;
	}

	public void accept(V value) {
		String key = keyF.e(value);
		int index = Cols.searchIndexedBinary(list, keyF, key);
		// Index always < 0 or duplicates
		if (index>-1) {
//			throw new RuntimeException("Existed");
			return;
		}
		list.add(-index-1, value);
	}

	public V get(String key) {
		int index = Cols.searchIndexedBinary(list, keyF, key);

		if (index>-1) {
			return list.get(index);
		}
		return null;
	}
	
	public F1<String,P1<P1<V>>> loopF = new F1<String, P1<P1<V>>>() {
		public P1<P1<V>> e(final String strStartWith) {
			return new P1<P1<V>>() {public void e(P1<V> p) {
				// Loop
				int index = Cols.searchIndexedBinary(list, keyF, strStartWith);
				if (index<0) {
					index = -index -1;
				}
				for (int i = index; i < list.size(); i++) {
					V v = list.get(i);
					if (!keyF.e(v).startsWith(strStartWith)) {
						return;
					}
					p.e(v);
				}
			}};
		}
	};
}