package asu.foe.sketchy.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KafkaGUISketchDataTransaction {

	// For this transaction we don't need a sessionId since it doesn't matter if it is consumed by its producer

	private String title;
	private String desc;

	public KafkaGUISketchDataTransaction() { super(); }
	public KafkaGUISketchDataTransaction(String title, String desc) { super(); this.title = title; this.desc = desc; }

	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	public String getDesc() { return desc; }
	public void setDesc(String desc) { this.desc = desc; }

}
