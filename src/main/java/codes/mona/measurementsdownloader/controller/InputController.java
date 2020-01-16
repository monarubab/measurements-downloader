package codes.mona.measurementsdownloader.controller;

import codes.mona.measurementsdownloader.model.DataModel;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/** Controller for user input. */
public class InputController {

	private final ListView<String> plantListView;

	private final TextField fromTextField;

	private final TextField toTextField;

	private final TextField pathTextField;

	private final ListView<String> queryAggregatorListView;

	private final ListView<String> queryFieldListView;

	private final DataModel model;

	public InputController(ListView<String> plantListView, TextField fromTextField, TextField toTextField,
						   TextField pathTextField, ListView<String> queryAggregatorListView,
						   ListView<String> queryFieldListView, DataModel model) {
		this.plantListView = plantListView;
		this.fromTextField = fromTextField;
		this.toTextField = toTextField;
		this.pathTextField = pathTextField;
		this.queryAggregatorListView = queryAggregatorListView;
		this.queryFieldListView = queryFieldListView;
		this.model = model;
		init();
	}

	private void init() {
		plantListView.setItems(model.getPlantList());
		plantListView.getSelectionModel().selectedItemProperty().addListener(
				(obs, oldSelection, newSelection) -> model.setCurrentPlant(newSelection));

		fromTextField.setText(model.getFrom());
		model.fromProperty().bind(fromTextField.textProperty());

		toTextField.setText(model.getTo());
		model.toProperty().bind(toTextField.textProperty());

		pathTextField.setText(model.getPath());
		model.pathProperty().bind(pathTextField.textProperty());

		queryAggregatorListView.setItems(model.getQueryAggregatorList());
		queryAggregatorListView.getSelectionModel().selectedItemProperty().addListener(
				(obs, oldSelection, newSelection) -> model.setCurrentQueryAggregator(newSelection));

		queryFieldListView.setItems(model.getQueryFieldList());
		queryFieldListView.getSelectionModel().selectedItemProperty().addListener(
				(obs, oldSelection, newSelection) -> model.setCurrentQueryField(newSelection));
	}
}
