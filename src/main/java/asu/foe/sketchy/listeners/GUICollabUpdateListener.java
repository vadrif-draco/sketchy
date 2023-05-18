package asu.foe.sketchy.listeners;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import asu.foe.sketchy.kafka.KafkaGUICollabUpdateTransaction;
import asu.foe.sketchy.scenes.GUISketchScene;
import asu.foe.sketchy.services.GUICollabUpdateHandlerService;
import javafx.application.Platform;

public class GUICollabUpdateListener {

	@Autowired
	private GUISketchScene currentSketch;

	@Lazy
	@Autowired
	private KafkaTemplate<String, KafkaGUICollabUpdateTransaction> guiCollabUpdateKafkaTemplate;

	private HashMap<String, GUICollabUpdateHandlerService> collabUpdatesHandlerMap = new HashMap<>();

	private final String id;
	private final String topic;

	public GUICollabUpdateListener(String id, String topic) { this.id = id; this.topic = topic; }

	public String getId() { return this.id; }
	public String getTopic() { return this.topic; }

	@KafkaListener(topics = { "#{__listener.topic}" }, groupId = "#{__listener.id}", //
				containerFactory = "guiCollabUpdateKafkaListenerContainerFactory")
	public void handleIncomingChanges(KafkaGUICollabUpdateTransaction transaction) {
		if (!transaction.getSessionId().equals(currentSketch.sessionId)) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					switch (transaction.getCollabUpdateType()) {

					case SOMEONE_MOVED: // If this "someone" doesn't already exist, add them to the handlers map
						GUICollabUpdateHandlerService handler = collabUpdatesHandlerMap.get(transaction.getUserId());
						if (handler == null) {
							collabUpdatesHandlerMap.put(
										transaction.getUserId(),
										new GUICollabUpdateHandlerService(currentSketch, transaction.getUserName()));
							handler = collabUpdatesHandlerMap.get(transaction.getUserId());
						}
						// Then handle their movement
						handler.moveMouse(currentSketch, transaction.getMouseX(), transaction.getMouseY());
						break;

					case SOMEONE_LEFT: // Somebody left. Remove their mouse from the sketch and their handler.
						collabUpdatesHandlerMap
									.get(transaction.getUserId())
									.removeFrom(currentSketch);
						collabUpdatesHandlerMap.remove(transaction.getUserId());
						break;

					default:
						break;
					}
				}
			});
		}
	}
}
