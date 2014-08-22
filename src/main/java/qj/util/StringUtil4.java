package qj.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import qj.tool.string.StringNode;
import qj.tool.string.StringTracker;
import qj.ui.DesktopUI4;
import qj.util.funct.F1;
import qj.util.funct.P1;
import qj.util.math.MathUtil4;

/**
 * Primary util Created by QuanLA Date: Mar 10, 2006 Time: 5:29:35 PM
 */
@SuppressWarnings({"JavaDoc"})
public class StringUtil4 {

    public static byte[] hexToBytes(final CharSequence hexString) {
		if (hexString == null || hexString.length() == 0) {
			return null;
		}

		final byte[] results = new byte[hexString.length() / 2];
		int resIdx = 0;
		try {
			for (int i = 0; i < hexString.length(); i += 2) {

				int value = Integer.parseInt("" + hexString.charAt(i)
						+ hexString.charAt(i + 1), 16);

				results[resIdx] = (byte) (value - 128);
				resIdx++;
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Can not decode hex string: " + hexString);
		}

		return results;
	}

    /**
	 * Converts the specified bytes array into its hex string representation.
	 *
	 * @param bytes
	 * @return null
	 */
	public static String bytesToHexString(final byte[] bytes) {
		if (bytes == null)
			return null;

		char[] chars = new char[bytes.length * 2];
		for (int byteIndex = 0, charIndex = 0; byteIndex < bytes.length; ++byteIndex, charIndex += 2) {
			String str = Integer.toString(bytes[byteIndex] + 128, 16);
			str = ensureLength(str, 2, false, '0');
			chars[charIndex] = str.charAt(0);
			chars[charIndex + 1] = str.charAt(1);
		}
		return new String(chars);
	}

	public static String randomString() {
		return randomString(5);
	}

	/**
	 * Generate a random String at specific length.
	 * String contains of all alpha-numeric chars
	 * @param length
	 * @return
	 */
	public static String randomString(int length) {
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			// 48-57 : 10
			// 65-90 : 26
			// 97-122: 26
			int feed = r.nextInt(62);
			char c;
			if (feed < 10) {
				c = (char)(feed + 48);
			} else if (feed < 36) {
				c = (char)(feed -10 + 65);
			} else {
				c = (char)(feed - 36 + 97);
			}
			sb.append(c);
		}
		return sb.toString();
	}
	
	/**
	 * Check if pattern string matches text string.
	 *
	 * At the beginning of iteration i of main loop
	 *
	 * old[j] = true if pattern[0..j] matches text[0..i-1]
	 *
	 * By comparing pattern[j] with text[i], the main loop computes
	 *
	 * states[j] = true if pattern[0..j] matches text[0..i]
	 *
	 */
	public static boolean wildcardMatches(String pattern, String text) {
		// add sentinel so don't need to worry about *'s at end of pattern
		text += '\0';
		pattern += '\0';

		int N = pattern.length();

		boolean[] states = new boolean[N + 1];
		boolean[] old = new boolean[N + 1];
		old[0] = true;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			states = new boolean[N + 1]; // initialized to false
			for (int j = 0; j < N; j++) {
				char p = pattern.charAt(j);

				// hack to handle *'s that match 0 characters
				if (old[j] && (p == '*'))
					old[j + 1] = true;

				if (old[j] && (p == c))
					states[j + 1] = true;
				if (old[j] && (p == '.'))
					states[j + 1] = true;
				if (old[j] && (p == '*'))
					states[j] = true;
				if (old[j] && (p == '*'))
					states[j + 1] = true;
			}
			old = states;
		}
		return states[N];
	}

	public static String xmlUnescape(String src) {
		StringBuffer sb = new StringBuffer();

		int index = 0;
		int found;
		while ((found = src.indexOf("&#", index)) != -1) {
			sb.append(src.substring(index, found));

			int end = src.indexOf(";", found);
			// Unescaping
			sb.append((char) Integer.parseInt(src.substring(found + 2, end)));

			index = end + 1;
		}
		sb.append(src.substring(index));

		return sb.toString();
	}

	/**
	 *
	 * @param range
	 * @return
	 */
	public static int[] extractIntRange(String[] range) {
		TreeSet ints = new TreeSet();
		for (int i = 0; i < range.length; i++) {
			String token = range[i];
			int dashPos = token.indexOf('-');
			if (dashPos == -1) {
				ints.add(Integer.valueOf(token));
			} else {
				int from = Integer.parseInt(token.substring(0, dashPos));
				int to = Integer.parseInt(token.substring(dashPos + 1));
				for (int j = from; j <= to; j++) {
					ints.add(new Integer(j));
				}
			}
		}

		// Convert to ints
		int[] ret = new int[ints.size()];
		int j = 0;
		for (Iterator iterator = ints.iterator(); iterator.hasNext(); j++) {
			Integer val = (Integer) iterator.next();
			ret[j] = val.intValue();
		}
		return ret;
	}

	public static String unicodeUnescape(String src) {
		if (src==null) {
			return null;
		}

		StringBuffer sb = new StringBuffer();

		int index = 0;
		int found;
		while ((found = src.indexOf("\\u", index)) != -1) {
			sb.append(src.substring(index, found));

			// Unescaping
			String hexString = src.substring(found + 2, found + 6);
			try {
				sb.append(unHex(hexString));
			} catch (Exception e) {
				sb.append(hexString);
			}
			index = found + 6;
		}
		sb.append(src.substring(index));

		return sb.toString();
	}

	public static String xmlEscape(String str) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch < 256)
				sb.append(ch);
			else
				sb.append(xmlEscape(ch));

		}

		return sb.toString();
	}

	public static String unicodeEscape(String in) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < in.length(); i++) {
			char ch = in.charAt(i);
			if (ch < 128)
				sb.append(ch);
			else
				sb.append(unicodeEscape(ch));

		}

		return sb.toString();
	}

	private static String unicodeEscape(char ch) {
		String hex = toHex(ch);
		hex = StringUtil4.createString(4 - hex.length(), '0') + hex;
		return "\\u" + hex;
	}

	private static Object xmlEscape(char ch) {
		return "&#" + ((int) ch) + ";";
	}

