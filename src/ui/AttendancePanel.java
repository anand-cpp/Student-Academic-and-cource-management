package ui;

import java.awt.BorderLayout;
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
import javax.swing.DefaultCellEditor;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import dao.AttendanceDAO;
import dao.CourseDAO;
import model.Attendance;
import service.AttendanceService;
import ui.components.AppComboBox;
import ui.components.AppIcon;
import ui.components.DataCard;
import ui.components.FormField;
import ui.components.RoundedButton;
import ui.components.SectionHeader;
import ui.components.TableCard;
import util.AppException;
import util.DateUtils;
import util.UIStyles;

/**
 * Attendance desk.
 *
 * <p>Workflow: choose a course and class date, load the enrolled students, mark
 * each row Present / Late / Absent, then save. Existing marks for that date are
 * pre-filled, so re-marking updates rather than duplicates.
 *
 * <p>Percentage rule (documented, consistent): Late counts as attended.
 * Attendance % = (Present + Late) / Total recorded &times; 100.
 */
public class AttendancePanel extends BasePanel implements MainFrame.Refreshable {

    private static final long serialVersionUID = 1L;

    private final AttendanceService service = new AttendanceService();

    private AppComboBox<EnrollmentPanel.ComboItem> cboCourses;
    private FormField fClassDate;
    private JTable table;
    private DefaultTableModel model;
    private TableCard tableCard;

    private int currentCourseId = -1;
    private LocalDate currentDate = null;

    public AttendancePanel(MainFrame main) {
        super(main);
        buildUI();
        refreshData();
    }

    private void buildUI() {
        SectionHeader head = header("Module 06 \u00B7 Presence", "Attendance",
                "Load a class roster, mark presence, and keep the record for the day.");
        add(head, BorderLayout.NORTH);

        JPanel body = pageBody();
        body.add(buildControls(), BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[][]{},
                new String[]{"Enrollment ID", "Student ID", "Name", "Status"}) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        table = new JTable(model);
        styleTable(table);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        setColumnWidths(table, 0, 110, 220, 120);

        AppComboBox<String> statusEditor = new AppComboBox<>();
        statusEditor.addItem("PRESENT");
        statusEditor.addItem("LATE");
        statusEditor.addItem("ABSENT");
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(statusEditor));

        tableCard = tableCard("Class roster", "Choose a course and date, then load students.",
                table, "No class loaded",
                "Pick a course and a class date above, then press Load students to begin marking.");
        tableCard.setCount(0, "student");

        RoundedButton save = new RoundedButton("Save attendance", AppIcon.Glyph.SAVE);
        save.applyPrimary();
        save.addActionListener(e -> doSave());
        tableCard.getCard().addHeaderAction(save);

        RoundedButton refresh = iconButton(AppIcon.Glyph.REFRESH, "Reset attendance view");
        refresh.addActionListener(e -> refreshData());
        tableCard.getCard().addHeaderAction(refresh);

        model.addTableModelListener(e -> updateSummary());

        body.add(tableCard, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    private JPanel buildControls() {
        DataCard card = card("Class selection",
                "Attendance is stored per enrollment, course and date.");

        cboCourses = new AppComboBox<>();
        fClassDate = new FormField("yyyy-MM-dd");
        fClassDate.setText(LocalDate.now().toString());
        fClassDate.setToolTipText("Class date yyyy-MM-dd");

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(16, 18, 18, 18));

        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0;
        g.anchor = GridBagConstraints.NORTHWEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 0, 12);

        g.gridx = 0;
        g.weightx = 1.0;
        form.add(field("Course *", cboCourses, null), g);

        g.gridx = 1;
        g.weightx = 0.32;
        form.add(field("Class date *", fClassDate, "yyyy-MM-dd"), g);

        RoundedButton load = new RoundedButton("Load students", AppIcon.Glyph.STUDENTS);
        load.applyOutline();
        load.setPreferredSize(new Dimension(158, 38));
        load.addActionListener(e -> doLoad());

        RoundedButton refresh = new RoundedButton("Refresh", AppIcon.Glyph.REFRESH);
        refresh.applyGhost();
        refresh.addActionListener(e -> refreshData());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actions.setOpaque(false);
        actions.add(load);
        actions.add(javax.swing.Box.createHorizontalStrut(8));
        actions.add(refresh);

