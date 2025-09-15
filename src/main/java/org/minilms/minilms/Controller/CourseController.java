package org.minilms.minilms.Controller;

import lombok.RequiredArgsConstructor;
import org.minilms.minilms.Entity.Course;
import org.minilms.minilms.Enums.courseStatus;
import org.minilms.minilms.Repository.MemberRepository;
import org.minilms.minilms.Service.CourseService;
import org.minilms.minilms.Service.EnrollmentService;
import org.minilms.minilms.domain.CourseDTO;
import org.minilms.minilms.domain.EnrollmentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Scanner;

@Controller
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final MemberRepository memberRepo;
    /** 검색 & 목록 */
    @GetMapping
    public String search(@RequestParam(required = false) String kw,
                         @RequestParam(required = false) String cat,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "12") int size,
                         Model model
                         ){
        Page<CourseDTO> list = courseService.search(kw,cat,page,size);
        model.addAttribute("courses", list);
        model.addAttribute("kw", kw);
        model.addAttribute("cat", cat);
        return "courses/list";
    }
    /** 상세 */
    @GetMapping("/{courseId}")
    public String detail(@PathVariable Long courseId, Model model, Authentication auth){
        var course = courseService.getById(courseId);
        model.addAttribute("course", course);

        boolean courseOpen = course.getStatus() == courseStatus.OPEN; // Enum이면 끝
        model.addAttribute("courseOpen", courseOpen);

        boolean enrolled = false;
        if (auth != null && auth.isAuthenticated()) {
            enrolled = enrollmentService.isEnrolled(courseId, auth.getName()); // auth.getName() == member_id
        }
        model.addAttribute("enrolled", enrolled);
        return "courses/detail";
    }

    /** 등록 폼/처리 (관리자/강사만 접근하도록 @PreAuthorize 권장) */
    @GetMapping("/new")
    public String newform(Model model){
        model.addAttribute("course", new CourseDTO());
        return "courses/form";
    }

    @PostMapping
    public String saveCourse(@ModelAttribute("course") CourseDTO courseDTO){
        Long id = courseService.create(courseDTO);
        return "redirect:/courses/" + id;
    }

    @GetMapping("/{courseId}/edit")
    public String edit(@PathVariable Long courseId, Model model){
        model.addAttribute("form",courseService.get(courseId));
        return "courses/form";
    }

    @PostMapping("/{courseId}")
    public String editCourse(@PathVariable Long courseId, @ModelAttribute("course") CourseDTO courseDTO){
        courseService.update(courseId,courseDTO);
        return "redirect:/courses/" + courseId;
    }

    @PostMapping("/{courseId}/status")
    public String updateStatus(@PathVariable Long courseId, @RequestParam String status){
        courseService.changeStatus(courseId,status);
        return "redirect:/courses/" + courseId;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{courseId}/enroll")
    public String enroll(@PathVariable Long courseId, RedirectAttributes ra, Authentication auth) {
        Course course = courseService.getById(courseId);
        if (course == null) {
            ra.addFlashAttribute("error", "존재하지 않는 강좌입니다.");
            return "redirect:/courses";
        }

        boolean courseOpen = course.getStatus() != null
                && course.getStatus().toString().trim().equalsIgnoreCase("OPEN");
        if (!courseOpen) {
            ra.addFlashAttribute("error", "마감된 강좌입니다.");
            return "redirect:/courses/" + courseId;
        }

        // (옵션) 이미 신청했는지 체크
        if (auth != null && enrollmentService.isEnrolled(courseId,auth.getName())) {
            ra.addFlashAttribute("msg", "이미 신청된 강좌입니다.");
            return "redirect:/courses/" + courseId;
        }

        enrollmentService.enroll(courseId,auth.getName()); // 내부에서 현재 사용자 ID 사용/전달
        ra.addFlashAttribute("msg", "수강 신청이 완료되었습니다.");
        return "redirect:/courses/" + courseId; // 상세 GET이 다시 모델 세팅
    }

    /** 수강 취소 */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{courseId}/unenroll")
    public String unenroll(@PathVariable Long courseId, RedirectAttributes ra, Authentication auth){
        enrollmentService.cancel(courseId, auth.getName());
        ra.addFlashAttribute("msg", "수강 신청이 취소되었습니다.");
        return "redirect:/courses/" + courseId;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/enrollments")
    public String myEnrollments(@RequestParam(defaultValue="0") int page,
                                @RequestParam(defaultValue="12") int size,
                                Model model,
                                Authentication auth) {
        var pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100));
        String memberId = auth.getName();        // ← 여기!
        Page<EnrollmentDTO> list = enrollmentService.listMyEnrollments(memberId, pageable);
        model.addAttribute("enrollments", list);
        return "enrollments/my";
    }
}