//	public static String encodePercent(String val) {
//		return encodePercent(val, new F1<Character,String>() {public String e(Character ch) {
//			return encodePercent(ch);
//		}});
//	}

	public static String encodePercent(String val) {
		byte[] bytes = val.getBytes(Charset.forName("UTF-8"));
		
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < bytes.length; i++) {
			byte ch = bytes[i];
			if (ch > 0 && Character.isLetterOrDigit(ch)) {
				sb.append((char)ch);
			} else {
				sb.append(encodePercent(ch));
			}

		}

		return sb.toString();
	}

	public static String decodePercent(String string) {
		return RegexUtil.replaceAll(string, "%(\\w\\w)", new F1<Matcher,String>() {public String e(Matcher m) {
			return "" + (char)Integer.parseInt(m.group(1), 16);
		}});
	}
	public static byte[] decodePercentToBytes(String string) {
		final ByteArrayOutputStream bo = new ByteArrayOutputStream();
		RegexUtil.each("%(\\w\\w)", string, 
				new P1<Matcher>() {public void e(Matcher m) {
					bo.write(Integer.parseInt(m.group(1), 16));
				}},
				new P1<String>() {public void e(String unmatched) {
					try {
						bo.write(unmatched.getBytes());
					} catch (IOException e1) {
						throw new RuntimeException(e1.getMessage(), e1);
					}
				}}
		);
		return bo.toByteArray();
	}
	
	public static void main(String[] args) {
//		byte[] c = new byte[] {(byte) 0xc3,(byte) 0xa1};
		DesktopUI4.alert2(new String(decodePercentToBytes("B%C3%A1n"), Charset.forName("UTF-8")));
		
	}
	
	public static String urlEncode(String val) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < val.length(); i++) {
			char ch = val.charAt(i);
			if (ch < 128 && (Character.isLetterOrDigit(ch) || ch == '/' || ch == ':' || ch == '.' || ch == '_'))
				sb.append(ch);
			else
				sb.append(encodePercent((byte)ch));

		}

		return sb.toString();
	}

	public static String urlEncode(byte[] val) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < val.length; i++) {
			byte b = val[i];

			String escape = toHex(b + 128);
			sb.append(escape);
		}

		return sb.toString();
	}

	public static byte[] urlDecodeBytes(String key) {
		byte[] bs = new byte[key.length() / 2];
		for (int i = 0; i < key.length() / 2; i++) {
			bs[i] = (byte) (((int) unHex(key.substring(i * 2, i * 2 + 2))) - 128);
		}
		return bs;
	}


	private static final Pattern PTN_SHRINK = Pattern.compile("\\s+");

	public static String shrinkSpaces(String str) {
		return PTN_SHRINK.matcher(str.trim()).replaceAll(" ");
	}

	public static String encodePercent(int ch) {
		if (ch<0) {
			ch += 256;
		}
		return "%" + toHex(ch);
	}
	
