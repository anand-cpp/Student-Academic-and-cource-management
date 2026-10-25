package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import dao.CourseDAO;
import dao.FacultyDAO;
import model.Course;
import model.Faculty;
import service.ReportService;
import util.AppException;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import ui.components.SectionHeader;
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

    private javax.swing.JComboBox<EnrollmentPanel.ComboItem> cboCourses;
    private javax.swing.JComboBox<EnrollmentPanel.ComboItem> cboFaculty;
    private JTable dirTable, studentsInCourseTable, coursesByFacultyTable,
            attendanceSummaryTable, courseResultsTable;
    private ui.components.RoundedPanel perfTotalBox, perfPassBox, perfFailBox, perfAvgBox;
    private javax.swing.JLabel perfTotalVal, perfPassVal, perfFailVal, perfAvgVal;

    public ReportsPanel(MainFrame main) {
        super(main);
        buildUI();
        refreshData();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));

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
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        RoundedButton refresh = new RoundedButton("Refresh");
        refresh.applyPrimary();
        refresh.addActionListener(e -> loadStudentDirectory());

        JPanel top = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));
        top.setOpaque(false);
        top.add(refresh);
        panel.add(top, BorderLayout.NORTH);

        dirTable = new JTable();
        JScrollPane scroll = new JScrollPane(dirTable);
        scroll.getViewport().setBackground(UIStyles.CARD);
        styleTable(dirTable);
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
                    styleTable(dirTable);
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
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        RoundedPanel top = new RoundedPanel();
        top.setRadius(8);
        top.setShadow(false);
        top.setFill(UIStyles.CARD_HOVER);
        top.setLayout(new GridBagLayout());
        top.setBorder(new EmptyBorder(12, 12, 12, 12));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        cboCourses = new javax.swing.JComboBox<>();
        javax.swing.JLabel lbl = new javax.swing.JLabel("Select course:");
        lbl.setFont(UIStyles.BODY);
        lbl.setForeground(UIStyles.INK);
        top.add(lbl, g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        top.add(cboCourses, g);
        g.gridx = 2;
        g.fill = GridBagConstraints.NONE;
        g.weightx = 0;
        RoundedButton load = new RoundedButton("Load");
        load.applyPrimary();
        load.addActionListener(e -> loadStudentsInCourse());
        top.add(load, g);

        panel.add(top, BorderLayout.NORTH);

        studentsInCourseTable = new JTable();
        JScrollPane scroll = new JScrollPane(studentsInCourseTable);
        scroll.getViewport().setBackground(UIStyles.CARD);
        styleTable(studentsInCourseTable);
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
                    styleTable(studentsInCourseTable);
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
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        RoundedPanel top = new RoundedPanel();
        top.setRadius(8);
        top.setShadow(false);
        top.setFill(UIStyles.CARD_HOVER);
        top.setLayout(new GridBagLayout());
        top.setBorder(new EmptyBorder(12, 12, 12, 12));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        cboFaculty = new javax.swing.JComboBox<>();
        javax.swing.JLabel lbl = new javax.swing.JLabel("Select faculty:");
        lbl.setFont(UIStyles.BODY);
        lbl.setForeground(UIStyles.INK);
        top.add(lbl, g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        top.add(cboFaculty, g);
        g.gridx = 2;
        g.fill = GridBagConstraints.NONE;
        g.weightx = 0;
        RoundedButton load = new RoundedButton("Load");
        load.applyPrimary();
        load.addActionListener(e -> loadCoursesByFaculty());
        top.add(load, g);

        panel.add(top, BorderLayout.NORTH);

        coursesByFacultyTable = new JTable();
        JScrollPane scroll = new JScrollPane(coursesByFacultyTable);
        scroll.getViewport().setBackground(UIStyles.CARD);
        styleTable(coursesByFacultyTable);
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
                    styleTable(coursesByFacultyTable);
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
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        RoundedPanel top = new RoundedPanel();
        top.setRadius(8);
        top.setShadow(false);
        top.setFill(UIStyles.CARD_HOVER);
        top.setLayout(new GridBagLayout());
        top.setBorder(new EmptyBorder(12, 12, 12, 12));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        javax.swing.JComboBox<EnrollmentPanel.ComboItem> cbo = new javax.swing.JComboBox<>();
        javax.swing.JLabel lbl = new javax.swing.JLabel("Select course:");
        lbl.setFont(UIStyles.BODY);
        lbl.setForeground(UIStyles.INK);
        top.add(lbl, g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        top.add(cbo, g);
        g.gridx = 2;
        g.fill = GridBagConstraints.NONE;
        g.weightx = 0;
        RoundedButton load = new RoundedButton("Load");
        load.applyPrimary();
        load.addActionListener(e -> {
            EnrollmentPanel.ComboItem it = (EnrollmentPanel.ComboItem) cbo.getSelectedItem();
            if (it != null) {
                loadCourseAttendance(it.id);
            }
        });
        top.add(load, g);

        panel.add(top, BorderLayout.NORTH);

        attendanceSummaryTable = new JTable();
        JScrollPane scroll = new JScrollPane(attendanceSummaryTable);
        scroll.getViewport().setBackground(UIStyles.CARD);
        styleTable(attendanceSummaryTable);
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
                    styleTable(attendanceSummaryTable);
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
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        RoundedPanel top = new RoundedPanel();
        top.setRadius(8);
        top.setShadow(false);
        top.setFill(UIStyles.CARD_HOVER);
        top.setLayout(new GridBagLayout());
        top.setBorder(new EmptyBorder(12, 12, 12, 12));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        javax.swing.JComboBox<EnrollmentPanel.ComboItem> cbo = new javax.swing.JComboBox<>();
        javax.swing.JLabel lbl = new javax.swing.JLabel("Select course:");
        lbl.setFont(UIStyles.BODY);
        lbl.setForeground(UIStyles.INK);
        top.add(lbl, g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        top.add(cbo, g);
        g.gridx = 2;
        g.fill = GridBagConstraints.NONE;
        g.weightx = 0;
        RoundedButton load = new RoundedButton("Load");
        load.applyPrimary();
        load.addActionListener(e -> {
            EnrollmentPanel.ComboItem it = (EnrollmentPanel.ComboItem) cbo.getSelectedItem();
            if (it != null) {
                loadCourseResults(it.id);
            }
        });
        top.add(load, g);

        panel.add(top, BorderLayout.NORTH);

        courseResultsTable = new JTable();
        JScrollPane scroll = new JScrollPane(courseResultsTable);
        scroll.getViewport().setBackground(UIStyles.CARD);
        styleTable(courseResultsTable);
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
                    styleTable(courseResultsTable);
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
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        RoundedPanel stats = new RoundedPanel();
        stats.setRadius(12);
        stats.setShadow(false);
        stats.setFill(UIStyles.CARD);
        stats.setLayout(new java.awt.GridLayout(1, 4, 12, 0));
        stats.setBorder(new EmptyBorder(16, 16, 16, 16));

        perfTotalBox = statBox("Total Results", "0", UIStyles.PRIMARY);
        perfPassBox = statBox("Passed", "0", UIStyles.SUCCESS);
        perfFailBox = statBox("Failed", "0", UIStyles.DANGER);
        perfAvgBox = statBox("Average %", "0.00", UIStyles.INFO);
        perfTotalVal = findValueLabel(perfTotalBox);
        perfPassVal = findValueLabel(perfPassBox);
        perfFailVal = findValueLabel(perfFailBox);
        perfAvgVal = findValueLabel(perfAvgBox);
        stats.add(perfTotalBox);
        stats.add(perfPassBox);
        stats.add(perfFailBox);
        stats.add(perfAvgBox);
        panel.add(stats, BorderLayout.NORTH);

        RoundedButton refresh = new RoundedButton("Refresh");
        refresh.applyPrimary();
        refresh.addActionListener(e -> loadOverallPerformance());

        JPanel top = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));
        top.setOpaque(false);
        top.add(refresh);
        panel.add(top, BorderLayout.SOUTH);

        loadOverallPerformance();
        return panel;
    }

    private ui.components.RoundedPanel statBox(String label, String value, Color c) {
        ui.components.RoundedPanel box = new ui.components.RoundedPanel();
        box.setRadius(8);
        box.setShadow(false);
        box.setFill(UIStyles.CARD);
        box.setLayout(new BorderLayout(8, 6));
        box.setBorder(new EmptyBorder(14, 14, 14, 14));

        javax.swing.JLabel val = new javax.swing.JLabel(value);
        val.setFont(UIStyles.CARD_VALUE);
        val.setForeground(c);

        javax.swing.JLabel lab = new javax.swing.JLabel(label);
        lab.setFont(UIStyles.CARD_LABEL);
        lab.setForeground(UIStyles.MUTED);

        box.add(val, BorderLayout.CENTER);
        box.add(lab, BorderLayout.SOUTH);
        return box;
    }

    private void setStat(javax.swing.JLabel label, String value) {
        label.setText(value);
    }

    private javax.swing.JLabel findValueLabel(javax.swing.JPanel panel) {
        java.awt.Component[] comps = panel.getComponents();
        for (java.awt.Component c : comps) {
            if (c instanceof javax.swing.JLabel) {
                javax.swing.JLabel l = (javax.swing.JLabel) c;
                if (l.getFont().getSize() > 20) {
                    return l;
                }
            }
        }
        return null;
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
                    setStat(perfTotalVal, String.valueOf(s.total));
                    setStat(perfPassVal, String.valueOf(s.pass));
                    setStat(perfFailVal, String.valueOf(s.fail));
                    setStat(perfAvgVal, String.format("%.2f", s.avg));
                } catch (Exception ex) {
                    showError("Report failed: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    /* ---------- Combo loaders ---------- */

    private void loadCourses(javax.swing.JComboBox<EnrollmentPanel.ComboItem> cbo) {
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
                    cbo.setModel(new javax.swing.DefaultComboBoxModel<>(
                            list.toArray(new EnrollmentPanel.ComboItem[0])));
                } catch (Exception ex) {
                    showError("Failed to load courses: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void loadFaculty(javax.swing.JComboBox<EnrollmentPanel.ComboItem> cbo) {
        SwingWorker<List<EnrollmentPanel.ComboItem>, Void> worker =
                new SwingWorker<List<EnrollmentPanel.ComboItem>, Void>() {
            @Override
            protected List<EnrollmentPanel.ComboItem> doInBackground() throws Exception {
                List<EnrollmentPanel.ComboItem> list = new ArrayList<>();
                FacultyDAO dao = new FacultyDAO();
                for (Faculty f : dao.findAll()) {
                    list.add(new EnrollmentPanel.ComboItem(f.getId(),
                            f.getFacultyId() + " - " + f.getFullName()));
                }
                return list;
            }

            @Override
            protected void done() {
                try {
                    List<EnrollmentPanel.ComboItem> list = get();
                    cbo.setModel(new javax.swing.DefaultComboBoxModel<>(
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

    protected void styleTable(JTable table) {
        table.setRowHeight(UIStyles.TABLE_ROW_HEIGHT);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(UIStyles.SELECTION);
        table.setSelectionForeground(UIStyles.INK);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(UIStyles.TABLE_HEADER);
        table.getTableHeader().setBackground(UIStyles.PRIMARY);
        table.getTableHeader().setForeground(UIStyles.INVERSE);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBorder(
                javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, UIStyles.PRIMARY));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.setDefaultRenderer(Object.class, new ui.components.TableRenderer());
            }

    private static final class PerfStats {
        int total;
        int pass;
        int fail;
        double avg;
    }
}



