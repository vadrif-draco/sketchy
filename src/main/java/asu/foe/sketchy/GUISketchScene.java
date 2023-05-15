package asu.foe.sketchy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

import asu.foe.sketchy.Pen.DrawingMode;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

@Component
public class GUISketchScene {

	// This handler service will be "autowired", which means it will be a bean, making it visible to spring-aop
	// This makes the aspect trigger on its method invocations
	@Autowired
	public GUISketchUpdateHandlerService handler;

	// This one however will be created with "new", making it invisible to the spring-aop proxy
	// This is helpful so that the aspect doesn't trigger on the incoming changes as well as the outgoing ones
	private GUISketchUpdateHandlerService incomingUpdatesHandler = new GUISketchUpdateHandlerService();

	// This runnable is used to handle incoming sketch changes in parallel with normal usage of sketch
	private abstract class GUISketchSceneRunnable implements Runnable {
		protected GUISketchScene sketch;
		GUISketchSceneRunnable(GUISketchScene sketch) { this.sketch = sketch; }
	}

	// To be returned to if user wants to close sketch
	// Needs to be lazily loaded because the sketch list scene also references this GUI
	@Lazy
	@Autowired
	private GUISketchListScene sketchListScene;

	// To change the scene
	@Autowired
	private GUIMainStage mainStage;

	// The pen to use for drawing shapes
	public Pen pen = new Pen();

	// The pane to place shapes on
	public Pane shapesPane = new Pane();

	// The pane that shows collaboration information
	private ScrollPane collaborationPane;

	public Parent getRoot() {

		HBox headerHBox = createHeaderHBox();
		ScrollPane drawingCanvasPane = createDrawingCanvasPane();
		VBox.setVgrow(drawingCanvasPane, Priority.ALWAYS);
		HBox toolbarHBox = createToolbarHBox();
		collaborationPane = createCollaborationPane();

		return new HBox(
					new VBox(headerHBox, drawingCanvasPane, toolbarHBox),
					collaborationPane);

	}

	private HBox createHeaderHBox() {

		Button exportBtn = new Button("Export...");
		exportBtn.setFocusTraversable(false);
		exportBtn.setStyle("-fx-font-weight: bold;");
		exportBtn.setGraphic(getImage("icons/export.png"));
		exportBtn.setPrefHeight(40);

		Button shareBtn = new Button("Share...");
		shareBtn.setFocusTraversable(false);
		shareBtn.setStyle("-fx-font-weight: bold;");
		shareBtn.setGraphic(getImage("icons/share.png"));
		shareBtn.setPrefHeight(40);

		HBox leftHBox = new HBox(8.0);
		leftHBox.setPadding(new Insets(8.0));
		leftHBox.setAlignment(Pos.CENTER_LEFT);
		leftHBox.getChildren().addAll(shareBtn, exportBtn, createSpacer());

		Button collabBtn = new Button();
		collabBtn.setFocusTraversable(false);
		collabBtn.setStyle("-fx-font-weight: bold;");
		collabBtn.setGraphic(getImage("icons/collab.png"));
		collabBtn.setPrefHeight(40);
		collabBtn.setOnAction(e -> { toggleCollaborationPane(); });

		Button returnBtn = new Button();
		returnBtn.setFocusTraversable(false);
		returnBtn.setStyle("-fx-font-weight: bold;");
		returnBtn.setGraphic(getImage("icons/return.png"));
		returnBtn.setPrefHeight(40);
		returnBtn.setOnAction(e -> {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Please confirm");
			alert.setHeaderText("Are you sure you want to close this sketch?");
			alert.setContentText("Your progress is automatically saved.");
			if (alert.showAndWait().get().equals(ButtonType.OK)) {
				mainStage.scene.setRoot(sketchListScene.getRoot());
			}
		});

		HBox rightHBox = new HBox(8.0);
		rightHBox.setPadding(new Insets(8.0));
		rightHBox.setAlignment(Pos.CENTER_RIGHT);
		leftHBox.getChildren().addAll(collabBtn, returnBtn);

		HBox mainHBox = new HBox(leftHBox, rightHBox);
		HBox.setHgrow(leftHBox, Priority.ALWAYS);
		mainHBox.setAlignment(Pos.CENTER);
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

		HBox strokeColorHBox = new HBox(8.0);
		strokeColorHBox.setPadding(new Insets(8.0));
		strokeColorHBox.setAlignment(Pos.CENTER);
		ColorPicker colorPicker = new ColorPicker(Color.BLACK);
		colorPicker.setOnAction(e -> pen.strokeColorFromJavaFXColor(colorPicker.getValue()));
		colorPicker.setPrefHeight(40);
		colorPicker.setFocusTraversable(false);
		strokeColorHBox.getChildren().add(getImage("icons/strokeColor.png", 24.0, 0.75));
		strokeColorHBox.getChildren().add(colorPicker);

		HBox toggleButtonsHBox = new HBox(8.0);
		toggleButtonsHBox.setPadding(new Insets(8.0));
		toggleButtonsHBox.setAlignment(Pos.CENTER);
		ToggleGroup tg = new ToggleGroup();
		toggleButtonsHBox.getChildren().add(createToolbarButton(tg, "icons/freehand.png", e -> pen.setDrawingMode(DrawingMode.FREEHAND)));
		toggleButtonsHBox.getChildren().add(createToolbarButton(tg, "icons/line.png", e -> pen.setDrawingMode(DrawingMode.LINE)));
		toggleButtonsHBox.getChildren().add(createToolbarButton(tg, "icons/rectangle.png", e -> pen.setDrawingMode(DrawingMode.RECTANGLE)));
		toggleButtonsHBox.getChildren().add(createToolbarButton(tg, "icons/ellipse.png", e -> pen.setDrawingMode(DrawingMode.ELLIPSE)));
		toggleButtonsHBox.getChildren().add(createToolbarButton(tg, "icons/eraser.png", e -> pen.setDrawingMode(DrawingMode.ERASER)));
		toggleButtonsHBox.getChildren().add(createToolbarButton(tg, "icons/ocr.png", e -> pen.setDrawingMode(DrawingMode.OCR)));

		HBox sliderHBox = new HBox(8.0);
		sliderHBox.setPadding(new Insets(8.0));
		sliderHBox.setAlignment(Pos.CENTER);
		Slider slider = new Slider();
		slider.setMin(1);
		slider.setMax(20);
		slider.setPrefHeight(40);
		slider.setFocusTraversable(false);
		slider.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> pen.setStrokeWidth(slider.getValue()));
		sliderHBox.getChildren().add(getImage("icons/strokeWidth.png", 24.0, 0.75));
		sliderHBox.getChildren().add(slider);

