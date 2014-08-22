package qj.tool.http.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class HttpServlet {

	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String method = req.getMethod();
		if ("POST".equals(method)) {
			doPost(req, resp);
		} else if ("GET".equals(method)) {
			doGet(req, resp);
		}
	}
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

	}
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

	}
	public Map<String,String> doConnect(HttpServletRequest req, Socket clientSk) throws IOException {
		return null;
	}
}
