package org.minilms.minilms.Controller;

import lombok.RequiredArgsConstructor;

import org.minilms.minilms.Service.NotificationService;
import org.minilms.minilms.commons.AuthUser;
import org.minilms.minilms.domain.NotificationDTO;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final AuthUser authUser;

    /** 전체 페이지 (서버사이드 렌더링) */
    @GetMapping
    public String notifications(User user,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                Model model) {
        Long memberPK = authUser.memberPK(user);
        Page<NotificationDTO> notis = notificationService.page(memberPK, page, size);
        model.addAttribute("notis", notis);
        return "notifications/list";
    }

    /** 헤더 드롭다운 프래그먼트 */
    @GetMapping("/recent")
    public String recent(User user, Model model) {
        Long memberPK = authUser.memberPK(user);
        List<NotificationDTO> notis = notificationService.recent(memberPK);
        long unread = notificationService.unreadCount(memberPK);
        model.addAttribute("notis", notis);
        model.addAttribute("unread", unread);
        return "fragments/notifications :: dropdown";
    }

    /** 단건 읽음 처리 */
    @PostMapping("/{id}/read")
    public String markRead(@PathVariable Long id, @RequestHeader(value = "Referer", required = false) String referer) {
        notificationService.markRead(id);
        return "redirect:" + (referer != null ? referer : "/notifications");
    }

    /** 모두 읽음 처리 */
    @PostMapping("/read-all")
    public String markReadAll(
            User user,
            @RequestHeader(value = "Referer", required = false) String referer) {
        Long memberPK = authUser.memberPK(user);
        notificationService.markAllRead(memberPK);
        return "redirect:" + (referer != null ? referer : "/notifications");
    }


}
