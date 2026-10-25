package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import dao.CourseDAO;
import model.Enrollment;
import service.EnrollmentService;
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
 * Enrollment desk: pair a student with a course, record the date, and review
 * the resulting registrations in one table.
 */
public class EnrollmentPanel extends BasePanel implements MainFrame.Refreshable {

    private static final long serialVersionUID = 1L;

    private final EnrollmentService service = new EnrollmentService();

    private AppComboBox<ComboItem> cboStudents;
    private AppComboBox<ComboItem> cboCourses;
    private FormField fEnrollmentDate;

    private JTable table;
    private TableCard tableCard;

    public EnrollmentPanel(MainFrame main) {
        super(main);
        buildUI();
        refreshData();
        loadCombos();
    }

    private void buildUI() {
        SectionHeader head = header("Module 05 \u00B7 Registration", "Enrollments",
                "Pair a student with a course and keep the registration ledger tidy.");
        add(head, BorderLayout.NORTH);

        JPanel body = pageBody();

        body.add(buildComposer(), BorderLayout.NORTH);

        table = buildTable(new Object[0][0],
                new String[]{"ID", "Student ID", "Student", "Course", "Status", "Date"});

        tableCard = tableCard("Registration ledger", "All enrollments across the institution.",
                table, "No enrollments yet",
                "Use the composer above to register the first student in a course.");
        tableCard.setCount(0, "enrollment");

        RoundedButton remove = new RoundedButton("Remove", AppIcon.Glyph.TRASH);
        remove.applyDanger();
        remove.addActionListener(e -> doRemove());
        tableCard.getCard().addHeaderAction(remove);

        RoundedButton refresh = iconButton(AppIcon.Glyph.REFRESH, "Reload ledger");
        refresh.addActionListener(e -> refreshData());
        tableCard.getCard().addHeaderAction(refresh);

        body.add(tableCard, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    private JPanel buildComposer() {
        DataCard card = card("Create an enrollment",
                "Choose a student and a course, then confirm the registration date.");

        cboStudents = new AppComboBox<>();
        cboCourses = new AppComboBox<>();
        fEnrollmentDate = new FormField("yyyy-MM-dd");
        fEnrollmentDate.setText(LocalDate.now().toString());
        fEnrollmentDate.setToolTipText("yyyy-MM-dd");

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
        form.add(field("Student *", cboStudents, null), g);

        g.gridx = 1;
        g.weightx = 1.0;
        form.add(field("Course *", cboCourses, null), g);

        g.gridx = 2;
        g.weightx = 0.34;
        form.add(field("Enrollment date *", fEnrollmentDate, "yyyy-MM-dd"), g);

        g.gridx = 3;
        g.weightx = 0;
        g.insets = new Insets(20, 0, 0, 0);
        g.fill = GridBagConstraints.NONE;
        g.anchor = GridBagConstraints.NORTHWEST;

        RoundedButton enroll = new RoundedButton("Enroll student", AppIcon.Glyph.PLUS);
        enroll.applyPrimary();
        enroll.setPreferredSize(new Dimension(158, 38));
        enroll.addActionListener(e -> doEnroll());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actions.setOpaque(false);
        actions.add(enroll);

        g.fill = GridBagConstraints.NONE;
        g.insets = new Insets(20, 0, 0, 0);
        form.add(actions, g);

        card.setBody(form);
        return card;
    }

    /* ---------- Loaders ---------- */

    private void loadCombos() {
        new SwingWorker<ComboData, Void>() {
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
                    ComboItem previousStudent = (ComboItem) cboStudents.getSelectedItem();
                    ComboItem previousCourse = (ComboItem) cboCourses.getSelectedItem();
                    cboStudents.setModel(new javax.swing.DefaultComboBoxModel<>(
                            d.students.toArray(new ComboItem[0])));
                    cboCourses.setModel(new javax.swing.DefaultComboBoxModel<>(
                            d.courses.toArray(new ComboItem[0])));
                    reselect(cboStudents, previousStudent);
                    reselect(cboCourses, previousCourse);
                } catch (Exception ex) {
                    showError("Failed to load students and courses: " + message(ex));
                }
            }
        }.execute();
    }

