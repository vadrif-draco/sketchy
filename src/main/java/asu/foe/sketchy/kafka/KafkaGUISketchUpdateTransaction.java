package asu.foe.sketchy.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import asu.foe.sketchy.GUIPen;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KafkaGUISketchUpdateTransaction {

	public enum SketchUpdateType {
		ADD,
		EDIT,
		REMOVE,
	}

	private String sessionId;
	private GUIPen pen;
	private double mouseX;
	private double mouseY;
	private SketchUpdateType updateType;

	public KafkaGUISketchUpdateTransaction() { super(); }
	public KafkaGUISketchUpdateTransaction(String sessionId, GUIPen pen, double mouseX, double mouseY, SketchUpdateType updateType) {
		super();
		this.sessionId = sessionId;
		this.pen = pen;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.updateType = updateType;
	}

	public String getSessionId() { return sessionId; }
	public void setSessionId(String sessionId) { this.sessionId = sessionId; }
	public GUIPen getPen() { return pen; }
	public void setPen(GUIPen pen) { this.pen = pen; }
	public double getMouseX() { return mouseX; }
	public void setMouseX(double mouseX) { this.mouseX = mouseX; }
	public double getMouseY() { return mouseY; }
	public void setMouseY(double mouseY) { this.mouseY = mouseY; }
	public SketchUpdateType getUpdateType() { return updateType; }
	public void setUpdateType(SketchUpdateType updateType) { this.updateType = updateType; }

}
