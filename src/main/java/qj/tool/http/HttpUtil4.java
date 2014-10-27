package qj.tool.http;

import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import qj.tool.string.StringTracker;
import qj.util.IOUtil4;
import qj.util.ImageUtil;
import qj.util.RegexUtil;
import qj.util.StringUtil4;

/**
 * Created by QuanLA
 * Date: Mar 13, 2006
 * Time: 2:03:18 PM
 */
public class HttpUtil4 {
    private static final Pattern ptnFindHTTPproperty = Pattern.compile("(.+): (.+)$", Pattern.MULTILINE);
    
    public static String addQueryParam(String query, String param) {
        if (query.indexOf("?") > -1) {
            return query + "&" + param;
        } else {
            return query + "?" + param;
        }
    }

    public static String getHttpResponse(String url) throws IOException {
		HttpURLConnection urlConn = HttpUtil4.makeIEGetConn(url);
		
		return getResponseHeader(urlConn) + "\r\n" + IOUtil4.inputStreamToString(urlConn.getInputStream());
    }
    
	private static final Pattern PTN = Pattern.compile("([^:]+)(:(\\d+))?");
    public static Proxy parseProxy(String strProxy) {
    	if (StringUtil4.isEmpty(strProxy)) {
    		return null;
    	}
    	
		Matcher m = PTN.matcher(strProxy);
		if (!m.matches())
			return null;
		
		int port = 80;
		String strPort = m.group(3);
		if (strPort!=null) {
			port = Integer.parseInt(strPort);
		}
		
		return new Proxy(Type.HTTP, new InetSocketAddress(m.group(1), port));
   	
    }
    
