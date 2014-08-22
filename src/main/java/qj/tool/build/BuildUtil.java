package qj.tool.build;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import qj.util.Cols;
import qj.util.FileUtil;
import qj.util.ObjectUtil;
import qj.util.SystemUtil;
import qj.util.ZipUtil;
import qj.util.funct.F1;
import qj.util.funct.Fs;
import qj.util.funct.P1;
import qj.util.funct.P2;

@SuppressWarnings("unchecked")
public class BuildUtil {
    public static final String INCLUDE_FILES_KEY = "includeFiles";
    public static final String JAVAW = "javaw";
    public static final String JVMARGS = "jvmargs";
    public static final String ARGS = "args";

    public static void writeZipFile(Collection<String> classpaths, String mainClass, OutputStream out) {
        writeZipFile(classpaths, mainClass, Cols.<String, Object>map(
        		BuildUtil.INCLUDE_FILES_KEY, BuildUtil.includeFiles(SystemUtil.getCurrentDir(), (List<String>)null)
	    ), out);
    }
    public static void writeZipFile(Collection<String> classpaths, String mainClass, OutputStream out, Map config) {
    	writeZipFile(classpaths, mainClass, Cols.merge( Cols.<String, Object>map(
    			BuildUtil.INCLUDE_FILES_KEY, BuildUtil.includeFiles(SystemUtil.getCurrentDir(), (List<String>)null)
    			), config), out);
    }

	public static File buildApp(Class<?> clazz) {
		File outputFile = buildApp(clazz,clazz.getSimpleName());
		return outputFile;
	}

	public static File buildApp(Class<?> clazz,String appName) {
		File zipFile = new File(appName+ ".zip");
		writeZipFile(SystemUtil.classPaths1(), clazz.getName(), FileUtil.fileOutputStream(zipFile,false));
		return zipFile;
	}
    
