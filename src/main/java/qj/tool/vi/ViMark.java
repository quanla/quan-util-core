package qj.tool.vi;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import qj.ui.DesktopUI4;
import qj.util.Cols;
import qj.util.IOUtil;
import qj.util.MathUtil;
import qj.util.ObjectUtil;
import qj.util.funct.Douce;
import qj.util.funct.F1;
import qj.util.funct.F2;
import qj.util.funct.P3;

@SuppressWarnings("unchecked")
public class ViMark {
	/**
	 * mark, <nonmarkChar -> markChar>
	 */
	static HashMap<Character, Map<Character, String>> nonmarkToMarkMap = nonmarkToMarkMap();
	
	/**
	 * markChar, <nonMarkChar, mark>
	 */
	static HashMap<Character, Douce<Character, Character>> markToNonmarkMap = markToNonmarkMap();
	static Map<Integer, Character> codeToMarkMap = Cols.map(
			768, '`',
			769, '\'',
			771, '~',
			777, '?',
			803, 'j'
			);
	/**
	 * The unicode characters are sometimes formatted with dual char, first for the main char, next for the mark.<br>
	 * This formatting make most string processing fail<br>
	 * This method will fix that problem, and return the string in normal formatting, each character take only 1 char
	 * @param string
	 * @return
	 */
	public static String normalizeSpecialUnicodeEncoding(String string) {
		if (string == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			char charAt = string.charAt(i);
			Character mark = codeToMarkMap.get((int)charAt);
			if (mark != null) {
				char nonMarkChar = sb.charAt(sb.length() - 1);
				String markChar = nonmarkToMarkMap.get(mark).get(nonMarkChar);
				if (markChar != null) {
					sb.replace(sb.length() - 1, sb.length(), markChar);
				}
			} else {
				sb.append(charAt);
			}
		}
		return sb.toString();
	}

	private static HashMap<Character, Map<Character, String>> nonmarkToMarkMap() {
		final HashMap<Character, Map<Character, String>> nonMarkToMarkMap = new HashMap<Character, Map<Character,String>>();
		
		eachMarkMapping(new P3<Character,Character,Character>() {public void e(Character mark, Character nonMarkChar, Character markChar) {
			Map<Character, String> markMap = nonMarkToMarkMap.get(mark);
			if (markMap == null) {
				markMap = new HashMap<Character, String>();
				nonMarkToMarkMap.put(mark, markMap);
			}
			markMap.put(nonMarkChar, markChar.toString());
			markMap.put(Character.toUpperCase(nonMarkChar), markChar.toString().toUpperCase());	
		}});
		
		return nonMarkToMarkMap;
	}
	
	private static HashMap<Character, Douce<Character, Character>> markToNonmarkMap() {
		final HashMap<Character, Douce<Character, Character>> markToNonmarkMap = new HashMap<Character, Douce<Character, Character>>();
		
		eachMarkMapping(new P3<Character,Character,Character>() {public void e(Character mark, Character nonMarkChar, Character markChar) {
			markToNonmarkMap.put(markChar, new Douce<Character, Character>(nonMarkChar, mark));
//			System.out.println(markChar + ": \"" + nonMarkChar + "\",");
			markToNonmarkMap.put(Character.toUpperCase(markChar), new Douce<Character, Character>(Character.toUpperCase(nonMarkChar), mark));
		}});
		
		return markToNonmarkMap;
	}
	
	/**
	 * 
	 * @param p3 
	 */
	public static void eachMarkMapping(P3<Character,Character,Character> p3) {

		String viMarkContent = IOUtil.toString(ViMark.class.getResourceAsStream("viMark.txt"), "UTF-8");
		String[] split = viMarkContent.split("\r?\n");
		String headLine = split[0];
		
		Character space = ' ';
		for (int i = 1; i < split.length; i++) {
			String line = split[i];
			Character nonMarkChar = line.charAt(0);
			for (int j = 1; j < line.length(); j++) {
				Character markChar = line.charAt(j);
				Character mark = headLine.charAt(j);
				
				if (!markChar.equals(space)) {
					p3.e(mark, nonMarkChar, markChar);
					
				}
			}
		}
	}
	