        g.gridx = 2;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        g.insets = new Insets(20, 0, 0, 0);
        form.add(actions, g);

        card.setBody(form);
        return card;
    }

    /* ---------- Data ---------- */

    private void loadCourses() {
        new SwingWorker<List<EnrollmentPanel.ComboItem>, Void>() {
            @Override
            protected List<EnrollmentPanel.ComboItem> doInBackground() throws Exception {
                List<EnrollmentPanel.ComboItem> list = new ArrayList<>();
                for (model.Course c : new CourseDAO().findAll()) {
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
                            (EnrollmentPanel.ComboItem) cboCourses.getSelectedItem();
                    cboCourses.setModel(new javax.swing.DefaultComboBoxModel<>(
                            list.toArray(new EnrollmentPanel.ComboItem[0])));
                    if (previous != null) {
                        for (int i = 0; i < cboCourses.getItemCount(); i++) {
                            if (cboCourses.getItemAt(i).id == previous.id) {
                                cboCourses.setSelectedIndex(i);
                                break;
                            }
                        }
                    }
                } catch (Exception ex) {
                    showError("Failed to load courses: " + message(ex));
                }
            }
        }.execute();
    }

    private void doLoad() {
        EnrollmentPanel.ComboItem item = (EnrollmentPanel.ComboItem) cboCourses.getSelectedItem();
        if (item == null) {
            showWarning("No course available. Add a course first.");
            return;
        }
        String dateText = fClassDate.getText() == null ? "" : fClassDate.getText().trim();
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

        new SwingWorker<List<Map<String, Object>>, Void>() {
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
                        model.addRow(new Object[]{
                            enrollmentId, e.get("studentId"), e.get("studentName"), status
                        });
                    }
                    tableCard.setSubtitle(date + " \u00B7 " + item.label);
                    tableCard.sync();
                    updateSummary();
                    if (enrollments.isEmpty()) {
                        showWarning("No active students are enrolled in this course.");
                    }
                } catch (Exception ex) {
                    showError("Failed to load students: " + message(ex));
                }
            }
        }.execute();
    }

    private void doSave() {
        if (currentCourseId < 0 || currentDate == null) {
            showWarning("Load a class roster before saving.");
            return;
        }
        Map<Integer, String> statusByEnrollment = new HashMap<>();
        for (int r = 0; r < model.getRowCount(); r++) {
            int enrollmentId = ((Number) model.getValueAt(r, 0)).intValue();
            String status = String.valueOf(model.getValueAt(r, 3)).toUpperCase();
            statusByEnrollment.put(enrollmentId, status);
        }
        new SwingWorker<Boolean, Void>() {
            private String error;

            @Override
            protected Boolean doInBackground() {
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
                        updateSummary();
                    } else {
                        showError(error);
                    }
                } catch (Exception ex) {
                    showError("Save failed: " + message(ex));
                }
            }
        }.execute();
    }

    private void updateSummary() {
        int present = 0;
        int late = 0;
        int absent = 0;
        for (int r = 0; r < model.getRowCount(); r++) {
            String status = String.valueOf(model.getValueAt(r, 3)).toUpperCase();
            switch (status) {
                case "PRESENT" -> present++;
                case "LATE" -> late++;
                case "ABSENT" -> absent++;
                default -> {
                }
            }
        }
        int total = model.getRowCount();
        tableCard.setCount(total, "student");
        if (total > 0) {
            String summary = present + " present";
            if (late > 0) {
                summary += " \u00B7 " + late + " late";
            }
            summary += " \u00B7 " + absent + " absent";
            tableCard.setSubtitle(summary);
        }
        tableCard.sync();
    }

    @Override
    public void refreshData() {
        loadCourses();
        if (model != null) {
            model.setRowCount(0);
        }
        currentCourseId = -1;
        currentDate = null;
        if (tableCard != null) {
            tableCard.setSubtitle("Choose a course and date, then load students.");
            tableCard.setCount(0, "student");
            tableCard.sync();
        }
    }

    private String message(Exception ex) {
        return ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
    }
}
