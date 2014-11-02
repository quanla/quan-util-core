package qj.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import qj.util.cache.Cache;
import qj.util.funct.Douce;
import qj.util.funct.F0;
import qj.util.funct.F1;
import qj.util.funct.F2;
import qj.util.funct.Fs;
import qj.util.funct.P0;
import qj.util.funct.P1;
import qj.util.funct.P2;
import qj.util.math.PointD;
import qj.util.math.Range;

/**
 * Created by QuanLA
 * Date: Mar 2, 2006
 * Time: 9:10:49 AM
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class Cols {
	public static final F1<Collection, Integer> sizeF = new F1<Collection, Integer>() {public Integer e(Collection obj) {
		return obj.size();
	}};

	public static <A> F0<A> safeIterator(Collection<A> col) {
		final Iterator<A> iterator = col.iterator();
		
		return new F0<A>() {
			public A e() {
				if (iterator.hasNext()) {
					return iterator.next();
				}
				return null;
			}
		};
	}
	public static interface RandomAccessCol<A> {
		A get(int i);
		int size();
//		void accept(int i);
	}
	
	public static <A,B> RandomAccessCol<A> randomAccessCol(final List<B> list, final F1<B,A> f) {
		return new RandomAccessCol<A>() {
			public A get(int i) {
				return f.e(list.get(i));
			}
			public int size() {
				return list.size();
			}
		};
		
	}

	/**
	 * 
	 * @param distanceF
	 * @param col
	 * @param acceptF return true when ok to stop
	 */
	public static void eachZeroDistances(final F1<Range, Integer> distanceF,
			final RandomAccessCol<Range> col, F1<Integer, Boolean> acceptF) {
		// Binary search

		F1<Integer, Integer> disF = disF(distanceF, col);
		
		int foundIndex = search(col.size(), disF);
		
		if (foundIndex == -1) {
			return;
		}
		
		// Found 1 sample, expand selection to start and end
		if (acceptF.e(foundIndex)) return;
		
//		if (foundIndex > chooser.size()) {
//			throw new RuntimeException();
//		}
		
//		int countDown = 3; // Sometimes sorting not work well with overlapping ranges
		
		// back
		for (int i = foundIndex -1; i > -1; i--) {
			if (disF.e(i) == 0) {
				if (acceptF.e(i)) return;
			} else { // if (countDown-- == 0) {
				break;
			}
		} 
		
//		countDown=3;
		// Forward
		for (int i = foundIndex + 1; i < col.size(); i++) {
			if (disF.e(i) == 0) {
				if (acceptF.e(i)) return;
			} else {//if (countDown-- == 0) {
				break;
			}
		}
	}

	public static F1<Integer, Integer> disF(final F1<Range, Integer> distanceF,
			final RandomAccessCol<Range> col) {
		F1<Integer,Integer> disF = new F1<Integer, Integer>() {public Integer e(Integer index) {
			return distanceF.e(col.get(index));
		}};
		return disF;
	}

	public static int search(int size, F1<Integer, Integer> disF) {
		int index = -1;
		for (int from = 0, to = size; ; ) {
			if (to<=from) return -1;
			
			index = ((to-from) / 2) + from;
			Integer result = disF.e(index);
			
			if (result== 0) {
				// found
				break;
			} else if (result < 0) { // We are behind
				from = index + 1;
			} else { // result > 0
				to = index;
			}
		}
		return index;
	}
	
	public static <A,B extends Comparable<B>> List<A> sort(List<A> list, final F1<A,B> keyF ) {
		if (keyF==null) {
			return list;
		}
		Collections.sort(list, new Comparator<A>() {public int compare(A a1, A a2) {
			return keyF.e(a1).compareTo(keyF.e(a2));
		}});
		return list;
	}

//	FOOL
//	
//	public static Object getKeyEquals(Object key, Map map) {
//		for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
//			Object keyInMap = (Object) iter.next();
//			
//			System.out.println("Checking key " + keyInMap);
//			
//			if (keyInMap.equals(key)) {
//				System.out.println("Found equals");
//				return map.get(keyInMap);
//			}
//		}
//		return null;
//	}

    /**
     * Test if the function return the same result on all collection's elements
     * @param col The collection which has elements tested
     * @param f The function to be invoked on each element
     * @param <A> Element's class
     * @param <B> Element's tested attribute class
     * @return true if all element return same value
     */
	public static <A, B> boolean same(Collection<A> col, F1<A, B> f) {
		boolean firstElem = true;
		B value = null;
		for (A a : col) {
			if (firstElem) {
				value = f.e(a);
				firstElem = false;
			} else {
				if (ObjectUtil.notEquals(value, f.e(a))) {
					return false;
				}
			}
		}
		return true;
	}

//    @SuppressWarnings({"unchecked"})
    public static <A> A[] asArray(Collection<A> aCol, Class<A> clazz) {
        A[] as = (A[]) Array.newInstance(clazz, aCol.size());
        int i = 0;
        for (A a : aCol) {
            as[i++] = a;
        }
        return as;
    }
    
    

	public static <A> List<A> toList(Collection<A> objects) {
        List<A> list;
        if (objects instanceof List) {
            list = (List<A>) objects;
        } else {
            list = new ArrayList<A>(objects);
        }
        return list;
    }
	
	public static <A> List<A> toList(Iterable<A> iter) {
		LinkedList<A> ret = new LinkedList<A>();
		for (A a : iter) {
			ret.add(a);
		}
		return ret;
	}

    public static <A> A find(A[] as, F1<A, Boolean> f1) {
        if (as == null) {
            return null;
        }
        for (A a: as) {
            if (f1.e(a)) {
                return a;
            }
        }
        return null;
    }

    public static <A> List<A> get(List<A> list, int... indexes) {
        return get(asList(indexes), list);
    }
    public static <A> List<A> get(Collection<Integer> indexes, List<A> list) {
        ArrayList<A> ret = new ArrayList<A>();
        for (Integer index : indexes) {
            ret.add(list.get(index));
        }
        return ret;
    }

    public static <A> List<Collection<A>> divide(Collection<A> col, int maxDividedNum, int minEleNum) {
        int eleNum;
        if (col.size() <= maxDividedNum * minEleNum) {
            eleNum = minEleNum;
        } else {
            eleNum = (int) Math.ceil((double)col.size() / maxDividedNum);
        }

        ArrayList<Collection<A>> ret = new ArrayList<Collection<A>>(maxDividedNum);
        LinkedList<A> feeder = new LinkedList<A>(col);

        while (!feeder.isEmpty()) {
            ArrayList<A> tempCol = new ArrayList<A>(eleNum);
            for (int i = 0; i < eleNum && !feeder.isEmpty(); i++) {
                tempCol.add(feeder.removeFirst());
            }
            ret.add(tempCol);
        }
        return ret;
    }

    public static <A> F1<A, Boolean> byClass(final Class<?> clazz) {
        return new F1<A, Boolean>() {public Boolean e(A obj) {
            return clazz.isAssignableFrom(obj.getClass());
        }};
    }

//    @SuppressWarnings("unchecked")
    public static <A, R> R find(A[] arr, Class<R> clazz) {
        return (R) find(arr, byClass(clazz));
    }

	public static <A> List<A> sort(Collection<A> col, final String attr) {
		return sort(col, attr, false);
	}
	public static <A> List<A> sortReverse(Collection<A> col, final String attr) {
		return sort(col, attr, true);
	}
