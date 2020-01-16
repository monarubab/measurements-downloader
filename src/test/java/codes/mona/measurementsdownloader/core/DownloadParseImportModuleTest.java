package codes.mona.measurementsdownloader.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import codes.mona.measurementsdownloader.core.CsvParser;
import codes.mona.measurementsdownloader.core.DatabaseManager;
import codes.mona.measurementsdownloader.core.DownloadParseImportModule;
import codes.mona.measurementsdownloader.core.FileManager;
import codes.mona.measurementsdownloader.core.S3Adapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DownloadParseImportModuleTest {

	@Mock
	S3Adapter s3Adapter;

	@Mock
	FileManager fileManager;

	@Mock
	CsvParser csvParser;

	@Mock
	DatabaseManager databaseManager;

	@InjectMocks
	DownloadParseImportModule downloadParseImportModule;

	@Before
	public void setUp() throws IOException {
		File file1 = new File("foo");
		File file2 = new File("bar");
		List<File> dowloadFilesResult = new ArrayList<>();
		dowloadFilesResult.add(file1);
		dowloadFilesResult.add(file2);
		when(s3Adapter.downloadFiles(any(), any())).thenReturn(dowloadFilesResult);

		// Unzipping always returns a list of one unzipped file.
		List<String> unzipAndDeleteResult = new ArrayList<>();
		unzipAndDeleteResult.add("baz");
		when(fileManager.unzipAndDelete(any(), any())).thenReturn(unzipAndDeleteResult);

		File[] listFilesResult = new File[]{new File("foo"), new File("bar")};
		when(fileManager.listFiles(any())).thenReturn(listFilesResult);
	}

	@Test
	public void downloadOrImport_noImport() throws ParseException, IOException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		// Input
		String plant = "plant";
		Date from = dateFormat.parse("2019-10-26 06");
		Date to = dateFormat.parse("2019-10-26 09");
		String toPath = "/Users/foo/bar/";
		boolean doImport = false;

		downloadParseImportModule.downloadOrImport(plant, from, to, toPath, doImport);

		verify(s3Adapter, times(4)).downloadFiles(any(), any());
		verify(fileManager, times(8)).unzipAndDelete(any(), any());
		verify(fileManager, times(1)).ensureFolderExists(eq(new File(toPath)));
		verify(csvParser, times(0)).parseCsv(any());
		verify(databaseManager, times(0)).mongoDBImport(any(), any());
	}

	@Test
	public void downloadOrImport_withImport() throws ParseException, IOException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		// Input
		String plant = "plant";
		Date from = dateFormat.parse("2019-10-26 06");
		Date to = dateFormat.parse("2019-10-26 09");
		String toPath = "/Users/foo/bar/";
		boolean doImport = true;

		downloadParseImportModule.downloadOrImport(plant, from, to, toPath, doImport);

		verify(s3Adapter, times(4)).downloadFiles(any(), any());
		verify(fileManager, times(8)).unzipAndDelete(any(), any());
		verify(fileManager, times(1)).ensureFolderExists(eq(new File(toPath)));
		verify(csvParser, times(8)).parseCsv(any());
		verify(databaseManager, times(4)).mongoDBImport(any(), any());
	}

	@Test
	public void onlyParseCsv() throws IOException, ParseException {
		when(fileManager.exists(any())).thenReturn(true);
		// Only the second file is a CSV file.
		when(fileManager.isCsvFile(any())).thenReturn(false).thenReturn(true);

		downloadParseImportModule.onlyParseCsv("/Users/foo/bar/", "plant");

		verify(csvParser, times(1)).parseCsv(any());
		verify(databaseManager, times(1)).mongoDBImport(any(), any());
	}

	@Test(expected = FileNotFoundException.class)
	public void onlyParseCsv_throwsFileNotFoundException() throws IOException, ParseException {
		when(fileManager.exists(any())).thenReturn(false);

		downloadParseImportModule.onlyParseCsv("/Users/foo/bar/", "plant");
	}
}
