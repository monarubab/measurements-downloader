package codes.mona.measurementsdownloader.task;

import java.io.IOException;
import java.text.ParseException;

import codes.mona.measurementsdownloader.core.DatabaseManager;
import codes.mona.measurementsdownloader.model.DataModel;
import codes.mona.measurementsdownloader.model.IllegalDateException;

/**
 * The query function is implemented in this method.
 */
public class QueryTask extends AbstractTask {

	public QueryTask(DataModel model) {
		super(model);
	}

	@Override
	protected void execute() throws IllegalDateException, ParseException, IOException {

		double queryResult = DatabaseManager.getInstance().aggregate(model.getCurrentPlant(), model.getFromDate(), model.getToDate(), model.getCurrentQueryAggregator(),
				model.getCurrentQueryField());
		model.setMessage("Result: " + queryResult);
	}

}
