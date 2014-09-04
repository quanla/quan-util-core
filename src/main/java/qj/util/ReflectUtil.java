package qj.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import qj.util.funct.F0;
import qj.util.funct.F1;
import qj.util.funct.F2;
import qj.util.funct.F3;
import qj.util.funct.F4;
import qj.util.funct.P0;
import qj.util.funct.P1;
import qj.util.funct.P2;
import qj.util.funct.P3;
import qj.util.funct.P4;

/**
 * Primary util Created by QuanLA Date: Mar 13, 2006 Time: 10:02:43 AM
 */
@SuppressWarnings("unchecked")
public class ReflectUtil {

	public static <A> A newInstance(Class<A> cla) {
        return (A) newInstance4(cla);
	}
	
	/**
	 * Create a new instance of the clazz
	 * @param clazz
	 * @param params
	 * @return
	 */
	public static <A> A newInstance(Class<A> clazz, Object... params) {
        try {
            return (A) getConstructor(params, clazz).newInstance(params);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
	}

	public static <A> A clone(A a1) {
		if (a1==null) {
			return null;
		}
		
		Class<A> clazz = (Class<A>) a1.getClass();
		A a2 = newInstance(clazz);
		for (Field field : getAllFields(clazz).values()) {
			Object value = getFieldValue(field, a1);
			setFieldValue(value, field, a2);
		}
		return a2;
	}
	public static <F,T> void copy(T t, F f) {
		if (f==null) {
			throw new NullPointerException("From object is null");
		}

		HashMap<String, Field> toFields = getAllFields(t.getClass());
		for (Field field : getAllFields(f.getClass()).values()) {
			Field toField = toFields.get(field.getName());
			if (toField != null && toField.getType().equals(field.getType())) {
				Object value = getFieldValue(field, f);
				setFieldValue(value, toField, t);
			}
		}
	}

    public static Method getMethod(String methodName, Class clazz) {
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		if (!clazz.equals(Object.class)) {
			Class superclass = clazz.getSuperclass();
			if (superclass != null) {
				return getMethod(methodName, superclass);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
    

	public static String objectToString(Object o) {
		byte[] ba = objectToByteArray(o);
		if (ba == null)
			return null;
		else
			return new String(ba);
	}
	
	public static byte[] objectToByteArray(Object o) {
		if (o == null)
			return null;

		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(bo);
			oos.writeObject(o);
			oos.flush();
			return bo.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				bo.close();
				oos.close();
			} catch (IOException e) {
			}
		}
	}

	public static Object byteArrayToObject(byte[] ba) {
		return byteArrayToObject(ba, null);
	}

	public static Object byteArrayToObject(byte[] ba, Object def) {
		if (ba == null)
			return def;

		try {

			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(ba));
			Object o = ois.readObject();
			ois.close();
			return o;
		} catch (IOException e) {
//			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static Object stringToObject(String str) {
		if (str == null)
			return null;
		return byteArrayToObject(str.getBytes());
	}

	/**
	 * Get the object's serialized size
	 * @param o Object to measure size
	 * @return int size in int, -1 if has problem - non serializable
	 */
	public static int objectSize(Object o) {
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream(1024);
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(o);
			oo.flush();
//			byte[] ba = bo.toByteArray();
			return bo.size();
		} catch (IOException e) {
			return -1;
		}
	}

	/**
	 * 
	 * @param fullQualifiedClassName
	 * @return NonQualifiedClassName
	 */
	public static String getNonQualifiedClassName(String fullQualifiedClassName) {
		return fullQualifiedClassName.substring(fullQualifiedClassName.lastIndexOf('.') + 1);
	}

	public static Runnable runnable(String clazz, String method, Object[] params) throws ClassNotFoundException {
		return new _Runnable(Class.forName(clazz), method, params);
	}

	public static Runnable runnable(Class clazz, String method, Object[] params) {
		return new _Runnable(clazz, method, params);
	}

	public static Runnable runnable(String clazz, String method) throws ClassNotFoundException {
		return runnable(clazz, method, null);
	}

	public static Runnable runnable(Class clazz, String method) {
		return runnable(clazz, method, null);
	}
	
	/**
	 * Invoke the method with given params
	 * @param method
	 * @param o
	 * @param params
	 * @return
	 */
	public static Object invoke(String methodName, Object o, Object... params) {
		return invoke(getMethod(methodName, o.getClass()), o, params);
	}

	/**
	 * Invoke the method with given params
	 * @param method
	 * @param o
	 * @param params
	 * @return
	 */
    public static <T> T invoke(Method method, Object o, Object... params) {
        try {
            return (T) method.invoke(o, params);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
    }

	public static F1<Object[], Object> invokeF(final Method method, final Object object) {
		return new F1<Object[], Object>() {public Object e(Object[] params) {
			return invoke(method, object, params);
		}};
	}

    public static Field getField(String name, Class<?> clazz) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (Object.class.equals(superClass)) {
                return null;
            } else {
                return getField(name, superClass);
            }
        }
    }