//	public static void main(String[] args) {
//		unescape("%7B%22clipRefId%22%3A%225018-title%22%2C%22flash_video_url_type%22%3A%22%22%2C%22fullyQualifiedURL%22%3A%22http%3A%2F%2Fwww.howstuffworks.com%2Fvideos%2Fits-all-geek-to-me-wireless-internet-security.html%22%2C%22episodeTitle%22%3A%22Wireless%20Internet%20Security%22%2C%22flash_video_url%22%3A%22%22%2C%22durationForm%22%3A%22short%22%2C%22promoLinkText%22%3A%22%22%2C%22duration%22%3A108000%2C%22programTitle%22%3A%22It's%20All%20Geek%20to%20Me%22%2C%22aspectRatio%22%3A%2...%20on%20the%20computer.%22%2C%22m3u8%22%3A%22%22%2C%22geoFilter%22%3A%22%22%2C%22networkId%22%3A%22HSW%22%2C%22promoLinkURL%22%3A%22http%3A%2F%2Fvideos.howstuffworks.com%2Fscience-channel%2F7371-editing-digital-photos-video.htm%22%2C%22thumbnailURL%22%3A%22http%3A%2F%2Fstatic.howstuffworks.com%2Fgif%2Fvideos%2F120x90%2F7371.jpg%22%7D%5D%7D");
//	}

	public static String unescape(String src) {
		StringBuffer sb = new StringBuffer();

		int index = 0;
		int found;
		while ((found = src.indexOf('%', index)) != -1) {
			sb.append(src.substring(index, found));

			// Unescaping
			sb.append(unHex(src.substring(found + 1, found + 3)));

			index = found + 3;
		}
		sb.append(src.substring(index));

		return sb.toString();
	}

	public static String toHex(int ch) {
		if (ch > 255) {
			
		}
		String s = Integer.toHexString(ch).toUpperCase();
		return s.length() == 1 ? "0" + s : s;
	}
	
