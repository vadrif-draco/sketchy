package asu.foe.sketchy;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import asu.foe.sketchy.GUIApplication.ShutdownEvent;
import asu.foe.sketchy.GUICollabUpdateTransaction.CollabUpdateType;
import asu.foe.sketchy.Pen.DrawingMode;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

@Component
public class GUISketchScene implements ApplicationListener<ShutdownEvent> {

	// This handler service will be "autowired", which means it will be a bean, making it visible to spring-aop
	// This makes the aspect trigger on its method invocations
	// This is different than the handler used by GUISketchUpdateListener
	@Autowired
	private GUISketchUpdateHandlerService handler;

	// We need the application context for dynamic creation of GUISketchUpdateListener beans per topic
	@Autowired
	private ApplicationContext applicationContext;

	// To be returned to if user wants to close sketch and return to sketch list scene
	// Needs to be lazily loaded because the sketch list scene also references this GUI
	@Lazy
	@Autowired
	private GUISketchListScene sketchListScene;

	// To change the scene
	@Autowired
	private GUIMainStage mainStage;

	@Lazy
	@Autowired
	private KafkaTemplate<String, GUICollabUpdateTransaction> guiCollabUpdateKafkaTemplate;

	// Flag to check whether sketch is on stage or not
	private Boolean sketchOnStage = false;

	// ID for the current sketch session; to be randomly generated on every sketch load
	// Used to create a unique Kafka listener on every sketch load to fetch topic information
	String sessionId;
	String sketchId;

	// Internally keeping track of number of active collaborators
	Integer numOfActiveCollaborators = 0;

	// The pen to use for drawing shapes
	public Pen pen;

	// The pane to place shapes on
	public Pane shapesPane;

	// The pane that shows collaboration information
	private ScrollPane collaborationPane;

	public Parent getRoot() {

		// Setup the session and sketch IDs
		sessionId = SketchyApplication.currentUser.getId().toString() + "-" + UUID.randomUUID().toString();
		sketchId = SketchyApplication.currentSketch.getId().toString();

		// Create a new parameterized bean for the sketch update listener
		applicationContext.getBean(
					GUISketchUpdateListener.class,
					sessionId, // Group ID; unique per user and per sketch opened by user (the UUID part does that)
					"sketch-updates-" + sketchId // Topic ID for sketch updates; unique per sketch
		);

		// And another for the collab update listener
		applicationContext.getBean(
					GUICollabUpdateListener.class,
					sessionId, // Group ID; also unique per user per sketch
					"collab-updates-" + sketchId // Topic ID for collaboration updates; also unique per sketch
		);

		// Prepare the pen and the rest of the nodes in the sketch
		pen = new Pen();

		HBox headerHBox = createHeaderHBox();
		ScrollPane drawingCanvasPane = createDrawingCanvasPane();
		VBox.setVgrow(drawingCanvasPane, Priority.ALWAYS);
		HBox toolbarHBox = createToolbarHBox();
		collaborationPane = createCollaborationPane();

		VBox drawingPane = new VBox(headerHBox, drawingCanvasPane, toolbarHBox);
		HBox root = new HBox(drawingPane, collaborationPane);
		HBox.setHgrow(drawingPane, Priority.ALWAYS);

		sketchOnStage = true;

		return root;

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
		shareBtn.setOnAction(e -> {
			TextArea textArea = new TextArea(sketchId);
			textArea.setStyle("""
						-fx-text-fill: gray;
						-fx-font-size: 20pt;
						-fx-font-weight: bold;
						""");
			textArea.setEditable(false);
			textArea.setPrefRowCount(1);
			textArea.setMaxWidth(360);
			textArea.setFocusTraversable(false);

			GridPane gridPane = new GridPane();
			gridPane.add(textArea, 0, 0);

			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Sharing your Sketch...");
			alert.setHeaderText("Copy this ID and share it with your friends! =)");
			alert.getDialogPane().setContent(gridPane);
			alert.showAndWait();
		});
		shareBtn.setPrefHeight(40);

		HBox leftHBox = new HBox(8.0);
		leftHBox.setPadding(new Insets(8.0));
		leftHBox.setAlignment(Pos.CENTER_LEFT);
		leftHBox.getChildren().addAll(shareBtn, exportBtn, createSpacer());

		Label sketchName = new Label(SketchyApplication.currentSketch.getTitle());
		sketchName.setStyle("-fx-font-size: 14pt; -fx-text-fill: gray; -fx-font-weight: bold;");

		Button collabBtn = new Button(" Collaborate");
		collabBtn.setFocusTraversable(false);
		collabBtn.setGraphic(getImage("icons/collab.png"));
		collabBtn.setPrefHeight(40);
		collabBtn.setOnAction(e -> { toggleCollaborationPane(); });

		Button returnBtn = new Button("Return");
		returnBtn.setFocusTraversable(false);
		returnBtn.setStyle("-fx-text-fill: red;");
		returnBtn.setGraphic(getImage("icons/return.png"));
		returnBtn.setPrefHeight(40);
		returnBtn.setOnAction(e -> {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
			alert.setTitle("Return to Sketch List - Confirmation");
			alert.setHeaderText("Are you sure you want to return to the sketch list?");
			alert.setContentText("Your progress is automatically saved.");
			if (alert.showAndWait().get().equals(ButtonType.YES)) {
				cleanUp();
				// Return to sketch list
				mainStage.scene.setRoot(sketchListScene.getRoot());
			}
		});

		HBox rightHBox = new HBox(8.0);
		rightHBox.setPadding(new Insets(8.0));
		rightHBox.setAlignment(Pos.CENTER_RIGHT);
		rightHBox.getChildren().addAll(collabBtn, returnBtn);

		HBox mainHBox = new HBox(leftHBox, createSpacer(), sketchName, createSpacer(), rightHBox);
		mainHBox.setAlignment(Pos.CENTER);
		mainHBox.setFocusTraversable(false);
		return mainHBox;

	}

