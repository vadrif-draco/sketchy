package asu.foe.sketchy;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaGUIUpdateConsumerConfig {

	@Value(value = "${spring.kafka.bootstrap-servers}")
	private String bootstrapAddress;

	@Value(value = "${spring.kafka.properties.security.protocol:}")
	private String securityProtocol;

	@Value(value = "${spring.kafka.properties.sasl.mechanism:}")
	private String saslMechanism;

	@Value(value = "${spring.kafka.properties.sasl.jaas.config:}")
	private String jaasConfig;

	@Bean
	ConsumerFactory<String, GUISketchUpdateTransaction> guiSketchUpdateConsumerFactory() {

		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		// Security! (if exists)
		if (!securityProtocol.equals("")) {
			configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
			configProps.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
			configProps.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);
		}
		return new DefaultKafkaConsumerFactory<>(configProps);
	}

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, GUISketchUpdateTransaction> guiSketchUpdateKafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, GUISketchUpdateTransaction> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(guiSketchUpdateConsumerFactory());
		return factory;
	}

	@Bean
	ConsumerFactory<String, GUICollabUpdateTransaction> guiCollabUpdateConsumerFactory() {

		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		// Security! (if exists)
		if (!securityProtocol.equals("")) {
			configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
			configProps.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
			configProps.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);
		}
		return new DefaultKafkaConsumerFactory<>(configProps);
	}

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, GUICollabUpdateTransaction> guiCollabUpdateKafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, GUICollabUpdateTransaction> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(guiCollabUpdateConsumerFactory());
		return factory;
	}

}
