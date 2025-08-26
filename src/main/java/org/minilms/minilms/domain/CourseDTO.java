package org.minilms.minilms.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CourseDTO {
    private Long courseId;
    private String title;
    private String description;
    private String instructor;
    private String category;
    private String status;   // OPEN / CLOSED
    private LocalDateTime createdAt;
}
