package qj.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil4 {
	private static final Pattern PTN_EMAIL_ADDRESS = Pattern
			.compile("[\\w.-]+@[\\w.-]+");


	private static final Pattern PTN_WCTR01 = Pattern.compile("\\\\\\*");
	private static final Pattern PTN_WCTR02 = Pattern.compile("\\\\\\?");
	public static Pattern wildCardToRegex(String wildCard) {
		// * -> .*
		// ? -> .?
		wildCard = toRegexLiteral(wildCard);

		wildCard = PTN_WCTR01.matcher(wildCard).replaceAll(".*");
		wildCard = PTN_WCTR02.matcher(wildCard).replaceAll(".?");
		return Pattern.compile(wildCard, Pattern.CASE_INSENSITIVE);
	}
	
	/**
	 * Check the given string to match the email format
	 * 
	 * @param address -
	 *            The given string to be checked as email address
	 * @return true if the given string is of email format. Other wise false
	 * @throws NullPointerException
	 *             address is null.
	 */
	public static boolean checkEmailAddress(String address) {
		Matcher m = PTN_EMAIL_ADDRESS.matcher(address);
		return m.matches();
	}

	
	
	// toRegexLiteral
	private static final Pattern PTN01 = Pattern.compile("\\\\");
	private static final Pattern PTN02 = Pattern.compile("\\{");
	private static final Pattern PTN03 = Pattern.compile("\\}");
	private static final Pattern PTN04 = Pattern.compile("\\|");
	private static final Pattern PTN05 = Pattern.compile("\\(");
	private static final Pattern PTN06 = Pattern.compile("\\)");
	private static final Pattern PTN07 = Pattern.compile("\\?");
	private static final Pattern PTN08 = Pattern.compile("\\*");
	private static final Pattern PTN09 = Pattern.compile("\\+");
	private static final Pattern PTN10 = Pattern.compile("\\.");
	private static final Pattern PTN11 = Pattern.compile("\\[");
	private static final Pattern PTN12 = Pattern.compile("\\]");
	private static final Pattern PTN13 = Pattern.compile("\\^");
	private static final Pattern PTN14 = Pattern.compile("\\$");
	private static final Pattern PTN15 = Pattern.compile("\r");
	private static final Pattern PTN16 = Pattern.compile("\n");
	private static final Pattern PTN17 = Pattern.compile("\t");
	/**
	 * Replace all:
	 * - \ -> \\
	 * - { -> \{
	 * - } -> \}
	 * - | -> \|
 	 * - ( -> \(
 	 * - ) -> \)
  	 * - ? -> \?
 	 * - * -> \*
  	 * - + -> \+
  	 * - . -> \.
  	 * - [ -> \[
  	 * - ] -> \]
  	 * - ^ -> \^
  	 * - $ -> \$
	 * @param content
	 * @return
	 */
	public static String toRegexLiteral(String content) {
		content = PTN01.matcher(content).replaceAll("\\\\\\\\");
		content = PTN02.matcher(content).replaceAll("\\\\{");
		content = PTN03.matcher(content).replaceAll("\\\\}");
		content = PTN04.matcher(content).replaceAll("\\\\|");
		content = PTN05.matcher(content).replaceAll("\\\\(");
		content = PTN06.matcher(content).replaceAll("\\\\)");
		content = PTN07.matcher(content).replaceAll("\\\\?");
		content = PTN08.matcher(content).replaceAll("\\\\*");
		content = PTN09.matcher(content).replaceAll("\\\\+");
		content = PTN10.matcher(content).replaceAll("\\\\.");
		content = PTN11.matcher(content).replaceAll("\\\\[");
		content = PTN12.matcher(content).replaceAll("\\\\]");
		content = PTN13.matcher(content).replaceAll("\\\\^");
		content = PTN14.matcher(content).replaceAll("\\\\\\$");
		content = PTN15.matcher(content).replaceAll("\\\\r");
		content = PTN16.matcher(content).replaceAll("\\\\n");
		content = PTN17.matcher(content).replaceAll("\\\\t");
		return content;
	}
	
	public static void main(String[] args) {
//		System.out.println(toRegexLiteral("abc"));
//		System.out.println(toRegexLiteral("\\$"));
		String temp = "${Date}${Date}";
//		temp.replaceFirst(toRegexLiteral(temp), "abd");
		System.out.println(temp.replaceFirst(toRegexLiteral("${Date}"), "abd"));
	}
	
	/**
	 * 
	 * @param groupNum
	 * @param toReplace
	 * @param matcher
	 * @return
	 */
	public static String replaceGroup(int groupNum, String toReplace, Matcher matcher) {
		String group = matcher.group();
		int start = matcher.start(groupNum) - matcher.start();
		int end = matcher.end(groupNum) - matcher.start();
		return group.substring(0, start) + toReplace + group.substring(end);
	}

    /**
     * Inside []
     * @param content
     * @return
     */
    public static String escapeInSquareBracket(String content) {
		content = PTN01.matcher(content).replaceAll("\\\\\\\\");
		content = PTN11.matcher(content).replaceAll("\\\\[");
		content = PTN12.matcher(content).replaceAll("\\\\]");
		content = PTN13.matcher(content).replaceAll("\\\\^");
		content = PTN15.matcher(content).replaceAll("\\\\r");
		content = PTN16.matcher(content).replaceAll("\\\\n");
		content = PTN17.matcher(content).replaceAll("\\\\t");
		return content;
    }

//    public static String replaceAll(String str, String... replaces) {
//        Pattern pattern = Pattern.compile(.)
//    }
}