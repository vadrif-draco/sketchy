package asu.foe.sketchy.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_sketch_map")
public class UserSketchMap {

	@EmbeddedId
	UserSketchMapCompositeKey id;

	@ManyToOne
	@MapsId("sketchId")
	@JoinColumn(name = "sketch_id")
	private Sketch sketch;

	@ManyToOne
	@MapsId("userId")
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "access_level")
	private int accessLevel;

	public UserSketchMap() { super(); }
	public UserSketchMap(UserSketchMapCompositeKey id, Sketch sketch, User user, int accessLevel) {
		super();
		this.id = id;
		this.sketch = sketch;
		this.user = user;
		this.accessLevel = accessLevel;
	}

	public UserSketchMapCompositeKey getId() { return id; }
	public void setId(UserSketchMapCompositeKey id) { this.id = id; }
	public Sketch getSketch() { return sketch; }
	public void setSketch(Sketch sketch) { this.sketch = sketch; }
	public User getUser() { return user; }
	public void setUser(User user) { this.user = user; }
	public int getAccessLevel() { return accessLevel; }
	public void setAccessLevel(int accessLevel) { this.accessLevel = accessLevel; }

}
