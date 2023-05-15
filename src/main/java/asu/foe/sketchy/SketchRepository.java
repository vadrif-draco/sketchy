package asu.foe.sketchy;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SketchRepository extends CrudRepository<Sketch, Long> {
	
	    @Query("SELECT s FROM Sketch s JOIN s.user u WHERE u.id = :userId")
	    List<Sketch> findByUserId(@Param("userId") Long userId);

	 
}