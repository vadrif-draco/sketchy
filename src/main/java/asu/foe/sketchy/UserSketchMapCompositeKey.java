package asu.foe.sketchy;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class UserSketchMapCompositeKey implements Serializable {

	@Column(name = "sketch_id")
	private Long sketchId;

	@Column(name = "user_id")
	private Long userId;

	@Override
	public int hashCode() { return Objects.hash(sketchId, userId); }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if ((obj == null) || (getClass() != obj.getClass())) return false;
		UserSketchMapCompositeKey other = (UserSketchMapCompositeKey) obj;
		return Objects.equals(sketchId, other.sketchId) && Objects.equals(userId, other.userId);
	}

	public UserSketchMapCompositeKey() { super(); }
	public UserSketchMapCompositeKey(Long sketchId, Long userId) {
		super();
		this.sketchId = sketchId;
		this.userId = userId;
	}

	public Long getSketchId() { return sketchId; }
	public void setSketchId(Long sketchId) { this.sketchId = sketchId; }
	public Long getUserId() { return userId; }
	public void setUserId(Long userId) { this.userId = userId; }

}
