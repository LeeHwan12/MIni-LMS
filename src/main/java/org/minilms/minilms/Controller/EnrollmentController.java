package org.minilms.minilms.Controller;

import lombok.RequiredArgsConstructor;
import org.minilms.minilms.Service.EnrollmentService;
import org.minilms.minilms.commons.AuthUser;
import org.minilms.minilms.domain.EnrollmentDTO;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final AuthUser authUser;

    /** 내 수강 목록 */
    @GetMapping
    public String myEnrollments(
            User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model){
        Long memberPK = authUser.memberPK(user);
        Page<EnrollmentDTO> list = enrollmentService.listByMember(memberPK, page, size);
        model.addAttribute("list", list);
        return "enrollments/list";
    }
    /** 수강 신청 */
    @PostMapping("/{courseId}")
    public String enroll(User user, @PathVariable Long courseId){
        Long memberPK = authUser.memberPK(user);
        enrollmentService.enroll(courseId, memberPK);
        return "redirect:/courses/" + courseId;
    }
    /** 수강 취소 **/
    @PostMapping("/{courseId}/cancel")
    public String cancel(User user, @PathVariable Long courseId){
        Long memberPK = authUser.memberPK(user);
        enrollmentService.unenroll(courseId, memberPK);
        return "redirect:/enrollments";
    }
}
