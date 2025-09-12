package org.minilms.minilms.Controller;

import lombok.RequiredArgsConstructor;
import org.minilms.minilms.Service.CourseService;
import org.minilms.minilms.Service.EnrollmentService;
import org.minilms.minilms.domain.CourseDTO;
import org.minilms.minilms.domain.EnrollmentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public String detail(@PathVariable Long courseId, Model model){
        model.addAttribute("course",courseService.get(courseId));
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
    public String enroll(@PathVariable Long courseId, RedirectAttributes ra){
        enrollmentService.enroll(courseId);
        ra.addFlashAttribute("msg", "수강 신청이 완료되었습니다.");
        return "redirect:/courses/" + courseId;
    }

    /** 수강 취소 */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{courseId}/unenroll")
    public String unenroll(@PathVariable Long courseId, RedirectAttributes ra){
        enrollmentService.cancel(courseId);
        ra.addFlashAttribute("msg", "수강 신청이 취소되었습니다.");
        return "redirect:/courses/" + courseId;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/enrollments")
    public String myEnrollments(@RequestParam(defaultValue="0") int page,
                                @RequestParam(defaultValue="12") int size,
                                Model model) {
        Page<EnrollmentDTO> list = enrollmentService.listMyEnrollments(PageRequest.of(page, size));
        model.addAttribute("enrollments", list);
        return "enrollments/my";
    }
}
