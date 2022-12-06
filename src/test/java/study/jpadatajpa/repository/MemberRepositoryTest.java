package study.jpadatajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnitUtil;
import org.assertj.core.api.Assertions;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.jpadatajpa.entity.Member;
import study.jpadatajpa.entity.Team;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();
        assertThat(findMember.getId()).isEqualTo(member.getId());

        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member); //JPA 엔티티 동일성 보장
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);
        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);
        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);
        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);
        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("aaa", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("aaa", "bbb"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void paging() {

        int age = 10;
        int offset = 0;
        int limit = 3;

        memberRepository.save(new Member("member1", age));
        memberRepository.save(new Member("member2", age));
        memberRepository.save(new Member("member3", age));
        memberRepository.save(new Member("member4", age));
        memberRepository.save(new Member("member5", age));

        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        List<Member> contents = page.getContent();
        for (Member member : contents) {
            System.out.println("member = " + member);
        }

        assertThat(contents.size()).isEqualTo(limit);
        assertThat(page.getSize()).isEqualTo(limit);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0); // page 숫자
        assertThat(page.getTotalPages()).isEqualTo(2); // 총 page 숫자
        assertThat(page.isFirst()).isEqualTo(true);
        assertThat(page.hasNext()).isEqualTo(true);

    }

    @Test
    public void bulkUpdate() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);
//        em.flush(); // 영속성 컨텍스트에 혹시나 남아 있는 변경사항을 DB에 반영
        em.clear(); // 영속성 컨텍스트 안의 데이터를 모두 날림

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    @DisplayName("findMemberLazy")
    public void findMemberLazy() throws Exception {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member memberA = new Member("memberA");
        Member memberB = new Member("memberB");
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        em.flush();
        em.clear();

        // when
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            //Hibernate 기능으로 확인
            Hibernate.isInitialized(member.getTeam());

            //JPA 표준 방법으로 확인
            PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
            util.isLoaded(member.getTeam());
        }

        // then
    }

    @Test
    @DisplayName("queryHint")
    public void queryHint() throws Exception {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        Member foundMember = memberRepository.findById(member1.getId()).get();
        foundMember.setUsername("member2");
        em.flush();

        // then
    }
}