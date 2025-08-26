package org.minilms.minilms.domain;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProfileSummaryDTO {
    private String memberId;
    private String nickname;
    private String email;
    private String avatarUrl;
    private int coursesCount;
    private double avgProgress;
    private String role;

}
