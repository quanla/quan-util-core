package qj.util.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import qj.tool.build.BuildUtil;
import qj.util.IOUtil;
import qj.util.NetUtil;
import qj.util.RegexUtil;
import qj.util.ThreadUtil;
import qj.util.funct.P0;
import qj.util.funct.P1;

public class TcpBoyFriend {
	static int gfControlPort = 36293;
	
	List<Target> targets = new LinkedList<Target>();
	
	public static void main(String[] args) {
		TcpBoyFriend boyFriend = new TcpBoyFriend();
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				Matcher matcher = RegexUtil.matcher("(\\d+)->(\\w+):(\\d+)", args[i]);
				matcher.matches();

				boyFriend.addTarget(Integer.parseInt(matcher.group(1)), matcher.group(2), Integer.parseInt(matcher.group(3)));
			}
		} else {
			System.out.println("No target configured. Try:");
			System.out.println("java qj.util.net.TcpBoyFriend 1010->localhost:80");
			System.exit(0);
			return;
		}
		boyFriend.run();
	}
	
	private static class Target {
		String host;
		int port;
		public int boyfriendPort;
	}
	
	private void addTarget(int boyfriendPort, String host, int port) {
		Target target = new Target();
		target.boyfriendPort = boyfriendPort;
		target.host = host;
		target.port = port;
		targets.add(target);
	}

	public static class Build {
		public static void main(String[] args) {
			BuildUtil.buildApp(TcpBoyFriend.class);
		}
	}

	private void run() {
		final LinkedList<P1<Socket>> waitGFs = new LinkedList<P1<Socket>>();
		final LinkedList<Socket> avaiGFs = new LinkedList<Socket>();
		
		ThreadUtil.runStrong(NetUtil.acceptF(gfControlPort, new P1<Socket>() {public void e(final Socket gfSk) {
			System.out.println("Got GF conn (" + (avaiGFs.size() + 1) + ")");
			if (!waitGFs.isEmpty()) {
				waitGFs.removeLast().e(gfSk);
				return;
			}
			
			avaiGFs.add(gfSk);
		}}));
		
		for (final Target target : targets) {
			ThreadUtil.runStrong(NetUtil.acceptF(target.boyfriendPort, new P1<Socket>() {public void e(final Socket clientSk) {
				System.out.println("Got client conn");
				if (!avaiGFs.isEmpty()) {
					connect(target.host, target.port, clientSk, avaiGFs.removeLast());
					return;
				}
				
				waitGFs.add(new P1<Socket>() {public void e(Socket gfSk) {
					connect(target.host, target.port, clientSk, gfSk);
				}});
			}}));
		}

	}

	private void connect(String targetHost, int targetPort, final Socket clientSk, final Socket gfSk) {
		try {
			InputStream gfIn = gfSk.getInputStream();
			OutputStream gfOut = gfSk.getOutputStream();
			TcpGirlFriend.send(targetHost + ":" + targetPort, gfOut);
			String result = TcpGirlFriend.read(gfIn);
			
			P0 closeF = new P0() {public void e() {
				IOUtil.close(clientSk);
				IOUtil.close(gfSk);
			}};
			
			if (result.equals("ok")) {
				System.out.println("Tunneling connection");
				TcpGirlFriend.connect(gfIn, gfOut, clientSk, closeF);
			} else {
				System.out.println("Girlfriend failed. Closing connections");
				closeF.e();
			}
		} catch (IOException e1) {
			IOUtil.close(clientSk);
		}
	}
}
