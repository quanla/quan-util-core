package qj.tool.findReplace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.JTextComponent;

import qj.tool.string.StringTracker;

public class FindReplace {
	
	/**
	 * 
	 * @param strToFind
	 * @param ta
	 * @param withRegex
	 * @return true if found
	 */
	public static boolean find(String strToFind, JTextComponent ta, boolean withRegex) {
		int position = ta.getCaretPosition();
		
		if (withRegex) {
			Matcher matcher = Pattern.compile(strToFind).matcher(ta.getText().substring(position));
			int foundPos = matcher.find()?matcher.start():-1;
			if (foundPos>-1) {
				ta.setSelectionStart(foundPos + position);
				ta.setSelectionEnd(foundPos + position + matcher.group().length());
				return true;
			} else
				return false;
		} else {
			int foundPos = ta.getText().substring(position).indexOf(strToFind);
			if (foundPos>-1) {
				ta.setSelectionStart(foundPos + position);
				ta.setSelectionEnd(foundPos + position + strToFind.length());
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * 
	 * @param strToFind
	 * @param strToReplace
	 * @param ta
	 * @param withRegex
	 * @return true if replaced something
	 */
	public static boolean replace(String strToFind, String strToReplace, JTextComponent ta, boolean withRegex) {
		String currentSelection = ta.getSelectedText();
		Matcher matcher;
		Pattern ptn = Pattern.compile(strToFind, Pattern.MULTILINE);
		
		// Check if currentSelection is valid - last find wasn't interrupted
		if (withRegex) {
			matcher = checkCurrentSelectionWithRegex(currentSelection, ptn);
			
			// Interrupted - or not find yet
			//  Do find again.
			if (matcher==null) {
				find(strToFind, ta, withRegex);
				currentSelection = ta.getSelectedText();
				matcher = checkCurrentSelectionWithRegex(currentSelection, ptn);
			}
			
			if (matcher == null) {
				// Fail to find
				return false;
			} else {
				// Do replace.
				ta.replaceSelection(buildStringToReplace(strToReplace, matcher));
				return true;
			}
		} else {
			//*** Do later
			return false; //***
		}
	}
	
	public static void replaceAll(String strToFind, String strToReplace, JTextComponent ta, boolean withRegex) {
		while (replace(strToFind, strToReplace, ta, withRegex))
			;
	}
	
	private static final StringTracker ct = new StringTracker(new String[]{
			"\\0", "\\1", "\\2", "\\3", "\\4", "\\5", "\\6", "\\7", "\\8", "\\9", 
			"\\U", "\\E", "\\L", "\\n", "\\t", "\\\\"
	});
	
	/**
	 * pendingChar: last char is not written to screen to check with next char.
	 * @param strToReplace
	 * @param matcher
	 * @return
	 */
	public static String buildStringToReplace(String strToReplace, Matcher matcher) {
		StringBuffer sbReturn = new StringBuffer();
		boolean forceUpperCase = false;
		boolean forceLowerCase = false;
		int length = strToReplace.length();
		char bufferChar = 0;
		for (int i = 0; i < length; i++) {

			if (!ct.track(strToReplace.charAt(i))) {
//				System.out.println(bufferChar>0);
				if (bufferChar>0)
					appendReturnSB(bufferChar, forceUpperCase, forceLowerCase, sbReturn);
				bufferChar = strToReplace.charAt(i);
			} else {
				bufferChar = 0;
				if (ct.checkAgainst("\\0"))
					appendReturnSB(matcher.group(), forceUpperCase, forceLowerCase, sbReturn);
				else if (ct.checkAgainst("\\1"))
					appendReturnSB(matcher.group(1), forceUpperCase, forceLowerCase, sbReturn);
				else if (ct.checkAgainst("\\2"))
					appendReturnSB(matcher.group(2), forceUpperCase, forceLowerCase, sbReturn);
				else if (ct.checkAgainst("\\3"))
					appendReturnSB(matcher.group(3), forceUpperCase, forceLowerCase, sbReturn);
				else if (ct.checkAgainst("\\4"))
					appendReturnSB(matcher.group(4), forceUpperCase, forceLowerCase, sbReturn);
				else if (ct.checkAgainst("\\5"))
					appendReturnSB(matcher.group(5), forceUpperCase, forceLowerCase, sbReturn);
				else if (ct.checkAgainst("\\6"))
					appendReturnSB(matcher.group(6), forceUpperCase, forceLowerCase, sbReturn);
				else if (ct.checkAgainst("\\7"))
					appendReturnSB(matcher.group(7), forceUpperCase, forceLowerCase, sbReturn);
				else if (ct.checkAgainst("\\8"))
					appendReturnSB(matcher.group(8), forceUpperCase, forceLowerCase, sbReturn);
				else if (ct.checkAgainst("\\9"))
					appendReturnSB(matcher.group(9), forceUpperCase, forceLowerCase, sbReturn);
				else if (ct.checkAgainst("\\U")) {
					forceUpperCase = true;
					forceLowerCase = false;
				}
				else if (ct.checkAgainst("\\L")) {
					forceUpperCase = false;
					forceLowerCase = true;
				}
				else if (ct.checkAgainst("\\E")) {
					forceUpperCase = false;
					forceLowerCase = false;
				}
				else if (ct.checkAgainst("\\n"))
					appendReturnSB('\n', forceUpperCase, forceLowerCase, sbReturn);
				else if (ct.checkAgainst("\\t"))
					appendReturnSB('\t', forceUpperCase, forceLowerCase, sbReturn);
				
				ct.refresh();
			}

			
			if (i==length-1 && bufferChar > 0) {
				appendReturnSB(bufferChar, forceUpperCase, forceLowerCase, sbReturn);
			}
		}
		return sbReturn.toString().replaceAll("\\$", "\\\\\\$");
	}

	/**
	 * Append a char to sb
	 * @param curChar
	 * @param forceUpperCase
	 * @param forceLowerCase
	 * @param sbReturn
	 */
	private static void appendReturnSB(char curChar, boolean forceUpperCase, boolean forceLowerCase, StringBuffer sbReturn) {
//		System.out.println("Append char: " + curChar);
		if (curChar==0)
			return;
		if (forceUpperCase) {
			sbReturn.append(Character.toUpperCase(curChar));
		} else if (forceLowerCase) {
			sbReturn.append(Character.toLowerCase(curChar));
		} else
			sbReturn.append(curChar);
	}

	private static void appendReturnSB(String strAppend, boolean forceUpperCase, boolean forceLowerCase, StringBuffer sbReturn) {
		if (forceUpperCase) {
			sbReturn.append(strAppend.toUpperCase());
		} else if (forceLowerCase) {
			sbReturn.append(strAppend.toLowerCase());
		} else
			sbReturn.append(strAppend);
	}

	private static Matcher checkCurrentSelectionWithRegex(String currentSelection, Pattern ptn) {
		if (currentSelection==null) 
			return null;
		
		Matcher matcher = ptn.matcher(currentSelection);
		if (!matcher.matches()) {
			// Was interrupted or never find before
			// Do find again
			return null;
		} else {
			return matcher;
		}
	}
	
	public static String replaceAll(String content, String ptnToFind, String strToReplace) {
		return replaceAll(content, Pattern.compile(ptnToFind), strToReplace);
	}
	
	public static String replaceAll(String content, Pattern ptnToFind, String strToReplace) {
		StringBuffer sb = new StringBuffer();
		
		Matcher matcher = ptnToFind.matcher(content);
		
		while (matcher.find()) {
			matcher.appendReplacement(sb, buildStringToReplace(strToReplace, matcher));
		}
		matcher.appendTail(sb);
		
		return sb.toString();
	}

	public static String replaceAll(String replace, Matcher matcher) {
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String val = buildStringToReplace(replace, matcher);
			matcher.appendReplacement(sb, val);
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
}
