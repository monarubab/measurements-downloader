package codes.mona.measurementsdownloader.core;

import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/** Parsing CSV-files into MongoDB document. CSV-Structure is to be adapted in this class. */
public class CsvParser {

	private static final String SEPARATOR = ";";

	private static final Pattern SQUARE_BRACKETS_PATTERN = Pattern.compile("\\[.+\\]");

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss z");

	List<Document> parseCsv(String filename) throws IOException, ParseException {
		List<Document> result = new ArrayList<>();

		BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));

		String firstLine = br.readLine();

		String[] headers = firstLine.split(SEPARATOR);

		String secondLine = br.readLine();
		String[] units = secondLine.split(SEPARATOR);
		for (int i = 0; i < units.length; i++) {
			units[i] = removeSquareBrackets(units[i]);
		}

		String s;
		while ((s = br.readLine()) != null) {
			Document document = new Document();
			String[] lineContents = s.split(SEPARATOR);

			// Always use UTC because the supported plants may not provide time zone information.
			// When adding additional plants, this needs to be checked again.
			Date date = DATE_FORMAT.parse(String.format("%s %s %s", lineContents[0], lineContents[1], "UTC"));
			document.append("Date", date);
			for (int i = 2; i < lineContents.length; i++) {
				// Remove dots from keys to have valid MongoDb field names
				String headerWithoutDots = removeDots(headers[i]);
				// Try saving as number first, fall back to String if number contains letters
				try {
					double d = Double.valueOf(lineContents[i]);
					document.append(headerWithoutDots, d);
				} catch (NumberFormatException e) {
					document.append(headerWithoutDots, lineContents[i]);
				}
			}
			result.add(document);
		}

		br.close();

		return result;
	}

	private String removeDots(String string) {
		return string.replaceAll("\\.", "");
	}

	private String removeSquareBrackets(String unit) {
		if (SQUARE_BRACKETS_PATTERN.matcher(unit).matches()) {
			return unit.substring(1, unit.length() - 1);
		}
		return unit;
	}
}
