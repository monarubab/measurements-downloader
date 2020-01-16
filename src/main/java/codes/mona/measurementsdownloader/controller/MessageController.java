package codes.mona.measurementsdownloader.controller;

import codes.mona.measurementsdownloader.model.DataModel;
import javafx.scene.control.TextArea;

public class MessageController {

	private final TextArea messageArea;

	private final DataModel model;

	public MessageController(TextArea messageArea, DataModel model) {
		this.messageArea = messageArea;
		this.model = model;
		init();
	}

	private void init() {
		messageArea.textProperty().bind(model.messageProperty());
	}
}
