package asu.foe.sketchy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

import asu.foe.sketchy.Pen.DrawingMode;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

@Component
public class GUISketchScene {

	private abstract class GUISketchSceneRunnable implements Runnable {
		protected GUISketchScene sketch;
		GUISketchSceneRunnable(GUISketchScene sketch) { this.sketch = sketch; }
	}

	// The pen to use for drawing shapes
	public Pen pen = new Pen();

	// The pane to place shapes on
	public Pane shapesPane = new Pane();

	// This handler service will be "autowired", which means it will be a bean, making it visible to spring-aop
	// This makes the aspect trigger on its method invocations
	@Autowired
	public GUISketchUpdateHandlerService handler;

	// This one however will be created with "new", making it invisible to the spring-aop proxy
	// This is helpful so that the aspect doesn't trigger on the incoming changes as well as the outgoing ones
	private GUISketchUpdateHandlerService incomingUpdatesHandler = new GUISketchUpdateHandlerService();

	public Scene getScene() {

		HBox headerHBox = createHeaderHBox();
		ScrollPane drawingCanvasPane = createDrawingCanvasPane();
		HBox toolbarHBox = createToolbarHBox();

		VBox.setVgrow(drawingCanvasPane, Priority.ALWAYS);
		VBox vBox = new VBox(headerHBox, drawingCanvasPane, toolbarHBox);
		Scene scene = new Scene(vBox);
		scene.getStylesheets().add(this.getClass().getResource("/stylesheet.css").toExternalForm());
		return scene;

	}

	private HBox createHeaderHBox() {

		Button exportBtn = new Button("Export...");
		exportBtn.setFocusTraversable(false);
		exportBtn.setStyle("-fx-font-weight: bold;");
		exportBtn.setGraphic(getImage("icons/export.png"));
		exportBtn.setPrefHeight(40);

		HBox leftHBox = new HBox(8.0);
		leftHBox.setPadding(new Insets(8.0));
		leftHBox.setAlignment(Pos.CENTER_LEFT);
		leftHBox.getChildren().add(exportBtn);

		HBox rightHBox = new HBox(8.0);
		rightHBox.setPadding(new Insets(8.0));
		rightHBox.setAlignment(Pos.CENTER_RIGHT);

		HBox.setHgrow(rightHBox, Priority.ALWAYS);
		HBox mainHBox = new HBox(leftHBox, rightHBox);
		mainHBox.setFocusTraversable(false);
		return mainHBox;

	}

