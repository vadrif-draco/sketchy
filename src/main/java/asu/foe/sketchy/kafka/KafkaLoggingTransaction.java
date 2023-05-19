package asu.foe.sketchy.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KafkaLoggingTransaction {

	private String message;

	public KafkaLoggingTransaction() {}
	public KafkaLoggingTransaction(String message) { this.message = message; }

	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }

}
