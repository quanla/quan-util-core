package qj.tool.http.server;

import java.io.IOException;

import qj.util.FileUtil;
import qj.util.IOUtil;

public class ResourceServlet extends HttpServlet {

	private String location;

	public ResourceServlet(String location) {
		this.location = location;
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String requestURI = req.getRequestURI();
		IOUtil.connect(FileUtil.fileInputStream(location + requestURI), resp.getOutputStream());
	}
	
}
