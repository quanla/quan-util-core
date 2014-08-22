package qj.tool.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import qj.util.Cols;
import qj.util.HttpUtil;
import qj.util.IOUtil;
import qj.util.RegexUtil;
import qj.util.StringUtil;
import qj.util.funct.Douce;
import qj.util.funct.F1;
import qj.util.funct.P1;

public class WebSession {
	public Integer readTimeout;
	public boolean followRedirect = true;
	
	public String getString(String url) {
		return getString(url, null);
	}

	static Pattern PTN_COOKIE = Pattern.compile("^([^=]+)=(.*);\\s*[Pp]ath=/");
	public String getString(String url, String charSet) {
		try {
			HttpURLConnection conn = conn(url);
			
			int responseCode = conn.getResponseCode();
			if (responseCode / 100 == 3) {
				throw new RedirectException(getHeaderField("Location", conn));
			} else if (responseCode / 100 != 2) {
				throw new ServerErrorException(responseCode, conn.getResponseMessage());
			}
			
			return toString(conn, charSet);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static class RedirectException extends RuntimeException {
		public RedirectException(String location) {
			super(location);
		}
	}
	public static class ServerErrorException extends RuntimeException {
		public int responseCode;

		public ServerErrorException(int responseCode, String message) {
			super(message);
			this.responseCode = responseCode;
		}
	}
	
	private static String getCharSet(HttpURLConnection conn) {
		String charSet = null;
		String contentType = getHeaderField("Content-Type", conn);
		if (contentType!=null) {
			String upCase = contentType.toUpperCase();
			int i = upCase.indexOf("CHARSET=");
			if (i > -1) {
				charSet = upCase.substring(i + "CHARSET=".length());
			}
		}
		return charSet;
	}
	
	public static String getHeaderField(String key, HttpURLConnection conn) {
		for (Entry<String, List<String>> entry : conn.getHeaderFields().entrySet()) {
			if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(key)) {
				if (Cols.isEmpty(entry.getValue())) {
					return null;
				} else {
					return entry.getValue().get(0);
				}
			}
		}
		return null;
	}
	
	public HttpURLConnection conn(String url) throws IOException {
		return conn(url, (byte[])null,null);
	}
	private URLConnection conn(String url,String content) throws IOException {
		return conn(url, content == null ? null : content.getBytes(),null);
	}

	private HttpURLConnection conn(String url,byte[] bytes,String contentType) throws IOException {
		return conn(url, bytes, contentType, null);
	}
	
	/**
	 * Open URL connection
	 * @param url
	 * @param bytes
	 * @param contentType
	 * @param p1
	 * @return
	 * @throws IOException
	 */
	public HttpURLConnection conn(String url,byte[] bytes,String contentType,P1<HttpURLConnection> p1) throws IOException {
		
		HttpURLConnection conn = (HttpURLConnection) new URL(formatUrl(url)).openConnection();
		conn.setInstanceFollowRedirects(followRedirect);
		setHeaders(conn, url);
		
		if (readTimeout!=null) {
			conn.setReadTimeout(readTimeout);
		}
		
		if (connDecor!=null) {
			connDecor.e(conn);
		}

		if (p1!=null) {
			p1.e(conn);
		}
		
		if (bytes!=null) {
			if (contentType!=null) {
				conn.setRequestProperty("Content-Type", contentType);
			}
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
//			conn.setRequestProperty("Content-Length", "" + bytes.length);
			conn.getOutputStream().write(bytes);
		}
		
//		System.out.println(HttpUtil.getResponseHeader(conn));
		
		checkCookie(conn);
		return conn;
	}
	private static String formatUrl(String url) {
		return RegexUtil.replaceAll(url, "[^a-zA-Z0-9_?.=/@:&-]", new F1<Matcher, String>() {public String e(Matcher m) {
			return StringUtil.encodePercent(m.group());
		}});
	}
	public static void main(String[] args) {
		System.out.println(formatUrl("https://ddtanktool.appspot.com/secure/AdminServlet?action=page&page=changeMessages"));
	}

	private void setHeaders(HttpURLConnection conn, String url) {
		HttpUtil.setIEConnProps(conn);
        conn.setRequestProperty("Host", HttpUtil.getDomain(url));
//		conn.setRequestProperty("User-Agent", "	Mozilla/5.0 (Windows NT 6.1; WOW64; rv:2.0) Gecko/20100101 Firefox/4.0");
		
		
		setCookie(conn);
	}
	
	private void setCookie(URLConnection conn) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> entry : cookies.entrySet()) {
			if (sb.length() > 0) {
				sb.append("; ");
			}
			sb.append(entry.getKey() + "=" + entry.getValue());
		}
		String cookie = sb.toString();
		if (StringUtil.isNotEmpty(cookie)) {
//			System.out.println("Setting Cookie: " + cookie);
			conn.setRequestProperty("Cookie", cookie);
		}
	}

	public Map<String,String> cookies = new HashMap<String, String>();
	public P1<HttpURLConnection> connDecor = null;
	private void checkCookie(URLConnection conn) {
		for (Entry<String, List<String>> entry : conn.getHeaderFields().entrySet()) {
//			System.out.println(entry);
			if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("set-cookie")) {
				List<String> list = entry.getValue();
				if (list==null) {
					return;
				}
				for (String string : list) {
					Matcher m = PTN_COOKIE.matcher(string);
					if (m.find()) {
//						System.out.println("Set [" + m.group(1) + "]=" + m.group(2));
						cookies.put(m.group(1), m.group(2));
					}
				}
				return;
			}
		}
	}

	public String post(String url, String post,P1<HttpURLConnection> p1) {
		return post(url, post == null ? null : post.getBytes(),"application/x-www-form-urlencoded",p1);
	}
	public String post(String url, String post) {
		return post(url, post == null ? null : post.getBytes(),"application/x-www-form-urlencoded");
	}
	public String post(String url, byte[] content,String contentType) {
		return post(url, content, contentType, null);
	}
	public String post(String url, byte[] content,String contentType,P1<HttpURLConnection> p1) {
		try {
			HttpURLConnection conn = conn(url, content,contentType,p1);
//			System.out.println("Response header: " + HttpUtil.getResponseHeader(conn));
			return toString(conn);
//			return IOUtil.inputStreamToString(conn.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public InputStream getStream(String url) {
		return getStream2(url).get1();
	}
	/**
	 * Stream and length
	 * @param url
	 * @return
	 */
	public Douce<InputStream, Long> getStream2(String url) {
		try {
			URLConnection conn = conn(url, (byte[])null,null);
			return new Douce<InputStream, Long>(conn.getInputStream(), (long)conn.getContentLength());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	public static String toString(HttpURLConnection conn) {
		return toString(conn, null);
	}

	public static String toString(HttpURLConnection conn, String charSet) {
		charSet = charSet == null ? getCharSet(conn) : charSet;
		try {
			InputStream in = getInputStream(conn);
			return IOUtil.inputStreamToString(in,charSet);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static InputStream getInputStream(HttpURLConnection conn)
			throws IOException {
		InputStream in = conn.getInputStream();
		String contentEncoding = getHeaderField("Content-Encoding",conn);
		if (contentEncoding!=null && contentEncoding.contains("gzip")) {
			in = new GZIPInputStream(in);
		}
		return in;
	}

}
