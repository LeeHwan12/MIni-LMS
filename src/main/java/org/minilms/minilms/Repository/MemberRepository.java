package org.minilms.minilms.Repository;

import org.minilms.minilms.Entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    // 로그인/조회
    Optional<Member> findByMemberId(String memberId);
    Optional<Member> findByEmail(String email);

    // 중복 체크
    boolean existsByMemberId(String memberId);
    boolean existsByEmail(String email);

    // 페이징 조회 (기본 findAll(Pageable)가 이미 있으니 사실 불필요)
    Page<Member> findAll(Pageable pageable);
}