//	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <A> List<A> sort(Collection<A> col, final String attr, final boolean desc) {
		if (col==null) {
			return null;
		}
		ArrayList<A> list = new ArrayList<A>(col);
		if (list.size() < 2) {
			return list;
		}
		
		final F1<A,Comparable> getter;
		if (Map.class.isInstance(list.get(0))) {
			getter = new F1<A, Comparable>() {public Comparable e(A obj) {
				return (Comparable) ((Map)obj).get(attr);
			}};
		} else {
			Class<? extends Object> clazz = list.get(0).getClass();
			
			final Field field = ReflectUtil.getField(attr, clazz);
			getter = new F1<A,Comparable>() {public Comparable e(A obj) {
				return (Comparable) ReflectUtil.getFieldValue(field, obj);
			}};
		}
		
		Collections.sort(list, new Comparator<A>() {
			public int compare(A a, A b) {
				return getter.e(a).compareTo(getter.e(b)) * (desc ? -1 : 1);
			}
		});
		return list;
	}
	
	public static <C extends Iterable<E>,E> P2<C, P1<E>> eachF() {
		return new P2<C, P1<E>>() {public void e(C a, P1<E> b) {
			each(a, b);
		}};
	}
	/**
	 * Apply function on each collection's element
	 * @param <E> Element type
	 * @param col Collection
	 * @param proc to run on each col's element, return true to interrupt
	 */
	public static <E> boolean each(Iterable<E> col, F1<E,Boolean> proc) {
		if (col==null) {
			return false;
		}
		for (E e : col) {
			if (proc.e(e)) {
				return true;
			}
		}
		return false;
	}
	public static <E> void each(Iterable<E> col, P1<E> proc) {
		each(col, Fs.<E, Boolean>f1(proc, false));
	}

    /**
     * 
     * @param list
     */
	public static void removeDuplicates(List<?> list) {
		for (int i = list.size() - 1; i > -1; i--) {
			Object o = list.get(i);
			if (list.indexOf(o)!=i) {
				list.remove(i);
			}
		}
	}

	public static <A> List<A> removeDuplicates(
			List<A> list,
			F1<A, List<Object>> extract) {
		LinkedList<A> removeds = new LinkedList<A>();
		
		List<List<Object>> extracteds = new LinkedList<List<Object>>();
		for (Iterator<A> iterator = list.iterator(); iterator.hasNext();) {
			A a = iterator.next();
			List<Object> extracted = extract.e(a);
			if (!extracteds.contains(extracted)) {
				extracteds.add(extracted);
			} else {
				removeds.add(a);
				iterator.remove();
			}
		}
		return removeds;
	}
    
    /**
     * Copy content of the list to another list, this new list is not thread-safe and editable.
     * @param oList - The list to be copied
     * @return new List with old content, this new list is not thread-safe and editable.
     */
    public static <A> List<A> clone (List<A> oList) {
    	ArrayList<A> nList = new ArrayList<A>();

        for (A a : oList) {
            nList.add(a);
        }
    	
    	return nList;
    }
    


    /**
     * This will fill in the array with list's items
     * @param array - The target array
     * @param list - List of original objects
     * @param offset - From this pos
     */
  	public static void fillArray(Object[] array, List<?> list, int offset) {
  		int length = Math.min(array.length - offset, list.size());
  		for (int i = 0; i < length; i++) {
			array[i + offset] = list.get(i);
		}
  	}
  	
  	/**
  	 * Cut down the list to specified length
  	 * @param cacheList
  	 * @param limit
  	 */
	public static void setLength(List<?> cacheList, int limit) {
		for (int i = cacheList.size() - 1; i > limit - 1; i--) {
			cacheList.remove(i);
		}
	}
	
	/**
	 * String representation of the collection
	 * @param col the Collection
	 * @return String representation of the collection
	 */
	public static String toString(Iterable<?> col) {
        return join(col, "\n");
	}

	public static <A> void assign(A[] bs, F1<A, A> f1) {
		for (int i = 0; i < bs.length; i++) {
			bs[i] = f1.e(bs[i]);
		}
	}

	/**
	 * Apply function on every elements to get new collection of returned value
	 * @param <A> Source element type
	 * @param <T> Returned element type
	 * @param col Source array
	 * @param f1
	 * @return
	 */
	public static <A,T> List<T> yield(A[] col, F1<A, T> f1) {
		ArrayList<T> list = new ArrayList<T>();
		return yield(col, list, f1);
	}

	public static <A,T> List<T> yield(List<A> col, F1<A, T> f1) {
        if (col!=null) {
            return yield(col, new ArrayList<T>(col.size()), f1);
        } else {
            return null;
        }
    }

    public static <A,R> List<R> yield(Iterable<A> col, F1<A, R> f1) {
		return yield(col, new ArrayList<R>(), f1);
    }
	public static <A,R> F1<Iterable<A>,List<R>> yieldF(final F1<A,R> f1) {
		return new F1<Iterable<A>, List<R>>() {public List<R> e(Iterable<A> obj) {
			return yield(obj, f1);
		}};
	}

	
	/**
	 * Apply function on every elements to get new collection of returned value
	 * @param <A>
	 * @param <T>
	 * @param <C>
	 * @param inputs
	 * @param col
	 * @param f1
	 * @return
	 */
	public static <A,T,C extends Collection<T>> C yield(Iterable<A> inputs, C col, F1<A, T> f1) {
//		ArrayList<T> list = new ArrayList<T>();
		if (inputs!=null) {
            for (A a : inputs) {
                T e = f1.e(a);
				if (e != null) {
					col.add(e);
				}
            }
		}
		return col;
	}

	public static <A,T,C extends Collection<T>> C yield(A[] inputs, C col, F1<A, T> f1) {
		if (inputs != null) {
            for (A a : inputs) {
                T evaluate = f1.e(a);
                if (evaluate != null) {
                    col.add(evaluate);
                }
            }
		}
		return col;
	}

