package qj.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import qj.util.funct.P1;
import qj.util.math.Range;

@Deprecated
public class XmlUtil extends XmlUtil4 {
	public static void eachTag(String tagName,String document, P1<Tag> p1) {
		int index = 0;
		for (Tag nextTag; (nextTag = nextTag(tagName, document, index)) != null;) {
			index = nextTag.range.getTo();
			p1.e(nextTag);
		}
	}
	public static void eachBeginTag(String tagName,String content, P1<Tag> p1) {
		int index = 0;
		for (Range beginRange; (beginRange = findBeginTag(tagName, content, index)) != null;) {
			index = beginRange.getTo();
			String innerHtml = content.substring(beginRange.getFrom(), beginRange.getTo());
			p1.e(new Tag(innerHtml,getAttrs(content.substring(beginRange.getFrom(), beginRange.getTo())),beginRange));
		}
	}

	/**
	 * Get the whole tag node from tag begin to tag end
	 * @param tagName
	 * @param content
	 * @param fromIndex
	 * @return
	 */
	public static Tag nextTag(String tagName, String content, int fromIndex) {

		Range beginTag = findBeginTag(tagName, content, fromIndex);
		
		if (beginTag==null) {
			return null;
		}
		
		String innerHtml = content.substring(beginTag.getFrom(), beginTag.getTo());
		if (content.charAt(beginTag.getTo() - 2) == '/') {
			return new Tag(innerHtml,getAttrs(innerHtml),beginTag);
		} else {
			String tagEndString = "</" + tagName + ">";
			int tagEnd = StringUtil4.indexOf(tagEndString, beginTag.getTo(), content);
			
			if (tagEnd == -1) { // Can not find end tag
				return new Tag(null,getAttrs(innerHtml),beginTag);
			} else {
				return new Tag(content.substring(beginTag.getTo(),tagEnd),
						getAttrs(innerHtml), new Range(beginTag.getFrom(),tagEnd + tagEndString.length()));
			}
		}
	}
	
	static Pattern ptn = Pattern.compile("(\\w+)\\s*=\\s*[\"']([^\"]+?)[\"']");
	private static Map<String, String> getAttrs(String content) {
		final HashMap<String, String> ret = new HashMap<String, String>();
		RegexUtil.each(ptn, content, new P1<Matcher>() {public void e(Matcher m) {
			ret.put(m.group(1), m.group(2));
		}});
		return ret;
	}

	public static class Tag {
		public Range range;
		public String innerHtml;
		public Map<String, String> attrs;
		public Tag(String innerHtml, Map<String, String> attrs,Range range) {
			this.innerHtml = innerHtml;
			this.attrs = attrs;
			this.range = range;
		}
	}

	public static String normalize(String xml) {
		return xml
				.replaceAll("<\\w+[^>]*>", "")
				.replaceAll("</\\w+>", "")
				;
	}
}
