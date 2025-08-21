package org.minilms.minilms.Service;

import lombok.RequiredArgsConstructor;
import org.minilms.minilms.Entity.Member;
import org.minilms.minilms.Repository.MemberRepository;
import org.minilms.minilms.domain.MemberRequestDTO;
import org.minilms.minilms.domain.MemberResponseDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponseDTO signUp(MemberRequestDTO requestDTO) {

        // (선택) 입력 정규화
        final String memberId = requestDTO.getMemberId().trim();
        final String email = requestDTO.getEmail().trim().toLowerCase(); // 이메일 소문자 정규화 권장
        final String nickname = requestDTO.getNickname().trim();

        // 선행 중복 체크 (UX용)
        if (memberRepository.existsByMemberId(memberId)) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        // 비밀번호 해시
        final String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());

        try {
            Member member = Member.builder()
                    .memberId(memberId)
                    .passwordHash(encodedPassword)   // ✅ 해시 저장
                    .nickname(nickname)
                    .email(email)
                    .age(requestDTO.getAge())
                    .enabled(true)
                    .build();

            Member saved = memberRepository.save(member);

            return MemberResponseDTO.builder()
                    .id(saved.getId())
                    .memberId(saved.getMemberId())
                    .nickname(saved.getNickname())
                    .email(saved.getEmail())
                    .age(saved.getAge())                   // ← 빠졌던 age도 포함
                    .enabled(saved.isEnabled())
                    .createdAt(saved.getCreatedAt())
                    .updatedAt(saved.getUpdatedAt())
                    .build();

        } catch (DataIntegrityViolationException dupEx) {
            // 동시 가입 등으로 DB UNIQUE 제약 위반 시 여기로 옴
            // (member_id / email / nickname 에 UNIQUE 제약이 있어야 함)
            throw new IllegalArgumentException("이미 사용 중인 아이디 또는 이메일입니다.");
        }
    }

    @Transactional(readOnly = true)
    public MemberResponseDTO getByMemberId(String memberId) {
        Member member = memberRepository.findByMemberId(memberId.trim())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId));

        return MemberResponseDTO.builder()
                .id(member.getId())
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .age(member.getAge())
                .enabled(member.isEnabled())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
