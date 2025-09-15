package org.minilms.minilms.Service;

import lombok.RequiredArgsConstructor;
import org.minilms.minilms.commons.NotFoundException;
import org.minilms.minilms.domain.CourseDTO;
import org.minilms.minilms.Entity.Course;
import org.minilms.minilms.Enums.courseStatus;
import org.minilms.minilms.Repository.CourseRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepo;

    @Transactional(readOnly = true)
    public CourseDTO get(Long courseId) {
        Course c = courseRepo.findById(courseId)
                .orElseThrow(() -> new NotFoundException("강좌 없음"));
        return toDto(c);
    }
    public Course getById(Long courseId) {
        return courseRepo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강좌를 찾을 수 없습니다. id=" + courseId));
    }

    @Transactional(readOnly = true)
    public Page<CourseDTO> search(String keyword, String category, int page, int size) {
        // 빈문자는 필터 해제
        String kw  = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        String cat = (category == null || category.isBlank() || "ALL".equalsIgnoreCase(category))
                ? null : category.trim();

        Pageable pageable = PageRequest.of(
                Math.max(0, page),
                Math.max(1, size),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return courseRepo.search(kw, cat, pageable).map(this::toDto);
    }

    @Transactional
    public Long create(CourseDTO req) {
        Course c = new Course();
        c.setTitle(req.getTitle());
        c.setDescription(req.getDescription());
        c.setInstructor(req.getInstructor());
        c.setCategory(normalizeCategory(req.getCategory())); // String

        // status 기본값 open
        courseStatus st = courseStatus.OPEN;
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            st = safeParseStatus(req.getStatus());
        }
        c.setStatus(st);

        courseRepo.save(c);
        return c.getCourseId(); // ✅ 엔티티의 실제 ID getter
    }

    @Transactional
    public void update(Long courseId, CourseDTO req) {
        Course c = courseRepo.findById(courseId)
                .orElseThrow(() -> new NotFoundException("강좌 없음"));

        c.setTitle(req.getTitle());
        c.setDescription(req.getDescription());
        c.setInstructor(req.getInstructor());

        if (req.getCategory() != null) {
            c.setCategory(normalizeCategory(req.getCategory())); // String
        }
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            c.setStatus(safeParseStatus(req.getStatus()));
        }
        // @Transactional 더티체킹으로 자동 반영
    }

    @Transactional
    public void changeStatus(Long courseId, String status) {
        Course c = courseRepo.findById(courseId)
                .orElseThrow(() -> new NotFoundException("강좌 없음"));
        c.setStatus(safeParseStatus(status)); // "open"/"closed" 등
    }


    private CourseDTO toDto(Course c) {
        CourseDTO dto = new CourseDTO();
        dto.setCourseId(c.getCourseId());
        dto.setTitle(c.getTitle());
        dto.setDescription(c.getDescription());
        dto.setInstructor(c.getInstructor());
        dto.setCategory(c.getCategory()); // String 그대로
        dto.setStatus(c.getStatus() != null ? c.getStatus().name() : null); // enum -> 문자열("open")
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }

    // -------- helpers --------
    private courseStatus safeParseStatus(String s) {
        try {
            return courseStatus.valueOf(s.toLowerCase(Locale.ROOT)); // enum 상수 소문자 가정
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("잘못된 상태 값: " + s + " (허용: " + java.util.Arrays.toString(courseStatus.values()) + ")");
        }
    }

    private String normalizeCategory(String category) {
        if (category == null) return null;
        String trimmed = category.trim();
        return trimmed.isEmpty() ? null : trimmed.toUpperCase(Locale.ROOT); // 저장 일관성(대문자) 추천
    }
}
