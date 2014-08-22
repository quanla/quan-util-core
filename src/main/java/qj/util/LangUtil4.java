package qj.util;


public class LangUtil4 {
	
	public static Class getTraceClass(int i) {
		StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
		StackTraceElement ele = stackTrace[1 + i];
		
		try {
			String className = ele.getClassName();
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static String getTraceMethod(int i) {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StackTraceElement ele = stackTrace[i];
		
		return ele.getMethodName();

	}
	
	/**
	 * Get name of calling method
	 * @return
	 */
	public static String getCallingMethod() {
		return getTraceMethod(4);
	}
	
	public static Class getCurrentClass() {
		return getTraceClass(1);
	}
	
	public static Object newInstance() {
		try {
			return getTraceClass(1).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

    public static String getClasspathDelimiter() {
        return SystemUtil.isWindows() ? ";" : ":";
    }

	/**
	 * 
	 * @param startWith The bytes to start with
	 * @param bytes The bytes to test
	 * @return
	 */
	public static boolean startWith(byte[] startWith, byte[] bytes) {
		if (bytes == null || startWith == null || bytes.length < startWith.length) {
			return false;
		}
		
		for (int i = 0; i < startWith.length; i++) {
			if (bytes[i] != startWith[i]) {
				return false;
			}
		}
		return true;
	}

}
