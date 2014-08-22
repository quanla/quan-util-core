package qj.util;

import java.util.ArrayList;
import java.util.Collection;

public class CmdUtil {
	public static String getString(String key, String[] args) {
		return getString(key, null, args);
	}
	public static String getString(String key, String def, String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-" + key)) {
				return args[i+1];
			}
		}
		return def ;
	}
	public static Collection<String> getStrings(String key, String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-" + key)) {

				ArrayList<String> ret = new ArrayList<String>();

				for (int j = i + 1; j < args.length; j++) {
					if (args[j].matches("-\\w")) {
						break;
					}
					ret.add(args[j]);
				}
				
				return ret;
				
			}
		}
		return null;
	}
	public static Integer getInt(String key, String[] args) {
		return getInt(key, null, args);
	}
	public static Integer getInt(String key, Integer def, String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-" + key)) {
				return Integer.valueOf(args[i+1]);
			}
		}
		return def;
	}
}
