package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.Color;

import dao.AttendanceDAO;
import dao.CourseDAO;
import dao.EnrollmentDAO;
import dao.FacultyDAO;
import dao.StudentDAO;
import model.Course;
import model.Enrollment;
import service.ReportService;
import util.AppException;
import util.DateUtils;
import util.UIStyles;

/**
 * Reports panel. All data is retrieved from MySQL; nothing is hardcoded.
 *
 * <p>Reports:<br>
 *   1. Student directory<br>
 *   2. Students enrolled in a selected course<br>
 *   3. Courses assigned to a selected faculty member<br>
 *   4. Course-wise attendance summary<br>
 *   5. Course-wise academic results<br>
 *   6. Overall academic performance (pass/fail/average)
 */
public class ReportsPanel extends BasePanel implements MainFrame.Refreshable {

    private static final long serialVersionUID = 1L;

    private final ReportService report = new ReportService();

    private JComboBox<EnrollmentPanel.ComboItem> cboCourses;
    private JComboBox<EnrollmentPanel.ComboItem> cboFaculty;
    private JTable dirTable, studentsInCourseTable, coursesByFacultyTable,
            attendanceSummaryTable, courseResultsTable;
    private JLabel perfTotal, perfPass, perfFail, perfAvg;

    public ReportsPanel(MainFrame main) {
        super(main);
        buildUI();
        refreshData();
    }

    private void buildUI() {
        setLayout(new BorderLayout(8, 8));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Student Directory", buildStudentDirectoryTab());
        tabs.addTab("Students in Course", buildStudentsInCourseTab());
        tabs.addTab("Courses by Faculty", buildCoursesByFacultyTab());
        tabs.addTab("Course Attendance", buildCourseAttendanceTab());
        tabs.addTab("Course Results", buildCourseResultsTab());
        tabs.addTab("Overall Performance", buildOverallPerformanceTab());
        add(tabs, BorderLayout.CENTER);
    }

    /* ---------- Tab 1: Student directory ---------- */

