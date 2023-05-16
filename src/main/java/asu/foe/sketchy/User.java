package asu.foe.sketchy;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "user")
public class User {

	@Id
	@Column(name = "id")
	private Long id;
	private String name;
	private String email;
	private String password;
	@OneToMany(mappedBy = "user")
	private Set<UserSketchMap> sketches = new HashSet<>();

	public User() { super(); }
	public User(Long id, String name, String email, String password, Set<UserSketchMap> sketches) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.sketches = sketches;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public Set<UserSketchMap> getSketches() { return sketches; }
	public void setSketches(Set<UserSketchMap> sketches) { this.sketches = sketches; }

}
