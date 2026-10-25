package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import dao.CourseDAO;
import model.Enrollment;
import service.EnrollmentService;
import util.AppException;
import util.DateUtils;
import ui.components.FormField;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import ui.components.SectionHeader;
import ui.components.StatusBadge;
import util.UIStyles;

/**
 * Enrollment management panel: enroll students in courses, view and remove enrollments.
 */
public class EnrollmentPanel extends BasePanel implements MainFrame.Refreshable {

    private static final long serialVersionUID = 1L;

    private final EnrollmentService service = new EnrollmentService();

    private javax.swing.JComboBox<ComboItem> cboStudents;
    private javax.swing.JComboBox<ComboItem> cboCourses;
    private JTextField fEnrollmentDate;

    private JTable table;

    public EnrollmentPanel(MainFrame main) {
        super(main);
        buildUI();
        refreshData();
    }

    private void buildUI() {
        JPanel header = sectionHeader("Enrollments",
                "Enroll students in courses and manage enrollment status.");

        RoundedPanel form = new RoundedPanel();
        form.setRadius(12);
        form.setShadow(false);
        form.setFill(UIStyles.CARD);
        form.setLayout(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0;
        g.gridy = 0;

        cboStudents = new javax.swing.JComboBox<>();
        cboCourses = new javax.swing.JComboBox<>();
        fEnrollmentDate = new JTextField();
        fEnrollmentDate.setFont(UIStyles.BODY);
        fEnrollmentDate.setBorder(new javax.swing.border.LineBorder(UIStyles.LINE, 1, true));
        fEnrollmentDate.setToolTipText("yyyy-MM-dd");

        JLabel lblStud = UIStyles.fieldLabel("Student *");
        JLabel lblCrs = UIStyles.fieldLabel("Course *");
        JLabel lblDate = UIStyles.fieldLabel("Enrollment Date *");

        addGrid(form, g, lblStud, cboStudents);
        addGrid(form, g, lblCrs, cboCourses);
        addGrid(form, g, lblDate, fEnrollmentDate);

        RoundedButton btnLoad = new RoundedButton("Load Data");
        btnLoad.applyPrimary();
        btnLoad.addActionListener(e -> loadCombos());

        RoundedButton btnEnroll = new RoundedButton("Enroll");
        btnEnroll.applySuccess();
        btnEnroll.addActionListener(e -> doEnroll());

        RoundedButton btnRefresh = new RoundedButton("Refresh");
        btnRefresh.applyGhost();
        btnRefresh.addActionListener(e -> refreshData());

        RoundedButton btnRemove = new RoundedButton("Remove Selected");
        btnRemove.applyDanger();
        btnRemove.addActionListener(e -> doRemove());

        JPanel btns = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 6));
        btns.setOpaque(false);
        btns.add(btnLoad);
        btns.add(btnEnroll);
        btns.add(btnRefresh);
        btns.add(btnRemove);

        g.gridx = 0;
        g.gridy++;
        g.gridwidth = 4;
        form.add(btns, g);

        JPanel formWrap = new JPanel(new BorderLayout());
        formWrap.setOpaque(false);
        formWrap.add(form, BorderLayout.NORTH);

