package org.minilms.minilms.Service;

import lombok.RequiredArgsConstructor;
import org.minilms.minilms.Entity.Course;
import org.minilms.minilms.Entity.Member;
import org.minilms.minilms.Entity.Progress;
import org.minilms.minilms.Repository.CourseRepository;
import org.minilms.minilms.Repository.MemberRepository;
import org.minilms.minilms.Repository.ProgressRepository;
import org.minilms.minilms.commons.NotFoundException;
import org.minilms.minilms.domain.ProgressDTO;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProgressService {
    private final ProgressRepository progressRepo;
    private final MemberRepository memberRepo;
    private final CourseRepository courseRepo;

    @Transactional(readOnly = true)
    public ProgressDTO get(Long memberPk, Long courseId) {
        Progress p = progressRepo.findByMember_IdAndCourse_CourseId(memberPk, courseId)
                .orElseThrow(() -> new NotFoundException("진행률 없음"));
        return toDto(p);
    }

    @Transactional
    public void upsertPercent(Long memberPk, Long courseId, double percent) {
        double v = Math.max(0, Math.min(100, percent));
        Progress p = progressRepo.findByMember_IdAndCourse_CourseId(memberPk, courseId).orElse(null);
        if (p == null) {
            Member m = memberRepo.findById(memberPk).orElseThrow(() -> new NotFoundException("회원 없음"));
            Course c = courseRepo.findById(courseId).orElseThrow(() -> new NotFoundException("강좌 없음"));
            p = new Progress();
            p.setMember(m);
            p.setCourse(c);
        }
        p.setPercent(v);
        progressRepo.save(p); // insert or update
    }

    @Transactional(readOnly = true)
    public double avgByMember(Long memberPk) {
        return progressRepo.avgByMember(memberPk);
    }

    @Transactional(readOnly = true)
    public Page<ProgressDTO> listByMember(Long memberPk, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0,page), Math.max(1,size), Sort.by(Sort.Direction.DESC,"updatedAt"));
        return progressRepo.findByMember_Id(memberPk, pageable).map(this::toDto);
    }

    private ProgressDTO toDto(Progress p) {
        ProgressDTO dto = new ProgressDTO();
        dto.setId(p.getId());
        dto.setMemberPk(p.getMember().getId());
        dto.setCourseId(p.getCourse().getCourseId());
        dto.setProgressPercent(p.getPercent());
        dto.setUpdatedAt(p.getUpdatedAt());
        return dto;
    }
}
