package org.minilms.minilms.Service;

import lombok.RequiredArgsConstructor;
import org.minilms.minilms.commons.NotFoundException;
import org.minilms.minilms.domain.CourseDTO;
import org.minilms.minilms.Entity.Course;
import org.minilms.minilms.Enums.*;
import org.minilms.minilms.Repository.CourseRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepo;

    @Transactional(readOnly = true)
    public CourseDTO get(Long courseId) {
        Course c = courseRepo.findById(courseId).orElseThrow(() -> new NotFoundException("강좌 없음"));
        return toDto(c);
    }

    @Transactional(readOnly = true)
    public Page<CourseDTO> search(String keyword, String category, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0,page), Math.max(1,size), Sort.by(Sort.Direction.DESC,"createdAt"));
        return courseRepo.search(keyword, category, pageable).map(this::toDto);
    }

    @Transactional
    public Long create(CourseDTO req) {
        Course c = new Course();
        c.setTitle(req.getTitle());
        c.setDescription(req.getDescription());
        c.setInstructor(req.getInstructor());
        c.setCategory(req.getCategory());
        c.setStatus(req.getStatus() == null ? courseStatus.open : courseStatus.valueOf(req.getStatus()));
        courseRepo.save(c);
        return c.getCourseId();
    }

    @Transactional
    public void update(Long courseId, CourseDTO req) {
        Course c = courseRepo.findById(courseId).orElseThrow(() -> new NotFoundException("강좌 없음"));
        c.setTitle(req.getTitle());
        c.setDescription(req.getDescription());
        c.setInstructor(req.getInstructor());
        c.setCategory(req.getCategory());
        if (req.getStatus() != null) c.setStatus(courseStatus.valueOf(req.getStatus()));
    }

    @Transactional
    public void changeStatus(Long courseId, String status) {
        Course c = courseRepo.findById(courseId).orElseThrow(() -> new NotFoundException("강좌 없음"));
        c.setStatus(courseStatus.valueOf(status)); // "OPEN"/"CLOSED"
    }

    private CourseDTO toDto(Course c) {
        CourseDTO dto = new CourseDTO();
        dto.setCourseId(c.getCourseId());
        dto.setTitle(c.getTitle());
        dto.setDescription(c.getDescription());
        dto.setInstructor(c.getInstructor());
        dto.setCategory(c.getCategory());
        dto.setStatus(c.getStatus().name());
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }
}
