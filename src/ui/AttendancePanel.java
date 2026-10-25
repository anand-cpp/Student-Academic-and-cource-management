package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;
import javax.swing.table.DefaultTableModel;
import dao.AttendanceDAO;
import dao.CourseDAO;
import model.Attendance;
import service.AttendanceService;
import util.AppException;
import util.DateUtils;
import ui.components.FormField;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import ui.components.SectionHeader;
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

    private javax.swing.JComboBox<EnrollmentPanel.ComboItem> cboCourses;
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
        JPanel header = sectionHeader("Attendance",
                "Mark and track attendance per course and date.");

        RoundedPanel controls = new RoundedPanel();
        controls.setRadius(12);
        controls.setShadow(false);
        controls.setFill(UIStyles.CARD);
        controls.setLayout(new GridBagLayout());
        controls.setBorder(new EmptyBorder(16, 16, 16, 16));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0;
        g.gridy = 0;

        cboCourses = new javax.swing.JComboBox<>();
        fClassDate = new JTextField();
        fClassDate.setFont(UIStyles.BODY);
        fClassDate.setBorder(new javax.swing.border.LineBorder(UIStyles.LINE, 1, true));
        fClassDate.setToolTipText("Class date yyyy-MM-dd");
        fClassDate.setPreferredSize(new Dimension(150, 34));

        JLabel lblCrs = UIStyles.fieldLabel("Course *");
        JLabel lblDate = UIStyles.fieldLabel("Class Date *");

        addGrid(controls, g, lblCrs, cboCourses);
        addGrid(controls, g, lblDate, fClassDate);
        g.gridwidth = 1;

        RoundedButton btnLoad = new RoundedButton("Load Students");
        btnLoad.applyPrimary();
        btnLoad.addActionListener(e -> doLoad());

        RoundedButton btnSave = new RoundedButton("Save Attendance");
        btnSave.applySuccess();
        btnSave.addActionListener(e -> doSave());

        RoundedButton btnRefresh = new RoundedButton("Refresh");
        btnRefresh.applyGhost();
        btnRefresh.addActionListener(e -> refreshData());

        JPanel btns = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 6));
        btns.setOpaque(false);
        btns.add(btnLoad);
        btns.add(btnSave);
        btns.add(btnRefresh);

        g.gridx = 0;
        g.gridy++;
        g.gridwidth = 4;
        controls.add(btns, g);

        JPanel controlsWrap = new JPanel(new BorderLayout());
        controlsWrap.setOpaque(false);
        controlsWrap.add(controls, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[][] {},
                new String[] { "Enrollment ID", "Student ID", "Name", "Status" }) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        table = new JTable(model);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UIStyles.CARD);

        JPanel summary = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));
        summary.setOpaque(false);
        summary.setBorder(new EmptyBorder(6, 0, 0, 0));
        JLabel lblSummary = new JLabel("Select a course and date, then load students to see attendance.");
        lblSummary.setFont(UIStyles.CAPTION);
        lblSummary.setForeground(UIStyles.MUTED);
        summary.add(lblSummary);

        setLayout(new BorderLayout(0, 0));
        add(header, BorderLayout.NORTH);
        JPanel topWrap = new JPanel(new BorderLayout());
        topWrap.setOpaque(false);
        topWrap.add(controlsWrap, BorderLayout.NORTH);
        add(topWrap, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(summary, BorderLayout.SOUTH);

        loadCourses();
    }

    private void addGrid(JPanel panel, GridBagConstraints g, JLabel label, java.awt.Component field) {
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
                CourseDAO dao = new CourseDAO();
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
                    AttendanceDAO adao = new AttendanceDAO();
                    for (Map<String, Object> e : enrollments) {
                        int enrollmentId = (Integer) e.get("enrollmentId");
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
                BorderFactory.createMatteBorder(0, 0, 2, 0, UIStyles.PRIMARY));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.setDefaultRenderer(Object.class, new ui.components.TableRenderer());
    }
}