    public static void buildClassesJar() {
    	try {
			writeClassDirs(FileUtil.fileOutputStream("target/classes.jar", false), SystemUtil.classPaths1());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }



//	public static void buildApp(Class<BenjaminDeployMain> clazz) {
//		// TODO Auto-generated method stub
//		
//	}


    public static void writeWrappedAppZipFile(Collection<String> classpaths, String mainClass, Map<String, Object> config, OutputStream out) {
        try {

            final ZipOutputStream zipFile = new ZipOutputStream(out);
            zipFile.setLevel(9);

            // Put classpaths
            copyPaths(classpaths, zipFile, null);
            ArrayList<String> wrapperPaths = wrapperPaths(classpaths, zipFile);
            P2<File, String> zipWrite = ZipUtil.zipWrite(zipFile);

            List<File> includeFiles = (List<File>) config.get(INCLUDE_FILES_KEY);
            FileUtil.eachFile(includeFiles, zipWrite, FileUtil.svn);

            zipFile.putNextEntry(new ZipEntry("run.bat"));
            OutputStreamWriter writer = new OutputStreamWriter(zipFile);
            writer.write(
                    "@echo off\n" +
                    (Boolean.TRUE.equals(config.get(JAVAW)) ? "start javaw" : "java") + " " +
//                    Cols.join((Collection<Object>) config.get(JVMARGS), " ") + " " +   TODO
                    "-cp \"" + Cols.join(wrapperPaths, ";") + "\" " +
                    "qj.util.Wrapper " +
                    mainClass + " " +
                    Cols.join((Collection<Object>) config.get(ARGS), " ") +
                    " %*"
            );
            writer.flush();
            zipFile.closeEntry();

            zipFile.putNextEntry(new ZipEntry("run.sh"));
            writer = new OutputStreamWriter(zipFile);
            writer.write(
                    "java " +
                    "-cp \"" + Cols.join(wrapperPaths, ":") + "\" " +
                    "qj.util.Wrapper " +
                    mainClass + " " +
                    Cols.join((Collection<Object>) config.get(ARGS), " ")
            );
            writer.flush();
            zipFile.closeEntry();

            zipFile.flush();
            zipFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void writeZipFile(Collection<String> classpaths, String mainClass, Map<String, Object> config, OutputStream out) {
        try {

            final ZipOutputStream zipFile = new ZipOutputStream(out);
//            zipFile.setMethod(ZipOutputStream.STORED);

            // Put classpaths
            F1<File, Boolean> excludes = excludes(config.get("exclude"));
            
            final ArrayList<String> putPaths = copyPaths(classpaths, zipFile, excludes);
            P2<File, String> zipWrite = ZipUtil.zipWrite(zipFile);

            List<File> includeFiles = (List<File>) config.get(INCLUDE_FILES_KEY);
			FileUtil.eachFile(includeFiles, zipWrite, excludes);

            writeRunBat(mainClass, config, zipFile, putPaths);
            writeStartBat(mainClass, config, zipFile, putPaths);

            writeRunSh(mainClass, config, zipFile, putPaths);
            
            
            P1<P2<File, String>> zipWriteF = (P1<P2<File, String>>) config.get("zipWriteF");
            if (zipWriteF != null) {
            	zipWriteF.e(zipWrite);
            }
            
            zipFile.flush();
            zipFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	private static F1<File, Boolean> excludes(Object exclude) {
//		new File(".");
		F1<File, Boolean> ret = FileUtil.svn;
		if (exclude != null) {
			if (exclude instanceof String) {
//				System.out.println("Excluding " + new File((String) exclude));
				ret = Fs.or(ret, ObjectUtil.equalsF(new File((String) exclude).getAbsoluteFile()));
			} else if (exclude instanceof List) {
				List<String> excludes = (List<String>)exclude;
				F1<File, Boolean> excludeConds = Fs.or(Cols.yield(excludes, new F1<String,F1<File,Boolean>>() {public F1<File, Boolean> e(String file) {
					File canonicalFile = FileUtil.getCanonicalFile(new File(file));
					return new F1<File, Boolean>() {public Boolean e(File obj) {
						return ObjectUtil.equals(canonicalFile, FileUtil.getCanonicalFile(obj))
								|| ObjectUtil.equals(canonicalFile, FileUtil.getCanonicalFile(obj.getParentFile()));
					}};
				}}));
				ret = Fs.or(ret, excludeConds);
			}
		}
//		FileUtil.isChild(null, null)
		return ret;
	}
	public static String runCommand(Class<?> clazz) {
		return "java -cp " + Cols.join(SystemUtil.classPaths1(), SystemUtil.classpathSeparator()) + " " + clazz.getName();
	}

	private static void writeRunSh(String mainClass,
			Map<String, Object> config, final ZipOutputStream zipFile,
			final ArrayList<String> putPaths) throws IOException {
		zipFile.putNextEntry(new ZipEntry("run.sh"));
		OutputStreamWriter writer = new OutputStreamWriter(zipFile);
		writer.write(
				"cd \"$( cd \"$( dirname \"${BASH_SOURCE[0]}\" )\" && pwd )\"\n"
				);
		writer.write(
		        "java " +
		        Cols.join((Collection<Object>) config.get(JVMARGS), " ") + " " +
		        "-cp \"" + Cols.join(putPaths, ":") + "\" " +
		        mainClass + " " +
		        Cols.join((Collection<Object>) config.get(ARGS), " ")
		);
		writer.flush();
		zipFile.closeEntry();
	}

	private static void writeStartBat(String mainClass,
			Map<String, Object> config, final ZipOutputStream zipFile,
			final ArrayList<String> putPaths) throws IOException {
		zipFile.putNextEntry(new ZipEntry("start.bat"));
		OutputStreamWriter writer = new OutputStreamWriter(zipFile);
		writer.write(
		        "@echo off\n" +
		        "set PATH=%PATH%;C:\\Program Files (x86)\\Java\\jre6\\bin;C:\\Program Files (x86)\\Java\\jre7\\bin;C:\\Program Files\\Java\\jre7\\bin;C:\\Program Files\\Java\\jre6\\bin;C:\\Program Files\\Java\\jre1.6.0\\bin\n" +
		        "start javaw " +
		        Cols.join((Collection<Object>) config.get(JVMARGS), " ") + " " +
		        "-cp \"" + Cols.join(putPaths, ";") + "\" " +
		        mainClass + " " +
		        Cols.join((Collection<Object>) config.get(ARGS), " ") +
		        " %*"
		);
		writer.flush();
		zipFile.closeEntry();
	}
	
	private static void writeRunBat(String mainClass,
			Map<String, Object> config, final ZipOutputStream zipFile,
			final ArrayList<String> putPaths) throws IOException {
		zipFile.putNextEntry(new ZipEntry("run.bat"));
		OutputStreamWriter writer = new OutputStreamWriter(zipFile);
		writer.write(
		        "@echo off\n" +
		        "set PATH=%PATH%;C:\\Program Files (x86)\\Java\\jre6\\bin;C:\\Program Files (x86)\\Java\\jre7\\bin;C:\\Program Files\\Java\\jre7\\bin;C:\\Program Files\\Java\\jre6\\bin;C:\\Program Files\\Java\\jre1.6.0\\bin\n" +
		        "java " +
		        Cols.join((Collection<Object>) config.get(JVMARGS), " ") + " " +
		        "-cp \"" + Cols.join(putPaths, ";") + "\" " +
		        mainClass + " " +
		        Cols.join((Collection<Object>) config.get(ARGS), " ") +
		        " %*"
		);
		writer.flush();
		zipFile.closeEntry();
	}
    public static void writeSrcZipFile(List<File> includeFiles, OutputStream out) {
        try {

            final ZipOutputStream zipFile = new ZipOutputStream(out);
//            zipFile.setMethod(ZipOutputStream.STORED);

            // Put classpaths
            P2<File, String> zipWrite = ZipUtil.zipWrite(zipFile);

            FileUtil.eachFile(includeFiles, zipWrite, FileUtil.svn);

            zipFile.flush();
            zipFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ArrayList<String> wrapperPaths(
            Collection<String> paths,
            ZipOutputStream zipFile) {
        final ArrayList<String> putPaths = new ArrayList<String>();
        P2<File, String> zipWrite = ZipUtil.zipWrite(zipFile);
        for (final String path : paths) {
            File pathFile = new File(path);
            if (pathFile.isFile() && pathFile.getName().startsWith("quan-util")) {
                String relPath = "data/wrapper";
                putPaths.add(relPath + "/" + pathFile.getName());

                zipWrite.e(pathFile, relPath);
            }
        }
        return putPaths;
    }
    
    /**
     * Copy classpaths
     * @param excludes 
     */
    private static ArrayList<String> copyPaths(
            Collection<String> paths,
           final ZipOutputStream zipFile, F1<File, Boolean> excludes) throws IOException {
        final ArrayList<String> putPaths = new ArrayList<String>();
        P2<File, String> zipWrite = ZipUtil.zipWrite(zipFile);

        // Copy all .jar files to package
        for (final String path : paths) {
            File pathFile = new File(path);
            if (pathFile.isFile()) {
                String relPath = "data/lib";
                putPaths.add(relPath + "/" + pathFile.getName());
//                if (pathFile.getName().startsWith("jett")) {
//                	System.out.println();
//                }
                if (excludes == null || !excludes.e(pathFile)) {
                	zipWrite.e(pathFile, relPath);
                }
            }
            
        }

        String classesJarPath = "data/lib/classes.jar";
        putPaths.add(classesJarPath);
        zipFile.putNextEntry(new ZipEntry(classesJarPath));
        
        writeClassDirs(zipFile, paths);

        zipFile.closeEntry();
        return putPaths;
    }

	private static void writeClassDirs(final OutputStream out,
			Collection<String> paths) throws IOException {
		final JarOutputStream classesZipFile = new JarOutputStream(out);

        P2<File, String> classesJarWriteF = ZipUtil.zipWrite(classesZipFile);

        // If a classpath is dir, zip it and put to classes.jar
        for (final String path : paths) {
            File dir = new File(path);
            if (dir.isDirectory()) {
                FileUtil.eachFile(dir, classesJarWriteF, FileUtil.svn);
            }
        }
        classesZipFile.flush();
        classesZipFile.finish();
	}

    public static List<File> includeFiles(String dir, final List<String> includes) {
        return includeFiles(dir, new F1<File, Boolean>() {
            public Boolean e(File obj) {
                String fileName = obj.getName();
                return 
                		fileName.equals("data") || 
                		fileName.endsWith(".properties") || 
                		fileName.endsWith(".txt") || 
                		fileName.endsWith(".so") || 
                		fileName.endsWith(".dll") || 
                		(includes != null && includes.contains(fileName));
            }
        });
    }

    private static List<File> includeFiles(String dir, F1<File, Boolean> workDirInclude) {
        if (dir == null) {
            return Collections.emptyList();
        }
        ArrayList<File> files = new ArrayList<File>();
        for (File file : new File(dir).listFiles()) {
            if (workDirInclude.e(file)) {
                files.add(file);
            }
        }
        return files;
    }
}