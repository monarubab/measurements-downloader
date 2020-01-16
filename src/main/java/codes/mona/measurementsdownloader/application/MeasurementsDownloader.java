package codes.mona.measurementsdownloader.application;

import codes.mona.measurementsdownloader.controller.InputController;
import codes.mona.measurementsdownloader.controller.MenuController;
import codes.mona.measurementsdownloader.controller.MessageController;
import codes.mona.measurementsdownloader.model.DataModel;
import codes.mona.measurementsdownloader.model.UiLogger;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * UI implementation and execution of the program. If the View is changed only
 * this class has to be modified.
 */
public class MeasurementsDownloader extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {

		stage.setTitle("Measurements Downloader");

		stage.setWidth(500);
		stage.setHeight(700);

//		Image im = new Image("file:logo.png");
//		stage.getIcons().add(im);

		ListView<String> plantListView = new ListView<>();
		plantListView.setMaxSize(150, 180);

		Label fromDateLabel = new Label("From");

		TextField fromDateTextField = new TextField();
		fromDateTextField.setPrefColumnCount(10);
		fromDateTextField.setPromptText("YYYY/MM/DD/HH");

		Label toDateLabel = new Label("To    ");

		TextField toDateTextField = new TextField();
		toDateTextField.setPrefColumnCount(10);
		toDateTextField.setPromptText("YYYY/MM/DD/HH");

		Label targetPathLabel = new Label("Target directory:");

		TextField toPathTextField = new TextField();
		toPathTextField.setPrefColumnCount(10);

		Label queryLabel = new Label("Query");

		ListView<String> queryAggregatorListView = new ListView<>();
		queryAggregatorListView.setMaxSize(150, 180);

		ListView<String> queryFieldListView = new ListView<>();
		queryFieldListView.setMaxSize(150, 180);

		TextArea messageArea = new TextArea();
		messageArea.setEditable(false);
		messageArea.setWrapText(true);
		messageArea.setStyle("-fx-control-inner-background: WHITESMOKE;");

		HBox hBoxFrom = new HBox();
		hBoxFrom.setSpacing(15);
		hBoxFrom.getChildren().addAll(fromDateLabel, fromDateTextField);

		HBox hBoxTo = new HBox();
		hBoxTo.setSpacing(15);
		hBoxTo.getChildren().addAll(toDateLabel, toDateTextField);

		VBox vBoxFromTo = new VBox();
		vBoxFromTo.setSpacing(10);
		vBoxFromTo.getChildren().addAll(hBoxFrom, hBoxTo);

		Button downloadButton = new Button("Download");
		Button downloadAndParseButton = new Button("Download and Parse");
		Button parseButton = new Button("Parse all CSV");
		Button queryButton = new Button("Query");

		// Setting sizes of View Elements and positioning them
		downloadButton.setMinWidth(100);
		downloadButton.setMinHeight(70);

		downloadAndParseButton.setMaxWidth(100);
		downloadAndParseButton.setMinHeight(70);
		downloadAndParseButton.wrapTextProperty().setValue(true);

		parseButton.setMaxWidth(100);
		parseButton.setMinHeight(70);
		parseButton.wrapTextProperty().setValue(true);

		queryButton.setMinWidth(100);
		queryButton.setMinHeight(70);

		DataModel model = new DataModel();

		UiLogger.getInstance().setDataModel(model);

		new InputController(plantListView, fromDateTextField, toDateTextField,
				toPathTextField, queryAggregatorListView, queryFieldListView, model);

		new MessageController(messageArea, model);

		new MenuController(downloadButton, downloadAndParseButton, parseButton,
				queryButton, model);

		GridPane gridPane = new GridPane();

		gridPane.setPadding(new Insets(25, 25, 25, 25));

		gridPane.setVgap(10);
		gridPane.setHgap(10);

		gridPane.add(plantListView, 0, 0, 2, 1);

		gridPane.add(vBoxFromTo, 2, 0, 2, 1);

		gridPane.add(targetPathLabel, 0, 3, 2, 1);
		gridPane.add(toPathTextField, 0, 4, 4, 1);

		gridPane.add(queryLabel, 0, 9);

		gridPane.add(queryAggregatorListView, 0, 10, 2, 1);
		queryAggregatorListView.setMinWidth(200);

		gridPane.add(queryFieldListView, 2, 10, 2, 1);
		queryFieldListView.setMinWidth(200);

		gridPane.add(messageArea, 0, 5, 4, 1);

		gridPane.add(downloadButton, 0, 11);
		gridPane.add(downloadAndParseButton, 1, 11);
		gridPane.add(parseButton, 2, 11);
		gridPane.add(queryButton, 3, 11);

		Scene scene = new Scene(gridPane);
		stage.setScene(scene);
		stage.show();
	}
}