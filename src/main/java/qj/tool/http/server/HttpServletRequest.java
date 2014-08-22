package qj.tool.http.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import qj.ui.DesktopUI4;
import qj.util.HttpUtil;
import qj.util.HttpUtil.HttpMessage;

public interface HttpServletRequest {

	public String getMethod();

	String getQueryString();

	public String getRequestURI();


	class Helper {
		public static HttpServletRequest toRequest(final InputStream inputStream) {
			final HttpMessage httpMessage = HttpUtil.readHttpMessage(inputStream);
			if (httpMessage==null) {
				return null;
			}
			
//			System.out.println(httpMessage);
			
			return new HttpServletRequest() {

				@Override
				public String getMethod() {
					return httpMessage.getMethod();
				}

				@Override
				public String getRequestURI() {
					return httpMessage.getSpec().replaceFirst("^https?://[^/]+", "").replaceFirst("\\?.+$", "");
				}

				@Override
				public String getParameter(String name) {
					String queryString = getQueryString();
					if (queryString!=null) {
						String queryParameter = HttpUtil.getQueryParameter(name, queryString);
						if (queryParameter!=null) {
							return queryParameter;
						}
					}
					return HttpUtil.parseHttpPostForm(httpMessage.getContent(), "UTF-8").get(name);
				}

				@Override
				public String getQueryString() {
					String spec = httpMessage.getSpec();
					if (spec.contains("?")) {
						return spec.replaceFirst("^.+?\\?", "");
					} else {
						return null;
					}
				}

				@Override
				public InputStream getInputStream() {
					return new ByteArrayInputStream(httpMessage.getContent());
				}

				@Override
				public String getHeader(String key) {
					return httpMessage.getHeader(key);
				}
				
			};
		}
	}


	public String getParameter(String string);

	public InputStream getInputStream();

	public String getHeader(String string);
}
