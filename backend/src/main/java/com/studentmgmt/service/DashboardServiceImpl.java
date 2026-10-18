package com.studentmgmt.service;

import com.studentmgmt.dto.DashboardStatsDTO;
import com.studentmgmt.model.CourseStatus;
import com.studentmgmt.model.EnrollmentStatus;
import com.studentmgmt.model.FacultyStatus;
import com.studentmgmt.model.StudentStatus;
import com.studentmgmt.repository.CourseRepository;
import com.studentmgmt.repository.EnrollmentRepository;
import com.studentmgmt.repository.FacultyRepository;
import com.studentmgmt.repository.StudentRepository;
import com.studentmgmt.util.DTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final DTOMapper dtoMapper;

    @Override
    public DashboardStatsDTO getDashboardStats() {
        long totalStudents = studentRepository.count();
        long totalFaculty = facultyRepository.count();
        long totalCourses = courseRepository.count();
        long activeEnrollments = enrollmentRepository.countByStatus(EnrollmentStatus.ENROLLED);

        // Calculate average attendance (simplified)
        double avgAttendance = calculateAverageAttendance();

        // Calculate average performance (simplified)
        double avgPerformance = calculateAveragePerformance();

        long pendingApprovals = 0; // Could be enrollment requests, leave requests, etc.

        return dtoMapper.toDashboardStatsDTO(
                totalStudents, totalFaculty, totalCourses,
                activeEnrollments, avgAttendance, avgPerformance, pendingApprovals);
    }

    @Override
    public DashboardStatsDTO getFacultyDashboardStats() {
        // Would filter by faculty's courses
        return getDashboardStats(); // Simplified
    }

    @Override
    public DashboardStatsDTO getStudentDashboardStats() {
        // Would filter by student's enrollments
        return getDashboardStats(); // Simplified
    }

    private double calculateAverageAttendance() {
        // Simplified calculation - would need to aggregate attendance records
        return 85.0;
    }

    private double calculateAveragePerformance() {
        // Simplified calculation - would need to aggregate marks
        return 75.0;
    }
}