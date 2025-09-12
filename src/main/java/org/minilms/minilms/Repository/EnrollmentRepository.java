package org.minilms.minilms.Repository;

import org.minilms.minilms.Entity.Enrollment;
import org.minilms.minilms.Enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;


public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByCourse_CourseIdAndMember_Id(Long courseId, Long memberId);

    Optional<Enrollment> findByCourse_CourseIdAndMember_Id(Long courseId, Long memberId);

    long countByCourse_CourseId(Long courseId);
    long countByCourse_CourseIdAndStatus(Long courseId, EnrollmentStatus status); // ENROLLED만 카운트용

    @EntityGraph(attributePaths = "course") // N+1 방지
    Page<Enrollment> findByMember_Id(Long memberId, Pageable pageable);
}
