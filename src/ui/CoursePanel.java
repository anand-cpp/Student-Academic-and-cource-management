package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import model.Course;
import service.CourseService;
import util.AppException;
import util.InputValidator;
import util.UIStyles;

/**
 * Course management panel: add, view, search, update, delete courses and assign faculty.
 */
public class CoursePanel extends BasePanel {

    private static final long serialVersionUID = 1L;

    private final CourseService service = new CourseService();

    private JTextField fCourseCode, fCourseName, fDepartment, fSemester,
            fCredits, fMaximumMarks, fPassingMarks, fAssignedFacultyId;

    private JTable table;
    private int selectedId = -1;

    public CoursePanel(MainFrame main) {
        super(main);
        buildUI();
        refreshData();
    }

    private void buildUI() {
        JPanel toolbar = new JPanel(new BorderLayout(8, 8));
        toolbar.setBackground(UIStyles.CARD);
        toolbar.setBorder(new EmptyBorder(0, 0, 8, 0));

        JTextField search = new JTextField();
        search.setToolTipText("Search by course code, name or department");
        search.setPreferredSize(new Dimension(360, 34));
        JButton btnSearch = button("Search", UIStyles.ACCENT, e -> doSearch(search.getText()));
        JButton btnRefresh = button("Refresh", UIStyles.PRIMARY, e -> refreshData());
        JButton btnAddNew = button("Add New", UIStyles.PRIMARY_DARK, e -> clearForm());

        JPanel searchWrap = new JPanel(new BorderLayout(8, 0));
        searchWrap.setBackground(UIStyles.CARD);
        searchWrap.add(new JLabel("  Search:"), BorderLayout.WEST);
        searchWrap.add(search, BorderLayout.CENTER);

        JPanel actions = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));
        actions.setBackground(UIStyles.CARD);
        actions.add(btnSearch);
        actions.add(btnRefresh);
        actions.add(btnAddNew);

        toolbar.add(searchWrap, BorderLayout.WEST);
        toolbar.add(actions, BorderLayout.EAST);
        add(toolbar, BorderLayout.NORTH);

        fCourseCode = newField();
        fCourseName = newField();
        fDepartment = newField();
        fSemester = newField();
        fCredits = newField();
        fMaximumMarks = newField();
        fPassingMarks = newField();
        fAssignedFacultyId = newField();

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIStyles.CARD);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIStyles.LINE, 1), "Course Details",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14), UIStyles.PRIMARY));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 7, 7, 7);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.gridx = 0;
        g.gridy = 0;

        g.gridwidth = 2;
        addFieldRow(form, g, "Course Code *", fCourseCode);
        addFieldRow(form, g, "Course Name *", fCourseName);
        addFieldRow(form, g, "Department *", fDepartment);
        addFieldRow(form, g, "Semester *", fSemester);
        addFieldRow(form, g, "Credits *", fCredits);
        addFieldRow(form, g, "Maximum Marks *", fMaximumMarks);
        addFieldRow(form, g, "Passing Marks *", fPassingMarks);
        addFieldRow(form, g, "Assigned Faculty ID (optional)", fAssignedFacultyId);
        g.gridwidth = 1;

        JPanel formActions = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 6));
        formActions.setBackground(UIStyles.CARD);
        JButton btnSave = button("Save", UIStyles.SUCCESS, e -> doSave());
        JButton btnUpdate = button("Update", UIStyles.ACCENT, e -> doUpdate());
        JButton btnDelete = button("Delete", UIStyles.DANGER, e -> doDelete());
        JButton btnClear = button("Reset", UIStyles.MUTED, e -> clearForm());
        formActions.add(btnSave);
        formActions.add(btnUpdate);
        formActions.add(btnDelete);
        formActions.add(btnClear);

        g.gridx = 0;
        g.gridy++;
        g.gridwidth = 2;
        g.anchor = GridBagConstraints.WEST;
        form.add(formActions, g);

        JPanel formWrap = new JPanel(new BorderLayout());
        formWrap.setBackground(UIStyles.CARD);
        formWrap.setPreferredSize(new Dimension(420, 0));
        formWrap.add(form, BorderLayout.NORTH);
        add(formWrap, BorderLayout.WEST);

        table = new JTable();
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(UIStyles.line());
        add(scroll, BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || table.getSelectedRow() < 0) {
                return;
            }
            loadSelectedRow();
        });
    }

    private JTextField newField() {
        JTextField f = new JTextField();
        f.setFont(UIStyles.BODY_FONT);
        f.setBorder(UIStyles.line());
        return f;
    }

    private void addFieldRow(JPanel form, GridBagConstraints g, String label, JTextField field) {
        JLabel l = new JLabel(label);
        l.setFont(UIStyles.BODY_FONT);
        l.setForeground(UIStyles.INK);
        g.gridx = 0;
        form.add(l, g);
        g.gridy++;
        g.gridx = 0;
        form.add(field, g);
        g.gridy++;
    }

    private void loadSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        selectedId = (Integer) table.getValueAt(row, 0);
        fCourseCode.setText(valueOf(table.getValueAt(row, 1)));
        fCourseName.setText(valueOf(table.getValueAt(row, 2)));
        fDepartment.setText(valueOf(table.getValueAt(row, 3)));
        fSemester.setText(valueOf(table.getValueAt(row, 4)));
        fCredits.setText(String.valueOf(table.getValueAt(row, 5)));
        fMaximumMarks.setText(valueOf(table.getValueAt(row, 6)));
        fPassingMarks.setText(valueOf(table.getValueAt(row, 7)));
        Object af = table.getValueAt(row, 8);
        fAssignedFacultyId.setText(af == null ? "" : String.valueOf(af));
    }

    private String valueOf(Object v) {
        return v == null ? "" : String.valueOf(v);
    }

    private void doSearch(String keyword) {
        SwingWorker<List<Course>, Void> worker = new SwingWorker<List<Course>, Void>() {
            @Override
            protected List<Course> doInBackground() throws Exception {
                return service.search(keyword);
            }

            @Override
            protected void done() {
                try {
                    fillTable(get());
                } catch (Exception ex) {
                    showError("Search failed: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void doSave() {
        try {
            BigDecimal max = InputValidator.decimal(fMaximumMarks.getText(), "Maximum marks");
            BigDecimal pass = InputValidator.decimal(fPassingMarks.getText(), "Passing marks");
            if (pass.compareTo(max) > 0) {
                throw new AppException("OUT_OF_RANGE", "Passing marks cannot exceed maximum marks.");
            }
            service.create(readCourseCode(), readCourseName(), readDepartment(), readSemester(),
                    readCredits(), fMaximumMarks.getText(), fPassingMarks.getText(),
                    fAssignedFacultyId.getText());
            showSuccess("Course saved successfully.");
            clearForm();
            refreshData();
        } catch (AppException ex) {
            showError(ex.getMessage());
        }
    }

    private void doUpdate() {
        if (selectedId < 0) {
            showWarning("Select a course from the table to update.");
            return;
        }
        try {
            BigDecimal max = InputValidator.decimal(fMaximumMarks.getText(), "Maximum marks");
            BigDecimal pass = InputValidator.decimal(fPassingMarks.getText(), "Passing marks");
            if (pass.compareTo(max) > 0) {
                throw new AppException("OUT_OF_RANGE", "Passing marks cannot exceed maximum marks.");
            }
            service.update(selectedId, readCourseCode(), readCourseName(), readDepartment(),
                    readSemester(), readCredits(), fMaximumMarks.getText(), fPassingMarks.getText(),
                    fAssignedFacultyId.getText());
            showSuccess("Course updated successfully.");
            clearForm();
            refreshData();
        } catch (AppException ex) {
            showError(ex.getMessage());
        }
    }

    private void doDelete() {
        if (selectedId < 0) {
            showWarning("Select a course from the table to delete.");
            return;
        }
        String name = fCourseName.getText();
        if (!confirm("Delete course \"" + name + "\"?\n"
                + "Allowed only if no enrollments, attendance or results reference it.", "Confirm Delete")) {
            return;
        }
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            private String error;

            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    service.delete(selectedId);
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
                        showSuccess("Course deleted.");
                        clearForm();
                        refreshData();
                    } else {
                        showError(error);
                    }
                } catch (Exception ex) {
                    showError("Delete failed: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void refreshData() {
        doSearch("");
    }

    private void fillTable(List<Course> list) {
        Object[][] data = new Object[list.size()][9];
        for (int i = 0; i < list.size(); i++) {
            Course c = list.get(i);
            data[i] = new Object[] {
                c.getId(), c.getCourseCode(), c.getCourseName(), c.getDepartment(),
                c.getSemester(), c.getCredits(), c.getMaximumMarks(),
                c.getPassingMarks(), c.getAssignedFacultyId() == null ? "Unassigned" : c.getAssignedFacultyId()
            };
        }
        replaceTableModel(table, data,
            new String[] { "ID", "Course Code", "Course Name", "Department", "Semester",
                "Credits", "Max Marks", "Pass Marks", "Faculty ID" });
        if (list.isEmpty()) {
            showWarning("No courses found.");
        }
    }

    private void clearForm() {
        selectedId = -1;
        fCourseCode.setText(""); fCourseName.setText(""); fDepartment.setText("");
        fSemester.setText(""); fCredits.setText(""); fMaximumMarks.setText("");
        fPassingMarks.setText(""); fAssignedFacultyId.setText("");
        table.clearSelection();
    }

    private String readCourseCode() { return fCourseCode.getText(); }
    private String readCourseName() { return fCourseName.getText(); }
    private String readDepartment() { return fDepartment.getText(); }
    private String readSemester() { return fSemester.getText(); }
    private String readCredits() { return fCredits.getText(); }
}