//	@SuppressWarnings("unchecked")
	public static <A, T> Map<A, Collection<T>> group(Collection<T> col, String groupBy) {
		F1<T,A> groupF = new F1<T,A>() {public A e(T obj) {
			return (A) BeanUtil4.getAttribute(groupBy, obj);
		}};
		
		return group(col, groupF);
	}

	public static <T, A> Map<A, Collection<T>> group(Collection<T> col,
			F1<T, A> groupF) {
		F1<A, Collection<T>> cacheCreation = obj -> new ArrayList<T>();
		Cache<A, Collection<T>> indiceCache = new Cache<>(cacheCreation);
		
		// Index
		for (T object : col) {
			A index = groupF.e(object);
			
			indiceCache.get(index).add(object);
		}
        return indiceCache.getData();
	}


	public static <A, T, M> Map<M, T> yieldValues(Map<M, A> map,
			F1<A, T> function1) {
		HashMap<M, T> result = new HashMap<M, T>(map.size());
		for (Map.Entry<M, A> entry : map.entrySet()) {
			result.put(entry.getKey(), function1.e(entry.getValue()));
		}
		return result;
	}


	public static <A, B> List<List<A>> flatten(List<B> tree,
			F1<B, List<B>> nodeQuerier,
			F1<B, A> presentationQuerier) {
		ArrayList<List<A>> result = new ArrayList<List<A>>();
		for (B b : tree) {
			result.addAll(flatten(b, nodeQuerier, presentationQuerier));
		}
		return result;
	}

	/**
	 * nodeQuerier returns null: leaf
	 * else: branch
	 * @param <A>
	 * @param <B>
	 * @param node
	 * @param nodeQuerier
	 * @param presentationQuerier
	 * @return
	 */
	public static <A, B> List<List<A>> flatten(B node,
			F1<B, List<B>> nodeQuerier,
			F1<B, A> presentationQuerier) {
		A a = presentationQuerier.e(node);
		List<B> bList = nodeQuerier.e(node);
		if (bList != null) {
			List<List<A>> flatten = flatten(bList, nodeQuerier,
					presentationQuerier);
			for (List<A> list : flatten) {
				list.add(0, a);
			}
			return flatten;
		} else {
			ArrayList<List<A>> list1 = new ArrayList<List<A>>();
			ArrayList<A> list2 = new ArrayList<A>();
			list2.add(a);
			list1.add(list2);
			return list1;
		}
	}

    public static Querier querier(Collection<String> strings) {
        final List<String> strList = toList(strings);
        return new Querier() {
            public void through(P1<String> f) {
                for (String string : strList) {
                    f.e(string);
                }
            }

            public String get(int i) {
                return strList.get(i);  //To change body of implemented methods use File | Settings | File Templates.
            }

            public String end(P1<Integer> lineIndex) {
                int lastIndex = strList.size() - 1;

                try {
                    return strList.get(lastIndex);
                } finally {
                    lineIndex.e(lastIndex);
                }
            }
        };
    }

	public static <A> List<A> filter(A[] l1,
			F1<A, Boolean> f1) {
		ArrayList<A> list = new ArrayList<A>();
		for (A a : l1) {
			if (f1.e(a)) {
				list.add(a);
			}
		}
		return list;
	}

	public static <A> List<A> filter(Iterable<A> l1,
			F1<A, Boolean> f1) {
		ArrayList<A> list = new ArrayList<A>();
		if (l1 != null) {
			for (A a : l1) {
				if (f1.e(a)) {
					list.add(a);
				}
			}
		}
		return list;
	}

	public static <A> int count(Iterable<A> l1,
			final F1<A, Boolean> f1) {
        final int[] count = new int[] {0};
        each(l1, new P1<A>() { public void e(A obj) {
            if (f1.e(obj)) {
                count[0] ++;
            }
        }});
		return count[0];
	}

	public static <A> A find(Collection<A> l1,
			F1<A, Boolean> f1) {
		for (A a : l1) {
			if (f1.e(a)) {
				return a;
			}
		}
		return null;
	}

	public static <A> String join(Iterable<A> objs, String delimiter,
			F1<A, String> f1) {
		StringBuffer sb = new StringBuffer();
		for (A a : objs) {
			sb.append(f1.e(a)).append(delimiter);
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - delimiter.length());
		}
		return sb.toString();
	}

	/**
	 * If a collection is empty
	 * @param objs
	 * @return
	 */
	public static boolean isEmpty(Collection<?> objs) {
		return objs == null || objs.isEmpty();
	}
	
	public static <A> F1<Collection<A>,Boolean> isEmptyF() { return new F1<Collection<A>, Boolean>() {public Boolean e(Collection<A> obj) {
		return isEmpty(obj);
	}};}

	public static boolean isEmpty(Map<?,?> objs) {
		return objs == null || objs.isEmpty();
	}

	/**
	 * If a collection is not empty
	 * @param col
	 * @return
	 */
    public static boolean isNotEmpty(Collection<?> col) {
        return !isEmpty(col);
    }

    public static boolean isNotEmpty(Map<?,?> col) {
        return !isEmpty(col);
    }

    public static String toString(Map<?, ?> map) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<?,?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }
    public static String toStringNonNull(Map<?, ?> map) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<?,?> entry : map.entrySet()) {
        	if (entry.getValue() == null) {
        		continue;
        	}
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }

	/**
     * Create a map based on the Object... param. Each 2 values is an entry
     * which is a pair of key then value
     * @param objects The params that will be converted to map.
     * 					Format: [key1, value1, key2, value2]
     * @return The map after converted from param objects
     */
