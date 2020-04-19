package codes.mona.measurementsdownloader.controller;

import codes.mona.measurementsdownloader.model.DataModel;
import codes.mona.measurementsdownloader.task.DownloadAndImportTask;
import codes.mona.measurementsdownloader.task.DownloadTask;
import codes.mona.measurementsdownloader.task.ParseTask;
import codes.mona.measurementsdownloader.task.QueryTask;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.Button;

/** Controller for menu actions. */
public class MenuController {

	private final Button downloadButton;
	private final Button downloadAndParseButton;
	private final Button parseButton;
	private final Button queryButton;
	private final DataModel model;

	public MenuController(Button downloadButton, Button downloadAndParseButton, Button parseButton, Button queryButton, DataModel model) {
		this.downloadButton = downloadButton;
		this.downloadAndParseButton = downloadAndParseButton;
		this.parseButton = parseButton;
		this.queryButton = queryButton;
		this.model = model;
		init();
	}

	private void init() {
		BooleanBinding downloadBinding = model.fromProperty().isEmpty()
				.or(model.toProperty().isEmpty()).or(model.pathProperty().isEmpty())
				.or(model.currentPlantProperty().isNull()).or(model.taskInProgressProperty());
		downloadButton.disableProperty().bind(downloadBinding);
		downloadAndParseButton.disableProperty().bind(downloadBinding);

		parseButton.disableProperty().bind(model.taskInProgressProperty());

		BooleanBinding queryBinding = model.fromProperty().isEmpty()
				.or(model.toProperty().isEmpty())
				.or(model.currentPlantProperty().isNull())
				.or(model.currentQueryAggregatorProperty().isNull())
				.or(model.currentQueryFieldProperty().isNull()
				.or(model.taskInProgressProperty()));
		queryButton.disableProperty().bind(queryBinding);

		downloadButton.setOnAction(e ->
			new Thread(new DownloadTask(model)).start()
		);
		downloadAndParseButton.setOnAction(e ->
			new Thread(new DownloadAndImportTask(model)).start()
		);
		parseButton.setOnAction(e ->
			new Thread(new ParseTask(model)).start()
		);
		queryButton.setOnAction(e ->
			new Thread(new QueryTask(model)).start()
		);
	}
}
