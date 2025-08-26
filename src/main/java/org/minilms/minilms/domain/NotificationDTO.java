package org.minilms.minilms.domain;

import lombok.*;

@Getter @Setter @NoArgsConstructor @Builder @AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private Long memberPk;
    private String message;
    private boolean read;
    private java.time.LocalDateTime createdAt;
}
