package qj.util;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.jar.JarOutputStream;
import java.util.zip.*;

import qj.util.QFileUtil.QFile;
import qj.util.QFileUtil.QFileContent;
import qj.util.col.Tree;
import qj.util.funct.*;

public class ZipUtil {
    public static P2<File, String> zipWrite(final ZipOutputStream zipFile) {
        return zipWrite( zipFile, null );
    }
    
    public static ZipWriter zipWriter(final File outputFile) {
    	OutputStream fileOutputStream = FileUtil.fileOutputStream(outputFile);
		return zipWriter(fileOutputStream);
    }

	public static ZipWriter zipWriter(OutputStream fileOutputStream) {
		return new ZipWriter(new ZipOutputStream(fileOutputStream));
	}
    
    public static class ZipWriter {
		private ZipOutputStream out;

		public ZipWriter(ZipOutputStream out) {
			this.out = out;
		}

//		public void close() {
//			try {
//				out.close();
//			} catch (IOException e) {
//				throw new RuntimeException(e.getMessage(), e);
//			}
//		}

		public void writeEntry(String entryPath, P1<OutputStream> p1) {
            ZipEntry zipEntry = new ZipEntry(entryPath);
			try {
				out.putNextEntry(zipEntry);
				p1.e(out);
				out.closeEntry();
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
			
		}

		public void finish() {
			try {
				out.finish();
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
    }

    public static P2<File, String> zipWrite( final ZipOutputStream zipFile, final String inPath) {
        return new P2<File, String>() {public void e(File file, String relPath) {
            String path = Cols.join("/", inPath, relPath, file.getName()).replaceAll("\\\\", "/");
            try {
//            	System.out.println(path);
                ZipEntry zipEntry = new ZipEntry(path);
				zipEntry.setTime(file.lastModified());
//            	if (ZipUtil.isFileGZIP(file)) {
//            		zipEntry.setMethod(ZipEntry.STORED);
//            		zipEntry.setSize(FileUtil.size(file));
//            	}
				zipFile.putNextEntry(zipEntry);
                FileUtil.readFileOut(file, zipFile);
                zipFile.closeEntry();
            } catch (ZipException e) {
                if (e.getMessage().startsWith("duplicate entry")) {
                    System.out.println("Duplicated entry: " + path);
                    // skip
                } else {
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }};
    }

    public static P2<byte[], String> zipWriteBytes( final ZipOutputStream zipFile, final String inPath) {
        return new P2<byte[], String>() {public void e(byte[] content, String fpath) {
            String path = Cols.join("/", inPath, fpath).replaceAll("\\\\", "/");
            try {
//            	System.out.println(path);
                ZipEntry zipEntry = new ZipEntry(path);
//				zipEntry.setTime(file.lastModified());
//            	if (ZipUtil.isFileGZIP(file)) {
//            		zipEntry.setMethod(ZipEntry.STORED);
//            		zipEntry.setSize(FileUtil.size(file));
//            	}
				zipFile.putNextEntry(zipEntry);
				zipFile.write(content);
//                FileUtil.readFileOut(file, zipFile);
                zipFile.closeEntry();
            } catch (ZipException e) {
                if (e.getMessage().startsWith("duplicate entry")) {
                    System.out.println("Duplicated entry: " + path);
                    // skip
                } else {
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }};
    }
    public static void each(File file, P2<String, InputStream> p2) {
        try {
            ZipFile zipFile = new ZipFile(file);
            try {
                each(null, zipFile, p2);
            } finally {
                zipFile.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void each(String path, final ZipFile zipFile, final P2<String, InputStream> p2) {
    	each(path, zipFile, new P1<ZipEntry>() {public void e(ZipEntry z) {
            try {
				String eName = z.getName();
				p2.e(eName, zipFile.getInputStream(z));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}});
    }

    public static void each(String path, ZipFile zipFile, P1<ZipEntry> p1) {
        String pathUpperCase = path==null ? null : path.toUpperCase();

        for (ZipEntry z : Cols.iterable(zipFile.entries())) {
            String eName = z.getName();
			if (!z.isDirectory() && (path == null || eName.toUpperCase().startsWith(pathUpperCase))) {
                p1.e(z);
            }
        }
    }

	public static void copy(String path, ZipFile zipFile, File toDir) {
		try {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				
				if (entry.getName().startsWith(path)) {
					FileUtil.writeToFile(zipFile.getInputStream(entry),
									new File(toDir.getPath()
											+ entry.getName().substring(
													path.length())));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void gzip(File file) {
		try {
			GZIPOutputStream gzip = new GZIPOutputStream(new FileOutputStream(file.getPath() + ".gz"));
			FileInputStream in = new FileInputStream(file);
			IOUtil.dump(in, gzip);
			in.close();
			file.delete();
			gzip.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void write(String content, String path,
			ZipOutputStream zipOut) throws IOException {
		zipOut.putNextEntry(new ZipEntry(path));
		zipOut.write(content.getBytes());
		zipOut.closeEntry();
	}

	/**
	 * Copy a zip file's content into the zipOut stream
	 * @param zipFile
	 * @param zipOut
	 */
	public static void copy(ZipFile zipFile, ZipOutputStream zipOut) {
		try {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			
			while (entries.hasMoreElements()) {
				
				
				ZipEntry entry = entries.nextElement();

//				System.out.println(entry.getName());
				
				if (entry.isDirectory()) {
					continue;
				}

				zipOut.putNextEntry(new ZipEntry(entry.getName()));
				IOUtil.dump(zipFile.getInputStream(entry), zipOut);
				zipOut.closeEntry();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Deprecated
	public static InputStream entryInputStream(File file) {
		try {
			ZipFile zipFile = new ZipFile(file);
			Enumeration<? extends ZipEntry> en = zipFile.entries();
			while (en.hasMoreElements()) {
				ZipEntry entry = en.nextElement();
				return zipFile.getInputStream(entry);
			}
			return null;
		} catch (ZipException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void unzip(File zipFile, final File outputDir) {
		outputDir.mkdirs();
		each(zipFile, new P2<String, InputStream>() {public void e(String path, InputStream in) {
			File file = new File(outputDir,path);
			file.getParentFile().mkdirs();
			IOUtil.connectAndClose(in, FileUtil.fileOutputStream(file, false));
		}});
	}
	
	/**
	 * update ( zipFile to update, new File(new content file), "/path in the zip file of the content file"
	 * @param zipFile
	 * @param updatedFiles
	 */
	public static void update(File zipFile, Object... updatedFiles ) {
		final Map<String, File> fileMap = toFileMap(updatedFiles);
		
		update(zipFile, fileMap);
	}
	public static void update(File zipFile, File aliasDir, Collection<String> paths ) {
		final Map<String, File> fileMap = new HashMap<String, File>();
		for (String path : paths) {
			fileMap.put(path, new File(aliasDir, path));
		}
		update(zipFile, fileMap);
	}

	private static void update(File zipFile, final Map<String, File> fileMap) {
		try {
			File tempFile = File.createTempFile("qqq", ".zip");
			final ZipOutputStream tempZipOut = new ZipOutputStream(FileUtil.fileOutputStream(tempFile));
			
			each(zipFile, new P2<String, InputStream>() {public void e(String path, InputStream in) {
				if (fileMap.containsKey(path)) {
					return;
				}
				
			    dump(in, path, tempZipOut);
			}});

			for (Entry<String, File> entry : fileMap.entrySet()) {
			    dump(FileUtil.fileInputStream(entry.getValue()), entry.getKey(), tempZipOut);
			}
			
			IOUtil.close(tempZipOut);
			
			tempFile.renameTo(zipFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static Map<String,File> toFileMap(Object[] files) {
		HashMap<String, File> ret = new HashMap<String, File>();
		for (int i = 0; i < files.length; i+=2) {
			File file = (File) files[i];
			String path = (String) files[i+1];
			ret.put(path, file);
		}
		return ret;
	}

	public static void zip(File dir, File outputFile) {
		zip(dir, outputFile, null);
	}

	public static void zip(File dir, File outputFile, final F1<File,Boolean> filter) {
		if (!dir.exists() || !dir.isDirectory()) {
			throw new RuntimeException("Dir not found: " + dir.getPath());
		}
		
		final ZipOutputStream zipOutputStream = new ZipOutputStream(FileUtil.fileOutputStream(outputFile, false));
		zip(dir, zipOutputStream, filter);
	}

	public static void zip(File dir, final ZipOutputStream zipOutputStream,
			final F1<File, Boolean> filter) {
		FileUtil.eachFile(dir, zipWrite(zipOutputStream), filter==null ? null : Fs.not(filter));
		
		IOUtil.close(zipOutputStream);
	}

	public static void jar(File dir, File outputFile) {
		try {
			final JarOutputStream jarOutputStream = new JarOutputStream(FileUtil.fileOutputStream(outputFile, false));
			
            FileUtil.eachFile(dir, zipWrite(jarOutputStream), Fs.or(FileUtil.svn, FileUtil.isName(".DS_Store")));
			
			IOUtil.close(jarOutputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void copyFile(final String path, File zipFile, final File toDir) {
		try {
			ZipFile zipFile2 = new ZipFile(zipFile);
			each(path, zipFile2, new P2<String,InputStream>() {public void e(String path1, InputStream in) {
				if (path1.equals(path)) {
					IOUtil.connect_force(in, FileUtil.fileOutputStream(new File(toDir,new File(path1).getName())));
				}
			}});
		} catch (ZipException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	public static byte[] gzip(byte[] value) {
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			GZIPOutputStream out = new GZIPOutputStream(buffer);
			out.write(value);
			out.flush();
			return buffer.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] gzip(String value) {
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			GZIPOutputStream gzout = new GZIPOutputStream(buffer);
			OutputStreamWriter writer = new OutputStreamWriter(gzout, "UTF-8");
			writer.write(value);
			writer.flush();
			gzout.finish();
			return buffer.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String gunzipString(byte[] bytes) {
		try {
			InputStreamReader reader = new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(bytes)),"UTF-8");
			return IOUtil.readAll(reader);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] gunzip(byte[] bytes) {
		ByteArrayInputStream in1 = new ByteArrayInputStream(bytes);
		return gunzip(in1);
	}

	public static byte[] gunzip(InputStream in1) {
		try {
			GZIPInputStream in = new GZIPInputStream(in1);
			return IOUtil.readData(in);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void dump(InputStream in, String path,
			final ZipOutputStream tempZipOut) {
		try {
		    tempZipOut.putNextEntry(new ZipEntry(path));
		    IOUtil.dump(in, tempZipOut);
		    tempZipOut.closeEntry();
		} catch (ZipException e) {
//		    if (e.getMessage().startsWith("duplicate entry")) {
//		        System.out.println("Duplicated entry: " + path);
//		        // skip
//		    } else {
		        throw new RuntimeException(e);
//		    }
		} catch (IOException e) {
		    throw new RuntimeException(e);
		}
	}

	public static void eachEntry(ZipInputStream zi, P2<String, InputStream> p2) {
		try {
			for (ZipEntry nextEntry;(nextEntry = zi.getNextEntry())!=null;) {
				if (!nextEntry.isDirectory()) {
					p2.e(nextEntry.getName(), zi);
				}
				zi.closeEntry();
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Builder builder(final OutputStream out) {
		final HashSet<String> insertedPaths = new HashSet<String>();
		final ZipOutputStream zout = new ZipOutputStream(out);
		return new Builder() {
			public void write(String path, byte[] content) {
				// Need to put parent dir?
				try {
					zout.putNextEntry(new ZipEntry(path));
					zout.write(content);
					zout.closeEntry();
					zout.flush();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				insertedPaths.add(path);
			}

			public void finish() {
				try {
					zout.finish();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void writeZipContent(InputStream in) {
				writeZipContent(null, in);
			}
			
			@Override
			public void writeZipContent(String path, InputStream in) {
				ZipInputStream zin = new ZipInputStream(in);
				try {
					for (ZipEntry entry;(entry = zin.getNextEntry()) != null;) {
						if (insertedPaths.contains(entry.getName())) {
							continue;
						}
						
						if (path==null) {
							zout.putNextEntry(entry);
						} else {
							zout.putNextEntry(new ZipEntry(path + "/" + entry.getName()));
						}
						if (!entry.isDirectory()) {
							IOUtil.dump(zin, zout);
						}
						zout.closeEntry();
						zout.flush();
						
						insertedPaths.add(entry.getName());
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
	
	public static interface Builder {

		void write(String string, byte[] content);
		void finish();
		void writeZipContent(InputStream in);
		void writeZipContent(String path, InputStream in);
		
	}

	public static QFileUtil.QFile toQFile(ZipInputStream zis) {
		final QFileUtil.QFile ret = new QFileUtil.QFile("");
		eachEntry(zis, new P2<String,InputStream>() {public void e(String path, InputStream in) {
			QFileUtil.QFile file = (QFileUtil.QFile) Tree.getTree(Tree.parsePaths(path), ret, Tree.MODE_FORCE, 
					QFile.CONSTRUCTOR
			);
			
			final byte[] content = IOUtil.readDataNice(in);
			file.value = new QFileUtil.QFileContent() {public InputStream getInputStream() {
				return new ByteArrayInputStream(content);
			}};
		}});
		return ret;
	}
}
