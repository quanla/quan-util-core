package qj.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import qj.util.funct.F0;

public class DebugUtil {
	public static String path = SystemUtil.isWindows() ? "e:\\temp\\debug.dat" : "/tmp/debug.dat";
	public static void save(Object o) {
		save(o, path);
	}
	public static void save(Object o, String path) {
		FileOutputStream out = FileUtil.fileOutputStream(path, false);
		IOUtil.serialize(o, out);
		IOUtil.close(out);
		System.out.println("Saved to " + new File(path).getAbsolutePath());
	}
	public static <A> A load() {
		return load(path);
	}
	public static <A> A load(String path) {
		if (!new File(path).exists()) {
			return null;
		}
		FileInputStream in = FileUtil.fileInputStream(path);
		A deserialize = IOUtil.deserialize(in);
		IOUtil.close(in);
		return deserialize;
	}
	
	public static <A> F0<A> loadF(final String path) {
		return new F0<A>() {public A e() {
			return load(path);
		}};
	}
}
