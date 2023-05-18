package asu.foe.sketchy.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import asu.foe.sketchy.kafka.KafkaGUISketchDataTransaction;
import asu.foe.sketchy.scenes.GUISketchScene;
import javafx.application.Platform;

public class GUISketchDataListener {

	@Autowired
	private GUISketchScene currentSketch;

	private final String id;
	private final String topic;

	public GUISketchDataListener(String id, String topic) { this.id = id; this.topic = topic; }

	public String getId() { return this.id; }
	public String getTopic() { return this.topic; }

	@KafkaListener(topics = { "#{__listener.topic}" }, groupId = "#{__listener.id}", //
				containerFactory = "guiSketchDataKafkaListenerContainerFactory")
	public void handleIncomingChanges(KafkaGUISketchDataTransaction transaction) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				currentSketch.updateSketchData(transaction.getTitle(), transaction.getDesc());
			}
		});
	}
}
