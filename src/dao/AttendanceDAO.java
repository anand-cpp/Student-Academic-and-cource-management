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
import model.Attendance;
import db.DatabaseConnection;
import util.AppException;

/** Data Access Object for {@link Attendance}. */
public class AttendanceDAO {

    public List<Attendance> findAll() throws AppException {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT id, enrollment_id, class_date, status FROM attendance ORDER BY class_date DESC, id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load attendance records.", ex);
        }
        return list;
    }

    public List<Attendance> findByEnrollmentAndDate(int enrollmentId, LocalDate date) throws AppException {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT id, enrollment_id, class_date, status FROM attendance "
                + "WHERE enrollment_id = ? AND class_date = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.setString(2, dbDate(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load attendance.", ex);
        }
        return list;
    }

    public List<Attendance> findByEnrollment(int enrollmentId) throws AppException {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT id, enrollment_id, class_date, status FROM attendance "
                + "WHERE enrollment_id = ? ORDER BY class_date";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load attendance.", ex);
        }
        return list;
    }

    public List<Attendance> findByCourseAndDate(int courseId, LocalDate date) throws AppException {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.id, a.enrollment_id, a.class_date, a.status "
                + "FROM attendance a JOIN enrollments e ON e.id = a.enrollment_id "
                + "WHERE e.course_id = ? AND a.class_date = ? ORDER BY a.id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setString(2, dbDate(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load attendance for course and date.", ex);
        }
        return list;
    }

    public boolean exists(int enrollmentId, LocalDate date, int excludeId) throws AppException {
        String sql = "SELECT COUNT(*) FROM attendance WHERE enrollment_id = ? AND class_date = ? AND id <> ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.setString(2, dbDate(date));
            ps.setInt(3, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to check duplicate attendance.", ex);
        }
        return false;
    }

    /** Save attendance records for one course/date in a transaction. Replaces existing rows. */
    public void save(int courseId, LocalDate classDate,
            java.util.Map<Integer, String> statusByEnrollmentId, Connection conn) throws AppException {
        // Remove existing records for this course/date so re-entry is an update, not a duplicate.
        String deleteSql = "DELETE a FROM attendance a "
                + "JOIN enrollments e ON e.id = a.enrollment_id "
                + "WHERE e.course_id = ? AND a.class_date = ?";
        try (PreparedStatement del = conn.prepareStatement(deleteSql)) {
            del.setInt(1, courseId);
            del.setString(2, dbDate(classDate));
            del.executeUpdate();
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to prepare attendance save.", ex);
        }

        String insertSql = "INSERT INTO attendance (enrollment_id, class_date, status) VALUES (?,?,?)";
        try (PreparedStatement ins = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            for (java.util.Map.Entry<Integer, String> entry : statusByEnrollmentId.entrySet()) {
                ins.setInt(1, entry.getKey());
                ins.setString(2, dbDate(classDate));
                ins.setString(3, entry.getValue());
                ins.addBatch();
            }
            ins.executeBatch();
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to save attendance records.", ex);
        }
    }

    public void delete(int id) throws AppException {
        String sql = "DELETE FROM attendance WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to delete attendance record.", ex);
        }
    }

    /** Attendance percentage for one enrollment: (Present + Late) / Total recorded * 100. */
    public double percentageForEnrollment(int enrollmentId) throws AppException {
        String sql = "SELECT status, COUNT(*) FROM attendance WHERE enrollment_id = ? GROUP BY status";
        int present = 0, late = 0, total = 0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("status");
                    int count = rs.getInt(2);
                    total += count;
                    if ("PRESENT".equals(status)) {
                        present += count;
                    } else if ("LATE".equals(status)) {
                        late += count;
                    }
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to calculate attendance percentage.", ex);
        }
        if (total == 0) {
            return 0.0;
        }
        // Rule: Late counts as attended (student was in the session).
        return (double) (present + late) / total * 100.0;
    }

    public int count() throws AppException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM attendance");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to count attendance records.", ex);
        }
        return 0;
    }

    /**
     * Institution-wide attendance percentage.
     *
     * <p>Rule: Late counts as attended. Returns 0 when no attendance is recorded.
     */
    public double averagePercentage() throws AppException {
        String sql = "SELECT SUM(CASE WHEN status IN ('PRESENT','LATE') THEN 1 ELSE 0 END) AS attended, "
                + "COUNT(*) AS total FROM attendance";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int attended = rs.getInt("attended");
                int total = rs.getInt("total");
                if (total == 0) {
                    return 0.0;
                }
                return (double) attended / total * 100.0;
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to calculate average attendance.", ex);
        }
        return 0.0;
    }

    /** Course-level attendance summary: present, late, absent, average percentage. */
    public List<Map<String, Object>> courseSummary(int courseId) throws AppException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT s.id AS student_id, s.student_id AS student_code, s.full_name, "
                + "SUM(CASE WHEN a.status='PRESENT' THEN 1 ELSE 0 END) AS present, "
                + "SUM(CASE WHEN a.status='LATE' THEN 1 ELSE 0 END) AS late, "
                + "SUM(CASE WHEN a.status='ABSENT' THEN 1 ELSE 0 END) AS absent, "
                + "COUNT(a.id) AS total "
                + "FROM students s "
                + "JOIN enrollments e ON e.student_id = s.id AND e.course_id = ? AND e.status='Active' "
                + "LEFT JOIN attendance a ON a.enrollment_id = e.id "
                + "GROUP BY s.id, s.student_id, s.full_name ORDER BY s.full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("studentId", rs.getInt("student_id"));
                    row.put("studentCode", rs.getString("student_code"));
                    row.put("studentName", rs.getString("full_name"));
                    row.put("present", rs.getInt("present"));
                    row.put("late", rs.getInt("late"));
                    row.put("absent", rs.getInt("absent"));
                    row.put("total", rs.getInt("total"));
                    int p = rs.getInt("present"), l = rs.getInt("late"), t = rs.getInt("total");
                    double pct = (t == 0) ? 0.0 : (double) (p + l) / t * 100.0;
                    row.put("percentage", Math.round(pct * 100.0) / 100.0);
                    list.add(row);
                }
            }
        } catch (SQLException ex) {
            throw new AppException("DB_ERROR", "Failed to load course attendance summary.", ex);
        }
        return list;
    }

    private Attendance map(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setId(rs.getInt("id"));
        a.setEnrollmentId(rs.getInt("enrollment_id"));
        a.setClassDate(toLocalDate(rs.getDate("class_date")));
        a.setStatus(rs.getString("status"));
        return a;
    }

    private String dbDate(LocalDate date) {
        return (date == null) ? null : date.toString();
    }

    private LocalDate toLocalDate(java.sql.Date sqlDate) {
        return (sqlDate == null) ? null : sqlDate.toLocalDate();
    }
}
