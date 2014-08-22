package qj.util.lang;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import qj.util.FileUtil;

public class DirClassPathClassLoader extends SimpleClassPathClassLoader {
	List<String> classPaths;
	
	public DirClassPathClassLoader(String... classPaths) {
		this.classPaths = Arrays.asList(classPaths);
	}
	

	public InputStream getResourceAsStream(String name) {
		for (String classPath : classPaths) {
			File file = new File(classPath + "/" + name);
			if (file.exists() && file.isFile()) {
				return FileUtil.fileInputStream(file);
			}
		}
		return null;
	}


	public byte[] loadNewClass(String name) {
		File file = findFile(name);
		if (file == null) {
			return null;
		}
		
		return FileUtil.readFileToBytes(file);
	}
	
	private File findFile(String name) {
		for (String classPath : classPaths) {
			File file = new File(classPath + "/" + toFilePath(name));
			if (file.exists() && file.isFile()) {
				return file;
			}
		}
		return null;
	}
}