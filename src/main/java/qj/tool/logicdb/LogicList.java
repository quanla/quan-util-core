package qj.tool.logicdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import qj.util.funct.F1;

public class LogicList <A> {
//	private final Class<A> eleClass;

	HashMap<Object, List<A>> allLists = new HashMap<Object, List<A>>();
	{
		allLists.put(null, new ArrayList<A>());
	}
	
	@SuppressWarnings("rawtypes")
	HashMap<Class, StringIndexHolder<A>> stringIndexes = new HashMap<Class, StringIndexHolder<A>>();
	private final F1<A, Object> keyF;
	
	public LogicList(F1<A, Object> keyF) {
		this.keyF = keyF;
	}

	public void cacheSeparate(Object tag) {
		allLists.put(tag, new ArrayList<A>());
	}

	public <R> void indexString(Class<R> class1, final F1<R, String> stringF) {
		stringIndexes.put(class1, 
				new StringIndexHolder<A>(
					new StringIndex<A>(new F1<A, String>() {
						@SuppressWarnings("unchecked")
						public String e(A obj) {
							return stringF.e((R) keyF.e(obj));
						}
					}),
					stringF
				)
		);
	}


	public void add(A record) {
		getList(keyF.e(record)).add(record);
		
		index(record);
	}

	public List<A> getList(Object marker) {
		if (allLists.containsKey(marker)) {
			return allLists.get(marker);
		} else if (allLists.containsKey(marker.getClass())) {
			return allLists.get(marker.getClass());
		}
		return allLists.get(null);
	}

	private void index(A record) {
		StringIndexHolder<A> stringIndex = stringIndexes.get(keyF.e(record).getClass());
		if (stringIndex!= null) {
			stringIndex.index.accept(record);
		}
	}

	@SuppressWarnings("unchecked")
	public A get(Object key) {

		StringIndexHolder<A> stringIndex = stringIndexes.get(key.getClass());
		if (stringIndex!=null) {
			// Use index
			return stringIndex.index.get((String) stringIndex.keyToString.e(key));
		}
		
		F1<Object, Boolean> t = LogicList.tagCond(key);
		for (A v : getList(key)) {
			if (t.e(keyF.e(v))) {
				return v;
			}
		}
		return null;
	}

	// TODO 
	@SuppressWarnings("unchecked")
	public A get(Class<?> clazz, Object range) {
		List<A> list = getList(clazz);
		F1<Object, Boolean> f = LogicList.tagCond(clazz);
		
//		Cols.eachZeroDistances(null, null, null)
		
//		
//		for (int i = list.size() - 1; i > -1; i--) {
//			A tagRange = list.get(i);
//			if (pos < tagRange.range.getFrom()
//					|| (tagRange.range.getTo() != null && pos > tagRange.range.getTo())) {
//				continue;
//			}
//			if (f.e(tagRange.tag)) {
//				return (A) tagRange.tag;
//			}
//		}
		return null;
	}

	public void separateList(Object tag) {
		allLists.put(tag, new ArrayList<A>());
	}



	public static F1<Object, Boolean> equalsF(final Object markupCond) {
		return new F1<Object, Boolean>() {public Boolean e(Object targetMarkup) {
			return targetMarkup.equals(markupCond);
		}};
	}

	public static F1<Object, Boolean> tagCond(final Object markupCond) {
		F1<Object, Boolean> t;
		if (markupCond instanceof Class) {
			final Class<?> clazz = (Class<?>) markupCond;
			t = new F1<Object, Boolean>() {public Boolean e(Object targetMarkup) {
				return clazz.isInstance(targetMarkup);
			}};
		} else {
			t = LogicList.equalsF(markupCond);
		}
		return t;
	}

}
