package asu.foe.sketchy.listeners;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class LoggingListenerConfig {

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	LoggingListener loggingListener(String id, String topic) {
		return new LoggingListener(id, topic);
	}

}