//	public static void main(String[] args) {
//		System.out.println((int)'Đ');
//		System.out.println(toHex('Đ'));
//	}

	public static char unHex(String str) {
		return (char) Integer.parseInt(str, 16);
	}

	/**
	 * Trim the source and lowercase it's first char
	 *
	 * @param source
	 * @return
	 * @throws NullPointerException
	 *             if source == null
	 */
	public static String lowerCaseFirstChar(String source) {
		source = source.trim();

		if (source.length() == 0)
			return "";
		else
			return Character.toLowerCase(source.charAt(0)) + source.substring(1, source.length());
	}

	/**
	 * Trim the source and uppercase it's first character
	 *
	 * @param source -
	 *            The string to be malnipulated
	 * @return The result String, null if the source String is null
	 */
	public static String upperCaseFirstChar(String source) {
		if (source == null)
			return null;

		source = source.trim();

		if (source.length() == 0)
			return "";
		else
			return Character.toUpperCase(source.charAt(0)) + source.substring(1, source.length());
	}
	public static F1<String,String> upperCaseFirstCharF = new F1<String,String>() {public String e(String obj) {
		return upperCaseFirstChar(obj);
	}};

	/**
	 *
	 * @param pos
	 * @param str
	 * @return String with a pos marked
	 */
	public static String markPosition(int pos, String str) {
		return str.substring(0, pos) + "$MARK$" + str.substring(pos);
	}

	/**
	 * Find the target if it meet more time than the anti String.
	 *
	 * For example: it will find the last ) here: " war( aew aw) aerawe) ear" if called: findNext(")", "(", " war( aew
	 * aw) aerawe) ear", 0)
	 *
	 * @param target
	 * @param anti
	 * @param source
	 * @param startFrom
	 * @return
	 */
	public static int findNext(String target, String anti, CharSequence source, int startFrom) {
		StringTracker st = new StringTracker(new String[] { target, anti });
		int length = source.length();
		int level = 0;
		for (int i = startFrom; i < length; i++) {
			if (st.track(source.charAt(i))) {
				if (st.checkAgainst(anti)) {
					level++;
				} else if (level == 0) {
					return i - target.length() + 1;
					// + 1 because i is not at
					// tail but point the last
					// char.
				} else { // meet target but level not == 0
					level--;
				}
			}
		}
		return -1;
	}
	
	public static int findBack(String target, String anti, CharSequence source, int startFrom) {
		target = reverse(target);
		anti = reverse(anti);
		StringTracker st = new StringTracker(new String[] { target, anti });
		int level = 0;
		for (int i = startFrom - 1; i > -1; i--) {
			if (st.track(source.charAt(i))) {
				if (st.checkAgainst(anti)) {
					level++;
				} else if (level == 0) {
					return i;
					// + 1 because i is not at
					// tail but point the last
					// char.
				} else { // meet target but level not == 0
					level--;
				}
//				System.out.println(level);
			}
		}
		return -1;
	}
	

	public static String reverse(String target) {
		char[] cs = new char[target.length()];
		for (int i = 0; i < cs.length; i++) {
			cs[i] = target.charAt(cs.length - i - 1);
		}
		return new String(cs);
	}

	/**
	 *
	 * @param target
	 * @param not
	 * @param before
	 * @param source
	 * @param startFrom
	 * @return
	 */
	public static int findNextButNotAndBefore(String target, String not, String before, CharSequence source,
			int startFrom) {
		StringTracker st = new StringTracker(new String[] { target, not, before });
		int length = source.length();
		for (int i = startFrom; i < length; i++) {
			if (st.track(source.charAt(i))) {
				if (st.checkAgainst(before)) {

					break;
				} else if (st.checkAgainst(not)) {
					continue;
				} else {
					return i - target.length() + 1;
					// + 1 because i is not at
					// tail but point the last
					// char.
				}
			}
		}
		return -1;
	}

	/**
	 * Find the target if it meet more time than the anti char.
	 *
	 * For example: it will find the last ) here: " war( aew aw) aerawe) ear" if called: findNext(")", "(", " war( aew
	 * aw) aerawe) ear", 0)
	 *
	 * @param target
	 * @param anti
	 * @param source
	 * @param pos
	 * @return
	 */
	public static int findNext(char target, char anti, CharSequence source, int pos) {
		int length = source.length();
		int level = 0;
		for (int i = pos; i < length; i++) {
			char found = source.charAt(i);

			if (found == anti)
				level++;
			else if (found == target)
				if (level == 0)
					return i;
				else
					level--;

		}
		return -1;
	}

	/**
	 * Find the target if it meet more time than the anti char.
	 *
	 * For example: it will find the last ) here: " war( aew aw) aerawe) ear" if called: findNext(")", "(", " war( aew
	 * aw) aerawe) ear", 0)
	 *
	 * @param target
	 * @param anti
	 * @param source
	 * @param pos
	 * @return
	 */
	public static int findBack(char target, char anti, String source, int pos) {
		int level = 0;
		for (int i = pos; i > -1; i--) {
			char found = source.charAt(i);

			if (found == anti)
				level++;
			else if (found == target)
				if (level == 0)
					return i;
				else
					level--;

		}
		return -1;
	}

	public static int findWordBack(String word, int pos, String source) {
		int rollIndex = pos;
		while (rollIndex > -1) {
			rollIndex = source.lastIndexOf(word, rollIndex);
			if (!Character.isLetter(source.charAt(rollIndex - 1))
					&& !Character.isLetter(source.charAt(rollIndex + word.length())))
				break;
		}
		return rollIndex;
	}

	public static int findWordNext(String word, String source) {
		return findWordNext(word, 0, source);
	}

	/**
	 *
	 * @param word
	 * @param pos
	 * @param source
	 * @return
	 */
	public static int findWordNext(String word, int pos, String source) {
		int rollIndex = pos;
		int length = source.length();
		while (rollIndex > -1) {
			rollIndex = source.indexOf(word, rollIndex);

			if (rollIndex == -1)
				break;

			if ((rollIndex == 0 || !Character.isLetter(source.charAt(rollIndex - 1)))
					&& (rollIndex + word.length() == length || !Character.isLetter(source.charAt(rollIndex
							+ word.length()))))
				break;

			rollIndex++;
		}
		return rollIndex;
	}

	/**
	 * Return index if target is met before the before String
	 *
	 * @param target
	 * @param before
	 * @param source
	 * @param pos
	 * @return
	 */
	public static int findBefore(char target, char before, CharSequence source, int pos) {
		int targetPos = indexOf(target, pos, source);
		if (targetPos == -1)
			return -1;
		int beforePos = indexOf(before, pos, source);
		if (beforePos == -1 || beforePos > targetPos)
			return targetPos;
		else
			return -1;
	}

	/**
	 * Check if this string contains any of the array
	 *
	 * @param str
	 * @param arr
	 * @return equal one
	 */
	public static boolean appearOne(String str, String[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if (str.indexOf(arr[i]) > -1)
				return true;
		}
		return false;
	}

	/**
	 * Check if this string equals any of the array
	 *
	 * @param str
	 * @param arr
	 * @return equal one
	 */
	public static boolean equalOne(String str, String[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if (str.equals(arr[i]))
				return true;
		}
		return false;
	}

	/**
	 * Check if this string equals any of the array
	 *
	 * @param str
	 * @param arr
	 * @return equal one
	 */
	public static boolean equalIgnoreCaseOne(String str, String[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if (str.equalsIgnoreCase(arr[i]))
				return true;
		}
		return false;
	}

	/**
	 * Check if this string equals any of the array
	 *
	 * @param str
	 * @param arrDeath
	 * @param arrOK
	 * @return equal one
	 */
	public static boolean equalOneExcept(String str, String[] arrDeath, String[] arrOK) {
		if (equalOne(str, arrOK))
			return false;
		for (int i = 0; i < arrDeath.length; i++) {
			if (str.equals(arrDeath[i]))
				return true;
		}
		return false;
	}

	/**
	 *
	 * @param ptnToFind
	 * @param startOfThisFound
	 * @param content
	 * @return Matcher with last found
	 */
	public static Matcher findLastMatch(Pattern ptnToFind, int startOfThisFound, CharSequence content) {
		Matcher matcher = ptnToFind.matcher(content);
		String lastFoundString = null;
		while (matcher.find() && matcher.end() <= startOfThisFound) {
			lastFoundString = matcher.group();
		}
		matcher = ptnToFind.matcher(lastFoundString);
		matcher.matches();
		return matcher;
	}

	/**
	 * Generate the String that has chr character duplicated num number of times
	 * @param num The number of character
	 * @param chr The character to be duplicated
	 * @return the String that has chr character duplicated num number of times
	 */
	public static String createString(int num, char chr) {
		char[] chs = new char[num];
		for (int i = 0; i < chs.length; i++) {
			chs[i] = chr;
		}
		return new String(chs);
	}

	/**
	 * Charsequence util - lastIndexOf
	 *
	 * @param chr
	 * @param str
	 * @return position of last found char
	 */
	public static int lastIndexof(char chr, int pos, CharSequence str) {
		int rollPos;
		for (rollPos = pos - 1; rollPos > -1; rollPos--) {
			if (str.charAt(rollPos) == chr)
				break;
		}

		return rollPos;
	}

	/**
	 * Charsequence util - indexOf
	 *
	 * @param chr
	 * @param str
	 * @return position of next found char
	 */
	public static int indexOf(char chr, int pos, CharSequence str) {
		int rollPos;
		int length = str.length();
		for (rollPos = pos; rollPos < length; rollPos++) {
			if (str.charAt(rollPos) == chr)
				break;
		}

		return rollPos;
	}

	public static int indexOf(String target, int index, CharSequence str) {
		StringTracker tracker = new StringTracker(target);
		for(;index < str.length();index ++ ) {
			if (tracker.track(str.charAt(index))) {
				return index - target.length() + 1;
			}
		}
		return -1;
	}

	/**
	 *
	 * @param chr
	 * @param string
	 * @return count
	 */
	public static int countHappens(char chr, CharSequence string) {
		if (string==null) {
			return 0;
		}
		int length = string.length();
		int count = 0;
		for (int i = 0; i < length; i++) {
			if (string.charAt(i) == chr)
				count++;
		}
		return count;
	}

	/**
	 *
	 * @param pos
	 * @param source
	 * @return get a line
	 */
	public static String getLine(int pos, CharSequence source) {
		int start = getLineBeginPos(pos, source);
		int end = getLineEndPos(pos, source);
		return source.subSequence(start, end).toString();

	}

	public static int getLineBeginPos(int pos, CharSequence source) {
		int start = lastIndexof('\n', pos, source) + 1;
		if (start == -1)
			start = 0;
		return start;
	}

	public static int getLineEndPos(int pos, CharSequence source) {
		int end = MathUtil4.minNotMinus1(indexOf('\n', pos, source), indexOf('\r', pos, source));
		end = end > -1 ? end : source.length();
		return end;
	}

	/**
	 *
	 * @param pos
	 * @param source
	 * @return get a line
	 */
	public static String getLine(int pos, byte[] source) {
		int start = lastIndexof('\n', pos, source) + 1;
		if (start == -1)
			start = 0;
		int end = MathUtil4.minNotMinus1(indexof('\n', pos, source), indexof('\r', pos, source));
		end = end > -1 ? end : source.length;

		return new String(source, start, end);

	}

	/**
	 *
	 * @param chr
	 * @param pos
	 * @param source
	 * @return
	 */
	private static int indexof(char chr, int pos, byte[] source) {
		int rollIndex;

		// Validation
		if (source.length == 0)
			return -1;

		for (rollIndex = pos; rollIndex < source.length; rollIndex++) {
			if (chr == source[rollIndex])
				return rollIndex;
		}

		return -1;
	}

	/**
	 *
	 * @param chr
	 * @param pos
	 * @param source
	 * @return
	 */
	private static int lastIndexof(char chr, int pos, byte[] source) {
		int rollIndex;

		// Validation
		if (source.length == 0)
			return -1;

		for (rollIndex = pos; rollIndex > -1; rollIndex--) {
			if (chr == source[rollIndex])
				return rollIndex;
		}

		return -1;
	}

	/**
	 * From abc | to |abc From abc| to |abc From ab|c to |abc
	 *
	 * @param pos
	 * @param source
	 * @return
	 */
	public static int jumpBack(int pos, CharSequence source) {
		StringTracker st = StringTracker.makeSpacesCharTracker();

		// Jump state
		boolean charMet = false;
		int i;

		for (i = pos - 1; i > -1; i--) {
			if (!charMet) {
				if (!st.track(source.charAt(i))) {
					charMet = true;
					continue;
				}
			} else {
				if (st.track(source.charAt(i)))
					return i + 1;
			}
		}
		return 0;
	}

	private static final Pattern ptnFindFirstWord = Pattern.compile("\\b(\\S+?)\\b");

	/**
	 * Get the first word of an inputed String
	 *
	 * @param source
	 * @return first word
	 */
	public static String getFirstWord(CharSequence source) {
		return getNextWord(0, source);
	}
	public static String getLastWord(CharSequence source) {
		return RegexUtil.lastFound(ptnFindFirstWord, source.length(), source).group(1);
	}
	
	public static String getNextWord(int pos, CharSequence source) {
		Matcher matcher = ptnFindFirstWord.matcher(source);
		if (matcher.find(pos)) {
			return matcher.group(1);
		} else {
			return null;
		}
	}
	public static int indexOfNextWord(int pos, CharSequence source) {
		Matcher matcher = ptnFindFirstWord.matcher(source);
		if (matcher.find(pos)) {
			return matcher.start(1);
		} else
			return -1;
	}


	/**
	 *
	 * @param t
	 * @return
	 */
	public static String getStackTrace(Throwable t) {
		ByteArrayOutputStream bao = new ByteArrayOutputStream(1024);
		PrintWriter pr = new PrintWriter(bao);
		t.printStackTrace(pr);
		return bao.toString();
	}

	/**
	 *
	 * @param newLine
	 * @param pos
	 * @param content
	 */
	public static void replaceLine(String newLine, int pos, StringBuffer content) {
		int from = pos == 0 ? 0 : content.lastIndexOf("\n", pos - 1);
		from = from == -1 ? 0 : from;

		int to = content.indexOf("\n", pos);
		to = to == -1 ? content.length() : to;

		content.replace(from, to, newLine);
	}

