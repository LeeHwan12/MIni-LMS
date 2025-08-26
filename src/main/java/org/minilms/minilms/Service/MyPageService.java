package org.minilms.minilms.Service;

import lombok.RequiredArgsConstructor;
import org.minilms.minilms.Repository.EnrollmentRepository;
import org.minilms.minilms.Repository.MemberRepository;
import org.minilms.minilms.Repository.NotificationRepository;
import org.minilms.minilms.Repository.ProgressRepository;
import org.minilms.minilms.domain.NotificationDTO;
import org.minilms.minilms.domain.ProfileSummaryDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final MemberRepository memberRepo;
    private final NotificationRepository notificationRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final ProgressRepository progressRepo;

    @Transactional(readOnly = true)
    public ProfileSummaryDTO summary(Long memberPk) {
        var m = memberRepo.findById(memberPk).orElseThrow();
        int coursesCount = enrollmentRepo.countByMember_Id(memberPk);
        double avg = progressRepo.avgByMember(memberPk);
        return ProfileSummaryDTO.builder()
                .memberId(m.getMemberId())
                .nickname(m.getNickname())
                .email(m.getEmail())
                .avatarUrl(m.getAvatar_url())
                .coursesCount(coursesCount)
                .avgProgress(Math.round(avg * 10.0) / 10.0)
                .build();
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> recentNotis(Long memberPk) {
        return notificationRepo.findTop10ByMember_IdOrderByCreatedAtDesc(memberPk)
                .stream()
                .map(n -> new NotificationDTO(n.getId(), memberPk, n.getMessage(), n.isRead(), n.getCreatedAt()))
                .toList();
    }
}