    public static Method getMethod(String methodName, Class[] paramClasses, Class<?> clazz) {
        try {
            return clazz.getMethod(methodName, paramClasses);
        } catch (NoSuchMethodException e) {
            if (!clazz.equals(Object.class)) {
				Class<?> superclass = clazz.getSuperclass();
				if (superclass != null) {
					return getMethod(methodName, paramClasses, superclass);
				}
				return null;
			} else {
				return null;
			}
        }
    }
    public static Method deepFindMethod(String method, Class clazz) {
        return deepFindMethod(method, null, clazz);
    }

    public static Method deepFindMethod(String method, Class[] paramTypes, Class clazz) {
        Method oMethod;
        while ((oMethod = findMethod(method, paramTypes, clazz))==null && !clazz.equals(Object.class)) {
            clazz = clazz.getSuperclass();
        }
        return oMethod;
    }
    public static Method findMethod(String method, Class[] paramTypes, Class clazz) {
        Method[] methods = clazz.getMethods();
        for (Method method1 : methods) {
            if (method1.getName().equals(method)
                    && (paramTypes == null || method1.getParameterTypes().length == paramTypes.length))
            return method1;
        }
        return null;
    }

    public static Map<Class<?>, Class<?>> implementations = Cols.map(
    		List.class, LinkedList.class,
    		Set.class, HashSet.class,
    		Collection.class, LinkedList.class,
    		Map.class, HashMap.class
    		);
    public static Object newInstance4(Class clazz) {
    	if (clazz.isInterface()) {
    		clazz = implementations.get(clazz);
    	}
    	
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
            Throwable cause = e.getCause();
            if (cause==null) {
            	cause = e;
            }
			throw new RuntimeException(cause);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
    }

    /**
     * Get the constructor suitable for these params
     * @param params
     * @param clazz
     * @return
     */
    @SuppressWarnings("rawtypes")
	private static Constructor getConstructor(Object[] params, Class clazz) {
        Constructor[] constructors = clazz.getDeclaredConstructors();
        for (int i = 0; i < constructors.length; i++) {
            Constructor constructor = constructors[i];
            if (constructor.getParameterTypes().length == params.length) {
                return constructor;
            }
        }
        return null;
    }

