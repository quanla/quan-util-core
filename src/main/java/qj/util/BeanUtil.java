package qj.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BeanUtil extends BeanUtil4 {
	/**
	 * Link all the beans together by calling to their setter methods 
	 * and set appropriate bean
	 * @param beans
	 */
	public static void wireBeans(Object... beans) {
		for (Object bean : beans) {
			wireBean(bean, beans);
		}
	}
	
	public static void main(String[] args) {
		
	}

	/**
	 * Link the bean to the beans array by calling to its setter methods 
	 * and set appropriate element in the beans array
	 * @param bean
	 * @param beans
	 */
	public static void wireBean(Object bean, Object... beans) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
			for (PropertyDescriptor p : beanInfo.getPropertyDescriptors()) {
				Object target = getByType(p.getPropertyType(), beans);
				if (target == null) {
					continue;
				}
				
				Method writeMethod = p.getWriteMethod();
				if (writeMethod != null) {
//					System.out.println("getPropertyType" + p.getPropertyType());
					writeMethod.invoke(bean, target);
				}
			}
		} catch (Exception e) {
//			System.out.println(bean.getClass());
			throw new RuntimeException(e);
		}
	}

	private static Object getByType(Class<?> propertyType, Object[] beans) {
		if ( propertyType != null 
				&& !propertyType.getName().startsWith("java.awt")
				&& !propertyType.getName().startsWith("javax.swing")
				) {
			for (Object object : beans) {
				if (propertyType.isAssignableFrom(object.getClass())) {
					return object;
				}
			}
		}
		return null;
	}

	public static <A extends Annotation> A getAnnotation(PropertyDescriptor descriptor,
			Class<?> clazz, Class<A> annoClazz) {
		try {
            Field field = ReflectUtil.getField(descriptor.getName(), clazz);
            if (field == null) {
                return null;
            }

            A annotation = field
					.getAnnotation(annoClazz);
			if (annotation != null) {
				return annotation;
			}
			if (descriptor.getWriteMethod() != null && (annotation = descriptor.getWriteMethod().getAnnotation(annoClazz)) != null) {
				return annotation;
			}
			annotation = descriptor.getReadMethod().getAnnotation(annoClazz);
			return annotation;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