	private ScrollPane createDrawingCanvasPane() {

		ScrollPane shapesScrollPane = new ScrollPane(); // A scrolling pane for the shapes pane to scroll on
		shapesScrollPane.prefWidthProperty().bind(shapesPane.prefWidthProperty());
		shapesScrollPane.prefHeightProperty().bind(shapesPane.prefHeightProperty());
		shapesScrollPane.setFocusTraversable(false);
		shapesScrollPane.setStyle("""
					-fx-border-style: solid hidden solid hidden;
					-fx-border-color: gray;
					-fx-background: white;
					""");
		shapesScrollPane.setContent(shapesPane);
		shapesPane.setMinWidth(2000);
		shapesPane.setMinHeight(2000);
		shapesPane.setFocusTraversable(false);
		shapesPane.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> handler.handleMousePress(this, pen, e.getX(), e.getY()));
		shapesPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> handler.handleMouseDrag(this, pen, e.getX(), e.getY()));
		shapesPane.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> handler.handleMouseRelease(this, pen, e.getX(), e.getY()));
		return shapesScrollPane;

	}

	private HBox createToolbarHBox() {

		ColorPicker colorPicker = new ColorPicker(Color.BLACK);
		colorPicker.setOnAction(e -> pen.strokeColorFromJavaFXColor(colorPicker.getValue()));
		colorPicker.setPrefHeight(40);
		colorPicker.setFocusTraversable(false);

		Slider slider = new Slider();
		slider.setMin(1);
		slider.setMax(20);
		slider.setPrefHeight(40);
		slider.setFocusTraversable(false);
		slider.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> pen.setStrokeWidth(slider.getValue()));

		HBox leftHBox = new HBox(8.0);
		leftHBox.setPadding(new Insets(8.0));
		leftHBox.setAlignment(Pos.CENTER_LEFT);
		ToggleGroup tg = new ToggleGroup();
		leftHBox.getChildren().add(createToolbarButton(tg, "icons/freehand.png", e -> pen.setDrawingMode(DrawingMode.FREEHAND)));
		leftHBox.getChildren().add(createToolbarButton(tg, "icons/line.png", e -> pen.setDrawingMode(DrawingMode.LINE)));
		leftHBox.getChildren().add(createToolbarButton(tg, "icons/rectangle.png", e -> pen.setDrawingMode(DrawingMode.RECTANGLE)));
		leftHBox.getChildren().add(createToolbarButton(tg, "icons/ellipse.png", e -> pen.setDrawingMode(DrawingMode.ELLIPSE)));
		leftHBox.getChildren().add(createToolbarButton(tg, "icons/eraser.png", e -> pen.setDrawingMode(DrawingMode.ERASER)));
		leftHBox.getChildren().add(createToolbarButton(tg, "icons/ocr.png", e -> pen.setDrawingMode(DrawingMode.OCR)));
		leftHBox.getChildren().add(new Separator(Orientation.HORIZONTAL));
		leftHBox.getChildren().add(getImage("icons/strokeColor.png"));
		leftHBox.getChildren().add(colorPicker);
		leftHBox.getChildren().add(new Separator(Orientation.HORIZONTAL));
		leftHBox.getChildren().add(getImage("icons/strokeWidth.png"));
		leftHBox.getChildren().add(slider);

		HBox rightHBox = new HBox(8.0);
		rightHBox.setPadding(new Insets(8.0));
		rightHBox.setAlignment(Pos.CENTER_RIGHT);
		rightHBox.getChildren().add(createToolbarButton(null, "icons/exit.png", e -> System.exit(0)));

		HBox.setHgrow(rightHBox, Priority.ALWAYS);
		HBox mainHBox = new HBox(leftHBox, rightHBox);
		mainHBox.setFocusTraversable(false);
		return mainHBox;

	}

	private ToggleButton createToolbarButton(ToggleGroup toggleGroup, String imagePath, EventHandler<ActionEvent> evh) {

		ToggleButton btn = new ToggleButton();
		btn.setOnAction(evh);
		btn.setGraphic(getImage(imagePath));
		btn.setFocusTraversable(false);
		btn.setContentDisplay(ContentDisplay.CENTER);
		if (toggleGroup != null) {
			btn.setToggleGroup(toggleGroup);
			if (toggleGroup.getToggles().size() == 1) btn.setSelected(true);
		}
		return btn;

	}

	private ImageView getImage(String imagePath) {

		ImageView imageView = new ImageView(new Image(imagePath));
		imageView.setFitHeight(32);
		imageView.setPreserveRatio(true);
		return imageView;

	}

	// TODO: Use the actual sketch id
	@KafkaListener(//
				topicPartitions = { @TopicPartition( // Topic/partition information for this listener
							topic = "sketch-updates", // The topic name
							partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0")) }, // The partition offset (start from beginning)
				groupId = "sketch-id", // The group ID which for our use case decides the sketch whose updates we want to listen to
				containerFactory = "guiSketchUpdateKafkaListenerContainerFactory" // The factory for the listener
	)
	public void handleIncomingChanges(GUISketchUpdateTransaction transaction) {
		if (transaction.getSessionId() != SketchyApplication.sessionId) {
			Platform.runLater(new GUISketchSceneRunnable(this) {
				@Override
				public void run() {
					switch (transaction.getUpdateType()) {
					case ADD:
						// All "node addition" operations are handled on the initial mouse press event
						incomingUpdatesHandler.handleMousePress(sketch, transaction.getPen(), transaction.getMouseX(), transaction.getMouseY());
						break;
					case EDIT:
						// All "node editing" operations are handled on the mouse drag event
						incomingUpdatesHandler.handleMouseDrag(sketch, transaction.getPen(), transaction.getMouseX(), transaction.getMouseY());
						break;
					case REMOVE:
						// All "node removal" operations are handled upon mouse release
						incomingUpdatesHandler.handleMouseRelease(sketch, transaction.getPen(), transaction.getMouseX(), transaction.getMouseY());
						break;
					default:
						break;
					}
				}
			});
		}
	}

}
