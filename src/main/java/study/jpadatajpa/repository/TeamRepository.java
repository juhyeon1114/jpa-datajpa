package study.jpadatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.jpadatajpa.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
}