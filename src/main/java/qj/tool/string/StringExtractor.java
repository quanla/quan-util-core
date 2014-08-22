package qj.tool.string;

import java.util.regex.Matcher;

import qj.util.RegexUtil;
import qj.util.funct.P1;

public class StringExtractor {
	private final String content;
	public int index;

	public StringExtractor(String content) {
		this.content = content;
	}

	public String getNext(String regex, int group) {
		Matcher matcher = RegexUtil.compileF.e(regex).matcher(content);
		if (!matcher.find(index)) {
			return null;
		}
		index = matcher.end();
		return matcher.group(group);
	}

	public boolean toEnd(String string) {
		int indexOf = content.indexOf(string, index);
		if (indexOf > -1) {
			index = indexOf + string.length();
			return true;
		} else {
			return false;
		}
	}

	public String nextBetween(String beginStr, String endStr) {
		if (!toEnd(beginStr)) {
			
			return null;
		}
		int begin = index;
		if (!toEnd(endStr)) {
			return null;
		}
		return content.substring(begin, index - endStr.length());
	}

	public static String nextBetween(String from, String to,
			String str) {
		return new StringExtractor(str).nextBetween(from, to);
	}

	public String untilNext(String string) {
		int indexOf = content.indexOf(string, index);
		if (indexOf==-1) {
			return null;
		}
		String ret = content.substring(index, indexOf);
		index = indexOf + string.length();
		return ret;
	}

	public String toEnd() {
		String ret = content.substring(index);
		index = content.length();
		return ret;
	}

	public void eachByStart(String start, P1<String> p) {
		eachByStart(start,content,p);
	}

	public static void eachByStart(String start, String content, P1<String> p) {
		int index = 0;
		while (true) {
			int indexOf = content.indexOf(start, index + start.length());
			if (indexOf == -1) {
				if (index != 0) {
					p.e(content.substring(index));
				}
				return;
			}
			
			if (index != 0) {
				p.e(content.substring(index, indexOf));
			}
			index = indexOf;
		}
	}
}