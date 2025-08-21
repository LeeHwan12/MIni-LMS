package org.minilms.minilms.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.minilms.minilms.Service.MemberService;
import org.minilms.minilms.domain.MemberRequestDTO;
import org.minilms.minilms.domain.MemberResponseDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/signup")
public class MemberController {
    private final MemberService memberService;

    @GetMapping
    public String singUpForm(Model model) {
        model.addAttribute("member",new MemberRequestDTO());
        return "signup";
    }

    @PostMapping
    public String signUp(@Valid @ModelAttribute("member") MemberRequestDTO memberRequestDTO,
                         BindingResult bindingResult, Model model,
                         RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()){
            return "signup";
        }

        try {
            MemberResponseDTO saved = memberService.signUp(memberRequestDTO);
            redirectAttributes.addFlashAttribute("welcomeNickName",saved.getNickname());
            return  "redirect:/login";
        }
        catch (IllegalArgumentException e) {
            bindingResult.reject("signup.fail", e.getMessage());
            return "signup";
        }
    }
}
