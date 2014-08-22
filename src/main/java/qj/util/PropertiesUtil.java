package qj.util;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import qj.util.bean.AValue;
import qj.util.bean.GetSet;
import qj.util.funct.F0;
import qj.util.funct.P1;

public class PropertiesUtil {

	public static Properties loadPropertiesFromFile(String fileName) {
        return loadPropertiesFromFile(new File(fileName));
	}

	/**
	 * 
	 * @param file
	 * @return
	 */
    public static Properties loadPropertiesFromFile(File file) {
    	if (!file.exists()) {
    		return null;
    	}
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			return load(fis);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
    }

    /**
     * Load a Properties object from inputstream. Close the stream afterward
     * @param is
     * @return
     */
	public static Properties load(InputStream is) {
		Properties properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
        	IOUtil.close(is);
		}
		return properties;
	}
	public static Properties load(String in) {
		return load(new ByteArrayInputStream(in.getBytes()));
	}
	
	private static final Pattern PTN = Pattern.compile("\\s*([^#\\s=]+)\\s*=([^#]*)(#.+)?", Pattern.MULTILINE);
	public static boolean setValue(String key, String value, File propsFile) {
		return setValue(propsFile, key, value);
	}

	public static boolean setValue(File propsFile, final String... strings) {
		final boolean[] found = {false};
		final StringBuilder sb = new StringBuilder();
		FileUtil.eachLine(propsFile, new P1<String>() {public void e(String line) {
			Matcher matcher = PTN.matcher(line);
			if (matcher.matches()) {
				for (int i = 0; i < strings.length; i+=2) {
					String key = strings[i];
					String value = strings[i + 1];

					if (matcher.group(1).equals(key)) {
						String comment = matcher.group(3);
						sb.append(key + "=" + value + (comment!=null?comment:"") + "\n");
						found[0] = true;
						return;
					} 
					
				}
			}

			sb.append(line);
			sb.append("\n");
		}}, -1);
		
		
		
		
		if (found[0]) {
			FileUtil.writeToFile(sb.toString(), propsFile);
		}
		return found[0];
	}
	
	public static void main(String[] args) {
		setValue(new File("D:\\WorkOn\\quan-java\\quan-app\\quan-lottery\\data\\config\\lottery.properties"), "db.url", "QQQQQ");
	}

	public static void save(Properties info, File file) {
		File parentFile = file.getParentFile();
		if (parentFile != null && !parentFile.exists()) {
			parentFile.mkdirs();
		}
		OutputStream out = FileUtil.fileOutputStream(file);
		try {
			info.store(out, null);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		IOUtil.close(out);
	}

	public static Rectangle toRect(String rectStr) {
		String[] rectSplit = rectStr.split("\\s*,\\s*");
		Rectangle rect = new Rectangle(
				Integer.parseInt(rectSplit[0]),
				Integer.parseInt(rectSplit[1]),
				Integer.parseInt(rectSplit[2]),
				Integer.parseInt(rectSplit[3])
				);
		return rect;
	}

	public static Color toColor(String colorProp) {
		if ("red".equals(colorProp)) {
			return Color.red;
		}
		String[] split = colorProp.split("\\s*,\\s*");
		return new Color(
				Integer.parseInt(split[0]), 
				Integer.parseInt(split[1]), 
				Integer.parseInt(split[2]) 
				);
	}

	public static GetSet<Properties> gs(final File file) {
		return new GetSet<Properties>(
			new F0<Properties>() {public Properties e() {
				return !file.exists() ? null : loadPropertiesFromFile(file);
			}}, new P1<Properties>() {public void e(Properties prop) {
				save(prop, file);
			}}
		);
	}

	public static AValue<String> persistValue(String propertiesName, File file) {
		Properties prop = PropertiesUtil.loadPropertiesFromFile(file);
		AValue<String> ret = new AValue<>(prop == null ? null : prop.getProperty(propertiesName));
		
		ret.onChanged(new P1<String>() {public void e(String value) {
			Properties prop = PropertiesUtil.loadPropertiesFromFile(file);
			if (prop == null) {
				prop = new Properties();
			}
			prop.setProperty(propertiesName, value);
			PropertiesUtil.save(prop, file);
		}});
		return ret;
	}
}
