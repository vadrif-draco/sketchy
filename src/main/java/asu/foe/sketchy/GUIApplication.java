package asu.foe.sketchy;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

// Every JavaFX application must extend "javafx.application.Application"
public class GUIApplication extends Application { // JavaFX Application

	private ConfigurableApplicationContext applicationContext;

	@Override
	public void init() {
		// This is where we need to initialise our application context...
		// This is to connect it with the Spring framework.
		// This way, when the JavaFX Application runs, it runs within Spring.
		applicationContext = new SpringApplicationBuilder(SketchyApplication.class).run();
	}

	@Override
	public void stop() {
		// Since we have an init() method, we should cleanup too.
		applicationContext.publishEvent(new ShutdownEvent(this));
		applicationContext.close();
		Platform.exit();
	}

	// Any JavaFX application must override this method.
	@Override
	public void start(Stage primaryStage) throws Exception {
		// We can use the Spring pattern of publishing events via the
		// application context to signal when this Stage is ready.
		// (https://www.baeldung.com/spring-events)
		applicationContext.publishEvent(new StageReadyEvent(primaryStage));
		// Now the "stage" is ready
		// Inside a stage we place a "scene"
		// Inside a scene we place multiple "controls" (such as Buttons, Labels, etc.)
		// To organize these controls, we use "layouts" (such as arrange them in a Grid)
	}

	public static class StageReadyEvent extends ApplicationEvent {
		private static final long serialVersionUID = 1L;
		public StageReadyEvent(Stage stage) { super(stage); }
		public Stage getStage() { return ((Stage) getSource()); }
	}

	public static class ShutdownEvent extends ApplicationEvent {
		private static final long serialVersionUID = 1L;
		public ShutdownEvent(Object source) { super(source); }
	}

}
