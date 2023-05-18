package asu.foe.sketchy.aspects;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoggingRepository extends JpaRepository<Log, Long> {}
