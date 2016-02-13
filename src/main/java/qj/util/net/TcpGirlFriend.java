package qj.util.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import qj.tool.build.BuildUtil;
import qj.util.IOUtil;
import qj.util.ThreadUtil;
import qj.util.funct.Fs;
import qj.util.funct.P0;
import qj.util.funct.P2;

public class TcpGirlFriend {
	public static class Build {
		public static void main(String[] args) {
			BuildUtil.buildApp(TcpGirlFriend.class);
		}
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
//		String host="localhost";
//		String host="172.16.166.1";
		String host = args.length==2 ? args[0] : "shopquen.com";
		int port = args.length==2 ? Integer.parseInt(args[1]) : 36293;
		
		P2<Socket, P0> reverseAcceptF = new P2<Socket, P0>() {public void e(final Socket cmdSocket, P0 successF) {
			try {
				InputStream cmdIn = cmdSocket.getInputStream();
				OutputStream cmdOut = cmdSocket.getOutputStream();
				
				String command = read(cmdIn);
				
				try {
					// Connect
					final Socket toSk = new Socket(toHost(command), toPort(command));

					send("ok", cmdOut);
					
					successF.e();
					
					connect(cmdIn, cmdOut, toSk, new P0() {public void e() {
						IOUtil.close(cmdSocket);
						IOUtil.close(toSk);
					}});
					
				} catch (IOException e1) {
					send("connectFail", cmdOut);
					
					successF.e();
				}
			} catch (Exception e) {
				// Spoiled Connection
				successF.e();
			}
			
		}};
		reverseAccept(host, port, reverseAcceptF);
		reverseAccept(host, port, reverseAcceptF);
		reverseAccept(host, port, reverseAcceptF);
		
	}
	
	static ExecutorService executorService = Executors.newCachedThreadPool();
	public static void reverseAccept(final String toHost, final int toPort, final P2<Socket,P0> serveF) {
		final P0 onRelease = new P0() {public void e() {
			reverseAccept(toHost, toPort, serveF);
		}};
		executorService.execute(new Runnable() {public void run() {
			try {
				Socket socket = new Socket(toHost, toPort);

				serveF.e(socket, onRelease);
//			} catch (UnknownHostException e1) {
//				throw new RuntimeException(e1);
			} catch (IOException e1) {
				if (!"Connection refused".equals(e1.getMessage())) {
					System.out.println(e1.toString());
					ThreadUtil.sleep(5000);
				}
				ThreadUtil.sleep(2000);
				onRelease.e();
			}
		}});
	}

	private static String toHost(String command) {
		if (command.contains(":")) {
			String[] split = command.split(":");
			return split[0];
		} else {
			return "localhost";
		}
		
	}
	private static int toPort(String command) {
		if (command.contains(":")) {
			String[] split = command.split(":");
			return Integer.parseInt(split[1]);
		} else {
			return Integer.parseInt(command);
		}
		
	}

	public static void send(String msg, OutputStream cmdOut)
			throws IOException {
		cmdOut.write(msg.getBytes());
		cmdOut.write(3);
		cmdOut.flush();
	}

	public static String read(InputStream in) {
		String command = new String(IOUtil.readUntil(in, (byte)3));
		
		return command;
	}

	public static void connect(InputStream in, OutputStream cmdOut, Socket toSk, P0 closeF)
			throws IOException {
		closeF = Fs.invokeOnce(closeF);
		IOUtil.connectAsync(in, toSk.getOutputStream(), Fs.<IOException>p1(closeF));
		IOUtil.connectAsync(toSk.getInputStream(), cmdOut, Fs.<IOException>p1(closeF));
	}
	
}