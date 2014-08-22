package qj.util.net;

import java.io.IOException;
import java.net.Socket;

import qj.tool.build.BuildUtil;
import qj.util.IOUtil;
import qj.util.NetUtil;
import qj.util.funct.P1;

public class SimpleTcpProxy {
	public static void main(String[] args) {
		String[] split = args[0].split(":");
		int waitPort = Integer.parseInt(split[0]);
		final String targetHost = split[1];
		final int targetPort = Integer.parseInt(split[2]);
		
		NetUtil.acceptF(waitPort, new P1<Socket>() {public void e(Socket clientSk) {
			try {
				Socket targetSk = new Socket(targetHost, targetPort);
				NetUtil.connect(clientSk, targetSk);
			} catch (IOException e1) {
				IOUtil.close(clientSk);
				throw new RuntimeException(e1);
			}
		}}).e();
	}
	
	public static class Build {
		public static void main(String[] args) {
			BuildUtil.buildApp(SimpleTcpProxy.class);
		}
	}
}
