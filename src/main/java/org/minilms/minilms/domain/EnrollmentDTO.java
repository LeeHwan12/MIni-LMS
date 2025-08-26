package org.minilms.minilms.domain;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class EnrollmentDTO {
    private Long id;
    private Long memberPk;
    private Long courseId;
    private String courseTitle;
    private String instructor;
    private String category;
    private String status;     // 강좌 상태
    private java.time.LocalDateTime enrolledAt;
}