	public static String removeMark(String string) {
		if (string==null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			sb.append(removeMark(string.charAt(i)));
		}
		return sb.toString();
	}

	private static Character removeMark(Character c) {
		while (true) {
			Douce<Character, Character> nonmarkChar = markToNonmarkMap.get(c);
			if (nonmarkChar==null) {
				return c;
			}
			c = nonmarkChar.get1();
		}
	}
	
	public static F1<String,String> removeMarkF = new F1<String, String>() {public String e(String string) {
		return removeMark(string);
	}};

	public static String markedCharsLowered() {
		StringBuilder sb = new StringBuilder();
		for (Character markChar : markToNonmarkMap.keySet()) {
			if (Character.isLowerCase(markChar)) {
				sb.append(markChar);
			}
		}
		return sb.toString();
	}
	public static String markedCharsUppered() {
		StringBuilder sb = new StringBuilder();
		for (Character markChar : markToNonmarkMap.keySet()) {
			if (Character.isUpperCase(markChar)) {
				sb.append(markChar);
			}
		}
		return sb.toString();
	}
	
//	public static void main(String[] args) {
//		System.out.println(markedCharsLowered());
//	}

	/**
	 * This will not check if base char is same, meaning ồ and ề will return true
	 * @param w1
	 * @param w2
	 * @return
	 */
	public static boolean sameViMarkLine(String w1, String w2) {
		if (ObjectUtil.equals(w1, w2)) {
			return true;
		}
		if (w1 == null || w2 == null) {
			return false;
		}
		if (w1.length() != w2.length()) {
			return false;
		}
		
		for (int i = 0; i < w1.length(); i++) {
			char c1 = w1.charAt(i);
			char c2 = w2.charAt(i);
			if (c1 == c2) {
				continue;
			}
			
			
			
			Set<Character> marks1 = getMarks(c1);
			Set<Character> marks2 = getMarks(c2);
			
			if (marks1.size() == marks2.size()
					&& !marks1.equals(marks2)) {
				return false;
			}
			
			Collection<Character> maxCol = MathUtil.max(Cols.sizeF, marks1, marks2);
			Collection<Character> minCol = MathUtil.max(MathUtil.negativeF(Cols.sizeF), marks1, marks2);
			if (!maxCol.containsAll(minCol)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This will not check if base char is same, meaning ồ and ề will return true
	 * @param less
	 * @param more
	 * @return
	 */
	public static boolean lessViMark(String less, String more) {
		if (ObjectUtil.equals(less, more)) {
			return true;
		}
		if (less == null || more == null) {
			return false;
		}
		if (less.length() != more.length()) {
			return false;
		}
		
		for (int i = 0; i < less.length(); i++) {
			char cLess = less.charAt(i);
			char cMore = more.charAt(i);
			if (cLess == cMore) {
				continue;
			}
			
			Set<Character> marksLess = getMarks(cLess);
			Set<Character> marksMore = getMarks(cMore);
			
			if (marksLess.size() == marksMore.size()
					&& !marksLess.equals(marksMore)) {
				return false;
			}
			
			if (!marksMore.containsAll(marksLess)) {
				return false;
			}
		}
		return true;
	}
	
	public static final F2<String,String,Boolean> lessViMarkF = new F2<String, String,Boolean>() {public Boolean e(String less, String more) {
		return lessViMark(less, more);
	}};

	private static Set<Character> getMarks(Character c1) {
		HashSet<Character> ret = new HashSet<Character>();
		
		while (true) {
			Douce<Character, Character> markDouce = markToNonmarkMap.get(c1);
			if (markDouce == null) {
				return ret;
			}
			Character mark = markDouce.get2();
			ret.add(mark);
			c1 = markDouce.get1();
		}
	}
	
	public static void main(String[] args) {
		System.out.println(sameViMarkLine("ồ", "ơ"));
	}
	
	public static class Unmark {
		public static void main(String[] args) {
			String str = DesktopUI4.prompt2();
			System.out.println(removeMark(str));
		}
	}
}
