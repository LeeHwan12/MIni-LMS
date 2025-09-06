package org.minilms.minilms.Repository;

import org.minilms.minilms.Entity.Member;
import org.minilms.minilms.domain.ProfileSummaryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MyPageRepository extends JpaRepository<Member, Long> {

    @Query(value = """
        select 
            m.id                          as memberId,
            m.nickname                    as nickname,
            count(distinct c.course_id)   as coursesCount,
            coalesce(avg(p.progress_percent), 0) as avgProgress,
            m.avatar_url                  as avatarUrl
        from member m
        left join course   c on c.instructor = m.member_id
        left join progress p on p.member_pk  = m.id
        where m.id = :memberId
        group by m.id, m.nickname, m.avatar_url
        """, nativeQuery = true)
    org.minilms.minilms.domain.ProfileSummaryView summaryNative(@Param("memberId") Long memberId);
}
