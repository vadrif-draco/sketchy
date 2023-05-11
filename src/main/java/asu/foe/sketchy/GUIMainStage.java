package asu.foe.sketchy;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import asu.foe.sketchy.GUIApplication.StageReadyEvent;

@Component
public class GUIMainStage implements ApplicationListener<StageReadyEvent> {

	// The following line from GUIApplication.java:
	// "applicationContext.publishEvent(new StageReadyEvent(primaryStage));"
	// Shall trigger this event handler
	@Override
	public void onApplicationEvent(StageReadyEvent event) {
		event.getStage().setTitle("Sketchy");
		event.getStage().show();
	}
}
