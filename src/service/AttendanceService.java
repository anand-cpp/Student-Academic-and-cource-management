package service;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import dao.AttendanceDAO;
import dao.CourseDAO;
import dao.EnrollmentDAO;
import model.Attendance;
import util.AppException;
import db.DatabaseConnection;

/**
 * Business rules for attendance management.
 *
 * <h2>Attendance percentage rule</h2>
 * <p>Attendance percentage = (Present sessions + Late sessions) / Total recorded
 * sessions &times; 100.<br>
 * A <b>Late</b> record counts as <b>attended</b> because the student was present
 * in the session (just late). Only <b>Absent</b> reduces the percentage.<br>
 * Future dates and unrecorded sessions are never counted as absences.
 *
 * <p>This rule is applied consistently in {@link #percentageForEnrollment} and
 * {@link #courseSummary}.
 */
public class AttendanceService {

    private final AttendanceDAO dao = new AttendanceDAO();
    private final CourseDAO courseDao = new CourseDAO();
    private final EnrollmentDAO enrollmentDao = new EnrollmentDAO();

    public List<Map<String, Object>> enrollmentsForCourse(int courseId) throws AppException {
        if (courseDao.findById(courseId) == null) {
            throw new AppException("NOT_FOUND", "Course does not exist.");
        }
        List<Map<String, Object>> rows = new java.util.ArrayList<>();
        for (EnrollmentSummary e : enrollmentDao.findByCourse(courseId).stream()
                .map(enr -> new EnrollmentSummary(enr.getId(), enr.getStudentId(), enr.getCourseId()))
                .toList()) {
            Map<String, Object> row = new java.util.HashMap<>();
            row.put("enrollmentId", e.enrollmentId);
            row.put("studentId", e.studentId);
            row.put("courseId", e.courseId);
            rows.add(row);
        }
        return rows;
    }

    public List<Attendance> findByEnrollment(int enrollmentId) throws AppException {
        return dao.findByEnrollment(enrollmentId);
    }

    public List<Attendance> findByCourseAndDate(int courseId, LocalDate date) throws AppException {
        if (date.isAfter(LocalDate.now())) {
            throw new AppException("DATE_FUTURE", "Class date cannot be in the future.");
        }
        return dao.findByCourseAndDate(courseId, date);
    }

    public boolean exists(int enrollmentId, LocalDate date, int excludeId) throws AppException {
        return dao.exists(enrollmentId, date, excludeId);
    }

    /**
     * Save attendance for a course/date in a single transaction.
     *
     * <p>{@code statusByEnrollmentId} maps enrollment id -> PRESENT/ABSENT/LATE.
     * Existing rows for that course/date are removed first so re-marking is an
     * update rather than a duplicate insert.
     */
    public void save(int courseId, LocalDate classDate,
            Map<Integer, String> statusByEnrollmentId) throws AppException {
        if (classDate.isAfter(LocalDate.now())) {
            throw new AppException("DATE_FUTURE", "Class date cannot be in the future.");
        }
        if (courseDao.findById(courseId) == null) {
            throw new AppException("NOT_FOUND", "Course does not exist.");
        }
        for (Map.Entry<Integer, String> entry : statusByEnrollmentId.entrySet()) {
            String s = entry.getValue().toUpperCase();
            if (!"PRESENT".equals(s) && !"ABSENT".equals(s) && !"LATE".equals(s)) {
                throw new AppException("INVALID_STATUS",
                        "Invalid attendance status for enrollment " + entry.getKey() + ": " + entry.getValue());
            }
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            dao.save(courseId, classDate, statusByEnrollmentId, conn);
            conn.commit();
        } catch (AppException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception ignored) {
                }
            }
            throw ex;
        } catch (Exception ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception ignored) {
                }
            }
            throw new AppException("DB_ERROR", "Failed to save attendance.", ex);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (Exception ignored) {
                }
            }
        }
    }

    /** Attendance percentage for a single enrollment. */
    public double percentageForEnrollment(int enrollmentId) throws AppException {
        return dao.percentageForEnrollment(enrollmentId);
    }

    public List<Map<String, Object>> courseSummary(int courseId) throws AppException {
        if (courseDao.findById(courseId) == null) {
            throw new AppException("NOT_FOUND", "Course does not exist.");
        }
        return dao.courseSummary(courseId);
    }

    public int count() throws AppException {
        return dao.count();
    }

    /** Lightweight summary of an enrollment for attendance marking UI. */
    private static final class EnrollmentSummary {
        final int enrollmentId;
        final int studentId;
        final int courseId;

        EnrollmentSummary(int enrollmentId, int studentId, int courseId) {
            this.enrollmentId = enrollmentId;
            this.studentId = studentId;
            this.courseId = courseId;
        }
    }
}
