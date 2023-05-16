package asu.foe.sketchy;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "sketch")
public class Sketch {

	@Id
	@Column(name = "id")
	private Long id;
	private String title;
	private String description;
	@OneToMany(mappedBy = "sketch")
	private Set<UserSketchMap> users = new HashSet<>();

	public Sketch() { super(); }
	public Sketch(Long id, String title, String description, Set<UserSketchMap> users) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.users = users;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public Set<UserSketchMap> getUsers() { return users; }
	public void setUsers(Set<UserSketchMap> users) { this.users = users; }

}