    public static HttpURLConnection makeIEGetConn(String url) throws IOException {
    	URL urlO = null;
		try {
			urlO = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    	HttpURLConnection conn = (HttpURLConnection) urlO.openConnection();
    	setIEConnProps(conn);
        return conn;
        
    }

    public static HttpURLConnection makeIEGetConn(String url, Proxy proxy) throws IOException {
    	URL urlO = null;
		try {
			urlO = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    	HttpURLConnection conn = (HttpURLConnection) (proxy!=null? urlO.openConnection(proxy) : urlO.openConnection());
    	setIEConnProps(conn);
        return conn;
        
    }


	public static void setIEConnProps(HttpURLConnection conn) {
//		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
        conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
        conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
        conn.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
        conn.setRequestProperty("Connection", "keep-alive");
	}

	public static String getHttpContent(String url) {
		return getHttpContent(url, null);
	}
	
//	static {
//		HttpURLConnection.setFollowRedirects(false);
//	}
	
	public static String getHttpContent(String url, String postContent) {
		
		return getHttpContent(url, postContent, null);
	}
	
	public static String getHttpContent(String url, String postContent, String[] headers) {
		try {
			HttpURLConnection urlConn = HttpUtil4.makeIEGetConn(url);

			for (int i = 0; headers != null && i < headers.length; i+=2) {
				urlConn.addRequestProperty(headers[i], headers[i + 1]);
			}
			
			if (postContent!=null) {
				urlConn.setDoOutput(true);
				IOUtil4.write(postContent, urlConn.getOutputStream());
			}
			
			return IOUtil4.inputStreamToString(urlConn.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param url
	 * @return
	 * @throws IOException 
	 */
    public static Image getImageContent(String url) {
		try {
			HttpURLConnection conn = HttpUtil4.makeIEGetConn(url);
			return ImageUtil.getImage(conn.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    }

    /**
     * Unsupport:
     *  images
     * @param in
     * @return HttpMessage
     */
    public static HttpMessage4 getHttpMessage(InputStream in) throws IOException {
        StringTracker TRACK_EMPTY_LINE = new StringTracker("\r\n\r\n");
        ByteArrayOutputStream bo = new ByteArrayOutputStream(1024);
        HttpMessage4 message = new HttpMessage4();
        int read;
        boolean readingHeader = true;
        int contentLength = 0;

        // Loop every character of the request
        while (true) {

            read = in.read();
            if (read==-1) {
                throw new IOException("Unexpected input end");
            }

            //----------

            if (readingHeader) {
                bo.write(read);
                
                if (TRACK_EMPTY_LINE.track((char)read)) {
                    readingHeader = false;

                    // Parse header from sb
                    String header = bo.toString();
                    bo.reset();
                    
                    readHttpHeader(message, header);

                    // Set content length
                    try {
                        contentLength = Integer.parseInt(message.getHeaders().getProperty("Content-Length"));
                    } catch (NumberFormatException e) {
                        contentLength = 0;
                    }
                    if (contentLength == 0) {
                        // End of content
                        message.setContent(new byte[0]);
                        break;
                    }
                }
            } else {
                // Read content
                if (contentLength > 0) {
                    bo.write(read);
                    contentLength--;
                }
                if (contentLength == 0) {
                    // End of content
                    message.setContent(bo.toByteArray());
                    break;
                }
            }

            //----------
        }

        return message;
    }

	public static void readHttpHeader(HttpMessage4 message, String header) {
		message.setFirstLine(header.substring(0, header.indexOf("\r\n")));
		Matcher matcher = ptnFindHTTPproperty.matcher(header);
		while (matcher.find()) {
		    message.putHeader(matcher.group(1), matcher.group(2));
		}
	}

    public static URLConnection createImgUrlConnection(String url) throws IOException {
      URLConnection conn = new URL(url).openConnection();
      conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
      conn.setRequestProperty("Accept", "image/png,*/*;q=0.5");
      conn.setRequestProperty("Accept-Language", "en-gb,en;q=0.5");
      conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
      conn.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
      return conn;
    }
    

	public static String getResponseHeader(HttpURLConnection conn) {
		String headerField = conn.getHeaderField(null);

		if (headerField==null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		
		sb.append(headerField).append("\r\n");
		String key;
		for (int i = 1; (key = conn.getHeaderFieldKey(i))!=null; i++) {
			if (
					!key.equals("Transfer-Encoding")
					)
			sb.append(key).append(": ").append(conn.getHeaderField(i)).append("\r\n");
		}
		
		return sb.toString();
	}
	
	public static String formatPostValue(String val) {
		return StringUtil4.encodePercent(val).replaceAll(" |%20", "+");
	}

	private static final Pattern PTN_ROOTURL = Pattern.compile("^\\w+://[^/]+");
	private static final Pattern PTN_HOMEURL = Pattern.compile("[^/]+$");
	public static String absoluteUrl(String oriUrl, String destUrl) {
		
		if (destUrl.startsWith("/")) {
			String root = getUrlRoot(oriUrl);
			if (root == null)
				root = "";
			
			return root + destUrl;
		} else {
			String home = oriUrl;
			if (PTN_ROOTURL.matcher(home).matches()) {
				home += "/";
			}
			
			if (StringUtil4.countHappens('/', home) == 3) {
				destUrl = destUrl.replaceAll("^\\.\\./", "");
			}

			home = PTN_HOMEURL.matcher(home).replaceAll("");

			return (home + destUrl).replaceAll("[^/]+/\\.\\./", "");
		}
	}

	public static String getUrlRoot(String url) {
		String root = null;
		Matcher m = PTN_ROOTURL.matcher(url);
		if (m.find())
			root = m.group();
		return root;
	}
	
	public static boolean isUrlAbsolute(String url) {
		return PTN_ROOTURL.matcher(url).find();
	}
	
	public static String getResponseHeader(HttpURLConnection conn, String modifiedKey, String newValue) {
		StringBuffer sb = new StringBuffer();
		sb.append(conn.getHeaderField(null)).append("\r\n");
		String key;
		String value;
		for (int i = 1; (key = conn.getHeaderFieldKey(i))!=null; i++) {
			if (key.equals("Transfer-Encoding"))
				continue;
			
			if (key.equals(modifiedKey))
				value = newValue;
			else
				value = conn.getHeaderField(i);
			sb.append(key).append(": ").append(value).append("\r\n");
		}
		
		return sb.toString();
	}

	public static String getHost(HttpMessage4 msg) {
		String header = msg.getHeader("Host");
		if (header!= null)
			return header;
		String url = msg.getRequestURL().replaceAll("^http://", "");
		return url.substring(0, url.indexOf("/"));
	}
	public static String getHost(String uri) {
		return RegexUtil.getString("^.+?://(.+?)[:/]", 1, uri);
	}

//	/**
//	 * Get root address of a http request
//	 * @param req
//	 * @return
//	 */
//	public static String getRoot(HttpServletRequest req) {
//		String host;
//		if (req.getLocalName()!=null)
//			host = req.getLocalName();
//		else
//			host = req.getLocalAddr();
//		
//		if (req.getLocalPort()==80)
//			return "http://" + host + req.getContextPath();
//		else
//			return "http://" + host + ":" + req.getLocalPort() + req.getContextPath();
//	}
	
//
//    public static PostMethod getPostMethod(int index, String html) {
//    	int posBegin;
//    	int posEnd;
//    	int found = 0;
//    	int ind = 0;
//
//    	while (true) {
//	    	posBegin = html.indexOf("<form", ind);
//	    	if (posBegin == -1) return null;
//			posEnd = StringUtil.findNext("</form>", "<form", html, posBegin + 5) + 7;
//	    	if (posEnd == -1)   return null;
//
//	    	ind = posEnd + 7;
//			found++;
//			
//			if (found == index + 1)
//				return getPostMethod(html.substring(posBegin + 5, posEnd));
//    	}
//    }
//    
//    // TODO not yet covered textarea tag
//    private static final Pattern PTN_ACTION = Pattern.compile("action\\s*=\\s*[\"']([^\"']*)[\"']");
//    private static final Pattern PTN_NAME = Pattern.compile("name\\s*=\\s*[\"']([^\"']*)[\"']");
//    private static final Pattern PTN_VALUE = Pattern.compile("value\\s*=\\s*[\"']([^\"']*)[\"']");
//	private static PostMethod getPostMethod(String formHtml) {
//		formHtml = StringUtil.wipeWhiteInside(formHtml, "<!--", "-->");
//		PostMethod method = new PostMethod();
//		// Get action
//		int endTag = formHtml.indexOf('>');
//		String formTag = formHtml.substring(0, endTag);
//		Matcher m = PTN_ACTION.matcher(formTag);
//		if (m.find()) {
//			try {
//				method.setURI(new URI(m.group(1), true));
//			} catch (URIException e) {
//				return null;
//			} catch (NullPointerException e) {
//				return null;
//			}
//		}
//		
//		
//		int index = 0;
//		while (true) {
//			int posBegin = formHtml.indexOf("<input", index);
//			if (posBegin==-1) break;
//			int posEnd = formHtml.indexOf('>', posBegin + 6);
//			if (posEnd==-1) break;
//			
//			index = posEnd + 1;
//			
//			String tag = formHtml.substring(posBegin + 6, posEnd);
//			Matcher matcher = PTN_NAME.matcher(tag);
//			if (!matcher.find())
//				continue;
//			String attrname = matcher.group(1);
//			matcher = PTN_VALUE.matcher(tag);
//			String attrvalue;
//			if (matcher.find())
//				attrvalue = matcher.group(1);
//			else
//				attrvalue = "";
//			
//			method.addParameter(attrname, attrvalue);
//		}
//		return method;
//	}

    public static void main(String[] args) {
        System.out.println(getQueryParameter("asyncId", "asyncId=0"));
    }

    /**
     * @param name
     * @param queryString
     * @return
     */
    public static String getQueryParameter(String name, String queryString) {
    	if (queryString==null) {
    		return null;
    	}
//        System.out.println("queryString=" + queryString);
        Pattern ptn = Pattern.compile("(?:^|[\\?&])" + name + "=([^&]*)");
        Matcher m = ptn.matcher(queryString);
        if (m.find()) {
            return StringUtil4.decodePercent(m.group(1)).replaceAll("\\+", " ");
        } else {
            return null;
        }
    }

}
