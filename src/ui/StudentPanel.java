package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import dao.StudentDAO;
import model.Student;
import service.StudentService;
import util.AppException;
import util.DateUtils;
import util.InputValidator;
import ui.components.EmptyState;
import ui.components.FormField;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import ui.components.SectionHeader;
import util.UIStyles;

/**
 * Student management panel: add, view, search, update, delete students.
 */
public class StudentPanel extends BasePanel {

    private static final long serialVersionUID = 1L;

    private final StudentService service = new StudentService();

    private FormField fStudentId, fRollNumber, fFullName, fEmail, fPhone,
            fAddress, fDob, fDepartment, fSemester, fAdmissionDate;

    private JTable table;
    private int selectedId = -1;

    public StudentPanel(MainFrame main) {
        super(main);
        buildUI();
        refreshData();
    }

    private void buildUI() {
        // Header section
        JPanel header = sectionHeader("Students",
                "Manage student records \u2014 add, search, edit, delete.");

        // Toolbar
        JPanel toolbar = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 12, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 12, 0));

        JTextField search = new JTextField();
        search.setFont(UIStyles.BODY);
        search.setBorder(new javax.swing.border.LineBorder(UIStyles.LINE, 1, true));
        search.setPreferredSize(new Dimension(340, 40));
        search.setToolTipText("Search by student ID, roll no., name, email or department");

        RoundedButton btnSearch = new RoundedButton("Search");
        btnSearch.applyAccent();
        btnSearch.addActionListener(e -> doSearch(search.getText()));

        RoundedButton btnRefresh = new RoundedButton("Refresh");
        btnRefresh.applyGhost();
        btnRefresh.addActionListener(e -> refreshData());

        RoundedButton btnAddNew = new RoundedButton("Add New");
        btnAddNew.applyPrimary();
        btnAddNew.addActionListener(e -> clearForm());

        toolbar.add(new JLabel("  Search:"));
        toolbar.add(search);
        toolbar.add(btnSearch);
        toolbar.add(btnRefresh);
        toolbar.add(btnAddNew);

        // Form (left card)
        fStudentId = new FormField("STU0001");
        fRollNumber = new FormField("ROLL0001");
        fFullName = new FormField("Aarav Singh");
        fEmail = new FormField("aarav@college.edu");
        fPhone = new FormField("+91 98765 43210");
        fAddress = new FormField("12, College Road, Delhi");
        fDob = new FormField("2002-03-15");
        fDepartment = new FormField("Computer Science");
        fSemester = new FormField("3");
        fAdmissionDate = new FormField("2021-08-01");

        RoundedPanel formCard = new RoundedPanel();
        formCard.setRadius(12);
        formCard.setShadow(false);
        formCard.setFill(UIStyles.CARD);
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.gridx = 0;
        g.gridy = 0;

        g.gridwidth = 2;
        addFormRow(formCard, g, "Student ID *", fStudentId);
        addFormRow(formCard, g, "Roll Number *", fRollNumber);
        addFormRow(formCard, g, "Full Name *", fFullName);
        addFormRow(formCard, g, "Email *", fEmail);
        addFormRow(formCard, g, "Phone", fPhone);
        addFormRow(formCard, g, "Address", fAddress);
        addFormRow(formCard, g, "Date of Birth (yyyy-MM-dd)", fDob);
        addFormRow(formCard, g, "Department *", fDepartment);
        addFormRow(formCard, g, "Semester *", fSemester);
        addFormRow(formCard, g, "Admission Date (yyyy-MM-dd)", fAdmissionDate);
        g.gridwidth = 1;

        JPanel formActions = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 6));
        formActions.setOpaque(false);
        RoundedButton btnSave = new RoundedButton("Save");
        btnSave.applySuccess();
        btnSave.addActionListener(e -> doSave());
        RoundedButton btnUpdate = new RoundedButton("Update");
        btnUpdate.applyAccent();
        btnUpdate.addActionListener(e -> doUpdate());
        RoundedButton btnDelete = new RoundedButton("Delete");
        btnDelete.applyDanger();
        btnDelete.addActionListener(e -> doDelete());
        RoundedButton btnClear = new RoundedButton("Reset");
        btnClear.applyGhost();
        btnClear.addActionListener(e -> clearForm());
        formActions.add(btnSave);
        formActions.add(btnUpdate);
        formActions.add(btnDelete);
        formActions.add(btnClear);

        g.gridx = 0;
        g.gridy++;
        g.gridwidth = 2;
        g.anchor = GridBagConstraints.WEST;
        formCard.add(formActions, g);

        // Form wrapper with fixed width
        JPanel formWrap = new JPanel(new BorderLayout());
        formWrap.setOpaque(false);
        formWrap.setPreferredSize(new Dimension(420, 0));
        formWrap.add(formCard, BorderLayout.NORTH);

        // Table (center)
        table = new JTable();
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UIStyles.CARD);
        styleTable(table);

        // Row selection
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || table.getSelectedRow() < 0) {
                return;
            }
            loadSelectedRow();
        });

        // Assemble
        JPanel content = new JPanel(new BorderLayout(16, 16));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(0, 20, 20, 20));
        content.add(formWrap, BorderLayout.WEST);
        content.add(scroll, BorderLayout.CENTER);

        setLayout(new BorderLayout(0, 0));
        add(header, BorderLayout.NORTH);
        add(toolbar, BorderLayout.SOUTH); // toolbar at top under header
        add(content, BorderLayout.CENTER);

        // Reorder: toolbar should be under header
        getComponent(1); // toolbar already added
    }

    private void addFormRow(JPanel form, GridBagConstraints g, String label, FormField field) {
        JLabel l = UIStyles.fieldLabel(label);
        l.setBorder(new EmptyBorder(0, 0, 4, 0));
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
        replaceTableData(table, data,
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

    private String readStudentId() { return fStudentId.getText(); }
    private String readRollNumber() { return fRollNumber.getText(); }
    private String readFullName() { return fFullName.getText(); }
    private String readEmail() { return fEmail.getText(); }
    private String readPhone() { return fPhone.getText(); }
    private String readAddress() { return fAddress.getText(); }
    private String readDob() { return fDob.getText(); }
    private String readDepartment() { return fDepartment.getText(); }
    private String readSemester() { return fSemester.getText(); }
    private String readAdmissionDate() { return fAdmissionDate.getText(); }

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
}




