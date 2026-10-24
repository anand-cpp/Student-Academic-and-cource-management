package service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import db.DatabaseConnection;
import dao.CourseDAO;
import dao.EnrollmentDAO;
import dao.ResultDAO;
import model.Result;
import util.AppException;
import util.InputValidator;

/**
 * Business rules and validation for marks and academic results.
 *
 * <p>Grading scale (documented, easy to modify, not an official institutional
 * policy):<br>
 * A: 90-100% | B: 80-89.99% | C: 70-79.99% | D: 60-69.99% | E: 50-59.99% |
 * F: below 50%.
 *
 * <p>Marks are processed with {@link BigDecimal} for precise numeric handling.
 * One final result per enrollment is preferred; saving replaces any existing
 * result for that enrollment.
 */
public class ResultService {

    private final ResultDAO dao = new ResultDAO();
    private final EnrollmentDAO enrollmentDao = new EnrollmentDAO();
    private final CourseDAO courseDao = new CourseDAO();
    private final GradeService gradeService = new GradeService();

    public List<Result> listAll() throws AppException {
        return dao.findAll();
    }

    public Result findByEnrollment(int enrollmentId) throws AppException {
        return dao.findByEnrollment(enrollmentId);
    }

    /**
     * Save or update a result within a transaction.
     *
     * <p>Validates that the enrollment exists and that marks are within the
     * course maximum. Computes percentage and grade, then writes a single
     * result for the enrollment. Uses commit/rollback correctly.
     *
     * @return the database id of the saved result
     */
    public int save(int enrollmentId, String marksObtained) throws AppException {
        if (!enrollmentDao.exists(enrollmentId)) {
            throw new AppException("NOT_FOUND", "Enrollment does not exist.");
        }

        BigDecimal marks = InputValidator.decimal(marksObtained, "Marks obtained");
        if (marks.signum() < 0) {
            throw new AppException("OUT_OF_RANGE", "Marks cannot be negative.");
        }

        int courseId = enrollmentDao.getCourseId(enrollmentId);
        BigDecimal maxMarks = courseDao.getMaximumMarks(courseId);
        InputValidator.marks(marks, maxMarks);

        BigDecimal percentage = marks.divide(maxMarks, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        String grade = gradeService.calculateGrade(percentage);

        Result result = new Result();
        result.setEnrollmentId(enrollmentId);
        result.setMarksObtained(marks.setScale(2, BigDecimal.ROUND_HALF_UP));
        result.setPercentage(percentage);
        result.setGrade(grade);

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            dao.save(result, conn);
            conn.commit();
            return result.getId();
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
            throw new AppException("DB_ERROR", "Failed to save result.", ex);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void deleteByEnrollment(int enrollmentId) throws AppException {
        dao.deleteByEnrollment(enrollmentId);
    }

    public int count() throws AppException {
        return dao.count();
    }

    public List<Map<String, Object>> courseSummary(int courseId) throws AppException {
        return dao.courseSummary(courseId);
    }

    public List<Map<String, Object>> findAllSummary() throws AppException {
        return dao.findAllSummary();
    }
}
