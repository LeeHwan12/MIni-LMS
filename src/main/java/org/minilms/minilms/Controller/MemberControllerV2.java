package org.minilms.minilms.Controller;

import lombok.RequiredArgsConstructor;
import org.minilms.minilms.Service.MemberService;
import org.minilms.minilms.commons.AuthUser;
import org.minilms.minilms.domain.MemberResponseDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberControllerV2 {
    private final MemberService memberService;
    private final AuthUser authUser;

    @GetMapping("/profile")
    public String profile(User user, Model model) {
        Long memberPK = authUser.memberPK(user);
        MemberResponseDTO res = memberService.getByPk(memberPK);
        model.addAttribute("member", res);
        return "member/profile";
    }
    @GetMapping("/member/profile/edit")
    public String editForm(@AuthenticationPrincipal User user, Model model) {
        Long pk = memberService.getPkByLoginId(user.getUsername());
        model.addAttribute("form", memberService.getByPk(pk)); // 기존값 채움
        return "member/profile-edit";
    }
    @PostMapping("/member/profile")
    public String update(@AuthenticationPrincipal User user,
                         @RequestParam String nickname,
                         @RequestParam String email,
                         @RequestParam(required=false) String avatarUrl) {
        Long pk = memberService.getPkByLoginId(user.getUsername());
        memberService.updateProfile(pk, nickname, email, avatarUrl);
        return "redirect:/member/profile"; // PRG
    }
}
