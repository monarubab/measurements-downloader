package codes.mona.measurementsdownloader.model;

/**
 * Setting feedback message on the UI.
 *
 */
public class UiLogger {

	private static UiLogger instance;

	public static UiLogger getInstance() {
		if (instance == null) {
			instance = new UiLogger();
		}
		return instance;
	}

	private DataModel model;

	public void setDataModel(DataModel model) {
		this.model = model;
	}

	public void log(String text) {
		if (model != null) {
			model.setMessage(text);
		}
	}
}