//    @SuppressWarnings({"unchecked"})
    public static <A, B> Map<A, B> map(Object... objects) {
    	if (objects==null) {
    		return null;
    	}
        Map<A, B> map = new LinkedHashMap<A, B>(objects.length / 2);
        for (int i = 0; i < objects.length; i+=2) {
            map.put((A)objects[i], (B)objects[i + 1]);
        }
        return map;
    }

    /**
     * Get single element, or null if Col is empty
     * @param collection
     * @return
     */
    public static <A> A getSingle(
            Collection<A> collection) {
    	if (collection == null) {
    		return null;
    	}
        for (A a : collection) {
            return a;
        }
        return null;
    }

	public static <A> A getLast(LinkedList<A> list) {
    	if (list == null || list.size() == 0) {
    		return null;
    	}
		return list.getLast();
	}

	public static <A> A getLast(List<A> list) {
    	if (list == null || list.size() == 0) {
    		return null;
    	}
		return list instanceof LinkedList ? ((LinkedList<A>)list).getLast() : list.get(list.size() - 1);
	}

    public static String join(String delimiter, String... strings) {
		StringBuilder sb = new StringBuilder();
		for (String str : strings) {
            if (StringUtil.isNotEmpty(str)) {
                sb.append(str).append(delimiter);
            }
        }
		if (sb.length() > 0) {
			sb.setLength(sb.length() - delimiter.length());
		}
		return sb.toString();
    }
    
    public static void main(String[] args) {
		System.out.println(merge(Arrays.asList("c","c","a","a"), (a1, a2)-> {
			return a1.startsWith(a2) ? a1 + a2 : null;
		}));
		System.out.println(merge(Arrays.asList("c","c"), (a1, a2)-> {
			return a1.startsWith(a2) ? a1 + a2 : null;
		}));
		System.out.println(merge(Arrays.asList("a", "b"), (a1, a2)-> {
			return a1.startsWith(a2) ? a1 + a2 : null;
		}));
	}
    
    public static <E> LinkedList<E> merge(List<E> list, F2<E,E,E> f) {
    	LinkedList<E> ret = new LinkedList<>();
    	for (int i = 0; i < list.size(); ) {
    		E merge = list.get(i);
    		for (int j = i+1;; j++) {
    			if (j == list.size()) {
        			ret.add(merge);
        			i++;
        			break;
    			}
        		E e2 = list.get(j);
        		
        		E merge1 = f.e(merge, e2);
        		if (merge1 != null) {
        			merge = merge1;
        			if (j==list.size() -1) {
        				ret.add(merge);
            			i=j+1;
            			break;
        			}
        		} else {
        			ret.add(merge);
        			i=j;
        			break;
        		}
			}
    		
		}
		return ret;
    }

    public static <A> Collection<A> merge(A a, List<A> list) {
        ArrayList<A> ret = new ArrayList<A>();
        ret.add(a);
        ret.addAll(list);
        return ret;
    }
    
    public static <A,B> Map<A,B> merge(Map<A,B> map1, Map<A,B> map2) {
    	HashMap<A, B> ret = new HashMap<A,B>();
    	ret.putAll(map1);
    	ret.putAll(map2);
		return ret;
    }

    public static <A> Iterable<A> iterable(
            final Enumeration<A> enumeration) {
        return new Iterable<A>() {
            public Iterator<A> iterator() {
                return new Iterator<A>() {
                    public boolean hasNext() {
                        return enumeration.hasMoreElements();
                    }

                    public A next() {
                        return enumeration.nextElement();
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };

    }

    public static <A> Collection<A> clone(Collection<A> col) {
        ArrayList<A> list = new ArrayList<A>(col.size());
        for (A a : col) {
            list.add(a);
        }
        return list;
    }

    public static String join(Map<?, ?> map, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append(delimiter);
            }
            sb.append(entry.getKey()).append(": ").append(entry.getValue());
        }
        return sb.toString();
    }

    public static <A> List<A> createList(int size, F0<A> f) {
        ArrayList<A> list = new ArrayList<A>(size);
        for (int i = 0; i < size; i++) {
            list.add(f.e());
        }
        return list;
    }

    public static <K, V> void entries(F1<K, Boolean> on, P2<K, V> action, Map<K, V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (on.e(entry.getKey())) {
                action.e(entry.getKey(), entry.getValue());
            }
        }
    }
    public static void entries(F1<String, Boolean> on, P2<String, String> action, Properties map) {
        for (Map.Entry<?,?> entry : map.entrySet()) {
            if (on.e((String) entry.getKey())) {
                action.e((String)entry.getKey(), (String)entry.getValue());
            }
        }
    }

    public static <A extends Comparable<A>> List<A> sort(Collection<A> collection) {
        List<A> list = toList(collection);
        Collections.sort(list);
        return list;
    }

    public static Range range(Collection<String> col, F1<String, Integer> f) {
        Range ret = new Range();
        for (String s : col) {
            Integer val = f.e(s);
            if (ret.getFrom() == null) {
                ret.setFrom(val);
                ret.setTo(val);
            } else {
                if (ret.getFrom() > val) {
                    ret.setFrom(val);
                }
                if (ret.getTo() < val) {
                    ret.setTo(val);
                }
            }
        }
        return ret;
    }

	public static <A> List<A> append(List<A> col, A a) {
		if (col == null) {
			return Collections.singletonList(a);
		}
		col.add(a);
		return col;
	}

    public static <A> void addCount(A a, Map<A, AtomicInteger> counts) {
        AtomicInteger count = counts.get(a);
        if (count == null) {
            count = new AtomicInteger(1);
            counts.put(a, count);
        } else {
            count.incrementAndGet();
        }
    }

    /**
     * Create a string connecting all values in collection, separated with delimiter
     * @param objs
     * @param delimiter
     * @return
     */
	public static <A> String join(Iterable<A> objs, String delimiter) {
        if (objs == null) {
            return "";
        }
		StringBuffer sb = new StringBuffer();
		for (A a : objs) {
			sb.append(a).append(delimiter);
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - delimiter.length());
		}
		return sb.toString();
	}
	public static <A> F1<List<A>, String> joinF(
		final String delimiter) {
		return new F1<List<A>, String>() {public String e(List<A> list) {
			return join(list, delimiter);
		}};
	}

	
	/**
	 * Create a collection contain of pointers to the same value by times.
	 * For example: multiply("a",3) => [a,a,a]
	 * @param a
	 * @param times
	 * @return
	 */
	public static <A> Collection<A> multiply(A a, int times) {
		LinkedList<A> ret = new LinkedList<A>();
		for (int i = 0; i < times; i++) {
			ret.add(a);
		}
		return ret;
	}

	public static <K, V> void add(Map<K, V> map,
			Map<K, Set<V>> maps) {
		int limit = -1;
		
		add(map, maps, limit);
	}

	public static <K, V> void add(Map<K, V> map, Map<K, Set<V>> maps, int limit) {
		for (Entry<K, V> entry : map.entrySet()) {
			K key = entry.getKey();
			Set<V> set = maps.get(key);
			if (set == null) {
				set = new HashSet<V>();
				maps.put(key, set);
			}
			if (limit == -1 || set.size() < limit) {
				set.add(entry.getValue());
			}
		}
	}

	/**
	 * Use LinkedList
	 * @param key
	 * @param value
	 * @param maps
	 */
	public static <K, V> void addList(K key, V value, Map<K, List<V>> maps) {
		List<V> list = maps.get(key);
		if (list == null) {
			list = new LinkedList<V>();
			maps.put(key, list);
		}
		list.add(value);
	}
	public static <K, V> void addSet(K key, V value, Map<K, Set<V>> maps) {
		Set<V> list = maps.get(key);
		if (list == null) {
			list = new HashSet<V>();
			maps.put(key, list);
		}
		list.add(value);
	}
	public static <K, V> void addListNotDup(K key, V value, Map<K, List<V>> maps) {
		List<V> list = maps.get(key);
		if (list == null) {
			list = new LinkedList<V>();
			maps.put(key, list);
		}
		
		if (!list.contains(value)) {
			list.add(value);
		}
	}
	
	public static <A> List<A> addFirst(A a, List<A> list) {
		LinkedList<A> ret = new LinkedList<A>(list);
		ret.addFirst(a);
		return ret;
	}

	public static <A> List<A> toList(Iterator<A> iterator) {
		ArrayList<A> list = new ArrayList<A>();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		return list;
	}

	public static <A, B extends Comparable<B>> void sort(final List<A> list, final Map<A, B> sortBy) {
		Collections.sort(list, new Comparator<A>() {
			public int compare(A o1, A o2) {
				return sortBy.get(o1).compareTo(sortBy.get(o2));
			}
		});
	}

	public static <A> Map<String, A> map_s(Object...objects) {
		return map(objects);
	}

	public static <A> Double max(Collection<A> col, F1<A, Double> f1) {
		Double max = null;
		for (A a : col) {
			Double fv = f1.e(a);
			max = max==null ? fv : Math.max(max, fv);
		}
		return max;
	}
	public static <A extends Comparable<A>> A max(Collection<A> col) {
		A max = null;
		for (A a : col) {
			if (max == null) {
				max = a;
			} else if (a.compareTo(max) > 0) {
				max = a;
			}
		}
		return max;
	}
	public static <A> Integer maxI(Collection<A> col, F1<A, Integer> f1) {
		int max = Integer.MIN_VALUE;
		for (A a : col) {
			max = Math.max(max, f1.e(a));
		}
		return max == Integer.MIN_VALUE ? null : max;
	}
	
	public static <E,N extends Comparable<N>> E maxE(Collection<E> col, F1<E, N> f1) {

		N max = null;
		E eMax = null;
		for (E e : col) {
			N a = f1.e(e);
			if (max == null || max.compareTo(a) < 0) {
				eMax = e;
				max = a;
			}
		}
		
		return eMax;
	}

	public static <A> Douce<A, Double> findMax(Collection<A> col, F1<A, Double> f1) {
		double max = Double.MIN_VALUE;
		A cur = null;
		for (A a : col) {
			Double val = f1.e(a);
			if ( cur == null || val > max) {
				max = val;
				cur = a;
			}
		}
		return new Douce<A, Double>(cur, max);
	}

	public static <A, B> P1<B> putF(final A key, final Map<A, B> map) {
		return new P1<B>() {public void e(B obj) {
			map.put(key, obj);
		}};
	}

	public static List<Integer> asList(int[] arr) {
		ArrayList<Integer> ret = new ArrayList<Integer>(arr.length);
		for (int i = 0; i < arr.length; i++) {
			ret.add(arr[i]);
		}
		return ret;
	}

	public static void cut(List<?> list, int index) {
		for (int i = list.size() - 1; i > -1 ; i--) {
			list.remove(i);
		}
	}

	public static <T,B>
	int searchIndexedBinary(
			final List<B> list, 
			final F1<B,? extends Comparable<? super T>> keyF, 
			T key) {
		RandomAccessCol<T> col = (RandomAccessCol<T>) randomAccessCol(list, keyF);
		
		return searchIndexedBinary(key, col);
	}
	
	public static <A,T extends Comparable<T>>
	int searchIndexedBinaryToHigh(
			final List<A> list, 
			final F1<A,T> keyF, 
					T key) {
		RandomAccessCol<T> col = (RandomAccessCol<T>) randomAccessCol(list, keyF);
		
		int index = searchIndexedBinary(key, col);
		if (index <0) {
			return -index -1 -1;
		}
		
		for (; index<col.size()-1; index++) {
			if (!ObjectUtil.equals(key, col.get(index+1))) {
				return index;
			}
		}
		
		return index;
	}

	public static <T> int searchIndexedBinary(T key, RandomAccessCol<T> col) {
		int low = 0;
		int high = col.size()-1;
	
		while (low <= high) {
		    int mid = (low + high) >>> 1;
//		    @SuppressWarnings("unchecked")
			Comparable<? super T> midVal = (Comparable<? super T>) col.get(mid);
		    int cmp = midVal.compareTo(key);
	
		    if (cmp < 0) {
				low = mid + 1;
			} else if (cmp > 0) {
				high = mid - 1;
			} else {
				return mid; // key found
			}
		}
		return -(low + 1);  // key not found
	}
	
	@Deprecated
	public static <T,B> 
	int indexedBinarySearch(List<B> l, F1<B,T> keyF, T key,
			Comparator<? super T> c) {
		int low = 0;
		int high = l.size() - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			T midVal = keyF.e(l.get(mid));
			int cmp = c.compare(midVal, key);

			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1); // key not found
	}


	public static <A,B extends Comparable<? super B>> A indexedBinaryGetLow(List<A> list,
			F1<A, B> get, B key) {
		int search = searchIndexedBinary(list, get, key);
		if (search== -1) {
			return null;
		}
		if (search < 0) {
			return list.get(-search -1 -1);
		}
		return list.get(search);
	}

	public static <A extends Comparable<A>> void insertSorted(A a, List<A> as) {
		int index = Collections.binarySearch(as, a);
		if (index<0) {
			index = -index -1;
		}
		as.add(index, a);
	}
	public static <A extends Comparable<A>> void insertSorted(A a, List<A> as, Comparator<? super A> comparator) {
		int index = Collections.binarySearch(as, a, comparator);
		if (index<0) {
			index = -index -1;
		}
		as.add(index, a);
	}
	public static <A, K extends Comparable<K>> void insertSorted(A a, List<A> as, F1<A,K> keyF) {
		int index = Cols.searchIndexedBinary(as, keyF, keyF.e(a));
		if (index<0) {
			index = -index -1;
		}
		as.add(index, a);
	}

	public static <A extends Comparable<A>> List<A> insertSorted(List<A> as1, List<A> as2) {
		ArrayList<A> list = new ArrayList<A>(as1);
		for (A a : as2) {
			insertSorted(a, list);
		}
		return list;
	}

	public static <A,B> Iterable<B> iterable(final Iterable<A> collection,
			final F1<A,B> convertF) {
		if (collection==null) {
			return null;
		}
		return new Iterable<B>() {public Iterator<B> iterator() {
			final Iterator<A> iterator = collection.iterator();
			return new Iterator<B>() {

				public boolean hasNext() {
					return iterator.hasNext();
				}

				public B next() {
					return convertF.e(iterator.next());
				}

				public void remove() {
					iterator.remove();
				}
			};
		}};
	}

	public static <A> List<A> addAll(List<A> target, List<A> source) {
		if (source==null) {
			return target;
		}
		if (target!=null) {
			LinkedList<A> ret = new LinkedList<A>(source);
			ret.addAll(target);
			return ret;
		}
		return source;
	}

	public static <A> RandomAccessCol<A> randomAccessCol(final List<A> as) {
		return new RandomAccessCol<A>() {
			public A get(int i) {
				return as.get(i);
			}

			public int size() {
				return as.size();
			}
		};
	}

	public static <V,K> Map<K, V> index(
			Iterable<V> list, F1<V, K> keyF) {
		HashMap<K, V> ret = new HashMap<K, V>();
		for (V a : list) {
			ret.put(keyF.e(a), a);
		}
		return ret;
	}
	public static <K,V> Map<K, V> indexKeys(
			Iterable<K> list, F1<K, V> valF) {
		HashMap<K, V> ret = new HashMap<K, V>();
		for (K key : list) {
			ret.put(key, valF.e(key));
		}
		return ret;
	}
	public static <E,K> Map<K, Collection<E>> indexMulti(
			List<E> list, F1<E, K> keyF) {
		HashMap<K, Collection<E>> ret = new HashMap<K, Collection<E>>();
		for (E e : list) {
			K key = keyF.e(e);
			Collection<E> col = ret.get(key);
			if (col == null) {
				col = new LinkedList<E>();
				ret.put(key, col);
			}
			col.add(e);
		}
		return ret;
	}

	public static <A> A getCenter(ArrayList<A> list) {
		if (isEmpty(list)) {
			return null;
		}
		return list.get(list.size() / 2);
	}

