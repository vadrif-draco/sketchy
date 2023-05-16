package asu.foe.sketchy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GUISketchUpdateTransaction {

	public enum SketchUpdateType {
		ADD,
		EDIT,
		REMOVE,
	}

	private String sessionId;
	private long timestamp;
	private Pen pen;
	private double mouseX;
	private double mouseY;
	private SketchUpdateType updateType;

	public GUISketchUpdateTransaction() { super(); }
	public GUISketchUpdateTransaction(String sessionId, long timestamp, Pen pen, double mouseX, double mouseY, SketchUpdateType updateType) {
		super();
		this.sessionId = sessionId;
		this.timestamp = timestamp;
		this.pen = pen;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.updateType = updateType;
	}

	public String getSessionId() { return sessionId; }
	public void setSessionId(String sessionId) { this.sessionId = sessionId; }
	public long getTimestamp() { return timestamp; }
	public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
	public Pen getPen() { return pen; }
	public void setPen(Pen pen) { this.pen = pen; }
	public double getMouseX() { return mouseX; }
	public void setMouseX(double mouseX) { this.mouseX = mouseX; }
	public double getMouseY() { return mouseY; }
	public void setMouseY(double mouseY) { this.mouseY = mouseY; }
	public SketchUpdateType getUpdateType() { return updateType; }
	public void setUpdateType(SketchUpdateType updateType) { this.updateType = updateType; }

}
