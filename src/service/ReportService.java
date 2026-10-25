package service;

import java.util.List;
import java.util.Map;
import dao.AttendanceDAO;
import dao.CourseDAO;
import dao.EnrollmentDAO;
import dao.FacultyDAO;
import dao.ResultDAO;
import dao.StudentDAO;
import util.AppException;

/**
 * Report queries. All data comes from MySQL; nothing is hardcoded.
 */
public class ReportService {

    private final StudentDAO studentDao = new StudentDAO();
    private final FacultyDAO facultyDao = new FacultyDAO();
    private final CourseDAO courseDao = new CourseDAO();
    private final EnrollmentDAO enrollmentDao = new EnrollmentDAO();
    private final AttendanceDAO attendanceDao = new AttendanceDAO();
    private final ResultDAO resultDao = new ResultDAO();

    public int totalStudents() throws AppException {
        return studentDao.count();
    }

    public int totalFaculty() throws AppException {
        return facultyDao.count();
    }

    public int totalCourses() throws AppException {
        return courseDao.count();
    }

    public int totalEnrollments() throws AppException {
        return enrollmentDao.count();
    }

    /** Mean attendance across all recorded sessions; Late counts as attended. */
    public double averageAttendance() throws AppException {
        return attendanceDao.averagePercentage();
    }

    /** Mean percentage across every recorded result. */
    public double averageAcademicPercentage() throws AppException {
        return resultDao.averagePercentage();
    }

    public List<Map<String, Object>> studentDirectory() throws AppException {
        return studentDao.findAll().stream().map(s -> {
            Map<String, Object> row = new java.util.HashMap<>();
            row.put("studentId", s.getStudentId());
            row.put("rollNumber", s.getRollNumber());
            row.put("fullName", s.getFullName());
            row.put("email", s.getEmail());
            row.put("department", s.getDepartment());
            row.put("semester", s.getSemester());
            return row;
        }).toList();
    }

    public List<Map<String, Object>> studentsInCourse(int courseId) throws AppException {
        return enrollmentDao.findReportByCourse(courseId);
    }

    public List<Map<String, Object>> coursesByFaculty(int facultyId) throws AppException {
        return courseDao.findAll().stream()
                .filter(c -> Integer.valueOf(facultyId).equals(c.getAssignedFacultyId()))
                .map(c -> {
                    Map<String, Object> row = new java.util.HashMap<>();
                    row.put("courseCode", c.getCourseCode());
                    row.put("courseName", c.getCourseName());
                    row.put("department", c.getDepartment());
                    row.put("credits", c.getCredits());
                    return row;
                }).toList();
    }

    public List<Map<String, Object>> courseAttendanceSummary(int courseId) throws AppException {
        return attendanceDao.courseSummary(courseId);
    }

    public List<Map<String, Object>> courseResults(int courseId) throws AppException {
        return resultDao.courseSummary(courseId);
    }

    public List<Map<String, Object>> allResults() throws AppException {
        return resultDao.findAllSummary();
    }

    public List<Map<String, Object>> recentEnrollments() throws AppException {
        return enrollmentDao.recentEnrollments(8);
    }
}
