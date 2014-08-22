package qj.tool.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import qj.ui.DesktopUI4;
import qj.util.RegexUtil;
import qj.util.StringUtil4;
import qj.util.funct.F1;


public class HtmlUtil {
	private static final String[] LINK_ATTR = new String[] {
					"ACTION",
					"HREF",
					"SRC",
					"TARGET",
					"VALUE",
			};
	private static final String[] SPECIAL_HTML_STRINGS = new String[] {
					"_top",
					"_blank",
					"_parent",
					"_self",
					"true",
					"false",
			};
	
	public static String[] getLinkAttributes() {
		return LINK_ATTR;
	}
	
	public static String[] getSpecialHtmlStrings() {
		return SPECIAL_HTML_STRINGS;
	}
	
	private static final Pattern PTN_LINKBEFORE = Pattern.compile(
		"([\\w-]+)\\s*=\\s*[\"']");
	private static final Pattern PTN_LINKCONTENT = Pattern.compile(
			"[\\w%. :;/?=&-]+");
//	public static SortedSet getAbsoluteLinks(String html, String oriUrl) {
//		SortedSet links = new TreeSet();
//		
//		Found found = findStrings(html);
//		while (found.next()) {
//			String link = found.getFound();
//			
//			if (!PTN_LINKCONTENT.matcher(link).matches())
//				continue;
//			String before = found.getBefore(10);
//			Matcher matcher = PTN_LINKBEFORE.matcher(before);
//			if (matcher.find() && !StringUtil4.equalIgnoreCaseOne(matcher.group(1), getLinkAttributes()))
//				continue;
//			
//			// Link you won't like
//			if (badLink(link))
//				continue;
//			
//			// Absolute it
//			if (!HttpUtil4.isUrlAbsolute(link)) {
//				link = HttpUtil4.absoluteUrl(oriUrl, link);
//			}
//			
//			// For case: value="url=http://..."
//			int index = 0;
//			while ((index = link.indexOf("http://", index)) > -1) {
//				links.add(link.substring(index));
//				index += 1;
//			}
//			
//			links.add(link);
//		}
//		
//		return links;
//	}
	
	private static boolean badLink(String link) {
		if (link.startsWith("&")
				|| link.startsWith(" ")
				|| StringUtil4.equalOne(link, getSpecialHtmlStrings())
				|| link.equals("undefined")
				|| link.equals("bookmark")
				|| (link.indexOf("://") > -1 && link.indexOf("http") != 0)
				|| link.indexOf("-//") > -1
				|| (link.indexOf(" ") > -1 && link.indexOf("/") == -1 && link.indexOf(".") == -1) // etc. "This is not a link"
				)
			return true;
		else
			return false;
	}

//	public static Found findStrings(String html) {
////		html = StringUtil.wipeWhiteInside(html, "<!--", "-->");
//		
//		Found result = new Found();
//		ArrayList founds = new ArrayList();
//		int index = 0;
//		while (true) {
//			
//			// Find 'abc'
//			Range range = new Range();
//			int found = StringUtil4.indexOf(new String[] {"'", "\""}, html, index);
//			if (found == -1)
//				break;
//			range.setFrom(found + 1);
//			String bracket = ("" + html.charAt(found)).intern();
//			String not;
//			if (bracket.equals("'"))
//				not = "\"";
//			else
//				not = "'";
//			int to = StringUtil4.findNextButNotAndBefore(bracket, not, "\n", html, found + 1);
//			if (to == -1) {
//				index = found + 1;
//				continue;
//			}
//			
//			range.setTo(to);
//			index = range.getTo() + 1;
//			
//			founds.add(range);
//		}
//		
//		result.setData(html);
//		result.setFoundRanges(founds);
//		return result;
//	}

    public static String normalizedHtml(String html) {
        if (html == null) {
            return null;
        }
        return html
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll(" ", "&nbsp;")
                .replaceAll("\n", "<br/>")
                ;
    }

    public static void main(String[] args) {
//    	"&#236;"
//    	RegexUtil.replaceAll(new F1<Matcher,String>() {public String e(Matcher m) {
//				return "" + (char)Integer.parseInt(m.group(1));
//			}}, Pattern.compile("&#(\\d+);"), "&#236;");
//		DesktopUI4.alert();
	}
}
