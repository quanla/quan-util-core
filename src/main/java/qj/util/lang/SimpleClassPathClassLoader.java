package qj.util.lang;

import java.util.HashSet;
import java.util.Set;

public abstract class SimpleClassPathClassLoader extends ClassLoader {

	Set<String> loadedClasses = new HashSet<String>();
	Set<String> unavaiClasses = new HashSet<String>();
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		try {
			return Class.forName(name);
		} catch (Exception e) {
		}
		
		if (loadedClasses.contains(name) || unavaiClasses.contains(name)) {
			return super.loadClass(name);
		}
		
		byte[] newClassData = loadNewClass(name);
		if (newClassData != null) {
			loadedClasses.add(name);
			return loadClass(newClassData, name);
		} else {
			unavaiClasses.add(name);
			return super.loadClass(name);
		}
	}
	
	/**
	 * Handle exception
	 * @param name
	 * @return
	 */
	public Class<?> load(String name) {
		try {
			return loadClass(name);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected abstract byte[] loadNewClass(String name);

	

	public Class<?> loadClass(byte[] classData, String name) {
		Class<?> clazz = defineClass(name, classData, 0, classData.length);
		if (clazz != null) {
			if (clazz.getPackage() == null) {
				definePackage(name.replaceAll("\\.\\w+$", ""), null, null, null, null, null, null, null);
			}
			resolveClass(clazz);
		}
		return clazz;
	}

	public static String toFilePath(String name) {
		return name.replaceAll("\\.", "/") + ".class";
	}
}