package asu.foe.sketchy.kafka;

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
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConsumersConfig {

	@Value(value = "${spring.kafka.bootstrap-servers}")
	private String bootstrapAddress;

	// For @Value xxx:yyy means yyy is the default value if xxx was not found
	// If you write xxx: , this means the value will default to an empty string

	@Value(value = "${spring.kafka.properties.security.protocol:}")
	private String securityProtocol;

	@Value(value = "${spring.kafka.properties.sasl.mechanism:}")
	private String saslMechanism;

	@Value(value = "${spring.kafka.properties.sasl.jaas.config:}")
	private String jaasConfig;

	Map<String, Object> getCommonConfigProps() {
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
		return configProps;
	}

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, KafkaGUISketchUpdateTransaction> guiSketchUpdateKafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, KafkaGUISketchUpdateTransaction> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(getCommonConfigProps()));
		return factory;
	}

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, KafkaGUICollabUpdateTransaction> guiCollabUpdateKafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, KafkaGUICollabUpdateTransaction> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(getCommonConfigProps()));
		return factory;
	}

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, KafkaGUISketchDataTransaction> guiSketchDataKafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, KafkaGUISketchDataTransaction> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(getCommonConfigProps()));
		return factory;
	}

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, KafkaLoggingTransaction> loggingKafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, KafkaLoggingTransaction> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(getCommonConfigProps()));
		return factory;
	}

}
