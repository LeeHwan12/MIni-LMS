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
    private String emailId;     // 이메일 아이디 부분
    private String emailDomain; // 이메일 도메인 선택 부분
    private Integer birthYear;

    public String getEmail() {
        if (emailId != null && emailDomain != null) {
            return emailId + "@" + emailDomain;
        }
        return null;
    }

    public Integer getAge() {
        if (birthYear != null) {
            int currentYear = java.time.LocalDate.now().getYear();
            return currentYear - birthYear + 1; // 한국식 나이(+1), 필요시 조정
        }
        return null;
    }
}
