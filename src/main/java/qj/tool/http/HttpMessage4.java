package qj.tool.http;

// TODO key value in HttpMessage

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Created by QuanLA
 * Date: Mar 13, 2006
 * Time: 2:03:34 PM
 */
public class HttpMessage4 implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String method;
    private String spec;
    private byte[] content;
    private Properties headers = new Properties();
	private ArrayList headerKeys = new ArrayList();
    
    private String firstLine;

    public String getSpec() {
        return spec;
    }
    public Properties getHeaders() {
        return headers;
    }

    public void setHeaders(Properties properties) {
        this.headers = properties;
    }
    
    public String getMethod() {
    	return method;
    }

    public String getFirstLine() {
        return firstLine;
    }

    public void setFirstLine(String firstLine) {
        this.firstLine = firstLine;
        try {
			StringTokenizer to = new StringTokenizer(firstLine, " ");
			method = to.nextToken();
			spec = to.nextToken();
		} catch (Exception e) {
		}
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String toString() {
        ByteArrayOutputStream o = null;
        try {
            o = new ByteArrayOutputStream(1024);
            o.write(getHeader(true).getBytes());
            o.write(getContent());
            return o.toString();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return o.toString();
        }
    }

    public String getHeader(boolean withProxyHeader) {
        StringBuffer sb = new StringBuffer();

        sb.append(getFirstLine()
          .replaceAll(" http://[^/]+(:\\d+)?", " ")
        ).append("\r\n");

        for (int i = 0; i < headerKeys.size(); i++) {
			String key = (String) headerKeys.get(i);
            String value = headers.getProperty(key);
            if (withProxyHeader
                    || (
                        !key.equalsIgnoreCase("HOST")
//                        && !key.equalsIgnoreCase("PRAGMA")
                        && !key.equalsIgnoreCase("PROXY-CONNECTION")
                    )) {
                sb.append(key).append(": ").append(value).append("\r\n");
            }
		}
        sb.append("\r\n");
//        System.out.println(sb);
        return sb.toString();
    }

    /**
     *
     * @param proxy 
     * @return
     * @throws IOException
     * @throws ProtocolException
     */
    public HttpURLConnection makeConnection() throws IOException, ProtocolException {
    	return makeConnection(null);
    }
    public HttpURLConnection makeConnection(Proxy proxy) throws IOException, ProtocolException {
    	String _spec = spec;
        HttpURLConnection conn;
		if (proxy!=null) {
			conn = (HttpURLConnection) new URL(_spec).openConnection(proxy);
		} else {
			conn = (HttpURLConnection) new URL(_spec).openConnection();
		}
		for (int i = 0; i < headerKeys.size(); i++) {
			String key = (String) headerKeys.get(i);
				
			if (!key.equals("Host")
					&& !key.equals("Proxy-Connection")) {
				conn.setRequestProperty(key, (String) headers.get(key));
			}
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; Alexa Toolbar)");
		}
        
        conn.setRequestMethod(getMethod());
        if (getContent()!=null && getContent().length > 0) {
	        conn.setDoOutput(true);
	        conn.getOutputStream().write(getContent());
        }
        
        // CONNECT
        conn.connect();

        return conn;
    }

    public HttpForm getForm() {
        return new HttpForm(content);
    }    
    public HttpForm getForm(String encode) {
        return new HttpForm(content, encode);
    }

    /**
     *
     * @return query string
     */
    public String getQueryString() {
        int qMarkPos = spec.indexOf('?');
        if (qMarkPos>-1) {
            return spec.substring(qMarkPos + 1);
        } else
            return null;
    }
    
    /**
	 * 
	 * @return query string
	 */
	public String getRequestURL() {
		int qMarkPos = spec.indexOf('?');
		if (qMarkPos > -1) {
			return spec.substring(0, qMarkPos);
		} else
			return spec;
	}
	public void putHeader(String key, String value) {
		headers.put(key, value);
		headerKeys.add(key);
	}
	public String getHeader(String header) {
		return headers.getProperty(header);
	}
}
