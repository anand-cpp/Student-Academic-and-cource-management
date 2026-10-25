package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import dao.CourseDAO;
import dao.FacultyDAO;
import model.Course;
import model.Faculty;
import service.ReportService;
import ui.components.AppComboBox;
import ui.components.AppIcon;
import ui.components.DataCard;
import ui.components.RoundedButton;
import ui.components.SideTabs;
import ui.components.SectionHeader;
import ui.components.StatCard;
import ui.components.TableCard;
import util.AppException;
import util.UIStyles;

/**
 * Analysis workspace.
 *
 * <p>Six report views share a vertical report library on the left and a single
 * card stage on the right:
 * <ol>
 *   <li>Student directory</li>
 *   <li>Students enrolled in a course</li>
 *   <li>Courses assigned to a faculty member</li>
 *   <li>Course-wise attendance summary</li>
 *   <li>Course-wise academic results</li>
 *   <li>Overall academic performance</li>
 * </ol>
 * All rows come from MySQL via {@link ReportService}.
 */
public class ReportsPanel extends BasePanel implements MainFrame.Refreshable {

    private static final long serialVersionUID = 1L;

    private static final String[] REPORT_NAMES = {
        "Student directory", "Students in a course", "Courses by faculty",
        "Course attendance", "Course results", "Overall performance"
    };

    private final ReportService report = new ReportService();

    private final CardLayout stageLayout = new CardLayout();
    private final JPanel stage = new JPanel(stageLayout);

    private AppComboBox<EnrollmentPanel.ComboItem> cboStudentsCourse;
    private AppComboBox<EnrollmentPanel.ComboItem> cboFaculty;
    private AppComboBox<EnrollmentPanel.ComboItem> cboAttendanceCourse;
    private AppComboBox<EnrollmentPanel.ComboItem> cboResultsCourse;

    private JTable dirTable, studentsInCourseTable, coursesByFacultyTable,
            attendanceSummaryTable, courseResultsTable, allResultsTable;
    private TableCard dirCard, studentsCard, facultyCard, attendanceCard,
            resultsCard, allResultsCard;

    private StatCard cardTotal, cardPass, cardFail, cardAverage;

    public ReportsPanel(MainFrame main) {
        super(main);
        buildUI();
        refreshData();
    }

