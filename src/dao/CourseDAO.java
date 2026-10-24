package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Course;
import db.DatabaseConnection;
import util.AppException;

/** Data Access Object for {@link Course}. */
public class CourseDAO {

    private static final String BASE =
            "SELECT id, course_code, course_name, department, semester, credits, "
            + "maximum_marks, passing_marks, assigned_faculty_id FROM courses";

    public List<Course> findAll() throws AppException {
        List<Course> list = new ArrayList<>();
        String sql = BASE + " ORDER BY course_code";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load courses.", ex);
        }
        return list;
    }

    public List<Course> search(String keyword) throws AppException {
        List<Course> list = new ArrayList<>();
        String sql = BASE + " WHERE course_code LIKE ? OR course_name LIKE ? OR department LIKE ? "
                + "ORDER BY course_code";
        String like = "%" + keyword + "%";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 3; i++) {
                ps.setString(i, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to search courses.", ex);
        }
        return list;
    }

    public Course findById(int id) throws AppException {
        String sql = BASE + " WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load course.", ex);
        }
        throw new AppException("NOT_FOUND", "Course not found.");
    }

    public Course findByCourseCode(String courseCode) throws AppException {
        String sql = BASE + " WHERE course_code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load course.", ex);
        }
        return null;
    }

    public boolean courseCodeExists(String courseCode, int excludeId) throws AppException {
        String sql = "SELECT COUNT(*) FROM courses WHERE course_code = ? AND id <> ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to validate course code.", ex);
        }
        return false;
    }

    public int insert(Course course) throws AppException {
        String sql = "INSERT INTO courses (course_code, course_name, department, semester, credits, "
                + "maximum_marks, passing_marks, assigned_faculty_id) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, course.getCourseCode());
            ps.setString(2, course.getCourseName());
            ps.setString(3, course.getDepartment());
            ps.setInt(4, course.getSemester());
            ps.setInt(5, course.getCredits());
            ps.setBigDecimal(6, course.getMaximumMarks());
            ps.setBigDecimal(7, course.getPassingMarks());
            if (course.getAssignedFacultyId() == null) {
                ps.setNull(8, java.sql.Types.INTEGER);
            } else {
                ps.setInt(8, course.getAssignedFacultyId());
            }
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new AppException("INSERT_FAILED", "Creating course failed, no rows affected.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new AppException("INSERT_FAILED", "Creating course failed, no id returned.");
        } catch (SQLException ex) {
            if (isDuplicate(ex)) {
                throw new AppException("DUPLICATE", "A course with this code already exists.", ex);
            }
            throw new AppException("DB_ERROR", "Failed to create course.", ex);
        }
    }

    public void update(Course course) throws AppException {
        String sql = "UPDATE courses SET course_code = ?, course_name = ?, department = ?, semester = ?, "
                + "credits = ?, maximum_marks = ?, passing_marks = ?, assigned_faculty_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getCourseCode());
            ps.setString(2, course.getCourseName());
            ps.setString(3, course.getDepartment());
            ps.setInt(4, course.getSemester());
            ps.setInt(5, course.getCredits());
            ps.setBigDecimal(6, course.getMaximumMarks());
            ps.setBigDecimal(7, course.getPassingMarks());
            if (course.getAssignedFacultyId() == null) {
                ps.setNull(8, java.sql.Types.INTEGER);
            } else {
                ps.setInt(8, course.getAssignedFacultyId());
            }
            ps.setInt(9, course.getId());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new AppException("NOT_FOUND", "Course not found.");
            }
        } catch (SQLException ex) {
            if (isDuplicate(ex)) {
                throw new AppException("DUPLICATE", "A course with this code already exists.", ex);
            }
            throw new AppException("DB_ERROR", "Failed to update course.", ex);
        }
    }

    /** Delete a course. Fails if enrollments/results reference it (FK RESTRICT). */
    public void delete(int id) throws AppException {
        String sql = "DELETE FROM courses WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new AppException("NOT_FOUND", "Course not found.");
            }
        } catch (SQLException ex) {
            if (isForeignKeyViolation(ex)) {
                throw new AppException("CONSTRAINT",
                        "Cannot delete this course: enrollments, attendance, or results reference it. "
                        + "Remove dependent records first.", ex);
            }
            throw new AppException("DB_ERROR", "Failed to delete course.", ex);
        }
    }

    public BigDecimal getMaximumMarks(int courseId) throws AppException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT maximum_marks FROM courses WHERE id = ?")) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("maximum_marks");
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load course maximum marks.", ex);
        }
        throw new AppException("NOT_FOUND", "Course not found.");
    }

    public int count() throws AppException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM courses");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to count courses.", ex);
        }
        return 0;
    }

    private Course map(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setId(rs.getInt("id"));
        c.setCourseCode(rs.getString("course_code"));
        c.setCourseName(rs.getString("course_name"));
        c.setDepartment(rs.getString("department"));
        c.setSemester(rs.getInt("semester"));
        c.setCredits(rs.getInt("credits"));
        c.setMaximumMarks(rs.getBigDecimal("maximum_marks"));
        c.setPassingMarks(rs.getBigDecimal("passing_marks"));
        Object af = rs.getObject("assigned_faculty_id");
        c.setAssignedFacultyId(af == null ? null : ((Number) af).intValue());
        return c;
    }

    private boolean isDuplicate(SQLException ex) {
        String code = ex.getSQLState();
        return "23000".equals(code) || ex.getMessage().contains("Duplicate")
                || ex.getMessage().contains("unique");
    }

    private boolean isForeignKeyViolation(SQLException ex) {
        return ex.getMessage().contains("foreign key")
                || ex.getMessage().contains("Foreign key")
                || "23000".equals(ex.getSQLState());
    }
}
