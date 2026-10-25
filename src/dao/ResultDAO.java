package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Result;
import db.DatabaseConnection;
import util.AppException;

/** Data Access Object for {@link Result}. */
public class ResultDAO {

    public List<Result> findAll() throws AppException {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT id, enrollment_id, marks_obtained, percentage, grade FROM results ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load results.", ex);
        }
        return list;
    }

    public Result findByEnrollment(int enrollmentId) throws AppException {
        String sql = "SELECT id, enrollment_id, marks_obtained, percentage, grade FROM results WHERE enrollment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load result.", ex);
        }
        return null;
    }

    public boolean exists(int enrollmentId, int excludeId) throws AppException {
        String sql = "SELECT COUNT(*) FROM results WHERE enrollment_id = ? AND id <> ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to check duplicate result.", ex);
        }
        return false;
    }

    /** Save or update one result within a transaction-managed connection. */
    public void save(Result result, Connection conn) throws AppException {
        if (result.getId() > 0) {
            update(result, conn);
            return;
        }
        String sql = "INSERT INTO results (enrollment_id, marks_obtained, percentage, grade) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, result.getEnrollmentId());
            ps.setBigDecimal(2, result.getMarksObtained());
            ps.setBigDecimal(3, result.getPercentage());
            ps.setString(4, result.getGrade());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    result.setId(keys.getInt(1));
                }
            }
        } catch (SQLException ex) {
            if (isDuplicate(ex)) {
                throw new AppException("DUPLICATE", "A result already exists for this enrollment.", ex);
            }
            throw new AppException("DB_ERROR", "Failed to save result.", ex);
        }
    }

    public void update(Result result, Connection conn) throws AppException {
        String sql = "UPDATE results SET marks_obtained = ?, percentage = ?, grade = ? WHERE enrollment_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, result.getMarksObtained());
            ps.setBigDecimal(2, result.getPercentage());
            ps.setString(3, result.getGrade());
            ps.setInt(4, result.getEnrollmentId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to update result.", ex);
        }
    }

    public void deleteByEnrollment(int enrollmentId) throws AppException {
        String sql = "DELETE FROM results WHERE enrollment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to delete result.", ex);
        }
    }

    public int count() throws AppException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM results");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to count results.", ex);
        }
        return 0;
    }

    /** Mean percentage across every recorded result. Returns 0 when there are none. */
    public double averagePercentage() throws AppException {
        String sql = "SELECT AVG(percentage) FROM results";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to calculate average academic percentage.", ex);
        }
        return 0.0;
    }

    /** Course-wise results summary: average percentage and pass/fail counts. */
    public List<Map<String, Object>> courseSummary(int courseId) throws AppException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT s.id AS student_id, s.student_id AS student_code, s.full_name, "
                + "r.marks_obtained, r.percentage, r.grade, c.maximum_marks, c.passing_marks "
                + "FROM results r "
                + "JOIN enrollments e ON e.id = r.enrollment_id AND e.course_id = ? "
                + "JOIN students s ON s.id = e.student_id "
                + "JOIN courses c ON c.id = e.course_id "
                + "ORDER BY s.full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("studentId", rs.getInt("student_id"));
                    row.put("studentCode", rs.getString("student_code"));
                    row.put("studentName", rs.getString("full_name"));
                    row.put("marksObtained", rs.getBigDecimal("marks_obtained"));
                    row.put("percentage", rs.getBigDecimal("percentage"));
                    row.put("grade", rs.getString("grade"));
                    row.put("maximumMarks", rs.getBigDecimal("maximum_marks"));
                    row.put("passingMarks", rs.getBigDecimal("passing_marks"));
                    list.add(row);
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load course results.", ex);
        }
        return list;
    }

    /** All results joined with student/course for reporting. */
    public List<Map<String, Object>> findAllSummary() throws AppException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT s.student_id AS student_code, s.full_name AS student_name, "
                + "c.course_code, c.course_name, r.marks_obtained, r.percentage, r.grade "
                + "FROM results r "
                + "JOIN enrollments e ON e.id = r.enrollment_id "
                + "JOIN students s ON s.id = e.student_id "
                + "JOIN courses c ON c.id = e.course_id "
                + "ORDER BY s.full_name, c.course_code";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("studentCode", rs.getString("student_code"));
                row.put("studentName", rs.getString("student_name"));
                row.put("courseCode", rs.getString("course_code"));
                row.put("courseName", rs.getString("course_name"));
                row.put("marksObtained", rs.getBigDecimal("marks_obtained"));
                row.put("percentage", rs.getBigDecimal("percentage"));
                row.put("grade", rs.getString("grade"));
                list.add(row);
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load results report.", ex);
        }
        return list;
    }

    private Result map(ResultSet rs) throws SQLException {
        Result r = new Result();
        r.setId(rs.getInt("id"));
        r.setEnrollmentId(rs.getInt("enrollment_id"));
        r.setMarksObtained(rs.getBigDecimal("marks_obtained"));
        r.setPercentage(rs.getBigDecimal("percentage"));
        r.setGrade(rs.getString("grade"));
        return r;
    }

    private boolean isDuplicate(SQLException ex) {
        return "23000".equals(ex.getSQLState())
                || ex.getMessage().contains("Duplicate")
                || ex.getMessage().contains("unique");
    }
}
