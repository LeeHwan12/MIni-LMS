package org.minilms.minilms.Service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.minilms.minilms.Entity.Course;
import org.minilms.minilms.Entity.Enrollment;
import org.minilms.minilms.Entity.Member;
import org.minilms.minilms.Repository.CourseRepository;
import org.minilms.minilms.Repository.EnrollmentRepository;
import org.minilms.minilms.Repository.MemberRepository;
import org.minilms.minilms.commons.NotFoundException;
import org.minilms.minilms.domain.EnrollmentDTO;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import org.minilms.minilms.*;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollRepo;
    private final MemberRepository memberRepo;
    private final CourseRepository courseRepo;

    @Transactional
    public void enroll(Long memberPk, Long courseId) {
        if (enrollRepo.existsByMember_IdAndCourse_CourseId(memberPk, courseId)) return; // 이미 수강 중
        Member m = memberRepo.findById(memberPk).orElseThrow(() -> new NotFoundException("회원 없음"));
        Course c = courseRepo.findById(courseId).orElseThrow(() -> new NotFoundException("강좌 없음"));
        Enrollment e = new Enrollment();
        e.setMember(m);
        e.setCourse(c);
        enrollRepo.save(e);
    }

    @Transactional
    public void unenroll(Long memberPk, Long courseId) {
        // Spring Data JPA 파생 메서드를 추가해두면 더 간단: deleteByMember_IdAndCourse_CourseId(...)
        enrollRepo.findByMember_Id(memberPk, PageRequest.of(0, 100)) // 간단한 안전망
                .get()
                .filter(e -> e.getCourse().getCourseId().equals(courseId))
                .findFirst()
                .ifPresent(enrollRepo::delete);
    }

    @Transactional(readOnly = true)
    public Page<EnrollmentDTO> listByMember(Long memberPk, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0,page), Math.max(1,size), Sort.by(Sort.Direction.DESC,"enrolledAt"));
        return enrollRepo.findByMember_Id(memberPk, pageable).map(this::toDto);
    }

    private EnrollmentDTO toDto(Enrollment e) {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(e.getId());
        dto.setMemberPk(e.getMember().getId());
        dto.setCourseId(e.getCourse().getCourseId());
        dto.setEnrolledAt(e.getEnrolledAt());
        dto.setCourseTitle(e.getCourse().getTitle());
        dto.setInstructor(e.getCourse().getInstructor());
        dto.setCategory(e.getCourse().getCategory());
        dto.setStatus(e.getCourse().getStatus().name());
        return dto;
    }
}