    private void reselect(AppComboBox<ComboItem> combo, ComboItem item) {
        if (item == null) {
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).id == item.id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private List<ComboItem> loadStudents() throws AppException {
        List<ComboItem> list = new java.util.ArrayList<>();
        for (model.Student s : new dao.StudentDAO().findAll()) {
            list.add(new ComboItem(s.getId(), s.getStudentId() + " \u00B7 " + s.getFullName()));
        }
        return list;
    }

    private List<ComboItem> loadCourses() throws AppException {
        List<ComboItem> list = new java.util.ArrayList<>();
        for (model.Course c : new CourseDAO().findAll()) {
            list.add(new ComboItem(c.getId(), c.getCourseCode() + " \u00B7 " + c.getCourseName()));
        }
        return list;
    }

    /* ---------- Operations ---------- */

    private void doEnroll() {
        ComboItem student = (ComboItem) cboStudents.getSelectedItem();
        ComboItem course = (ComboItem) cboCourses.getSelectedItem();
        if (student == null || course == null) {
            showWarning("Select both a student and a course first.");
            return;
        }
        String date = fEnrollmentDate.getText();
        new SwingWorker<Integer, Void>() {
            private String error;

            @Override
            protected Integer doInBackground() {
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
                        showSuccess("Enrollment #" + id + " created.");
                        fEnrollmentDate.setText(LocalDate.now().toString());
                        refreshData();
                    } else {
                        showError(error);
                    }
                } catch (Exception ex) {
                    showError("Enroll failed: " + message(ex));
                }
            }
        }.execute();
    }

    private void doRemove() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showWarning("Select an enrollment in the ledger first.");
            return;
        }
        int id = ((Number) table.getModel().getValueAt(
                table.convertRowIndexToModel(row), 0)).intValue();
        if (!confirmDanger("Remove enrollment #" + id + "?\n"
                + "Attendance and results attached to it must be removed first.",
                "Remove enrollment")) {
            return;
        }
        new SwingWorker<Boolean, Void>() {
            private String error;

            @Override
            protected Boolean doInBackground() {
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
                        showSuccess("Enrollment #" + id + " removed.");
                        refreshData();
                    } else {
                        showError(error);
                    }
                } catch (Exception ex) {
                    showError("Remove failed: " + message(ex));
                }
            }
        }.execute();
    }

    @Override
    public void refreshData() {
        loadCombos();
        new SwingWorker<List<Map<String, Object>>, Void>() {
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
                        data[i] = new Object[]{
                            r.get("id"), r.get("studentId"), r.get("studentName"),
                            r.get("courseCode"), r.get("status"),
                            DateUtils.display((java.time.LocalDate) r.get("enrollmentDate"))
                        };
                    }
                    replaceTableData(table, data,
                            new String[]{"ID", "Student ID", "Student", "Course", "Status", "Date"});
                    hideColumn(table, "ID");
                    setColumnWidths(table, 115, 190, 190, 95, 105);
                    tableCard.setCount(rows.size(), "enrollment");
                    tableCard.setSubtitle(rows.isEmpty()
                            ? "No registrations recorded yet."
                            : "All enrollments across the institution.");
                    tableCard.sync();
                } catch (Exception ex) {
                    showError("Could not load enrollments: " + message(ex));
                }
            }
        }.execute();
    }

    private void hideColumn(JTable table, String name) {
        javax.swing.table.TableColumn column = table.getColumn(name);
        if (column != null) {
            table.removeColumn(column);
        }
    }

    private String message(Exception ex) {
        return ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
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

        List<ComboItem> students;
        List<ComboItem> courses;
    }
}
