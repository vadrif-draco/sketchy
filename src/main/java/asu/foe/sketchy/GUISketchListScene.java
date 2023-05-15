package asu.foe.sketchy;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

@Component
public class GUISketchListScene {

	@Autowired
	private GUIMainStage mainStage;

	@Autowired
	private GUISketchScene sketchScene;

	@Autowired
	private SketchRepository sketchRepository;

	public Parent getRoot() {
		VBox root = new VBox(10);
	    root.setPadding(new Insets(25, 25, 50, 25)); // Add bottom padding of 50
		root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-font-size: 14pt;");

     // Fetch the user's sketches from the repository using the user ID
        List<Sketch> userSketches = sketchRepository.findByUserId(SketchyApplication.currentUser.getId());// Replace 'userId' with the actual user ID

		if (!userSketches.isEmpty()) {
			// Display the user's sketches in a ListView
			ListView<Sketch> sketchListView = new ListView<>(FXCollections.observableArrayList(userSketches));
			sketchListView.setOnMouseClicked(event -> {

				// TODO: @NY Handle sketch selection logic here...

				SketchyApplication.currentSketch = sketchListView.getSelectionModel().getSelectedItem();
				mainStage.scene.setRoot(sketchScene.getRoot());
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
			// TODO: @NY Create a GUI to insert sketch "name" and "description"
			// TODO: @NY Insert this newly created sketch into the sketch repository
			Sketch newSketch = new Sketch(
						new Random().nextLong(), // Randomly generated ID
						"name...", // Sketch name
						"description...", // Sketch description
						SketchyApplication.currentUser // The currently logged in user
			);
			

		    // Save the newSketch in the sketch repository
		    sketchRepository.save(newSketch);

			// set the currentSketch 
			SketchyApplication.currentSketch = newSketch;
			
			// navigate to the sketch scene 
			mainStage.scene.setRoot(sketchScene.getRoot());
		});
		 createSketchButton.setPrefWidth(200); // Set preferred width for the button
		  createSketchButton.setPadding(new Insets(10)); // Add padding to the button
		  root.getChildren().add(createSketchButton);

		return root;
	}

}
