package smoke;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import dao.AttendanceDAO;
import model.Attendance;
import model.Course;
import model.Enrollment;
import model.Faculty;
import model.Result;
import model.Student;
import service.AttendanceService;
import service.CourseService;
import service.EnrollmentService;
import service.FacultyService;
import service.ReportService;
import service.ResultService;
import service.StudentService;

/**
 * End-to-end service lifecycle test against the real MySQL schema.
 * Creates a temporary student/faculty/course, enrolls, marks attendance,
 * records a result, updates every record, then removes it all again.
 * Run from the project root with MySQL running.
 */
public final class CrudSmoke {

    private static int passed;
    private static int failed;

    private static final StudentService students = new StudentService();
    private static final FacultyService faculty = new FacultyService();
    private static final CourseService courses = new CourseService();
    private static final EnrollmentService enrollments = new EnrollmentService();
    private static final AttendanceService attendance = new AttendanceService();
    private static final ResultService results = new ResultService();
    private static final ReportService report = new ReportService();
    private static final AttendanceDAO attendanceDao = new AttendanceDAO();

    private static int studentId = -1;
    private static int facultyId = -1;
    private static int courseId = -1;
    private static int unassignedCourseId = -1;
    private static int enrollmentId = -1;
    private static boolean resultSaved;