//    public static void main(String[] args) {
//        System.out.println(replaceLine("c", 3, "a\n- \nb"));
//    }

	/**
	 *
	 * @param code
	 * @param pos
	 * @param content
	 * @return
	 */
	public static String replaceLine(String code, int pos, String content) {

		StringBuffer sb = new StringBuffer(content);
		replaceLine(code, pos, sb);
		return sb.toString();
	}

    public static String xmlContentEscape(String s) {
        return "\"" + (s == null ? "" : s.replaceAll("<", "&lt;").replaceAll(">", "&gt;")) + "\"";
    }

    public static String rejectSingleLineComment(CharSequence input, String commentMarkup) {
        String s = commentMarkup + ".*$";
        Pattern p = Pattern.compile(s, Pattern.MULTILINE);
        Matcher m = p.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            if (!JavaCodeUtil.posInString(m.start(), input)) {
				m.appendReplacement(sb, createString(m.group(0)
                        .length() - 1, ' ') + '\n');
			} else {
				m.appendReplacement(sb, "");
				sb.append(m.group());
			}
        }
        m.appendTail(sb);
        return sb.toString();
    }
    
//    public static String rejectSingleLineComment1(CharSequence input, String commentMarkup) {
//    	
//        String s = commentMarkup + ".*$";
//        Pattern p = Pattern.compile(s, Pattern.MULTILINE);
//        Matcher m = p.matcher(input);
//        StringBuffer sb = new StringBuffer();
//        while (m.find()) {
//            if (!JavaCodeUtil.posInString(m.start(), input))
//                m.appendReplacement(sb, createString(m.group(0)
//                        .length() - 1, ' ') + '\n');
//            else
//                m.appendReplacement(sb, m.group());
//        }
//        m.appendTail(sb);
//        return sb.toString();
//    }

    public static class Patterns {

		public static final Pattern SPACE_PTN = Pattern.compile("\\s");

		public static final Pattern NON_SPACE_PTN = Pattern.compile("\\S");
	}

	/**
	 * TODO Incompleted
	 *
	 * @param string
	 * @return
	 */
	public static String formatQueryString(String string) {
		return string.replaceAll(" ", "%20");
	}

	public static int indexOf(String regex, String str) {
		return indexOf(regex, str, 0, 0);
	}
	public static int indexOf(String regex, String str, int flag) {
		return indexOf(regex, str, flag, 0);
	}

	public static int indexOf(String regex, String str, int flag, int startPos) {
		Matcher matcher = Pattern.compile(regex, flag).matcher(str);
		if (matcher.find(startPos)) {
			return matcher.start();
		} else {
			return -1;
		}
	}

	/**
	 * This function detach a string into smaller contents by extracting strings inside openChar and closeChar.
	 *
	 * @param openChar
	 * @param closeChar
	 * @param content
	 * @return
	 */
	public static List detachContent(char openChar, char closeChar, String content) {
		StringNode node = makeStringNode(openChar, closeChar, content);
		return node.flatUp();
	}

	public static StringNode makeStringNode(char openChar, char closeChar, String content) {
		StringNode node = new StringNode();
		node.setSubNodes(new ArrayList());
		int index = 0;
		int pos;
		while ((pos = content.indexOf(openChar, index)) > -1) {
			int end = StringUtil4.findNext(closeChar, openChar, content, pos + 1);

			node.getSubNodes().add(makeStringNode(openChar, closeChar, content.substring(pos + 1, end)));

			content = content.substring(0, pos + 1) + content.substring(end);
			index = pos + 2;
		}
		node.setValue(content);

		return node;
	}

	public static String declareStringBuffer(String str) {
		return "StringBuffer sb = new StringBuffer();\n\n" + "sb.append(       \""
				+ toJavaStringLiteral(str).replaceAll("\n", "\");\nsb.append(\"\\\\n\" + \"")
				+ "\");\n\nreturn sb.toString();";
	}

	public static String declareString(String str) {
		return "\"" + toJavaStringLiteral(str).replaceAll("\n", "\n\" +\n\"") + "\"";
	}

	public static String toJavaStringLiteral(String raw) {
		return raw.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
	}


    /**
     * Add ' ' to make str as long as specified <p>
     * Append ' ' chars to right
     * @param str String
     * @param length min length
     * @return Str with length no less than min length
     */
	public static String ensureLengthLeft(String str, int length) {
		return ensureLength(str, length, true, ' ');
	}


	public static String ensureLengthMiddle(String str, int countLength) {
		if (str == null)
			str = "null";
		String left = createString((int)Math.floor((countLength - str.length()) / 2), ' ');
		String right = createString((int)Math.ceil((countLength - str.length()) / 2), ' ');
		return left + str + right;
	}

    /**
     * Add chars if str's length not enougth
     * @param str The string
     * @param minLength the min length
     * @param appendToRight append to right
     * @param appendChar the appended char
     * @return the string with length >= minLength
     */
	public static String ensureLength(String str, int minLength, boolean appendToRight,
			char appendChar) {
		if (str == null)
			str = "null";
		else if (appendToRight) {
			str = str + createString(Math.max(minLength, str.length()) - str.length(), appendChar);
		} else {
			str = createString(Math.max(minLength, str.length()) - str.length(), appendChar) + str;
		}
		return str;
	}

	public static String ensureLengthRight(int value, int length) {
		return ensureLength(String.valueOf(value), length, false, ' ');
	}

	public static String ensureLengthRight(String str, int length) {
		return ensureLength(str, length, false, ' ');
	}

	public static String innerTrim(String str) {
		if (str == null)
			return null;
		return str.trim().replaceAll("[ \t\r\n]+", " ");
	}

	public static String rTrim(String str) {
		return trimAt(str.length(), str);
	}

	public static String trimAt(int index, String str) {
		int rightPos, leftPos;
		for (rightPos = index; rightPos < str.length(); rightPos++) {
			char c = str.charAt(rightPos);
			if (c == ' ' || c == '\n' || c == '\r' || c == '\t') {
				continue;
			} else {
				break;
			}
		}

		for (leftPos = index - 1; leftPos > -1; leftPos--) {
			char c = str.charAt(leftPos);
			if (c == ' ' || c == '\n' || c == '\r' || c == '\t') {
				continue;
			} else {
				leftPos++;
				break;
			}
		}
		return str.substring(0, leftPos) + str.substring(rightPos);
	}

	public static int lineNum(int pos, String content) {
		int lineNum = 1;
		for (int i = 0; i < content.length() && i < pos; i++) {
			if (content.charAt(i) == '\n')
				lineNum++;
		}
		return lineNum;
	}

	private static final Pattern PTN_UNSPACE = Pattern.compile("\\S");

	public static String wipeWhiteInside(String content, String start, String end) {
		int pos = 0;
		StringBuffer sb = new StringBuffer(content);

		while (pos < content.length()) {
			int startPos = content.indexOf(start, pos);

			if (startPos == -1)
				break;

			startPos += start.length();
			int endPos = content.indexOf(end, startPos);

			// Wipe to end if find no end String
			if (endPos == -1)
				endPos = content.length();
			// endPos += end.length();

			sb.replace(startPos, endPos, PTN_UNSPACE.matcher(sb.substring(startPos, endPos)).replaceAll(" "));

			pos = endPos + end.length();
		}

		return sb.toString();
	}

	public static String wipeWhiteOutside(String content, String start, String end) {
		int pos = 0;
		StringBuffer sb = new StringBuffer(content);

		while (pos < content.length()) {
			int startPos = content.indexOf(start, pos);

			if (startPos == -1)
				break;

			// startPos += start.length();
			int endPos = content.indexOf(end, startPos);

			// Wipe to end if find no end String
			if (endPos == -1)
				endPos = content.length();
			endPos += end.length();

			sb.replace(startPos, endPos, PTN_UNSPACE.matcher(sb.substring(startPos, endPos)).replaceAll(" "));

			pos = endPos + end.length();
		}

		return sb.toString();
	}

