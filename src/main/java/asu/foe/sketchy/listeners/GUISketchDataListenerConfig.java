package asu.foe.sketchy.listeners;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class GUISketchDataListenerConfig {
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	GUISketchDataListener sketchDataListener(String id, String topic) {
		return new GUISketchDataListener(id, topic);
	}
}
