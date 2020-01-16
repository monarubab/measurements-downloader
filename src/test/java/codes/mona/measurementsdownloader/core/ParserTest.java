package codes.mona.measurementsdownloader.core;

import org.bson.Document;
import org.junit.Test;

import codes.mona.measurementsdownloader.core.CsvParser;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class ParserTest {

	CsvParser csvParser = new CsvParser();

	@Test
	public void parserParsesCorrectFile() throws IOException, ParseException {
		List<Document> result = csvParser.parseCsv("src/test/resources/simple.csv");
		assertThat(result).hasSize(2);
		assertThat(result.get(0)).containsAtLeast("c1c2c3Messgroesse1", 3d, "d1d2d3Messgroesse2", 4d);
		assertThat(result.get(1)).containsAtLeast("c1c2c3Messgroesse1", 7d, "d1d2d3Messgroesse2", 8d);
	}

	@Test(expected = ParseException.class)
	public void parserThrowsOnIllegalDate() throws IOException, ParseException {
		csvParser.parseCsv("src/test/resources/illegaldate.csv");
	}

	@Test(expected = ParseException.class)
	public void parserThrowsOnIllegalTime() throws IOException, ParseException {
		csvParser.parseCsv("src/test/resources/illegaltime.csv");
	}
}
