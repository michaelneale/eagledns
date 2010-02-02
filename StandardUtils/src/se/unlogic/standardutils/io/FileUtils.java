package se.unlogic.standardutils.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Utility class for handling files and folders
 * 
 * @author Robert "Unlogic" Olofsson (unlogic@unlogic.se)
 * 
 */
public class FileUtils {

	public final static byte[] getRawBytes(File f) throws IOException {
		FileInputStream fin = new FileInputStream(f);
		byte[] buffer = new byte[(int) f.length()];
		fin.read(buffer);
		fin.close();
		return buffer;
	}

	public static String getFileExtension(File file) {
		return getFileExtension(file.getName());
	}

	public static String getFileExtension(String filename) {

		int dotIndex = filename.lastIndexOf(".");

		if (dotIndex == -1 || (dotIndex + 1) == filename.length()) {
			return null;
		} else {
			return filename.substring(dotIndex + 1);
		}
	}

	public static boolean fileExists(String path) {

		File file = new File(path);

		return file.exists();
	}

	/**
	 * Removes all files in the given directory matching the given filter
	 * 
	 * @param directory
	 *            the directory to be cleared
	 * @param filter
	 *            {@link FileFilter} used to filter files
	 * @param recursive
	 *            controls weather files should be deleted from sub directories too
	 */
	public static int deleteFiles(String directory, FileFilter filter, boolean recursive) {

		File dir = new File(directory);

		if (dir.exists() && dir.isDirectory()) {

			int deletedFiles = 0;

			File[] files = dir.listFiles(filter);

			for (File file : files) {

				if (file.isDirectory()) {

					if (recursive) {

						deletedFiles += deleteFiles(file.getPath(), filter, recursive);
					}

				} else {

					file.delete();

					deletedFiles++;
				}
			}

			return deletedFiles;
		}

		return 0;
	}

	public static boolean deleteDirectory(String directoryPath) {

		return deleteDirectory(new File(directoryPath));
	}

	public static boolean deleteDirectory(File directory) {

		if (directory.exists()) {

			File[] files = directory.listFiles();

			for (File file : files) {

				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					file.delete();
				}
			}
		}
		return directory.delete();
	}

	public static void deleteFile(String path) {

		File file = new File(path);
		
		if(file.exists()){
			file.delete();
		}
	}
}
