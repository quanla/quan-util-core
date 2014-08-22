package qj.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import qj.util.math.Range;
//import qj.util.string.Range;
import qj.util.xml.Tag;

@Deprecated
public class XmlUtil4 {

	public static String document2XmlString(Document xmldoc) {
        try {
			Source src = new DOMSource(xmldoc);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();

			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(); // identity transformer
			transformer.transform(src, new StreamResult(bout));
			return bout.toString("UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

	public static Document xmlString2Document(String xml) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException{
        byte[] byteArray = xml.getBytes("UTF-8");
        ByteArrayInputStream baos = new ByteArrayInputStream(byteArray);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = factory.newDocumentBuilder().parse(baos);
        return doc;
    }
	
	/**
	 * 
	 * @param tagName
	 * @param content
	 * @param fromIndex
	 * @return
	 */
	public static int findTagBegin(String tagName, CharSequence content, int fromIndex) {
		Pattern PTN_TAG_BEGIN = Pattern.compile("<" + tagName + "[\\s>]");
		Matcher matcher = PTN_TAG_BEGIN.matcher(content);
		if (matcher.find(fromIndex)) {
			return matcher.start();
		} else {
			return -1;
		}
	}
	/**
	 * 
	 * @param tagName
	 * @param content
	 * @param fromIndex
	 * @return
	 */
	public static Range findBeginTag(String tagName, CharSequence content, int fromIndex) {
		int begin = findTagBegin(tagName, content, fromIndex);
		
		if (begin == -1)
			return null;
		
		int end = StringUtil4.findNext('>', '<', content, begin + 1);
		if (end > -1)
			return new Range(begin, ++end);
		else
			return null;
	}

	/**
	 * Get the whole tag node from tag begin to tag end
	 * @param tagName
	 * @param content
	 * @param fromIndex
	 * @return
	 */
	public static Range getTagNodeRange(String tagName, CharSequence content, int fromIndex) {

		int tagBegin;
		Range beginTag = findBeginTag(tagName, content, fromIndex);
		
		if (beginTag==null) {
			return null;
		}
		
		tagBegin = beginTag.getFrom();
		if (content.charAt(beginTag.getTo() - 2) == '/') {
			return beginTag;
		} else {
			int tagEnd = StringUtil4.indexOf("</" + tagName + ">", beginTag.getTo(), content);
			
			if (tagEnd == -1) {
				return beginTag; // Can not find end tag
			} else {
				return new Range(tagBegin, tagEnd + tagName.length() + 3);
			}
		}
		
//		// TODO need tweeking using findTagBegin method
//		Pattern PTN_TAG_BEGIN = Pattern.compile("<" + tagName + "[\\s>]");
//		Matcher matcher = PTN_TAG_BEGIN.matcher(content);
//		if (matcher.find(fromIndex)) {
//			tagBegin = matcher.start();
//			
//			// Finding tag end
//			int tagEnd = fromIndex;
//			while (true) {
//				tagEnd = StringUtil.indexOf("</" + tagName + ">", tagEnd, content);
//			
//				if (tagEnd > -1 && matcher.find() && matcher.start() < tagEnd) {
//					// Redo finding tag end
//				} else
//					break;
//			}
//			
//			if (tagEnd == -1)
//				throw new RuntimeException("Can not find end tag of tag " + tagName + " at " + tagBegin);
//			
//			return new Range(tagBegin, tagEnd + tagName.length() + 3);
//			
//		} else {
//			return null;
//		}
	}

	
	public static Range findMarkedRange(String markup, CharSequence content, int fromIndex) {
		String start = "<!-- " + markup + "::BEGIN -->";
		String end = "<!-- " + markup + "::END -->";
		int pos1 = StringUtil4.indexOf(start, fromIndex, content);
		if (pos1==-1)
			return null;

		int pos2 = StringUtil4.indexOf(end, pos1 + start.length(), content);
		if (pos1==-1)
			return null;
		
		return new Range(pos1, pos2 + end.length());
	}
	
	private static final String PTN_TAGATTR = "\\s*=\\s*\"([^\"]*)\"";
	/**
	 * 
	 * @param tagAttrName
	 * @param content
	 * @param fromIndex
	 * @return
	 */
	public static Range getTagAttrRange(String tagAttrName, CharSequence content, int fromIndex) {
		Matcher matcher = getTagAttrPattern(tagAttrName).matcher(content);
		
		if (matcher.find(fromIndex)) {
			return new Range(matcher.start(), matcher.end());
		} else
			return null;
	}
	
	
	public static Pattern getTagAttrPattern(String tagAttrName) {
		return Pattern.compile(tagAttrName + PTN_TAGATTR, Pattern.CASE_INSENSITIVE);
	}
	
	
	public static Range getAroundTagRange(int pos, String content) {
		if (pos < 0 || pos > content.length())
			return null;
		
		int start;
		int end;

		for (start = pos; start > -1; start--) {
			if (content.charAt(start) == '<')
				break;
		}
		
		for (end = pos; end < content.length(); end++) {
			if (content.charAt(end) == '>') {
				end++;
				break;
			}
		}
		
		return new Range(start, end);
	}

    public static String toAttrValue(String s) {
        return "\"" + (s == null ? "" : s.replaceAll("\"", "\\\\\"")) + "\"";
    }

	public static String formatContent(String content) {
		content = content.replaceAll("&([^#])", "&amp;$1");
		content = content.replaceAll("<", "&lt;");
		content = content.replaceAll(">", "&gt;");
		content = content.replaceAll("\"", "&quot;");
		content = content.replaceAll("'", "&#39;");
		return content;
	}

	public static Tag getTagNode(String tagName, String content, int index) {
		Range range = getTagNodeRange(tagName, content, index);
		
		if (range != null) {
			return new Tag(content.substring(range.getFrom(), range.getTo()));
		} else {
			return null;
		}
	}
}
