package asu.foe.sketchy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GUISketchUpdateTransaction {

	public enum UpdateType {
		ADD,
		EDIT,
		REMOVE,
	}

	private long transactionId;
	private Pen pen;
	private double mouseX;
	private double mouseY;
	private UpdateType updateType;

	public GUISketchUpdateTransaction() { super(); }
	public GUISketchUpdateTransaction(long transactionId, Pen pen, double mouseX, double mouseY, UpdateType updateType) {
		super();
		this.transactionId = transactionId;
		this.pen = pen;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.updateType = updateType;
	}

	public void setTransactionId(long transactionId) { this.transactionId = transactionId; }
	public long getTransactionId() { return transactionId; }
	public Pen getPen() { return pen; }
	public void setPen(Pen pen) { this.pen = pen; }
	public double getMouseX() { return mouseX; }
	public void setMouseX(double mouseX) { this.mouseX = mouseX; }
	public double getMouseY() { return mouseY; }
	public void setMouseY(double mouseY) { this.mouseY = mouseY; }
	public UpdateType getUpdateType() { return updateType; }
	public void setUpdateType(UpdateType updateType) { this.updateType = updateType; }

}
