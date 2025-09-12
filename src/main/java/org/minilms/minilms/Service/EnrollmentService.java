// src/main/java/org/minilms/minilms/Service/EnrollmentService.java
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

    private String me() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !a.isAuthenticated() || "anonymousUser".equals(a.getName())) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }
        return a.getName();
    }

    /** 수강 신청(재신청 가능: CANCELED → ENROLLED) */
    @Transactional
    public void enroll(Long courseId) {
        Long me = currentMemberId();
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new NotFoundException("강좌 없음"));

        if (course.getStatus() != courseStatus.OPEN) {
            throw new IllegalStateException("마감된 강좌입니다.");
        }

        var existing = enrollmentRepo.findByCourse_CourseIdAndMember_Id(courseId, me).orElse(null);
        if (existing == null) {
            Enrollment e = new Enrollment();
            e.setCourse(course);

            e.setMember(memberRepo.getReferenceById(me));  // getReference 사용 권장
            e.getMember().setId(me);
            e.setStatus(EnrollmentStatus.ENROLLED);
            enrollmentRepo.save(e);
        } else {
            existing.setStatus(EnrollmentStatus.ENROLLED);
            existing.setCanceledAt(null);
        }
    }
    /** 수강 취소 */
    @Transactional
    public void cancel(Long courseId) {
        Long me = currentMemberId();
        Enrollment e = enrollmentRepo.findByCourse_CourseIdAndMember_Id(courseId, me)
                .orElseThrow(() -> new NotFoundException("신청 내역 없음"));
        e.setStatus(EnrollmentStatus.CANCELED);
        e.setCanceledAt(java.time.LocalDateTime.now());
    }

    /** 현재 로그인 사용자의 신청 여부 */
    @Transactional(readOnly = true)
    public boolean isEnrolled(Long courseId) {
        Long me = currentMemberId();
        return enrollmentRepo.findByCourse_CourseIdAndMember_Id(courseId, me)
                .map(x -> x.getStatus() == EnrollmentStatus.ENROLLED)
                .orElse(false);
    }

    /** 강좌별 신청자 수 */
    @Transactional(readOnly = true)
    public long count(Long courseId) {
        return enrollmentRepo.countByCourse_CourseIdAndStatus(courseId, EnrollmentStatus.ENROLLED);
    }

    private Long currentMemberId() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !a.isAuthenticated() || "anonymousUser".equals(a.getName())) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }
        Object principal = a.getPrincipal();

        // 3) 그 외: Authentication.name 사용
        return memberRepo.findIdByUsername(a.getName())
                .orElseThrow(() -> new IllegalStateException("회원 정보가 없습니다."));
    }

    @Transactional(readOnly = true)
    public Page<EnrollmentDTO> listMyEnrollments(Pageable pageable) {
        Long me = currentMemberId();

        // 상태 컬럼을 쓰는 경우(ENROLLED만 노출)
        // Page<Enrollment> page = enrollmentRepo.findByMember_IdAndStatus(me, EnrollmentStatus.ENROLLED, pageable);

        // 상태 컬럼이 없거나, 취소건도 함께 보이려면 아래 라인 사용
        Page<Enrollment> page = enrollmentRepo.findByMember_Id(me, pageable);

        return page.map(e -> EnrollmentDTO.builder()
                .enrollmentId(e.getId())
                .courseId(e.getCourse().getCourseId())
                .title(e.getCourse().getTitle())
                .instructor(e.getCourse().getInstructor())
                .category(e.getCourse().getCategory())
                .status(
                        // 상태 컬럼 없는 경우를 대비한 기본값
                        e.getStatus() != null ? e.getStatus().name() : "ENROLLED"
                )
                .enrolledAt(e.getEnrolledAt())
                .build()
        );
    }
}