//	/**
//	 *
//	 * @param pos
//	 * @param ranges
//	 * @return
//	 */
//	public static boolean inRanges(int pos, List ranges) {
//		for (Iterator iterator = ranges.iterator(); iterator.hasNext();) {
//			Range range = (Range) iterator.next();
//
//			if (range.getFrom() <= pos && range.getTo() >= pos) {
//				return true;
//			}
//		}
//		return false;
//	}

	public static boolean isMutiLine(String content) {
		return content != null && content.indexOf("\n") > -1;
	}

	public static String[] lineCut(String message, int num, int max1, int max2) {
		int index = 0;

		if (message == null)
			return new String[0];

		ArrayList list = new ArrayList();
		while (true) {

			int max = num-- > 0 ? max1 : max2;
			int cutPos = getCutPos(message, index, max);

			list.add(message.substring(index, cutPos));

			if (cutPos < message.length())
				index = cutPos + 1;
			else
				break;
		}

		return (String[]) list.toArray(new String[list.size()]);
	}

	private static int getCutPos(String message, int index, int max) {
		int lastLf = -1;
		int i;
		for (i = index; i < message.length() && i < max + index; i++) {
			if (message.charAt(i) == '\n')
				lastLf = i;

			if (i == message.length() - 1)
				return i + 1;
		}

		if (lastLf == -1)
			return i;
		else
			return lastLf;
	}

	/**
	 * Find in one place, replace is the other
	 *
	 * @param find
	 * @param replace
	 * @param source
	 * @param dest
	 * @return
	 */
	public static String replaceAll(String find, String replace, String source, String dest) {
		StringBuffer sb = new StringBuffer();

		int index = 0;
		while (true) {
			int found = source.indexOf(find, index);
			if (found == -1)
				break;
			sb.append(dest.substring(index, found)).append(replace);
			index = found + replace.length();
		}
		sb.append(dest.substring(index));

		return sb.toString();
	}

	public static int indexOf(String[] targets, String source, int startFrom) {
		StringTracker st = new StringTracker(targets);
		int length = source.length();
		for (int i = startFrom; i < length; i++) {
			if (st.track(source.charAt(i))) {
				return i - st.getMatchString().length() + 1;
				// + 1 because i is not at
				// tail but point the last
				// char.
			}
		}
		return -1;
	}

	public static int indexOf(String[] targets, String source) {
		return indexOf(targets, source, 0);
	}

	/**
	 * Make code that will construct the specified String when executed
	 *
	 * @param rawStr
	 * @return code String
	 */
	public static String parseStringToJavaCode(String rawStr) {
		ByteArrayInputStream in = new ByteArrayInputStream(rawStr.getBytes());
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		StringBuffer sbReturn = new StringBuffer();
		String strTemp;
		sbReturn.append("StringBuffer sb = new StringBuffer();\n\n");

		try {
			while ((strTemp = br.readLine()) != null) {
				sbReturn.append("sb.append(\"\\n\").append(\""
						+ strTemp.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"") + "\");\n");
			}
		} catch (IOException e) {
			e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
		}

		sbReturn.append("\nreturn sb.toString();");

		return sbReturn.toString();
	}

	public static int countLine(String content) {
		return countHappens('\n', content) + 1;
	}

	public static void findLongTokens(String str, String not, int howLong) {

	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

    /**
     * String is not null and length > 0
     * @param str
     * @return
     */
	public static boolean isNotEmpty(String str) {
		return str != null && str.length() > 0;
	}

	/**
	 * TODO Unfinished
	 * @param req
	 * @return
	 */
	public static String[] getCommandParams(String req) {
		return req.substring(req.indexOf(' ') + 1).replaceAll("\"", "").split(" ");
	}

	/**
	 * Synchronize the case of <code>str</code> based on <code>sample</code>'s case<p/>
	 * Assume that str is much similar to sample, only case difference.
	 * @param str
	 * @param sample
	 * @return
	 */
	public static String syncCase(String str, String sample) {
		StringBuilder sb = new StringBuilder();
		char[] cs = str.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			char c = cs[i];
			if (Character.isUpperCase(sample.charAt(i)) && !Character.isUpperCase(c) ) {
				c = Character.toUpperCase(c);
			} else if (Character.isLowerCase(sample.charAt(i)) && !Character.isLowerCase(c)) {
				c = Character.toLowerCase(c);
			}
			sb.append(c);
		}
		return sb.toString();
	}

}