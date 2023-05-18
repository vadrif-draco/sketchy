package asu.foe.sketchy.persistence;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

	User findByEmailAndPassword(String email, String password);

}
