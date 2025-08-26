package org.minilms.minilms.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.minilms.minilms.Service.MemberService;
import org.minilms.minilms.commons.AuthUser;
import org.minilms.minilms.domain.ChangePasswordForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberPasswordController {
    private final MemberService memberService;
    private final AuthUser auth;

    /** 비밀번호 변경 폼 */
    @GetMapping("/password")
    public String passwordForm(@ModelAttribute("form") ChangePasswordForm form) {
        return "member/password"; // 비번 변경 템플릿
    }

    /** 비밀번호 변경 처리 */
    @PostMapping("/password")
    public String changePassword(org.springframework.security.core.userdetails.User user,
                                 @Valid @ModelAttribute("form") ChangePasswordForm form,
                                 BindingResult binding,
                                 RedirectAttributes ra) {
        // 1) 폼 레벨 검증: 새 비밀번호 일치 확인
        if (!form.getNewPassword().equals(form.getNewPasswordConfirm())) {
            binding.reject("mismatch", "새 비밀번호와 확인이 일치하지 않습니다.");
        }
        if (binding.hasErrors()) {
            return "member/password"; // 검증 실패 → 같은 폼으로
        }

        // 2) 서비스 호출
        Long memberPk = auth.memberPK(user);
        try {
            memberService.changePassword(memberPk, form.getCurrentPassword(), form.getNewPassword());
        } catch (IllegalArgumentException ex) {
            binding.reject("invalid", ex.getMessage()); // 현재 비번 불일치 등
            return "member/password";
        }

        // 3) 성공 시 PRG
        ra.addFlashAttribute("ok", "비밀번호가 변경되었습니다.");
        return "redirect:/member/profile";
    }

}
