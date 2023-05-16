package asu.foe.sketchy;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

	User findByEmailAndPassword(String email, String password);

}
