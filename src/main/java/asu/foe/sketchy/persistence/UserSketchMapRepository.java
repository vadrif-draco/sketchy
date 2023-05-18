package asu.foe.sketchy.persistence;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserSketchMapRepository extends CrudRepository<UserSketchMap, UserSketchMapCompositeKey> {

	@Query("SELECT s FROM Sketch s WHERE s.id IN (SELECT usm.sketch.id FROM UserSketchMap usm WHERE usm.user.id = ?1)")
	Set<Sketch> findByUserId(Long userId);

}
