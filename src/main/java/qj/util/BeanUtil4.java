package qj.util;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class BeanUtil4 {
	public static void populate(Dictionary dic, Object bean) {
		Enumeration enu = dic.keys();
		while (enu.hasMoreElements()) {
			String key = (String) enu.nextElement();
			setAttribute(bean, key, dic.get(key));
		}
	}
	
	public static void copy(Object beanTo, Object beanFrom) {

		if (beanFrom instanceof Map) {
			Map map = (Map)beanFrom;
			Set<Entry> entrySet = map.entrySet();
			for (Entry entry : entrySet) {
				String attrName = (String) entry.getKey();
				Object val = entry.getValue();
				setAttribute(beanTo, attrName, val);
			}
//			map.put(attrName, value);
			return;
		}
		
		
		List getters = getGetters(beanFrom);
		for (Iterator iterator = getters.iterator(); iterator.hasNext();) {
			Method getter = (Method) iterator.next();
			try {
				String attrName = getterNameToAttrName(getter.getName());
				
				Object val = getter.invoke(beanFrom, null);
				
				setAttribute(beanTo, attrName, val);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * TODO unfinished
	 * @param bean
	 * @param attrName
	 * @param value
	 */
	public static void setAttribute(Object bean, String attrName, Object value) {

		if (bean instanceof Map) {
			((Map)bean).put(attrName, value);
			return;
		}
		try {
			
			List setters = getSetters(bean, attrName);
			
			for (int i = 0; i < setters.size(); i++) {
				Method method = (Method) setters.get(i);

				Class paraClass = method.getParameterTypes()[0];
				
//				if (value != null && !paraClass.isAssignableFrom(value.getClass())) {
//					continue;
//				}
				
				invokeSetter(bean, value, method, paraClass);
			}
			
		} catch (SecurityException e) {
//			e.printStackTrace();
		} catch (IllegalAccessException e) {
//			e.printStackTrace();
		} catch (InvocationTargetException e) {
//			e.getCause().printStackTrace();
		}
	}

	/**
	 * @param bean
	 * @param value
	 * @param method
	 * @param paraClass
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
	private static void invokeSetter(Object bean, Object value, Method method, Class paraClass) 
	throws IllegalAccessException, InvocationTargetException {
		if (value==null) {
			method.invoke(bean, new Object[] { null } );
		} else if (paraClass.isAssignableFrom(value.getClass())) {
			
			method.invoke(bean, new Object[] { value} );
			
		} else if (paraClass.equals(String[].class)) {
			
			String[] newValue = ((String) value).split("\\|");
			method.invoke(bean, new Object[]{ newValue });
			
//		} else if (! (value instanceof String) ) {
//			method.invoke(bean, new Object[]{ value });
//			return;

		} else if (paraClass.equals(int.class)) {

			Integer newValue = Integer.valueOf((String) value);
			method.invoke(bean, new Object[]{ newValue });

		} else if (paraClass.equals(short.class)) {

			Short newValue = Short.valueOf((String) value);
			method.invoke(bean, new Object[]{ newValue });

		} else if (paraClass.equals(boolean.class)) {
			boolean newValue = "1".equals( value ) || "TRUE".equalsIgnoreCase((String) value);
			method.invoke(bean, new Object[]{ Boolean.valueOf( newValue ) });

		} else if (paraClass.equals(float.class)) {

			Float newValue = Float.valueOf((String) value);
			method.invoke(bean, new Object[]{ newValue });

		} else if (paraClass.equals(long.class)) {

			Long newValue = Long.valueOf((String) value);
			method.invoke(bean, new Object[]{ newValue });

		} else if (paraClass.equals(char.class)) {
			if (((String) value).length() != 1)
				return;
			
			Character newValue = new Character(((String) value).charAt(0));
			method.invoke(bean, new Object[]{ newValue });

		} else if (paraClass.equals(byte.class)) {

			Byte newValue = value instanceof String ? Byte.valueOf((String) value) : (Byte)value;
			method.invoke(bean, new Object[]{ newValue });

		} else if (paraClass.equals(double.class)) {

			Double newValue = value instanceof String ? Double.valueOf((String) value) : (Double)value;
			method.invoke(bean, new Object[]{ newValue });

		} else if (paraClass.equals(BigDecimal.class)) {

			BigDecimal newValue = new BigDecimal(Double.parseDouble((String) value));
			method.invoke(bean, new Object[]{ newValue });

		} else if (paraClass.equals(BigInteger.class)) {

			BigInteger newValue = BigInteger.valueOf(Long.parseLong((String) value));
			method.invoke(bean, new Object[]{ newValue });
			
			//*** ... many more
		}
	}

	private static List getSetters(Object bean, String key) {
		ArrayList list = new ArrayList();
		
		String methodName = "set" + StringUtil4.upperCaseFirstChar(key);
		Method[] methods = bean.getClass().getMethods();
		
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (method.getName().equals(methodName) && method.getParameterTypes().length == 1)
				list.add(method);
		}
		return list;
	}

	private static final Pattern PTN_GETTER = Pattern.compile("(get|is)[A-Z0-9].+");
	public static List getGetters(Object bean) {
		ArrayList list = new ArrayList();
		
		Method[] methods = bean.getClass().getMethods();
		
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (PTN_GETTER.matcher(method.getName()).matches()
					&& !"getClass".equals(method.getName())
					&& method.getParameterTypes().length == 0
					&& Modifier.isPublic(method.getModifiers())
					&& !Modifier.isStatic(method.getModifiers())
					)
				list.add(method);
		}

		return list;
	}
	
	@SuppressWarnings("unchecked")
	public static Method getGetterMethod(String attributeName, Class clazz) {
//			System.out.println("attr[" + attributeName + "], class: " + clazz.getName());
		Method getter = null;
		try {
			getter = clazz.getMethod("get"
					+ StringUtil4.upperCaseFirstChar(attributeName), (Class[])null);
		} catch (NoSuchMethodException e) {
			try {
				getter = clazz.getMethod("is"
						+ StringUtil4.upperCaseFirstChar(attributeName), (Class[])null);
			} catch (NoSuchMethodException e1) {
//				throw new RuntimeException("Can not find reader method for attr[" + attributeName + "], class: " + clazz.getName());
			}
		}
		return getter;
	};
	

	public static String getterNameToAttrName(String getterName) {
		String replace = getterName.replaceAll("^get", "");
		if (replace.length() == getterName.length()) {
			replace = getterName.replaceAll("^is", "");
		}
		return StringUtil4.lowerCaseFirstChar(replace);
	};
	/**
	 * 
	 * @param attributeName
	 * @param bean
	 * @return
	 */
	public static Object getAttribute(String attributeName, Object bean) {
		try {
			Class clazz = bean.getClass();
			Method getter = getGetterMethod(attributeName, clazz);
			if (getter==null) {
				return null;
			}
			return getter.invoke(bean, (Object[])null);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static List getSetters(Object bean, Class class1) {
		ArrayList list = new ArrayList();
		
		Method[] methods = bean.getClass().getMethods();
		
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (method.getName().startsWith("set")
					&& !"getClass".equals(method.getName())
					&& Modifier.isPublic(method.getModifiers())
					&& !Modifier.isStatic(method.getModifiers())
					&& method.getParameterTypes().length == 1
					&& (class1 == null || method.getParameterTypes()[0].isAssignableFrom(class1))
					)
				list.add(method);
		}

		return list;
	}

	public static Object findByProperty(
			Object value, 
			String property,
			Object[] arr) {
		
    	if (arr == null || arr.length == 0)
    		return null;
    	
    	for (int i = 0; i < arr.length; i++) {
    		Object target = arr[i];
    		
    		Object targetProperty = null;
    		
			try {
				targetProperty = getAttribute(property, target);
			} catch (Exception e) {
			}
			
			if (value==targetProperty)
				return target;
			
			if (targetProperty==null || value==null)
				continue;
			
			if (targetProperty.equals(value)) {
				return target;
			}
		}
		return null;
	}

	public static Object[] searchByProperty(
			Object value, 
			String property,
			Object[] arr) {
		ArrayList list = new ArrayList();
		
    	// Same as findByProperty BEGIN ---- 
    	if (arr == null || arr.length == 0)
    		return null;
    	
    	for (int i = 0; i < arr.length; i++) {
    		Object target = arr[i];
    		
    		Object targetProperty = null;
    		
			try {
				targetProperty = getAttribute(property, target);
			} catch (Exception e) {
			}
			
			if (value==targetProperty) {
				list.add(target);
				continue;
			}

			if (targetProperty==null || value==null)
				continue;

			if (targetProperty.equals(value)) {
				list.add(target);
				continue;
			}
		}
    	// Same as findByProperty END ---- 
    	
		Object[] arrRet = (Object[]) Array.newInstance(arr.getClass().getComponentType(), list.size());
		return list.toArray(arrRet);
	}
	
	/**
	 * {@link}L2FProd.com 
	 * @param clazz
	 * @param propertyName
	 * @return
	 */
	public static Method getReadMethod(Class clazz, String propertyName) {
		Method readMethod = null;
		String base = StringUtil4.upperCaseFirstChar(propertyName);

		// Since there can be multiple setter methods but only one getter
		// method, find the getter method first so that you know what the
		// property type is. For booleans, there can be "is" and "get"
		// methods. If an "is" method exists, this is the official
		// reader method so look for this one first.
		try {
			readMethod = clazz.getMethod("is" + base, (Class[])null);
		} catch (Exception getterExc) {
			try {
				// no "is" method, so look for a "get" method.
				readMethod = clazz.getMethod("get" + base, (Class[])null);
			} catch (Exception e) {
				// no is and no get, we will return null
			}
		}

		return readMethod;
	}
	
	public static void waitToEquals(Object value, String property, Object bean) {
		Thread th = Thread.currentThread();
		int currpriority = th.getPriority();
		th.setPriority(1);

		while (true) {
			if (getAttribute(property, bean).equals(value)) {
				th.setPriority(currpriority);
				return;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Get a collection of only given property
	 * @param propertyName
	 * @param beans
	 * @return
	 */
	public static Collection sliceProperties(String propertyName, Collection beans) {
		// Rinat
		ArrayList ret = new ArrayList();
		
		for (Iterator iterator = beans.iterator(); iterator.hasNext();) {
			Object o = (Object) iterator.next();
			ret.add(getAttribute(propertyName, o));
		}
		
		return ret;
	}

	
//	static Cache2<Class<?>, String, PropertyDescriptor> propertyDescriptorCache = new Cache2<Class<?>, String, PropertyDescriptor>(new Function2<Class<?>, String, PropertyDescriptor>() {
//		public PropertyDescriptor e(Class<?> beanClass, String propName) {
//			try {
//				return new PropertyDescriptor(propName, beanClass);
//			} catch (IntrospectionException e) {
//				throw new RuntimeException(e);
//			}
//		}});
	
	public static void lineupProperties(Object bean, String[] propertyStrings) {
		
		// Line up
		FOR1:
		for (int toIndex = 0; toIndex < propertyStrings.length; toIndex++) {
			String val1 = (String) BeanUtil4.getAttribute(propertyStrings[toIndex], bean);
			if (StringUtil4.isEmpty(val1)) {
				for (int fromIndex = toIndex + 1; fromIndex < propertyStrings.length; fromIndex++) {
					String val2 = (String) BeanUtil4.getAttribute(propertyStrings[fromIndex], bean);
					if (StringUtil4.isNotEmpty(val2)) {
						// Replace
						setAttribute(bean, propertyStrings[toIndex], val2);
						setAttribute(bean, propertyStrings[fromIndex], null);
						continue FOR1;
					}
				}
				
				// Can not find a not empty val to replace
				break;
			}
		}
	}
	
}
