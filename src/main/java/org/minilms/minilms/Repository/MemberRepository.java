package org.minilms.minilms.Repository;

import org.minilms.minilms.Entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    // 로그인/조회
    Optional<Member> findByMemberId(String memberId);
    Optional<Member> findByEmail(String email);

    // 중복 체크
    boolean existsByMemberId(String memberId);
    boolean existsByEmail(String email);


    @Query("select m.id from Member m where m.memberId = :memberId")
    Optional<Long> findPkByMemberId(@Param("memberId") String memberId); // ← @Param 이름과 쿼리 이름 일치!
}
