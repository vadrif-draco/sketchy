package asu.foe.sketchy;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

@Component
public class GUISketchListScene {
	
	 @Autowired
	 private GUIMainStage mainStage;
	 
	    @Autowired
	    private SketchRepository sketchRepository;
	    
		public Parent getRoot() {
			VBox root = new VBox(10);
			root.setPadding(new Insets(25));
			root.setAlignment(Pos.CENTER);

			// Fetch the user's sketches from the repository
			List<Sketch> userSketches = sketchRepository.findAll(); // Fetch all sketches from the repository

			if (!userSketches.isEmpty()) {
						
				// Display the user's sketches in a ListView
				ListView<Sketch> sketchListView = new ListView<>(FXCollections.observableArrayList(userSketches));
				sketchListView.setOnMouseClicked(event -> {
					// TODO Handle sketch selection logic here...
					//TODO Salama

					Sketch selectedSketch = sketchListView.getSelectionModel().getSelectedItem();
					// Navigate to the selected sketch
					navigateToSketch(selectedSketch);
				});
				root.getChildren().add(sketchListView);	
			}

			Button createSketchButton = new Button("Create New Sketch");
			createSketchButton.setOnAction(event -> {
				// Handle create new sketch logic here....
				//TODO Salama
			});
			root.getChildren().add(createSketchButton);
			
			return root;
		}


		private void navigateToSketch(Sketch sketch) {
			// TODO Logic to navigate to the selected sketch (sketches should be clickable)
		}
	}