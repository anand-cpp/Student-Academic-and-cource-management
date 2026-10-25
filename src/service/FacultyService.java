package service;

import java.time.LocalDate;
import java.util.List;
import dao.FacultyDAO;
import model.Faculty;
import util.AppException;
import util.DateUtils;
import util.InputValidator;

/** Business rules and validation for faculty management. */
public class FacultyService {

    private final FacultyDAO dao = new FacultyDAO();

    public List<Faculty> listAll() throws AppException {
        return dao.findAll();
    }

    public List<Faculty> search(String keyword) throws AppException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return listAll();
        }
        return dao.search(keyword.trim());
    }

    public Faculty getById(int id) throws AppException {
        return dao.findById(id);
    }

    public int create(String facultyId, String employeeId, String fullName, String email,
            String phone, String address, String dob, String department, String designation,
            String specialization) throws AppException {
        String fid = InputValidator.idCode(facultyId, "Faculty ID");
        String eid = InputValidator.idCode(employeeId, "Employee ID");
        String name = InputValidator.name(fullName, "Full name");
        String em = InputValidator.email(email);
        String ph = InputValidator.phone(phone);
        String addr = InputValidator.required(address, "Address");
        LocalDate birth = DateUtils.parse(dob);
        String dept = InputValidator.required(department, "Department");
        String desig = InputValidator.required(designation, "Designation");
        String spec = InputValidator.required(specialization, "Specialization");

        // Uniqueness checks
        if (facultyIdExists(fid, -1)) {
            throw new AppException("DUPLICATE", "Faculty ID '" + fid + "' already exists.");
        }
        if (dao.employeeIdExists(eid, -1)) {
            throw new AppException("DUPLICATE", "Employee ID '" + eid + "' already exists.");
        }
        if (dao.emailExists(em, -1)) {
            throw new AppException("DUPLICATE", "Email '" + em + "' already exists.");
        }

        Faculty faculty = new Faculty();
        faculty.setFacultyId(fid);
        faculty.setEmployeeId(eid);
        faculty.setFullName(name);
        faculty.setEmail(em);
        faculty.setPhone(ph);
        faculty.setAddress(addr);
        faculty.setDateOfBirth(birth);
        faculty.setDepartment(dept);
        faculty.setDesignation(desig);
        faculty.setSpecialization(spec);

        return dao.insert(faculty);
    }

    public void update(int id, String facultyId, String employeeId, String fullName, String email,
            String phone, String address, String dob, String department, String designation,
            String specialization) throws AppException {
        Faculty existing = dao.findById(id);
        String fid = InputValidator.idCode(facultyId, "Faculty ID");
        String eid = InputValidator.idCode(employeeId, "Employee ID");
        String name = InputValidator.name(fullName, "Full name");
        String em = InputValidator.email(email);
        String ph = InputValidator.phone(phone);
        String addr = InputValidator.required(address, "Address");
        LocalDate birth = DateUtils.parse(dob);
        String dept = InputValidator.required(department, "Department");
        String desig = InputValidator.required(designation, "Designation");
        String spec = InputValidator.required(specialization, "Specialization");

        if (facultyIdExists(fid, id)) {
            throw new AppException("DUPLICATE", "Faculty ID '" + fid + "' already exists.");
        }
        if (dao.employeeIdExists(eid, id)) {
            throw new AppException("DUPLICATE", "Employee ID '" + eid + "' already exists.");
        }
        if (dao.emailExists(em, id)) {
            throw new AppException("DUPLICATE", "Email '" + em + "' already exists.");
        }

        existing.setFacultyId(fid);
        existing.setEmployeeId(eid);
        existing.setFullName(name);
        existing.setEmail(em);
        existing.setPhone(ph);
        existing.setAddress(addr);
        existing.setDateOfBirth(birth);
        existing.setDepartment(dept);
        existing.setDesignation(desig);
        existing.setSpecialization(spec);

        dao.update(existing);
    }

    public void delete(int id) throws AppException {
        dao.delete(id);
    }

    public int count() throws AppException {
        return dao.count();
    }

    /** Helper: check whether a faculty business id exists for a different row. */
    private boolean facultyIdExists(String facultyId, int excludeId) throws AppException {
        // FacultyDAO does not expose findByFacultyId; reuse search approach via findAll is costly,
        // so we add a tiny direct check using a dedicated DAO method would be cleaner. Here we
        // delegate to a simple query through a temporary DAO call pattern.
        try (java.sql.Connection conn = db.DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(
                     "SELECT COUNT(*) FROM faculty WHERE faculty_id = ? AND id <> ?")) {
            ps.setString(1, facultyId);
            ps.setInt(2, excludeId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (util.AppException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new util.AppException("DB_ERROR", "Failed to validate faculty id.", ex);
        }
        return false;
    }
}
