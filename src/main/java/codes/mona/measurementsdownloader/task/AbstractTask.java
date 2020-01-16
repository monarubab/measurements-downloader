package codes.mona.measurementsdownloader.task;

import javafx.concurrent.Task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.NoSuchElementException;

import codes.mona.measurementsdownloader.model.DataModel;
import codes.mona.measurementsdownloader.model.IllegalDateException;

/**
 * Represents a Task. Abstracts the Exception Handling of various concrete
 * implementations.
 */
abstract class AbstractTask extends Task<Object> {

	protected final DataModel model;

	public AbstractTask(DataModel model) {

		this.model = model;
	}

	@Override
	protected Object call() throws Exception {

		try {
			model.setTaskInProgress(true);
			execute();
		} catch (IllegalDateException e) {
			// Message for illegal date was already set in getFromDate() or getToDate()
		} catch (FileNotFoundException e) {
			model.setMessage("The path does not exist");
		} catch (ParseException e) {
			model.setMessage("Malformed date in CSV");
		} catch (NoSuchElementException e) {
			model.setMessage("No results were found for this date range");
		} catch (IOException e) {
			model.setMessage("I/O exception while writing to disk");
		} finally {
			model.setTaskInProgress(false);
		}
		return null;
	}

	protected abstract void execute() throws IllegalDateException, ParseException, IOException;
}
