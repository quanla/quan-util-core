package qj.util.i18n;

import java.io.InputStream;

import qj.util.IOUtil;

public class LocaleResource {
	private Class<?> clazz;
	private String name;
	private String ext;

	public LocaleResource(Class<?> clazz, String name, String ext) {
		this.clazz = clazz;
		this.name = name;
		this.ext = ext;
	}
	
	public String getString(String locale) {
		InputStream in = null;
		if (locale != null) {
			in = clazz.getResourceAsStream(name + "_" + locale + "." + ext);
		}
		if (in==null) {
			in = clazz.getResourceAsStream(name + "." + ext);
		}
		return IOUtil.inputStreamToString_force(in, "UTF-8");
	}

	public static LocaleResource getLocaleResource(Class<?> clazz, String name, String ext) {
		return new LocaleResource(clazz, name, ext);
	}
}
