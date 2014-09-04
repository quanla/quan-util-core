package qj.util;

import static qj.util.funct.FsGenerated.p3;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInput;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.Collection;

import qj.tool.string.StringTracker;
import qj.util.col.IdentitySet;
import qj.util.funct.*;

public class IOUtil extends IOUtil4 {
	
    public static void closeAll(Collection<? extends Closeable> os) {
        for (Closeable o : os) {
            close(o);
        }
    }
    public static void closeAllSockets(Collection<? extends Socket> os) {
        for (Socket o : os) {
            close(o);
        }
    }
    public static void closeAllSockets(Socket... os) {
        for (Socket o : os) {
            close(o);
        }
    }

    public static void closeAll(Closeable... os) {
        for (Closeable o : os) {
            close(o);
        }
    }
    
	public static void eachBuffer(InputStream inputStream, P2<byte[], Integer> p) {
        try {
            byte[] buffer = new byte[1024];
            int read;
            while ((read=inputStream.read(buffer, 0, 1024)) > -1) {
            	p.e(buffer, read);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
        	close(inputStream);
        }
	}

	/**
	 * Async read the input stream for texts;
	 * Lock when something is coming in but not yet finish
	 * @param in
	 * @param delimiter
	 * @param p1
	 * @param excHandler
	 * @param lock
	 */
	public static void asyncReadUntil(final InputStream in, 
			final char delimiter, 
			final P1<String> p1,
			final P1<IOException> excHandler,
			Object lock) {
		if (lock == null) {
			lock = new Object();
		}
		final Object oLock = lock;
		Thread readThread = new Thread() {
			public void run() {
				try {
					String read;
					while ((read = oLock==null ? readUntil(in, delimiter) : readUntil(in, delimiter, oLock)) != null) {
						p1.e(read);
					}
				} catch (IOException e) {
					if (excHandler != null) {
						excHandler.e(e);
					}
				}
			}
		};
		readThread.setDaemon(true);
		readThread.start();
	}

	public static void readLines(InputStream in,
			P1<String> lineReader) {
		try {
			// TODO Dirty hack to skip 3 BOM charaters EF BB BF
			in.read();
			in.read();
			in.read();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String str;
			while ((str = br.readLine()) != null) {
				lineReader.e(str);
			}
			in.close();
		} catch (UnsupportedEncodingException e) {
			// Hate this checked E
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static final int MAX_ASYNC = 20;
	public static void asynConnect(InputStream inputStream, OutputStream out) {
		ConnectTask connectTask = new ConnectTask(inputStream, out, connect);
//		connectTask.setDaemon(true);
		if (ConnectTask.running == MAX_ASYNC) {
			synchronized (ConnectTask.class) {
				try {
					ConnectTask.class.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		connectTask.start();
	}

	public static void connectAsync(final InputStream in, final OutputStream out) {
		ThreadUtil.run(new P0() {public void e() {
			try {
				dump(in, out);
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
		}});
	}

	public static void connectAsync(final InputStream in, final OutputStream out, final P1<IOException> excF) {
		ThreadUtil.run(new P0() {public void e() {
			try {
				dump(in, out);
			} catch (IOException e1) {
				excF.e(e1);
			}
		}});
	}

	public static void asyncConnect3(InputStream inputStream,
			OutputStream out) {
		ConnectTask connectTask = new ConnectTask(inputStream, out, connect_persist);
		if (ConnectTask.running == MAX_ASYNC) {
			synchronized (ConnectTask.class) {
				try {
					ConnectTask.class.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		connectTask.start();
	}

	public static Thread asyncConnect2(InputStream inputStream,
			OutputStream out) {
        return asyncConnect2(inputStream, out, null);
    }
    public static F1<Character, Boolean> NO_R = new F1<Character, Boolean>() {public Boolean e(Character obj) {
        return obj != '\r';
    }};
	public static Thread asyncConnect2(InputStream inputStream,
			OutputStream out, F1<Character, Boolean> filter) {
		ConnectTask connectTask = new ConnectTask(inputStream, out, connect2, filter);
		if (ConnectTask.running == MAX_ASYNC) {
			synchronized (ConnectTask.class) {
				try {
					ConnectTask.class.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return connectTask;
	}
	static P2<InputStream, OutputStream> connect = new P2<InputStream, OutputStream>() {public void e(InputStream in, OutputStream out) {
		try {
			IOUtil4.connect(in, out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
    };

	static P2<InputStream, OutputStream> connect_force = new P2<InputStream, OutputStream>() {public void e(InputStream in, OutputStream out) {
        IOUtil4.connect_force(in, out);
	}
    };

	static P3<InputStream, OutputStream, F1<Character, Boolean>> connect2 = new P3<InputStream, OutputStream, F1<Character, Boolean>>() {public void e(InputStream in, OutputStream out, F1<Character, Boolean> filter) {
		try {
        	int i;
			while ((i = in.read()) > -1) {
                if (filter == null || filter.e((char) i)) {
                    out.write(i);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            flush(out);
        	close(in);
        }
	}
    };

    static P2<InputStream, OutputStream> connect_persist = new P2<InputStream, OutputStream>() {public void e(InputStream in, OutputStream out) {
		try {
			IOUtil4.connect_persist(in, out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}};

    public static <A> A loadObject(String fileLoc) {
        FileInputStream fi;
        try {
            fi = new FileInputStream(fileLoc);
        } catch (FileNotFoundException e) {
            return null;
        }
        return IOUtil.<A>loadObject(fi);
    }

    @SuppressWarnings({"unchecked"})
    public static <A> A loadObject(InputStream fi) {
        ObjectInputStream oi = null;
        try {
            oi = new ObjectInputStream(fi);
            return (A) oi.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            close(oi);
        }
    }

    public static void saveObject(Object object, String fileLoc) {
        try {
		    FileUtil.mkParentDirs(fileLoc);
            FileOutputStream fo = new FileOutputStream(fileLoc);
            try {
                serialize(object, fo);
            } finally {
                close(fo);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Querier querier(String resource, Class<?> clazz) {
        return querier(inF(resource, clazz));
    }

    private static Querier querier(final F0<InputStream> inF) {
        return new Querier() {
            public void through(P1<String> f) {
                eachLine(inF.e(), f, -1);
            }

            public String get(int lineIndex) {
                return getLine(inF.e(), lineIndex);
            }

            public String end(P1<Integer> lineIndex) {
                return lastLine(inF.e(), lineIndex);
            }
        };
    }

    private static F0<InputStream> inF(final String resource, final Class<?> clazz) {
        return new F0<InputStream>() {
            public InputStream e() {
                return clazz.getResourceAsStream(resource);
            }
        };
    }

    public static void eachLine(InputStream fi, final P1<String> f, int limit) {
    	final int[] limitRef = {limit};
    	eachLine(fi, new F1<String, Boolean>() {public Boolean e(String obj) {
    		f.e(obj);
    		
    		if (limitRef[0] == -1) {
    			return true;
    		}
    		limitRef[0]--;
			return limitRef[0] > 0;
		}});
    }

    public static void eachLine(InputStream fi, F1<String, Boolean> f) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(fi));
            for (String str; (str = br.readLine()) != null;) {
                if (!f.e(str)) {
                	break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            close(br);
        }
    }

    public static String getLine(InputStream fi, int lineIndex) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(fi));
            for (String str; (str = br.readLine()) != null; lineIndex --) {
                if (lineIndex==0) {
                    return str;
                }
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            close(br);
        }
    }

    public static String lastLine(InputStream fi, P1<Integer> index) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(fi));
            String lastLine = null;
            int i = 0;
            for (String str; (str = br.readLine()) != null; i++) {
                lastLine = str;
            }
            index.e(i-1);
            return lastLine;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            close(br);
        }
    }

    public static F0<String> lineReader(InputStream inputStream) {
        final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        return new F0<String>() {
            public String e() {
                try {
                    String line = br.readLine();
                    if (line != null) {
                        return line;
                    } else {
                        br.close();
                        return null;
                    }
                } catch (IOException e) {
                    return null;
                }
            }
        };
//        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public static void eachLine(InputStream inputStream, P1<String> p1) {
        eachLine(inputStream, p1, -1);
    }

    /**
     * Read the input stream until the specified byte is met
     * @param in
     * @param c
     * @return The read bytes, not include <code>c</code>
     */
    public static byte[] readUntil(InputStream in, byte c) {
        ByteArrayOutputStream sb = new ByteArrayOutputStream(2046);
		int read;
        try {
            while ((read = in.read())  > -1 && read != c) {
                sb.write(read);
            }
            if (read == -1 && sb.size() == 0) {
                return null;
            }
            return sb.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


	public static byte[] readUntil(InputStream in, String... untils) {
		StringTracker stringTracker = new StringTracker(untils);
		ByteArrayOutputStream sb = new ByteArrayOutputStream(2046);
		int read;
        try {
            while ((read = in.read())  > -1 && !stringTracker.track((char) read)) {
                sb.write(read);
            }
            if (read == -1 && sb.size() == 0) {
                return null;
            }
            sb.write(read);
            return sb.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
	}

    static class ConnectTask extends Thread {
		private static int running = 0;
		
		private InputStream in;
		private OutputStream out;
		private P3<InputStream, OutputStream, F1<Character, Boolean>> connector;
        private F1<Character, Boolean> filter;

        public ConnectTask(InputStream in, OutputStream out, P2<InputStream, OutputStream> connector) {
			this.in = in;
			this.out = out;
			this.connector = p3(connector);
		}


		public ConnectTask(InputStream in,
                           OutputStream out,
                           P3<InputStream, OutputStream, F1<Character, Boolean>> connector,
                           F1<Character, Boolean> filter) {
			this.in = in;
			this.out = out;
			this.connector = connector;
            this.filter = filter;
        }

        public void run() {
			try {
				synchronized (ConnectTask.class) {
					running++;
				}
				connector.e(in, out, filter);
				out.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				synchronized (ConnectTask.class) {
					running--;
					if (running<=0) {
						ConnectTask.class.notifyAll();
					} else {
						ConnectTask.class.notify();
					}
				}
				
//				try {
//					out.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
			}
		}

	}


	public static InputStream getRawInputStream(InputStream inputStream) {
		if (inputStream instanceof FilterInputStream) {
			try {
				return //(InputStream) FilterInputStream.class.getDeclaredField("in").get(inputStream);
				inputStream;
			} catch (SecurityException e) {
				throw new RuntimeException(e);
//			} catch (IllegalAccessException e) {
//				throw new RuntimeException(e);
//			} catch (NoSuchFieldException e) {
//				throw new RuntimeException(e);
			}
		}
		return inputStream;
	}

	/**
	 * Read the input stream and fill the whole byte buffer
	 * @param bytes the buffer to be filled
	 * @param in Stream to get bytes from
	 * @return true if the byte buffer is filled, false if stream ended before 
	 *         buffer is filled
	 * @throws IOException if an I/O error occurs.
	 */
	public static boolean fill(byte[] bytes, InputStream in) throws IOException {
		for (int i = 0; i < bytes.length; i++) {
			int read = in.read();
			if (read == -1) {
				return false;
			} else {
				bytes[i] = (byte)read;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public static <A> A deserialize(InputStream in) {
		ObjectInputStream oin;
		try {
			oin = new ObjectInputStream(in);
			return (A) oin.readObject();
		} catch (IOException e) {
			return null;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static <A> A deserialize(byte[] data) {
		return deserialize(new ByteArrayInputStream(data));
	}
	public static <A> A deserialize(String data) {
		if (data==null) {
			return null;
		}
		return deserialize(StringUtil.hexToBytes(data));
	}
	public static String serialize_s(Object o) {
		return StringUtil.bytesToHexString(serialize(o));
	}

	public static byte[] serialize(Object o) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		serialize(o, bout);
		return bout.toByteArray();
	}

	public static int count(int countTarget, InputStream in) {
		try {
			int count = 0;
			for (int i; (i = in.read()) != -1;) {
				if (countTarget == i) {
					count++;
				}
			}
			return count;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			close(in);
		}
	}

	@Deprecated
	public static byte[] readData_force(InputStream inputStream) {
//		try {
			return readData(inputStream);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
	}

	public static byte[] readEnough(long total, InputStream in) {
		return readEnough(total, in, null);
	}
	
	public static byte[] readEnough(long total, InputStream in, P1<Long> progressF) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		dumpEnough(total, in, out, progressF);
		
		return out.toByteArray();
	}
	public static byte[] readEnough(int total, DataInput in) {
		ByteArrayOutputStream out = new ByteArrayOutputStream(total);
		
		dumpEnough(total, in, out);
		
		return out.toByteArray();
	}
	
	public static void dumpEnough(long total, InputStream in,OutputStream out) {
		dumpEnough(total, in, out, null);
	}
	
	
	
	public static void dumpEnough(long total, InputStream in,OutputStream out, P1<Long> progress) {
		try {
			byte[] buffer = new byte[1024];
			int read;
			long remain = total;
			while ((read = in.read(buffer, 0, (int) Math.min(remain, 1024))) > -1) {
				out.write(buffer, 0, read);
				remain -= read;
				if (progress != null) {
					long pro = total - remain;
					progress.e(pro);
				}
				if (remain <= 0) {
					return;
				}
				
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		P2<Long,Long> progressF = new P2<Long,Long>() {public void e(Long a, Long b) {
			System.out.println(a);
		}};
		readEnough(3000, FileUtil.fileInputStream("c:/Users/quanle/Desktop/AutoClick.zip"), Fs.p1(progressF, 1010L));
	}

	public static void dumpEnough1(int total, InputStream in,OutputStream out) {
		try {
			for (int i = 0; i < total; i++) {
				int read = in.read();
				if (read == -1) {
					break;
				}
				out.write(read);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public static void dumpEnough(int total, DataInput in,OutputStream out) {
		try {
			for (int i = 0; i < total; i++) {
				byte read = in.readByte();
				if (read == -1) {
					break;
				}
				out.write(read);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String md5(InputStream in) {
		try {
			final MessageDigest m=MessageDigest.getInstance("MD5");
			
			IOUtil.eachBuffer(in, new P2<byte[], Integer>() {public void e(byte[] buffer, Integer read) {
				m.update(buffer, 0, read);
			}});
			
			return new BigInteger(1,m.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtil.close(in);
		}
	}

	public static void readInputStreamToP1(final InputStream in,
			final P1<String> writeF) {
		ThreadUtil.run(new Runnable() {
			
			public void run() {
		        int read;
		        int bufferSize = 1024;
		        byte[] buffer = new byte[bufferSize];
		        try {
					while ((read=in.read(buffer, 0, bufferSize)) > -1) {
					    String str = new String(buffer, 0, read);
					    
					    writeF.e(str);
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	public static String readAll(Reader reader) {
		char[] buffer = new char[8*1024];
		try {
			StringBuilder sb = new StringBuilder();
			for (int read; ((read = reader.read(buffer)) != -1);) {
				sb.append(buffer, 0, read);
			}
			return sb.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static P0 closeF(final Connection conn) {
		return new P0() {public void e() {
			close(conn);
		}};
	}

	public static P1<String> dualOut(final OutputStream out1, final OutputStream out2) {
		return new P1<String>() {public void e(String line) {
			try {
				out1.write((line + "\n").getBytes());
				out2.write((line + "\n").getBytes());
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
		}};
	}

	public static P0 flushF(final Flushable flushable) {
		return new P0() {

			@Override
			public void e() {
				try {
					flushable.flush();
				} catch (IOException e1) {
				}
			}};
	}

	public static P0 closeAllSocketsF(final Collection<Socket> sockets) {
		return new P0() {public void e() {
			closeAllSockets(sockets);
		}};
	}

	public static P0 closeAllSocketsF(final Socket... sockets) {
		return new P0() {public void e() {
			closeAllSockets(sockets);
		}};
	}
	
	/**
	 * Read and call back each time it meet the separationLine
	 * p1 is called with multiple lines concated
	 * @param separationLine
	 * @param reader
	 * @param p1
	 */
	public static void eachMarkedLines(String separationLine,
			InputStreamReader reader, P1<String> p1) {
		BufferedReader br = new BufferedReader(reader);
		try {
			StringBuilder sb = new StringBuilder();
			for (String line; (line = br.readLine()) != null ;) {
				if (line.equals(separationLine)) {
					p1.e(sb.toString());
					sb.setLength(0);
				} else {
					if (sb.length() > 0) {
						sb.append("\n");
					}
					sb.append(line);
				}
			}
			String left = sb.toString().trim();
			if (left.length() > 0) {
				p1.e(left);
			}
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
