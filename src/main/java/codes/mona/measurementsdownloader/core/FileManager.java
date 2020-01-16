package codes.mona.measurementsdownloader.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * After unzipping files the .zip-files are deleted. Ensuring that a certain
 * folder exists. Searching for CSV-files. If the compression format of the file
 * is different then only this class has to be altered.
 */
public class FileManager {

	public List<String> unzipAndDelete(File file, File toPathFolder) throws IOException {

		// We expect only 1 file per zip archive.
		List<String> unzippedFiles = new ArrayList<>(1);

		try (ZipFile zipFile = new ZipFile(file)) {
			Enumeration<?> zipEntries = zipFile.entries();

			String outDir = toPathFolder + File.separator;

			while (zipEntries.hasMoreElements()) {

				ZipEntry zipEntry = (ZipEntry) zipEntries.nextElement();
				String entryFullyQualifiedName = outDir + zipEntry.getName();

				InputStream in = zipFile.getInputStream(zipEntry);

				try (OutputStream out = new BufferedOutputStream(new FileOutputStream(entryFullyQualifiedName))) {

					byte[] bytes = new byte[8192];
					int length;

					while ((length = in.read(bytes)) >= 0) {
						out.write(bytes, 0, length);
					}
				}

				unzippedFiles.add(entryFullyQualifiedName);
			}
		}
		file.delete();

		return unzippedFiles;
	}

	public void ensureFolderExists(File folder) {
		if (!folder.exists()) {
			folder.mkdirs();
		}
	}

	public boolean exists(File file) {
		return file.exists();
	}

	public File[] listFiles(File folder) {
		return folder.listFiles();
	}

	public boolean isCsvFile(File file) {
		return file.isFile() && file.getName().endsWith(".csv");
	}
}
