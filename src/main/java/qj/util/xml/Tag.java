package qj.util.xml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import qj.util.RegexUtil4;
import qj.util.StringUtil4;
import qj.util.XmlUtil4;
import qj.util.math.Range;

/**
 * Represent a tag like: open tag or end tag, not with full content
 * @author Le Anh Quan
 *
 */
public class Tag {
	private StringBuffer content;

	public Tag(String content) {
		super();
		setContent(content);
	}

	public String getContent() {
		return content.toString();
	}

	public void setContent(String content) {
		this.content = new StringBuffer(content);
	}
	
	public String getName() {
		return StringUtil4.getFirstWord(content);
	}
	
	private static final Pattern PTN_TAGEND = Pattern.compile("(\\s*)/?>");
	public void setAttr(String attrName, String value) {
		Range tagAttrRange = XmlUtil4.getTagAttrRange(attrName, content, 0);
		if (tagAttrRange!=null) {
			// Replace current attr
			String tagAttrContent = content.substring(tagAttrRange.getFrom(), tagAttrRange.getTo());
			Matcher matcher = Pattern.compile(attrName + "\\s*=\\s*(\"[^\"]*\")", Pattern.CASE_INSENSITIVE).matcher(tagAttrContent);
			matcher.matches();
			String newAttrContent = RegexUtil4.replaceGroup(1, XmlUtil4.toAttrValue(value), matcher);
			content.replace(tagAttrRange.getFrom(), tagAttrRange.getTo(), newAttrContent);
		} else {
			// Add to the end
			Matcher matcher = PTN_TAGEND.matcher(content);
			if (!matcher.find())
				throw new RuntimeException("Invalid tag - no end");
			String spaceEnd = matcher.group(1);
			String newAttrContent = spaceEnd;
			if (spaceEnd == null || spaceEnd.length() == 0)
				newAttrContent = " ";
			
			newAttrContent += attrName + "=" + XmlUtil4.toAttrValue(value);
			content.replace(matcher.start(), matcher.end(), RegexUtil4.replaceGroup(1, newAttrContent, matcher));
		}
	}

	public String getAttr(String name) {
		Pattern ptn = XmlUtil4.getTagAttrPattern(name);
		Matcher m = ptn.matcher(content);
		if (m.find()) {
			return m.group(1);
		} else {
			return null;
		}
	}

	public String getInnerHtml() {
		int pos1 = content.indexOf(">");
		int pos2 = content.indexOf("</" + getName(), pos1);
		if (pos2 == -1)
			return null;
		else {
			return content.substring(pos1 + 1, pos2);
		}
	}
	
	public static void main(String[] args) {
		Tag tag = new Tag("<messageTextInput model=\"${bindings.FullName2}\" prompt=\"${cedarTxt.pdrDetailsLabelLineManager2}\" readOnly=\"true\">");
		tag.setAttr("model1", "h e he aewrwea waer aw");
		System.out.println(tag.getContent());
	}
}
