package org.minilms.minilms.Service;

import lombok.RequiredArgsConstructor;
import org.minilms.minilms.Entity.Member;
import org.minilms.minilms.Repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String memberId)
            throws org.springframework.security.core.userdetails.UsernameNotFoundException {
        Member m = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("존재하지 않는 사용자: " + memberId));

        // 권한은 예시로 USER 하나만 부여
        return org.springframework.security.core.userdetails.User
                .withUsername(m.getMemberId())
                .password(m.getPasswordHash())   // 회원가입 때 저장한 bcrypt 해시
                .roles("USER")
                .build();
    }
}
