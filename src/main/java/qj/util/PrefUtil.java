package qj.util;

import qj.util.funct.F0;
import qj.util.funct.Fs;
import qj.util.funct.P1;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

@SuppressWarnings({"rawtypes","unchecked"})
public class PrefUtil {
    public static String getString(String key) {
		final Class callerClass = LangUtil.getTraceClass(1);
        return Preferences.userNodeForPackage(callerClass).get(key, null);
    }
    
//    public static void main(String[] args) {
//		System.out.println(PrefUtil.getString("gunnytool.branch", "he he"));
//	}

    public static void clear() {
		final Class callerClass = LangUtil.getTraceClass(1);
		clear(callerClass);
    }

	public static void clear(final Class callerClass) {
		try {
			Preferences.userNodeForPackage(callerClass).clear();
		} catch (BackingStoreException e) {
			throw new RuntimeException(e);
		}
	}
    
    public static String getString(String key, String def) {
        final Class callerClass = LangUtil.getTraceClass(1);
        return getString(key, def, callerClass);
    }

	public static String getString(String key, String def,
			final Class callerClass) {
		return Preferences.userNodeForPackage(callerClass).get(key, def);
	}
    public static boolean getBoolean(String key) {
        final Class callerClass = LangUtil.getTraceClass(1);
        return getBoolean(key, callerClass);
    }
	public static boolean getBoolean(String key, final Class callerClass) {
		boolean value = Preferences.userNodeForPackage(callerClass).getBoolean(key, false);
		return value;
	}
	public static boolean getBoolean(String key, boolean defau) {
        final Class callerClass = LangUtil.getTraceClass(1);
        return getBoolean(key, defau, callerClass);
	}

	public static boolean getBoolean(String key, boolean defau,
			final Class callerClass) {
		return Preferences.userNodeForPackage(callerClass).getBoolean(key, defau);
	}

    public static void setBoolean(String key, boolean value) {
        Class clazz = LangUtil.getTraceClass(1);
        setBoolean(key, value, clazz);
    }

    public static P1<Boolean> setBooleanF(final String key) {
        final Class clazz = LangUtil.getTraceClass(1);
        return setBooleanF(key, clazz);
    }

	public static P1<Boolean> setBooleanF(final String key, final Class clazz) {
		return new P1<Boolean>() {public void e(Boolean value) {
            Preferences.userNodeForPackage(clazz).putBoolean(key, value);
        }};
	}
    public static P1<Integer> setIntF(final String key) {
        final Class clazz = LangUtil.getTraceClass(1);
//        System.out.println(clazz);
        return new P1<Integer>() {public void e(Integer value) {
            Preferences.userNodeForPackage(clazz).putInt(key, value);
        }};
    }
    
	public static void setBoolean(String key, boolean value, Class callerClass) {
//		System.out.println(key + " set to " + value + " for " + callerClass);
		Preferences.userNodeForPackage(callerClass).putBoolean(key, value);
	}
    public static int getInt(String key, int def) {
        final Class callerClass = LangUtil.getTraceClass(1);
        return Preferences.userNodeForPackage(callerClass).getInt(key, def);
    }
    public static F0<Integer> getIntF(final String key, final int def) {
        final Class callerClass = LangUtil.getTraceClass(1);
        return new F0<Integer>() {public Integer e() {
			return Preferences.userNodeForPackage(callerClass).getInt(key, def);
		}};
    }
    
    public static boolean getString(String key, boolean def) {
        final Class callerClass = LangUtil.getTraceClass(1);
        return getBoolean(key, def, callerClass);
    }
    public static F0<String> stringF(final String key) {
        final Class callerClass = LangUtil.getTraceClass(1);
        return getF(key, callerClass);
    }
    public static F0<String> getF(final String key, final String def) {
    	final Class callerClass = LangUtil.getTraceClass(1);
    	return new F0<String>() {
            public String e() {
                return Preferences.userNodeForPackage(callerClass).get(key, def);
            }
        };
    }

    public static <A> F0<A> getObjectF(final String key) {
        final Class callerClass = LangUtil.getTraceClass(1);
        return new F0<A>() {
            public A e() {
                return PrefUtil.<A>getObject1(key, callerClass, null);
            }
        };
    }

    public static F0<String> getF(final String key, final Class callerClass) {
        return new F0<String>() {
            public String e() {
                return Preferences.userNodeForPackage(callerClass).get(key, null);
            }
        };
    }
    public static F0<String> getF(final String key) {
        return getF(key, LangUtil.getTraceClass(1));
    }

    public static <A> A getObject(final String key) {
        final Class callerClass = LangUtil.getTraceClass(1);
        return PrefUtil.<A>getObject1(key, callerClass, null);
    }
    
    public static <A> A getObject(final String key, A def) {
        final Class callerClass = LangUtil.getTraceClass(1);
        return PrefUtil.<A>getObject1(key, callerClass, def);
    }

    public static <A> A getObject1(String key, Class callerClass, A def) {
        byte[] bytes = Preferences.userNodeForPackage(callerClass).getByteArray(key, null);
        A value = (A) ReflectUtil.byteArrayToObject(bytes, def);
//		System.out.println(key + " get to " + value + " for " + callerClass);
		return value;
    }

    public static <A> void setObject(final String key, A a) {
        final Class callerClass = LangUtil.getTraceClass(1);
        setObject(key, a, callerClass);
    }
	public static <A> void setObject(final String key, A a,
			final Class callerClass) {
//		System.out.println(key + " set to " + a + " for " + callerClass);
		byte[] bytes = ReflectUtil.objectToByteArray(a);
        Preferences.userNodeForPackage(callerClass).putByteArray(key, bytes);
	}

    public static void set(String key, String value) {
        Class clazz = LangUtil.getTraceClass(1);
        Preferences.userNodeForPackage(clazz).put(key, value);
    }
    public static void setInt(String key, int value) {
        Class clazz = LangUtil.getTraceClass(1);
        Preferences.userNodeForPackage(clazz).putInt(key, value);
    }

	public static P1<String> setF(F0<String> key) {
        final Class callerClass = LangUtil.getTraceClass(1);
		return setF(key, callerClass);
	}

    public static P1<String> setF(final String key, final Class clazz) {
    	return setF(Fs.f0(key), clazz);
    }

    public static <A> P1<A> setObjectF(final String key) {
        final Class callerClass = LangUtil.getTraceClass(1);
        return new P1<A>() {public void e(A a) {
        	setObject(key, a, callerClass);
        }};
    }
    
    public static P1<String> setF(final F0<String> keyF, final Class clazz) {
        return new P1<String>() {public void e(String value) {
            Preferences.userNodeForPackage(clazz).put(keyF.e(), value);
        }};
    }

    public static <A> A getObject(String key, Class<A> clazz) {
        final Class callerClass = LangUtil.getTraceClass(1);
        A o = PrefUtil.<A>getObject1(key, callerClass, null);
        if (o != null) {
            return o;
        } else {
            return ReflectUtil.newInstance(clazz);
        }
    }
	public static P1<String> setF(String key) {
        final Class callerClass = LangUtil.getTraceClass(1);
		return setF(Fs.f0(key), callerClass);
	}

}
