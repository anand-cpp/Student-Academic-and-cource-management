package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import dao.EnrollmentDAO;
import model.Enrollment;
import service.EnrollmentService;
import util.AppException;
import util.DateUtils;
import util.UIStyles;

/**
 * Enrollment management panel: enroll students in courses, view and remove enrollments.
 */
public class EnrollmentPanel extends BasePanel implements MainFrame.Refreshable {

    private static final long serialVersionUID = 1L;

    private final EnrollmentService service = new EnrollmentService();

    private JComboBox<ComboItem> cboStudents;
    private JComboBox<ComboItem> cboCourses;
    private JTextField fEnrollmentDate;

    private JTable table;

    public EnrollmentPanel(MainFrame main) {
        super(main);
        buildUI();
        refreshData();
    }

    private void buildUI() {
        // ---- Enrollment form (top) ----
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIStyles.CARD);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIStyles.LINE, 1), "Enroll Student in Course",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14), UIStyles.PRIMARY));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 7, 7, 7);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0;
        g.gridy = 0;

        cboStudents = new JComboBox<>();
        cboCourses = new JComboBox<>();
        fEnrollmentDate = new JTextField();
        fEnrollmentDate.setFont(UIStyles.BODY_FONT);
        fEnrollmentDate.setBorder(UIStyles.line());
        fEnrollmentDate.setToolTipText("yyyy-MM-dd");

        JLabel lblStud = new JLabel("Student *");
        lblStud.setFont(UIStyles.BODY_FONT);
        JLabel lblCrs = new JLabel("Course *");
        lblCrs.setFont(UIStyles.BODY_FONT);
        JLabel lblDate = new JLabel("Enrollment Date *");
        lblDate.setFont(UIStyles.BODY_FONT);

        addGrid(form, g, lblStud, cboStudents);
        addGrid(form, g, lblCrs, cboCourses);
        addGrid(form, g, lblDate, fEnrollmentDate);

        JButton btnLoad = button("Load Data", UIStyles.PRIMARY, e -> loadCombos());
        JButton btnEnroll = button("Enroll", UIStyles.SUCCESS, e -> doEnroll());
        JButton btnRefresh = button("Refresh", UIStyles.PRIMARY_DARK, e -> refreshData());
        JButton btnRemove = button("Remove Selected", UIStyles.DANGER, e -> doRemove());

        JPanel btns = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 6));
        btns.setBackground(UIStyles.CARD);
        btns.add(btnLoad);
        btns.add(btnEnroll);
        btns.add(btnRefresh);
        btns.add(btnRemove);

        g.gridx = 0;
        g.gridy++;
        g.gridwidth = 4;
        form.add(btns, g);

        JPanel formWrap = new JPanel(new BorderLayout());
        formWrap.setBackground(UIStyles.CARD);
        formWrap.add(form, BorderLayout.NORTH);
        add(formWrap, BorderLayout.NORTH);

        // ---- Table (center) ----
        table = new JTable();
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(UIStyles.line());
        add(scroll, BorderLayout.CENTER);
    }

    private void addGrid(JPanel form, GridBagConstraints g, JLabel label, java.awt.Component field) {
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
                data.students = servicePlaceholderStudents();
                data.courses = servicePlaceholderCourses();
                return data;
            }

            @Override
            protected void done() {
                try {
                    ComboData d = get();
                    cboStudents.setModel(new DefaultComboBoxModel<>(d.students.toArray(new ComboItem[0])));
                    cboCourses.setModel(new DefaultComboBoxModel<>(d.courses.toArray(new ComboItem[0])));
                } catch (Exception ex) {
                    showError("Failed to load students/courses: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    // These call the DAO layer directly for combo population.
    private java.util.List<ComboItem> servicePlaceholderStudents() throws AppException {
        java.util.List<ComboItem> list = new java.util.ArrayList<>();
        // Load via StudentDAO findAll
        dao.StudentDAO dao = new dao.StudentDAO();
        for (model.Student s : dao.findAll()) {
            list.add(new ComboItem(s.getId(), s.getStudentId() + " - " + s.getFullName()));
        }
        return list;
    }

    private java.util.List<ComboItem> servicePlaceholderCourses() throws AppException {
        java.util.List<ComboItem> list = new java.util.ArrayList<>();
        dao.CourseDAO dao = new dao.CourseDAO();
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
                    UIStyles.styleTable(table);
                } catch (Exception ex) {
                    // ignore
                }
            }
        };
        worker.execute();
    }

    /** Combo box item pairing an id with a display label. */
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

