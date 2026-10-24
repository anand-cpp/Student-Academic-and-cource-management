package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Student;
import db.DatabaseConnection;
import util.AppException;

/**
 * Data Access Object for {@link Student}.
 *
 * <p>All SQL is expressed with {@link PreparedStatement} (application-supplied
 * values are never concatenated), which prevents SQL injection. Resources are
 * closed with try-with-resources. Database errors are wrapped in
 * {@link AppException} so the service/UI layer can show a friendly message.
 */
public class StudentDAO {

    private static final String BASE =
            "SELECT id, student_id, roll_number, full_name, email, phone, address, "
            + "date_of_birth, department, semester, admission_date FROM students";

    public List<Student> findAll() throws AppException {
        List<Student> list = new ArrayList<>();
        String sql = BASE + " ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load students.", ex);
        }
        return list;
    }

    public List<Student> search(String keyword) throws AppException {
        List<Student> list = new ArrayList<>();
        String sql = BASE + " WHERE student_id LIKE ? OR roll_number LIKE ? OR full_name LIKE ? "
                + "OR email LIKE ? OR department LIKE ? ORDER BY id";
        String like = "%" + keyword + "%";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) {
                ps.setString(i, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to search students.", ex);
        }
        return list;
    }

    public Student findById(int id) throws AppException {
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
            throw new AppException("DB_ERROR", "Failed to load student.", ex);
        }
        throw new AppException("NOT_FOUND", "Student not found.");
    }

    public Student findByStudentId(String studentId) throws AppException {
        String sql = BASE + " WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load student.", ex);
        }
        return null;
    }

    public boolean rollNumberExists(String rollNumber, int excludeId) throws AppException {
        String sql = "SELECT COUNT(*) FROM students WHERE roll_number = ? AND id <> ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNumber);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to validate roll number.", ex);
        }
        return false;
    }

    public boolean emailExists(String email, int excludeId) throws AppException {
        String sql = "SELECT COUNT(*) FROM students WHERE email = ? AND id <> ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to validate email.", ex);
        }
        return false;
    }

    /** Insert a new student and return the generated database id. */
    public int insert(Student student) throws AppException {
        String sql = "INSERT INTO students (student_id, roll_number, full_name, email, phone, address, "
                + "date_of_birth, department, semester, admission_date) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, student.getStudentId());
            ps.setString(2, student.getRollNumber());
            ps.setString(3, student.getFullName());
            ps.setString(4, student.getEmail());
            ps.setString(5, student.getPhone());
            ps.setString(6, student.getAddress());
            ps.setString(7, dbDate(student.getDateOfBirth()));
            ps.setString(8, student.getDepartment());
            ps.setInt(9, student.getSemester());
            ps.setString(10, dbDate(student.getAdmissionDate()));
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new AppException("INSERT_FAILED", "Creating student failed, no rows affected.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new AppException("INSERT_FAILED", "Creating student failed, no id returned.");
        } catch (SQLException ex) {
            if (isDuplicate(ex)) {
                throw new AppException("DUPLICATE", "A student with this ID, roll number, or email already exists.", ex);
            }
            throw new AppException("DB_ERROR", "Failed to create student.", ex);
        }
    }

    public void update(Student student) throws AppException {
        String sql = "UPDATE students SET student_id = ?, roll_number = ?, full_name = ?, email = ?, "
                + "phone = ?, address = ?, date_of_birth = ?, department = ?, semester = ?, "
                + "admission_date = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getStudentId());
            ps.setString(2, student.getRollNumber());
            ps.setString(3, student.getFullName());
            ps.setString(4, student.getEmail());
            ps.setString(5, student.getPhone());
            ps.setString(6, student.getAddress());
            ps.setString(7, dbDate(student.getDateOfBirth()));
            ps.setString(8, student.getDepartment());
            ps.setInt(9, student.getSemester());
            ps.setString(10, dbDate(student.getAdmissionDate()));
            ps.setInt(11, student.getId());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new AppException("NOT_FOUND", "Student not found.");
            }
        } catch (SQLException ex) {
            if (isDuplicate(ex)) {
                throw new AppException("DUPLICATE", "A student with this ID, roll number, or email already exists.", ex);
            }
            throw new AppException("DB_ERROR", "Failed to update student.", ex);
        }
    }

    /** Delete a student. Fails if enrollments reference it (FK RESTRICT). */
    public void delete(int id) throws AppException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new AppException("NOT_FOUND", "Student not found.");
            }
        } catch (SQLException ex) {
            if (isForeignKeyViolation(ex)) {
                throw new AppException("CONSTRAINT",
                        "Cannot delete this student: enrollments, attendance, or results reference it. "
                        + "Remove dependent records first.", ex);
            }
            throw new AppException("DB_ERROR", "Failed to delete student.", ex);
        }
    }

    public int count() throws AppException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM students");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to count students.", ex);
        }
        return 0;
    }

    private Student map(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setStudentId(rs.getString("student_id"));
        s.setRollNumber(rs.getString("roll_number"));
        s.setFullName(rs.getString("full_name"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        s.setAddress(rs.getString("address"));
        s.setDateOfBirth(toLocalDate(rs.getDate("date_of_birth")));
        s.setDepartment(rs.getString("department"));
        s.setSemester(rs.getInt("semester"));
        s.setAdmissionDate(toLocalDate(rs.getDate("admission_date")));
        return s;
    }

    private String dbDate(LocalDate date) {
        return (date == null) ? null : date.toString();
    }

    private LocalDate toLocalDate(java.sql.Date sqlDate) {
        return (sqlDate == null) ? null : sqlDate.toLocalDate();
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
