package qj.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;


import qj.tool.http.HttpUtil4;
import qj.util.HttpUtil.HttpMessage;
import qj.util.funct.Fs;
import qj.util.funct.P1;
import qj.util.funct.P2;

public class HttpUtil extends HttpUtil4 {
	public static String formatQueryValue(String val) {
		return StringUtil.encodePercent(val);
	}
	
	public static HttpMessage readHttpHeader(InputStream in) {
		try {
			HttpMessage ret = new HttpMessage();
			
			for (;;) {
				String line = IOUtil4.readUntil(in, '\r');
				if (line==null) {
//					throw new EOFException();
					return null;
				}
				if (line.length() <= 0) {
					break;
				}
				in.read(); // \n
				ret.lines.add(line);
			}
			in.read(); // \n
			
			if (ret.lines.size()==0) {
				return null;
			}
			
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static class EOFException extends RuntimeException {
		
	}
	
	public static class HttpMessage implements Serializable {
		private static final Long serialVersionUID = 1L;
		public static final HttpMessage _404 = new HttpMessage(
				null,
				"HTTP/1.0 404 Not Found"
		);
		public static final HttpMessage _200 = new HttpMessage(
				null,
				"HTTP/1.0 200 OK" 
		);
		public List<String> lines = new ArrayList<String>();
		byte[] content;
		
		public HttpMessage() {
		}

		public HttpMessage(List<String> lines, byte[] content) {
			this.lines = lines;
			this.content = content;
		}
		public HttpMessage(byte[] content, String... lines) {
			this.lines = new LinkedList<String>(Arrays.asList(lines));
			this.content = content;
		}
		public HttpMessage(byte[] content, List<String> lines) {
			this.lines = lines;
			this.content = content;
		}
		
		public byte[] getContent() {
			return content;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (String line : lines) {
				sb.append(line).append("\r\n");
			}
			sb.append("\r\n");
			if (content!=null) {
				sb.append(new String(content));
			}
			return sb.toString();
		}

		static byte[] lineBreak = new byte[] {'\r', '\n'};
		public byte[] getBytes() {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			send(bout, content);
			return bout.toByteArray();
		}

		public void send(OutputStream out) {
			send(out, content);
		}

		public void send(OutputStream out, byte[] content) {
			try {
				for (String line : lines) {
//					System.out.println("Send: " + line);
					out.write(line.getBytes());
					out.write(lineBreak);
				}
				contentLength(out, content);
				
				out.write(lineBreak);
				if (content != null) {
					out.write(content);
//					System.out.println(content.length);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private void contentLength(OutputStream bout, byte[] content) throws IOException {
			if (getHeader("Content-Length") == null) {
				bout.write(contentLength(content).getBytes());
				bout.write(lineBreak);
			}
		}

		private void contentLength(OutputStream bout, long length) throws IOException {
			if (getHeader("Content-Length") == null) {
				bout.write(("Content-Length: " + length).getBytes());
				bout.write(lineBreak);
			}
		}

		private static String contentLength(byte[] content) {
//			System.out.println("Content-Length: " + (content == null ? 0 : content.length));
			return "Content-Length: " + (content == null ? 0 : content.length);
		}

		public String getHeader(String key) {
			if (key==null) {
				return lines.get(0);
			}
			String lowerCaseKey = key.toLowerCase();
			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);
				if (line.length() > key.length() + 2 &&
						line.substring(0, key.length() + 2).toLowerCase().equals(lowerCaseKey + ": ")) {
					return line.substring(key.length() + 2);
				}
			}
			return null;
		}
		

	    public HttpURLConnection makeConnection() {
	    	return makeConnection(null);
	    }
	    public HttpURLConnection makeConnection(Proxy proxy) {
	        try {
				HttpURLConnection conn;
//				System.out.println(getMethod());
//				System.out.println(getSpec());
				URL url = new URL(getSpec());
//				System.out.println(url);
				if (proxy!=null) {
					conn = (HttpURLConnection) url.openConnection(proxy);
				} else {
					conn = (HttpURLConnection) url.openConnection();
				}
				for (int i = 1; i < lines.size(); i++) {
					String line = lines.get(i);
						
					if (!line.startsWith("Host:")
							&& !line.startsWith("Proxy-Connection:")) {
						int pos = line.indexOf(":");
						conn.setRequestProperty(line.substring(0, pos), line.substring(pos + 1).trim());
					}
//				conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; Alexa Toolbar)");
				}
				
				conn.setRequestMethod(getMethod());
				if (content!=null && content.length > 0) {
				    conn.setDoOutput(true);
				    conn.getOutputStream().write(content);
				}
				
				// CONNECT
				conn.connect();

				return conn;
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			} catch (ProtocolException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
	    }

		private String method;
	    private String spec;
		public String getSpec() {
			if (spec==null) {
				extractMethodNSpec();
			}
			return spec;
		}
		public String getMethod() {
			if (method==null) {
				extractMethodNSpec();
			}
			return method;
		}
		
		void extractMethodNSpec() {
	        try {
				StringTokenizer to = new StringTokenizer(getHeader(null), " ");
				method = to.nextToken();
				spec = to.nextToken();
			} catch (Exception e) {
			}
		}

		private String queryString = null;
		private String baseUrl = null;
		public String getBaseUrl() {
			if (baseUrl != null) {
				return baseUrl;
			}
			int pos = getSpec().indexOf("?");
			if (pos > -1 ) {
				baseUrl = getSpec().substring(0, pos);
				queryString = getSpec().substring(pos + 1);
				return baseUrl;
			} else {
				return getSpec();
			}
		}
		public String getQueryString() {
			if (baseUrl == null) {
				getBaseUrl();
			}
			return queryString;
		}
		
		public String getParameter(String key) {
			if (getQueryString() ==null) {
				return null;
			}
			int pos = queryString.indexOf(key + "=");
			if (pos > -1) {
				int pos1 = pos + key.length()+ 1;
				int pos2 = queryString.indexOf("&", pos1);
				if (pos2 == -1) {
					return queryString.substring(pos1);
				} else {
					return queryString.substring(pos1, pos2);
				}
			} else {
				return null;
			}
		}

		public void send(OutputStream outputStream, String content) {
			send(outputStream, content.getBytes());
		}

		public void send(OutputStream out, File file) {
			try {
				for (String line : lines) {
					out.write(line.getBytes());
					out.write(lineBreak);
				}
				contentLength(out, file.length());
				
				out.write(lineBreak);
				if (file != null) {
					IOUtil.dump(new FileInputStream(file), out);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void replaceAll(String from, String to) {
			LinkedList<String> newLines = new LinkedList<String>();
			
			for (String line : lines) {
				newLines.add(line.replaceAll(from, to));
			}
			lines = newLines;
		}

		public static HttpMessage get(String spec) {
			return new HttpMessage(null, "GET " + spec + " HTTP/1.1");
		}
		public static HttpMessage post(String spec) {
			return new HttpMessage(null, "POST " + spec + " HTTP/1.1");
		}

		public static HttpMessage request(String method, String spec) {
			return new HttpMessage(null, method + " " + spec + " HTTP/1.1");
		}

		public int getResponseStatus() {
			return Integer.parseInt(getHeader(null).split(" ")[1]);
		}

		public HttpMessage setHeader(String key, String value) {
			lines.add(key + ": " + value);
			return this;
		}

		public HttpMessage setContent(byte[] content) {
			this.content = content;
			return this;
		}

		public void setHeaders(Map<String, String> headers) {
			for (Entry<String, String> entry : headers.entrySet()) {
				setHeader(entry.getKey(), entry.getValue());
			}
		}
	}

	public static HttpMessage parse(HttpURLConnection conn) {
		try {
			Map<String, List<String>> headerFields = conn.getHeaderFields();
			HttpMessage ret = new HttpMessage();
			boolean firstLineMet = false;
			for (Entry<String, List<String>> entry : headerFields.entrySet()) {
				for (String value : entry.getValue()) {
					String key = entry.getKey();
					if (key != null) {
						ret.lines.add(key + ": " + value);
					} else {
						ret.lines.add(0, value);
						firstLineMet = true;
					}
				}
			}
			
			if (!firstLineMet) {
				ret.lines.add(0, "HTTP/1.1 200 OK");
			}
			
//			if (conn.getDoInput()) {
			ret.content = IOUtil.readData(conn.getInputStream());
//			System.out.println(new String(ret.content));
//			}
			
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static HttpMessage readHttpMessage(InputStream in) {
		HttpMessage ret = readHttpHeader(in);
		if (ret==null) {
			return null;
		}
		String lengthStr = ret.getHeader("Content-Length");
		if (lengthStr!=null) {
			ret.content = IOUtil.readEnough(Integer.parseInt(lengthStr), in);
		}
		return ret;
	}
	
	public static byte[] getContent(HttpMessage header, InputStream in, final P2<Long,Long> progressF) {
		String lengthStr = header.getHeader("Content-Length");
		final long total = Long.parseLong(lengthStr);
//		return IOUtil.readEnough(total, in, progressF== null ? null : Fs.p1(progressF, total));
		return IOUtil.readEnough(total, in, progressF== null ? null : new P1<Long>() {public void e(Long pr) {
			progressF.e(pr, total);
		}});
	}

	public static HttpMessage parseHttpMessage(String string) {
		ByteArrayInputStream in = new ByteArrayInputStream(string.replaceAll("\r?\n", "\r\n").getBytes());
		HttpMessage ret = readHttpHeader(in);
		if (ret==null) {
			return null;
		}
		ret.content = IOUtil.readData(in);
		return ret;
	}


	public static HashMap<String, byte[]> getMultipart(String boundary, InputStream in) {
		final HashMap<String, byte[]> ret = new HashMap<String, byte[]>();
		serveMultiPart(boundary, in, new P2<String, byte[]>() {public void e(String name, byte[] bytes) {
			ret.put(name, bytes);
		}});
		return ret;
	}

	private static void serveMultiPart(String boundary, InputStream in,
			P2<String, byte[]> p2) {
		IOUtil.skip(in, boundary.length() + 4);
		while (true) {
			
			HttpMessage header = HttpUtil.readHttpHeader(in);
			if (header==null) {
//				System.out.println("header==null");
				return;
			}
			String contentDisposition = header.getHeader("Content-Disposition");
			String name = RegexUtil.getString("name=\"(.+?)\"(?:$|;)", 1, contentDisposition);
			
			String endBoundary = "\r\n--" + boundary + "\r\n";
			byte[] bytes = IOUtil.readUntil(in, endBoundary, "\r\n--" + boundary + "--");
			p2.e(name,Arrays.copyOf(bytes, bytes.length - endBoundary.length()));
		}
	}

	public static FileUploadHandler createFileUploadHandler(final Object[][] params) {
		return new FileUploadHandler() {
			public void serve(String contentType, InputStream in) {
				String boundary = RegexUtil.getString("boundary=(.+?)(?:$|;)", 1, contentType);
				
				serveMultiPart(boundary, in, new P2<String,byte[]>() {public void e(String name, byte[] b) {
					P1<byte[]> handler = getHandler(name, params);
					handler.e(b);
				}});
			}

			private P1<byte[]> getHandler(String name, Object[][] params) {
				for (Object[] param : params) {
					if (param[0].equals(name)) {
						return (P1<byte[]>) param[1];
					}
				}
				return null;
			}
		};
	}
	
	public static interface FileUploadHandler {

		void serve(String header, InputStream inputStream);
		
	}

	@SuppressWarnings("rawtypes")
	public static String formatPostForm(Map map) {
		StringBuilder sb = new StringBuilder();
		for (Object entryO : map.entrySet()) {
			Entry entry = (Entry) entryO;
			if (sb.length() > 0) {
				sb.append("&");
			}
			Object value = entry.getValue();
			sb.append(entry.getKey() + "=" + (value==null ? "" : formatPostValue(String.valueOf(value))));
		}
		return sb.toString();
	}

	public static byte[] formatMultiPartForm(String boundary, Map<Object, Object> values) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			for (Entry<Object, Object> entry : values.entrySet()) {
				Object value = entry.getValue();
				if (value == null) {
					continue;
				}
				out.write(("--" + boundary + "\r\n").getBytes());
				out.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n").getBytes());
				out.write(("\r\n").getBytes());
				if (value instanceof byte[]) {
					out.write((byte[])value);
				} else {
					out.write(value.toString().getBytes());
				}
				out.write(("\r\n").getBytes());
			}
			out.write(("--" + boundary + "--").getBytes());
			return out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Map<String,String> parseHttpPostForm(byte[] content) {
		return parseHttpPostForm(content, null);
	}
	
	public static Map<String,String> parseHttpPostForm(byte[] content, String charset) {
		String string = 
				content == null ? null :
				charset == null ? new String(content) : 
				new String(StringUtil4.decodePercentToBytes(new String(content)), Charset.forName(charset));
		
		return parseHttpPostForm(string, charset==null);
	}

	public static Map<String, String> parseHttpPostForm(String string, boolean needDecode) {
		if (string == null) {
			return Collections.emptyMap();
		}
		HashMap<String, String> ret = new HashMap<String, String>();
		for (String line : string.split("&")) {
			if (StringUtil.isBlank(line)) {
				continue;
			}
			int indexOf = line.indexOf("=");
			ret.put(line.substring(0, indexOf), 
					(needDecode ? StringUtil4.decodePercent( line.substring(indexOf + 1) ) : line.substring(indexOf + 1))
					.replaceAll("\\+", " ")
					); 
		}
		return ret;
	}

	public static String getDomain(String url) {
		return RegexUtil.getString("^https?://([^/]+)", 1, url);
	}
}
