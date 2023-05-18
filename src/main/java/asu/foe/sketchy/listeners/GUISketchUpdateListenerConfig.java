package asu.foe.sketchy.listeners;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class GUISketchUpdateListenerConfig {

	// By default, the scope for Beans in Spring framework is Singleton
	// However, for this Bean we want to create a different parameterized instance every time
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	GUISketchUpdateListener sketchUpdateListener(String id, String topic) {
		return new GUISketchUpdateListener(id, topic);
	}

}
