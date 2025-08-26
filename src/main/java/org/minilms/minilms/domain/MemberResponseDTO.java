package org.minilms.minilms.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberResponseDTO {
    private Long id;
    private String memberId;
    private String nickname;
    private String email;
    private Integer age;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String avatar_url;
}
