package qj.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import qj.util.funct.*;
import qj.util.math.Range;

public class StringUtil extends StringUtil4 {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.#");

	public static F1<String, Integer> length = new F1<String, Integer>() {public Integer e(String obj) {
        return obj.length();
    }};

    public static F1<String, String> toUpperCase = new F1<String, String>() {public String e(String obj) {
        return obj == null ? null : obj.toUpperCase();
    }};
    public static F1<String, String> toLowerCase = new F1<String, String>() {public String e(String obj) {
    	return obj == null ? null : obj.toLowerCase();
    }};

    private static final Pattern PTN = Pattern.compile("-(\\w+)(:(.+))?");
    public static Map<String, Object> parseArgs(String[] args) {
        HashMap<String, Object> ret = new HashMap<String, Object>();
        for (String arg : args) {
            Matcher m = PTN.matcher(arg);
            if (m.matches()) {
                ret.put(m.group(1), m.group(3));
            }
        }
        return ret;
    }

    public static String limit(Object o, int limit) {
        if (o == null) {
            return "";
        }
        String str = String.valueOf(o);
        if (str.length() > limit) {
            str = str.substring(0, limit - 3) + "...";
        }
        return str;
    }

    public static boolean isBlank(String s) {
        return isEmpty(s) || s.trim().length() == 0;
    }
    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    public static boolean contains(String s, Collection<String> col) {
        for (String s1 : col) {
            if (s1.indexOf(s) > -1) {
                return true;
            }
        }
        return false;
    }

    public static F1<String, Boolean> startWith(final String startWith) {
        return new F1<String, Boolean>() {
            public Boolean e(String obj) {
                return obj.startsWith(startWith);
            }
        };
    }

