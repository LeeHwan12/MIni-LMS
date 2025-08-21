package org.minilms.minilms.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(
        name = "member",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_member_member_id", columnNames = "member_id"),
                @UniqueConstraint(name = "uq_member_email",     columnNames = "email"),
                @UniqueConstraint(name = "uq_member_nickname",  columnNames = "nickname")
        }
)
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 로그인 ID (별칭) */
    @Column(name = "member_id", nullable = false, length = 50)
    private String memberId;

    /** 해시된 비밀번호(예: BCrypt 60자 이상) */
    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "nickname", nullable = false, length = 30)
    private String nickname;

    @Column(name = "email", nullable = false, length = 320)
    private String email;

    @Column(name = "age")
    private Integer age;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /* ====== 도메인 행위(Setter 대신 의미 있는 변경 메서드) ====== */

    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    public void enable()  { this.enabled = true;  }
    public void disable() { this.enabled = false; }
}
