package org.minilms.minilms.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollment",
       uniqueConstraints = @UniqueConstraint(
               name = "uq_enrollment_member_course", columnNames = {"member_pk","course_id"}
       )
)
@Getter
@Setter
public class Enrollment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="member_pk", nullable=false,
            foreignKey=@ForeignKey(name="fk_enroll_member_pk"))
    private Member member;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="course_id", nullable=false,
            foreignKey=@ForeignKey(name="fk_enroll_course"))
    private Course course;

    @Column(nullable=false, updatable=false)
    private LocalDateTime enrolledAt;
}
