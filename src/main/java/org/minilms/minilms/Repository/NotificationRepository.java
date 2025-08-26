package org.minilms.minilms.Repository;

import org.minilms.minilms.Entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository  extends JpaRepository<Notification,Long> {
    List<Notification> findTop10ByMember_IdOrderByCreatedAtDesc(Long memberPk);

    Page<Notification> findByMember_IdOrderByCreatedAtDesc(Long memberPk, Pageable pageable);

    @Modifying
    @Query("update Notification n set n.read = true where n.id = :id")
    int markRead(@Param("id") Long id);

    @Modifying
    @Query("update Notification n set n.read = true where n.member.id = :memberPk and n.read = false")
    int markAllRead(@Param("memberPk") Long memberPk);

    long countByMember_IdAndReadFalse(Long memberPk);
}
