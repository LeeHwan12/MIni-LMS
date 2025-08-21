package org.minilms.minilms.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class MemberRequestDTO {
    private String memberId;
    private String password;
    private String nickname;
    private String email;
    private Integer age;
}
