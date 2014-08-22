package qj.util;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

@SuppressWarnings("rawtypes")
public class JsonUtil {
	static SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	static {
		DF.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	public static String toJsonString(Map map) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (Object entryO : map.entrySet()) {
			if (sb.length() > 1) {
				sb.append(",");
			}
			Entry entry = (Entry) entryO;
			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\":");
			
			Object value = entry.getValue();
			sb.append(toJsonString(value));
		}
		sb.append("}");
		return sb.toString();
	}

	public static String toJsonString(Collection col) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (Object e : col) {
			if (sb.length() > 1) {
				sb.append(",");
			}
			sb.append(toJsonString(e));
		}
		sb.append("]");
		return sb.toString();
	}
	
	public static String toJsonString(Object value) {
		if (value instanceof String) {
			return "\"" +
			String.valueOf(value)
					.replaceAll("\\\\", "\\\\\\\\")
					.replaceAll("\"", "\\\\\"")
					.replaceAll("\t", " ")
					.replaceAll("\r?\n", "\\\\n") +
			"\"";
		} else if (value instanceof Date) {
			return "\"" + DF.format((Date)value) + "\"";
			// 2014-08-14T11:22:31
		} else if (value instanceof Map) {
			return toJsonString((Map) value);
		} else if (value instanceof Collection) {
			return toJsonString((Collection) value);
		} else {
			return String.valueOf(value);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(DF.format(new Date()));
	}
}