//	@SuppressWarnings("rawtypes")
	public static Map toMap(Object[] props) {
		HashMap ret = new HashMap();
		for (int i = 0; i < props.length; i+=2) {
			ret.put(props[i], props[i + 1]);
		}
		return ret;
	}

	public static <K,V> void putMulti(K key, V value, Map<K, List<V>> multiMap) {
		List<V> list = multiMap.get(key);
		if (list==null) {
			list = new LinkedList<V>();
			multiMap.put(key, list);
		}
		list.add(value);
	}

	public static <A,B> Iterable<B> convert(final Iterable<A> entities,final F1<A,B> f) {
		return new Iterable<B>() {
			public Iterator<B> iterator() {
				final Iterator<A> it = entities.iterator();
				return new Iterator<B>() {
					public void remove() {
						it.remove();
					}
					public B next() {
						return f.e(it.next());
					}
					public boolean hasNext() {
						return it.hasNext();
					}
				};
			}
		};
	}

	public static <E> void toTop(E item,
			List<E> list) {
		int indexOf = list.indexOf(item);
		if (indexOf==-1 || indexOf==0) {
			return;
		}
		list.remove(indexOf);
		list.add(0,item);
	}

	public static Map add(Map map, Object... values) {
		return merge(map, Cols.map(values));
	}

	public static <A> Map<String,Integer> groupCount(Iterable<A> col,
			F1<A, String> groupF) {
		HashMap<String, AtomicInteger> result = new HashMap<String, AtomicInteger>();
		for (A a : col) {
			String group = groupF.e(a);
			
			MathUtil.increase(group, result);
		}
		return MathUtil.intMap(result);
	}

	public static <A> Map<String,BigDecimal> groupSum(Iterable<A> col,
			F1<A, String> groupF,
			F1<A, ? extends Number> valF) {
		HashMap<String, BigDecimal> result = new HashMap<String, BigDecimal>();
		for (A a : col) {
			String group = groupF.e(a);
			Number val = valF.e(a);
			MathUtil.increaseBig(group, val, result);
		}
		return result;
	}
	
	public static String join(Map map1, Map map2, String delimiter) {
		HashSet keys = new HashSet(map1.keySet());
		keys.addAll(map2.keySet());
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (Object key : keys) {
			if (sb.length() > 1) {
				sb.append(",");
			}
			sb.append(key).append("=");
			Object v1 = map1.get(key);
			Object v2 = map2.get(key);
			if (v1!=null) {
				sb.append(v1);
			}
			sb.append(delimiter);
			if (v2!=null) {
				sb.append(v2);
			}
		}
		sb.append("}");
		return sb.toString();
	}

	public static <A,T> F1<A,T> toF1(final Map<A, T> map) {
		return new F1<A, T>() {
			public T e(A obj) {
				return map.get(obj);
			}
		};
	}
	
	public static <A> List<Map> toListMap(LinkedList<A> as,
			P2<A, P2<String, Object>> p2) {
		LinkedList<Map> ret = new LinkedList<Map>();
		for (A a : as) {
			final HashMap map = new HashMap();
			
			p2.e(a, putF(map));
			
			ret.add(map);
		}
		return ret;
	}
	
	private static P2<String, Object> putF(final Map map) {
		return new P2<String, Object>() {public void e(String key, Object value) {
			map.put(key, value);
		}};
	}

	public static <A,B> List<B> values(List<A> as,
			Map<A, B> map) {
		LinkedList<B> ret = new LinkedList<B>();
		for (A a : as) {
			ret.add(map.get(a));
		}
		return ret;
	}

	public static void removeAll(Collection<PointD> col,
			F1<PointD, Boolean> f1) {
		for (Iterator<PointD> iterator = col.iterator(); iterator.hasNext();) {
			PointD pointD = iterator.next();
			if (f1.e(pointD)) {
				iterator.remove();
			}
		}
	}

	public static <A> void add(A a, Collection<A> col) {
		if (a != null) {
			col.add(a);
		}
	}

	public static <K,V> P0 removeValF(final V val,
			final Map<K, V> map) {
		return new P0() {public void e() {
			for (Entry<K, V> entry : map.entrySet()) {
				if (entry.getValue() == val) {
					map.remove(entry.getKey());
					return;
				}
			}
		}};
	}

	public static <A> void setSize(LinkedList<A> list, int size) {
		int l = list.size() - size;
		for (int i = 0; i < l; i++) {
			list.removeLast();
		}
	}

	public static int distanceToLine(List<String> list1, List<String> list2) {
		LinkedList<String> line1 = new LinkedList<String>(list1);
		LinkedList<String> line2 = new LinkedList<String>(list2);
		
		int size = Math.min(line1.size(), line2.size());
		setSize(line1, size);
		setSize(line2, size);
		
		while (true) {
			if (line1.isEmpty()) {
				return -1;
			}
			
			if (line1.equals(line2)) {
				return list1.size() - line1.size();
			}
			
			line1.removeLast();
			line2.removeLast();
		}
	}

	public static <A> int sum(Collection<A> list, F1<A, Integer> numF) {
		int sum = 0;
		for (A a : list) {
			sum += numF.e(a);
		}
		return sum;
	}
	public static long sumL(Collection<Long> list) {
		long sum = 0;
		for (long a : list) {
			sum += a;
		}
		return sum;
	}

	public static <K,V, V1 extends V> V getForced(K key,
			Map<K, V> map,
			Class<V1> valueClass) {
		V v = map.get(key);
		if (v==null) {
			v = ReflectUtil.newInstance(valueClass);
			map.put(key, v);
		}
		return v;
	}

	public static HashMap<String,String> toMap(String string, String keyvalDelimiter, String itemDelimiter) {
		HashMap<String, String> ret = new LinkedHashMap<String, String>();
		for (String item : string.split(itemDelimiter)) {
			String[] split = item.split(keyvalDelimiter);
			ret.put(split[0], split[1]);
		}
		return ret;
	}
	
	public static String toString(Map map, String keyvalDelimiter, String itemDelimiter) {
		StringBuilder sb = new StringBuilder();
		for (Object entryO : map.entrySet()) {
			if (sb.length() > 0) {
				sb.append(itemDelimiter);
			}
			Entry entry = (Entry) entryO;
			sb.append(entry.getKey());
			sb.append(keyvalDelimiter);
			sb.append(entry.getValue());
		}
		return sb.toString();
	}
	
	public static <A> A containAny(Iterable<A> target, Collection<A> col) {
		for (A a : target) {
			if (col.contains(a)) {
				return a;
			}
		}
		return null;
	}
	public static <A> boolean containsAll(Iterable<A> target, Collection<A> col) {
		for (A a : target) {
			if (!col.contains(a)) {
				return false;
			}
		}
		return true;
	}

	public static <E> List<E> addList(E e,
			List<E> list) {
		if (list==null) {
			list = new LinkedList<E>();
		}
		list.add(e);
		return list;
	}

	static <A> F1<Iterable<A>, Iterator<A>> iteratorF() {
		return  new F1<Iterable<A>, Iterator<A>>() {public Iterator<A> e(Iterable<A> iterable) {
			return iterable==null ? null : iterable.iterator();
		}};
	}
	public static <A extends Comparable<A>> Iterable<A> sequenceSorted(final List<Iterable<A>> iterables) {
		return new Iterable<A>() {public Iterator<A> iterator() {
			final List<Iterator<A>> iterators = Cols.yield(iterables, Cols.<A>iteratorF());
			final HashMap<Integer, A> valMap = valMap(iterators);
			return new Iterator<A>() {

				@Override
				public boolean hasNext() {
					for (Entry<Integer, A> entry : valMap.entrySet()) {
						if (entry.getValue() != null) {
							return true;
						}
					}
					return false;
				}

				@Override
				public A next() {
					Entry<Integer, A> top = getTop(valMap);
					A ret = top.getValue();
					
					Iterator<A> iterator = iterators.get(top.getKey());
					A newValue = !iterator.hasNext() ? null : iterator.next();
					valMap.put(top.getKey(), newValue);
					return ret;
				}

				@Override
				public void remove() {
				}
			};
		}};
	}
	
