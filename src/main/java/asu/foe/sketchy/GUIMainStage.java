package asu.foe.sketchy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import asu.foe.sketchy.GUIApplication.StageReadyEvent;

@Component
public class GUIMainStage implements ApplicationListener<StageReadyEvent> {

	@Autowired
	GUISketchScene guiSketchScene;
	@Autowired
	GUIRegistrationScene guiRegistrationScene;

	@Autowired
	GUILoginScene guiLoginScene;

	// The following line from GUIApplication.java:
	// "applicationContext.publishEvent(new StageReadyEvent(primaryStage));"
	// Shall trigger this event handler
	@Override
	public void onApplicationEvent(StageReadyEvent event) {
		event.getStage().setTitle("Sketchy");
		guiRegistrationScene.setStage(event.getStage());
		event.getStage().setScene(guiRegistrationScene.getScene());
		event.getStage().setMinWidth(1280);
		event.getStage().setMinHeight(720);
		event.getStage().show();
	}

}