		HBox mainHBox = new HBox(
					createSpacer(),
					createSpacer(),
					strokeColorHBox,
					createSpacer(),
					toggleButtonsHBox,
					createSpacer(),
					sliderHBox,
					createSpacer(),
					createSpacer() // 2 : 1 : 1 : 2
		);
		mainHBox.setFocusTraversable(false);
		return mainHBox;

	}

	private ScrollPane createCollaborationPane() {

		ScrollPane collaborationPane = new ScrollPane();
		collaborationPane.setStyle("-fx-border-style: hidden;");
		collaborationPane.setFocusTraversable(false);
		collaborationPane.setPrefWidth(0);
		collaborationPane.setMinWidth(0);
		collaborationPane.setPrefViewportWidth(0);
		collaborationPane.setMinViewportWidth(0);
		collaborationPane.setFitToWidth(true);
		return collaborationPane;

	}

	private ToggleButton createToolbarButton(ToggleGroup toggleGroup, String imagePath, EventHandler<ActionEvent> evh) {

		ToggleButton btn = new ToggleButton();
		btn.setOnAction(evh);
		btn.setGraphic(getImage(imagePath));
		btn.setFocusTraversable(false);
		btn.setContentDisplay(ContentDisplay.CENTER);
		btn.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> { if (btn.isSelected()) event.consume(); });
		if (toggleGroup != null) {
			btn.setToggleGroup(toggleGroup);
			if (toggleGroup.getToggles().size() == 1) btn.setSelected(true);
		}
		return btn;

	}

	private ImageView getImage(String imagePath) { return getImage(imagePath, 32.0, 1.0); }
	private ImageView getImage(String imagePath, double height, double opacity) {

		ImageView imageView = new ImageView(new Image(imagePath));
		imageView.setFitHeight(height);
		imageView.setOpacity(opacity);
		imageView.setPreserveRatio(true);
		return imageView;

	}

	private Node createSpacer() {

		final Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		return spacer;

	}

	private void toggleCollaborationPane() {

		Double newMinWidth;
		Interpolator interpolator;
		if (collaborationPane.getMinWidth() == 0) {
			newMinWidth = 360.0;
			interpolator = Interpolator.EASE_IN;
		} else {
			newMinWidth = 0.0;
			interpolator = Interpolator.EASE_OUT;
		}
		Timeline timeline = new Timeline();
		KeyValue keyValue = new KeyValue(collaborationPane.minWidthProperty(), newMinWidth, interpolator);
		KeyFrame keyFrame = new KeyFrame(Duration.millis(250), keyValue);
		timeline.getKeyFrames().add(keyFrame);
		timeline.play();

	}

	// TODO: @AS Use the actual sketch id from SketchyApplication.currentSketch.getId()
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
//	@KafkaListener(topics = "collab-updates", groupId = "sketch-id", containerFactory = "guiCollabUpdateKafkaListenerContainerFactory")
//	public void handleCollaborationEvents(GUICollabUpdateTransaction transaction) {
//		if (transaction.getSessionId() != SketchyApplication.sessionId) {
//			Platform.runLater(new GUISketchSceneRunnable(this) {
//				@Override
//				public void run() {
//					switch (transaction.getUpdateType()) {
//					case ADD:
//						// All "node addition" operations are handled on the initial mouse press event
//						incomingUpdatesHandler.handleMousePress(sketch, transaction.getPen(), transaction.getMouseX(), transaction.getMouseY());
//						break;
//					case EDIT:
//						// All "node editing" operations are handled on the mouse drag event
//						incomingUpdatesHandler.handleMouseDrag(sketch, transaction.getPen(), transaction.getMouseX(), transaction.getMouseY());
//						break;
//					case REMOVE:
//						// All "node removal" operations are handled upon mouse release
//						incomingUpdatesHandler.handleMouseRelease(sketch, transaction.getPen(), transaction.getMouseX(), transaction.getMouseY());
//						break;
//					default:
//						break;
//					}
//				}
//			});
//		}
//	}

}
