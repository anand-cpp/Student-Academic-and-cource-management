package com.studentmgmt.service;

import com.studentmgmt.dto.AssessmentDTO;
import com.studentmgmt.dto.AssessmentCreateDTO;
import com.studentmgmt.dto.AssessmentUpdateDTO;
import com.studentmgmt.model.Assessment;
import com.studentmgmt.model.AssessmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AssessmentService {

    Optional<AssessmentDTO> findById(Long id);

    Page<AssessmentDTO> search(Long courseId, AssessmentType type, Boolean isPublished,
                               LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<AssessmentDTO> findByCourseId(Long courseId);

    List<AssessmentDTO> findPublishedByCourseId(Long courseId);

    AssessmentDTO create(AssessmentCreateDTO createDTO);

    AssessmentDTO update(Long id, AssessmentUpdateDTO updateDTO);

    void delete(Long id);

    void publish(Long id);

    void unpublish(Long id);

    long countByCourseId(Long courseId);
}