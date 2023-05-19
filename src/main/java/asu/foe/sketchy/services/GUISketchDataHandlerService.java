package asu.foe.sketchy.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import asu.foe.sketchy.SketchyApplication;
import asu.foe.sketchy.persistence.SketchRepository;
import asu.foe.sketchy.scenes.GUISketchScene;
import javafx.scene.control.Label;

@Component
public class GUISketchDataHandlerService {

	@Lazy
	@Autowired
	GUISketchScene sketch;

	// Needed to reflect updates (by this user or by others) to persistent sketch data
	@Autowired
	SketchRepository sketchRepository;

	public void updateSketchData(String newTitle, String newDesc, Boolean internal) {
		// Update data (whatever exists from it)
		if (newTitle != null) SketchyApplication.getCurrentSketch().setTitle(newTitle);
		if (newDesc != null) SketchyApplication.getCurrentSketch().setDescription(newDesc);
		// Persist the changes
		SketchyApplication.setCurrentSketch(sketchRepository.save(SketchyApplication.getCurrentSketch()));
		// Reflect the changes
		((Label) sketch.getHeaderHBox().lookup("#sketchTitle")).setText(SketchyApplication.getCurrentSketch().getTitle());
	}
}
