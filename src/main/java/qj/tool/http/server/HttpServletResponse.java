package qj.tool.http.server;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import qj.tool.http.HttpCodes;
import qj.util.HttpUtil;
import qj.util.IOUtil;
import qj.util.funct.Fs;
import qj.util.funct.P0;

public interface HttpServletResponse {
	void finish();
	
	void setHeader(String key, String value);

	Writer getWriter();
	
	OutputStream getOutputStream();
	
	class Helper {
		public static HttpServletResponse getResponse(final OutputStream outputStream) {
			return new HttpServletResponse() {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				int status = 200;
				Map<String,String> headers = new LinkedHashMap<String,String>();
				{
					headers.put("Content-Type", "text/html; charset=UTF-8");
				}
				
				LinkedList<P0> beforeFinish = new LinkedList<P0>();
				
				@Override
				public void finish() {
					Fs.invokeAll(beforeFinish);
					HttpUtil.HttpMessage httpMessage = new HttpUtil.HttpMessage(out.toByteArray(), 
							"HTTP/1.1 " + status + " " + HttpCodes.codes.get(status));
					httpMessage.setHeaders(headers);
					httpMessage.send(outputStream);
					IOUtil.flush(outputStream);
				}

				@Override
				public Writer getWriter() {
					OutputStreamWriter writer = new OutputStreamWriter(out, Charset.forName("UTF-8"));
					beforeFinish.add(IOUtil.flushF(writer));
					return writer;
				}

				@Override
				public void sendError(int status) {
					this.status = status;
				}

				@Override
				public OutputStream getOutputStream() {
					return out;
				}

				@Override
				public void setHeader(String key, String value) {
					headers.put(key, value);
				}
			};
		}
	}

	void sendError(int i);

}