    public static String toString(byte[] bytes) {
        try {
            return new String(bytes, "windows-1252"); // ISO-8859-1
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    public static P1<byte[]> toString(final P1<String> p) {
        return new P1<byte[]>() {
            public void e(byte[] obj) {
                p.e(StringUtil.toString(obj));
            }
        };
    }
    
    public static String normalize(String raw) {
    	if (raw== null) {
    		return null;
    	}
    	return normalize(raw.getBytes()); // .trim()
    }

    public static String normalize(byte[] bytes) {
    	if (bytes==null) {
    		return "";
    	}
    		
        StringBuilder sb = new StringBuilder(bytes.length);
        for (byte b : bytes) {
            if (b == -115) {
                b = ' ';
            } else if (b == 0) {
                b = ' ';
            } else if (b == '\n'
                    || b == 13) {
                b = ' ';
            }
            sb.append((char)b);
        }
        return sb.toString();
    }

    public static boolean isBlank(Object o) {
        if (o instanceof String) {
            return isBlank((String)o);
        }
        return o == null;
    }
	public static void eachLine(String str, P1<String> p1) {
		eachLine(str, Fs.f1(p1,false));
	}

	public static void eachLine(String str, F1<String,Boolean> f1) {
		if (str == null) {
			return;
		}
		
		String[] strs = str.trim().split("\r?\n");
		for (String string : strs) {
			if (f1.e(string)) {
				break;
			}
		}
	}

	static Pattern PTN_INCREASE = Pattern.compile("[,\\d]+(\\.\\d+)?|\\.\\d+");

	public static F1<String, Boolean> isEmptyF = new F1<String, Boolean>() {public Boolean e(String obj) {
		return isEmpty(obj);
	}};
	public static String increase(String str, Double amount) {
		if (str == null) {
			return null;
		}
		
		Matcher m = PTN_INCREASE.matcher(str);
		if (m.find()) {
			return str.substring(0, m.start())
			+ DECIMAL_FORMAT.format(LanguageUtil.toNumber(m.group()) + amount)
			+ str.substring(m.end());
		} else {
			return str;
		}
	}
	public static void main(String[] args) {
		System.out.println(increase("50K", 50D));
		System.out.println(increase("1 con vit", 50D));
		System.out.println(increase("a weraw .23 er K", 50D));
		System.out.println(increase("a weraw 4.23 er K", 50D));
		System.out.println(increase("a weraw -124.23 er K", 50D));
		System.out.println(increase("1,000", 50D));
		
	}

	public static boolean isEmpty(Object val) {
		return val == null ? true : isEmpty(String.valueOf(val));
	}

	static DecimalFormat NF = new DecimalFormat(",##0.#####");
	public static String format(double num) {
		return NF.format(num);
	}

	public static String percent(double number) {
		return format(number * 100) + "%";
	}

	public static F0<String> lineF(final String string, int index) {
		final int[] ind = {index};
		return new F0<String>() {public String e() {
			int pos = string.indexOf('\n', ind[0]);
			if (pos == -1) {
				return null;
			}
			try {
				return string.substring(ind[0], pos);
			} finally {
				ind[0] = pos + 1;
			}
		}};
	}

	public static boolean isCapitalized(String str) {
		if (Character.isLowerCase(0)) {
			return false;
		}
		for (int i = 1; i < str.length(); i++) {
			if (Character.isUpperCase(i)) {
				return false;
			}
		}
		return true;
	}

	public static String longestLine(String text) {
		String longestLine = null;
		for (String line : text.split("\r?\n")) {
			if (longestLine == null || line.length() > longestLine.length()) {
				longestLine = line;
			}
		}
		return longestLine;
	}

	public static String substring(Range range, String text) {
		return text.substring(range.getFrom(), range.getTo());
	}

	public static String between(String start, String end, String content) {
		int i1 = content.indexOf(start);
		if (i1==-1) {
			return null;
		}
		i1 += start.length();
		
		int i2 = content.indexOf(end, i1);
		if (i2==-1) {
			return null;
		}
		
		return content.substring(i1, i2);
	}

	public static String replace(String replace, Range range, String text) {
		return text.substring(0, range.getFrom()) + replace + text.substring(range.getTo());
	}

	public static Range lineRange(Range range, String text) {
		int from;
		for (int index = range.getFrom() - 1;;index--) {
			
			if (index==-1 || text.charAt(index)=='\n') {
				from = index + 1;
				break;
			}
		}
		
		int to;
		for (int index = Math.max(range.getTo() - 1, range.getFrom());;index++) {
			if (index >=text.length() || text.charAt(index)=='\n') {
				to = index + 1;
				break;
			}
		}
		return new Range(from, to);
	}

	public static int indexOfHappenNum(char c, int num, String text) {
		if (num==0) {
			return 0;
		}
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i)==c) {
				num --;
				if (num==0) {
					return i;
				}
			}
		}
		return -1;
	}

	public static String tab(String str) {
		return str.replaceAll("^|\r?\n", "$0\t");
	}
	public static String untab(String str) {
		return str.replaceAll("(^|\r?\n)\t", "$1");
	}

	public static Range range(String target, String src) {
		int start = src.indexOf(target);
		if (start == -1) {
			return null;
		}
		
		return new Range(start,start + target.length());
	}

	public static boolean isNumber(String string) {
		if (isBlank(string)) {
			return false;
		}
		try {
			Double.parseDouble(string);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static String multiply(String string, int times) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < times; i++) {
			sb.append(string);
		}
		return sb.toString();
	}

	public static String firstLine(String string) {
		try {
			return new BufferedReader(new StringReader(string)).readLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Apply all the spaceholders like $name or $age with the values
	 * @param templateParams
	 * @param template
	 * @return
	 */
	public static String simpleTemplateApply(Map<String, String> templateParams, String template) {
		if (templateParams==null) {
			return template;
		}
		for (Entry<String, String> entry : templateParams.entrySet()) {
			template = template.replaceAll("\\$" + entry.getKey(), entry.getValue());
		}
		return template;
	}

	public static F1<String, String> replaceF(final String from, final String to) {
		return new F1<String, String>() {public String e(String obj) {
			return obj.replace(from, to);
		}};
	}

	public static F1<String, String> assureLineF(final String line) {
		return new F1<String, String>() {public String e(String content) {
			if (content.contains("\n" + line)) {
				return content;
			}
			return content + "\n" + line;
		}};
	}

	public static F0<String> concat(final F0<String> f, final String string) {
		return new F0<String>() {public String e() {
			return f.e() + string;
		}};
	}

	public static String nextInAscii(String str) {
		char c = (char) (str.charAt(str.length()-1) + 1);
		
		return str.substring(0, str.length() - 1) + c;
	}

	public static String insert(String insertedStr, int pos, String str) {
		return str.substring(0, pos) + insertedStr + str.substring(pos);
	}

	public static String toLowerCase(String str) {
		return str == null ? null : str.toLowerCase();
	}

	public static String trimDeep(String str) {
		if (str == null) {
			return null;
		}
		return str.trim().replaceAll("\\s+", " ");
	}

	public static F1<Character, Integer> countHappensF(final String str) {
		return new F1<Character,Integer>() {public Integer e(Character c) {
			return countHappens(c, str);
		}};
	}
	
	public static F1<String,String> internF = new F1<String, String>() {public String e(String obj) {
		return obj.intern();
	}};

	public static <O> F1<O,String> valueOfF() {
		return new F1<O,String>() {public String e(O obj) {
			return String.valueOf(obj);
		}};
	};
}
