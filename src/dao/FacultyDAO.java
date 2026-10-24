package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Faculty;
import db.DatabaseConnection;
import util.AppException;

/** Data Access Object for {@link Faculty}. */
public class FacultyDAO {

    private static final String BASE =
            "SELECT id, faculty_id, employee_id, full_name, email, phone, address, "
            + "date_of_birth, department, designation, specialization FROM faculty";

    public List<Faculty> findAll() throws AppException {
        List<Faculty> list = new ArrayList<>();
        String sql = BASE + " ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load faculty.", ex);
        }
        return list;
    }

    public List<Faculty> search(String keyword) throws AppException {
        List<Faculty> list = new ArrayList<>();
        String sql = BASE + " WHERE faculty_id LIKE ? OR employee_id LIKE ? OR full_name LIKE ? "
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
            throw new AppException("DB_ERROR", "Failed to search faculty.", ex);
        }
        return list;
    }

    public Faculty findById(int id) throws AppException {
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
            throw new AppException("DB_ERROR", "Failed to load faculty.", ex);
        }
        throw new AppException("NOT_FOUND", "Faculty member not found.");
    }

    public boolean employeeIdExists(String employeeId, int excludeId) throws AppException {
        String sql = "SELECT COUNT(*) FROM faculty WHERE employee_id = ? AND id <> ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to validate employee id.", ex);
        }
        return false;
    }

    public boolean emailExists(String email, int excludeId) throws AppException {
        String sql = "SELECT COUNT(*) FROM faculty WHERE email = ? AND id <> ?";
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

    public int insert(Faculty faculty) throws AppException {
        String sql = "INSERT INTO faculty (faculty_id, employee_id, full_name, email, phone, address, "
                + "date_of_birth, department, designation, specialization) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, faculty.getFacultyId());
            ps.setString(2, faculty.getEmployeeId());
            ps.setString(3, faculty.getFullName());
            ps.setString(4, faculty.getEmail());
            ps.setString(5, faculty.getPhone());
            ps.setString(6, faculty.getAddress());
            ps.setString(7, dbDate(faculty.getDateOfBirth()));
            ps.setString(8, faculty.getDepartment());
            ps.setString(9, faculty.getDesignation());
            ps.setString(10, faculty.getSpecialization());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new AppException("INSERT_FAILED", "Creating faculty failed, no rows affected.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new AppException("INSERT_FAILED", "Creating faculty failed, no id returned.");
        } catch (SQLException ex) {
            if (isDuplicate(ex)) {
                throw new AppException("DUPLICATE", "A faculty member with this ID, employee id, or email already exists.", ex);
            }
            throw new AppException("DB_ERROR", "Failed to create faculty.", ex);
        }
    }

    public void update(Faculty faculty) throws AppException {
        String sql = "UPDATE faculty SET faculty_id = ?, employee_id = ?, full_name = ?, email = ?, "
                + "phone = ?, address = ?, date_of_birth = ?, department = ?, designation = ?, "
                + "specialization = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, faculty.getFacultyId());
            ps.setString(2, faculty.getEmployeeId());
            ps.setString(3, faculty.getFullName());
            ps.setString(4, faculty.getEmail());
            ps.setString(5, faculty.getPhone());
            ps.setString(6, faculty.getAddress());
            ps.setString(7, dbDate(faculty.getDateOfBirth()));
            ps.setString(8, faculty.getDepartment());
            ps.setString(9, faculty.getDesignation());
            ps.setString(10, faculty.getSpecialization());
            ps.setInt(11, faculty.getId());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new AppException("NOT_FOUND", "Faculty member not found.");
            }
        } catch (SQLException ex) {
            if (isDuplicate(ex)) {
                throw new AppException("DUPLICATE", "A faculty member with this ID, employee id, or email already exists.", ex);
            }
            throw new AppException("DB_ERROR", "Failed to update faculty.", ex);
        }
    }

    public void delete(int id) throws AppException {
        String sql = "DELETE FROM faculty WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new AppException("NOT_FOUND", "Faculty member not found.");
            }
        } catch (SQLException ex) {
            if (isForeignKeyViolation(ex)) {
                throw new AppException("CONSTRAINT",
                        "Cannot delete this faculty member: courses are assigned to them. "
                        + "Unassign or delete those courses first.", ex);
            }
            throw new AppException("DB_ERROR", "Failed to delete faculty member.", ex);
        }
    }

    public int count() throws AppException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM faculty");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to count faculty.", ex);
        }
        return 0;
    }

    private Faculty map(ResultSet rs) throws SQLException {
        Faculty f = new Faculty();
        f.setId(rs.getInt("id"));
        f.setFacultyId(rs.getString("faculty_id"));
        f.setEmployeeId(rs.getString("employee_id"));
        f.setFullName(rs.getString("full_name"));
        f.setEmail(rs.getString("email"));
        f.setPhone(rs.getString("phone"));
        f.setAddress(rs.getString("address"));
        f.setDateOfBirth(toLocalDate(rs.getDate("date_of_birth")));
        f.setDepartment(rs.getString("department"));
        f.setDesignation(rs.getString("designation"));
        f.setSpecialization(rs.getString("specialization"));
        return f;
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
