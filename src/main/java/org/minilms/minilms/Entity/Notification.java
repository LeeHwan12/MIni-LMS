package org.minilms.minilms.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification",
        indexes = @Index(name="idx_notif_member_pk_created", columnList="member_pk, created_at"))
@Getter @Setter
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="member_pk", nullable=false,
            foreignKey=@ForeignKey(name="fk_notif_member_pk"))
    private Member member;

    @Column(nullable=false, length=255)
    private String message;

    @Column(name="is_read", nullable=false)
    private boolean read = false;

    @Column(nullable=false, updatable=false)
    private LocalDateTime createdAt;
}