    public static void main(String[] args) {
        String stamp = String.valueOf(System.currentTimeMillis() % 1000000);
        String studentCode = "T" + stamp;
        String facultyCode = "F" + stamp;
        String courseCode = "C" + stamp;
        String employeeCode = "E" + stamp;
        String rollNumber = "R" + stamp;
        String studentEmail = "temp." + stamp + "@college.edu";
        String facultyEmail = "prof." + stamp + "@college.edu";
        LocalDate today = LocalDate.now();

        try {
            /* ---------- Create ---------- */
            facultyId = faculty.create(facultyCode, employeeCode, "Temp Faculty",
                    facultyEmail, "9876500000", "Block A, Campus", "1980-03-12",
                    "Computer Science", "Assistant Professor", "Databases");
            check("faculty created", true, facultyId > 0);

            studentId = students.create(studentCode, rollNumber, "Temp Student",
                    studentEmail, "9876511111", "Hostel B, Room 12", "2004-05-10",
                    "Computer Science", "3", "2024-08-01");
            check("student created", true, studentId > 0);

            courseId = courses.create(courseCode, "Temp Course", "Computer Science",
                    "3", "4", "100", "40", String.valueOf(facultyId));
            check("course created", true, courseId > 0);

            unassignedCourseId = courses.create("U" + stamp, "Unassigned Course",
                    "Computer Science", "3", "3", "100", "40", "");
            check("unassigned course created", true, unassignedCourseId > 0);

            List<Map<String, Object>> byFaculty = report.coursesByFaculty(facultyId);
            check("report courses-by-faculty size", 1, byFaculty.size());
            check("report courses-by-faculty code", courseCode, byFaculty.get(0).get("courseCode"));

            /* ---------- Enroll ---------- */
            enrollmentId = enrollments.enroll(studentId, courseId, today.toString());
            check("enrollment created", true, enrollmentId > 0);

            List<Map<String, Object>> roster = attendance.enrollmentsForCourse(courseId);
            check("roster size", 1, roster.size());
            check("roster student code", studentCode, roster.get(0).get("studentId"));
            check("roster student name", "Temp Student", roster.get(0).get("studentName"));

            List<Map<String, Object>> inCourse = report.studentsInCourse(courseId);
            check("report students-in-course size", 1, inCourse.size());
            check("report student code", studentCode, inCourse.get(0).get("studentCode"));
            check("report student name", "Temp Student", inCourse.get(0).get("studentName"));

            expectError("duplicate student code rejected", "DUPLICATE",
                    () -> students.create(studentCode, "D" + stamp, "Duplicate Student",
                            "dup." + stamp + "@college.edu", "9876533333", "Somewhere",
                            "2004-01-01", "Computer Science", "3", "2024-08-01"));
            expectError("duplicate active enrollment rejected", "DUPLICATE",
                    () -> enrollments.enroll(studentId, courseId, today.toString()));
            expectError("student delete blocked by enrollment", "CONSTRAINT",
                    () -> students.delete(studentId));

            /* ---------- Attendance + result ---------- */
            attendance.save(courseId, today, Map.of(enrollmentId, "ABSENT"));
            check("attendance percentage after ABSENT", 0.0,
                    attendance.percentageForEnrollment(enrollmentId));
            attendance.save(courseId, today, Map.of(enrollmentId, "PRESENT"));
            check("attendance percentage after PRESENT", 100.0,
                    attendance.percentageForEnrollment(enrollmentId));

            int resultId = results.save(enrollmentId, "85");
            resultSaved = true;
            check("result created", true, resultId > 0);
            Result result = results.findByEnrollment(enrollmentId);
            check("result marks", 0, result.getMarksObtained().compareTo(new BigDecimal("85")));
            check("result percentage", 0, result.getPercentage().compareTo(new BigDecimal("85.00")));
            check("result grade", "B", result.getGrade());
            expectError("marks above maximum rejected", "OUT_OF_RANGE",
                    () -> results.save(enrollmentId, "101"));
            expectError("non-numeric marks rejected", "INVALID_NUMBER",
                    () -> results.save(enrollmentId, "abc"));

            /* ---------- Update everything ---------- */
            students.update(studentId, studentCode, rollNumber, "Temp Student Updated",
                    "updated." + stamp + "@college.edu", "9876522222",
                    "Hostel C, Room 7", "2004-05-10", "Information Technology", "4",
                    "2024-08-01");
            check("student updated", "Temp Student Updated",
                    students.getById(studentId).getFullName());
            check("student department updated", "Information Technology",
                    students.getById(studentId).getDepartment());

            faculty.update(facultyId, facultyCode, employeeCode, "Temp Faculty Updated",
                    facultyEmail, "9876500000", "Block A, Campus", "1980-03-12",
                    "Computer Science", "Associate Professor", "Data Mining");
            check("faculty updated", "Associate Professor",
                    faculty.getById(facultyId).getDesignation());
            check("faculty specialization updated", "Data Mining",
                    faculty.getById(facultyId).getSpecialization());

            courses.update(courseId, courseCode, "Temp Course Updated", "Computer Science",
                    "3", "3", "100", "40", String.valueOf(facultyId));
            check("course updated", "Temp Course Updated",
                    courses.getById(courseId).getCourseName());
            check("course credits updated", 3,
                    courses.getById(courseId).getCredits());

            enrollments.update(enrollmentId, studentId, courseId, today.toString(), "Completed");
            List<Enrollment> studentEnrollments = enrollments.findByStudent(studentId);
            check("enrollment status updated", "Completed",
                    studentEnrollments.get(0).getStatus());
            check("report includes completed enrollment", "Completed",
                    report.studentsInCourse(courseId).get(0).get("status"));

            attendance.save(courseId, today, Map.of(enrollmentId, "LATE"));
            check("attendance percentage after LATE", 100.0,
                    attendance.percentageForEnrollment(enrollmentId));

            results.save(enrollmentId, "92");
            Result updated = results.findByEnrollment(enrollmentId);
            check("result marks updated", 0, updated.getMarksObtained().compareTo(new BigDecimal("92")));
            check("result grade updated", "A", updated.getGrade());

            /* ---------- Delete ---------- */
            cleanup();
            check("enrollment deleted", true, enrollments.findByStudent(studentId).isEmpty());
            check("course deleted", true, missing(() -> courses.getById(courseId)));
            check("unassigned course deleted", true, missing(() -> courses.getById(unassignedCourseId)));
            check("student deleted", true, missing(() -> students.getById(studentId)));
            check("faculty deleted", true, missing(() -> faculty.getById(facultyId)));
            studentId = -1;
            facultyId = -1;
            courseId = -1;
            unassignedCourseId = -1;
            enrollmentId = -1;
        } catch (Exception ex) {
            failed++;
            System.out.println("FAIL exception: " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            ex.printStackTrace(System.out);
        } finally {
            try {
                cleanup();
            } catch (Exception ex) {
                System.out.println("CLEANUP WARNING: " + ex.getMessage());
            }
        }

        System.out.println("CRUD_RESULT passed=" + passed + " failed=" + failed);
        System.exit(failed == 0 ? 0 : 1);
    }

    /** Reverse-order cleanup; safe to call more than once. */
    private static void cleanup() throws Exception {
        if (enrollmentId > 0) {
            if (resultSaved) {
                results.deleteByEnrollment(enrollmentId);
                resultSaved = false;
            }
            for (Attendance a : attendanceDao.findByEnrollment(enrollmentId)) {
                attendanceDao.delete(a.getId());
            }
            enrollments.delete(enrollmentId);
            enrollmentId = -1;
        }
        if (courseId > 0) {
            courses.delete(courseId);
            courseId = -1;
        }
        if (unassignedCourseId > 0) {
            courses.delete(unassignedCourseId);
            unassignedCourseId = -1;
        }
        if (studentId > 0) {
            students.delete(studentId);
            studentId = -1;
        }
        if (facultyId > 0) {
            faculty.delete(facultyId);
            facultyId = -1;
        }
    }

    private static boolean missing(ThrowingSupplier<?> call) {
        try {
            call.get();
            return false;
        } catch (Exception ex) {
            return "NOT_FOUND".equals(ex instanceof util.AppException ae ? ae.getErrorCode() : null);
        }
    }

    private static void check(String label, Object expected, Object actual) {
        boolean ok = expected == null ? actual == null : expected.equals(actual);
        if (ok) {
            passed++;
            System.out.println("PASS " + label + " = " + actual);
        } else {
            failed++;
            System.out.println("FAIL " + label + " expected=" + expected + " actual=" + actual);
        }
    }

    private static void expectError(String label, String expectedCode, ThrowingRunnable call) {
        try {
            call.run();
            failed++;
            System.out.println("FAIL " + label + " expected error " + expectedCode + " but none was thrown");
        } catch (util.AppException ex) {
            check(label, expectedCode, ex.getErrorCode());
        } catch (Exception ex) {
            failed++;
            System.out.println("FAIL " + label + " unexpected " + ex.getClass().getSimpleName()
                    + ": " + ex.getMessage());
        }
    }

    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private interface ThrowingSupplier<T> {
        T get() throws Exception;
    }
}
