package org.minilms.minilms.domain;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressDTO {
    private Long id;
    private Long memberPk;
    private Long courseId;
    private double progressPercent;
    private java.time.LocalDateTime updatedAt;
}
