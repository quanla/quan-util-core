package qj.util.funct;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import qj.util.Cols;
import qj.util.DesktopUtil4;
import qj.util.DigestUtil;
import qj.util.ObjectUtil;
import qj.util.ReflectUtil;
import qj.util.StringUtil;

/**
 * The utility that employ idea of functional programming
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class Fs extends FsGenerated {

	public static F0<Boolean> booleanRef(final Boolean[] b) {
		return new F0<Boolean>() {
			public Boolean e() {
				return b[0];
			}
		};
	}
	public static F0<Boolean> booleanRef(final boolean[] b) {
		return new F0<Boolean>() {
			public Boolean e() {
				return b[0];
			}
		};
	}

	public static <A> P0 p0(final P1<A> p1, final F0<A> aF) {
		return new P0(){public void e() {
			p1.e(aF.e());
		}};
	}
	
    /**
	 * Cache the returned value of the function so that
	 * one same argument will not invoke function's evaluate method twice
	 * @param <A>
	 * @param <B>
	 * @param function
	 * @return
	 */
	public static <A, B> F1Cache<A, B> cache(final F1<A, B> function) {
		return new F1Cache<A,B>(function);
	}

	public static <A> F1<A,A> f1() {
		return new F1<A,A>() {public A e(A a) {
			return a;
		}};
	}
	
    public static <A, R> F1<A, R> f1(final Map<A, R> map) {
        return new F1<A, R>() {
            public R e(A key) {
                return map.get(key);
            }
        };
    }

    public static <A, R> F1<A, R> f1Map(final Map<A, R> m1, final Map<A, R> m2) {
        return new F1<A, R>() {
            public R e(A key) {
                R val;
                if (m1 != null && (val = m1.get(key)) != null) {
                    return val;
                } else {
                    if (m2 != null && (val = m2.get(key)) != null) {
                        return val;
                    } else {
                        return null;
                    }
                }
            }
        };
    }
    
    public static <A,R> F1<A,R> cache(final F1<A,R> f, final F1<A,R> cacheGet, final P2<A,R> cacheSet) {
    	return new F1<A, R>() {public R e(A index) {
    		R cachedResult = cacheGet.e(index);
            if (cachedResult == null) {
                cachedResult = f.e(index);
                cacheSet.e(index, cachedResult);
            }
            return cachedResult;
		}};
    }
    public static F1<String,String> cacheDigest(final F1<String,String> f, final F1<String,String> cacheGet, final P2<String,String> cacheSet) {
    	return new F1<String,String>() {public String e(String a) {
    		String digest = DigestUtil.digest(a);
    		String cachedResult = cacheGet.e(digest);
    		if (cachedResult == null) {
    			cachedResult = f.e(a);
    			cacheSet.e(digest, cachedResult);
    		}
    		return cachedResult;
    	}};
    }

    public static class F1Cache<A, B> implements F1<A, B> {
        public Map<A, B> cache = new HashMap<A, B>();
        private F1<A, B> function;
        private P1<B> decor;
        
        public F1Cache(F1<A, B> function) {
            this.function = function;
        }

        public F1Cache(F1<A, B> function, P1<B> decor) {
			this.function = function;
			this.decor = decor;
		}

		public B e(A index) {
            B cachedResult = cache.get(index);
            if (cachedResult == null) {
                cachedResult = function.e(index);
                if (decor != null) {
					decor.e(cachedResult);
				}
				cache.put(index, cachedResult);
            }
            return cachedResult;
        }
        public Collection<B> getValues() {
            return cache.values();
        }
    }

    public static void printException(P0e p) {
        try {
            p.e();
        } catch (Exception e) {
            //
            e.printStackTrace();
        }
    }

    /**
     * WARNING: Invoke this e method by Reflection will not work
     * @param executeF
     * @return
     */
    public static Method eMethod(Object executeF) {
        return executeF.getClass().getMethods()[0];
    }

	public static Object deepEval(Object o) {
        if (o==null) {
            return null;
        } else if (o instanceof F0) {
            return deepEval(((F0)o).e());
        } else {
            return o;
        }
    }

    public static Object eval(Object oF, Object[] params) {
        if (oF == null) {
            return null;
        } else if (oF instanceof F0) {
            return ((F0)oF).e();
        } else if (oF instanceof F1) {
            return ((F1)oF).e(
                    params[0]
            );
        } else if (oF instanceof F2) {
            return ((F2)oF).e(
                    params[0],
                    params[1]

            );
        } else if (oF instanceof F3) {
            return ((F3)oF).e(
                    params[0],
                    params[1],
                    params[2]
            );
        } else if (oF instanceof F4) {
            return ((F4)oF).e(
                    params[0],
                    params[1],
                    params[2],
                    params[3]
            );
        } else if (oF instanceof F5) {
            return ((F5)oF).e(
                    params[0],
                    params[1],
                    params[2],
                    params[3],
                    params[4]
            );
        } else if (oF instanceof F6) {
            return ((F6)oF).e(
                    params[0],
                    params[1],
                    params[2],
                    params[3],
                    params[4],
                    params[5]
            );
        } else if (oF instanceof P0){
            ((P0)oF).e();
            return null;
        } else if (oF instanceof Runnable){
            ((Runnable)oF).run();
            return null;
        } else if (oF instanceof P1) {
            if (params.length == 1) { // TODO
				((P1) oF).e(params[0]);
			} else {
				((P1) oF).e(params);
			}
			return null;
        } else if (oF instanceof P2) {
            ((P2)oF).e(
                params[0],
                params[1]
            );
            return null;
        } else if (oF instanceof P3) {
            ((P3)oF).e(
                params[0],
                params[1],
                params[2]
            );
            return null;
        } else if (oF instanceof P4) {
            ((P4)oF).e(
                params[0],
                params[1],
                params[2],
                params[3]
            );
            return null;
        } else if (oF instanceof P5) {
            ((P5)oF).e(
                params[0],
                params[1],
                params[2],
                params[3],
                params[4]
            );
            return null;
        } else if (oF instanceof P6) {
            ((P6)oF).e(
                params[0],
                params[1],
                params[2],
                params[3],
                params[4],
                params[5]
            );
            return null;
        }
        throw new RuntimeException("Function class is not supported: " + oF.getClass());
    }

    /**
     * Find and return the static method of given class
     * @param methodName Name of the static method
     * @param className Name of the class to find the method
     * @param classLoader ClassLoader used to load class
     * @return The function that represent the found static method, when function's e() method called,
     *          the static method is called
     */
    public static <A> F0<A> f0(String methodName, String className, ClassLoader classLoader) {
        try {
            Class<?> clazz = Class.forName(className, true, classLoader);
            final Method method = clazz.getMethod(methodName, (Class[]) null);
            return f(method, null);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static <A, T> F0<T> f0(final P1<A> p1, final A a, final T ret) {
        return new F0<T>() {
            public T e() {
                p1.e(a);
                return ret;
            }
        };
    }

    public static <A, T> F1<A, T> get(final String attr, final Class<A> clazz) {
        final Method m = ReflectUtil.getMethod("get" + StringUtil.upperCaseFirstChar(attr), clazz );
        return new F1<A, T>() {public T e(A a) {
            return (T) ReflectUtil.invoke(m, a);
        }};
    }

	public static <A, T> F1<A, T> f1Runnable(
			final Runnable run, final T ret) {
		return new F1<A, T>() {public T e(A obj) {
            run.run();
			return ret;
		}};
	}

	public static <A, B, C> F2<A, B, C> f2(
			final Runnable run, final C c) {
		return new F2<A, B, C>() {public C e(A a, B b) {
			run.run();
			return c;
		}};
	}

	public static <A, B, C, T> F3<A, B, C, T> f3(
			final Runnable runnable, final T ret) {
		return new F3<A, B, C, T>() {public T e(A a, B b, C c) {
			runnable.run();
			return ret;
		}};
	}
    
	/**
	 *
	 * @param actions
	 * @return
	 */
	public static Runnable merge(final List<Runnable> actions) {
		return new Runnable() {
			public void run() {
				for (Runnable runnable : actions) {
					runnable.run();

					if (DesktopUtil4.isMouseUsed()) {
						return;
					}
				}
			}
		};
	}

    public static F0<Boolean> notEquals(final F0<String> f0, final String e) {
        return new F0<Boolean>() {
            public Boolean e() {
                return ObjectUtil.notEquals(f0.e(), e);
            }
        };
    }

    public static <A> F0<A> on(final F0<Boolean> test, final A then) {
        return on(test, then, null);
    }
    public static <A> F0<A> on(final F0<Boolean> test, final A then, final A elze) {
        return new F0<A>(){public A e() {
            if (test.e()) {
                return then;
            } else {
                return elze;
            }
        }
        };
    }
    
	public static <A, B> P1<A> p1(final F1<A, B> f1) {
		return new P1<A>() {public void e(A a) {
			f1.e(a);
		}
        };
	}

	public static <A, B> P2<A, B> p2(final Runnable runnable) {
		return new P2<A, B>() {public void e(A a, B b) {
			runnable.run();
		}};
	}

	public static <A, B, C> P3<A, B, C> p3(final Runnable runnable) {
		return new P3<A, B, C>() {public void e(A a, B b, C c) {
			runnable.run();
		}};
	}

	/**
	 * Get the boolean method and wrap it into a Function1<A, Boolean> object
	 * @param <A> The target class (class of Function1's param object)
	 * @param method Method name
	 * @param clazz The target class (class of Function1's param object)
	 * @return Function1<A, Boolean> object TODO Why Class<A>
	 */
	public static <A> F1<A, Boolean> predicate(final String method, final Class<A> clazz) {
        final Method m = ReflectUtil.getMethod(method, clazz );
        return new F1<A, Boolean>() {public Boolean e(A obj) {
            return (Boolean) ReflectUtil.invoke(m, null);
        }};
	}

	public static <A> void run(P1<A> p1, A a) {
		new Thread(Fs.runnable(p1, a)).start();
	}

	public static <A> Runnable runnable(final P1<A> p1,
			final A a) {
		if (p1 == null) {
			throw new NullPointerException();
		}
		return new Runnable() {public void run() {
			p1.e(a);
		}};
	}

	public static <A> Runnable runnable(final Object[] o, final String method) {
		return runnable(o[0], method);
	}
	public static <A> Runnable runnable(final Object o, final String method) {
		try {
			final Method m = o.getClass().getMethod(method, (Class[]) null);
			return new Runnable() {
				public void run() {
                    ReflectUtil.invoke(m, o);
				}
			};
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public static <A, B> Runnable runnable(
			final P2<A, B> p2,
			final A a, final B b) {
		return new Runnable() {public void run() {
			p2.e(a, b);
		}};
	}
	public static <A, B> Runnable runnable(
			final F2<A, B, ?> f2,
			final A a, final B b) {
		return new Runnable() {public void run() {
			f2.e(a, b);
		}};
	}

	public static <A, B, C> Runnable runnable(
			final P3<A, B, C> p3,
			final A a, final B b, final C c) {
		return new Runnable() {public void run() {
			p3.e(a, b, c);
		}};
	}

	public static <A> P1<A> sequel(final P1<A>... p1s) {
		return new P1<A>() {public void e(A obj) {
            for (P1 p1 : p1s) {
                if (p1 != null) {
                    p1.e(obj);
                }
            }
        }};
	}
	public static <A,B> P2<A,B> sequel(final P2<A,B>... p2s) {
		return new P2<A, B>() {public void e(A a, B b) {
			for (P2<A,B> p2 : p2s) {
				if (p2 != null) {
					p2.e(a, b);
				}
			}
		}};
	}

	public static <A> F1<A,A> sequel(final F1<A,A>... f1s) {
		return new F1<A,A>() {public A e(A obj) {
			A result = obj;
            for (F1<A,A> f1 : f1s) {
                if (f1 != null) {
                	result = f1.e(result);
                }
            }
            return result;
        }};
	}

	public static P0 sequel(final P0... p0s) {
		return new P0() {public void e() {
            for (P0 p0 : p0s) {
                if (p0 != null) {
                    p0.e();
                }
            }
        }};
	}
	
	public static <A> A wrap(final A object, Class<A> intf, final Runnable before, final Runnable after, final P1<Exception> onExc) {
		InvocationHandler handler = new InvocationHandler() {public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
			try {
				if (before!= null) {
					before.run();
				}
				Object result = method.invoke(object, args);
				if (after!= null) {
					after.run();
				}
				return result;

			} catch (Exception e) {
				onExc.e(e);
				throw e;
			}
		}};
		return (A) Proxy.newProxyInstance(intf.getClassLoader(), new Class<?>[] {intf}, handler);
	}

	/**
	 * Just store the object to collection
	 * @param col
	 * @return
	 */
    public static <A> P1<A> store(final Collection<A> col) {
        return new P1<A>() {public void e(A a) {
            col.add(a);
        }};
    }
    

    public static <A> F0<A> cache(final F0<A> f0) {
        final boolean[] alrRun = new boolean[] {false};
        final Object[] ret = new Object[] {null};
        return new F0<A>() {public A e() {
            if (!alrRun[0]) {
                ret[0] = f0.e();
                alrRun[0] = true;
            }
            return (A) ret[0];
        }};
    }

    public static <A> F1<Object[], A> fMulti(final Class<A> clazz) {
        return new F1<Object[], A>() {public A e(Object[] params) {
            return ReflectUtil.newInstance(clazz, params);
        }};
    }

    public static P0 invokeOnceF(final List<P0> fs) {
        return new P0() {public void e() {
            invokeOnce(fs);
        }};
    }
    public static <A> F0<A> invokeOnce(final F0<A> f0) {
    	final boolean[] invoked = {false};
    	final AtomicReference<A> ref = new AtomicReference<A>();
        return new F0<A>() {public A e() {
        	if (!invoked[0]) {
        		invoked[0] = true;
        		A ret = f0.e();
        		ref.set(ret);
				return ret;
        	} else {
        		return ref.get();
        	}
        }};
    }
	public static P0 invokeOnce(final P0 p0) {
		final boolean[] invoked = {false};
		return new P0() {public void e() {
			if (!invoked[0]) {
				p0.e();
				invoked[0] = true;
			}
		}};
	}
	

    public static void invokeOnce(Collection<P0> fs) {
    	LinkedList<P0> clone = new LinkedList<P0>(fs);
    	fs.clear();
		for (P0 p0 : clone) {
            p0.e();
		}
    }

    public static P0 p0(final Runnable run) {
        return new P0() {public void e() {
            run.run();
        }};
    }
    
    public static <A> P0 p0(final F0<A> f0) {
    	return new P0() {public void e() {
			f0.e();
		}};
    }

    public static List<Object> yield(ArrayList fs) {
		return Cols.yield(fs, new F1(){public Object e(Object obj) {
			return ((F0)obj).e();
		}});
	}

    public static <A> F1<A, Boolean> or(final F1<A,Boolean>... ors) {
        return new F1<A, Boolean>() {
            public Boolean e(A obj) {
            	for (F1<A, Boolean> or : ors) {
					if (or.e(obj)) {
						return true;
					}
				}
                return false;
            }
        };
    }
    public static <A> F1<A, Boolean> or(final Iterable<F1<A,Boolean>> ors) {
        return new F1<A, Boolean>() {
            public Boolean e(A obj) {
            	for (F1<A, Boolean> or : ors) {
					if (or.e(obj)) {
						return true;
					}
				}
                return false;
            }
        };
    }
    public static <A> F1<A, Boolean> and(final F1<A,Boolean>... ands) {
        return new F1<A, Boolean>() {
            public Boolean e(A obj) {
            	for (F1<A, Boolean> and : ands) {
					if (and != null && !and.e(obj)) {
						return false;
					}
				}
                return true;
            }
        };
    }
    public static <A> F1<A, Boolean> and(final Iterable<F1<A,Boolean>> ands) {
    	return new F1<A, Boolean>() {
    		public Boolean e(A obj) {
    			for (F1<A, Boolean> and : ands) {
    				if (and != null && !and.e(obj)) {
    					return false;
    				}
    			}
    			return true;
    		}
    	};
    }

    public static <A> BF1<A> and(final BF1<A>... ands) {
        return new BF1<A>() {
            public boolean e(A obj) {
            	for (BF1<A> and : ands) {
					if (and != null && !and.e(obj)) {
						return false;
					}
				}
                return true;
            }
        };
    }
    
    public static <A> F0<Boolean> not(final F0<Boolean> f) {
        return new F0<Boolean>() {
            public Boolean e() {
                return !f.e();
            }
        };
    }

    public static <A> F1<A, Boolean> not(final F1<A, Boolean> f) {
        return new F1<A, Boolean>() {
            public Boolean e(A obj) {
                return !f.e(obj);
            }
        };
    }

	public static <A,B> F2<A, B, Boolean> not(
			final F2<A, B, Boolean> f) {
		return new F2<A, B, Boolean>() {public Boolean e(A a, B b) {
			return !f.e(a, b);
		}};
	}

    public static <A,R> F1<A, R> storeFirst(final F2<A,A,R> f2) {
        final Object[] store = new Object[1];
        return new F1<A, R>() {
            public R e(A obj) {
                if (store[0] == null) {
                    store[0] = obj;
                }
                return f2.e(obj, (A) store[0]);
            }
        };
    }

    public static <A,B,C,R> List<R> invoke(List<F3<A,B,C,R>> list, A a, B b, C c) {
        ArrayList<R> ret = new ArrayList<R>();
        for (F3<A, B, C, R> f3 : list) {
            R r = f3.e(a, b, c);
            if (r != null) {
                ret.add(r);
            }
        }
        return ret;
    }
    public static <A> void invoke(Collection<P1<A>> list, A a) {
        for (P1<A> p1 : list) {
            p1.e(a);
        }
    }

    /**
     *
     * @return P1 that when called store f's return to col
     */
    public static <A, B> P1<A> store(final F1<A, B> f, final Collection<B> col) {
        return new P1<A>() {
            public void e(A obj) {
                col.add(f.e(obj));
            }
        };
    }

    public static Runnable runnable(final P0 action) {
        return new Runnable() {
            public void run() {
                action.e();
            }
        };
    }

    public static <A> F0<A> construct(final Class<A> clazz) {
        return new F0<A>() {public A e() {
            return ReflectUtil.newInstance(clazz);
        }};
    }

	public static P1<Integer> increase(final P1<Integer> p1) {
		return new P1<Integer>() {public void e(Integer obj) {
			p1.e(obj + 1);
		}};
	}

    public static <A, B, C> F1<A,C> chain (final F1<A,B> ab, final F1<? super B,C> bc) {
        return new F1<A, C>() {
            public C e(A obj) {
                B val1 = ab.e(obj);
				return bc.e(val1);
            }
        };
    }
	public static <A,B> F1<A,B> chain(final F1... fs) {
		return new F1() {public Object e(Object obj) {
			for (F1 f1 : fs) {
				obj = f1.e(obj);
			}
			return obj;
		}};
	}

    public static <A, R> P1<A> chain(final F1<A, R> fAR, final P1<R> pB) {
        return new P1<A>() {
            public void e(A a) {
                pB.e(fAR.e(a));
            }
        };
    }

    public static <K,V> Map<K,V> map(final F1<K,V> f1) {
        return new Map<K, V>() {

            // Read only map

            public V get(Object key) {
                return f1.e((K)key);
            }

            public int size() {throw new UnsupportedOperationException();}
            public boolean isEmpty() {throw new UnsupportedOperationException();}

			public boolean containsKey(Object key) {
				try {
					return f1.e((K) key) != null;
				} catch (Exception e) {
					return false;
				}
			}
            public boolean containsValue(Object value) {throw new UnsupportedOperationException();}
            public Object put(Object key, Object value) {throw new UnsupportedOperationException();}
            public V remove(Object key) {throw new UnsupportedOperationException();}

            public void putAll(Map<? extends K, ? extends V> m) {throw new UnsupportedOperationException();}
            public void clear() {throw new UnsupportedOperationException();}
            public Set<K> keySet() {throw new UnsupportedOperationException();}
            public Collection<V> values() {throw new UnsupportedOperationException();}
            public Set entrySet() {throw new UnsupportedOperationException();}

			@Override
			public V getOrDefault(Object key, V defaultValue) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void forEach(BiConsumer<? super K, ? super V> action) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void replaceAll(
					BiFunction<? super K, ? super V, ? extends V> function) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public V putIfAbsent(K key, V value) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean remove(Object key, Object value) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean replace(K key, V oldValue, V newValue) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public V replace(K key, V value) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public V computeIfAbsent(K key,
					Function<? super K, ? extends V> mappingFunction) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public V computeIfPresent(
					K key,
					BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public V compute(
					K key,
					BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public V merge(
					K key,
					V value,
					BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
				// TODO Auto-generated method stub
				return null;
			}
        };
    }

	public static void attempt(P0 run, F1<Exception, Boolean> onFail) {
		while (true) {
			try {
				run.e();
				break;
			} catch (RuntimeException e) {
				if (onFail.e(e)) {
					continue;
				} else {
					throw e;
				}
			}
		}
	}


	public static <A> A attemptGet(F0<A> f, P0 onFail) {
		A a = f.e();
		if (a==null) {
			onFail.e();
			return f.e();
		} else {
			return a;
		}
	}
	
	public static P1<Boolean> update(final boolean[] showOk) {
		return new P1<Boolean>() {public void e(Boolean obj) {
			showOk[0] = obj;
		}};
	}
	public static P0 update(final boolean[] var, final boolean newVal) {
		return new P0() {public void e() {
			var[0] = newVal;
		}};
	}

	public static P0 execF(final List<P0> fs) {
		return new P0() {public void e() {
			for (P0 p0 : fs) {
				p0.e();
			}
		}};
	}

	public static P0 merge(final Collection<P0> p0s) {
		return new P0() {public void e() {
			for (P0 p0 : p0s) {
				p0.e();
			}
		}};
	}

	public static F0<Boolean> or(final F0<Boolean>... f0s) {
		return new F0<Boolean>() {
			
			public Boolean e() {
				for (F0<Boolean> f0 : f0s) {
					if (f0.e()) {
						return true;
					}
				}
				return false;
			}
		};
	}

	public static F0<Boolean> and(final F0<Boolean>... f0s) {
		return new F0<Boolean>() {
			
			public Boolean e() {
				for (F0<Boolean> f0 : f0s) {
					if (!f0.e()) {
						return false;
					}
				}
				return true;
			}
		};
	}

	
	public static List<P0> invokeSequelF (final List<P1<F0<Boolean>>> ps, final P1<P0> asyncF) {
		final Boolean[][] cancelledRef = new Boolean[ps.size()][];
		final Boolean[][] startedRef = new Boolean[ps.size()][];
		
		ArrayList<P0> ret = new ArrayList<P0>();
		for (int i = 0; i < ps.size(); i++) {
			final int index = i;
			P1<F0<Boolean>> p = new P1<F0<Boolean>>() {public void e(F0<Boolean> interrupted) {
				for (int j = index; j < ps.size(); j++) {
					if (interrupted.e()) return;
					ps.get(j).e(interrupted);
				}
			}};
			ret.add(invokeF_inner(i, p, cancelledRef, startedRef, asyncF));
		}
		
		return ret;
	}
	
	public static P0 invokeRef(final P0[] p0Ref) {
		return new P0() {public void e() {
			if (p0Ref[0]!=null) {
				p0Ref[0].e();
			}
		}};
	}

	public static P0 invokeF(final Collection<P0> col) {
		return new P0() {public void e() {
			invokeAll(col);
		}};
	}
	
	public static P0 invokeF(final P1<F0<Boolean>> p, final P1<P0> asyncF) {
		final Boolean[][] cancelledRef = {null};
		final Boolean[][] startedRef = {null};
		
		return invokeF_inner(0, p, cancelledRef, startedRef, asyncF);
	}

	public static P0 invokeF_inner(final int index, final P1<F0<Boolean>> p,
			final Boolean[][] cancelledRef, final Boolean[][] startedRef,
			final P1<P0> asyncF) {
		return new P0() {public void e() {
//			System.out.println("!");
//			System.out.print(Thread.currentThread().getName() + "... ");
			// If parent or self scheduled, no need to do any thing
			for (int i = 0; i < index + 1; i++) {
				if (startedRef[i] != null && !startedRef[i][0] && !cancelledRef[i][0]) {
//					System.out.println("Returned as found pending " + i);
					return;
				}
			}

			// Cancel all childs and self
			for (int i = index; i < cancelledRef.length; i++) {
				if (cancelledRef[i] != null) {
					cancelledRef[i][0] = true;
//					System.out.println("Cancelled");
					cancelledRef[i] = null;
					startedRef[i] = null;
				}
			}

			final Boolean[] cancelled = {false};
			final Boolean[] started = {false};
			cancelledRef[index] = cancelled;
			startedRef[index] = started;
			final F0<Boolean> cancelledF = Fs.booleanRef(cancelled);
//			System.out.println("Schedulled " + index);
			asyncF.e(new P0() {public void e() {
				if (!cancelled[0] && !started[0]) {
					started[0] = true;
//					System.out.println("Started " + index);
					p.e(cancelledF);
//					System.out.println("Finished " + index);
				}
			}});
//			System.out.println("done.");
		}};
	}

	public static <A> P1<A> sequel1(final List<P1<A>> p1s) {
		return new P1<A>() {
			public void e(A obj) {
				for (P1<A> p1 : p1s) {
					p1.e(obj);
				}
			}
		};
	}
	public static <A,B> P2<A,B> sequel2(final List<P2<A,B>> p2s) {
		return new P2<A,B>() {
			public void e(A a, B b) {
				for (P2<A,B> p2 : p2s) {
					p2.e(a, b);
				}
			}
		};
	}

	public static P0 sequel0(final List<P0> p0s) {
		return new P0() {
			public void e() {
				for (P0 p1 : p0s) {
					p1.e();
				}
			}
		};
	}

	public static <A,B> F2<A, B, Boolean> and(
			final F2<A, B, Boolean> a1,
			final F2<A, B, Boolean> a2) {
		return new F2<A, B, Boolean>() {public Boolean e(A a, B b) {
			return a1.e(a, b) && a2.e(a, b);
		}};
	}
	
	public static P0 p0(final P0 p0, final P1<P0> invoker) {
		return new P0() {public void e() {
			invoker.e(p0);
		}};
	}
	public static P0 repeat(final int i, final P0 p0) {
		return new P0() {public void e() {
			for (int j = 0; j < i; j++) {
				p0.e();
			}
		}};
	}
	public static void invokeAll(final Collection<P0> col) {
		for (P0 p0 : col) {
			p0.e();
		}
	}
	public static void invokeAll_force(final Collection<P0> col) {
		for (P0 p0 : col) {
			try {
				p0.e();
			} catch (Exception e) {
				System.err.print(e.getMessage());
			}
		}
	}
	public static <A> void invokeAll_force(final Collection<P1<A>> col, A a) {
		for (P1<A> p1 : col) {
			try {
				p1.e(a);
			} catch (Exception e) {
				System.err.print(e.getMessage());
			}
		}
	}
	public static <A> void invokeAll(final Collection<P1<A>> col, A a) {
		for (P1<A> p1 : col) {
			p1.e(a);
		}
	}
	public static <A, B> void invokeAll(final Collection<P2<A,B>> col, A a, B b) {
		for (P2<A, B> p2 : col) {
			p2.e(a, b);
		}
	}

	public static P0 invokeAllP0(final Collection<P0> col) {
		return new P0() {public void e() {
			invokeAll(col);
		}};
	}

	public static <A> P1<A> invokeAllP1(final Collection<P1<A>> col) {
		return new P1<A>() {public void e(A a) {
			invokeAll(col, a);
		}};
	}

	public static <A,B> P2<A, B> invokeAllP2(final Collection<P2<A, B>> col) {
		return new P2<A, B>() {public void e(A a, B b) {
			invokeAll(col, a, b);
		}};
	}
	
	public static <A> P1<A> skip(int skips, final P1<A> p1) {
		final int[] s = {skips};
		return new P1<A>() {public void e(A a) {
			if (s[0] > 0) {
				s[0]--;
				return;
			}
			p1.e(a);
		}};
	}

	public static P0 atomicP0(final AtomicReference<P0> ref) {
		return new P0() {public void e() {
			P0 p0 = ref.get();
			if (p0!=null) {
				p0.e();
			}
		}};
	}
	public static <A> P1<A> atomicP1(final AtomicReference<P1<A>> ref) {
		return new P1<A>() {public void e(A obj) {
			P1<A> p1 = ref.get();
			if (p1!=null) {
				p1.e(obj);
			}
		}};
	}
	public static <A,B> P2<A,B> atomicP2(final AtomicReference<P2<A,B>> ref) {
		return new P2<A,B>() {public void e(A a, B b) {
			P2<A,B> p2 = ref.get();
			if (p2!=null) {
				p2.e(a, b);
			}
		}};
	}

	public static <A> F0 atomicF0(final AtomicReference<F0<A>> ref, final A defValue) {
		return new F0<A>() {public A e() {
			F0<A> f0 = ref.get();
			if (f0!=null) {
				return f0.e();
			}
			return defValue;
		}};
	}
	public static <A,T> F1<A,T> atomicF1(final AtomicReference<F1<A,T>> ref, final T defValue) {
		return new F1<A,T>() {public T e(A a) {
			F1<A,T> f1 = ref.get();
			if (f1!=null) {
				return f1.e(a);
			}
			return defValue;
		}};
	}
	public static P0 repeatUltimate(final P0 p, final boolean[] interrupted) {
		return new P0() {public void e() {
			while (!interrupted[0]) {
				p.e();
			}
//			System.out.println("interrupted 2");
		}};
	}
	public static <A> Iterable<A> iterable(final F0<A> f) {
		return new Iterable<A>() {public Iterator<A> iterator() {
			final AtomicReference<A> next = new AtomicReference<A>();
			return new Iterator<A>() {
				@Override
				public boolean hasNext() {
					next.set(f.e());
					return next.get() != null;
				}

				@Override
				public A next() {
					return next.get();
				}

				@Override
				public void remove() {
				}

				@Override
				public void forEachRemaining(Consumer<? super A> action) {
					// TODO Auto-generated method stub
					
				}
			};
		}

		@Override
		public void forEach(Consumer<? super A> action) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Spliterator<A> spliterator() {
			// TODO Auto-generated method stub
			return null;
		}};
	}
	public static <A> P1<A> setter(
			final AtomicReference<A> ref) {
		return new P1<A>() {public void e(A obj) {
			ref.set(obj);
		}};
	}
	public static <A> P1<A> setter(
			final A[] ref) {
		return new P1<A>() {public void e(A obj) {
			ref[0] = obj;
		}};
	}
	public static P0 nice(final P0 p0) {
		return new P0() {public void e() {
			try {
				p0.e();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}};
	}
	public static <A> F0<A> execNStore(final F0<A> f0,
			final Collection<A> col) {
		return new F0<A>() {public A e() {
			A a = f0.e();
			col.add(a);
			return a;
		}};
	}
	public static <A,B> P1<B> fixFirst(final P2<A,B> p2, final A a) {
		return new P1<B>() {public void e(B b) {
			p2.e(a, b);
		}};
	}
}
