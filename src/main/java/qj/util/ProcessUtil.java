package qj.util;

import static qj.util.IOUtil.NO_R;
import static qj.util.IOUtil.asyncConnect2;
import static qj.util.SwingUtil.show;

import java.awt.Dimension;
import java.awt.Window;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

import qj.util.funct.F1;
import qj.util.funct.F3;
import qj.util.funct.Fs;
import qj.util.funct.P0;
import qj.util.math.Range;

public class ProcessUtil {

    static HashSet<Process> processes = new HashSet<Process>();
    public static F3<String, String, String[], Process> exec = Fs.f3("exec1", ProcessUtil.class);
    public static Process exec1(String cmd, String dir, String... params) {
        if (params == null) {
            params = new String[0];
        }
        OutputStream output = System.out;

        ProcessBuilder builder = new ProcessBuilder();
//        builder.environment().put("", dir)
        if (dir!= null) {
            builder.directory(new File(dir));
            cmd = dir + "/" + cmd;
        } else {
        	dir = ".";
        }

        try {


            final Process p;
			if (SystemUtil.isWindows()) {
				p = builder.command(ArrayUtil.appendToArrayBegin(cmd, params)).start();
			} else {
				File execShFile = new File(dir + "/exec.sh");
//				if (!execShFile.exists()) {
					FileUtil.writeToFile(Cols.join(Arrays.asList(ArrayUtil.appendToArrayBegin(cmd, params)), " "), execShFile);
					try {
						Runtime.getRuntime().exec("chmod 777 exec.sh", null,
								new File(dir)).waitFor();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
//				}
				p = Runtime.getRuntime().exec("./exec.sh", null, new File(dir));
			}
        	
//            processes.add(p);

            if (output != null) {
                Thread thread = asyncConnect2(p.getInputStream(), output, NO_R);
                thread.setDaemon(true);
                thread.start();
                thread = asyncConnect2(p.getErrorStream(), output, NO_R);
                thread.setDaemon(true);
                thread.start();
            }

//            IOUtil.asyncConnect2(System.in, p.getOutputStream()).start();
//            try {
//                p.waitFor();
//            } catch (InterruptedException e) {
//                //
//            }
//            p.destroy();
//            Runtime.getRuntime().addShutdownHook(new Thread() {public void run() {
//                System.out.println("Process destroyed");
//                p.destroy();
//            }});
            return p;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	public static Process exec(String cmd) {
		return exec(cmd, false);
	}
	public static Process exec(String cmd, boolean asAdmin) {
		
		if (asAdmin) {
			String os = System.getProperty("os.name");
			if (os.startsWith("Windows Vista")
					|| os.startsWith("Windows 7")) {
				cmd = "data\\app\\elevate_" + System.getProperty("os.arch")
						+ ".exe " + wrap(cmd);
			}
		}
		Process process = exec(cmd, ".");
//		receiveOutput(process);
		return process;
	}

	public static Process exec(String cmd, String dir) {
		return exec(cmd, null, dir);
	}
	public static Process exec(String cmd, String[] envp, String dir) {
		try {
			cmd = shell(cmd, dir);
//			System.out.println(cmd);
			
			Process process =
					Runtime.getRuntime().exec(
							cmd, envp, new File(dir)
					);
//			receiveOutput(process);

			return process;
		} catch (IOException e) {
			throw new RuntimeException(cmd, e);
		}
		
	}
	public static Process exec(String[] cmd, String[] envp, String dir) {
		try {
			Process process =
					Runtime.getRuntime().exec(
							cmd, envp, new File(dir)
							);
//			receiveOutput(process);
			
			return process;
		} catch (IOException e) {
			throw new RuntimeException(Arrays.toString(cmd), e);
		}
		
	}
	public static String shell(String cmd, String dir) {
		String baseCmd = baseCmd(cmd);
		if (!SystemUtil.isWindows()) {
			if (new File(dir + "/" + baseCmd).exists()) {
				cmd = "./" + cmd;
			} else if (new File(dir + "/" + baseCmd + ".sh").exists()) {
				cmd = "./" + baseCmd + ".sh" + cmd.substring(baseCmd.length());
			}
		} else if (!new File(dir + "/" + baseCmd).exists()) {
			if (SystemUtil.isWindows()) {
				if (existInPathWindows(baseCmd, dir)) {
					cmd = cmd.replaceAll("^[^\\s]+(.*)", "cmd /c " + baseCmd.replaceAll("/", "\\\\\\\\") + ".bat$1");
				}
			}
		}
		return cmd;
	}
	private static String baseCmd(String cmd) {
		return cmd.split("\\s+")[0];
	}
	private static boolean existInPathWindows(String baseCmd, String dir) {
		if (new File(dir + "/" + baseCmd + ".bat").exists()) {
			return true;
		}
		for (String path : System.getenv("PATH").split("\\s*;\\s*")) {
			if (new File(path + "/" + baseCmd + ".bat").exists()) {
				return true;
			}
		}
		return false;
	}
	public static void receiveOutput(Process process) {
		IOUtil.asynConnect(process.getInputStream(), System.out);
		IOUtil.asynConnect(process.getErrorStream(), System.err);
	}
	public static String shell(String shell) {
		return shell(shell, ".");
//		return (SystemUtil.isWindows() ? "cmd /c " + shell + ".bat" : "./" + shell);
	}


    //    public static void cmd(String cmd, String dir) {
//        ProcessBuilder builder = new ProcessBuilder();
//        builder.directory(new File(dir));
//
//        try {
//            final Process p = builder.command("cmd").start();
//            processes.add(p);
//            OutputStream out = p.getOutputStream();
//            out.write(cmd.getBytes());
//            out.write('\n');
//            out.write("exit\n".getBytes());
//            out.flush();
//
//            Thread thread = IOUtil.asyncConnect2(p.getInputStream(), System.out);
//            thread.setDaemon(true);
//            thread.start();
//            thread = IOUtil.asyncConnect2(p.getErrorStream(), System.out);
//            thread.setDaemon(true);
//            thread.start();
//
////            IOUtil.asyncConnect2(System.in, p.getOutputStream()).start();
//
//            Runtime.getRuntime().addShutdownHook(new Thread() {public void run() {
//                System.out.println("Process destroyed");
//                p.destroy();
//            }});
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
	
	public static String wrap(String cmd) {
		if (cmd.contains(" ")) {
			return "\"" + cmd + "\"";
		} else {
			return cmd;
		}
	}
	public static void onExit(final Process process, final P0 p0) {
		if (process==null) {
			p0.e();
			return;
		}
		ThreadUtil.run(new P0() {public void e() {
			try {
				process.waitFor();
			} catch (InterruptedException e1) {
				throw new RuntimeException(e1);
			}
			p0.e();
		}});
	}
	
	public static String collectOutput(Process process) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtil.asynConnect(process.getInputStream(), out);
		IOUtil.asynConnect(process.getErrorStream(), out);
		final Object lock = new Object();
		ProcessUtil.onExit(process, ThreadUtil.notifyAllF(lock));
		ThreadUtil.wait(lock);
		String output = out.toString();
		return output;
	}
	public static P0 showConsole(final Process process, Window window) {
		return showConsole("Console", process, window);
	}
	public static P0 showConsole(final String title, final Process process, Window window) {
		
		final JTextArea ta = new JTextArea(21, 80);

		SwingUtil.readToTA(process.getInputStream(), ta);
		SwingUtil.readToTA(process.getErrorStream(), ta);
		SwingUtil.console(ta);

		SwingUtil.onPopup(ta, new F1<Range, JPopupMenu>() {
			public JPopupMenu e(Range obj) {
				JPopupMenu menu = new JPopupMenu();
				menu.add(SwingUtil.menuItem("Clear", SwingUtil.async( new P0() {public void e() {
					ta.setText("");
				}})));
				menu.add(SwingUtil.menuItem("Terminate", new P0() {public void e() {
					process.destroy();
				}}));
				return menu;
			}
		});
		
		final JDialog dialog = SwingUtil.dialog(ta, title, window);
		dialog.setPreferredSize(new Dimension(400, 400));
		onExit(process, new P0() {public void e() {
			dialog.setTitle(title + " <terminated>");
		}});
		
		show(dialog);
		
		return new P0() {public void e() {
			dialog.dispose();
		}};
	}
	
	public static void main(String[] args) {
		Clipboard.copy("He he he");
	}
	public static int waitFor(Process p) {
		try {
			return p.waitFor();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	public static boolean isTerminated(Process process) {
		try {
			process.exitValue();
			return true;
		} catch (IllegalThreadStateException e) {
			return false;
		}
		
	}

}
