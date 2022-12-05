package study.jpadatajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.jpadatajpa.dto.MemberDto;
import study.jpadatajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsername(String username);

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.jpadatajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    //    List<Member> findByUsername(String name); //컬렉션
//    Member findByUsername(String name); //단건
//    Optional<Member> findByUsername(String name); //단건 Optional


    /**
     * Page, Slice 기본 사용법
     */
//    Page<Member> findByAge(int age, Pageable pageable);
//    Slice<Member> findByUsername(String name, Pageable pageable); //count 쿼리 사용 안함
//    List<Member> findByUsername(String name, Pageable pageable); //count 쿼리 사용 안함

    /**
     * 카운트 쿼리 최적화
     * - 카운트 쿼리는 join같은 것을 할 필요가 없으므로 최적화의 여지가 있다.
     */
//    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m) from Member m")
    @Query(countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);



}
