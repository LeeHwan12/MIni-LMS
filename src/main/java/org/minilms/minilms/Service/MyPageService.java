package org.minilms.minilms.Service;

import lombok.RequiredArgsConstructor;
import org.minilms.minilms.Repository.*;
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
    private final MyPageRepository myPageRepository;

    @Transactional(readOnly = true)
    public ProfileSummaryDTO summary(Long memberId) {
        var v = myPageRepository.summaryNative(memberId);
        if (v == null) {
            return new ProfileSummaryDTO(memberId, null, 0L, 0.0, null);
        }
        return new ProfileSummaryDTO(
                v.getMemberId(), v.getNickname(), v.getCoursesCount(), v.getAvgProgress(), v.getAvatarUrl()
        );
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> recentNotis(Long memberPk) {
        return notificationRepo.findTop10ByMember_IdOrderByCreatedAtDesc(memberPk)
                .stream()
                .map(n -> new NotificationDTO(n.getId(), memberPk, n.getMessage(), n.isRead(), n.getCreatedAt()))
                .toList();
    }
}
