package org.minilms.minilms.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class EnrollmentDTO {
    private Long enrollmentId;
    private Long courseId;
    private String title;
    private String instructor;
    private String category;
    private String status;          // ENROLLED/CANCELED
    private LocalDateTime enrolledAt;
    private Long memberId;          // 관리자 화면용
    private String memberUsername;  // 선택
}
