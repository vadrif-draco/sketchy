package asu.foe.sketchy.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import asu.foe.sketchy.GUIMainStage;
import asu.foe.sketchy.scenes.GUILoginScene;
import asu.foe.sketchy.scenes.GUISketchListScene;



@Component
public class AuthService {
	
	@Autowired
	private GUIMainStage mainStage;

	@Autowired
	@Lazy
	private GUILoginScene loginScene;
	
	@Autowired
	private GUISketchListScene sketchListScene;
	
	public void login() {
		mainStage.scene.setRoot(sketchListScene.getRoot());

	}
	
	public void register() {
		mainStage.scene.setRoot(loginScene.getRoot());
		
	}
	

}
