package qj.util.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import qj.tool.build.BuildUtil;
import qj.util.IOUtil;
import qj.util.NetUtil;
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
				String[] split = args[i].split(":");
				boyFriend.addTarget(split[0], Integer.parseInt(split[1]));
			}
		} else {
			boyFriend.addTarget("localhost", 9443);
			boyFriend.addTarget("localhost", 80);
		}
		boyFriend.run();
	}
	
	private static class Target {
		String host;
		int port;
	}
	
	private void addTarget(String host, int port) {
		Target target = new Target();
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
			ThreadUtil.runStrong(NetUtil.acceptF(target.port, new P1<Socket>() {public void e(final Socket clientSk) {
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
