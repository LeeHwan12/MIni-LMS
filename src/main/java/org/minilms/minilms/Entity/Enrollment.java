package org.minilms.minilms.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.minilms.minilms.Enums.EnrollmentStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollment",
        uniqueConstraints = @UniqueConstraint(name="uq_enrollment_member_course",
                columnNames = {"member_pk","course_id"}))
@Getter @Setter
public class Enrollment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="member_pk", nullable=false)
    private Member member; // member(id) 가정

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="course_id", nullable=false)
    private Course course;

    // 상태/이력 쓰는 경우
    @Enumerated(EnumType.STRING)
    @Column(length=12, nullable=false)
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    @Column(name="enrolled_at", nullable=false)
    private LocalDateTime enrolledAt;

    @Column(name="canceled_at")
    private LocalDateTime canceledAt;

    @Column(name="updated_at", nullable=false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (enrolledAt == null) enrolledAt = now;
        if (updatedAt == null)  updatedAt = now;
    }
    @PreUpdate
    void onUpdate() { updatedAt = LocalDateTime.now(); }
}