    private JPanel buildStudentDirectoryTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyles.CARD);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        JButton refresh = button("Refresh", UIStyles.PRIMARY, e -> loadStudentDirectory());
        JPanel top = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));
        top.setBackground(UIStyles.CARD);
        top.add(refresh);
        panel.add(top, BorderLayout.NORTH);

        dirTable = new JTable();
        JScrollPane scroll = new JScrollPane(dirTable);
        scroll.setBorder(UIStyles.line());
        panel.add(scroll, BorderLayout.CENTER);
        loadStudentDirectory();
        return panel;
    }

    private void loadStudentDirectory() {
        SwingWorker<List<Map<String, Object>>, Void> worker =
                new SwingWorker<List<Map<String, Object>>, Void>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return report.studentDirectory();
            }
            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> rows = get();
                    Object[][] data = new Object[rows.size()][6];
                    for (int i = 0; i < rows.size(); i++) {
                        Map<String, Object> r = rows.get(i);
                        data[i] = new Object[] {
                            r.get("studentId"), r.get("rollNumber"), r.get("fullName"),
                            r.get("email"), r.get("department"), r.get("semester")
                        };
                    }
                    dirTable.setModel(new javax.swing.table.DefaultTableModel(
                            data, new String[] { "Student ID", "Roll No.", "Name", "Email", "Department", "Semester" }));
                    UIStyles.styleTable(dirTable);
                } catch (Exception ex) {
                    showError("Report failed: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    /* ---------- Tab 2: Students in a course ---------- */

    private JPanel buildStudentsInCourseTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyles.CARD);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new GridBagLayout());
        top.setBackground(UIStyles.CARD);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        cboCourses = new JComboBox<>();
        JLabel lbl = new JLabel("Select course:");
        lbl.setFont(UIStyles.BODY_FONT);
        top.add(lbl, g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        top.add(cboCourses, g);
        g.gridx = 2;
        g.fill = GridBagConstraints.NONE;
        g.weightx = 0;
        JButton load = button("Load", UIStyles.PRIMARY, e -> loadStudentsInCourse());
        top.add(load, g);

        panel.add(top, BorderLayout.NORTH);

        studentsInCourseTable = new JTable();
        JScrollPane scroll = new JScrollPane(studentsInCourseTable);
        scroll.setBorder(UIStyles.line());
        panel.add(scroll, BorderLayout.CENTER);

        loadCourses(cboCourses);
        return panel;
    }

    private void loadStudentsInCourse() {
        EnrollmentPanel.ComboItem item = (EnrollmentPanel.ComboItem) cboCourses.getSelectedItem();
        if (item == null) {
            showWarning("Select a course.");
            return;
        }
        SwingWorker<List<Map<String, Object>>, Void> worker =
                new SwingWorker<List<Map<String, Object>>, Void>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return report.studentsInCourse(item.id);
            }
            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> rows = get();
                    Object[][] data = new Object[rows.size()][3];
                    for (int i = 0; i < rows.size(); i++) {
                        Map<String, Object> r = rows.get(i);
                        data[i] = new Object[] {
                            r.get("enrollmentId"), r.get("studentId"), r.get("status")
                        };
                    }
                    studentsInCourseTable.setModel(new javax.swing.table.DefaultTableModel(
                            data, new String[] { "Enrollment ID", "Student ID", "Status" }));
                    UIStyles.styleTable(studentsInCourseTable);
                } catch (Exception ex) {
                    showError("Report failed: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    /* ---------- Tab 3: Courses by faculty ---------- */

    private JPanel buildCoursesByFacultyTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyles.CARD);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new GridBagLayout());
        top.setBackground(UIStyles.CARD);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        cboFaculty = new JComboBox<>();
        JLabel lbl = new JLabel("Select faculty:");
        lbl.setFont(UIStyles.BODY_FONT);
        top.add(lbl, g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        top.add(cboFaculty, g);
        g.gridx = 2;
        g.fill = GridBagConstraints.NONE;
        g.weightx = 0;
        JButton load = button("Load", UIStyles.PRIMARY, e -> loadCoursesByFaculty());
        top.add(load, g);

        panel.add(top, BorderLayout.NORTH);

        coursesByFacultyTable = new JTable();
        JScrollPane scroll = new JScrollPane(coursesByFacultyTable);
        scroll.setBorder(UIStyles.line());
        panel.add(scroll, BorderLayout.CENTER);

        loadFaculty(cboFaculty);
        return panel;
    }

    private void loadCoursesByFaculty() {
        EnrollmentPanel.ComboItem item = (EnrollmentPanel.ComboItem) cboFaculty.getSelectedItem();
        if (item == null) {
            showWarning("Select a faculty member.");
            return;
        }
        SwingWorker<List<Map<String, Object>>, Void> worker =
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
                        data[i] = new Object[] {
                            r.get("courseCode"), r.get("courseName"),
                            r.get("department"), r.get("credits")
                        };
                    }
                    coursesByFacultyTable.setModel(new javax.swing.table.DefaultTableModel(
                            data, new String[] { "Course Code", "Course Name", "Department", "Credits" }));
                    UIStyles.styleTable(coursesByFacultyTable);
                } catch (Exception ex) {
                    showError("Report failed: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    /* ---------- Tab 4: Course attendance summary ---------- */

    private JPanel buildCourseAttendanceTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyles.CARD);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new GridBagLayout());
        top.setBackground(UIStyles.CARD);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        JComboBox<EnrollmentPanel.ComboItem> cbo = new JComboBox<>();
        JLabel lbl = new JLabel("Select course:");
        lbl.setFont(UIStyles.BODY_FONT);
        top.add(lbl, g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        top.add(cbo, g);
        g.gridx = 2;
        g.fill = GridBagConstraints.NONE;
        g.weightx = 0;
        JButton load = button("Load", UIStyles.PRIMARY, e -> {
            EnrollmentPanel.ComboItem it = (EnrollmentPanel.ComboItem) cbo.getSelectedItem();
            if (it != null) {
                loadCourseAttendance(it.id);
            }
        });
        top.add(load, g);

        panel.add(top, BorderLayout.NORTH);

        attendanceSummaryTable = new JTable();
        JScrollPane scroll = new JScrollPane(attendanceSummaryTable);
        scroll.setBorder(UIStyles.line());
        panel.add(scroll, BorderLayout.CENTER);

        loadCourses(cbo);
        return panel;
    }

    private void loadCourseAttendance(int courseId) {
        SwingWorker<List<Map<String, Object>>, Void> worker =
                new SwingWorker<List<Map<String, Object>>, Void>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return report.courseAttendanceSummary(courseId);
            }
            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> rows = get();
                    Object[][] data = new Object[rows.size()][7];
                    for (int i = 0; i < rows.size(); i++) {
                        Map<String, Object> r = rows.get(i);
                        data[i] = new Object[] {
                            r.get("studentCode"), r.get("studentName"), r.get("present"),
                            r.get("late"), r.get("absent"), r.get("total"),
                            r.get("percentage") + "%"
                        };
                    }
                    attendanceSummaryTable.setModel(new javax.swing.table.DefaultTableModel(
                            data, new String[] { "Student ID", "Name", "Present", "Late", "Absent", "Total", "% Attendance" }));
                    UIStyles.styleTable(attendanceSummaryTable);
                } catch (Exception ex) {
                    showError("Report failed: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    /* ---------- Tab 5: Course results ---------- */

    private JPanel buildCourseResultsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyles.CARD);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new GridBagLayout());
        top.setBackground(UIStyles.CARD);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        JComboBox<EnrollmentPanel.ComboItem> cbo = new JComboBox<>();
        JLabel lbl = new JLabel("Select course:");
        lbl.setFont(UIStyles.BODY_FONT);
        top.add(lbl, g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        top.add(cbo, g);
        g.gridx = 2;
        g.fill = GridBagConstraints.NONE;
        g.weightx = 0;
        JButton load = button("Load", UIStyles.PRIMARY, e -> {
            EnrollmentPanel.ComboItem it = (EnrollmentPanel.ComboItem) cbo.getSelectedItem();
            if (it != null) {
                loadCourseResults(it.id);
            }
        });
        top.add(load, g);

        panel.add(top, BorderLayout.NORTH);

        courseResultsTable = new JTable();
        JScrollPane scroll = new JScrollPane(courseResultsTable);
        scroll.setBorder(UIStyles.line());
        panel.add(scroll, BorderLayout.CENTER);

        loadCourses(cbo);
        return panel;
    }

    private void loadCourseResults(int courseId) {
        SwingWorker<List<Map<String, Object>>, Void> worker =
                new SwingWorker<List<Map<String, Object>>, Void>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return report.courseResults(courseId);
            }
            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> rows = get();
                    Object[][] data = new Object[rows.size()][7];
                    for (int i = 0; i < rows.size(); i++) {
                        Map<String, Object> r = rows.get(i);
                        BigDecimal pct = (BigDecimal) r.get("percentage");
                        BigDecimal marks = (BigDecimal) r.get("marksObtained");
                        BigDecimal max = (BigDecimal) r.get("maximumMarks");
                        BigDecimal pass = (BigDecimal) r.get("passingMarks");
                        data[i] = new Object[] {
                            r.get("studentCode"), r.get("studentName"), marks,
                            max, pass, pct == null ? "-" : pct + "%", r.get("grade")
                        };
                    }
                    courseResultsTable.setModel(new javax.swing.table.DefaultTableModel(
                            data, new String[] { "Student ID", "Name", "Marks Obtained",
                                "Max Marks", "Pass Marks", "Percentage", "Grade" }));
                    UIStyles.styleTable(courseResultsTable);
                } catch (Exception ex) {
                    showError("Report failed: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    /* ---------- Tab 6: Overall performance ---------- */

    private JPanel buildOverallPerformanceTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyles.CARD);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel stats = new JPanel(new java.awt.GridLayout(1, 4, 12, 0));
        stats.setBackground(UIStyles.CARD);
        perfTotal = valueLabel("0", UIStyles.PRIMARY);
        perfPass = valueLabel("0", UIStyles.SUCCESS);
        perfFail = valueLabel("0", UIStyles.DANGER);
        perfAvg = valueLabel("0.00", UIStyles.INFO);
        stats.add(statBox(perfTotal, "Total Results", UIStyles.PRIMARY));
        stats.add(statBox(perfPass, "Passed", UIStyles.SUCCESS));
        stats.add(statBox(perfFail, "Failed", UIStyles.DANGER));
        stats.add(statBox(perfAvg, "Average %", UIStyles.INFO));
        panel.add(stats, BorderLayout.NORTH);

        JButton refresh = button("Refresh", UIStyles.PRIMARY, e -> loadOverallPerformance());
        JPanel top = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));
        top.setBackground(UIStyles.CARD);
        top.add(refresh);
        panel.add(top, BorderLayout.SOUTH);

        loadOverallPerformance();
        return panel;
    }

    private JLabel valueLabel(String text, Color c) {
        JLabel val = new JLabel(text);
        val.setFont(UIStyles.CARD_VALUE_FONT);
        val.setForeground(c);
        return val;
    }

    private JPanel statBox(JLabel value, String label, Color c) {
        JPanel box = new JPanel(new java.awt.BorderLayout(8, 6));
        box.setBackground(UIStyles.CARD);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.LINE, 1),
                new EmptyBorder(14, 14, 14, 14)));
        value.setFont(UIStyles.CARD_VALUE_FONT);
        value.setForeground(c);
        JLabel lab = new JLabel(label);
        lab.setFont(UIStyles.CARD_LABEL_FONT);
        lab.setForeground(UIStyles.MUTED);
        box.add(value, BorderLayout.CENTER);
        box.add(lab, BorderLayout.SOUTH);
        return box;
    }

    private void loadOverallPerformance() {
        SwingWorker<PerfStats, Void> worker = new SwingWorker<PerfStats, Void>() {
            @Override
            protected PerfStats doInBackground() throws Exception {
                PerfStats s = new PerfStats();
                List<Map<String, Object>> rows = report.allResults();
                s.total = rows.size();
                int pass = 0;
                double sum = 0;
                int withGrade = 0;
                for (Map<String, Object> r : rows) {
                    BigDecimal pct = (BigDecimal) r.get("percentage");
                    if (pct != null) {
                        sum += pct.doubleValue();
                        withGrade++;
                        String grade = (String) r.get("grade");
                        if ("A".equals(grade) || "B".equals(grade) || "C".equals(grade)
                                || "D".equals(grade)) {
                            pass++;
                        }
                    }
                }
                s.pass = pass;
                s.fail = s.total - pass;
                s.avg = (withGrade == 0) ? 0.0 : sum / withGrade;
                return s;
            }
            @Override
            protected void done() {
                try {
                    PerfStats s = get();
                    setStat(perfTotal, String.valueOf(s.total));
                    setStat(perfPass, String.valueOf(s.pass));
                    setStat(perfFail, String.valueOf(s.fail));
                    setStat(perfAvg, String.format("%.2f", s.avg));
                } catch (Exception ex) {
                    showError("Report failed: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void setStat(JLabel label, String value) {
        label.setText(value);
    }

    /* ---------- Combo loaders ---------- */

    private void loadCourses(JComboBox<EnrollmentPanel.ComboItem> cbo) {
        SwingWorker<List<EnrollmentPanel.ComboItem>, Void> worker =
                new SwingWorker<List<EnrollmentPanel.ComboItem>, Void>() {
            @Override
            protected List<EnrollmentPanel.ComboItem> doInBackground() throws Exception {
                List<EnrollmentPanel.ComboItem> list = new ArrayList<>();
                CourseDAO dao = new CourseDAO();
                for (Course c : dao.findAll()) {
                    list.add(new EnrollmentPanel.ComboItem(c.getId(),
                            c.getCourseCode() + " - " + c.getCourseName()));
                }
                return list;
            }
            @Override
            protected void done() {
                try {
                    List<EnrollmentPanel.ComboItem> list = get();
                    cbo.setModel(new DefaultComboBoxModel<>(
                            list.toArray(new EnrollmentPanel.ComboItem[0])));
                } catch (Exception ex) {
                    showError("Failed to load courses: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void loadFaculty(JComboBox<EnrollmentPanel.ComboItem> cbo) {
        SwingWorker<List<EnrollmentPanel.ComboItem>, Void> worker =
                new SwingWorker<List<EnrollmentPanel.ComboItem>, Void>() {
            @Override
            protected List<EnrollmentPanel.ComboItem> doInBackground() throws Exception {
                List<EnrollmentPanel.ComboItem> list = new ArrayList<>();
                FacultyDAO dao = new FacultyDAO();
                for (model.Faculty f : dao.findAll()) {
                    list.add(new EnrollmentPanel.ComboItem(f.getId(),
                            f.getFacultyId() + " - " + f.getFullName()));
                }
                return list;
            }
            @Override
            protected void done() {
                try {
                    List<EnrollmentPanel.ComboItem> list = get();
                    cbo.setModel(new DefaultComboBoxModel<>(
                            list.toArray(new EnrollmentPanel.ComboItem[0])));
                } catch (Exception ex) {
                    showError("Failed to load faculty: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    @Override
    public void refreshData() {
        loadStudentDirectory();
        loadCourses(cboCourses);
        loadFaculty(cboFaculty);
        loadOverallPerformance();
    }

    private static final class PerfStats {
        int total;
        int pass;
        int fail;
        double avg;
    }
}

