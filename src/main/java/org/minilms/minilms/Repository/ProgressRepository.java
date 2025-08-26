package org.minilms.minilms.Repository;

import org.minilms.minilms.Entity.Progress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
    Optional<Progress> findByMember_IdAndCourse_CourseId(Long memberPk, Long courseId);

    @Query("select coalesce(avg(p.percent),0) from Progress p where p.member.id = :memberPk")
    double avgByMember(@Param("memberPk") Long memberPk);

    Page<Progress> findByMember_Id(Long memberPk, Pageable pageable);
}
