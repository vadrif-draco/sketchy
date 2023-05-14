package asu.foe.sketchy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GUISketchUpdateTransaction {

	public enum UpdateType {
		ADD,
		EDIT,
		REMOVE,
	}

	private long sessionId;
	private long timestamp;
	private Pen pen;
	private double mouseX;
	private double mouseY;
	private UpdateType updateType;

	public GUISketchUpdateTransaction() { super(); }
	public GUISketchUpdateTransaction(long sessionId, long timestamp, Pen pen, double mouseX, double mouseY, UpdateType updateType) {
		super();
		this.sessionId = sessionId;
		this.timestamp = timestamp;
		this.pen = pen;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.updateType = updateType;
	}

	public long getSessionId() { return sessionId; }
	public void setSessionId(long sessionId) { this.sessionId = sessionId; }
	public long getTimestamp() { return timestamp; }
	public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
	public Pen getPen() { return pen; }
	public void setPen(Pen pen) { this.pen = pen; }
	public double getMouseX() { return mouseX; }
	public void setMouseX(double mouseX) { this.mouseX = mouseX; }
	public double getMouseY() { return mouseY; }
	public void setMouseY(double mouseY) { this.mouseY = mouseY; }
	public UpdateType getUpdateType() { return updateType; }
	public void setUpdateType(UpdateType updateType) { this.updateType = updateType; }

}
