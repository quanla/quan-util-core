package qj.util;

import qj.tool.file.FileSearch;
import qj.util.funct.F1;
import qj.util.funct.P1;
import qj.util.lang.DynamicClassLoader;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class LangUtil extends LangUtil4 {
	public static F1<byte[],String> bytesToStringF = obj -> new String(obj);
	public static F1<String,Long> parseLongF = obj -> Long.valueOf(obj);

	public static F1<Character, Boolean> isWord = c -> Character.isLetterOrDigit(c);
    
    public static Character[] toObjArr(char[] arr) {
        Character[] ret = new Character[arr.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = arr[i];
        }
        
        return ret;
    }

    public static char[] toPriArr(Character[] arr) {
        char[] ret = new char[arr.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = arr[i];
        }
        return ret;
    }

	public static void eachClass(String pkg, ClassLoader cl, P1<Class> f) {
		String pkgRootPath = pkg.replaceAll("\\.", "/");
		String[] classpaths = System.getProperty("java.class.path").split(";");
		for (int i = 0; i < classpaths.length; i++) {
			File file = new File(classpaths[i]);
			List<String> paths = getPaths(file, pkgRootPath);

			if (paths==null) {
				continue;
			}
			for (String path : paths) {
				try {
					Class<?> clazz = (cl == null ? LangUtil.class.getClassLoader() : cl).loadClass(path);
					f.e(clazz);

				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static final Pattern PTN = Pattern.compile("\\$\\d");
	private static List<String> getPaths(File file, String pkgRootPath) {
		if (file.isDirectory()) {
			final String filePath = file.getPath();
			File pkgDir = new File(filePath + "/" + pkgRootPath);
			if (!pkgDir.exists()) {
				return null;
			}
			
			List<String> yield = Cols.yield(Arrays.asList(FileSearch.searchFile(pkgDir.getPath(), ".class")), obj -> {
				if (PTN.matcher(obj.getPath()).find()) {
					return null;
				}
				return obj.getPath().substring(filePath.length() + 1).replaceAll("\\.class$", "").replaceAll("[\\\\/]", ".");
			});
			while (yield.contains(null)) {
				yield.remove(null);
			}
			return yield;
		} else {
			try {
				ZipFile zipFile = new ZipFile(file);
				Enumeration<?> en = zipFile.entries();
				ArrayList<String> list = new ArrayList<>();
				while (en.hasMoreElements()) {
					ZipEntry entry = (ZipEntry) en.nextElement();
					if (entry.isDirectory()) {
						continue;
					}
					if (!entry.getName().startsWith(pkgRootPath)) {
						continue;
					}
					if (PTN.matcher(entry.getName()).find()) {
						continue;
					}
					list.add(
						entry.getName()
						.replaceAll("\\.class$", "")
						.replaceAll("/", "."));
				}
				zipFile.close();
				return list;
			} catch (IOException e) {
//				e.printStackTrace();
				return null;
			}
		}
	}

    public static Throwable getRootCause(Throwable e) {
        Throwable cause = e.getCause();
        if (cause != null) {
            return getRootCause(cause);
        } else {
            return e;
        }
    }

    /**
     * Get the bit in byte.
     * Order for index is: 76543210
     * @param index
     * @param b
     * @return true if bit is 1, false if bit is 0
     */
    public static boolean getBit(int index, byte b) {
        return ((b >> index) & 1) == 1;
    }

    public static boolean isTrue(Object value) {
        if (value == null) {
            return false;
        } else if (value instanceof Boolean) {
            return (Boolean)value;
        } else {
            return Boolean.valueOf(value.toString());
        }
    }

	public static byte[] decrypt(byte[] serialize) {
		return encrypt(serialize);
	}

	public static byte[] encrypt(byte[] serialize) {
		for (int i = 0; i < serialize.length; i++) {
//			serialize[i] = shiftBits(serialize[i]);
			serialize[i] = (byte) (serialize[i] ^ 3629);
		}
		return serialize;
	}

//	static final int b3 = Integer.parseInt("1000", 2);
	public static byte shiftBits(byte b) {
		int upper = b << 4 & b1;
		int lower = b >> 4 & b2;
//		if (LangUtil.getBit(7, b)) {
//			lower = lower | b3;
//		}
		return (byte) (upper | lower);
	}

	public static final int b1 = Integer.parseInt("11110000", 2);
	public static final int b2 = Integer.parseInt("1111", 2);

	public static String getStackTrace(Throwable t) {
		StringWriter out = new StringWriter();
		t.printStackTrace(new PrintWriter(out));
		return out.toString();
	}

	public static ClassLoader classLoader(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return null;
		} else {
			return new DynamicClassLoader(path);
		}
	}

	public static int parseInt(String str) {
		if (str==null) {
			return -1;
		}
		try {
			return Integer.parseInt(str.trim());
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	public static Integer increase(Integer attempts) {
		if (attempts==null) {
			return 1;
		}
		return attempts+1;
	}
	
	public static F1<String,List<Double>> parseDoubleListF = obj -> {
		LinkedList<Double> ret = new LinkedList<>();
		for (String str : obj.split("\\s*,\\s*")) {
			ret.add(Double.valueOf(str));
		}
		return ret;
	};
	
	public static F1<String,TimeZone> timezoneF = obj -> TimeZone.getTimeZone(obj);

	public static byte[] toBytes(char[] chars) {
		byte[] ret = new byte[chars.length];
		for (int i = 0; i < chars.length; i++) {
			ret[i] = (byte)chars[i];
		}
		return ret;
	}
	
	public static void main(String[] args) {
		System.out.println(dumpStackTrace(5));
	}

	private static String dumpStackTrace(int depth) {
		StringBuilder sb = new StringBuilder();
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (int i = 0; i < stackTrace.length && i < depth; i++) {
			StackTraceElement se = stackTrace[i];
			sb.append(" - " + se.getClassName() + "#" + se.getMethodName() + ":" + se.getLineNumber() + "\n");
		}
		return sb.toString();
	}
	
	public static <A> A convert(Object o, Class<A> clazz) {
		if (o==null) {
			return null;
		}
		
		if (Number.class.isAssignableFrom(clazz)) {
			if (Double.class.equals(clazz)) {
				if (o instanceof Integer) {
					return (A) Double.valueOf(((Integer)o).doubleValue());
				}
				return null;
			}
		}
		throw new RuntimeException("Unsupported conversion: " + o.getClass() + " -> " + clazz.getName());
	}

	public static byte[] replace(byte[] find, byte[] replace,
			byte[] bytes) {
		int indexOf = indexOf(find, bytes, 0);
		if (indexOf==-1) {
			return bytes;
		}
		byte[] ret = new byte[bytes.length - find.length + replace.length];
		copy(bytes, 0, ret, 0, indexOf);
		copy(replace, 0, ret, indexOf, replace.length);
		copy(bytes, indexOf + find.length, ret, indexOf + replace.length, bytes.length - indexOf - find.length);
		return ret;
	}

	private static void copy(byte[] from, int fromIndex, byte[] to, int toIndex, int length) {
		for (int i = 0; i < length; i++) {
			to[toIndex + i] = from[fromIndex + i];
		}
	}

	private static int indexOf(byte[] find, byte[] bytes, int fromIndex) {
		LOOP1:
		for (int i = fromIndex; i < bytes.length - find.length; i++) {
			for (int j = 0; j < find.length; j++) {
				if (find[j] != bytes[i + j]) {
					continue LOOP1;
				}
			}
			return i;
		}
		return -1;
	}
	
	public static byte[] longToBytes(long x) {
	    ByteBuffer buffer = ByteBuffer.allocate(8);
	    buffer.putLong(x);
	    return buffer.array();
	}
	
	public static Long bytesToLong(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip 
        return buffer.getLong();
    }

	public static int[] parseInts(String[] split) {
		int[] ret = new int[split.length];
		for (int i = 0; i < split.length; i++) {
			ret[i] = Integer.parseInt(split[i]);
		}
		return ret;
	}

	public static int join(List<Integer> recycled) {
		int ret = 0;
		for (int i = 0; i < recycled.size(); i++) {
			ret += Math.pow(10, i) * recycled.get(recycled.size() - i - 1);
		}
		return ret;
	}

	public static BigDecimal add(BigDecimal b1, BigDecimal b2) {
		if (b1 == null) {
			return b2;
		} else {
			if (b2 == null) {
				return b1;
			} else {
				return b1.add(b2);
			}
		}
	}
}