//	public static void main(String[] args) {
//		LinkedList<Iterable<Range>> l2 = new LinkedList<Iterable<Range>>();
//		l2.add(new LinkedList<Range>());
//		l2.add(Arrays.asList(new Range(2,4), new Range(12,14)));
//		for (Range r : sequenceSorted(l2)) {
//			System.out.println(r);
//		}
//	}
	
	public static <K,V extends Comparable<V>> Entry<K,V> getTop(Map<K,V> map) {
		Entry<K, V> topEntry = null;
		for (Entry<K, V> entry : map.entrySet()) {
			if (entry.getValue()==null) {
				continue;
			}
			if (topEntry != null) {
				if (entry.getValue().compareTo(topEntry.getValue()) < 0) {
					topEntry = entry;
				}
			} else {
				topEntry = entry;
			}
		}
		return topEntry;
	}
	
	public static <A> Iterable<A> valueIterable(Map<?,A> map) {
		LinkedList<A> ret = new LinkedList<A>();
		for (Entry<?, A> entry : map.entrySet()) {
			A val = entry.getValue();
			if (val!=null) {
				ret.add(val);
			}
		}
		return ret;
	}
	
	public static <A> Iterable<A> sequence(final Iterable<A>... iterables) {
		return new Iterable<A>() {public Iterator<A> iterator() {
			final int[] i = {-1};
			final AtomicReference<Iterator<A>> ref = new AtomicReference<Iterator<A>>();
			
			return new Iterator<A>() {

				@Override
				public boolean hasNext() {
					while (true) {
						
						if (ref.get() == null) {
							if (i[0] < iterables.length - 1) {
								Iterable<A> iterable = iterables[++i[0]];
								if (iterable != null) {
									ref.set(iterable.iterator());
								}
								continue;
							} else {
								return false;
							}
						} else {
							if (ref.get().hasNext()) {
								return true;
							} else {
								ref.set(null);
								continue;
							}
						}
					}
				}

				@Override
				public A next() {
					return ref.get().next();
				}

				@Override
				public void remove() {
				}
			};
		}};
	}

	public static <A> P1<A> addFirstF(final LinkedList<A> list) {
		return new P1<A>() {public void e(A obj) {
			list.addFirst(obj);
		}};
	}
	public static <A> P1<A> addLastF(final LinkedList<A> list) {
		return new P1<A>() {public void e(A obj) {
			list.addLast(obj);
		}};
	}

	public static <A extends List> A addAll(Class<A> listClass,
			List... lists) {
		A ret = ReflectUtil.newInstance(listClass);
		
		for (List list : lists) {
			ret.addAll(list);
		}
		
		return ret;
	}

	private static <A> HashMap<Integer, A> valMap(List<Iterator<A>> iterators) {
		HashMap<Integer, A> vals = new HashMap<Integer, A>();
		int i = 0;
		for (Iterator<A> iterator : iterators) {
			if (iterator.hasNext()) {
				vals.put(i, iterator.next());
			}
			i++;
		}
		return vals;
	}

	/**
	 * Each of steps can introduce a number of possibility. Each of them will be combined with each 
	 * possibility of each next step. This will result something like lines from root to each branch 
	 * of a tree
	 * @param steps
	 * @param digF
	 * @param resultF
	 */
	public static <F,N> void eachLine(Collection<F> steps, P2<F,P1<N>> digF, P1<List<N>> resultF) {
		eachLine(new LinkedList<F>(steps), digF, new LinkedList<N>(), resultF);
	}
	
	private static <F,N> void eachLine(final List<F> steps, final P2<F,P1<N>> digF, final List<N> collecteds, final P1<List<N>> resultF) {
		if (Cols.isEmpty(steps)) {
			resultF.e(collecteds);
			return;
		}

		F feed = steps.get(0);
		digF.e(feed, new P1<N>() {public void e(N n) {
			LinkedList<N> newCollecteds = new LinkedList<N>(collecteds);
			newCollecteds.add(n);
			
			eachLine(steps.subList(1, steps.size()), digF, newCollecteds, resultF);
		}});
	}

	public static <A> LinkedList<A> toLinkedList(
			List<A> list) {
		if (list == null) {
			return null;
		}
		if (list instanceof LinkedList) {
			return (LinkedList<A>) list;
		} else {
			return new LinkedList<A>(list);
		}
	}
	
	public static <A> Douce<A, Collection<A>> getFirst(Iterable<A> iter) {
		LinkedList<A> leftovers = new LinkedList<A>();
		A first = null;
		boolean gotFirst = false;
		for (A a : iter) {
			if (!gotFirst) {
				first = a;
				gotFirst = true;
			} else {
				leftovers.add(a);
			}
		}
		return new Douce<A, Collection<A>>(first, leftovers);
	}
	
	public static <A> boolean containsPointer(A a, Iterable<A> col) {
		for (A a2 : col) {
			if (a == a2) {
				return true;
			}
		}
		return false;
	}

	public static <A> A find(P1<F1<A,Boolean>> iterate, final F1<A,Boolean> findF) {
		final AtomicReference<A> ref = new AtomicReference<A>();
		iterate.e(new F1<A,Boolean>() {public Boolean e(A obj) {
			if (findF.e(obj)) {
				ref.set(obj);
				return true;
			}
			return false;
		}});
		return ref.get();
	}

	public static <A> LinkedList<A> join(Collection<A>... cols) {
		LinkedList<A> ret = new LinkedList<A>();

		for (Collection<A> col : cols) {
			if (col != null) {
				ret.addAll(col);
			}
		}
		return ret;
	}

	public static <A, B extends Comparable<B>> A best(Iterable<A> col, F1<A,B> f) {
		A best = null;
		for (A a : col) {
			if (best == null) {
				best = a;
			} else {
				if (f.e(best).compareTo(f.e(a)) > 0) {
					;
				} else {
					best = a;
				}
			}
		}
		return best;
	}

	public static <K,V> void eachEntry(Map<K, V> map,
			P2<K, V> p2) {
		if (map == null) {
			return;
		}
		
		for (Entry<K, V> entry : map.entrySet()) {
			p2.e(entry.getKey(), entry.getValue());
		}
	}

	public static <A,B> boolean contains(B target, Iterable<A> col,
			F1<A, B> yieldF) {
		for (A a : col) {
			if (yieldF.e(a).equals(target)) {
				return true;
			}
		}
		return false;
	}
	public static <A,B> A find(B target, Iterable<A> col,
			F1<A, B> yieldF) {
		for (A a : col) {
			if (yieldF.e(a).equals(target)) {
				return a;
			}
		}
		return null;
	}

	public static <A> Iterable<A> reversedIterable(
			final List<A> col) {
		return new Iterable<A>() {public Iterator<A> iterator() {
			final int[] i = { col.size() - 1 };
			return new Iterator<A>() {

				@Override
				public boolean hasNext() {
					return i[0] > -1;
				}

				@Override
				public A next() {
					return col.get(i[0] --);
				}

				@Override
				public void remove() {
				}
			};
		}};
	}

	public static <A> List<A> reverse(List<A> list) {
		LinkedList<A> ret = new LinkedList<A>();
		for (A a : list) {
			ret.add(0, a);
		}
		return ret;
	}
	public static <A> F1<List<A>,List<A>> reverseF() {
		return new F1<List<A>, List<A>>() {public List<A> e(List<A> obj) {
			return reverse(obj);
		}};
	}

	public static <A> P0 setF(final int index, final A a,
			final List<A> list) {
		return new P0() {public void e() {
			list.set(index, a);
		}};
	}

	public static <A,B> boolean check(List<A> colA, List<B> colB,
			F2<A, B, Boolean> checkF) {
		int i = -1;
		for (A a : colA) {
			i++;
			
			B b = colB.get(i);
			
			if (!checkF.e(a, b)) {
				return false;
			}
		}
		return true;
	}

	public static <A,B> LinkedList<B> yield(Iterator<A> iter, F1<A,B> f1) {
		LinkedList<B> ret = new LinkedList<B>();
		for (; iter.hasNext();) {
			A a = iter.next();
			ret.add(f1.e(a));
		}
		return ret;
	}

	public static F1<Collection,Set> toSetF() {
		
		return new F1<Collection, Set>() {public Set e(Collection col) {
			return toSet(col);
		}};
	}

	public static <A> Collection<A> cast(Collection col) {
		return col;
	}

	public static <A> List<A> getFirst(int count, Iterable<A> col) {
		LinkedList<A> ret = new LinkedList<A>();
		int i = 0;
		for (A a : col) {
			if (++i > count) {
				break;
			}
			ret.add(a);
		}
		return ret;
	}

	public static boolean isAllType(Collection col, Class<?> clazz) {
		for (Object o : col) {
			if (!(clazz.isInstance(o))) {
				return false;
			}
		}
		return true;
	}

	public static <A> void eachChildRecursive(A a,
			F1<A, Collection<A>> digF,
			P1<A> p1) {
		Collection<A> col = digF.e(a);
		if (col==null) {
			return;
		}
		for (A child : col) {
			p1.e(child);
			eachChildRecursive(child, digF, p1);
		}
	}

	public static <A> Set<A> toSet(Collection<A> col) {
		if (col instanceof Set) {
			return (Set) col;
		}
		return new HashSet(col);
	}

	public static <A> HashSet<A> mutableSet(Set<A> col) {
		if (col instanceof HashSet) {
			return (HashSet<A>) col;
		}
		return new HashSet<A>(col);
	}
	
	public static <A> F0<A> repeat(final Collection<A> col) {
		final AtomicReference<Iterator<A>> iterRef = new AtomicReference<Iterator<A>>();
		return new F0<A>() {public A e() {
			if (iterRef.get() == null || !iterRef.get().hasNext()) {
				iterRef.set(col.iterator());
			}
			return iterRef.get().next();
		}};
	}

	public static <A> boolean findCombiMutual2(List<A> col,
			F2<A, A, Boolean> finder) {
		for (int i = 0; i < col.size() - 1; i++) {
			A a1 = col.get(i);
			for (int j = i + 1; j < col.size(); j++) {
				A a2 = col.get(j);
				if (finder.e(a1, a2)) {
					return true;
				}
			}
		}
		return false;
	}

	public static <A> boolean isNotEmpty(A[] array) {
		return array != null && array.length > 0;
	}

	public static <A> Map yieldMap(Collection<A> col, F1<A,Douce> f) {
		LinkedHashMap ret = new LinkedHashMap();
		
		for (A a : col) {
			Douce douce = f.e(a);
			ret.put(douce.get1(), douce.get2());
		}
		
		return ret;
	}

	public static <A> A getLast(Collection<A> col) {
		if (col instanceof List) {
			return getLast((List<A>)col);
		}
		A last = null;
		for (A a : col) {
			last = a;
		}
		return last;
	}

	public static <A> List<A> getLast(int count, List<A> col) {
		return col.subList(Math.max(0, col.size() - count), col.size());
	}

	public static <A,B> List<Douce<A,B>> listDouce(Douce<List<A>,List<B>> douce) {
		LinkedList<Douce<A, B>> ret = new LinkedList<Douce<A,B>>();
		Iterator<B> iterB = douce.get2().iterator();
		
		for (A a : douce.get1()) {
			ret.add(new Douce<A,B>(a, iterB.next()));
		}
		return ret;
	}
	
	/**
	 * 
	 * @param col
	 * @param separate
	 * @return matched , not matched
	 */
	public static <A> Douce<List<A>,List<A>> separate(Iterable<A> col, F1<A,Boolean> separate) {
		LinkedList<A> matcheds = new LinkedList<A>();
		LinkedList<A> notMatcheds = new LinkedList<A>();
		for (A a : col) {
			if (separate.e(a)) {
				matcheds.add(a);
			} else {
				notMatcheds.add(a);
			}
		}
		return new Douce<List<A>, List<A>>(matcheds, notMatcheds);
	}

	public static <A> Set<A> addSet(List<A> col1,
			List<A> col2) {
		HashSet<A> set = new HashSet<A>();
		if (col1 != null) {
			set.addAll(col1);
		}
		if (col2 != null) {
			set.addAll(col2);
		}
		return set;
	}
	
	public static <A> Douce<Set<A>,Set<A>> getDiffs(Collection<A> col1, Collection<A> col2) {
		HashSet<A> diff1 = new HashSet<A>(col1);
		Set<A> set2 = toSet(col2);
		HashSet<A> diff2 = new HashSet<A>();
		for (A a : set2) {
			if (diff1.remove(a)) {
				; // Both contain this element, simply remove
			} else {
				// Only col2 contain this element, add to diff2 set
				diff2.add(a);
			}
		}
		return new Douce<Set<A>,Set<A>>(diff1, diff2);
	}
	
	/**
	 * Cut and remove elements out of list
	 * @param from
	 * @param length
	 * @param list
	 * @return
	 */
	public static <A> List<A> splice(int from, int length, List<A> list) {
		ArrayList<A> ret = new ArrayList<A>(list.size() - length);
		for (int i = 0; i < list.size(); i++) {
			if (i >= from && i < from+length) {
				;
			} else {
				ret.add(list.get(i));
			}
		}
		return ret;
	}
	
	/**
	 * Including self
	 * @param set
	 * @param p1
	 */
	public static <A> void eachSubSetAndSelf(Set<A> set, final P1<Set<A>> p1) {
		p1.e(set);
		eachSubSet(set, p1);
	}
	
	/**
	 * Not including self
	 * @param set
	 * @param p1
	 */
	public static <A> void eachSubSet(Set<A> set, final P1<Set<A>> p1) {
		if (Cols.isEmpty(set)) {
			return;
		}
		
		if (set.size() == 1) {
			return;
		}
		
		A a = getSingle(set);
//		System.out.println("a=" + a);
		// The a itself
		p1.e(asSet(a));
		
		HashSet<A> subset = subSetExcept(a, set);
		p1.e(subset);
		eachSubSet(subset, new P1<Set<A>>() {public void e(Set<A> subOfSub) {
//			System.out.println("set=" + set + ", subset=" + subset + ", subOfSub=" + subOfSub);
			// Not with a
			p1.e(subOfSub);
			
			// With a
			HashSet<A> newSub = new HashSet<A>(subOfSub);
			newSub.add(a);
			p1.e(newSub);
		}});
	}
	
	private static <A> Set<A> asSet(A... as) {
		HashSet<A> ret = new HashSet<>();
		for (A a : as) {
			ret.add(a);
		}
		return ret;
	}

	private static <A> HashSet<A> subSetExcept(A a, Set<A> set) {
		HashSet<A> subset = new HashSet<A>();
		for (A a2 : set) {
			if (a2 != a) {
				subset.add(a2);
			}
		}
		return subset;
	}
	
	
	
	/**
	 * Same list with different orders
	 * @param list
	 * @param p1
	 */
	public static <A> void eachArrangement(List<A> list,
			final P1<List<A>> p1) {
		if (list.size() == 1) {
			p1.e(list);
		} else if (list.size() == 2) {
			p1.e(list);
			p1.e(Arrays.asList(list.get(1), list.get( 0 )));
		} else {
			for (int i = 0; i < list.size(); i++) {
				final A a = list.get(i);
				final LinkedList<A> feed = new LinkedList<A>();
                List<A> splice = splice(i, 1, list);
                eachArrangement(splice, new P1<List<A>>() {public void e(List<A> subList) {
					feed.add(a);
					feed.addAll(subList);
					p1.e(feed);
					feed.clear();
				}});
			}
		}
		
		
	}

	public static <A> F0<A> loopF(LinkedList<A> col) {
		return repeat(col);
	}

	public static <A> F1<A,Boolean> containsF(final Collection<A> col) {
		return new F1<A, Boolean>() {public Boolean e(A obj) {
			return col.contains(obj);
		}};
	}

	public static <A> boolean any(Iterable<A> as, F1<A,Boolean> f) {
		for (A a : as) {
			if (f.e(a)) {
				return true;
			}
		}
		return false;
	}
	
	public static <K,V> Map<K,V> parseMapString(String string) {
		if (!(string.startsWith("{") && string.endsWith("}"))) {
			throw new IllegalArgumentException("Not a map string: " + string);
		}
		
		string = string.substring(1, string.length() - 1);
		LinkedHashMap map = new LinkedHashMap();
		for (String pairStr : string.split(", ")) {
			int indexOf = pairStr.indexOf("=");
			map.put(pairStr.substring(0, indexOf), pairStr.substring(indexOf + 1));
		}
		return map;
	}

	/**
	 * Duplicated elements stay together
	 * @param list
	 * @return
	 */
	public static <E> ArrayList<E> removeDuplicates2(
			Iterable<E> list) {
		E last = null;
		int count = 0;
		for (E e : list) {
			if (last == null || !last.equals(e)) {
				count ++;
			}
			
			last = e;
		}
		ArrayList<E> ret = new ArrayList<>(count);

		last = null;
		for (E e : list) {
			if (last == null || !last.equals(e)) {
				ret.add(e);
			}
			
			last = e;
		}
		return ret;
	}

	public static <A,K> Collection<A> retrieveNexts(
			LinkedList<A> col,
			F1<A, K> f) {
		LinkedList<A> ret = new LinkedList<A>();
		A first = col.removeFirst();
		ret.add(first);
		for (;col.size() > 0;) {
			A attempt = col.getFirst();
			if (ObjectUtil.equals(f.e(attempt), f.e(first))) {
				col.removeFirst();
				ret.add(attempt);
			} else {
				break;
			}
		}
		return ret;
	}
	
	public static <E> boolean eachPair(List<E> col, F2<E,E,Boolean> f) {
		for (int i = 0; i < col.size(); i++) {
			E ei = col.get(i);
			for (int j = i+1; j < col.size(); j++) {
				if (f.e(ei, col.get(j))) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static <A,B> List<Douce<A,B>> joinDouce(List<A> as, List<B> bs) {
		LinkedList<Douce<A, B>> ret = new LinkedList<>();
		
		Iterator<B> iterator = bs.iterator();
		for (A a : as) {
			B b = iterator.next();
			ret.add(new Douce<A, B>(a, b));
		}
		
		return ret;
	}
	
	public static <K,V1,V2> Map<K, Douce<V1,V2>> joinDouce(Map<K,V1> map1, Map<K,V2> map2) {
		HashMap<K, Douce<V1, V2>> ret = new HashMap<>();
		
		for (Entry<K, V1> entry : map1.entrySet()) {
			ret.put(entry.getKey(), new Douce<V1, V2>(entry.getValue(), null));
		}
		for (Entry<K, V2> entry : map2.entrySet()) {
			K key = entry.getKey();
			Douce<V1, V2> douce = ret.get(key);
			V2 value = entry.getValue();
			if (douce == null) {
				ret.put(key, new Douce<V1, V2>(null, value));
			} else {
				douce.set2(value);
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 * @param col
	 * @param wouldRemove
	 * @return Removed elements
	 */
	public static <E> List<E> remove(Iterable<E> col, F1<E,Boolean> wouldRemove) {
		LinkedList<E> ret = new LinkedList<>();
		for (Iterator<E> iterator = col.iterator(); iterator.hasNext();) {
			E e = iterator.next();
			if (wouldRemove.e(e)) {
				ret.add(e);
				iterator.remove();
			}
		}
		return ret;
	}

	public static <A> Set<A> merge(Set<A> s1, Set<A> s2) {
		HashSet<A> ret = new HashSet<A>(s1);
		ret.addAll(s2);
		return ret;
	}

	public static <A> Set<A> remove(A a, Set<A> needs) {
		HashSet<A> ret = new HashSet<>(needs);
		ret.remove(a);
		return ret;
	}

	public static <K,V> Map<K,V> toMap(List<V> col, F1<V, K> keyF) {
		HashMap<K, V> ret = new HashMap<K, V>();
		for (V v : col) {
			ret.put(keyF.e(v), v);
		}
		return ret;
	}

	public static <K,V> Map<V,K> reverseMulti(Map<K, Collection<V>> groups) {
		HashMap<V, K> ret = new HashMap<>();
		for (Entry<K, Collection<V>> entry : groups.entrySet()) {
			for (V v : entry.getValue()) {
				ret.put(v, entry.getKey());
			}
		}
		return ret;
	}
}

