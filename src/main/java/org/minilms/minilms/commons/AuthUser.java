package org.minilms.minilms.commons;

import lombok.RequiredArgsConstructor;
import org.minilms.minilms.Service.MemberService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUser {
    private final MemberService memberService;

    public Long memberPK(@AuthenticationPrincipal User user){
        return memberService.getPkByLoginId(user.getUsername());
    }
}
