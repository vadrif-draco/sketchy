package asu.foe.sketchy;

import java.util.Random;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javafx.application.Application;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SketchyApplication {

	public static long sessionId = new Random().nextLong();
	public static void main(String[] args) {
		// To launch a JavaFX application, you have to use the Application.launch method
		Application.launch(GUIApplication.class, args);
	}

}
