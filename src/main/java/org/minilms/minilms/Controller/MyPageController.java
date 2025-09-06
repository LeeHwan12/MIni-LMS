package org.minilms.minilms.Controller;

import lombok.RequiredArgsConstructor;
import org.minilms.minilms.Service.MyPageService;
import org.minilms.minilms.commons.AuthUser;
import org.minilms.minilms.domain.ProfileSummaryDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {
    private final MyPageService myPageService;
    private final AuthUser authUser;


    @GetMapping
    public String index(@AuthenticationPrincipal User user, Model model) {
        Long memberPK = authUser.memberPK(user);
        ProfileSummaryDTO summary = myPageService.summary(memberPK);
        model.addAttribute("summary", summary);
        return "mypage/index";
    }

}
