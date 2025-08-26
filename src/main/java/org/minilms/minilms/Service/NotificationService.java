package org.minilms.minilms.Service;

import lombok.RequiredArgsConstructor;
import org.minilms.minilms.Entity.Notification;
import org.minilms.minilms.Repository.NotificationRepository;
import org.minilms.minilms.domain.NotificationDTO;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repo;

    @Transactional(readOnly = true)
    public List<NotificationDTO> recent(Long memberPk) {
        return repo.findTop10ByMember_IdOrderByCreatedAtDesc(memberPk)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Page<NotificationDTO> page(Long memberPk, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0,page), Math.max(1,size), Sort.by(Sort.Direction.DESC, "createdAt"));
        return repo.findByMember_IdOrderByCreatedAtDesc(memberPk, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public long unreadCount(Long memberPk) { return repo.countByMember_IdAndReadFalse(memberPk); }

    @Transactional
    public void markRead(Long notiId) { repo.markRead(notiId); }

    @Transactional
    public int markAllRead(Long memberPk) { return repo.markAllRead(memberPk); }

    private NotificationDTO toDto(Notification n) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(n.getId());
        dto.setMessage(n.getMessage());
        dto.setRead(n.isRead());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }
}
