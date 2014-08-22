package qj.util;

import java.io.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import qj.util.funct.P1;

/**
 * Use ZipUtil instead
 * @author ngan
 *
 */
@Deprecated
public class CompressUtil {

	public static byte[] zip(String string) {
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			
			GZIPOutputStream out = new GZIPOutputStream(buffer);
			byte[] bytes = string.getBytes("UTF-8");
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			IOUtil.dump(in, out);
			out.finish();
			buffer.flush();
			return buffer.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		System.out.println(Arrays.toString(zip("abc erg awgawe gawe gawf xvdfgbdfbsehgser")));
	}

	public static void unzip(File file, String dir, P1<File> eachDestFile) {
		try {
			ZipFile zipFile = new ZipFile(file);
			Enumeration<? extends ZipEntry> en = zipFile.entries();
			while (en.hasMoreElements()) {
				ZipEntry zipEntry = en.nextElement();
				if (!zipEntry.isDirectory()) {
					InputStream in = zipFile.getInputStream(zipEntry);
					File destFile = new File(dir + "/" + zipEntry.getName());
					System.out.println(destFile);
					destFile.getParentFile().mkdirs();
					FileOutputStream out = new FileOutputStream(destFile);
					IOUtil.dump(in,
							out);
					IOUtil.close(out);
				}
			}
			zipFile.close();
		} catch (ZipException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
