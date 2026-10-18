package com.studentmgmt.service;

import com.studentmgmt.dto.*;
import com.studentmgmt.model.*;
import com.studentmgmt.repository.*;
import com.studentmgmt.util.DTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceSessionRepository sessionRepository;
    private final AttendanceRecordRepository recordRepository;
    private final AssessmentRepository assessmentRepository;
    private final MarksRepository marksRepository;
    private final DepartmentRepository departmentRepository;
    private final DTOMapper dtoMapper;

    @Override
    public StudentAcademicProfileDTO getStudentAcademicProfile(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        List<EnrollmentDTO> enrollmentDTOs = enrollments.stream()
                .map(dtoMapper::toEnrollmentDTO)
                .collect(Collectors.toList());

        // Calculate attendance summary
        AttendanceSummaryDTO attendanceSummary = calculateAttendanceSummary(studentId);

        // Calculate academic summary
        AcademicSummaryDTO academicSummary = calculateAcademicSummary(studentId);

        return StudentAcademicProfileDTO.builder()
                .student(dtoMapper.toStudentDTO(student))
                .enrollments(enrollmentDTOs)
                .attendanceSummary(attendanceSummary)
                .academicSummary(academicSummary)
                .build();
    }

    @Override
    public CourseEnrollmentReportDTO getCourseEnrollmentReport(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        List<EnrollmentDTO> enrollmentDTOs = enrollments.stream()
                .map(dtoMapper::toEnrollmentDTO)
                .collect(Collectors.toList());

        long enrolled = enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED).count();
        long dropped = enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.DROPPED).count();
        long completed = enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED).count();
        long withdrawn = enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.WITHDRAWN).count();
        long failed = enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.FAILED).count();

        return CourseEnrollmentReportDTO.builder()
                .course(dtoMapper.toCourseDTO(course))
                .totalEnrollments(enrollments.size())
                .enrolledCount(enrolled)
                .droppedCount(dropped)
                .completedCount(completed)
                .withdrawnCount(withdrawn)
                .failedCount(failed)
                .enrollments(enrollmentDTOs)
                .build();
    }

    @Override
    public FacultyCourseAssignmentReportDTO getFacultyCourseAssignmentReport(Long facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        List<Course> courses = courseRepository.findByFacultyId(facultyId);
        List<CourseDTO> courseDTOs = courses.stream()
                .map(dtoMapper::toCourseDTO)
                .collect(Collectors.toList());

        long activeCourses = courses.stream().filter(Course::isActive).count();
        long totalStudents = courses.stream()
                .mapToLong(c -> enrollmentRepository.countByCourseIdAndStatus(c.getId(), EnrollmentStatus.ENROLLED))
                .sum();

        return FacultyCourseAssignmentReportDTO.builder()
                .faculty(dtoMapper.toFacultyDTO(faculty))
                .totalCourses(courses.size())
                .activeCourses(activeCourses)
                .totalStudents(totalStudents)
                .courses(courseDTOs)
                .build();
    }

    @Override
    public AttendanceReportDTO getAttendanceReport(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<AttendanceSession> sessions = sessionRepository.findByCourseId(courseId);
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);

        List<StudentAttendanceDTO> studentAttendances = enrollments.stream()
                .map(enrollment -> {
                    List<AttendanceRecord> records = recordRepository.findByEnrollmentIdOrderBySessionDate(enrollment.getId());
                    long totalSessions = records.size();
                    long present = records.stream().filter(r -> r.getStatus() == AttendanceStatus.PRESENT).count();
                    long late = records.stream().filter(r -> r.getStatus() == AttendanceStatus.LATE).count();
                    long absent = records.stream().filter(r -> r.getStatus() == AttendanceStatus.ABSENT).count();
                    long excused = records.stream().filter(r -> r.getStatus() == AttendanceStatus.EXCUSED).count();
                    long medical = records.stream().filter(r -> r.getStatus() == AttendanceStatus.MEDICAL).count();

                    long eligible = totalSessions - excused - medical;
                    double percentage = eligible > 0 ? ((double) (present + late) / eligible) * 100.0 : 0.0;

                    return StudentAttendanceDTO.builder()
                            .studentId(enrollment.getStudent().getId())
                            .studentName(enrollment.getStudent().getFullName())
                            .studentCode(enrollment.getStudent().getStudentId())
                            .totalSessions(totalSessions)
                            .presentCount(present)
                            .lateCount(late)
                            .absentCount(absent)
                            .excusedCount(excused + medical)
                            .attendancePercentage(Math.round(percentage * 100.0) / 100.0)
                            .build();
                })
                .collect(Collectors.toList());

        return AttendanceReportDTO.builder()
                .course(dtoMapper.toCourseDTO(course))
                .totalSessions(sessions.size())
                .studentAttendances(studentAttendances)
                .build();
    }

    @Override
    public AttendanceReportDTO getStudentAttendanceReport(Long studentId, Long courseId) {
        return getAttendanceReport(courseId); // Simplified
    }

    @Override
    public CourseResultsReportDTO getCourseResultsReport(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<Assessment> assessments = assessmentRepository.findByCourseIdOrderByDate(courseId);
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);

        List<StudentResultDTO> studentResults = enrollments.stream()
                .map(enrollment -> {
                    List<Marks> marksList = marksRepository.findFinalizedByCourseIdAndStudentId(courseId, enrollment.getStudent().getId());

                    double totalWeightedScore = marksList.stream()
                            .mapToDouble(Marks::getWeightedScore)
                            .sum();

                    BigDecimal totalWeightage = marksList.stream()
                            .map(m -> m.getAssessment().getWeightage())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    double percentage = totalWeightage.compareTo(BigDecimal.ZERO) > 0
                            ? totalWeightedScore / totalWeightage.doubleValue() * 100.0
                            : 0.0;

                    String grade = calculateGrade(percentage);

                    return StudentResultDTO.builder()
                            .studentId(enrollment.getStudent().getId())
                            .studentName(enrollment.getStudent().getFullName())
                            .studentCode(enrollment.getStudent().getStudentId())
                            .percentage(Math.round(percentage * 100.0) / 100.0)
                            .grade(grade)
                            .gradePoints(calculateGradePoints(grade))
                            .marks(marksList.stream().map(dtoMapper::toMarksDTO).collect(Collectors.toList()))
                            .build();
                })
                .collect(Collectors.toList());

        return CourseResultsReportDTO.builder()
                .course(dtoMapper.toCourseDTO(course))
                .assessments(assessments.stream().map(dtoMapper::toAssessmentDTO).collect(Collectors.toList()))
                .studentResults(studentResults)
                .build();
    }

    @Override
    public StudentPerformanceSummaryDTO getStudentPerformanceSummary(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

        List<CoursePerformanceDTO> coursePerformances = enrollments.stream()
                .map(enrollment -> {
                    List<Marks> marksList = marksRepository.findFinalizedByCourseIdAndStudentId(enrollment.getCourse().getId(), studentId);

                    if (marksList.isEmpty()) {
                        return CoursePerformanceDTO.builder()
                                .courseId(enrollment.getCourse().getId())
                                .courseCode(enrollment.getCourse().getCourseCode())
                                .courseTitle(enrollment.getCourse().getTitle())
                                .credits(enrollment.getCourse().getCredits())
                                .build();
                    }

                    double totalWeightedScore = marksList.stream()
                            .mapToDouble(Marks::getWeightedScore)
                            .sum();

                    BigDecimal totalWeightage = marksList.stream()
                            .map(m -> m.getAssessment().getWeightage())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    double percentage = totalWeightage.compareTo(BigDecimal.ZERO) > 0
                            ? totalWeightedScore / totalWeightage.doubleValue() * 100.0
                            : 0.0;

                    String grade = calculateGrade(percentage);

                    return CoursePerformanceDTO.builder()
                            .courseId(enrollment.getCourse().getId())
                            .courseCode(enrollment.getCourse().getCourseCode())
                            .courseTitle(enrollment.getCourse().getTitle())
                            .credits(enrollment.getCourse().getCredits())
                            .percentage(Math.round(percentage * 100.0) / 100.0)
                            .grade(grade)
                            .gradePoints(calculateGradePoints(grade))
                            .build();
                })
                .collect(Collectors.toList());

        double overallPercentage = coursePerformances.stream()
                .filter(cp -> cp.getPercentage() != null)
                .mapToDouble(CoursePerformanceDTO::getPercentage)
                .average()
                .orElse(0.0);

        double totalCredits = coursePerformances.stream()
                .filter(cp -> cp.getGradePoints() != null)
                .mapToDouble(cp -> cp.getCredits() * cp.getGradePoints())
                .sum();

        double totalCreditsEarned = coursePerformances.stream()
                .filter(cp -> cp.getGradePoints() != null && cp.getGradePoints() > 0)
                .mapToInt(CoursePerformanceDTO::getCredits)
                .sum();

        double gpa = totalCreditsEarned > 0 ? totalCredits / totalCreditsEarned : 0.0;

        return StudentPerformanceSummaryDTO.builder()
                .student(dtoMapper.toStudentDTO(student))
                .coursePerformances(coursePerformances)
                .overallPercentage(Math.round(overallPercentage * 100.0) / 100.0)
                .overallGrade(calculateGrade(overallPercentage))
                .gpa(Math.round(gpa * 100.0) / 100.0)
                .totalCreditsEarned(totalCreditsEarned)
                .totalCreditsAttempted((double) coursePerformances.stream().mapToInt(CoursePerformanceDTO::getCredits).sum())
                .build();
    }

    @Override
    public List<StudentPerformanceSummaryDTO> getDepartmentStudentPerformance(Long departmentId) {
        List<Student> students = studentRepository.findByDepartmentId(departmentId);
        return students.stream()
                .map(s -> getStudentPerformanceSummary(s.getId()))
                .collect(Collectors.toList());
    }

    private AttendanceSummaryDTO calculateAttendanceSummary(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        long totalSessions = 0;
        long present = 0;
        long late = 0;
        long absent = 0;
        long excused = 0;

        for (Enrollment enrollment : enrollments) {
            List<AttendanceRecord> records = recordRepository.findByEnrollmentIdOrderBySessionDate(enrollment.getId());
            totalSessions += records.size();
            present += records.stream().filter(r -> r.getStatus() == AttendanceStatus.PRESENT).count();
            late += records.stream().filter(r -> r.getStatus() == AttendanceStatus.LATE).count();
            absent += records.stream().filter(r -> r.getStatus() == AttendanceStatus.ABSENT).count();
            excused += records.stream().filter(r -> r.getStatus() == AttendanceStatus.EXCUSED).count();
            excused += records.stream().filter(r -> r.getStatus() == AttendanceStatus.MEDICAL).count();
        }

        long eligible = totalSessions - excused;
        double percentage = eligible > 0 ? ((double) (present + late) / eligible) * 100.0 : 0.0;

        return AttendanceSummaryDTO.builder()
                .totalSessions(totalSessions)
                .totalPresent(present)
                .totalAbsent(absent)
                .totalLate(late)
                .totalExcused(excused)
                .overallPercentage(Math.round(percentage * 100.0) / 100.0)
                .build();
    }

    private AcademicSummaryDTO calculateAcademicSummary(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        List<CourseResultDTO> courseResults = enrollments.stream()
                .map(enrollment -> {
                    List<Marks> marksList = marksRepository.findFinalizedByCourseIdAndStudentId(enrollment.getCourse().getId(), studentId);

                    if (marksList.isEmpty()) {
                        return CourseResultDTO.builder()
                                .courseId(enrollment.getCourse().getId())
                                .courseCode(enrollment.getCourse().getCourseCode())
                                .courseTitle(enrollment.getCourse().getTitle())
                                .credits(enrollment.getCourse().getCredits())
                                .build();
                    }

                    double totalWeightedScore = marksList.stream()
                            .mapToDouble(Marks::getWeightedScore)
                            .sum();

                    BigDecimal totalWeightage = marksList.stream()
                            .map(m -> m.getAssessment().getWeightage())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    double percentage = totalWeightage.compareTo(BigDecimal.ZERO) > 0
                            ? totalWeightedScore / totalWeightage.doubleValue() * 100.0
                            : 0.0;

                    String grade = calculateGrade(percentage);

                    return CourseResultDTO.builder()
                            .courseId(enrollment.getCourse().getId())
                            .courseCode(enrollment.getCourse().getCourseCode())
                            .courseTitle(enrollment.getCourse().getTitle())
                            .credits(enrollment.getCourse().getCredits())
                            .percentage(Math.round(percentage * 100.0) / 100.0)
                            .grade(grade)
                            .gradePoints(calculateGradePoints(grade))
                            .status(enrollment.getStatus().name())
                            .build();
                })
                .collect(Collectors.toList());

        double overallPercentage = courseResults.stream()
                .filter(cr -> cr.getPercentage() != null)
                .mapToDouble(CourseResultDTO::getPercentage)
                .average()
                .orElse(0.0);

        return AcademicSummaryDTO.builder()
                .overallPercentage(Math.round(overallPercentage * 100.0) / 100.0)
                .overallGrade(calculateGrade(overallPercentage))
                .courseResults(courseResults)
                .build();
    }

    private String calculateGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 85) return "A";
        if (percentage >= 80) return "A-";
        if (percentage >= 75) return "B+";
        if (percentage >= 70) return "B";
        if (percentage >= 65) return "B-";
        if (percentage >= 60) return "C+";
        if (percentage >= 55) return "C";
        if (percentage >= 50) return "C-";
        if (percentage >= 40) return "D";
        return "F";
    }

    private double calculateGradePoints(String grade) {
        return switch (grade) {
            case "A+" -> 10.0;
            case "A" -> 9.5;
            case "A-" -> 9.0;
            case "B+" -> 8.5;
            case "B" -> 8.0;
            case "B-" -> 7.5;
            case "C+" -> 7.0;
            case "C" -> 6.5;
            case "C-" -> 6.0;
            case "D" -> 5.0;
            default -> 0.0;
        };
    }
}