    public static <A> A getFieldValue(Field field, Object obj) {
        try {
            return (A) field.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


	public static <A> A getFieldValue(String field, Object obj) {
		return getFieldValue(getField(field, obj.getClass()), obj);
	}

    public static void setFieldValue(Object value, String field, Object obj) {
        try {
            setFieldValue(value, obj.getClass().getDeclaredField(field), obj);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setFieldValue(Object value, Field field, Object obj) {
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static class _Runnable implements Runnable {
		private final Class clazz;

		private String method;

		private Object[] params;

		public _Runnable(Class clazz, String method, Object[] params) {
			super();
			this.clazz = clazz;
			this.method = method;
			this.params = params;
		}

		public void run() {
			Method[] methods = clazz.getMethods();
            for (Method method1 : methods) {
                if (method1.getName().equals(method)
                        && (params == null || method1.getParameterTypes().length == params.length))
                    if (params == null) {
                        params = new Object[method1.getParameterTypes().length];
                    }
                invoke(method1, newInstance4(clazz), params);
            }
		}

	}

	
	public static void main(String[] args) {
		byteArrayToObject(objectToByteArray(new ArrayList()));
	}
	public static Class<?> forName(String clazz) {
		return forName(clazz, ReflectUtil.class.getClassLoader());
	}
	public static Class<?> forName(String clazz, ClassLoader classLoader) {
		try {
			return Class.forName(clazz, true, classLoader);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	//    
	// public static void main(String[] args) {
	// System.out.println(isSubClass(String.class, String.class));
	// // System.out.println(isSubClass(List .class, Vector.class));
	// System.out.println(isSubClass(Vector.class, List.class));
	//
	// System.out.println(isSubClass(Vector.class, Collection.class));
	//		
	// System.out.println(isSubClass(Set.class, Collection.class));
	//		
	// }
	//    
	// public static boolean isSubClass(Class child, Class father) {
	// System.out.println("isSubClass - [" + child.getName() + ", " +
	// father.getName() + "]");
	//    	
	// if (child.equals(father))
	// return true;
	//
	// if ( ! child.isInterface() && ! father.isInterface()) {
	// Class tempFather;
	//    		
	// while ((tempFather = child.getSuperclass()) != null && !
	// tempFather.equals(father))
	// ;
	//    		
	// if (tempFather == null)
	// return false;
	// else
	// return true;
	// } else if ( father.isInterface() ) {
	// Class[] interfaces = child.getInterfaces();
	//    		
	// for (int i = 0; i < interfaces.length; i++) {
	// Class interf = interfaces[i];
	// System.out.println(interfaces[i].getName());
	// if (isSubClass(interf, father))
	// return true;
	// }
	//    		
	// return false;
	//    		
	// } else
	// return false; // Child: interface, father: class
	// }




	public static HashMap<String, Method> getAllMethods(Class<?> clazz) {
		final HashMap<String,Method> methods = new HashMap<String,Method>();
		getMethods(clazz, methods, 0);
		return methods;
	}
	private static void getMethods(Class<?> clazz,
			HashMap<String, Method> methods, int paramCount) {
		for (final Method method : clazz.getDeclaredMethods()) {
//			int modifiers = method.getModifiers();
			if (method.getParameterTypes().length != paramCount) {
				continue;
			}
			String name = method.getName();
	
			if (methods.containsKey(name)) {
				continue;
			}
			methods.put(name,method);
		}
		
		if (clazz.getSuperclass() != Object.class) {
			getMethods(clazz.getSuperclass(), methods, paramCount);
		}
	}

	public static HashMap<String, Field> getAllFields(Class<?> clazz) {
		final HashMap<String,Field> fields = new LinkedHashMap<String,Field>();
		getFields(clazz, fields);
		return fields;
	}

	private static void getFields(Class<?> clazz, HashMap<String, Field> fields) {
		for (final Field field : clazz.getDeclaredFields()) {
			int modifiers = field.getModifiers();
			if ((modifiers & (Modifier.STATIC | Modifier.FINAL | Modifier.TRANSIENT)) > 0
					|| (modifiers & (Modifier.PUBLIC)) == 0
					) {
				continue;
			}
			
			String name = field.getName();
	
			if (fields.containsKey(name)) {
				continue;
			}
			fields.put(name,field);
		}
		
		if (clazz.getSuperclass() != Object.class) {
			getFields(clazz.getSuperclass(), fields);
		}
	}
	/**
	 * Invoke static method
	 * @param methodName
	 * @param objects
	 * @param clazz
	 * @return
	 */
	public static Object invokeMethod(String methodName, Object[] objects, Class<?> clazz) {
		return invoke(getMethod(methodName, clazz), null, objects);
	}


	@SuppressWarnings("rawtypes")
	public static Map toMap(Object o) {
		LinkedHashMap ret = new LinkedHashMap();
		for (Entry<String, Field> entry : getAllFields(o.getClass()).entrySet()) {
			ret.put(entry.getKey(), getFieldValue(entry.getValue(), o));
		}
		return ret;
	}

	@SuppressWarnings("rawtypes")
	public static <A> A toFunction(final Method method, final Object obj) {
		int parameterCount = method.getParameterTypes().length;
		
		if (method.getReturnType().equals(void.class)) {
			// All P
			if (parameterCount==0) {
				return (A) new P0() {public void e() {
					ReflectUtil.invoke(method, obj);
				}};
			} else if (parameterCount==1) {
				return (A) new P1() {public void e(Object a1) {
					ReflectUtil.invoke(method, obj, a1);
				}};
			} else if (parameterCount==2) {
				return (A) new P2() {public void e(Object a1, Object a2) {
					ReflectUtil.invoke(method, obj, a1, a2);
				}};
			} else if (parameterCount==3) {
				return (A) new P3() {public void e(Object a1, Object a2, Object a3) {
					ReflectUtil.invoke(method, obj, a1, a2, a3);
				}};
			} else if (parameterCount==4) {
				return (A) new P4() {public void e(Object a1, Object a2, Object a3, Object a4) {
					ReflectUtil.invoke(method, obj, a1, a2, a3, a4);
				}};
			}
		} else {
			// All F
			if (parameterCount==0) {
				return (A) new F0() {public Object e() {
					return ReflectUtil.invoke(method, obj);
				}};
			} else if (parameterCount==1) {
				return (A) new F1() {public Object e(Object a1) {
					return ReflectUtil.invoke(method, obj, a1);
				}};
			} else if (parameterCount==2) {
				return (A) new F2() {public Object e(Object a1, Object a2) {
					return ReflectUtil.invoke(method, obj, a1, a2);
				}};
			} else if (parameterCount==3) {
				return (A) new F3() {public Object e(Object a1, Object a2, Object a3) {
					return ReflectUtil.invoke(method, obj, a1, a2, a3);
				}};
			} else if (parameterCount==4) {
				return (A) new F4() {public Object e(Object a1, Object a2, Object a3, Object a4) {
					return ReflectUtil.invoke(method, obj, a1, a2, a3, a4);
				}};
			}
		}
		throw new IllegalArgumentException("Unsupported too many parameters");
	}

	public static void eachAnnotatedMethods(Class<?> clazz,
			final Class<? extends Annotation> annoClass, final P1<Method> p) {
		eachMethod(clazz, new P1<Method>() {public void e(Method obj) {
			if (obj.getAnnotation(annoClass) != null) {
				p.e(obj);
			}
		}});
		
	}

	private static void eachMethod(Class<?> clazz, P1<Method> p1) {
		for (Method m : clazz.getMethods()) {
			p1.e(m);
		}
	}

	/**
	 * Create new instance
	 */
	public static <A> F0<A> newInstanceF(final Class<A> clazz) {
		return new F0() {
			public Object e() {
				return ReflectUtil.newInstance(clazz);
			}
		};
	}

	public static <A> F1<Object, A> newInstanceF1(final Class<A> clazz) {
		return new F1<Object, A>() {
			public A e(Object param) {
				return ReflectUtil.newInstance(clazz, param);
			}
		};
	}
}
