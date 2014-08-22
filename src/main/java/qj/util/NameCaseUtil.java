package qj.util;

import java.util.regex.Matcher;

import qj.util.funct.F1;

public class NameCaseUtil {
	public static String camelToHyphen(String name) {
		return RegexUtil.replaceAll(name, "[A-Z]", new F1<Matcher, String>() {public String e(Matcher m) {
			return "_" + m.group().toLowerCase();
		}});
	}
	
	public static void main(String[] args) {
		System.out.println(camelToHyphen("receiverName"));
	}
}
