package org.minilms.minilms.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.minilms.minilms.Enums.courseStatus;
import org.springframework.data.annotation.CreatedBy;

@Entity
@Table(name = "course")
@Getter @Setter
public class Course {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100, nullable = false)
    private String instructor;

    @Column(length = 50)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private courseStatus status = courseStatus.OPEN; // enum 상수 소문자 유지

    @Column(nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "thumbnail_url", length = 255)
    private String thumbnailUrl;

    @CreatedBy
    @Column(updatable = false, length = 100)
    private String createdBy;   // 등록자 (username)

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = java.time.LocalDateTime.now();
        }
        if (status == null) {
            status = courseStatus.OPEN;
        }
    }
}
