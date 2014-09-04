package qj.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import qj.tool.string.StringTracker;

/**
 * Created by QuanLA Date: Mar 10, 2006 Time: 4:57:11 PM
 */
public class JavaCodeUtil {

	public static final String[] ALL_KEY_WORDS = new String[] { "abstract",
			"boolean", "break", "byte", "case", "catch", "char", "class",
			"const", "continue", "default", "do", "double", "else", "extends",
			"final", "finally", "float", "for", "goto", "if", "implements",
			"import", "instanceof", "int", "interface", "long", "native",
			"new", "package", "private", "protected", "public", "return",
			"short", "static", "strictfp", "super", "switch", "synchronized",
			"this", "throw", "throws", "transient", "try", "void", "volatile",
			"while", "assert", };

	public static final String[] ALL_PRIMATIVE_TYPES = new String[] {
			"boolean", "float", "long", "short", "char", "int", "byte",
			"double",
	};

	private static final Pattern ptnFindCodeLine = Pattern
			.compile("\\s*([^;\\{\\}]+;)");

	/**
	 * Unsupport: return the first line of file
	 * 
	 * @param pos
	 * @param source
	 * @return the command line before this line
	 */
	public static String getPreviousCommandLine(int pos, CharSequence source) {
		return StringUtil4.findLastMatch(ptnFindCodeLine, pos, source).group(1);
	}

	/**
	 * 
	 * @param source
	 * @return content with no comment
	 */
	public static String rejectAllComment(CharSequence source) {
		StringBuffer code;
		if (source instanceof StringBuffer)
			code = (StringBuffer) source;
		else
			code = new StringBuffer(source.toString());

		int length = code.length();
		StringTracker charTracker = new StringTracker(
				new String[] { "//", "/*" });
		// Find // and /*
		// Find fail: break
		// What find first => check in string
		// if fail: rollIndex + 2, continue
		// true: replace with spaces
		for (int rollIndex = 0; rollIndex < length; rollIndex++) {

			if (charTracker.track(code.charAt(rollIndex))
					&& !posInString(rollIndex, code)) {
				int posTemp;
				if (charTracker.getMatchString().equals("//")) {
					posTemp = code.indexOf("\n", rollIndex + 1);
					if (posTemp == -1)
						posTemp = length;
					replaceWords(rollIndex - 1, posTemp, code);
				} else {
					posTemp = code.indexOf("*/", rollIndex + 1) + 2;
					if (posTemp == 1)
						posTemp = length;
					replaceWords(rollIndex - 1, posTemp, code);
				}
				charTracker.refresh();
				rollIndex = posTemp;
			}
		}
		// assert source.length() == code.length():"Length altered"; //*** Debug
		return code.toString();
	}

	/**
	 * Will not reject if // is in string
	 * 
	 * @param input
	 * @return content with no single line comment
	 */
	public static String rejectSingleLineComment(CharSequence input) {
        String commentMarkup = "//";
        return StringUtil4.rejectSingleLineComment(input, commentMarkup);
	}

    /**
	 * Make all words to spaces
	 * 
	 * @param start
	 * @param end
	 * @param sb
	 */
	public static void replaceWords(int start, int end, StringBuffer sb) {
		StringBuffer sbTemp = new StringBuffer();
		for (int i = start; i < end; i++) {
			char c = sb.charAt(i);
			sbTemp.append((c == ' ' || c == '\n' || c == '\r' || c == '\t') ? c
					: ' ');
		}
		sb.replace(start, end, sbTemp.toString());
	}

	/**
	 * Will not reject if /* is in string
	 * 
	 * @param content
	 * @return content with no multiline comment
	 */
	public static String rejectMultiLineComment(String content) {
		int start;
		int current = 0;
		StringBuffer sb = new StringBuffer(content);
		while ((start = nextOpenMultilineComment(sb, current)) > -1) {
			int end = sb.indexOf("*/", start + 2);

			sb.replace(start, end + 2, StringUtil4.createString(end + 2 - start,
					' '));
			current = end;
		}

		return sb.toString();
	}

