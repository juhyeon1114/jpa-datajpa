package study.jpadatajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.jpadatajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