	private ScrollPane createDrawingCanvasPane() {

		ScrollPane shapesScrollPane = new ScrollPane(); // A scrolling pane for the shapes pane to scroll on
		shapesPane = new Pane(); // Prepare a new pane for the shapes pane
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
		shapesPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
			handler.handleMouseDrag(this, pen, e.getX(), e.getY());
			moveCollabMouse(e.getX(), e.getY());
		});
		shapesPane.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> handler.handleMouseRelease(this, pen, e.getX(), e.getY()));
		shapesPane.addEventHandler(MouseEvent.MOUSE_MOVED, e -> moveCollabMouse(e.getX(), e.getY()));
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
		collaborationPane.setMaxWidth(0);
		collaborationPane.setPrefViewportWidth(0);
		collaborationPane.setMinViewportWidth(0);
		collaborationPane.setFitToWidth(true);
		collaborationPane.setHbarPolicy(ScrollBarPolicy.NEVER);
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

	private void moveCollabMouse(double x, double y) {

		guiCollabUpdateKafkaTemplate.send(
					"collab-updates-" + sketchId,
					"transaction",
					new GUICollabUpdateTransaction(
								sessionId,
								SketchyApplication.currentUser.getName(),
								SketchyApplication.currentUser.getId().toString() + '-' + sessionId,
								x, y, CollabUpdateType.SOMEONE_MOVED //
					) //
		);

	}

	private void cleanUp() {
		// Inform everyone that you're leaving
		guiCollabUpdateKafkaTemplate.send(
					"collab-updates-" + sketchId,
					"transaction",
					new GUICollabUpdateTransaction(
								sessionId,
								null, // Username is not important when you're leaving
								SketchyApplication.currentUser.getId().toString() + '-' + sessionId,
								null, null, CollabUpdateType.SOMEONE_LEFT // You're leaving; so no mouse info.
					)//
		);
		// Alright, bye
		sketchOnStage = false;
	}

	@Override
	public void onApplicationEvent(ShutdownEvent event) {
		if (sketchOnStage) cleanUp();
	}

}
