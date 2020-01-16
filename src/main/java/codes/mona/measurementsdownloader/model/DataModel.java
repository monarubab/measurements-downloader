package codes.mona.measurementsdownloader.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DataModel {

	private final BooleanProperty taskInProgress = new SimpleBooleanProperty(false);

	private final ObservableList<String> plantList = FXCollections.observableArrayList("foo", "bar", "baz");

	private final ObjectProperty<String> currentPlant = new SimpleObjectProperty<>();

	private final DateFormat dateFormat;

	private final StringProperty from = new SimpleStringProperty("2019/09/30/00");

	private final StringProperty to = new SimpleStringProperty("2019/09/30/01");

	private final StringProperty path = new SimpleStringProperty(
			System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "Measurements" + File.separator);

	private final StringProperty message = new SimpleStringProperty();

	private final ObservableList<String> queryAggregatorList = FXCollections.observableArrayList("min", "max", "avg");

	private final ObjectProperty<String> currentQueryAggregator = new SimpleObjectProperty<>();

	private final ObservableList<String> queryFieldList = FXCollections.observableArrayList("foo",
			"bar", "baz");

	private final ObjectProperty<String> currentQueryField = new SimpleObjectProperty<>();

	public DataModel() {
		dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	public BooleanProperty taskInProgressProperty() {
		return taskInProgress;
	}

	public void setTaskInProgress(boolean value) {
		taskInProgress.set(value);
	}

	public ObservableList<String> getPlantList() {
		return plantList;
	}

	public ObjectProperty<String> currentPlantProperty() {
		return currentPlant;
	}

	public String getCurrentPlant() {
		return currentPlant.get();
	}

	public void setCurrentPlant(String plant) {
		currentPlant.set(plant);
	}

	public StringProperty fromProperty() {
		return from;
	}

	public String getFrom() {
		return from.get();
	}

	public Date getFromDate() throws IllegalDateException {
		try {
			return dateFormat.parse(from.get());
		} catch (ParseException e) {
			message.set("From Date must be in the format: yyyy/MM/dd/HH");
			throw new IllegalDateException();
		}
	}

	public StringProperty toProperty() {
		return to;
	}

	public String getTo() {
		return to.get();
	}

	public Date getToDate() throws IllegalDateException {

		try {
			return dateFormat.parse(to.get());
		} catch (ParseException e) {
			message.set("To Date must be in the format: yyyy/MM/dd/HH");
			throw new IllegalDateException();
		}
	}

	public StringProperty pathProperty() {
		return path;
	}

	public String getPath() {
		return path.get();
	}

	public File getPathFile() {
		return new File(path.get());
	}

	public StringProperty messageProperty() {
		return message;
	}

	public void setMessage(String text) {
		message.set(text);
	}

	public ObservableList<String> getQueryAggregatorList() {
		return queryAggregatorList;
	}

	public ObjectProperty<String> currentQueryAggregatorProperty() {
		return currentQueryAggregator;
	}

	public String getCurrentQueryAggregator() {
		return currentQueryAggregator.get();
	}

	public void setCurrentQueryAggregator(String value) {
		currentQueryAggregator.set(value);
	}

	public ObservableList<String> getQueryFieldList() {
		return queryFieldList;
	}

	public ObjectProperty<String> currentQueryFieldProperty() {
		return currentQueryField;
	}

	public String getCurrentQueryField() {
		return currentQueryField.get();
	}

	public void setCurrentQueryField(String value) {
		currentQueryField.set(value);
	}
}
