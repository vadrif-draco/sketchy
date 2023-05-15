package asu.foe.sketchy;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface SketchRepository extends CrudRepository<Sketch, Long> {
	
    List<Sketch> findAll();

}