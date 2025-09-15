package org.minilms.minilms.Service;


import lombok.RequiredArgsConstructor;
import org.minilms.minilms.Entity.Course;
import org.minilms.minilms.Entity.Enrollment;
import org.minilms.minilms.Enums.EnrollmentStatus;
import org.minilms.minilms.Enums.courseStatus; // 기존 open/closed enum
import org.minilms.minilms.Repository.CourseRepository;
import org.minilms.minilms.Repository.EnrollmentRepository;
import org.minilms.minilms.Repository.MemberRepository;
import org.minilms.minilms.commons.NotFoundException;
import org.minilms.minilms.domain.EnrollmentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final MemberRepository memberRepo;

    @Transactional
    public void enroll(Long courseId, String memberId /* == username */) {
        Long myId = resolveMemberPk(memberId);

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new NotFoundException("강좌 없음"));
        if (course.getStatus() != courseStatus.OPEN) { // enum 이름은 PascalCase 권장
            throw new IllegalStateException("마감된 강좌입니다.");
        }

        Enrollment existing = enrollmentRepo
                .findByCourse_CourseIdAndMember_Id(courseId, myId)
                .orElse(null);

        if (existing == null) {
            Enrollment e = new Enrollment();
            e.setCourse(course);
            e.setMember(memberRepo.getReferenceById(myId));
            e.setStatus(EnrollmentStatus.ENROLLED);
            enrollmentRepo.save(e);
        } else {
            existing.setStatus(EnrollmentStatus.ENROLLED);
            existing.setCanceledAt(null);
        }
    }

    @Transactional
    public void cancel(Long courseId, String memberId) {
        Long myId = resolveMemberPk(memberId);
        Enrollment e = enrollmentRepo
                .findByCourse_CourseIdAndMember_Id(courseId, myId)
                .orElseThrow(() -> new NotFoundException("신청 내역 없음"));
        e.setStatus(EnrollmentStatus.CANCELED);
        e.setCanceledAt(java.time.LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public boolean isEnrolled(Long courseId, String memberId) {
        Long myId = resolveMemberPk(memberId);
        return enrollmentRepo.findByCourse_CourseIdAndMember_Id(courseId, myId)
                .map(x -> x.getStatus() == EnrollmentStatus.ENROLLED)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public Page<EnrollmentDTO> listMyEnrollments(String memberId, Pageable pageable) {
        System.out.println("memberId = " + memberId + ", pageable = " + pageable);
        Long myId = resolveMemberPk(memberId);
        var page = enrollmentRepo.findByMember_Id(myId, pageable);
        return page.map(e -> EnrollmentDTO.builder()
                .enrollmentId(e.getId())
                .courseId(e.getCourse().getCourseId())
                .title(e.getCourse().getTitle())
                .instructor(e.getCourse().getInstructor())
                .category(e.getCourse().getCategory())
                .status(e.getStatus() != null ? e.getStatus().name() : "ENROLLED")
                .enrolledAt(e.getEnrolledAt())
                .build());
    }

    private Long resolveMemberPk(String memberId /* == username */) {
        if (memberId == null || memberId.isBlank()) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }
        return memberRepo.findPkByMemberId(memberId)
                .orElseThrow(() -> new IllegalStateException("회원 정보가 없습니다."));
    }
}
