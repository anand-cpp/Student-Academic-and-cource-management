package service;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import dao.CourseDAO;
import dao.EnrollmentDAO;
import dao.StudentDAO;
import model.Enrollment;
import util.AppException;
import util.DateUtils;
import util.InputValidator;
import db.DatabaseConnection;

/** Business rules and validation for enrollment management. */
public class EnrollmentService {

    private final EnrollmentDAO dao = new EnrollmentDAO();
    private final StudentDAO studentDao = new StudentDAO();
    private final CourseDAO courseDao = new CourseDAO();

    public List<Map<String, Object>> listAllSummary() throws AppException {
        return dao.findAllSummary();
    }

    public List<model.Enrollment> findByStudent(int studentId) throws AppException {
        return dao.findByStudent(studentId);
    }

    public List<model.Enrollment> findByCourse(int courseId) throws AppException {
        return dao.findByCourse(courseId);
    }

    /**
     * Enroll a student in a course within a transaction.
     *
     * <p>Validates that both student and course exist and that no active
     * duplicate enrollment exists. Uses commit/rollback correctly.
     */
    public int enroll(int studentId, int courseId, String enrollmentDate) throws AppException {
        if (studentDao.findById(studentId) == null) {
            throw new AppException("NOT_FOUND", "Student does not exist.");
        }
        if (courseDao.findById(courseId) == null) {
            throw new AppException("NOT_FOUND", "Course does not exist.");
        }
        LocalDate date = DateUtils.parse(enrollmentDate);
        DateUtils.notFuture(date, "Enrollment date");

        if (dao.existsActive(studentId, courseId, -1)) {
            throw new AppException("DUPLICATE",
                    "An active enrollment for this student and course already exists.");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollment.setEnrollmentDate(date);
        enrollment.setStatus("Active");

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            int id = dao.insert(enrollment, conn);
            conn.commit();
            return id;
        } catch (AppException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception rollbackEx) {
                    // ignore
                }
            }
            throw ex;
        } catch (Exception ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception rollbackEx) {
                    // ignore
                }
            }
            throw new AppException("DB_ERROR", "Failed to create enrollment.", ex);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void update(int id, int studentId, int courseId, String enrollmentDate, String status)
            throws AppException {
        model.Enrollment existing = dao.findByStudentAndCourse(studentId, courseId);
        // If the found enrollment is a different row, changing to it would duplicate.
        if (existing != null && existing.getId() != id) {
            throw new AppException("DUPLICATE",
                    "An enrollment for this student and course already exists.");
        }
        LocalDate date = DateUtils.parse(enrollmentDate);
        DateUtils.notFuture(date, "Enrollment date");

        model.Enrollment e = new model.Enrollment();
        e.setId(id);
        e.setStudentId(studentId);
        e.setCourseId(courseId);
        e.setEnrollmentDate(date);
        e.setStatus(status);

        dao.update(e);
    }

    public void delete(int id) throws AppException {
        dao.delete(id);
    }

    public int count() throws AppException {
        return dao.count();
    }

    public int countActive() throws AppException {
        return dao.countActive();
    }
}
