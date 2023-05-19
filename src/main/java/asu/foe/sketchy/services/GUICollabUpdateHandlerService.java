package asu.foe.sketchy.services;

import asu.foe.sketchy.scenes.GUISketchScene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class GUICollabUpdateHandlerService {

	private HBox cursor;

	public GUICollabUpdateHandlerService(GUISketchScene sketch, String userName) {
		sketch.setNumOfActiveCollaborators(sketch.getNumOfActiveCollaborators() + 1);
		Label userNameLabel = new Label(userName);
		userNameLabel.setStyle("-fx-font-size: 12pt; -fx-font-weight: bold; -fx-background-color: rgba(0,0,0,0);");
		ImageView cursorImageView = new ImageView(new Image("icons/cursor.png"));
		cursorImageView.setFitHeight(24);
		cursorImageView.setPreserveRatio(true);
		cursor = new HBox(cursorImageView, userNameLabel);
		cursor.setSpacing(8);
		sketch.getShapesPane().getChildren().add(cursor);
		sketch.setNumOfActiveCollaborators(sketch.getNumOfActiveCollaborators() + 1);
	}

	public void moveMouse(GUISketchScene sketch, double x, double y) {
		cursor.setTranslateX(x);
		cursor.setTranslateY(y);
	}

	public void removeFrom(GUISketchScene sketch) {
		sketch.getShapesPane().getChildren().remove(cursor);
		sketch.setNumOfActiveCollaborators(sketch.getNumOfActiveCollaborators() - 1);
	}
}
