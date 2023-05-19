package asu.foe.sketchy.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;

import asu.foe.sketchy.kafka.KafkaLoggingTransaction;
import asu.foe.sketchy.scenes.GUISketchScene;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;

public class LoggingListener {

	@Autowired
	GUISketchScene sketch;

	private final String id;
	private final String topic;

	public LoggingListener(String id, String topic) { this.id = id; this.topic = topic; }

	public String getId() { return this.id; }
	public String getTopic() { return this.topic; }

	@KafkaListener(topicPartitions = { @TopicPartition(topic = "#{__listener.topic}",//
				partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0"))
	}, containerFactory = "loggingKafkaListenerContainerFactory", groupId = "#{__listener.id}")
	public void handleIncomingChanges(KafkaLoggingTransaction transaction) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Label messageLabel = new Label(transaction.getMessage());
				messageLabel.setPadding(new Insets(8));
				messageLabel.setWrapText(true);
				messageLabel.setStyle("-fx-font-size: 12pt;");
				sketch.getCollaborationPaneContents().getChildren().add(messageLabel);
			}
		});
	}

}
