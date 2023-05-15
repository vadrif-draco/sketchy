package asu.foe.sketchy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import asu.foe.sketchy.GUIApplication.StageReadyEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@Component
public class GUIMainStage implements ApplicationListener<StageReadyEvent> {


	@Autowired
	@Lazy
	GUIRegistrationScene guiRegistrationScene;

	private Stage stage;
	public Scene scene;

	// The following line from GUIApplication.java:
	// "applicationContext.publishEvent(new StageReadyEvent(primaryStage));"
	// Shall trigger this event handler
	@Override
	public void onApplicationEvent(StageReadyEvent event) {
		stage = event.getStage();
		scene = new Scene(guiRegistrationScene.getRoot());
		stage.setTitle("Sketchy");
		stage.setScene(scene);
		stage.setMinWidth(1280);
		stage.setMinHeight(720);
		stage.show();
	}

}