        table = new JTable();
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UIStyles.CARD);
        styleTable(table);

        JPanel content = new JPanel(new BorderLayout(16, 16));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(0, 20, 20, 20));
        content.add(formWrap, BorderLayout.NORTH);
        content.add(scroll, BorderLayout.CENTER);

        setLayout(new BorderLayout(0, 0));
        add(header, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }

    private void addGrid(JPanel form, GridBagConstraints g, javax.swing.JLabel label, java.awt.Component field) {
        g.gridx = 0;
        form.add(label, g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        form.add(field, g);
        g.gridx = 0;
        g.gridy++;
        g.fill = GridBagConstraints.NONE;
        g.weightx = 0;
    }

    private void loadCombos() {
        SwingWorker<ComboData, Void> worker = new SwingWorker<ComboData, Void>() {
            @Override
            protected ComboData doInBackground() throws Exception {
                ComboData data = new ComboData();
                data.students = loadStudents();
                data.courses = loadCourses();
                return data;
            }

            @Override
            protected void done() {
                try {
                    ComboData d = get();
                    cboStudents.setModel(new javax.swing.DefaultComboBoxModel<>(
                            d.students.toArray(new ComboItem[0])));
                    cboCourses.setModel(new javax.swing.DefaultComboBoxModel<>(
                            d.courses.toArray(new ComboItem[0])));
                } catch (Exception ex) {
                    showError("Failed to load students/courses: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private java.util.List<ComboItem> loadStudents() throws AppException {
        java.util.List<ComboItem> list = new java.util.ArrayList<>();
        dao.StudentDAO dao = new dao.StudentDAO();
        for (model.Student s : dao.findAll()) {
            list.add(new ComboItem(s.getId(), s.getStudentId() + " - " + s.getFullName()));
        }
        return list;
    }

    private java.util.List<ComboItem> loadCourses() throws AppException {
        java.util.List<ComboItem> list = new java.util.ArrayList<>();
        CourseDAO dao = new CourseDAO();
        for (model.Course c : dao.findAll()) {
            list.add(new ComboItem(c.getId(), c.getCourseCode() + " - " + c.getCourseName()));
        }
        return list;
    }

    private void doEnroll() {
        ComboItem student = (ComboItem) cboStudents.getSelectedItem();
        ComboItem course = (ComboItem) cboCourses.getSelectedItem();
        if (student == null || course == null) {
            showWarning("Please load data and select both a student and a course.");
            return;
        }
        String date = fEnrollmentDate.getText();
        SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
            private String error;

            @Override
            protected Integer doInBackground() throws Exception {
                try {
                    return service.enroll(student.id, course.id, date);
                } catch (AppException ex) {
                    error = ex.getMessage();
                    return -1;
                }
            }

            @Override
            protected void done() {
                try {
                    int id = get();
                    if (id > 0) {
                        showSuccess("Enrollment created (id=" + id + ").");
                        fEnrollmentDate.setText("");
                        refreshData();
                    } else {
                        showError(error);
                    }
                } catch (Exception ex) {
                    showError("Enroll failed: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void doRemove() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showWarning("Select an enrollment from the table to remove.");
            return;
        }
        int id = (Integer) table.getValueAt(row, 0);
        if (!confirm("Remove this enrollment? Attendance and results for it must be removed first.",
                "Confirm Remove")) {
            return;
        }
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            private String error;

            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    service.delete(id);
                    return true;
                } catch (AppException ex) {
                    error = ex.getMessage();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        showSuccess("Enrollment removed.");
                        refreshData();
                    } else {
                        showError(error);
                    }
                } catch (Exception ex) {
                    showError("Remove failed: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    @Override
    public void refreshData() {
        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<List<Map<String, Object>>, Void>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return service.listAllSummary();
            }

            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> rows = get();
                    Object[][] data = new Object[rows.size()][6];
                    for (int i = 0; i < rows.size(); i++) {
                        Map<String, Object> r = rows.get(i);
                        data[i] = new Object[] {
                            r.get("id"), r.get("studentId"), r.get("studentName"),
                            r.get("courseCode"), r.get("status"),
                            DateUtils.display((java.time.LocalDate) r.get("enrollmentDate"))
                        };
                    }
                    table.setModel(new javax.swing.table.DefaultTableModel(
                            data, new String[] { "ID", "Student ID", "Student", "Course", "Status", "Date" }));
                    styleTable(table);
                } catch (Exception ex) {
                    // ignore
                }
            }
        };
        worker.execute();
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

    public static final class ComboItem {
        final int id;
        final String label;

        ComboItem(int id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static final class ComboData {
        java.util.List<ComboItem> students;
        java.util.List<ComboItem> courses;
    }
}




