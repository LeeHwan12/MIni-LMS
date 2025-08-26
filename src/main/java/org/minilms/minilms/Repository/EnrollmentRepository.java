package org.minilms.minilms.Repository;

import org.minilms.minilms.Entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByMember_IdAndCourse_CourseId(Long memberPk, Long courseId);
    int countByMember_Id(Long memberPk);
    Page<Enrollment> findByMember_Id(Long memberPk, Pageable pageable);
}
