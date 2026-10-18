package com.studentmgmt.service;

import com.studentmgmt.dto.MarksDTO;
import com.studentmgmt.dto.MarksCreateDTO;
import com.studentmgmt.dto.MarksUpdateDTO;
import com.studentmgmt.dto.MarksBulkCreateDTO;
import com.studentmgmt.dto.CourseResultDTO;
import com.studentmgmt.model.Marks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MarksService {

    Optional<MarksDTO> findById(Long id);

    Page<MarksDTO> search(Long assessmentId, Long enrollmentId, Boolean isFinalized, Pageable pageable);

    List<MarksDTO> findByAssessmentId(Long assessmentId);

    List<MarksDTO> findByEnrollmentId(Long enrollmentId);

    List<MarksDTO> findByStudentAndCourse(Long studentId, Long courseId);

    MarksDTO create(MarksCreateDTO createDTO);

    List<MarksDTO> createBulk(MarksBulkCreateDTO createDTO);

    MarksDTO update(Long id, MarksUpdateDTO updateDTO);

    void finalizeMarks(Long id);

    void unfinalizeMarks(Long id);

    CourseResultDTO getCourseResult(Long studentId, Long courseId);
}