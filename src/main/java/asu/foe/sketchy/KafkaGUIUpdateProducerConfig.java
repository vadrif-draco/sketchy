package asu.foe.sketchy;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaGUIUpdateProducerConfig {

	@Value(value = "${spring.kafka.bootstrap-servers}")
	private String bootstrapAddress;

	@Value(value = "${spring.kafka.properties.security.protocol:}")
	private String securityProtocol;

	@Value(value = "${spring.kafka.properties.sasl.mechanism:}")
	private String saslMechanism;

	@Value(value = "${spring.kafka.properties.sasl.jaas.config:}")
	private String jaasConfig = null;

	@Bean
	ProducerFactory<String, GUISketchUpdateTransaction> guiSketchUpdateProducerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		// Security! (if exists)
		if (!securityProtocol.equals("")) {
			configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
			configProps.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
			configProps.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);
		}
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	KafkaTemplate<String, GUISketchUpdateTransaction> guiSketchUpdateKafkaTemplate() {
		return new KafkaTemplate<>(guiSketchUpdateProducerFactory());
	}

	@Bean
	ProducerFactory<String, GUICollabUpdateTransaction> guiCollabUpdateProducerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		// Security! (if exists)
		if (!securityProtocol.equals("")) {
			configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
			configProps.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
			configProps.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);
		}
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	KafkaTemplate<String, GUICollabUpdateTransaction> guiCollabUpdateKafkaTemplate() {
		return new KafkaTemplate<>(guiCollabUpdateProducerFactory());
	}
}
