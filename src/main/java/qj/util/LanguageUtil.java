package qj.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LanguageUtil {

	public static List<MeasurementUnit> FILE_UNITS = Arrays.asList(
			new MeasurementUnit(1024 * 1024 * 1024, "GB"),
			new MeasurementUnit(1024 * 1024, "MB"),
			new MeasurementUnit(1024, "KB"),
			new MeasurementUnit(1, "B")
	);
	
	public static String translateFileSize(long size) {

        ArrayList<String> vals = new ArrayList<String>(7);

		size = toleratize(size, 1024 * 1024 * 1024);
		long gb = size / (1024 * 1024 * 1024);
		if (gb > 0) {
			vals.add( gb + "GB" );
            size = size % (1024 * 1024 * 1024);
		}

		size = toleratize(size, 1024 * 1024);
		long mb = size / (1024 * 1024);
		if (mb > 0) {
			vals.add( mb + "MB" );
            size = size % (1024 * 1024);
		}
	
		size = toleratize(size, 1024);
		long kb = size / 1024;
		if (kb > 0) {
			vals.add( kb + "KB" );
            size = size % (1024);
		}

		if (size > 0) {
            vals.add(size + " bytes");
        }
		return Cols.join(vals, " ");
	}

	public static String translateSeconds(long minutes) {
		return translateMillis(minutes * 1000);
	}

	public static List<MeasurementUnit> TIME_UNITS = Arrays.asList(
			new MeasurementUnit(DateUtil.DAY_LENGTH, "d"),
			new MeasurementUnit(DateUtil.HOUR_LENGTH, "h"),
			new MeasurementUnit(DateUtil.MINUTE_LENGTH, "'"),
			new MeasurementUnit(DateUtil.SECOND_LENGTH, "\""),
			new MeasurementUnit(1, "ms")
	);
	public static List<MeasurementUnit> TIME_UNITS_VI = Arrays.asList(
			new MeasurementUnit(DateUtil.DAY_LENGTH, " ngày"),
			new MeasurementUnit(DateUtil.HOUR_LENGTH, "h"),
			new MeasurementUnit(DateUtil.MINUTE_LENGTH, "'"),
			new MeasurementUnit(DateUtil.SECOND_LENGTH, "\""),
			new MeasurementUnit(1, "ms")
			);
	public static String translateMillis(long millis) {
		return translateAmount(millis,TIME_UNITS,2);
	}
	public static String translateFileSize(long size,int depth) {
		return translateAmount(size,FILE_UNITS,depth);
	}
	public static String translateAmount(long amount,
			List<MeasurementUnit> measurements, int depth) {

        ArrayList<String> vals = new ArrayList<String>(7);

        int start = -1;
        for (int i = 0; i < measurements.size() && (start==-1 || i < start + depth); i++) {
        	MeasurementUnit m = measurements.get(i);

    		amount = toleratize(amount, m.value);
    		long val = amount / (m.value);
    		if (val > 0) {
    			vals.add(val + m.name);
    			amount = amount % (m.value);
    			if (start==-1) {
    				start = i;
    			}
    		}
		}
        
        if (vals.size() == 0) {
        	return "0" + measurements.get(measurements.size() - 1).name;
        }
        
		return Cols.join(vals, " ");
	}
	public static class MeasurementUnit {
		long value;
		String name;
		public MeasurementUnit(long value, String name) {
			this.value = value;
			this.name = name;
		}
	}

	public static String translateMillis1(long millis) {
        ArrayList<String> vals = new ArrayList<String>(7);

		millis = toleratize(millis, DateUtil4.DAY_LENGTH);
		long days = millis / (DateUtil4.DAY_LENGTH);
		if (days > 0) {
			vals.add(days + (days == 1 ? " day" : " days"));
			millis = millis % (DateUtil4.DAY_LENGTH);
		}
	
		millis = toleratize(millis, DateUtil4.HOUR_LENGTH);
		long hours = millis / (DateUtil4.HOUR_LENGTH);
		if (hours > 0) {
			vals.add(hours + "h");
			millis = millis % (DateUtil4.HOUR_LENGTH);
		}
	
		millis = toleratize(millis, DateUtil4.MINUTE_LENGTH);
		long minutes = millis / (DateUtil4.MINUTE_LENGTH);
		if (minutes > 0) {
			vals.add(minutes + "'");
			millis = millis % (DateUtil4.MINUTE_LENGTH);
		}
	
		long seconds = millis / DateUtil4.SECOND_LENGTH;
		if (seconds > 0) {
			vals.add(seconds + "\"");
			millis = millis % DateUtil4.SECOND_LENGTH;
		}
	
		if (millis >= 0) {
			vals.add(millis + "ms");
		}
		return Cols.join(vals, " ");
	}

	private static long toleratize(long millis, long length) {
		int toleranceRange = (int) (length / 1000000);
		millis += toleranceRange;
		if ((millis % length) < 2 * toleranceRange) {
			return millis - (millis % length);
		} else {
			return millis - toleranceRange;
		}
	}

	public static String getGreetings(String name) {
		return getGreetings() + ", " + name;
	}
	
	private static final Pattern PTN_COMMA = Pattern.compile(",(\\d+)");
	public static Double toNumber(String str) {
		
		if (str.contains(",")) {
			
			if (str.contains(".")) {
				
			} else {
				if (!isCommaNormal(str)) {
					return Double.valueOf(str.replaceAll(",", "."));
				} else {
					return Double.valueOf(str.replaceAll(",", ""));
				}
			}
		} else if (str.contains(".")) {
			if (str.contains(",")) {
				
			} else {
				if (isDotNormal(str)) {
					return Double.valueOf(str);
				} else {
					return Double.valueOf(str.replaceAll("\\.", ""));
				}
			}
		} else {
			return Double.valueOf(str.replaceAll(" ", ""));
		}
//		return null;
		throw new RuntimeException("He he: " + str);
	}

	public static boolean isCommaNormal(String str) {
		if (StringUtil.countHappens(',', str) > 1) {
			return true;
		}
		
		Matcher matcher = PTN_COMMA.matcher(str);
		while (matcher.find()) {
			if (matcher.group(1).length() != 3) {
				return false;
			}
		}
		return true;
	}
	
	private static final Pattern PTN_DOT = Pattern.compile("\\.(\\d+)");
	public static boolean isDotNormal(String str) {
		if (StringUtil.countHappens('.', str) > 1) {
			return false;
		}
		
		Matcher matcher = PTN_DOT.matcher(str);
		while (matcher.find()) {
			if (matcher.group(1).length() != 3) {
				return true;
			}
		}
		return false;
	}
	
	public static String getGreetings() {
		Calendar ca = Calendar.getInstance();
		int hour = ca.get(Calendar.HOUR_OF_DAY);
		if (hour >= 2 && hour < 12)
			return "Good morning";
		else if (hour >= 12 && hour < 19)
			return "Good afternoon";
		else
			return "Good evening";
	}

	
	
	private static char[] SPECIAL_CHARACTERS = { ' ', '!', '"', '#', '$', '%',
	   '*', '+', ',', ':', '<', '=', '>', '?', '@', '[', '\\', ']', '^',
	   '`', '|', '~', };
	 
	private static char[] REPLACEMENTS = { '-', '\0', '\0', '\0', '\0', '\0',
	   '\0', '_', '\0', '_', '\0', '\0', '\0', '\0', '\0', '\0', '_',
	   '\0', '\0', '\0', '\0', '\0', };

	public static final char[] MARKED_VI_CHARS = { 'À', 'Á', 'Â', 'Ã', 'È', 'É', 'Ê', 'Ì', 'Í', 'Ò',
	   'Ó', 'Ô', 'Õ', 'Ù', 'Ú', 'Ỳ', 'Ý', 'Ỹ', 'à', 'á', 'â', 'ã', 'è', 'é', 'ê',
	   'ì', 'í', 'ò', 'ó', 'ô', 'õ', 'ù', 'ú', 'ỳ', 'ỹ', 'ý', 'Ă', 'ă', 'Đ', 'đ',
	   'Ĩ', 'ĩ', 'Ũ', 'ũ', 'Ơ', 'ơ', 'Ư', 'ư', 'Ạ', 'ạ', 'Ả', 'ả', 'Ấ',
	   'ấ', 'Ầ', 'ầ', 'Ẩ', 'ẩ', 'Ẫ', 'ẫ', 'Ậ', 'ậ', 'Ắ', 'ắ', 'Ằ', 'ằ',
	   'Ẳ', 'ẳ', 'Ẵ', 'ẵ', 'Ặ', 'ặ', 'Ẹ', 'ẹ', 'Ẻ', 'ẻ', 'Ẽ', 'ẽ', 'Ế',
	   'ế', 'Ề', 'ề', 'Ể', 'ể', 'Ễ', 'ễ', 'Ệ', 'ệ', 'Ỉ', 'ỉ', 'Ị', 'ị',
	   'Ọ', 'ọ', 'Ỏ', 'ỏ', 'Ố', 'ố', 'Ồ', 'ồ', 'Ổ', 'ổ', 'Ỗ', 'ỗ', 'Ộ',
	   'ộ', 'Ớ', 'ớ', 'Ờ', 'ờ', 'Ở', 'ở', 'Ỡ', 'ỡ', 'Ợ', 'ợ', 'Ụ', 'ụ',
	   'Ủ', 'ủ', 'Ứ', 'ứ', 'Ừ', 'ừ', 'Ử', 'ử', 'Ữ', 'ữ', 'Ự', 'ự', };
	 
	private static char[] UNMARKED_VI_CHARS = { 'A', 'A', 'A', 'A', 'E', 'E', 'E',
	   'I', 'I', 'O', 'O', 'O', 'O', 'U', 'U', 'Y', 'Y', 'Y', 'a', 'a', 'a', 'a',
	   'e', 'e', 'e', 'i', 'i', 'o', 'o', 'o', 'o', 'u', 'u', 'y', 'y', 'y', 'A',
	   'a', 'D', 'd', 'I', 'i', 'U', 'u', 'O', 'o', 'U', 'u', 'A', 'a',
	   'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A',
	   'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'E', 'e', 'E', 'e',
	   'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'I',
	   'i', 'I', 'i', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o',
	   'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O',
	   'o', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u',
	   'U', 'u', };
	
	private static TreeSet<Character> UNMARKED_VI_CHARS_SET = new TreeSet<Character>(Arrays.asList(LangUtil.toObjArr(UNMARKED_VI_CHARS)));
	public static String toUrlFriendly(String s) {
		s = replaceChars(s, MARKED_VI_CHARS, UNMARKED_VI_CHARS);
		
		int maxLength = Math.min(s.length(), 236);
		char[] buffer = new char[maxLength];
		int n = 0;
		for (int i = 0; i < maxLength; i++) {
			char ch = s.charAt(i);
			int index = Arrays.binarySearch(SPECIAL_CHARACTERS, ch);
			if (index >= 0) {
				buffer[n] = REPLACEMENTS[index];
			} else {
				buffer[n] = ch;
			}
			// skip not printable characters
			if (buffer[n] > 31) {
				n++;
			}
		}
		// skip trailing slashes
		while (n > 0 && buffer[n - 1] == '/') {
			n--;
		}
		return String.valueOf(buffer, 0, n);
	}
	public static String replaceChars(String s, char[] from, char[] to) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			int index = Arrays.binarySearch(from, ch);
			if (index >= 0) {
				sb.append(to[index]);
			} else {
				sb.append(ch);
			}
		}
		return sb.toString();
	}
	
	public static String removeViMark(String str) {
		return replaceChars(str, MARKED_VI_CHARS, UNMARKED_VI_CHARS);
	}
	
	public static void main(String[] args) {
		System.out.println(removeViMark("Xe 7 chổ"));
	}

	public static boolean isMarkedVi(String content) {
		int unmarked = 0;
		int marked = 0;
		for (int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);
			Character cO = Character.valueOf(c);
			if (UNMARKED_VI_CHARS_SET.contains(cO)) {
				unmarked ++;
			} else if (ArrayUtil.contains(c, MARKED_VI_CHARS)) {
				marked ++;
			}
		}
		return (double)marked/(marked+unmarked) > .1;
	}
}
