package qj.util.lang;

import java.io.IOException;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import qj.util.IOUtil;

public class JarClassPathClassLoader extends SimpleClassPathClassLoader {
	String classPath;
	
	JarFile jarFile = null;
	public JarClassPathClassLoader(String classPath) {
		this.classPath = classPath;
		
		try {
			jarFile = new JarFile(classPath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public byte[] loadNewClass(String name) {
		if (jarFile == null) {
			return null;
		}
		
		String filePath = toFilePath(name);
		ZipEntry entry = jarFile.getJarEntry(filePath);
		if (entry == null) {
			return null;
		}
		try {
			return IOUtil.readData(jarFile.getInputStream(entry));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	@Override
	protected void finalize() throws Throwable {
		IOUtil.close(jarFile);
		super.finalize();
	}
}