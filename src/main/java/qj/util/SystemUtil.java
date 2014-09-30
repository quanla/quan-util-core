package qj.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import qj.util.funct.Fs;
import qj.util.funct.P0;
import qj.util.funct.P1;


public class SystemUtil {
    public static P0 exit = new P0() {public void e() {
		System.exit(0);
	}};

	public static List<byte[]> getMacs() {
		try {
			LinkedList<byte[]> ret = new LinkedList<byte[]>();
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			while (en.hasMoreElements()) {
				NetworkInterface ni = en.nextElement();
				byte[] ha = ni.getHardwareAddress();
				if (ha==null || ha.length == 0
						|| !ni.isUp()
						|| ni.getDisplayName().contains("Virtual")
						) {
					continue;
				}
				ret.add(ha);
			};
			return ret;
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isWindows() {
        return System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") > -1;
    }
    
    public static boolean isMac() {
    	// Mac OS X
    	return System.getProperty("os.name").toUpperCase().indexOf("MAC OS") > -1;
    }

	public static String getClasspath(Class<?> clazz) {
		String[] paths = System.getProperty("java.class.path").split("[;:]");
		String classPath = clazz.getName().replaceAll("\\.", "/");
		
//		System.out.println(System.getProperties());

		for (String path : paths) {
			File file = new File(path);
//			System.out.println(file);
			if (!file.exists()) {
				continue;
			}
			
			if (file.isDirectory()) {
				if (new File(file, classPath).exists()) {
					return file.getPath();
				}
			}
			
			if (file.isFile()) {
				try {
					ZipFile zipFile = new ZipFile(file);
					ZipEntry entry = zipFile.getEntry(classPath + ".class");
					if (entry!=null) {
						return file.getPath();
					}
				} catch (ZipException e) {
//					throw new RuntimeException(e);
				} catch (IOException e) {
//					throw new RuntimeException(e);
				}
			}
		}
		
		return null;
	}
	
	public static String getUserHome() {
		return System.getProperty("user.home");
	}

	public static String classpathSeparator() {
		return isWindows() ? ";" : ":";
	}

	public static String absolutePaths(String paths) {
		StringBuilder sb = new StringBuilder();
		String cd = System.getProperty("user.dir");
		for (String path : paths.split(classpathSeparator())) {
			if (sb.length()>0) {
				sb.append(classpathSeparator());
			}
			if (!new File(path).isAbsolute()) {
				sb.append(cd);
				sb.append("/");
			}
			
			sb.append(path);
		}
		return sb.toString();
	}

	public static String getCurrentDir() {
		return System.getProperty("user.dir");
	}

	public static String classPaths() {
		return absolutePaths(System.getProperty("java.class.path"));
	}
	public static List<String> classPaths1() {
		return Cols.filter(Arrays.asList(System.getProperty("java.class.path").split(classpathSeparator())), (p) -> {
			return !p.contains("jre") && !p.contains("JetBrains");
		});
	}


	public static void eachClassPath(P1<String> p1) {

		for (String path : System.getProperty("java.class.path").split(classpathSeparator())) {
			p1.e(path);
		}
	}

	public static void onExit(P0 p0) {
		Runtime.getRuntime().addShutdownHook(new Thread(Fs.runnable(p0)));
	}

	/**
	 * In jar only
	 * @param packa
	 * @return 
	 */
	public static List<String> getClasses(String packa) {
		final LinkedList<String> ret = new LinkedList<String>();
		final String packagePath = packa.replaceAll("\\.", "/");
		SystemUtil.eachClassPath(new P1<String>() {public void e(String path) {
			File file = new File(path);
			if (file.exists() && file.isFile()) {
				try {
					ZipUtil.each(packagePath, new ZipFile(file), new P1<ZipEntry>() {public void e(ZipEntry z) {
						ret.add(z.getName()
								.replaceAll("\\.class$", "")
								.replaceAll("/", ".")
								);
					}});
				} catch (IOException e1) {
					throw new RuntimeException(e1);
				}
			}
		}});
		return ret;
		
	}

	public static String jdkHome() {
		return System.getenv("JAVA_HOME");
	}
	public static void main(String[] args) {
//		System.out.println(System.getenv("JAVA_HOME"));
//		System.out.println(Cols.toString(System.getenv()));
//		System.out.println(Cols.toString(System.getProperties()));
//		System.out.println("---------------------------------");
//		System.out.println(Cols.toString(System.getenv()));

//		System.out.println(Cols.toString(classPaths1()));
	}
	
	public static Process runJava(Class<?> mainClass, String dir) {
		return ProcessUtil.exec(new String[] {System.getProperty("java.home") + "/bin/java", "-cp", classPaths(), mainClass.getName()}, null, dir);
	}

	static BufferedReader br;
	public static String readLine() {
		if (br==null) {
			br = new BufferedReader(new InputStreamReader(System.in));
		}
		try {
			return br.readLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void onReturn(final P0 p0) {
		ThreadUtil.run(new P0() {public void e() {
			while (true) {
				try {
					readLine();
				} catch (Exception e1) {
					return;
				}
				p0.e();
			}
		}});
	}
	public static void onReturn1(final P0 p0) {
		onReturn1(Fs.<String>p1(p0));
	}
	public static void onReturn1(final P1<String> p1) {
		ThreadUtil.runStrong(new P0() {public void e() {
			try {
				String readLine = readLine();
				p1.e(readLine);
			} catch (Exception e1) {
				return;
			}
		}});
	}

	public static Process runJava(Map config) {
		String mainClass = (String) config.get("mainClass");
		String homeDir = (String) config.get("home.dir");
		return ProcessUtil.exec(System.getProperty("java.home") + "/bin/java -cp " + classPathString((List<String>)config.get("classpath")) + " " + mainClass, homeDir);

	}

	private static String classPathString(List<String> list) {
		LinkedList<String> paths = new LinkedList<String>();
		for (String string : list) {
			paths.add(new File(string).getAbsolutePath());
		}
		return Cols.join(paths, classpathSeparator());
	}

	public static File getTmpDir() {
		return new File(System.getProperty("java.io.tmpdir"));
	}
}

