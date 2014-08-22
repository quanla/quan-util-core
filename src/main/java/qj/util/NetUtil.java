package qj.util;

import java.io.*;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import qj.util.HttpUtil.HttpMessage;
import qj.util.funct.F0;
import qj.util.funct.F1;
import qj.util.funct.Fs;
import qj.util.funct.P0;
import qj.util.funct.P1;

public class NetUtil extends SocketUtil {

	public static DatagramSocket UDP_SK = null;
	
	public static P0 acceptF(final int port, final P1<Socket> p1) {
		return acceptF(port, p1, Fs.f0(false));
	}

	@SuppressWarnings("unchecked")
	public static P0 acceptF(final int port, final P1<Socket> p1, final F0<Boolean> interrupted1) {
		try {
			final Boolean[] stopped = new Boolean[] {false};
			final F0<Boolean> interrupted = Fs.or( interrupted1, Fs.booleanRef(stopped));

			final ServerSocket ssk = new ServerSocket(port);
			SystemUtil.onExit(new P0() {public void e() {
				stopped[0] = true;
				IOUtil.close(ssk);
			}});
			
			ssk.setSoTimeout(3000);

			return new P0() {public void e() {
				try {
					while (true) {
						try {
							if (interrupted.e()) {
//								System.out.println("Http server terminated");
								IOUtil.close(ssk);
								return;
							}
							
							final Socket sk = ssk.accept();
							ThreadUtil.run(new Runnable() {public void run() {
								p1.e(sk);
							}});
						} catch (SocketTimeoutException e) {
							if (interrupted.e()) {
//								System.out.println("Http server terminated");
								IOUtil.close(ssk);
								return;
							}
						}
					}
				} catch (IOException e) {
					if (interrupted.e()) {
//						System.out.println("Http server terminated");
						IOUtil.close(ssk);
						return;
					}
					throw new RuntimeException(e);
				}
			}};
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		
	}
	
	public static void main(String[] args) {

		final boolean[] called = {false};
		int port = 8086;
		System.out.println("Listening " + port);
		acceptF(port, new P1<Socket>() {public void e(Socket sk) {
			
//			connect(sk, new )
			
			if (called[0]) {
				return ;
			}
			called[0] = true;
			try {
				IOUtil.connect_force(sk.getInputStream(), System.out);
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
//			HttpMessage httpMessage;
//			try {
//				httpMessage = HttpUtil.readHttpMessage(sk.getInputStream());
//				httpMessage.replaceAll("localhost:8080","vf_external_service_provisioning");
//				Socket toSk = new Socket("vf_external_service_provisioning",80);
//				System.out.println(httpMessage.toString());
//				httpMessage.send(toSk.getOutputStream());
//				IOUtil.dump(toSk.getInputStream(), sk.getOutputStream());
//			} catch (IOException e1) {
//				throw new RuntimeException(e1);
//			}
		}}).e();
	}
	
	public static void main1(String[] args) {
//		System.out.println("Listening UDP on 514");
//		acceptUdp(514, new P1<byte[]>() {
//			public void e(byte[] obj) {
//				System.out.println(new String(obj));
//			}
//		});

		System.out.println("Listening on 8459");
		acceptF(8459, new P1<Socket>() {public void e(Socket obj) {
			try {
				IOUtil.connect(obj.getInputStream(), System.out);
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
		}}).e();
		
//		System.out.println("Listening on 8459");
//		acceptF(8459, new P1<Socket>() {public void e(Socket obj) {
//			try {
//				P2<byte[], Integer> toOut = new P2<byte[], Integer>() {public void e(byte[] bytes, Integer read) {
//					System.out.println(new String(bytes,0,read));
//				}};
//				SocketUtil.connect(obj, new Socket("localhost", 8458), toOut, toOut, null);
//				
////				IOUtil.connect(obj.getInputStream(), System.out);
//			} catch (IOException e1) {
//				throw new RuntimeException(e1);
//			}
//		}}).e();
	}

	public static void acceptUdpOnce(int port) {
		try {
			DatagramSocket sk = new DatagramSocket(port);
			DatagramPacket p = new DatagramPacket(new byte[1024], 1024);
			sk.receive(p);
			System.out.println(new String(p.getData(), 0, p.getLength()));
		} catch (SocketException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void sendUdp(byte[] bytes, final String host, final int port) {
		try {
			if (UDP_SK == null) {
				UDP_SK = new DatagramSocket();
			}
			DatagramPacket p = new DatagramPacket(bytes, bytes.length, 
					InetAddress.getByName(host), port
					);
			UDP_SK.send(p);
			
		} catch (UnknownHostException e1) {
			throw new RuntimeException(e1);
		} catch (SocketException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	public static P0 acceptUdpF(
			final int port, 
			final P1<byte[]> acceptF, 
			final F0<Boolean> interrupted) {
		return new P0() {public void e() {
			acceptUdp(port, acceptF, interrupted);
		}};
	}
	
	public static void acceptUdp(int port, P1<byte[]> acceptF) {
		acceptUdp(port, acceptF, Fs.f0(false));
	}
	public static void acceptUdp(int port, P1<byte[]> acceptF, F0<Boolean> interrupted) {
		try {
			DatagramSocket sk = new DatagramSocket(port);
			DatagramPacket p = new DatagramPacket(new byte[8192], 8192);
			sk.setSoTimeout(800);
			while (true) {
				try {
					sk.receive(p);
					acceptF.e(Arrays.copyOf(p.getData(), p.getLength()));
				} catch (SocketTimeoutException e) {
					if (interrupted.e()) {
						sk.close();
						break;
					}
				}
			}
		} catch (SocketException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void comingObjectLink(int port, F1<Object, Object> action) {
		try {
			Socket socket = new Socket("localhost", port);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
//			out.flush();
			for (Object o;(o=in.readObject())!=null;) {
				if ("test".equals(o)) {
					out.writeObject("ok");
				} else {
					out.writeObject(action.e(o));
				}
//				out.flush();
			}
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static class GoingObjectLink {

		final ObjectInputStream in;
		final ObjectOutputStream out;
		public GoingObjectLink(Socket socket) {
			try {
				out = new ObjectOutputStream(socket.getOutputStream());
				InputStream inputStream = socket.getInputStream();
				in = new ObjectInputStream(inputStream);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		public synchronized Object send(Object obj) {
			try {
				out.writeObject(obj);
				return in.readObject();
			} catch (IOException e1) {
				return null;
			} catch (ClassNotFoundException e1) {
				throw new RuntimeException(e1);
			}
		}
		public boolean alive() {
			return "ok".equals(send("test"));
		}
	}

	public static void connectPersist(String host, int port, P1<Socket> onConnected) {
			while (true) {
				try {
					Socket sk = new Socket(host,port);
					onConnected.e(sk);
					ThreadUtil.sleep(1000);
				} catch (ConnectException e) {
					if (!"Connection refused".equals(e.getMessage())) {
	//					System.out.println(e.toString() + " - Sleeping");
					}
					ThreadUtil.sleep(3000);
				} catch (Exception e) {
	//				System.out.println(e.toString() + " - Sleeping");
					ThreadUtil.sleep(3000);
				}
			}
		}

	public static Socket socket(String sssAddress) {
		String[] split = sssAddress.split(":");
		try {
			return new Socket(split[0], Integer.parseInt(split[1]));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static class Sender {
		
		ExecutorService executor = Executors.newSingleThreadExecutor(ThreadUtil.daemonThreadFactory);
	
		private final Socket sk;
	
		private P1<Exception> onException;
	
		public Sender(Socket sk) {
			this.sk = sk;
		}
		
		public void onException(P1<Exception> onException) {
			this.onException = onException;
		}
	
		public void send(final HttpMessage msg) {
			executor.execute(new Runnable() {public void run() {
				synchronized (sk) {
					try {
						OutputStream out = sk.getOutputStream();
						msg.send(out);
						out.flush();
					} catch (IOException e) {
						if (onException != null) {
							onException.e(e);
						}
					}
				}
			}});
		}
		
		public HttpMessage sendNReceive(HttpMessage msg) throws IOException {
			synchronized (sk) {
				send(msg);
				return HttpUtil.readHttpMessage(sk.getInputStream());
			}
		}
	}
}
