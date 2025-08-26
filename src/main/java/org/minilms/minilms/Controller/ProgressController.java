package org.minilms.minilms.Controller;

import lombok.RequiredArgsConstructor;
import org.minilms.minilms.Repository.ProgressRepository;
import org.minilms.minilms.Service.ProgressService;
import org.minilms.minilms.commons.AuthUser;
import org.minilms.minilms.domain.ProgressDTO;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/progress")
public class ProgressController {

    private final ProgressService progressService;
    private final AuthUser authUser;

    /** 특정 강좌 진행률 보기 (뷰 or JSON 필요에 따라) */
    @GetMapping("/{couresId}")
    public String get(User user, @PathVariable Long couresId, Model model){
        Long userPK = authUser.memberPK(user);
        ProgressDTO dto = progressService.get(couresId, userPK);
        model.addAttribute("progress", dto);
        return "progress/detail";
    }

    /** 진행률 갱신 (폼/버튼 액션) */
    @PostMapping("/{courseId}")
    public String update(User user,
                         @PathVariable Long courseId, @RequestParam double percent,
                         @RequestParam(value = "Referer",required = false) String referer,
                         Model model){
        Long userPK = authUser.memberPK(user);
        progressService.upsertPercent(courseId, userPK, percent);
        return "redirect:" + (referer != null ? referer : ("/progress/" + courseId));
    }
}
