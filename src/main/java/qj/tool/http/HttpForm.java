package qj.tool.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import qj.util.StringUtil4;
import qj.util.funct.Douce;

/**
 * Created by QuanLA
 * Date: Mar 16, 2006
 * Time: 10:14:12 AM
 */
public class HttpForm {
	private String action;
    public List<Douce<String, String>> keyValues = new LinkedList<Douce<String,String>>();
    private static final Pattern ptnKeyValue = Pattern.compile("(\\w+)=([^&]*)");
    
    public HttpForm() {
    	
    }
    
    public HttpForm(byte[] data) {
        this(data, null);
    }
    public HttpForm(byte[] data, String encode) {
    	if (encode==null)
    		encode = "UTF-8";
    	
        String strData = new String(data);
        
        construct(strData, encode);
    }

    public HttpForm(String data, String encode) {
    	construct(data, encode);
    }
    
    private void construct(String data, String encode) {
		Matcher matcher = ptnKeyValue.matcher(data);
        while (matcher.find()) {
            try {
				keyValues.add(new Douce<String, String>(matcher.group(1), URLDecoder.decode(matcher.group(2), encode)) );
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    	
    }
    public String getValue(String key) {
    	for (Douce<String, String> entry : keyValues) {
			if (entry.get1().equals(key)) {
				return entry.get2();
			}
		}
    	return null;
    }
    
    public void setValue(String key, String val) {
    	keyValues.add(new Douce<String, String>(key, val));
    }
    
    public String getContent() {
    	StringBuffer sb = new StringBuffer();

    	for (Douce<String, String> entry : keyValues) {
    		String key = entry.get1();
			String val = entry.get2();

			if (val==null) {
				continue;
			}
			
			if (sb.length() > 0)
				sb.append("&");
			
			sb.append(key.replaceAll("\\$", "%24")).append("=").append(HttpUtil4.formatPostValue(val));
		}
//    	for (Iterator iter = keyValues.keySet().iterator(); iter.hasNext();) {
//			String key = (String) iter.next();
//			String val = (String) keyValues.get(key);
//			
//			if (val==null) {
//				continue;
//			}
//			
//			if (sb.length() > 0)
//				sb.append("&");
//			
//			sb.append(key.replaceAll("\\$", "%24")).append("=").append(HttpUtil4.formatPostValue(val));
//		}

		return sb.toString();
    }
    
    public static HttpForm getForm(int index, String html) {
    	int posBegin;
    	int posEnd;
    	int found = 0;
    	int ind = 0;

    	while (true) {
	    	posBegin = html.indexOf("<form", ind);
	    	if (posBegin == -1) return null;
			posEnd = StringUtil4.findNext("</form>", "<form", html, posBegin + 5) + 7;
	    	if (posEnd == -1)   return null;

	    	ind = posEnd + 7;
			found++;
			
			if (found == index + 1)
				return getForm(html.substring(posBegin + 5, posEnd));
    	}
    }
    
    // TODO not yet covered textarea tag
    private static final Pattern PTN_ACTION = Pattern.compile("action\\s*=\\s*[\"']([^\"']*)[\"']");
    private static final Pattern PTN_NAME = Pattern.compile("name\\s*=\\s*[\"']([^\"']*)[\"']");
    private static final Pattern PTN_VALUE = Pattern.compile("value\\s*=\\s*[\"']([^\"']*)[\"']");
	private static HttpForm getForm(String formHtml) {
		formHtml = StringUtil4.wipeWhiteInside(formHtml, "<!--", "-->");
		HttpForm form = new HttpForm();
		// Get action
		int endTag = formHtml.indexOf('>');
		String formTag = formHtml.substring(0, endTag);
		Matcher m = PTN_ACTION.matcher(formTag);
		if (m.find()) {
			form.action = m.group(1);
		}
		
		
		int index = 0;
		while (true) {
			int posBegin = formHtml.indexOf("<input", index);
			if (posBegin==-1) break;
			int posEnd = formHtml.indexOf('>', posBegin + 6);
			if (posEnd==-1) break;
			
			index = posEnd + 1;
			
			String tag = formHtml.substring(posBegin + 6, posEnd);
			Matcher matcher = PTN_NAME.matcher(tag);
			if (!matcher.find())
				continue;
			String attrname = matcher.group(1);
			matcher = PTN_VALUE.matcher(tag);
			String attrvalue;
			if (matcher.find())
				attrvalue = matcher.group(1);
			else
				attrvalue = "";
			
			form.keyValues.add(new Douce<String, String>(attrname, attrvalue));
		}
		return form;
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}

