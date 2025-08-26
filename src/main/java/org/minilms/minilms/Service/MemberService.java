package org.minilms.minilms.Service;

import lombok.RequiredArgsConstructor;
import org.minilms.minilms.Entity.Member;
import org.minilms.minilms.Repository.MemberRepository;
import org.minilms.minilms.commons.NotFoundException;
import org.minilms.minilms.domain.MemberRequestDTO;
import org.minilms.minilms.domain.MemberResponseDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository; // ✅ 하나로 통일
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponseDTO signUp(MemberRequestDTO requestDTO) {
        // (선택) 입력 정규화
        final String memberId = requestDTO.getMemberId().trim();
        final String email = requestDTO.getEmail().trim().toLowerCase();
        final String nickname = requestDTO.getNickname().trim();

        // 선행 중복 체크 (UX 향상)
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
                    .passwordHash(encodedPassword)
                    .nickname(nickname)
                    .email(email)
                    .birthYear(requestDTO.getBirthYear()) // ✅ 스키마에 맞게 반영
                    .enabled(true)
                    .build();

            Member saved = memberRepository.save(member);
            return toResponse(saved);

        } catch (DataIntegrityViolationException dupEx) {
            // UNIQUE 제약 위반(경합 등)
            throw new IllegalArgumentException("이미 사용 중인 아이디/이메일/닉네임입니다.");
        }
    }

    @Transactional(readOnly = true)
    public MemberResponseDTO getByMemberId(String memberId) {
        Member member = memberRepository.findByMemberId(memberId.trim())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId));
        return toResponse(member);
    }

    @Transactional(readOnly = true)
    public MemberResponseDTO getByPk(Long memberPk) {
        Member m = memberRepository.findById(memberPk)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다. id=" + memberPk));
        return toResponse(m); // ✅ 통일
    }

    @Transactional(readOnly = true)
    public Long getPkByLoginId(String loginId) {
        return memberRepository.findPkByMemberId(loginId)
                .orElseThrow(() -> new NotFoundException("회원 없음: " + loginId));
    }

    @Transactional
    public void changePassword(Long memberPk, String currentRaw, String newRaw) {
        Member m = memberRepository.findById(memberPk)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다. id=" + memberPk));

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(currentRaw, m.getPasswordHash())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        // 같은 비밀번호 방지(선택)
        if (passwordEncoder.matches(newRaw, m.getPasswordHash())) {
            throw new IllegalArgumentException("이전과 동일한 비밀번호는 사용할 수 없습니다.");
        }
        m.setPasswordHash(passwordEncoder.encode(newRaw)); // 변경감지로 업데이트
    }

    @Transactional
    public void updateProfile(Long memberPk, String nickname, String email, String avatarUrl) {
        Member m = memberRepository.findById(memberPk)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다. id=" + memberPk));

        final String normalizedEmail = email.trim().toLowerCase();
        final String normalizedNickname = nickname.trim();

        // 이메일 중복 검사(자기 자신 제외)
        if (memberRepository.existsByEmail(normalizedEmail) && !normalizedEmail.equalsIgnoreCase(m.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }
        // 닉네임 UNIQUE 제약이 있다면 동일 검사(자기 자신 제외)
        // 주의: existsByNickname(..)가 없다면 Repo에 추가하거나 catch로 처리
        // if (memberRepository.existsByNickname(normalizedNickname) && !normalizedNickname.equals(m.getNickname())) { ... }

        m.setNickname(normalizedNickname);
        m.setEmail(normalizedEmail);
        m.setAvatar_url(avatarUrl); // ✅ camelCase 사용
    }

    /** 공통 매핑: Entity -> ResponseDTO */
    private MemberResponseDTO toResponse(Member m) {
        // age 필드가 DTO에 있으면 birthYear로 계산해서 내려줌(연도만 있으므로 단순 계산)
        Integer age = null;
        if (m.getBirthYear() != null) {
            int currentYear = Year.now().getValue();
            age = currentYear - m.getBirthYear(); // 생일 정보가 없으므로 근사치
        }

        return MemberResponseDTO.builder()
                .id(m.getId())
                .memberId(m.getMemberId())
                .nickname(m.getNickname())
                .email(m.getEmail())
                .age(age)                    // ✅ DTO에 age가 있다면 이 줄 사용(둘 중 하나에 맞춰 쓰세요)
                .avatar_url(m.getAvatar_url())
                .enabled(m.isEnabled())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }
}
