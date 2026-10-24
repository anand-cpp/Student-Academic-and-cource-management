package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
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
import javax.swing.table.DefaultTableModel;
import dao.AttendanceDAO;
import dao.CourseDAO;
import dao.EnrollmentDAO;
import model.Attendance;
import service.AttendanceService;
import util.AppException;
import util.DateUtils;
import util.UIStyles;

/**
 * Attendance management panel.
 *
 * <p>Workflow: select a course, pick a class date, load enrolled students, mark
 * each as Present/Absent/Late, then save. Existing marks for that date are
 * pre-filled so re-marking updates rather than duplicates.
 *
 * <p>Percentage rule (documented, consistent): Late counts as attended.
 * Attendance % = (Present + Late) / Total recorded &times; 100.
 */
public class AttendancePanel extends BasePanel implements MainFrame.Refreshable {

    private static final long serialVersionUID = 1L;

    private final AttendanceService service = new AttendanceService();

    private JComboBox<EnrollmentPanel.ComboItem> cboCourses;
    private JTextField fClassDate;
    private JTable table;
    private DefaultTableModel model;

    private int currentCourseId = -1;
    private LocalDate currentDate = null;

    public AttendancePanel(MainFrame main) {
        super(main);
        buildUI();
        refreshData();
    }

    private void buildUI() {
        // ---- Controls (top) ----
        JPanel controls = new JPanel(new GridBagLayout());
        controls.setBackground(UIStyles.CARD);
        controls.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIStyles.LINE, 1), "Mark Attendance",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14), UIStyles.PRIMARY));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 7, 7, 7);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0;
        g.gridy = 0;

        cboCourses = new JComboBox<>();
        fClassDate = new JTextField();
        fClassDate.setFont(UIStyles.BODY_FONT);
        fClassDate.setBorder(UIStyles.line());
        fClassDate.setToolTipText("Class date yyyy-MM-dd");
        fClassDate.setPreferredSize(new Dimension(150, 30));

        JLabel lblCrs = new JLabel("Course *");
        lblCrs.setFont(UIStyles.BODY_FONT);
        JLabel lblDate = new JLabel("Class Date *");
        lblDate.setFont(UIStyles.BODY_FONT);

        g.gridwidth = 2;
        addField(controls, g, lblCrs, cboCourses);
        addField(controls, g, lblDate, fClassDate);
        g.gridwidth = 1;

        JButton btnLoad = button("Load Students", UIStyles.PRIMARY, e -> doLoad());
        JButton btnSave = button("Save Attendance", UIStyles.SUCCESS, e -> doSave());
        JButton btnRefresh = button("Refresh", UIStyles.PRIMARY_DARK, e -> refreshData());

        JPanel btns = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 6));
        btns.setBackground(UIStyles.CARD);
        btns.add(btnLoad);
        btns.add(btnSave);
        btns.add(btnRefresh);

        g.gridx = 0;
        g.gridy++;
        g.gridwidth = 4;
        controls.add(btns, g);

        add(controls, BorderLayout.NORTH);

        // ---- Table (center) ----
        model = new DefaultTableModel(
                new Object[][] {},
                new String[] { "Enrollment ID", "Student ID", "Name", "Status" }) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        table = new JTable(model);
        table.getColumnModel().getColumn(3).setCellEditor(
                new DefaultCellEditor(new JComboBox<>(new String[] { "PRESENT", "ABSENT", "LATE" })));
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        UIStyles.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(UIStyles.line());
        add(scroll, BorderLayout.CENTER);

        // ---- Summary (bottom) ----
        JPanel summary = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));
        summary.setBackground(UIStyles.CARD);
        summary.setBorder(new EmptyBorder(6, 0, 0, 0));
        JLabel lblSummary = new JLabel("Select a course and date, then load students to see attendance.");
        lblSummary.setFont(UIStyles.SMALL_FONT);
        lblSummary.setForeground(UIStyles.MUTED);
        summary.add(lblSummary);
        add(summary, BorderLayout.SOUTH);

        loadCourses();
    }

    private void addField(JPanel panel, GridBagConstraints g, JLabel label, java.awt.Component field) {
        g.gridx = 0;
        panel.add(label, g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        panel.add(field, g);
        g.gridx = 0;
        g.gridy++;
        g.fill = GridBagConstraints.NONE;
        g.weightx = 0;
    }

    private void loadCourses() {
        SwingWorker<List<EnrollmentPanel.ComboItem>, Void> worker =
                new SwingWorker<List<EnrollmentPanel.ComboItem>, Void>() {
            @Override
            protected List<EnrollmentPanel.ComboItem> doInBackground() throws Exception {
                List<EnrollmentPanel.ComboItem> list = new ArrayList<>();
                dao.CourseDAO dao = new dao.CourseDAO();
                for (model.Course c : dao.findAll()) {
                    list.add(new EnrollmentPanel.ComboItem(c.getId(),
                            c.getCourseCode() + " - " + c.getCourseName()));
                }
                return list;
            }

            @Override
            protected void done() {
                try {
                    List<EnrollmentPanel.ComboItem> list = get();
                    cboCourses.setModel(new javax.swing.DefaultComboBoxModel<>(
                            list.toArray(new EnrollmentPanel.ComboItem[0])));
                } catch (Exception ex) {
                    showError("Failed to load courses: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void doLoad() {
        EnrollmentPanel.ComboItem item = (EnrollmentPanel.ComboItem) cboCourses.getSelectedItem();
        if (item == null) {
            showWarning("No course available.");
            return;
        }
        String dateText = fClassDate.getText().trim();
        if (dateText.isEmpty()) {
            showWarning("Enter a class date (yyyy-MM-dd).");
            return;
        }
        LocalDate date;
        try {
            date = DateUtils.parse(dateText);
        } catch (AppException ex) {
            showError(ex.getMessage());
            return;
        }
        if (date.isAfter(LocalDate.now())) {
            showError("Class date cannot be in the future.");
            return;
        }

        currentCourseId = item.id;
        currentDate = date;

        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<List<Map<String, Object>>, Void>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return service.enrollmentsForCourse(currentCourseId);
            }

            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> enrollments = get();
                    model.setRowCount(0);
                    dao.AttendanceDAO adao = new dao.AttendanceDAO();
                    for (Map<String, Object> e : enrollments) {
                        int enrollmentId = (Integer) e.get("enrollmentId");
                        // Pre-fill existing attendance for this date
                        String status = "PRESENT";
                        List<Attendance> existing = adao.findByEnrollmentAndDate(enrollmentId, date);
                        if (!existing.isEmpty()) {
                            status = existing.get(0).getStatus();
                        }
                        model.addRow(new Object[] {
                            enrollmentId, e.get("studentId"), e.get("studentName"), status
                        });
                    }
                    if (enrollments.isEmpty()) {
                        showWarning("No active students enrolled in this course.");
                    }
                } catch (Exception ex) {
                    showError("Failed to load students: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void doSave() {
        if (currentCourseId < 0 || currentDate == null) {
            showWarning("Please load students first.");
            return;
        }
        Map<Integer, String> statusByEnrollment = new HashMap<>();
        for (int r = 0; r < model.getRowCount(); r++) {
            int enrollmentId = (Integer) model.getValueAt(r, 0);
            String status = String.valueOf(model.getValueAt(r, 3));
            statusByEnrollment.put(enrollmentId, status);
        }
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            private String error;

            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    service.save(currentCourseId, currentDate, statusByEnrollment);
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
                        showSuccess("Attendance saved for " + currentDate + ".");
                    } else {
                        showError(error);
                    }
                } catch (Exception ex) {
                    showError("Save failed: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    @Override
    public void refreshData() {
        loadCourses();
        model.setRowCount(0);
        currentCourseId = -1;
        currentDate = null;
    }
}

