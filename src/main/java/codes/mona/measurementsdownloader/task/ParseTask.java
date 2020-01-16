package codes.mona.measurementsdownloader.task;

import java.io.IOException;
import java.text.ParseException;

import codes.mona.measurementsdownloader.core.DownloadParseImportModule;
import codes.mona.measurementsdownloader.model.DataModel;
import codes.mona.measurementsdownloader.model.IllegalDateException;

/**
 * The parse function is implemented in this method.
 */
public class ParseTask extends AbstractTask {

	public ParseTask(DataModel model) {
		super(model);
	}

	@Override
	protected void execute() throws IllegalDateException, ParseException, IOException {

		DownloadParseImportModule.getInstance().onlyParseCsv(model.getPath(), model.getCurrentPlant());
		model.setMessage("Parsed and imported");
	}
}
