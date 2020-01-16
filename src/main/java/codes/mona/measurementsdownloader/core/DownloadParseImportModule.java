package codes.mona.measurementsdownloader.core;

import org.bson.Document;

import codes.mona.measurementsdownloader.model.UiLogger;
import software.amazon.awssdk.regions.Region;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/** Downloading, parsing or importing CSV-files. If the Database changes then only this class has to be altered. */
public class DownloadParseImportModule {

	private static DownloadParseImportModule instance;

	/** Gets the singleton instance of this module. */
	public static DownloadParseImportModule getInstance() {
		if (instance == null) {
			instance = new DownloadParseImportModule(
					new S3Adapter(Region.US_EAST_1, "ems-ingress-prod-useastcoast"), new FileManager(), new CsvParser(),
					DatabaseManager.getInstance());
		}
		return instance;
	}

	private final S3Adapter s3Adapter;
	private final FileManager fileManager;
	private final CsvParser csvParser;
	private final DatabaseManager databaseManager;

	public DownloadParseImportModule(S3Adapter s3Adapter, FileManager fileManager, CsvParser csvParser,
			DatabaseManager databaseManager) {
		this.s3Adapter = s3Adapter;
		this.fileManager = fileManager;
		this.csvParser = csvParser;
		this.databaseManager = databaseManager;
	}

	public void downloadOrImport(String plant, Date from, Date to, String toPath, boolean doImport)
			throws IOException, ParseException {

		SimpleDateFormat s = new SimpleDateFormat("yyyy/MM/dd/HH");
		s.setTimeZone(TimeZone.getTimeZone("UTC"));

		Calendar c = Calendar.getInstance();
		c.setTime(from);

		File toPathFolder = new File(toPath);
		fileManager.ensureFolderExists(toPathFolder);

		while (from.before(to) || from.equals(to)) {
			downloadOrImport(plant, s.format(from), toPathFolder, doImport);
			c.add(Calendar.HOUR, 1);
			from = c.getTime();
		}
	}

	private void downloadOrImport(String plant, String date, File toPathFolder, boolean doImport)
			throws IOException, ParseException {

		String prefix = plant + "/iv1/" + date;

		List<File> files = s3Adapter.downloadFiles(prefix, toPathFolder);

		List<String> unzippedFiles = new ArrayList<>();
		for (File file : files) {
			unzippedFiles.addAll(fileManager.unzipAndDelete(file, toPathFolder));
		}

		if (doImport) {
			importIntoMongoDB(unzippedFiles, plant);
		}
	}

	private void importIntoMongoDB(List<String> files, String plantName) throws IOException, ParseException {

		List<Document> documents = new ArrayList<>();

		for (String file : files) {

			UiLogger.getInstance().log("Parsing " + file);
			File fileEntry = new File(file);
			documents.addAll(csvParser.parseCsv(fileEntry.getPath()));
		}

		databaseManager.mongoDBImport(documents, plantName);
	}

	public void onlyParseCsv(String toPath, String plantName) throws IOException, ParseException {

		List<Document> documents = new ArrayList<>();
		File folder = new File(toPath);
		if (!fileManager.exists(folder)) {
			throw new FileNotFoundException();
		}

		for (File file : fileManager.listFiles(folder)) {

			if (fileManager.isCsvFile(file)) {
				UiLogger.getInstance().log("Parsing " + file);
				documents.addAll(csvParser.parseCsv(file.getAbsolutePath()));
			}
		}

		databaseManager.mongoDBImport(documents, plantName);
	}
}