	/**
	 * 
	 * @param sb
	 * @param current
	 * @return position of next open comment mark
	 */
	private static int nextOpenMultilineComment(StringBuffer sb, int current) {
		int foundPos;
		do {
			foundPos = sb.indexOf("/*", current);
			if (foundPos == -1 || !posInString(foundPos, sb))
				return foundPos;
			else
				current = foundPos + 2;
		} while (true);
	}

	/**
	 * 
	 * @param pos
	 * @param sb
	 * @return is this position in a string declaration
	 */
	public static boolean posInString(int pos, CharSequence sb) {
		boolean inString = false;
		int rollPos;
		// Go back to begin of line
		// Roll, if find a String > set inString flag to true
		// if = pos, return inString

		for (rollPos = StringUtil4.lastIndexof('\n', pos, sb) + 1; rollPos < pos; rollPos++) {
			if (sb.charAt(rollPos) == '\"')
				inString = !inString;
		}

		return inString;
	}

	/**
	 * 
	 * @param code
	 * @param pos
	 * @param source
	 */
	public static void replaceCodeLine(String code, int pos, StringBuffer source) {
		int replaceEndPos = source.indexOf("\n", pos);
		int replaceStartPos = source.lastIndexOf("\n", pos - 1) + 1;
		// Find first \n -> insertPos
		// find next \n(\s*), insert: \n\1code
		Matcher matcher = PTN_NEXT_LINE.matcher(source);
		if (matcher.find(pos)) {
			System.out.println("replaceStartPos = " + replaceStartPos
					+ " , replaceEndPos = " + replaceEndPos);
			source.replace(replaceStartPos, replaceEndPos, matcher.group(1)
					+ code);
		} else {
			source.replace(replaceStartPos, replaceEndPos, code);
		}
	}

