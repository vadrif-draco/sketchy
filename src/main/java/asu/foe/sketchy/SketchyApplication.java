package asu.foe.sketchy;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javafx.application.Application;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SketchyApplication {

	public static void main(String[] args) {
		// To launch a JavaFX application, you have to use the Application.launch method
		Application.launch(GUIApplication.class, args);
	}

}
