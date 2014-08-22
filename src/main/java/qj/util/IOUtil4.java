package qj.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.zip.ZipFile;

import qj.util.funct.P2;

/**
 * Created by QuanLA
 * Date: Mar 14, 2006
 * Time: 5:12:05 PM
 */
@SuppressWarnings("unchecked")
public class IOUtil4 {


	public static void skip(InputStream in, int length) {
		try {
			in.skip(length);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
    /**
     * Close zip file
     * @param zipFile
     */
    public static void close(ZipFile zipFile) {
        if (zipFile != null) {
            try {
                zipFile.close();
            } catch (IOException e) {
                // When the file is closed already, can ignore this exception
            }
        }
    }
    public static void close(ResultSet rs) {
    	if (rs != null) {
    		try {
    			rs.close();
    		} catch (SQLException e) {
    			// When the file is closed already, can ignore this exception
    		}
    	}
    }
    public static void close(PreparedStatement ps) {
    	if (ps != null) {
    		try {
    			ps.close();
    		} catch (SQLException e) {
    			// When the file is closed already, can ignore this exception
    		}
    	}
    }

	public static InputStream obf(final InputStream stream,final int num) {
		return new InputStream() {
			
			public int read() throws IOException {
				int read = stream.read();
				if (read==-1) {
					return -1;
				}
				return read ^ num;
			}
			public void close() throws IOException {
				stream.close();
			}
		};
	}
	public static OutputStream obf(final OutputStream stream,final int num) {
		return new OutputStream() {
			public void close() throws IOException {
				stream.close();
			}
			@Override
			public void write(int write) throws IOException {
				stream.write(write ^ num);
			}
		};
	}

    public static void close(Socket sk) {
        if (sk != null) {
            try {
            	sk.close();
            } catch (IOException e) {
                // When the file is closed already, can ignore this exception
            	e.printStackTrace();
            }
        }
    }
    public static void close(ServerSocket ssk) {
        if (ssk != null) {
            try {
            	ssk.close();
            } catch (IOException e) {
                // When the file is closed already, can ignore this exception
//            	e.printStackTrace();
            }
        }
    }
    /**
     * Close streams (in or out)
     * @param stream
     */
    public static void close(Closeable stream) {
        if (stream != null) {
            try {
                if (stream instanceof Flushable) {
                    ((Flushable)stream).flush();
                }
                stream.close();
            } catch (IOException e) {
                // When the stream is closed or interupted, can ignore this exception
            }
        }
    }

	public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // When the conn is closed or interupted, can ignore this exception
			}
        }
		
	}

    public static void flush(OutputStream out) {
        if (out != null) {
            try {
                out.flush();
            } catch (IOException e) {
                //
            }
        }
    }

    public static interface ByteProc {
		void evaluate(byte b);
	}

	public static void serialize(Object o, File file) {
		try {
			try {
				file.getParentFile().mkdirs();
			} catch (Exception e) {
			}
            FileOutputStream fout = new FileOutputStream(file);
            serialize(o, fout);
            close(fout);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

    public static void serialize(Object o, OutputStream out) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(o);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	public static <A> A deserialize(File file) {
    	if (!file.exists()) {
    		return null;
    	}
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			Object object = ois.readObject();
			ois.close();
			return (A)object;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static void connect(InputStream is, WritableByteChannel bc) throws IOException {
        int read;
        byte[] buffer = null;
        try {
	        buffer = new byte[8192];
	        ByteBuffer b = ByteBuffer.wrap(buffer);
	        while ((read=is.read(buffer, 0, 8192)) > -1) {
	        	b.limit(read);
	        	bc.write(b);
	        	b.position(0);
	        	b.limit(8192);
	        }
        } finally {
        	is.close();
        }
	}
	
	
	/**
	 * Read the input stream and write to output stream
	 * @param inputStream
	 * @param out
	 * @return
	 * @throws IOException
	 */
    public static long connect(InputStream inputStream, OutputStream out) throws IOException {
    	return connect(inputStream, out, null);
    }
    
	/**
	 * Read the input stream and write to output stream
	 * @param inputStream
	 * @param out
	 * @param byteProc 
	 * @return
	 * @throws IOException
	 */
    public static long connect(InputStream inputStream, OutputStream out, P2<byte[],Integer> byteProc) throws IOException {
        try {
            return dump(inputStream, out, byteProc);
        } finally {
            close(inputStream);
        }
    }
    
    public static long dump(InputStream inputStream, OutputStream out) throws IOException {
        return dump(inputStream, out, null );
    }
    private static long dump(InputStream inputStream, OutputStream out, P2<byte[],Integer> byteProc) throws IOException {
        long total = 0;
        int read;
        int bufferSize = 8192;
        byte[] buffer = new byte[bufferSize];
        while ((read=inputStream.read(buffer, 0, bufferSize)) > -1) {
            if (byteProc!=null) {
                byteProc.e(buffer,read);
            }
            out.write(buffer, 0, read);
            total+=read;
        }
        out.flush();
        return total;
    }

    /**
	 * Read the input stream and write to output stream
	 * @param inputStream
	 * @param out
	 * @return
	 * @throws IOException
	 */
    public static long connect_force(InputStream inputStream, OutputStream out) {
        try {
            try {
                int i;
                long total = 0;
                while ((i = inputStream.read()) > -1) {
                    out.write(i);
                    out.flush();
                    ++total;
                }
                return total;
            } finally {
                close(inputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
	/**
	 * Read the input stream and write to output stream
	 * @param inputStream
	 * @param out
	 * @return
	 * @throws IOException
	 */
    public static long connect_persist(InputStream inputStream, OutputStream out) throws IOException {
        try {
        	int i;
			while (true) {
				if ((i = inputStream.read()) > -1) {
					out.write(i);
					out.flush();
				} else {
					ThreadUtil4.sleep(2000);
				}
	        }
        } finally {
        	inputStream.close();
        }
    }
    
	public static long connectAndClose(InputStream in, OutputStream out) {
		try {
			try {
				return connect(in, out);
			} finally {
				out.flush();
				out.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

    public static byte[] connect_extractData(InputStream inputStream, OutputStream out) throws IOException {
        ByteArrayOutputStream boTemp = null;
        byte[] buffer = null;
        try {
            int read;
            buffer = new byte[8192];
            boTemp = new ByteArrayOutputStream(8192);
            while ((read=inputStream.read(buffer, 0, 8192)) > -1) {
                out.write(buffer, 0, read);
                boTemp.write(buffer, 0, read);
            }
            return boTemp.toByteArray();
        } finally {
        	inputStream.close();
        }
    }

	/**
	 * Read the stream into byte array
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
    public static byte[] readData(InputStream inputStream) {
        try {
			return readDataNice(inputStream);
		} finally {
        	close(inputStream);
		}
    }

    public static byte[] readDataNice(InputStream inputStream) {
		ByteArrayOutputStream boTemp = null;
        byte[] buffer = null;
        try {
            int read;
			buffer = new byte[8192];
            boTemp = new ByteArrayOutputStream();
            while ((read=inputStream.read(buffer, 0, 8192)) > -1) {
                boTemp.write(buffer, 0, read);
            }
            return boTemp.toByteArray();
        } catch (IOException e) {
			throw new RuntimeException(e);
        }
	}

    /**
     * Reads in whole input stream and returns as a string
     * @param in The input stream to read in, will be closed 
     * 				by this method at finish
     * @return the result string
     * @throws IOException
     */
	public static String inputStreamToString(InputStream in) throws IOException {
		return inputStreamToString(in, null);
	}

    /**
     * Reads in whole input stream and returns as a string<br>
     * Will close stream
     * @param in The input stream to read in, will be closed 
     * 				by this method at finish
     * @param charSet charset to convert the input bytes into string
     * @return the result string
     * @throws IOException
     */
	public static String inputStreamToString(InputStream in, String charSet) throws IOException {
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = charSet == null? new InputStreamReader(in) : new InputStreamReader(in, charSet);

			return toString(inputStreamReader);
		} catch (UnsupportedEncodingException e1) {
			throw new RuntimeException(e1);
		} finally {
			close(in);
		}
	}
	
    /**
     * Reads in whole input stream and returns as a string
     * @param reader The input reader to read in, will be closed 
     * 				by this method at finish
     * @return the result string
     * @throws IOException
     */
	public static String toString(Reader reader) {
		try {
			StringBuilder sb = new StringBuilder();
			char[] buffer = new char[4096];
			for (int read; (read = reader.read(buffer)) > -1;) {
				sb.append(buffer, 0, read);
			}
			return sb.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			close(reader);
		}
	}

	public static String inputStreamToString_force(InputStream in) {
		return inputStreamToString_force(in, null);
	}

	/**
	 * Will close in
	 * @param in
	 * @return
	 */
	public static String toString(InputStream in) {
		return inputStreamToString_force(in, null);
	}

	/**
	 * Will close stream
	 * @param in
	 * @param charSet
	 * @return
	 */
	public static String toString(InputStream in, String charSet) {
		return inputStreamToString_force(in, charSet);
	}

	/**
	 * Write String to output stream
	 * @param string
	 * @param out
	 */
	public static void write(String string, OutputStream out) {
		PrintWriter pr = new PrintWriter(out);
		pr.write(string);
		pr.flush();
	}

	/**
	 * Will close stream
	 * @param in
	 * @param charSet
	 * @return
	 */
	public static String inputStreamToString_force(InputStream in, String charSet) {
		try {
			return inputStreamToString(in, charSet);
		} catch (IOException e) {
			return null;
		}
	}


	public static int count(InputStream inputStream) throws IOException {
        byte[] buffer;
        try {
        	int count = 0;
            int read;
			buffer = new byte[8192];
            while ((read=inputStream.read(buffer, 0, 8192)) > -1) {
            	count+=read;
            }
            return count;
        } finally {
        	inputStream.close();
        }
	}

	public static String readUntil_force(InputStream in, char c) {
		try {
			return readUntil(in, c);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public static String readUntil(InputStream in, char c) throws IOException {
		StringBuilder sb = new StringBuilder();
		int read;
		while ((read = in.read())  > -1 && read != c) {
			sb.append((char)read);
		}
		if (read == -1 && sb.length() == 0) {
			return null;
		}
		return sb.toString();
	}

	public static String readUntil(InputStream in, char c, Object lock) throws IOException {
		
		StringBuffer sb = new StringBuffer();
		int read;
		
		read = in.read();
		if (read  > -1 && read != c) {
			synchronized (lock) { // TODO
				do {
					sb.append((char) read);
					read = in.read();
				} while (read > -1 && read != c);
			}
		}
		if (read == -1 && sb.length() == 0) {
			return null;
		}
		return sb.toString();
	}
	public static Properties loadProperties(Class clazz,
			String resource) {
		Properties props = new Properties();
		InputStream in = clazz.getResourceAsStream(resource);
		try {
			props.load(in);
	    	in.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return props;
	}
	
	public static String resource(String string) {
		InputStream s = LangUtil4.getTraceClass(1).getResourceAsStream(string);
		try {
			return inputStreamToString(s);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public static BufferedReader resourceReader(String string) {
		InputStream s = LangUtil4.getTraceClass(1).getResourceAsStream(string);
		try {
            return new BufferedReader(new InputStreamReader(s, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// Blah, never happens, fool checked exception
			throw new RuntimeException(e);
		}
	}

	// The methods below can be possibly moved to other classes (IOUtil, ArrayUtil...)
	/**
	 * Read the input stream and tell if the next bytes are the same with 
	 * the specified bytes
	 * @param bytes To compare with next bytes of stream
	 * @param in Input stream to read bytes from
	 * @return true if next bytes from stream are the same with specified bytes
	 * @throws IOException if an I/O error occurs.
	 */
	public static boolean isNext(
			byte[] bytes,
			InputStream in) throws IOException {
		for (int i = 0; i < bytes.length; i++) {
			int read = in.read();
			if (read == -1 // Stream ended
					|| bytes[i] != (byte)read) {
				return false;
			}
		}
		return true;
	}
}
