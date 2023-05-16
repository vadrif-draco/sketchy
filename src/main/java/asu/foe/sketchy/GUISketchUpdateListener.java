package asu.foe.sketchy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;

import javafx.application.Platform;

public class GUISketchUpdateListener {

	// Used to refer to the current sketch to invoke updates on it
	@Autowired
	private GUISketchScene currentSketch;

	// This handler will be created with "new", making it invisible to the spring-aop proxy
	// This is helpful so that the aspect doesn't trigger on the incoming changes as well as the outgoing ones
	private GUISketchUpdateHandlerService incomingUpdatesHandler = new GUISketchUpdateHandlerService();

	private final String id;
	private final String topic;

	public GUISketchUpdateListener(String id, String topic) { this.id = id; this.topic = topic; }

	public String getId() { return this.id; }
	public String getTopic() { return this.topic; }

	@KafkaListener(// Topic+partition information for this listener
				topicPartitions = { @TopicPartition(
							// The topic name
							topic = "#{__listener.topic}",
							// The partition offset (that tells it to start from beginning)
							partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0"))
				},
				// The factory used to create the listener
				containerFactory = "guiSketchUpdateKafkaListenerContainerFactory",
				// The group ID for this listener (unique per listener... each group has one listener)
				groupId = "#{__listener.id}")
	public void handleIncomingChanges(GUISketchUpdateTransaction transaction) {
		if (!transaction.getSessionId().equals(currentSketch.sessionId)) {
			// This runnable is used to handle incoming sketch changes in parallel without interrupting the normal usage of the sketch
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					switch (transaction.getUpdateType()) {
					case ADD:
						// All "node addition" operations are handled on the initial mouse press event
						incomingUpdatesHandler.handleMousePress(currentSketch, transaction.getPen(), transaction.getMouseX(), transaction.getMouseY());
						break;
					case EDIT:
						// All "node editing" operations are handled on the mouse drag event
						incomingUpdatesHandler.handleMouseDrag(currentSketch, transaction.getPen(), transaction.getMouseX(), transaction.getMouseY());
						break;
					case REMOVE:
						// All "node removal" operations are handled upon mouse release
						incomingUpdatesHandler.handleMouseRelease(currentSketch, transaction.getPen(), transaction.getMouseX(), transaction.getMouseY());
						break;
					default:
						break;
					}
				}
			});
		}
	}

}