    private void buildUI() {
        SectionHeader head = header("Module 08 \u00B7 Analysis", "Reports",
                "Six views of the register, computed live from the database.");
        add(head, BorderLayout.NORTH);

        JPanel body = pageBody();

        SideTabs tabs = new SideTabs(REPORT_NAMES);
        DataCard nav = card("Report library", "Choose a view to inspect.");
        nav.setBody(tabs);

        JPanel navWrap = new JPanel(new BorderLayout());
        navWrap.setOpaque(false);
        navWrap.setPreferredSize(new Dimension(252, 0));
        navWrap.add(nav, BorderLayout.NORTH);

        stage.setOpaque(false);
        stage.add(buildStudentDirectoryPane(), "0");
        stage.add(buildStudentsInCoursePane(), "1");
        stage.add(buildCoursesByFacultyPane(), "2");
        stage.add(buildCourseAttendancePane(), "3");
        stage.add(buildCourseResultsPane(), "4");
        stage.add(buildOverallPane(), "5");

        tabs.addSelectionListener(() -> stageLayout.show(stage, String.valueOf(tabs.getSelectedIndex())));

        body.add(navWrap, BorderLayout.WEST);
        body.add(stage, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    /* ---------------------------------------------------------------
       Panes
       --------------------------------------------------------------- */

    private JPanel pane() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        return p;
    }

    private JPanel buildStudentDirectoryPane() {
        dirTable = buildTable(new Object[0][0],
                new String[]{"Student ID", "Name", "Email", "Department", "Sem."});
        dirCard = tableCard("Student directory", "Everyone currently on the register.",
                dirTable, "No students on file",
                "The directory will fill as soon as students are added.");
        RoundedButton refresh = new RoundedButton("Refresh", AppIcon.Glyph.REFRESH);
        refresh.applyOutline();
        refresh.addActionListener(e -> loadStudentDirectory());
        dirCard.getCard().addHeaderAction(refresh);

        JPanel pane = pane();
        pane.add(dirCard, BorderLayout.CENTER);
        return pane;
    }

    private JPanel buildStudentsInCoursePane() {
        studentsInCourseTable = buildTable(new Object[0][0],
                new String[]{"Student ID", "Name", "Status", "Enrolled"});
        studentsCard = tableCard("Students in a course", "Enrollments for the chosen course.",
                studentsInCourseTable, "No enrollments for this course",
                "Pick another course, or enroll students from the Enrollments module.");

        cboStudentsCourse = new AppComboBox<>();
        cboStudentsCourse.setPreferredSize(new Dimension(250, 34));
        RoundedButton load = new RoundedButton("Load", AppIcon.Glyph.ARROW_RIGHT);
        load.applyOutline();
        load.addActionListener(e -> loadStudentsInCourse());
        studentsCard.getCard().addHeaderAction(cboStudentsCourse);
        studentsCard.getCard().addHeaderAction(load);

        JPanel pane = pane();
        pane.add(studentsCard, BorderLayout.CENTER);
        return pane;
    }

    private JPanel buildCoursesByFacultyPane() {
        coursesByFacultyTable = buildTable(new Object[0][0],
                new String[]{"Course Code", "Course Name", "Department", "Credits"});
        facultyCard = tableCard("Courses by faculty", "Assignments for the chosen faculty member.",
                coursesByFacultyTable, "No courses assigned",
                "Assign courses to this faculty member from the Courses module.");

        cboFaculty = new AppComboBox<>();
        cboFaculty.setPreferredSize(new Dimension(240, 34));
        RoundedButton load = new RoundedButton("Load", AppIcon.Glyph.ARROW_RIGHT);
        load.applyOutline();
        load.addActionListener(e -> loadCoursesByFaculty());
        facultyCard.getCard().addHeaderAction(cboFaculty);
        facultyCard.getCard().addHeaderAction(load);

        JPanel pane = pane();
        pane.add(facultyCard, BorderLayout.CENTER);
        return pane;
    }

    private JPanel buildCourseAttendancePane() {
        attendanceSummaryTable = buildTable(new Object[0][0],
                new String[]{"Student ID", "Name", "Present", "Late", "Absent", "Total", "Percentage"});
        attendanceCard = tableCard("Course attendance", "Present, late and absent totals per student.",
                attendanceSummaryTable, "No attendance for this course",
                "Mark a class from the Attendance module, then return to this report.");

        cboAttendanceCourse = new AppComboBox<>();
        cboAttendanceCourse.setPreferredSize(new Dimension(250, 34));
        RoundedButton load = new RoundedButton("Load", AppIcon.Glyph.ARROW_RIGHT);
        load.applyOutline();
        load.addActionListener(e -> loadCourseAttendance());
        attendanceCard.getCard().addHeaderAction(cboAttendanceCourse);
        attendanceCard.getCard().addHeaderAction(load);

        JPanel pane = pane();
        pane.add(attendanceCard, BorderLayout.CENTER);
        return pane;
    }

    private JPanel buildCourseResultsPane() {
        courseResultsTable = buildTable(new Object[0][0],
                new String[]{"Student ID", "Name", "Marks", "Max",
                    "Pass", "Percent", "Grade"});
        resultsCard = tableCard("Course results", "Marks, percentages and grades for the chosen course.",
                courseResultsTable, "No results for this course",
                "Record results from the Results module, then return to this report.");

        cboResultsCourse = new AppComboBox<>();
        cboResultsCourse.setPreferredSize(new Dimension(250, 34));
        RoundedButton load = new RoundedButton("Load", AppIcon.Glyph.ARROW_RIGHT);
        load.applyOutline();
        load.addActionListener(e -> loadCourseResults());
        resultsCard.getCard().addHeaderAction(cboResultsCourse);
        resultsCard.getCard().addHeaderAction(load);

        JPanel pane = pane();
        pane.add(resultsCard, BorderLayout.CENTER);
        return pane;
    }

    private JPanel buildOverallPane() {
        cardTotal = new StatCard("Results", "0", UIStyles.PRIMARY,
                new AppIcon(AppIcon.Glyph.RESULTS, 17, UIStyles.PRIMARY));
        cardTotal.setCaption("recorded in total");

        cardPass = new StatCard("Passed", "0", UIStyles.SUCCESS,
                new AppIcon(AppIcon.Glyph.CHECK, 17, UIStyles.SUCCESS));
        cardPass.setCaption("grades A\u2013D");

        cardFail = new StatCard("Failed", "0", UIStyles.DANGER,
                new AppIcon(AppIcon.Glyph.WARNING, 17, UIStyles.DANGER));
        cardFail.setCaption("grade F");

        cardAverage = new StatCard("Average", "0.00%", UIStyles.INFO,
                new AppIcon(AppIcon.Glyph.SPARK, 17, UIStyles.INFO));
        cardAverage.setCaption("mean percentage");
        cardAverage.setProgress(0);

        JPanel stats = new JPanel(new GridLayout(1, 4, 14, 0));
        stats.setOpaque(false);
        stats.setPreferredSize(new Dimension(0, 126));
        stats.add(cardTotal);
        stats.add(cardPass);
        stats.add(cardFail);
        stats.add(cardAverage);

        allResultsTable = buildTable(new Object[0][0],
                new String[]{"Student ID", "Student", "Course",
                    "Marks", "Percentage", "Grade"});
        allResultsCard = tableCard("All recorded results", "Every result in the institution.",
                allResultsTable, "No results recorded",
                "Results entered in the Results module will appear here.");
        RoundedButton refresh = new RoundedButton("Refresh", AppIcon.Glyph.REFRESH);
        refresh.applyOutline();
        refresh.addActionListener(e -> loadOverallPerformance());
        allResultsCard.getCard().addHeaderAction(refresh);

        JPanel pane = pane();
        pane.add(stats, BorderLayout.NORTH);
        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setOpaque(false);
        tableWrap.setBorder(new EmptyBorder(16, 0, 0, 0));
        tableWrap.add(allResultsCard, BorderLayout.CENTER);
        pane.add(tableWrap, BorderLayout.CENTER);
        return pane;
    }

    /* ---------------------------------------------------------------
       Loaders
       --------------------------------------------------------------- */

    private void loadStudentDirectory() {
        new SwingWorker<List<Map<String, Object>>, Void>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return report.studentDirectory();
            }

            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> rows = get();
                    Object[][] data = new Object[rows.size()][5];
                    for (int i = 0; i < rows.size(); i++) {
                        Map<String, Object> r = rows.get(i);
                        data[i] = new Object[]{
                            r.get("studentId"), r.get("fullName"),
                            r.get("email"), r.get("department"), r.get("semester")
                        };
                    }
                    replaceTableData(dirTable, data,
                            new String[]{"Student ID", "Name", "Email", "Department", "Sem."});
                    setColumnWidths(dirTable, 90, 112, 195, 150, 66);
                    dirCard.setCount(rows.size(), "student");
                    dirCard.sync();
                } catch (Exception ex) {
                    showError("Report failed: " + message(ex));
                }
            }
        }.execute();
    }

    private void loadStudentsInCourse() {
        EnrollmentPanel.ComboItem item = (EnrollmentPanel.ComboItem) cboStudentsCourse.getSelectedItem();
        if (item == null) {
            showWarning("Select a course first.");
            return;
        }
        new SwingWorker<List<Map<String, Object>>, Void>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return report.studentsInCourse(item.id);
            }

            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> rows = get();
                    Object[][] data = new Object[rows.size()][4];
                    for (int i = 0; i < rows.size(); i++) {
                        Map<String, Object> r = rows.get(i);
                        data[i] = new Object[]{
                            r.get("studentCode"), r.get("studentName"),
                            r.get("status"), r.get("enrollmentDate")
                        };
                    }
                    replaceTableData(studentsInCourseTable, data,
                            new String[]{"Student ID", "Name", "Status", "Enrolled"});
                    setColumnWidths(studentsInCourseTable, 95, 185, 85, 95);
                    studentsCard.setCount(rows.size(), "enrollment");
                    studentsCard.setSubtitle(item.label);
                    studentsCard.sync();
                } catch (Exception ex) {
                    showError("Report failed: " + message(ex));
                }
            }
        }.execute();
    }

    private void loadCoursesByFaculty() {
        EnrollmentPanel.ComboItem item = (EnrollmentPanel.ComboItem) cboFaculty.getSelectedItem();
        if (item == null) {
            showWarning("Select a faculty member first.");
            return;
        }
        new SwingWorker<List<Map<String, Object>>, Void>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return report.coursesByFaculty(item.id);
            }

            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> rows = get();
                    Object[][] data = new Object[rows.size()][4];
                    for (int i = 0; i < rows.size(); i++) {
                        Map<String, Object> r = rows.get(i);
                        data[i] = new Object[]{
                            r.get("courseCode"), r.get("courseName"),
                            r.get("department"), r.get("credits")
                        };
                    }
                    replaceTableData(coursesByFacultyTable, data,
                            new String[]{"Course Code", "Course Name", "Department", "Credits"});
                    setColumnWidths(coursesByFacultyTable, 95, 200, 130, 65);
                    facultyCard.setCount(rows.size(), "course");
                    facultyCard.setSubtitle(item.label);
                    facultyCard.sync();
                } catch (Exception ex) {
                    showError("Report failed: " + message(ex));
                }
            }
        }.execute();
    }

    private void loadCourseAttendance() {
        EnrollmentPanel.ComboItem item = (EnrollmentPanel.ComboItem) cboAttendanceCourse.getSelectedItem();
        if (item == null) {
            showWarning("Select a course first.");
            return;
        }
        new SwingWorker<List<Map<String, Object>>, Void>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return report.courseAttendanceSummary(item.id);
            }

            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> rows = get();
                    Object[][] data = new Object[rows.size()][7];
                    for (int i = 0; i < rows.size(); i++) {
                        Map<String, Object> r = rows.get(i);
                        data[i] = new Object[]{
                            r.get("studentCode"), r.get("studentName"), r.get("present"),
                            r.get("late"), r.get("absent"), r.get("total"),
                            r.get("percentage") + "%"
                        };
                    }
                    replaceTableData(attendanceSummaryTable, data,
                            new String[]{"Student ID", "Name", "Present", "Late", "Absent",
                                "Total", "Percentage"});
                    setColumnWidths(attendanceSummaryTable, 90, 135, 65, 52, 62, 55, 82);
                    attendanceCard.setCount(rows.size(), "student");
                    attendanceCard.setSubtitle(item.label);
                    attendanceCard.sync();
                } catch (Exception ex) {
                    showError("Report failed: " + message(ex));
                }
            }
        }.execute();
    }

    private void loadCourseResults() {
        EnrollmentPanel.ComboItem item = (EnrollmentPanel.ComboItem) cboResultsCourse.getSelectedItem();
        if (item == null) {
            showWarning("Select a course first.");
            return;
        }
        new SwingWorker<List<Map<String, Object>>, Void>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return report.courseResults(item.id);
            }

            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> rows = get();
                    Object[][] data = new Object[rows.size()][7];
                    for (int i = 0; i < rows.size(); i++) {
                        Map<String, Object> r = rows.get(i);
                        BigDecimal pct = (BigDecimal) r.get("percentage");
                        data[i] = new Object[]{
                            r.get("studentCode"), r.get("studentName"), r.get("marksObtained"),
                            r.get("maximumMarks"), r.get("passingMarks"),
                            pct == null ? "\u2014" : pct + "%", r.get("grade")
                        };
                    }
                    replaceTableData(courseResultsTable, data,
                            new String[]{"Student ID", "Name", "Marks", "Max",
                                "Pass", "Percent", "Grade"});
                    setColumnWidths(courseResultsTable, 95, 180, 65, 55, 55, 80, 55);
                    resultsCard.setCount(rows.size(), "result");
                    resultsCard.setSubtitle(item.label);
                    resultsCard.sync();
                } catch (Exception ex) {
                    showError("Report failed: " + message(ex));
                }
            }
        }.execute();
    }

    private void loadOverallPerformance() {
        new SwingWorker<PerfStats, Void>() {
            @Override
            protected PerfStats doInBackground() throws Exception {
                PerfStats s = new PerfStats();
                List<Map<String, Object>> rows = report.allResults();
                s.total = rows.size();
                int pass = 0;
                double sum = 0;
                int withPercentage = 0;

                Object[][] data = new Object[rows.size()][6];
                for (int i = 0; i < rows.size(); i++) {
                    Map<String, Object> r = rows.get(i);
                    BigDecimal pct = (BigDecimal) r.get("percentage");
                    data[i] = new Object[]{
                        r.get("studentCode"), r.get("studentName"),
                        r.get("courseName"), r.get("marksObtained"),
                        pct == null ? "\u2014" : pct + "%", r.get("grade")
                    };
                    if (pct != null) {
                        sum += pct.doubleValue();
                        withPercentage++;
                        String grade = (String) r.get("grade");
                        if ("A".equals(grade) || "B".equals(grade)
                                || "C".equals(grade) || "D".equals(grade)) {
                            pass++;
                        }
                    }
                }
                s.pass = pass;
                s.fail = s.total - pass;
                s.average = withPercentage == 0 ? 0.0 : sum / withPercentage;
                s.data = data;
                return s;
            }

            @Override
            protected void done() {
                try {
                    PerfStats s = get();
                    cardTotal.setValue(String.valueOf(s.total));
                    cardPass.setValue(String.valueOf(s.pass));
                    cardFail.setValue(String.valueOf(s.fail));
                    cardAverage.setValue(String.format("%.2f%%", s.average));
                    cardAverage.setProgress(s.average);

                    replaceTableData(allResultsTable, s.data,
                            new String[]{"Student ID", "Student", "Course",
                                "Marks", "Percentage", "Grade"});
                    setColumnWidths(allResultsTable, 88, 120, 160, 60, 85, 55);
                    allResultsCard.setCount(s.total, "result");
                    allResultsCard.sync();
                } catch (Exception ex) {
                    showError("Report failed: " + message(ex));
                }
            }
        }.execute();
    }

    /* ---------------------------------------------------------------
       Combo loaders
       --------------------------------------------------------------- */

    private void loadCourses(AppComboBox<EnrollmentPanel.ComboItem> combo, Runnable afterLoad) {
        new SwingWorker<List<EnrollmentPanel.ComboItem>, Void>() {
            @Override
            protected List<EnrollmentPanel.ComboItem> doInBackground() throws Exception {
                List<EnrollmentPanel.ComboItem> list = new ArrayList<>();
                for (Course c : new CourseDAO().findAll()) {
                    list.add(new EnrollmentPanel.ComboItem(c.getId(),
                            c.getCourseCode() + " \u00B7 " + c.getCourseName()));
                }
                return list;
            }

            @Override
            protected void done() {
                try {
                    List<EnrollmentPanel.ComboItem> list = get();
                    EnrollmentPanel.ComboItem previous =
                            (EnrollmentPanel.ComboItem) combo.getSelectedItem();
                    combo.setModel(new javax.swing.DefaultComboBoxModel<>(
                            list.toArray(new EnrollmentPanel.ComboItem[0])));
                    boolean restored = false;
                    if (previous != null) {
                        for (int i = 0; i < combo.getItemCount(); i++) {
                            if (combo.getItemAt(i).id == previous.id) {
                                combo.setSelectedIndex(i);
                                restored = true;
                                break;
                            }
                        }
                    }
                    if (afterLoad != null && (restored || !list.isEmpty())) {
                        afterLoad.run();
                    }
                } catch (Exception ex) {
                    showError("Failed to load courses: " + message(ex));
                }
            }
        }.execute();
    }

    private void loadFaculty(AppComboBox<EnrollmentPanel.ComboItem> combo) {
        new SwingWorker<List<EnrollmentPanel.ComboItem>, Void>() {
            @Override
            protected List<EnrollmentPanel.ComboItem> doInBackground() throws Exception {
                List<EnrollmentPanel.ComboItem> list = new ArrayList<>();
                for (Faculty f : new FacultyDAO().findAll()) {
                    list.add(new EnrollmentPanel.ComboItem(f.getId(),
                            f.getFacultyId() + " \u00B7 " + f.getFullName()));
                }
                return list;
            }

            @Override
            protected void done() {
                try {
                    List<EnrollmentPanel.ComboItem> list = get();
                    EnrollmentPanel.ComboItem previous =
                            (EnrollmentPanel.ComboItem) combo.getSelectedItem();
                    combo.setModel(new javax.swing.DefaultComboBoxModel<>(
                            list.toArray(new EnrollmentPanel.ComboItem[0])));
                    if (previous != null) {
                        for (int i = 0; i < combo.getItemCount(); i++) {
                            if (combo.getItemAt(i).id == previous.id) {
                                combo.setSelectedIndex(i);
                                break;
                            }
                        }
                    }
                } catch (Exception ex) {
                    showError("Failed to load faculty: " + message(ex));
                }
            }
        }.execute();
    }

    @Override
    public void refreshData() {
        loadStudentDirectory();
        loadCourses(cboStudentsCourse, this::loadStudentsInCourse);
        loadCourses(cboAttendanceCourse, this::loadCourseAttendance);
        loadCourses(cboResultsCourse, this::loadCourseResults);
        loadFaculty(cboFaculty);
        loadOverallPerformance();
    }

    private String message(Exception ex) {
        return ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
    }

    private static final class PerfStats {

        int total;
        int pass;
        int fail;
        double average;
        Object[][] data;
    }
}
