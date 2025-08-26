package org.minilms.minilms.Repository;

import org.minilms.minilms.Entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;




public interface CourseRepository extends JpaRepository<Course, Long> {
    Page<Course> findByInstructorContainingIgnoreCase(String instructor, Pageable pageable);
    @Query("""
        select c from Course c
        where (:kw is null or lower(c.title) like lower(concat('%', :kw, '%')))
          and (:cat is null or c.category = :cat)
        """)
    Page<Course> search(@Param("kw") String keyword, @Param("cat") String category, Pageable pageable);
}
