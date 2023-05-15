package asu.foe.sketchy;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    @SuppressWarnings("unchecked")
    User save(User user);

    User findByEmailAndPassword(String email, String password);

	
}