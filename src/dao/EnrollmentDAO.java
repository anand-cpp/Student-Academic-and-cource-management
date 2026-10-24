package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Enrollment;
import db.DatabaseConnection;
import util.AppException;

/** Data Access Object for {@link Enrollment}. */
public class EnrollmentDAO {

    private static final String BASE =
            "SELECT e.id, e.student_id, e.course_id, e.enrollment_date, e.status "
            + "FROM enrollments e";

    public List<Enrollment> findAll() throws AppException {
        List<Enrollment> list = new ArrayList<>();
        String sql = BASE + " ORDER BY e.enrollment_date DESC, e.id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load enrollments.", ex);
        }
        return list;
    }

    public List<Enrollment> findByStudent(int studentId) throws AppException {
        List<Enrollment> list = new ArrayList<>();
        String sql = BASE + " WHERE e.student_id = ? ORDER BY e.enrollment_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load student enrollments.", ex);
        }
        return list;
    }

    public List<Enrollment> findByCourse(int courseId) throws AppException {
        List<Enrollment> list = new ArrayList<>();
        String sql = BASE + " WHERE e.course_id = ? ORDER BY e.enrollment_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load course enrollments.", ex);
        }
        return list;
    }

    public Enrollment findByStudentAndCourse(int studentId, int courseId) throws AppException {
        String sql = BASE + " WHERE e.student_id = ? AND e.course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load enrollment.", ex);
        }
        return null;
    }

    public boolean existsActive(int studentId, int courseId, int excludeId) throws AppException {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND course_id = ? "
                + "AND id <> ? AND status = 'Active'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.setInt(3, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to check duplicate enrollment.", ex);
        }
        return false;
    }

    /** Insert within a transaction-managed connection when part of multi-step ops. */
    public int insert(Enrollment enrollment, Connection conn) throws AppException {
        String sql = "INSERT INTO enrollments (student_id, course_id, enrollment_date, status) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, enrollment.getStudentId());
            ps.setInt(2, enrollment.getCourseId());
            ps.setString(3, dbDate(enrollment.getEnrollmentDate()));
            ps.setString(4, enrollment.getStatus());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new AppException("INSERT_FAILED", "Creating enrollment failed, no rows affected.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new AppException("INSERT_FAILED", "Creating enrollment failed, no id returned.");
        } catch (SQLException ex) {
            if (isDuplicate(ex)) {
                throw new AppException("DUPLICATE",
                        "An active enrollment for this student and course already exists.", ex);
            }
            throw new AppException("DB_ERROR", "Failed to create enrollment.", ex);
        }
    }

    public void update(Enrollment enrollment) throws AppException {
        String sql = "UPDATE enrollments SET student_id = ?, course_id = ?, enrollment_date = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollment.getStudentId());
            ps.setInt(2, enrollment.getCourseId());
            ps.setString(3, dbDate(enrollment.getEnrollmentDate()));
            ps.setString(4, enrollment.getStatus());
            ps.setInt(5, enrollment.getId());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new AppException("NOT_FOUND", "Enrollment not found.");
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to update enrollment.", ex);
        }
    }

    public void delete(int id) throws AppException {
        String sql = "DELETE FROM enrollments WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new AppException("NOT_FOUND", "Enrollment not found.");
            }
        } catch (SQLException ex) {
            if (isForeignKeyViolation(ex)) {
                throw new AppException("CONSTRAINT",
                        "Cannot delete this enrollment: attendance or results reference it. "
                        + "Remove dependent records first.", ex);
            }
            throw new AppException("DB_ERROR", "Failed to delete enrollment.", ex);
        }
    }

    public boolean exists(int id) throws AppException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM enrollments WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to validate enrollment.", ex);
        }
        return false;
    }

    public int getCourseId(int enrollmentId) throws AppException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT course_id FROM enrollments WHERE id = ?")) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("course_id");
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load enrollment course.", ex);
        }
        throw new AppException("NOT_FOUND", "Enrollment not found.");
    }

    public int count() throws AppException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM enrollments");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to count enrollments.", ex);
        }
        return 0;
    }

    /** Count active enrollments for dashboard stats. */
    public int countActive() throws AppException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM enrollments WHERE status='Active'");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to count active enrollments.", ex);
        }
        return 0;
    }

    public List<Map<String, Object>> recentEnrollments(int limit) throws AppException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT s.student_id, s.full_name, c.course_code, c.course_name, e.status, e.enrollment_date "
                + "FROM enrollments e "
                + "JOIN students s ON s.id = e.student_id "
                + "JOIN courses c ON c.id = e.course_id "
                + "ORDER BY e.enrollment_date DESC, e.id LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("studentId", rs.getString("student_id"));
                    row.put("studentName", rs.getString("full_name"));
                    row.put("courseCode", rs.getString("course_code"));
                    row.put("courseName", rs.getString("course_name"));
                    row.put("status", rs.getString("status"));
                    row.put("enrollmentDate", toLocalDate(rs.getDate("enrollment_date")));
                    list.add(row);
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load recent enrollments.", ex);
        }
        return list;
    }

    /** Join summary: enrollment with student name and course code. */
    public List<Map<String, Object>> findAllSummary() throws AppException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT e.id, s.student_id, s.full_name AS student_name, "
                + "c.course_code, c.course_name, e.enrollment_date, e.status "
                + "FROM enrollments e "
                + "JOIN students s ON s.id = e.student_id "
                + "JOIN courses c ON c.id = e.course_id "
                + "ORDER BY e.enrollment_date DESC, e.id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("studentId", rs.getString("student_id"));
                row.put("studentName", rs.getString("student_name"));
                row.put("courseCode", rs.getString("course_code"));
                row.put("courseName", rs.getString("course_name"));
                row.put("enrollmentDate", toLocalDate(rs.getDate("enrollment_date")));
                row.put("status", rs.getString("status"));
                list.add(row);
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load enrollment summary.", ex);
        }
        return list;
    }

    private Enrollment map(ResultSet rs) throws SQLException {
        Enrollment e = new Enrollment();
        e.setId(rs.getInt("id"));
        e.setStudentId(rs.getInt("student_id"));
        e.setCourseId(rs.getInt("course_id"));
        e.setEnrollmentDate(toLocalDate(rs.getDate("enrollment_date")));
        e.setStatus(rs.getString("status"));
        return e;
    }

    private String dbDate(LocalDate date) {
        return (date == null) ? null : date.toString();
    }

    private LocalDate toLocalDate(java.sql.Date sqlDate) {
        return (sqlDate == null) ? null : sqlDate.toLocalDate();
    }

    private boolean isDuplicate(SQLException ex) {
        return "23000".equals(ex.getSQLState())
                || ex.getMessage().contains("Duplicate")
                || ex.getMessage().contains("unique");
    }

    private boolean isForeignKeyViolation(SQLException ex) {
        return ex.getMessage().contains("foreign key")
                || ex.getMessage().contains("Foreign key")
                || "23000".equals(ex.getSQLState());
    }
}
