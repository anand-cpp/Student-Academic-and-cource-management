package service;

import java.time.LocalDate;
import java.util.List;
import dao.StudentDAO;
import model.Student;
import util.AppException;
import util.DateUtils;
import util.InputValidator;

/** Business rules and validation for student management. */
public class StudentService {

    private final StudentDAO dao = new StudentDAO();

    public List<Student> listAll() throws AppException {
        return dao.findAll();
    }

    public List<Student> search(String keyword) throws AppException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return listAll();
        }
        return dao.search(keyword.trim());
    }

    public Student getById(int id) throws AppException {
        return dao.findById(id);
    }

    public Student getByStudentId(String studentId) throws AppException {
        return dao.findByStudentId(studentId);
    }

    /** Create a student after validating uniqueness of id, roll number, email. */
    public int create(String studentId, String rollNumber, String fullName, String email,
            String phone, String address, String dob, String department, String semester,
            String admissionDate) throws AppException {
        // Validate all fields before touching the database.
        String sid = InputValidator.idCode(studentId, "Student ID");
        String roll = InputValidator.idCode(rollNumber, "Roll number");
        String name = InputValidator.name(fullName, "Full name");
        String em = InputValidator.email(email);
        String ph = InputValidator.phone(phone);
        String addr = InputValidator.required(address, "Address");
        LocalDate birth = DateUtils.parse(dob);
        String dept = InputValidator.required(department, "Department");
        int sem = InputValidator.positiveInt(semester, "Semester", 1);
        LocalDate admission = DateUtils.parse(admissionDate);
        DateUtils.notFuture(admission, "Admission date");

        if (dao.findByStudentId(sid) != null) {
            throw new AppException("DUPLICATE", "Student ID '" + sid + "' already exists.");
        }
        if (dao.rollNumberExists(roll, -1)) {
            throw new AppException("DUPLICATE", "Roll number '" + roll + "' already exists.");
        }
        if (dao.emailExists(em, -1)) {
            throw new AppException("DUPLICATE", "Email '" + em + "' already exists.");
        }

        Student student = new Student();
        student.setStudentId(sid);
        student.setRollNumber(roll);
        student.setFullName(name);
        student.setEmail(em);
        student.setPhone(ph);
        student.setAddress(addr);
        student.setDateOfBirth(birth);
        student.setDepartment(dept);
        student.setSemester(sem);
        student.setAdmissionDate(admission);

        return dao.insert(student);
    }

    /** Update an existing student after validating uniqueness against other rows. */
    public void update(int id, String studentId, String rollNumber, String fullName, String email,
            String phone, String address, String dob, String department, String semester,
            String admissionDate) throws AppException {
        Student existing = dao.findById(id); // throws if not found
        String sid = InputValidator.idCode(studentId, "Student ID");
        String roll = InputValidator.idCode(rollNumber, "Roll number");
        String name = InputValidator.name(fullName, "Full name");
        String em = InputValidator.email(email);
        String ph = InputValidator.phone(phone);
        String addr = InputValidator.required(address, "Address");
        LocalDate birth = DateUtils.parse(dob);
        String dept = InputValidator.required(department, "Department");
        int sem = InputValidator.positiveInt(semester, "Semester", 1);
        LocalDate admission = DateUtils.parse(admissionDate);
        DateUtils.notFuture(admission, "Admission date");

        // Uniqueness: ignore the student being updated
        Student bySid = dao.findByStudentId(sid);
        if (bySid != null && bySid.getId() != id) {
            throw new AppException("DUPLICATE", "Student ID '" + sid + "' already exists.");
        }
        if (dao.rollNumberExists(roll, id)) {
            throw new AppException("DUPLICATE", "Roll number '" + roll + "' already exists.");
        }
        if (dao.emailExists(em, id)) {
            throw new AppException("DUPLICATE", "Email '" + em + "' already exists.");
        }

        existing.setStudentId(sid);
        existing.setRollNumber(roll);
        existing.setFullName(name);
        existing.setEmail(em);
        existing.setPhone(ph);
        existing.setAddress(addr);
        existing.setDateOfBirth(birth);
        existing.setDepartment(dept);
        existing.setSemester(sem);
        existing.setAdmissionDate(admission);

        dao.update(existing);
    }

    public void delete(int id) throws AppException {
        dao.delete(id);
    }

    public int count() throws AppException {
        return dao.count();
    }
}
