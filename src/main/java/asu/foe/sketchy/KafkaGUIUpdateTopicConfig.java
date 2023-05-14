package asu.foe.sketchy;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaGUIUpdateTopicConfig {

	@Value(value = "${spring.kafka.bootstrap-servers}")
	private String bootstrapAddress;

	@Bean
	KafkaAdmin kafkaAdmin() {
		Map<String, Object> configs = new HashMap<>();
		configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		return new KafkaAdmin(configs);
	}

	// Creates a topic called "sketch-updates" on application startup
	@Bean
	NewTopic sketchUpdatesTopic() {
		return TopicBuilder.name("sketch-updates")
					.build();
	}

	// Creates a topic called "collab-updates" on application startup
	@Bean
	NewTopic collabUpdatesTopic() {
		return TopicBuilder.name("collab-updates")
					.build();
	}
}
