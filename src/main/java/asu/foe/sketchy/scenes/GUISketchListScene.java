package asu.foe.sketchy.scenes;

import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import asu.foe.sketchy.GUIMainStage;
import asu.foe.sketchy.SketchyApplication;
import asu.foe.sketchy.kafka.KafkaGUISketchDataTransaction;
import asu.foe.sketchy.persistence.Sketch;
import asu.foe.sketchy.persistence.SketchRepository;
import asu.foe.sketchy.persistence.UserSketchMap;
import asu.foe.sketchy.persistence.UserSketchMapCompositeKey;
import asu.foe.sketchy.persistence.UserSketchMapRepository;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

@Component
public class GUISketchListScene {

	@Autowired
	private GUIMainStage mainStage;

	@Autowired
	private GUISketchScene sketchScene;

	@Autowired
	private SketchRepository sketchRepository;

	@Autowired
	private UserSketchMapRepository userSketchMapRepository;

	@Autowired
	private KafkaTemplate<String, KafkaGUISketchDataTransaction> guiSketchDataKafkaTemplate;

	public Parent getRoot() {
		VBox root = new VBox(10);
		root.setPadding(new Insets(25, 100, 50, 100)); // Add bottom padding of 50
		root.setAlignment(Pos.CENTER);
		root.setStyle("-fx-font-size: 14pt;");

		// Fetch the user's sketches from the repository using the user ID
		Set<Sketch> userSketches = userSketchMapRepository.findByUserId(SketchyApplication.currentUser.getId());

		if (!userSketches.isEmpty()) {
			// Display the user's sketches in a ListView of Sketch objects
			ListView<Sketch> sketchListView = new ListView<>(FXCollections.observableArrayList(userSketches));
			// Adding a border to the list view
			sketchListView.setStyle("-fx-border-color: gray;");
			// Tell the ListView to use the title field of the Sketch objects when rendering
			sketchListView.setCellFactory(new Callback<ListView<Sketch>, ListCell<Sketch>>() {
				@Override
				public ListCell<Sketch> call(ListView<Sketch> listView) {
					return new ListCell<>() {
						@Override
						public void updateItem(Sketch item, boolean empty) {
							super.updateItem(item, empty);
							setText(item == null ? null : item.getTitle());
						}
					};
				}
			});
			sketchListView.setOnMouseClicked(event -> {
				SketchyApplication.currentSketch = sketchListView.getSelectionModel().getSelectedItem();
				if (SketchyApplication.currentSketch != null) mainStage.scene.setRoot(sketchScene.getRoot());
			});
			root.getChildren().add(sketchListView);
		} else {
			Label label = new Label("You have no sketches yet! :(");
			label.setStyle("-fx-font-size: 20px; -fx-text-fill: gray; -fx-font-weight: bold;");
			label.setPadding(new Insets(16, 16, 16, 16));
			root.getChildren().add(label);
		}

		Button createSketchButton = new Button("Create New Sketch");
		createSketchButton.setOnAction(event -> {
			// TODO: @NY Create a GUI to insert "description"... the following TextInputDialog is for "name" only
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("Creating a new Sketch...");
			dialog.setHeaderText("Please enter a name for your new Sketch:");
			dialog.setContentText("Name:");
			Optional<String> result = dialog.showAndWait();
			if (result.isEmpty() || result.get().length() < 1) {
				Alert alert = new Alert(AlertType.ERROR, "Please enter a valid sketch name!");
				alert.showAndWait();
				return;
			}
			Long sketchId = new Random().nextLong(0, Long.MAX_VALUE);

			// For a many-to-many relationship, we need entities from three tables
			// For our case, we need the User (already have it), the Sketch, and the mapping

			// Let's create the sketch then insert it
			Sketch newSketch = new Sketch();
			newSketch.setId(sketchId);
			newSketch.setTitle(result.get());
			newSketch.setDescription("description...");
			SketchyApplication.currentSketch = sketchRepository.save(newSketch);

			// And now the mapping
			UserSketchMap userSketchMap = new UserSketchMap();
			userSketchMap.setId(new UserSketchMapCompositeKey());
			userSketchMap.setUser(SketchyApplication.currentUser);
			userSketchMap.setSketch(SketchyApplication.currentSketch);
			userSketchMap.setAccessLevel(0); // 0 means observer, lowest access level
			userSketchMapRepository.save(userSketchMap);

			// Save the newSketch in the sketch repository and set it as the current sketch
			SketchyApplication.currentSketch = sketchRepository.save(newSketch);

			// Create a SketchDataTransaction with the data of this new sketch
			KafkaGUISketchDataTransaction transaction = new KafkaGUISketchDataTransaction(
						SketchyApplication.currentSketch.getTitle(),
						SketchyApplication.currentSketch.getDescription());

			// Publish an event with this transaction on the sketch data topic for this sketch (identified by its ID)
			guiSketchDataKafkaTemplate.send("sketch-data-" + SketchyApplication.currentSketch.getId(), "transaction", transaction);

			// Finally, navigate to the sketch scene
			mainStage.scene.setRoot(sketchScene.getRoot());
		});
		createSketchButton.setPrefWidth(240); // Set preferred width for the button
		createSketchButton.setPadding(new Insets(10)); // Add padding to the button

		Button openSketchWithIdButton = new Button("Open Sketch with ID");
		openSketchWithIdButton.setOnAction(event -> {
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("Opening an existing Sketch...");
			dialog.setHeaderText("Please enter the ID of the sketch shared with you:");
			dialog.setContentText("ID:");
			Optional<String> result = dialog.showAndWait();
			if (result.isEmpty()) return;
			String resultString = result.get().trim();
			System.out.println(resultString);
			if (!resultString.matches("^[1-9][0-9]+$")) {
				Alert alert = new Alert(AlertType.ERROR, "Invalid Sketch ID!");
				alert.showAndWait();
				return;
			}
			Sketch newSketch = new Sketch();
			newSketch.setId(Long.parseLong(resultString));
			newSketch.setTitle("Loading..."); // Will be fetched when the user opens the sketch
			newSketch.setDescription("Loading..."); // Will be fetched when the user opens the sketch

			// Save the newSketch in the sketch repository and set it as the current sketch
			SketchyApplication.currentSketch = sketchRepository.save(newSketch);

			// Add a mapping between the current user and this new sketch and also save it
			UserSketchMap map = new UserSketchMap();
			map.setId(new UserSketchMapCompositeKey());
			map.setUser(SketchyApplication.currentUser);
			map.setSketch(SketchyApplication.currentSketch);
			userSketchMapRepository.save(map);

			// Navigate to the sketch scene
			mainStage.scene.setRoot(sketchScene.getRoot());
		});
		openSketchWithIdButton.setPrefWidth(240);
		openSketchWithIdButton.setPadding(new Insets(10));

		HBox buttonsHBox = new HBox(createSketchButton, openSketchWithIdButton);
		buttonsHBox.setSpacing(32);
		buttonsHBox.setPadding(new Insets(32));
		buttonsHBox.setAlignment(Pos.CENTER);
		root.getChildren().add(buttonsHBox);

		return root;
	}

}
