package qj.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import qj.util.funct.*;

public class Wrapper {
    static Process process;
    static F0<Process> spawnProcess;

    public static void main(String[] args) {
        createSpawner(args);
        process = spawnProcess.e();

        while (true) {
            // Observe
            try { // TODO is it more sense checking at last
                process.exitValue();
                break; // Process exited
            } catch (IllegalThreadStateException e) {
                // The process still going
            }

            File applyingPatchesDir = new File("data/applyingPatches");
            if (applyingPatchesDir.exists()) {
                ThreadUtil4.sleep(2000);
                applyingPatches(applyingPatchesDir.listFiles());
                FileUtil.rd(applyingPatchesDir);
            }

            File commandsDir = new File("data/wrapperCommands");
            if (commandsDir.exists()) {
                ThreadUtil4.sleep(500);
                if (new File(commandsDir, "restart").exists()) {
                	shutdownProcess();
                	process = spawnProcess.e();
                }
                FileUtil.rd(commandsDir);
            }

            ThreadUtil4.sleep(5000);
        }
    }

    private static void applyingPatches(File[] patchFiles) {
        shutdownProcess();
        for (File patchFile : patchFiles) {
            ZipUtil.each(patchFile, new P2<String, InputStream>() {public void e(String path, InputStream content) {
                if ("META-INF/deleted.txt".equals(path)) {
                    for (String deletedFile : IOUtil.inputStreamToString_force(content).split("\r?\n")) {
                        new File(deletedFile).delete();
                    }
                } else if (path.startsWith("data/wrapper")) {
                } else {
					try {
						FileUtil.writeToFile(content, new File(path));
					} catch (Exception e1) {
						System.out.println("Error writing file " + path + ": " + e1.getMessage());
//						throw new RuntimeException(e1);
					}
                }
            }});
            patchFile.delete();
        }

        process = spawnProcess.e();
    }

	private static void shutdownProcess() {
		process.destroy();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
        }
	}

    private static void createSpawner(String[] args) {
        ArrayList<String> processParams = getProcessParams(new File("."));
        ArrayUtil.appendArrayToList(args, processParams);
        spawnProcess = Fs.f0(ProcessUtil.exec, System.getProperty("java.home") + "/bin/java", null, processParams.toArray(new String[0]));
    }

    /**
     * splash & classpath
     * @return
     * @param dir
     */
    private static ArrayList<String> getProcessParams(File dir) {
        String splash = getSplash(dir);

        ArrayList<String> processParams = new ArrayList<String>();
        if (splash != null) {
            processParams.add(splash);
        }
        processParams.add("-cp");

        String classpath = getClasspath(dir);

        processParams.add(classpath);
        return processParams;
    }

    private static String getSplash(File dir) {
        File splashDir = new File(dir.getAbsolutePath() + "/data/splash");
        File splashFile;
        String splash = null;
        if (splashDir.exists() && splashDir.isDirectory() && (splashFile = RandomUtil.choose(splashDir.listFiles())) != null) {
            splash = "-splash:data/splash/" + splashFile.getName();
        }
        return splash;
    }

    private static String getClasspath(File dir) {
        File libDir = new File(dir.getAbsolutePath() + "/data/lib");
        final F1<File, Boolean> isJar = FileUtil.isExtention("jar");
        List<String> paths = new ArrayList<String>();
        if (libDir.exists() && libDir.isDirectory()) {
            paths.addAll(Cols.yield(libDir.listFiles(), new F1<File, String>() {public String e(File f) {
                if (isJar.e(f)) {
                    return "data/lib/" + f.getName();
                } else {
                    return null;
                }
            }}));
        }
        if (new File("data/classes").exists()) {
            paths.add("data/classes");
        }

        return Cols.isEmpty(paths) ? System.getProperty("java.class.path") : Cols.join(paths, LangUtil4.getClasspathDelimiter());
    }

    public static String getRunCmd(File dir) {
        ArrayList<String> cmd = getProcessParams(dir);
        cmd.add(0, "java");
        return Cols.join(cmd, " ");
    }

    public static void main1(String[] args) {
        System.out.println(getRunCmd(new File("d:\\temp\\3")));
    }

	public static void update(String appName, final P0 beforeClose,
			String updateSite, Proxy proxy, long currentVersionBuildTime, P1<String> statusF) {
		InputStream in;
		if (updateSite.startsWith("http://")) {
			try {
				
				URLConnection conn = HttpUtil.makeIEGetConn(
						updateSite + "/latest?app=" + appName, 
						proxy);
				
				if (currentVersionBuildTime < Long.parseLong(IOUtil.inputStreamToString(conn.getInputStream()))) {
					// Update
					conn = HttpUtil.makeIEGetConn(
							"http://" + updateSite + "/down?app=" + appName,
							proxy);
					statusF.e("Updating");
					in = conn.getInputStream();
				} else {
					statusF.e("System is up-to-date");
					return;
				}
			} catch (MalformedURLException e1) {
				throw new RuntimeException(e1);
			} catch (FileNotFoundException e1) {
				statusF.e("System is up-to-date");
				return;
			} catch (IOException e1) {
				statusF.e(e1.getMessage());
				return;
			}
		} else {
			File file = FileUtil.newestFile(updateSite.replaceAll("^dir://", ""));
			
			if (file==null) {
				statusF.e("Update repository is blank");
				return;
			}
			
			if (currentVersionBuildTime < file.lastModified()) {
				// Update
				statusF.e("Updating");
				in = FileUtil.fileInputStream(file);
			} else {
				statusF.e("System is up-to-date");
				return;
			}
		}
		

		FileUtil.writeToFile(in, new File("data/applyingPatches/latest.zip"));
		statusF.e("Updated sucessfully, restarting");
		if (beforeClose != null) {
			beforeClose.e();
		}
	}

    
}
