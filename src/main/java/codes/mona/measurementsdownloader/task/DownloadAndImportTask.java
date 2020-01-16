package codes.mona.measurementsdownloader.task;

import java.io.IOException;
import java.text.ParseException;

import codes.mona.measurementsdownloader.core.DownloadParseImportModule;
import codes.mona.measurementsdownloader.model.DataModel;
import codes.mona.measurementsdownloader.model.IllegalDateException;

/**
 * The download and import function is implemented in this method.
 */
public class DownloadAndImportTask extends AbstractTask {

	public DownloadAndImportTask(DataModel model) {
		super(model);
	}

	@Override
	protected void execute() throws IllegalDateException, ParseException, IOException {

		DownloadParseImportModule.getInstance().downloadOrImport(model.getCurrentPlant(), model.getFromDate(), model.getToDate(), model.getPath(), true);
		model.setMessage("Parsed and imported");
	}
}
