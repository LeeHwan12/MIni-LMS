package org.minilms.minilms.domain;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProfileSummaryDTO {
    private Long memberId;
    private String nickname;
    private Long coursesCount;   // count(...) 는 Long
    private Double avgProgress;  // avg(...) 는 Double (0.0과 호환)
    private String avatarUrl;    // Member.avatar_url

}
