package asu.foe.sketchy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LoggingRepository extends JpaRepository<Log, Long> {
}