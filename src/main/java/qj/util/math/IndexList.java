package qj.util.math;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import qj.util.funct.F1;

public class IndexList<A> {
	HashMap<String, F1<A, Object>> indexFs = new HashMap<String,F1<A,Object>>();
	HashMap<String, Map<Object, A>> indice = new HashMap<String,Map<Object,A>>();
	LinkedList<A> list = new LinkedList<A>();
	
	public void addIndex(String name, F1<A,Object> indexF) {
		indexFs.put(name, indexF);
		indice.put(name, new HashMap<Object, A>());
	}
	
	public void add(A a) {
		
		for (Entry<String, F1<A, Object>> entry : indexFs.entrySet()) {
			String index = entry.getKey();
			Object key = entry.getValue().e(a);
			
			A put = indice.get(index).put(key, a);
			if (put != null) {
				list.remove(put);
			}
		}
		
		list.add(a);
	}
	
	public boolean remove(A a) {
		boolean ret = list.remove(a);

		if (ret) {
			for (Entry<String, F1<A, Object>> entry : indexFs.entrySet()) {
				String index = entry.getKey();
				Object key = entry.getValue().e(a);
				
				indice.get(index).remove(key);
			}
		}
		return ret;
	}
	
	public A get(String index, Object key) {
		return indice.get(index).get(key);
	}
	
	public List<A> getList() {
		return new LinkedList<A>(list);
	}

	public void clear() {
		for (String key : indice.keySet()) {
			indice.get(key).clear();
		}
		list.clear();
	}
}
