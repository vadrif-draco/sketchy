package asu.foe.sketchy;

public class GUICollabUpdateTransaction {

	public enum CollabUpdateType {
		SOMEONE_MOVED,
		SOMEONE_LEFT,
	}

	private String sessionId;
	private String userName;
	private String userId;
	private Double mouseX;
	private Double mouseY;
	private CollabUpdateType collabUpdateType;

	public GUICollabUpdateTransaction() { super(); }
	public GUICollabUpdateTransaction(String sessionId, String userName, String userId, Double mouseX, Double mouseY, CollabUpdateType collabUpdateType) {
		super();
		this.sessionId = sessionId;
		this.userName = userName;
		this.userId = userId;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.collabUpdateType = collabUpdateType;
	}

	public String getSessionId() { return sessionId; }
	public void setSessionId(String sessionId) { this.sessionId = sessionId; }
	public String getUserName() { return userName; }
	public void setUserName(String userName) { this.userName = userName; }
	public String getUserId() { return userId; }
	public void setUserId(String userId) { this.userId = userId; }
	public Double getMouseX() { return mouseX; }
	public void setMouseX(Double mouseX) { this.mouseX = mouseX; }
	public Double getMouseY() { return mouseY; }
	public void setMouseY(Double mouseY) { this.mouseY = mouseY; }
	public CollabUpdateType getCollabUpdateType() { return collabUpdateType; }
	public void setCollabUpdateType(CollabUpdateType collabUpdateType) { this.collabUpdateType = collabUpdateType; }

}
