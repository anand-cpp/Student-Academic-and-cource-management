package service;

import java.math.BigDecimal;
import java.util.List;
import dao.CourseDAO;
import model.Course;
import util.AppException;
import util.InputValidator;

/** Business rules and validation for course management. */
public class CourseService {

    private final CourseDAO dao = new CourseDAO();

    public List<Course> listAll() throws AppException {
        return dao.findAll();
    }

    public List<Course> search(String keyword) throws AppException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return listAll();
        }
        return dao.search(keyword.trim());
    }

    public Course getById(int id) throws AppException {
        return dao.findById(id);
    }

    public Course getByCourseCode(String courseCode) throws AppException {
        return dao.findByCourseCode(courseCode);
    }

    /** Create a course. Credits positive; passing marks must not exceed maximum marks. */
    public int create(String courseCode, String courseName, String department, String semester,
            String credits, String maximumMarks, String passingMarks, String assignedFacultyId)
            throws AppException {
        String code = InputValidator.idCode(courseCode, "Course code");
        String name = InputValidator.name(courseName, "Course name");
        String dept = InputValidator.required(department, "Department");
        int sem = InputValidator.positiveInt(semester, "Semester", 1);
        int cr = InputValidator.positiveInt(credits, "Credits", 1);
        BigDecimal max = InputValidator.decimal(maximumMarks, "Maximum marks");
        BigDecimal pass = InputValidator.decimal(passingMarks, "Passing marks");

        InputValidator.marks(BigDecimal.ZERO, max); // ensure max positive via setter later
        if (max.signum() <= 0) {
            throw new AppException("OUT_OF_RANGE", "Maximum marks must be positive.");
        }
        if (pass.signum() < 0) {
            throw new AppException("OUT_OF_RANGE", "Passing marks cannot be negative.");
        }
        if (pass.compareTo(max) > 0) {
            throw new AppException("OUT_OF_RANGE", "Passing marks cannot exceed maximum marks.");
        }

        if (dao.courseCodeExists(code, -1)) {
            throw new AppException("DUPLICATE", "Course code '" + code + "' already exists.");
        }

        Integer facultyId = null;
        if (assignedFacultyId != null && !assignedFacultyId.trim().isEmpty()) {
            try {
                facultyId = Integer.parseInt(assignedFacultyId.trim());
            } catch (NumberFormatException ex) {
                throw new AppException("INVALID_NUMBER", "Assigned faculty id must be a number.");
            }
        }

        Course course = new Course();
        course.setCourseCode(code);
        course.setCourseName(name);
        course.setDepartment(dept);
        course.setSemester(sem);
        course.setCredits(cr);
        course.setMaximumMarks(max);
        course.setPassingMarks(pass);
        course.setAssignedFacultyId(facultyId);

        return dao.insert(course);
    }

    public void update(int id, String courseCode, String courseName, String department, String semester,
            String credits, String maximumMarks, String passingMarks, String assignedFacultyId)
            throws AppException {
        Course existing = dao.findById(id);
        String code = InputValidator.idCode(courseCode, "Course code");
        String name = InputValidator.name(courseName, "Course name");
        String dept = InputValidator.required(department, "Department");
        int sem = InputValidator.positiveInt(semester, "Semester", 1);
        int cr = InputValidator.positiveInt(credits, "Credits", 1);
        BigDecimal max = InputValidator.decimal(maximumMarks, "Maximum marks");
        BigDecimal pass = InputValidator.decimal(passingMarks, "Passing marks");

        if (max.signum() <= 0) {
            throw new AppException("OUT_OF_RANGE", "Maximum marks must be positive.");
        }
        if (pass.signum() < 0) {
            throw new AppException("OUT_OF_RANGE", "Passing marks cannot be negative.");
        }
        if (pass.compareTo(max) > 0) {
            throw new AppException("OUT_OF_RANGE", "Passing marks cannot exceed maximum marks.");
        }

        if (dao.courseCodeExists(code, id)) {
            throw new AppException("DUPLICATE", "Course code '" + code + "' already exists.");
        }

        Integer facultyId = null;
        if (assignedFacultyId != null && !assignedFacultyId.trim().isEmpty()) {
            try {
                facultyId = Integer.parseInt(assignedFacultyId.trim());
            } catch (NumberFormatException ex) {
                throw new AppException("INVALID_NUMBER", "Assigned faculty id must be a number.");
            }
        }

        existing.setCourseCode(code);
        existing.setCourseName(name);
        existing.setDepartment(dept);
        existing.setSemester(sem);
        existing.setCredits(cr);
        existing.setMaximumMarks(max);
        existing.setPassingMarks(pass);
        existing.setAssignedFacultyId(facultyId);

        dao.update(existing);
    }

    public void delete(int id) throws AppException {
        dao.delete(id);
    }

    public int count() throws AppException {
        return dao.count();
    }
}
