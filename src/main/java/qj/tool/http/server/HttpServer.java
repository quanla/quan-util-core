package qj.tool.http.server;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Map;

import qj.util.HttpUtil;
import qj.util.IOUtil;
import qj.util.LangUtil;
import qj.util.NetUtil;
import qj.util.RegexUtil;
import qj.util.ThreadUtil;
import qj.util.funct.F1;
import qj.util.funct.Fs;
import qj.util.funct.P0;
import qj.util.funct.P1;

public class HttpServer {
	LinkedList<ServletHolder> servlets = new LinkedList<ServletHolder>();
	boolean[] stopped = new boolean[] {false};
	LinkedList<P0> onFinishRequests = new LinkedList<P0>();
	public void onFinishRequest(P0 p0) {
		onFinishRequests.add(p0);
	}
	public void start(int port) {
		ThreadUtil.runStrong(NetUtil.acceptF(port, new P1<Socket>() {public void e(Socket clientSk) {
			while (true) {
				HttpServletResponse resp = null;
				try {
					
					HttpServletRequest req = HttpServletRequest.Helper.toRequest(clientSk.getInputStream());
					resp = HttpServletResponse.Helper.getResponse(clientSk.getOutputStream());
					
					if (req==null) {
						break;
					}
					
					HttpServlet servlet = getServlet(req.getRequestURI());
					if (servlet== null) {
						resp.sendError(404);
					} else {
						if (req.getMethod().equals("CONNECT")) {
							serveConnect(req, resp, clientSk, servlet);
							return;
						} else {
							servlet.service(req, resp);
						}
					}
					
					resp.finish();
				} catch (Exception e) {
					if (LangUtil.getRootCause(e) instanceof IOException) {
						Fs.invokeAll(onFinishRequests);
						break;
					} else if (resp!=null) {
						resp.sendError(500);
						resp.finish();
					}
					e.printStackTrace();
				}
				Fs.invokeAll(onFinishRequests);
			}
			IOUtil.close(clientSk);
		}}, Fs.booleanRef(stopped)));
	}
	
	public void stop() {
		stopped[0] = true;
//		ThreadUtil.sleep(3100);
	}
	public void stopAsync() {
		stopped[0] = true;
	}

	protected HttpServlet getServlet(String requestURI) {
//		System.out.println("requestURI=" + requestURI);
		for (ServletHolder servletHolder : servlets) {
			if (servletHolder.condition.e(requestURI)) {
				return servletHolder.servlet;
			}
		}
		return null;
		
	}

	/**
	 * 
	 * @param regex
	 * @param servlet
	 */
	public void addServlet(String regex, HttpServlet servlet) {
		servlets.add(new ServletHolder(RegexUtil.matchF(regex), servlet));
	}
	
	private void serveConnect(HttpServletRequest req, HttpServletResponse resp,
			Socket clientSk, HttpServlet servlet) throws IOException {
		try {
			Map<String, String> doConnect = servlet.doConnect(req, clientSk);
			if (doConnect==null) {
				resp.sendError(404);
				resp.finish();
				IOUtil.close(clientSk);
			} else {
				resp.getWriter().write(HttpUtil.formatPostForm(doConnect));
				resp.finish();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			resp.sendError(500);
			resp.finish();
			IOUtil.close(clientSk);
		}
	}

	public static class ServletHolder {

		private F1<String, Boolean> condition;
		private HttpServlet servlet;

		public ServletHolder(F1<String, Boolean> condition, HttpServlet servlet) {
			this.condition = condition;
			this.servlet = servlet;
		}
		
	}

	public static void main(String[] args) {
		HttpServer httpServer = new HttpServer();
		httpServer.addServlet("/", new HttpServlet() {

			@Override
			public void doGet(HttpServletRequest req, HttpServletResponse resp)
					throws IOException {
				resp.getWriter().write("<h1>Hello world</h1>");
			}
		});
		httpServer.start(8881);
		
//		ThreadUtil.sleep(1110);
//		
//		new WebSession().getString("http://localhost:8881/");
	}

	public void setResourceLocation(final String resourceLocation) {
		servlets.add(new ServletHolder(
				new F1<String, Boolean>() {public Boolean e(String obj) {
					File file = new File(resourceLocation + obj);
					return file.exists() && file.isFile();
				}}, 
				new ResourceServlet(resourceLocation)));
	}
}
