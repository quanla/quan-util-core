package qj.util;

import java.awt.Desktop;
import java.io.*;
import java.util.*;
import java.util.zip.*;

import qj.util.funct.*;
import qj.util.funct.Fs.F1Cache;

public class FileUtil {

	public static F1<File, Boolean> csv = isExtention("csv");
    public static F1<File, Boolean> svn = isName(".svn");
    public static F1<File, Boolean> dsStore = isName(".DS_Store");
    public static F1<File, Boolean> thumbsDb = isName("Thumbs.db");
    
	@SuppressWarnings("unchecked")
	public static F1<File, Boolean> filter = Fs.and(Fs.not(svn), Fs.not(dsStore), Fs.not(thumbsDb));
	
	public static String md5(File file) {
		InputStream in = fileInputStream(file);
	    return IOUtil.md5(in);
	}
	
	public static F1<File,String> getName = new F1<File,String>() {public String e(File obj) {
		return obj.getName();
	}};

	public static FileOutputStream fileOutputStream(String path, boolean append) {
		try {
			return new FileOutputStream(path, append);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static FileOutputStream fileOutputStream(File path, boolean append) {
		try {
			return new FileOutputStream(path, append);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	public static FileOutputStream fileOutputStreamUTF8(String path) {
		try {
			FileOutputStream out = fileOutputStream(path, false);
			out.write(0xEF);
			out.write(0xBB);
			out.write(0xBF);
			return out;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
//	public static FileOutputStream fileOutputStreamUTF1(String path) {
//		try {
//			FileOutputStream out = fileOutputStream(path, false);
//			out.write(0xEF);
//			out.write(0xBB);
////			out.write(0xBF);
//			return out;
//		} catch (FileNotFoundException e) {
//			throw new RuntimeException(e);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}
	
	
    public static class JunkInput extends RuntimeException {
		public JunkInput() {
			super();
		}
	
		public JunkInput(String message, Throwable cause) {
			super(message, cause);
		}
	
		public JunkInput(String message) {
			super(message);
		}
	
		public JunkInput(Throwable cause) {
			super(cause);
		}
	}

    private static final F1<String,File> STRING_TO_FILE = new F1<String,File>() {public File e(String path) {
        return new File(path);
    }};
	public static final F1<File,Boolean> isFile = new F1<File, Boolean>() {public Boolean e(File obj) {
		return obj.isFile();
	}};

    public static F1<File, Boolean> isExtention(final String ext) {
        final String endsWith = "." + ext.toUpperCase();
        return new F1<File, Boolean>() {public Boolean e(File file) {
            return file != null && file.exists() && file.isFile() && file.getName().toUpperCase().endsWith(endsWith);
        }};
    }
    public static F1<File, Boolean> isName(final String name) {
        final String equals = name.toUpperCase();
        return new F1<File, Boolean>() {public Boolean e(File file) {
            return file != null && file.exists() && file.getName().toUpperCase().equals(equals);
        }};
    }
    
    public static Collection<File> findFiles(File dir, F1<File, Boolean> filter) {
        return Cols.filter(Arrays.asList(dir.listFiles()), filter);
    }

    /**
     * Recursive
     * @param path
     * @param f
     */
    public static void eachFile(File path, P1<File> f) {
    	eachFile(path, Fs.<File, String>p2(f));
    }

    public static void eachFile(File path, P2<File, String> f) {
        eachFile(path, f, null);
    }

    public static void eachFile(File path, P2<File, String> f, F1<File, Boolean> exclude) {
    	eachFile(path, Fs.f2(f, true), exclude);
    }
    public static void eachFile(File path, F2<File, String,Boolean> f, F1<File, Boolean> exclude) {

        ArrayList<String> relPath = new ArrayList<String>();

        if (path.isFile()) {
            f.e(path, Cols.join(relPath, File.separator));
        } else {
            if (!eachFileInDir(path, f, relPath, exclude)) return;
        }
    }
    public static void eachFile(Collection<File> paths, P2<File, String> f) {
        eachFile(paths, f, null);
    }
    public static void eachFile1(Collection<String> paths, P2<File, String> f) {
        eachFile(Cols.yield(paths, STRING_TO_FILE), f, null);
    }

    public static void eachFile(Collection<File> paths, P2<File, String> f, F1<File, Boolean> exclude) {
    	eachFile(paths, Fs.f2(f, true), exclude);
    }
    public static void eachFile(Collection<File> paths, F2<File, String,Boolean> f, F1<File, Boolean> exclude) {
        if (paths == null) {
            return;
        }
        ArrayList<String> relPath = new ArrayList<String>();
        for (File path: paths) {
            if (exclude != null && exclude.e(path)) {
//            	System.out.println("Excluded " + path);
                continue;
            }
//        	System.out.println("Accepted " + path);

            if (path.isFile()) {
                f.e(path, Cols.join(relPath, File.separator));
            } else {
                relPath.add(path.getName());
                if (!eachFileInDir(path, f, relPath, exclude)) return;
                relPath.remove(relPath.size() - 1);
            }
        }
    }

    private static boolean eachFileInDir(File path, F2<File, String,Boolean> f, ArrayList<String> relPath, F1<File, Boolean> exclude) {
        if (!path.exists() || !path.isDirectory()) {
            throw new RuntimeException("Invalid path: " + path);
        }
        for (File child : path.listFiles()) {
            if (exclude != null && exclude.e(child)) {
//            	System.out.println("Excluded " + child);
                continue;
            }
//        	System.out.println("Accepted " + child);

            if (child.isFile()) {
                if (!f.e(child, Cols.join(relPath, File.separator))) return false;
            } else {
                relPath.add(child.getName());
                if (!eachFileInDir(child, f, relPath, exclude)) return false;
                relPath.remove(relPath.size() - 1);
            }
        }
        return true;
    }

    public static File createTempFile() {
        try {
            return File.createTempFile("qjTemp", "dat");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<File> toFiles(List<String> filePaths) {
        return Cols.yield(filePaths, STRING_TO_FILE);
    }
	public static P0 out(final P1<OutputStream> outf, final File file) {
		return new P0() {public void e() {
			try {
				FileOutputStream fout = new FileOutputStream(file);
				outf.e(fout);
				IOUtil.close(fout);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}};
	}

    public static long crc(File file, final long[] size) {
        size[0] = 0;
        final CRC32 crc32 = new CRC32();
        IOUtil.eachBuffer(fileInputStream(file), new P2<byte[], Integer>() {public void e(byte[] buffer, Integer read) {
            size[0] += read;
            crc32.update(buffer, 0, read);
        }});
        return crc32.getValue();
    }
	public static F1<File, Boolean> modifyBefore(final Date date) {
		return new F1<File, Boolean>() {
			
			public Boolean e(File file) {
//				System.out.println("Checking file: " + file);
				boolean ret = file.lastModified() < date.getTime();
//				System.out.println("ret=" + ret);
				return ret;
			}
		};
		
	}

    public static void eachLine(String path, P1<String> f, int limit) {
        eachLine(new File(path), f, limit);
    }
    /**
     * 
     * @param path
     * @param f return true to continue
     */
    public static void eachLine(String path, F1<String, Boolean> f) {
        eachLine(new File(path), f);
    }
    public static void eachLine(File file, P1<String> f) {
    	eachLine(file, Fs.f1(f, true));
    }

    /**
     * 
     * @param file
     * @param f
     */
    public static void eachLine(File file, F1<String, Boolean> f) {
		ZipFile zipFile = null;
		InputStream in = null;
		try {
			if (file.getName().endsWith(".gz")) {
				in = new GZIPInputStream(fileInputStream(file));
			} else if (file.getName().endsWith(".zip")) {
				zipFile = new ZipFile(file);
				Enumeration<? extends ZipEntry> en = zipFile.entries();
				while (en.hasMoreElements()) {
					ZipEntry entry = en.nextElement();
					in = zipFile.getInputStream(entry);
					break;
				}
			}
			
			if (in == null) {
				in = fileInputStream(file);
			}
			
	        IOUtil.eachLine(in, f);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtil.close(in);
			IOUtil.close(zipFile);
		}
    }
    
    /**
     * 
     * @param file
     * @param f
     * @param limit if -1: unlimited
     */
    public static void eachLine(File file, P1<String> f, int limit) {
        InputStream fi;
		try {
			fi = file.getName().endsWith(".gz") ? new GZIPInputStream(fileInputStream(file)) : fileInputStream(file);
	        IOUtil.eachLine(fi, f, limit);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }

    public static FileInputStream fileInputStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static FileInputStream fileInputStream(String file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Querier querier(final File file, final int limit) {
        return new Querier() {
            public void through(P1<String> f) {
                eachLine(file, f, limit);
            }

            public String get(int i) {
                return getLine(i, file);
            }

            public String end(P1<Integer> lineIndex) {
                return lastLine(file, lineIndex);
            }

            @SuppressWarnings("unused")
			public File getFile() {
                return file;
            }

            public boolean equals(Object o) {
                if (o == null ||this.getClass() != o.getClass()) {
                    return false;
                }

                return Fs.f0("getFile", o).e().equals(file);
            }
        };
    }

    public static String getLine(int lineIndex, File file) {
        if (lineIndex <0) {
            throw new IndexOutOfBoundsException("Invalid line index: " + lineIndex);
        }

        FileInputStream fi = fileInputStream(file);
        return IOUtil.getLine(fi, lineIndex);
    }


    public static String lastLine(File file, P1<Integer> index) {
        FileInputStream fi = fileInputStream(file);
        return IOUtil.lastLine(fi, index);
    }

    /**
     * Empty a file's content
     * @param file
     */
    public static void clear(String file) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
			throw new RuntimeException(e);
		}
    }

    public static F0<String> lineReader(String path) {
        try {
            return IOUtil.lineReader(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static int indexOf(final String str, final String path) {
        final int[] index = {0};
        try {
            FileUtil.eachLine(path, new P1<String>() {public void e(String line) {
                int pos = line.indexOf(str);
                if (pos > -1) {
                    throw new Error("" + index[0] + pos); // TODO Lazy :D
                } else {
                    index[0] += path.length();
                }
            }});
            return -1;
        } catch (Error e) {
            return Integer.parseInt(e.getMessage());
        }
    }

    private static void eachLine(String path, P1<String> p1) {
        eachLine(path, p1, -1);
    }

    public static File search(String fileName, File near) {
        if (near.exists() && near.isDirectory()) {
            File file = new File(near, fileName);
            if (file.exists()) {
                return file;
            }
            file = new File(near, new File(fileName).getName());
            if (file.exists()) {
                return file;
            }
        }

        File parentFile = near.getParentFile();
        if (parentFile != null) {
            return search(fileName, parentFile);
        } else {
            return null;
        }
    }
	public static void eachLineReversed(File file, P1<String> p1) {
		RandomAccessFile f = null;
		try {
			f = new RandomAccessFile(file, "r");
			long[] index = {f.length() - 1};
			
			String line;
			while ((line = readLineBackward(index, f)) != null) {
				p1.e(line);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtil.close(f);
		}
	}
	private static String readLineBackward(long[] index, RandomAccessFile f) throws IOException {
		ByteArrayOutputStream bo = new ByteArrayOutputStream(1024);
		
		boolean reading = false;

		while (true) {
			if (index[0] == -1) {
				if (bo.size() > 0) {
					return toStringReversed(bo);
				} else {
					return null;
				}
			}
			
			f.seek(index[0]);
			int b = f.read();
	
			if (!reading) {
				// -1 ***
				if ((b=='\r') || (b =='\n')) {
				} else {
					reading = true;
					bo.write(b);
				}
				index[0]--;
			} else {
				if (b=='\r' || b =='\n' || b == -1) {
					return toStringReversed(bo);
				} else {
					bo.write(b);
					index[0]--;
				}
			}
		}
	}
	private static String toStringReversed(ByteArrayOutputStream bo) {
		byte[] byteArray = bo.toByteArray();
		ArrayUtil.reverse(byteArray);
		
		return new String(byteArray);
	}
	public static F1Cache<String, OutputStream> fileOutCache(final String dir, final String ext) {
		return fileOutCache(dir, ext, null);
	}
	
	public static F1Cache<String, OutputStream> fileOutCache(final String dir, final String ext, P1<OutputStream> decor) {
		return new Fs.F1Cache<String, OutputStream>(new F1<String, OutputStream>() {public OutputStream e(String entryCode){
			String file = dir + "/" + entryCode + "." + ext;
	        FileUtil.mkParentDirs(file);
	        try {
	            return new FileOutputStream(file);
	        } catch (FileNotFoundException e) {
	            throw new RuntimeException(e);
	        }
	    }}, decor);
	}
	
	public static void search(String filePtn, P1<File> p1) {
		filePtn = filePtn.replaceAll("\\\\", "/");
		int pos = filePtn.indexOf("*");
		File dir;
		String ptn;
		if (filePtn.charAt(pos - 1) == '/') {
			dir = new File(filePtn.substring(0, pos - 1));
			ptn = filePtn.substring(pos).replaceAll("\\.", "\\\\.").replaceAll("\\*", "[^.]*");
		} else {
			int lastIndexOf = filePtn.lastIndexOf('/', pos);
			dir = new File(filePtn.substring(0, 
					lastIndexOf
			));
			ptn = filePtn.substring(lastIndexOf + 1).replaceAll("\\.", "\\\\.").replaceAll("\\*", "[^.]*");
		}
		
		for (File file : dir.listFiles()) {
			if (file.getName().matches(ptn)) {
				p1.e(file);
			}
		}
	}
	
	public static Collection<File> search(String filePtn) {
		ArrayList<File> ret = new ArrayList<File>();
		search(filePtn, Fs.store(ret));
		return ret;
	}
	public static String firstLine(File file) {
		return getLine(0, file);
	}
	
	public static P1<String> writeToFileF(final F0<String> pathF) {
		return new P1<String> () {public void e(String content) {
			writeToFile(content, pathF.e(), "UTF-8");
		}};
	}
	public static void findLine(String dir, final List<F1<String, Boolean>> list1) {
		final List<F1<String, Boolean>> list = new ArrayList<F1<String, Boolean>>(list1);
	
		F1<String, Boolean> e = lineF(list);
		
		for (File file : new File(dir).listFiles()) {
			if (file.isDirectory()) {
				continue;
			}
			
			eachLine(file, e);
		}
	}
	public static void findLine(File file, final List<F1<String, Boolean>> list) {
//		final List<F1<String, Boolean>> list = list1;//new ArrayList<F1<String, Boolean>>(list1);
	
		F1<String, Boolean> e = lineF(list);
		eachLine(file, e);
	}
	public static F1<String, Boolean> lineF(final List<F1<String, Boolean>> list) {
		return new F1<String, Boolean>() {public Boolean e(String line) {
			if (list.isEmpty()) {
				return false;
			}
			try {
				@SuppressWarnings("unused")
				boolean lineUsed = false;
				for (Iterator<F1<String, Boolean>> iterator = list.iterator(); iterator.hasNext();) {
					F1<String, Boolean> f = iterator.next();
				
					if (f.e(line)) {
						iterator.remove();
						lineUsed = true;
					}
				}
//				if (lineUsed == false) {
//					System.out.println("Line unused: " + line);
//				}
			} catch (JunkInput e) {
				System.out.println(e.getMessage());
			}
			return true;
		}};
	}
	public static FileOutputStream out(File file, boolean append) {
		try {
			return new FileOutputStream(file, append);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static File up(String goUp, String childPath) {
		File file = new File(System.getProperty("user.dir"));
//		System.out.println(System.getProperties());
//		System.out.println(file.getParentFile());
		while (((file=file.getParentFile()) != null) && !file.getName().equals(goUp));
//		System.out.println(file);
		return new File(file,childPath);
	}
	public static Collection<File> getChildFiles(File file) {
		ArrayList<File> childs = new ArrayList<File>();
//		System.out.println(file);
		for (File child : file.listFiles()) {
			if (child.isDirectory()) {
				childs.addAll(getChildFiles(child));
			} else {
				childs.add(child);
			}
//			childs.add(file)
		}
		return childs;
	}
	public static F1<File, String> getRelPath(final File from) {
		final String fromAbsolutePath = from.getAbsolutePath();

		return new F1<File, String>() {public String e(File file) {
			String fileAbsolutePath = file.getAbsolutePath();
			if (fileAbsolutePath.startsWith(fromAbsolutePath)) {
				return fileAbsolutePath.substring(fromAbsolutePath.length() + 1);
			} else {
				return fileAbsolutePath;
			}
		}};
	}
	
	public static List<File> getFiles(String inputs) {
		ArrayList<File> ret = new ArrayList<File>();
		for (String input : inputs.trim().split("\\s*,\\s*")) {
			File file = new File(input);
			if (!file.exists()) {
				continue;
			} else if (file.isFile()) {
				ret.add(file);
			} else {
				for (File child : file.listFiles()) {
					if (child.isFile()) {
						ret.add(child);
					}
				}
			}
		}
		return ret;
	}
	public static void removeLines(final Collection<Integer> lines, File file) {
		changeLines(lines, file, Fs.<String, String>f1((String)null));
	}
	public static void changeLines(final Collection<Integer> lines, File file, final F1<String,String> f) {
		try {
			File tempFile = tempFile(file);
			final PrintStream out = new PrintStream(tempFile);
			final int[] index = {-1};
			eachLine(file, new F1<String, Boolean>() {public Boolean e(String line) {
				index[0]++;
				if (!lines.contains(index[0])) {
					out.println(line);
				} else {
					String result = f.e(line);
					if (result != null) {
						out.println(result);
					}
				}
				return true;
			}});
			IOUtil.close(out);
			
			file.delete();
			tempFile.renameTo(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	private static File tempFile(File file) {
		return new File(file.getPath()+ ".temp123");
	}
	public static Douce<File, File> commonParents(File f1, File f2) {
		
		if (f1 != null
				&& f2 != null
				&& f1.getName().equals(f2.getName())) {
			return commonParents(f1.getParentFile(), f2.getParentFile());
		} else {
			return new Douce<File, File>(f1, f2);
		}
	}
	
	public static boolean isChild(File child, File folder) {
		if (folder==null) {
			return true;
		}
		
		File directParent = child.getParentFile();
		if (directParent==null) {
			return false;
		}
		
		if (directParent.equals(folder)) {
			return true;
		}
		
		return isChild(directParent, folder);
	}
	public static File newestFile(String dir) {
		Long max = null;
		File latest = null;
		if (dir!=null) {
			for (File file : new File(dir).listFiles()) {
				long lastModified = file.lastModified();
				if (max == null || lastModified > max) {
					max = lastModified;
					latest = file;
				}
			}
		}
		return latest;
	}
	public static File createTempFile(String pre, String sub) {
		try {
			return File.createTempFile(pre, sub);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void editFile(File file, F1<String,String> f) {
		writeToFile(f.e(readFileToString(file)), file);
	}
	public static void copy(String file, final String targetDir) {
		copy(new File(file), new File(targetDir));
	}
	
	public static void copy(File file, final File targetDir) {
		if (file.isFile()) {
			targetDir.mkdirs();
			copyFile(file, new File(targetDir,file.getName()));
		} else {
			eachFile(file, new P2<File,String>() {public void e(File f, String path) {
				copy(f, new File(targetDir,path ));
			}});
		}
	}

	public static OutputStream fileOutputStream(File file) {
		return fileOutputStream(file, false);
	}

	public static void replaceContent(String path, F1<String, String> f1) {
		writeToFile(f1.e(readFileToString(path)), path);
	}
	
	public static P0 search(final String pattern, final String rootDir, final P1<File> foundFile) {
		final boolean[] cont = {true};
		
		ThreadUtil.run(new P0() {public void e() {
			eachFile(new File(rootDir), new F2<File, String,Boolean>() {public Boolean e(File file, String b) {
				if (patternMatch(file, pattern)) {
					foundFile.e(file);
				}
				
				return cont[0];
			}}, null);
		}});

		return new P0() {public void e() {
			cont[0] = false;
		}};
	}

	public static F2<String, P1<File>, P0> searchF(final String rootDir) {
		final TreeMap<String, List<File>> index = new TreeMap<String, List<File>>();
		eachFile(new File(rootDir), new F2<File, String,Boolean>() {public Boolean e(File file, String relPath) {
			Cols.putMulti(file.getName(), file, index);
			
			addZipEntries(file,index);
			
			return true;
		}}, null);
		
		return new F2<String, P1<File>, P0>() {public P0 e(final String pattern, final P1<File> foundFile) {

			final boolean[] cont = {true};
			
			List<File> files = index.get(pattern);
			if (files!=null) {
				Cols.each(files, foundFile);
			}

			return new P0() {public void e() {
				cont[0] = false;
			}};
		}};
		
	}
	
	protected static void addZipEntries(File file, TreeMap<String, List<File>> index) {
		String name = file.getName().toLowerCase();
		if (name.endsWith(".zip") || name.endsWith(".jar")) {
			try {
				ZipFile zipFile = new ZipFile(file);
				for (ZipEntry entry : Cols.iterable(zipFile.entries())) {
					Cols.putMulti(new File(entry.getName()).getName(), new File(file.getPath() + "!" + entry.getName()), index);
				}
				IOUtil.close(zipFile);
			} catch (ZipException e) {
				throw new RuntimeException("Error opening file " + file, e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static boolean patternMatch(File file, String pattern) {
		return file.getName().equals(pattern);
	}

	public static void copyNice(File file, File dir) {
		
		File targetFile = new File(dir, file.getName());
		if (!targetFile.exists()) {
			copyFile(file, targetFile);
		} else {
			String name = rejectExtFileName(file.getName());
			String ext = getFileExt(file);
			
			for (int i = 0; ; i++) {
				targetFile = new File(dir, name + "_" + (i+1) + "." + ext);
				if (!targetFile.exists()) {
					copyFile(file, targetFile);
					return;
				}
			}
		}
	}

	public static File lastUpdatedFile(File dir, final F1<File,Boolean> filterF) {
		final long[] maxLastModified = {0};
		final File[] maxLastModifiedFile = {null};
		eachFile(dir, new P1<File>() {public void e(File file) {
			long lastModified = file.lastModified();
			if (lastModified > maxLastModified[0] && filterF.e(file)) {
				maxLastModified[0] = lastModified;
				maxLastModifiedFile[0] = file;
			}
		}});
		
		return maxLastModifiedFile[0];
	}

	public static P0 onTextAdded(String path, final P1<String> f) {
		try {
			final RandomAccessFile raf = new RandomAccessFile(new File(path), "r");
			
			final long oldLength[] = {raf.length()};
			raf.seek(oldLength[0]);
			
			final boolean[] interrupted = {false};
			
			ThreadUtil.run(new P0() {public void e() {
				try {
					while (!interrupted[0]) {
						
						long length = raf.length();
						if (length > oldLength[0]) {
							f.e(new String(IOUtil.readEnough((int)(length - oldLength[0]), raf)));
						}
						oldLength[0] = length;
						ThreadUtil.sleep(500);
					}
				} catch (IOException e1) {
					throw new RuntimeException(e1);
				}
			}});
			return new P0(){public void e() {
				IOUtil.close(raf);
				interrupted[0] = true;
			}};
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static P0 onChange(String path, final P0 p) {
		final File file = new File(path);
		final long[] lastModified = {file.lastModified()};
		final boolean[] interrupted = {false};
		
		ThreadUtil.run(new P0() {public void e() {
			while (!interrupted[0]) {
				ThreadUtil.sleep(2000);
				long lastModified2 = file.lastModified();
				if (lastModified2 != lastModified[0]) {
					lastModified[0] = lastModified2;
					p.e();
				}
			}
		}});
		
		return new P0() {public void e() {
			interrupted[0] = true;
		}};
	}

	public static String readFilePortionToString(File file, long from) {
		return new String(readFilePortion(file, from, file.length()));
	}

	public static byte[] readFilePortion(File file, long from, long to) {
		RandomAccessFile raf= null;
		try {
			raf = new RandomAccessFile(file, "r");
			raf.seek(from);
			return IOUtil.readEnough((int)(to-from), raf);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtil.close(raf);
		}
	}

	public static void copyDir(String from, final String to) {
		copyDir(from, to, null);
	}

	public static void copyDir(String from, final String to, final F1<File, Boolean> filter) {
		File fromDir = new File(from);
		final File toDir = new File(to);
		copyDir(fromDir, toDir, filter);
	}
	public static void copyDir(File fromDir, final File toDir) {
		copyDir(fromDir, toDir, null);
	}

	public static void copyDir(File fromDir, final File toDir, final F1<File, Boolean> filter) {
		toDir.mkdirs();
		eachFile(fromDir, new P2<File,String>() {public void e(File f, String path) {
			copy(f, new File(toDir, path));
		}}, filter==null ? null : Fs.not(filter));
	}

	public static P0 deleteF(final String path) {
		return new P0() {public void e() {
			delete(path);
		}};
	}

	public static boolean exists(String path) {
		return new File(path).exists();
	}

	public static boolean moveReplace(File from, File to) {
		if (!from.exists()) {
			return false;
		}
		to.delete();
		from.renameTo(to);
		return true;
	}
	
	

	private static final String FILE_SEPARATOR = System.getProperty("file.separator", "/");
	private static String currentDir;
	static {
		try {
			currentDir = new File(".").getCanonicalPath();
		} catch (IOException e) {
		}
	}
	
	/**
	 * 
	 * @param targetFile
	 * @param destFile
	 */
	public static void moveFile(String targetFile, String destFile) {
		File fileTarget = getFile(targetFile);
		fileTarget.renameTo(getFile(destFile));
	}
	
	private static File getFile(String path) {
		File file = new File(path);
		File file2;
		if (!file.exists()
				&& (file2 = new File(currentDir + FILE_SEPARATOR + path)).exists())
			return file2;
		return file;
	}

	/**
	 * @param file
	 * @return file name
	 */
	public static String getFileExt(File file) {
		String name = file.getName();
		int dotPos = name.lastIndexOf('.');
		if (dotPos == -1)
			return "";
		else
			return name.substring(dotPos + 1);
	}

	/**
	 * @param filePath
	 * @return file name
	 */
	public static String getFileName(String filePath) {
		return getFile(filePath).getName();
	}

	/**
	 * 
	 * @param fileName
	 * @return file name with no ext
	 */
	public static String rejectExtFileName(String fileName) {
		return fileName.substring(0, fileName.indexOf('.'));
	}

	/**
	 * Read a file path to a String
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String readFileToString(String path) {
		File fileToRead = getFile(path);
		return readFileToString(fileToRead);
	}

	/**
	 * 
	 * @param fileToRead
	 * @return
	 * @throws IOException
	 */
	public static byte[] readFileToBytes(File fileToRead) {
		try {
			return IOUtil4.readData(new FileInputStream(fileToRead));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * Read a file path to a String
	 * 
	 * @param fileToRead
	 * @return
	 * @throws IOException
	 */
	public static String readFileToString(File fileToRead) {
		return readFileToString(fileToRead, "UTF-8");
	}

	/**
	 * Read the file content to String.<br>
	 * The file content is decoded with given charset
	 * 
	 * @param fileToRead
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static String readFileToString(File fileToRead, String charset) {
		if (!fileToRead.exists()) {
			return null;
		}
		FileInputStream fileIn = null;
		try {
			fileIn = new FileInputStream(fileToRead);
			InputStreamReader in = charset != null ? new InputStreamReader(fileIn, charset) : new InputStreamReader(fileIn);
			
			return IOUtil.toString(in);
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtil.close(fileIn);
		}
	}
	
	public static String readFileToString2(File fileToRead, String charset) {
		BufferedReader br = null;
		try {
			if (charset != null)
				br = new BufferedReader(new InputStreamReader(new FileInputStream(fileToRead), charset));
			else
				br = new BufferedReader(new InputStreamReader(new FileInputStream(fileToRead)));
			StringBuffer sb = new StringBuffer((int) fileToRead.length());
			String tempRead;
			boolean first = true;
			while ((tempRead = br.readLine()) != null) {
				if (!first)
					sb.append('\n');
				sb.append(tempRead);
				if (first)
					first = false;
			}
			
			if (sb.length() > 0 && sb.charAt(0) == '\uFEFF') {
				sb.delete(0, 1);
			}
			
			return sb.toString();
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (br!=null)
				try {
					br.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
		}
	}

	public static void mkParentDirs(String filePath) {
		File file = getFile(filePath);
		mkParentDirs(file);
	}

	public static void writeToFile(String content, String filePath) {
		
		writeToFile(content, filePath, null);
	}
	
	public static void writeToFile(String content, String filePath, String charSet) {
	
		File file = getFile(filePath);
		
		writeToFile(content, file, charSet);
	}

	public static void writeToFile(String content, File file, String charSet) {
        try {
            File parentFile = file.getParentFile();
            if (parentFile!=null && !parentFile.exists() && !parentFile.mkdirs()) {
                throw new RuntimeException("Can not make parent dir");
            }

            PrintWriter printWriter;
            FileOutputStream fStream = new FileOutputStream(file);
            if (charSet == null) {
                printWriter = new PrintWriter(
                        fStream);
            } else {
                try {
                    printWriter = new PrintWriter(
                            new OutputStreamWriter( fStream, charSet) );
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                printWriter.print(content);
            } finally {
            	
                IOUtil4.close(printWriter);
                IOUtil4.close(fStream);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

	public static void appendToFile(String content, String filePath)
			throws IOException {
		
		FileOutputStream fileOut = null;
		PrintWriter printWriter = null;
		
		try {
			fileOut = new FileOutputStream(filePath, true);
			printWriter = new PrintWriter(fileOut);
			printWriter.print(content);
			printWriter.flush();
		} finally {
			if (printWriter!=null)
				printWriter.close();
			if (fileOut!=null)
				fileOut.close();
			
		}
	}

	public static void writeToFile(String content, File file) {
		writeToFile(content, file, null);
	}
	
	/**
	 * Attempt cleaning the folder content and delete the folder
	 * @param folderPath
	 * @return
	 */
	public static boolean rd(String folderPath) {
		File f = getFile(folderPath);
		
		return rd(f);
	}

	/**
	 * Attempt cleaning the folder content and delete the folder
	 * @param folder
	 * @return
	 */
	public static boolean rd(File folder) {
		if (folder.exists()
				&& folder.isDirectory()) {
			File[] files = folder.listFiles();
			// Delete content
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					rd(files[i]);
				} else {
					files[i].delete();
				}
			}
			
			// Delete the folder
			return folder.delete();
		} else {
			return false;
		}
	}

	public static void copyFile(File file, File outputFile) {
		mkParentDirs(outputFile);
		try {
			FileOutputStream out = new FileOutputStream(outputFile);
			IOUtil4.connect(new FileInputStream(file), out);
			IOUtil.close(out);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void writeToFile(byte[] content, File file) {
		File parentFile = file.getParentFile();
		if (parentFile!=null && !parentFile.exists()) {
			parentFile.mkdirs();
		}

        OutputStream out = null;
        try {
            out = new FileOutputStream(file);

            out.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtil4.close(out);
        }
    }

	public static int size(File f) throws IOException {
		return IOUtil4.count(new FileInputStream(f));
	}
	public static long sizeDir(File dir) {
		final long[] total = {0};
		eachFile(dir, new P1<File>() {public void e(File obj) {
			if (obj.isDirectory()) {
				return;
			}
			try {
				total[0] += size(obj);
			} catch (IOException e1) {
				throw new RuntimeException(e1.getMessage(), e1);
			}
		}});
		return total[0];
	}

	public static void writeToFile(InputStream in, File file) {
		FileOutputStream fout = null;
		mkParentDirs(file);
		try {
			fout = new FileOutputStream(file);
			IOUtil4.connect(in, fout);
		} catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtil4.close(fout);
		}
	}

	public static File mkParentDirs(File file) {
		if (file.getParentFile() != null && !file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		return file;
	}

	public static void play(File file) throws IOException {
		System.out.println("Opening " + file.getName());
		Desktop.getDesktop().open(file);
//		Runtime.getRuntime().exec(new String[] {"cmd", file.getName()}, null, file.getParentFile());
	}

	public static void delete(String filePath) {
		delete(new File(filePath));
	}
    /**
     * Delete file or folder
     * @param file
     */
	public static void delete(File file) {
		if (!file.exists()) {
			return;
		}
		
		if (file.isFile()) {
			file.delete();
			return;
		}
		
		deleteChilds(file);
		
		file.delete();
	}

	/**
	 * Delete all sub files and sub folders recursively
	 * @param dir
	 */
	public static void deleteChilds(String dir) {
		deleteChilds(new File(dir));
	}

	public static void deleteChilds(String dir, F1<File,Boolean> filter) {
		deleteChilds(new File(dir), filter);
	}

	public static void deleteChilds(File dir) {
		deleteChilds(dir, null);
	}
	public static void deleteChilds(File dir, F1<File,Boolean> filter) {
		File[] subFiles = dir.listFiles();
		
		if (subFiles != null) {
			for (int i = 0; i < subFiles.length; i++) {
				File f = subFiles[i];
				if (filter==null || filter.e(f)) {
					delete(f);
				}
			}
		}
	}

	public static byte[] readFileToBytes(String filePath) {
		return readFileToBytes(new File(filePath));
	}

	public static String[] readFilesToStrings(File[] listFiles) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < listFiles.length; i++) {
			File file = listFiles[i];
			
			list.add(readFileToString(file));
		}
		return (String[]) list.toArray(new String[] {});
	}


    public static void readFileOut(File path, OutputStream out) {
        try {
            IOUtil4.connect(new FileInputStream(path), out);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	public static Collection<InputStream> childFileInputStreams(File file, final String ext) {
		return Cols.yield(file.listFiles(), new F1<File,InputStream>() {public InputStream e(File obj) {
			if (ext != null && !obj.getName().endsWith("." + ext)) {
				return null;
			}
			return fileInputStream(obj);
		}});
	}
	
	public static void main(String[] args) {
		QFileUtil.toQFile(new File("/Users/quanle/Documents/Workon/qj-svn/commercial-apps/leap/scanner/data/vn"));
	}
	
	public static F1<String,String> fileStringGetter(final File dir) {
		return new F1<String, String>() {public String e(String key) {
			File file = new File(dir, key);
			if (!file.exists()) {
				return null;
			}
			return FileUtil.readFileToString(file);
		}};
	}
	public static P2<String,String> fileStringSetter(final File dir) {
		return new P2<String, String>() {public void e(String key, String value) {
			File file = new File(dir, key);
			FileUtil.writeToFile(value, file);
		}};
	}
	
	public static File getCanonicalFile(File file) {
		try {
			return file.getCanonicalFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
