package qj.tool.file;

import java.io.File;
import java.util.ArrayList;

import qj.util.ArrayUtil4;

/**
 * Contain methods for searching
 */
public class FileSearch {
    public static File[] searchFile(String path) {
        return searchFile(path, "");
    }

    public static File[] searchFile(String path, String titleContains) {
        return searchFile(path, new String[]{titleContains}, false, true);
    }
    
    public static File[] searchFile(String path, String titleContains, boolean recursive) {
        return searchFile(path, new String[]{titleContains}, false, recursive);
    }

    /**
     *
     * @param path
     * @param titleContains
     * @return File list
     */
    public static File[] searchFile(String path, String[] titleContains, boolean caseSensitive, boolean recursive) {
        return (File[]) ArrayUtil4.listToArray(searchFile(new File(path), titleContains, false, true), File.class);
    }

    /**
     * Search file in dir and subdir.
     * @param rootFile
     * @param title
     * @param caseSensitive
     * @return List of found file
     */
	private static ArrayList searchFile(File rootFile, String[] title, boolean caseSensitive, boolean recursive ) {
        ArrayList returnList = new ArrayList();
        File[] subFiles = rootFile.listFiles();
        
        for (int i = 0; i < subFiles.length; i++) {

            File subFile = subFiles[i];
            // Check if match the specified search requirement
            // Then add to returning list.
            if (checkTitle(subFile, title, caseSensitive)
            		&& !subFile.isDirectory()
                    ) {
                returnList.add(subFile);
            }
            // more matching check ....

            // Meet sub dir, checking through
            if (subFile.isDirectory() && recursive) {
                // Call to this function to parse sub dir
                // Then merge it to returning array.
                //  Beware of StackOverflowError.
                try {
                    returnList.addAll(
                            searchFile(subFile, title, caseSensitive, recursive)
                    );
                } catch (StackOverflowError se) {
                    // Just reject exception and continue working.
                    System.err.println("StackOverflowError reading folder: " + subFile.getAbsolutePath());
                }
            }

        }

        return returnList;
    }

    /**
     * Used in searchFile
     * @param file
     * @param titles
     * @param caseSensitive
     * @return checkTitle
     */
    private static boolean checkTitle(File file, String[] titles, boolean caseSensitive) {
        for (int i = 0; i < titles.length; i++) {
            String title = titles[i];
            if ( (title == null)
                || (title.length()==0)
                || (caseSensitive? file.getName().indexOf(title) > -1
                        :(file.getName().toUpperCase().indexOf(title.toUpperCase()) > -1))
                )
            return true;

        }
        return false;
    }
}