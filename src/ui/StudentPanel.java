package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import dao.StudentDAO;
import model.Student;
import service.StudentService;
import util.AppException;
import util.DateUtils;
import util.UIStyles;

/**
 * Student management panel: add, view, search, update, delete students.
 *
 * <p>Consistent CRUD layout: toolbar (search + actions) at top, input form on
 * the left, data table in the center. All database work runs in a
 * SwingWorker so the UI stays responsive.
 */
public class StudentPanel extends BasePanel {

    private static final long serialVersionUID = 1L;

    private final StudentService service = new StudentService();

    private JTextField fStudentId, fRollNumber, fFullName, fEmail, fPhone,
            fAddress, fDob, fDepartment, fSemester, fAdmissionDate;

    private JTable table;
    private int selectedId = -1;

    public StudentPanel(MainFrame main) {
        super(main);
        buildUI();
        refreshData();
    }

    private void buildUI() {
        // ---- Toolbar ----
        JPanel toolbar = new JPanel(new BorderLayout(8, 8));
        toolbar.setBackground(UIStyles.CARD);
        toolbar.setBorder(new EmptyBorder(0, 0, 8, 0));

        JTextField search = new JTextField();
        search.setToolTipText("Search by student ID, roll no., name, email or department");
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

        // ---- Form (left) ----
        fStudentId = newField();
        fRollNumber = newField();
        fFullName = newField();
        fEmail = newField();
        fPhone = newField();
        fAddress = newField();
        fDob = newField();
        fDepartment = newField();
        fSemester = newField();
        fAdmissionDate = newField();

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIStyles.CARD);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIStyles.LINE, 1), "Student Details",
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
        addFieldRow(form, g, "Student ID *", fStudentId);
        addFieldRow(form, g, "Roll Number *", fRollNumber);
        addFieldRow(form, g, "Full Name *", fFullName);
        addFieldRow(form, g, "Email *", fEmail);
        addFieldRow(form, g, "Phone", fPhone);
        addFieldRow(form, g, "Address", fAddress);
        addFieldRow(form, g, "Date of Birth (yyyy-MM-dd)", fDob);
        addFieldRow(form, g, "Department *", fDepartment);
        addFieldRow(form, g, "Semester *", fSemester);
        addFieldRow(form, g, "Admission Date (yyyy-MM-dd)", fAdmissionDate);
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

        // ---- Table (center) ----
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
        fStudentId.setText(valueOf(table.getValueAt(row, 1)));
        fRollNumber.setText(valueOf(table.getValueAt(row, 2)));
        fFullName.setText(valueOf(table.getValueAt(row, 3)));
        fEmail.setText(valueOf(table.getValueAt(row, 4)));
        fPhone.setText(valueOf(table.getValueAt(row, 5)));
        fAddress.setText(valueOf(table.getValueAt(row, 6)));
        fDob.setText(valueOf(table.getValueAt(row, 7)));
        fDepartment.setText(valueOf(table.getValueAt(row, 8)));
        fSemester.setText(valueOf(table.getValueAt(row, 9)));
        fAdmissionDate.setText(valueOf(table.getValueAt(row, 10)));
    }

    private String valueOf(Object v) {
        return v == null ? "" : String.valueOf(v);
    }

    /* ---------- Operations ---------- */

    private void doSearch(String keyword) {
        SwingWorker<List<Student>, Void> worker = new SwingWorker<List<Student>, Void>() {
            @Override
            protected List<Student> doInBackground() throws Exception {
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
            service.create(readStudentId(), readRollNumber(), readFullName(), readEmail(),
                    readPhone(), readAddress(), readDob(), readDepartment(),
                    readSemester(), readAdmissionDate());
            showSuccess("Student saved successfully.");
            clearForm();
            refreshData();
        } catch (AppException ex) {
            showError(ex.getMessage());
        }
    }

    private void doUpdate() {
        if (selectedId < 0) {
            showWarning("Select a student from the table to update.");
            return;
        }
        try {
            service.update(selectedId, readStudentId(), readRollNumber(), readFullName(),
                    readEmail(), readPhone(), readAddress(), readDob(), readDepartment(),
                    readSemester(), readAdmissionDate());
            showSuccess("Student updated successfully.");
            clearForm();
            refreshData();
        } catch (AppException ex) {
            showError(ex.getMessage());
        }
    }

    private void doDelete() {
        if (selectedId < 0) {
            showWarning("Select a student from the table to delete.");
            return;
        }
        String name = fFullName.getText();
        if (!confirm("Delete student \"" + name + "\"?\n"
                + "Allowed only if no enrollments reference this student.", "Confirm Delete")) {
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
                        showSuccess("Student deleted.");
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

    /* ---------- Helpers ---------- */

    private void refreshData() {
        doSearch("");
    }

    private void fillTable(List<Student> students) {
        Object[][] data = new Object[students.size()][11];
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            data[i] = new Object[] {
                s.getId(), s.getStudentId(), s.getRollNumber(), s.getFullName(),
                s.getEmail(), s.getPhone(), s.getAddress(),
                DateUtils.display(s.getDateOfBirth()), s.getDepartment(),
                s.getSemester(), DateUtils.display(s.getAdmissionDate())
            };
        }
        replaceTableModel(table, data,
            new String[] { "ID", "Student ID", "Roll No.", "Full Name", "Email", "Phone",
                "Address", "DOB", "Department", "Semester", "Admission" });
        if (students.isEmpty()) {
            showWarning("No students found.");
        }
    }

    private void clearForm() {
        selectedId = -1;
        fStudentId.setText(""); fRollNumber.setText(""); fFullName.setText("");
        fEmail.setText(""); fPhone.setText(""); fAddress.setText("");
        fDob.setText(""); fDepartment.setText(""); fSemester.setText("");
        fAdmissionDate.setText("");
        table.clearSelection();
    }

    private String readStudentId() {
        return fStudentId.getText();
    }

    private String readRollNumber() {
        return fRollNumber.getText();
    }

    private String readFullName() {
        return fFullName.getText();
    }

    private String readEmail() {
        return fEmail.getText();
    }

    private String readPhone() {
        return fPhone.getText();
    }

    private String readAddress() {
        return fAddress.getText();
    }

    private String readDob() {
        return fDob.getText();
    }

    private String readDepartment() {
        return fDepartment.getText();
    }

    private String readSemester() {
        return fSemester.getText();
    }

    private String readAdmissionDate() {
        return fAdmissionDate.getText();
    }
}


