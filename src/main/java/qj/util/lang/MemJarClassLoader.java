package qj.util.lang;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

/**
 * Load class from a jar file stream
 */
@SuppressWarnings("UnusedDeclaration")
public class MemJarClassLoader extends AggressiveClassLoader {
	Map<String,byte[]> store = new HashMap<>();
	
	public MemJarClassLoader(InputStream stream) {
		JarInputStream jarInputStream;
		try {
			jarInputStream = new JarInputStream(stream);
			for (ZipEntry entry;(entry = jarInputStream.getNextEntry()) != null;) {
				if (entry.isDirectory()) {
					continue;
				}
				String name = entry.getName();
				
				int len;
				byte[] buffer = new byte[8192];
				ByteArrayOutputStream bb = new ByteArrayOutputStream();
				while ((len = jarInputStream.read(buffer, 0, buffer.length)) != -1) {
					bb.write(buffer, 0, len);
				}
				byte[] data = bb.toByteArray();
				
				store.put(name, data);
				jarInputStream.closeEntry();
			}
			jarInputStream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public byte[] loadNewClass(String name) {
		String filePath = toFilePath(name);
		return store.remove(filePath); // Remove it because it will be cached with default CL mechanism
	}
}