	/**
	 * 
	 * @param code
	 * @param pos
	 * @param content
	 * @return
	 */
	public static String replaceCodeLine(String code, int pos, String content) {
		StringBuffer sb = new StringBuffer(content);
		replaceCodeLine(code, pos, sb);
		return sb.toString();
	}

	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer("awer\n\nrawe\nawer");
		addCodeLineBefore("&&", 5, sb);
		System.out.println(sb);
	}

	private static final Pattern PTN_NEXT_LINE = Pattern
			.compile("\\n([\\s&&[^\\n]]*)\\S");
	
	/**
	 * Support: + after { but get indent of next valid line
	 * 
	 * @param code
	 * @param pos
	 * @param source
	 * @return the source with content appended
	 */
	public static void addCodeLineAfter(String code, int pos,
			StringBuffer source) {
		int insertPos = source.indexOf("\n", pos);
		if (insertPos == -1)
			insertPos = source.length();
		// Find first \n -> insertPos
		// find next \n(\s*), insert: \n\1code
		Matcher matcher = PTN_NEXT_LINE.matcher(source);
		String indent = "";
			
		if (matcher.find(pos)) {
			indent = matcher.group(1);
		}
		
		String[] codes = code.split("\r?\n");
		for (int i = codes.length - 1; i > -1; i--) {
			source.insert(insertPos, "\n" + indent + codes[i]);
		}
	}

	private static final Pattern ptnSpace = Pattern
			.compile("\\n([\\s&&[^\\n]]*)(\\S)");

	/**
	 * 
	 * @param code
	 * @param pos
	 * @param source
	 * @return the source with content appended
	 */
	public static StringBuffer addCodeLineBefore(String code, int pos,
			StringBuffer source) {
		int pos1, pos2;
		String strSpace = "";
		// Find backward first and second \n pos
		// At second \n get \s*, set to strSpace
		// At first \n, insert \1code\n
		pos1 = source.lastIndexOf("\n", pos - 1);
		pos2 = source.lastIndexOf("\n", pos1 - 1);

		Matcher matcher = ptnSpace.matcher(source);

		if (matcher.find(pos2 + 1))
			strSpace = matcher.group(1);

		if (matcher.group(2).equals("}"))
			strSpace += "    ";

		source.insert(pos1 + 1, strSpace + code + "\n");

		return source;
	}

	/**
	 * 
	 * @param code
	 * @param pos
	 * @param source
	 * @return call to addCodeLineAfter using StringBuffer
	 */
	public static String addCodeLineAfter(String code, int pos, String source) {
		StringBuffer stringBuffer = new StringBuffer(source);
		addCodeLineAfter(code, pos, stringBuffer);
		return stringBuffer.toString();
	}

	/**
	 * 
	 * @param code
	 * @param pos
	 * @param source
	 * @return call to addCodeLineAfter using StringBuffer
	 */
	public static String addCodeLineBefore(String code, int pos, String source) {
		return addCodeLineBefore(code, pos, new StringBuffer(source))
				.toString();
	}

	public static String makeStringWhite(String content) {
		return makeStringWhite(content, "\"");
	}
	/**
	 * InString true: - Meet " -> if \": ignore, if ": change to InString false,
	 * do clear content. InString false: - Meet " -> inString
	 * 
	 * @param content
	 * @return
	 */
	public static String makeStringWhite(String content, String strMarker) {
		StringBuffer sbContent = new StringBuffer(content);
		content = null;
		int length = sbContent.length();
		StringTracker ct = new StringTracker(new String[] { strMarker, "\\" + strMarker });
		boolean inString = false;
		int openStringPos = 0;
		for (int rollPos = 0; rollPos < length; rollPos++) {
			if (ct.track(sbContent.charAt(rollPos))) {
				if (!inString) {
					inString = true;
					openStringPos = rollPos;
				} else {
					if (ct.checkAgainst("\\" + strMarker))
						continue;
					else {
						inString = false;
						sbContent.replace(openStringPos + 1, rollPos,
								StringUtil4.createString(rollPos - openStringPos
										- 1, ' '));
					}
				}
			}
		}

		return sbContent.toString();
	}

	public static final String[] JAVA_LANG_CLASSES = { "String", "Long",
			"Float", "Short", "Boolean", "Byte", "Character", "Object" };

	public static final String[] JAVA_UTIL_CLASSES = { "Date", "Calendar",
			"Dictionary", "HashMap", "Enumeration", "HashSet", "Hashtable",
			"List", "Map", "Locale", "Random", "Set", "Vector" };

	public static final String[] JAVA_SERVLET_CLASSES = { "Filter",
			"FilterChain", "Servlet", "RequestDispatcher", "ServletConfig",
			"ServletContext", "ServletException", "ServletInputStream",
			"ServletOutputStream", "ServletRequest", "ServletResponse" };

	public static final String[] JAVA_MATH_CLASSES = { "BigDecimal",
			"BigInteger" };

	/**
	 * 
	 * @param type
	 * @return
	 * @throws AnalyzeException
	 */
	public static String suggestFullyQualifiedName(String type) {
		if (StringUtil4.equalOne(type, JAVA_LANG_CLASSES)) {
			return "java.lang." + type;
		} else if (StringUtil4.equalOne(type, ALL_PRIMATIVE_TYPES)) {
			return type;
		} else if (StringUtil4.equalOne(type, JAVA_UTIL_CLASSES)) {
			return "java.util." + type;
		} else if (StringUtil4.equalOne(type, JAVA_MATH_CLASSES)) {
			return "java.math." + type;
		} else if (StringUtil4.equalOne(type, JAVA_SERVLET_CLASSES)) {
			return "javax.servlet." + type;
		}
		throw new IllegalArgumentException(
				"Can not make fully qualified name for type: " + type);
	}

	public static int countCodeLine(String content) {
		return StringUtil4.countLine(content.replaceAll("(\n|\\G)\\s+\n", "$1"));
	}

}
