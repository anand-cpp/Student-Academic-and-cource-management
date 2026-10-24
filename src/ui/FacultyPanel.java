package ui;

import java.awt.BorderLayout;
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
import model.Faculty;
import service.FacultyService;
import util.AppException;
import util.DateUtils;
import util.UIStyles;

/**
 * Faculty management panel: add, view, search, update, delete faculty members.
 */
public class FacultyPanel extends BasePanel {

    private static final long serialVersionUID = 1L;

    private final FacultyService service = new FacultyService();

    private JTextField fFacultyId, fEmployeeId, fFullName, fEmail, fPhone,
            fAddress, fDob, fDepartment, fDesignation, fSpecialization;

    private JTable table;
    private int selectedId = -1;

    public FacultyPanel(MainFrame main) {
        super(main);
        buildUI();
        refreshData();
    }

    private void buildUI() {
        JPanel toolbar = new JPanel(new BorderLayout(8, 8));
        toolbar.setBackground(UIStyles.CARD);
        toolbar.setBorder(new EmptyBorder(0, 0, 8, 0));

        JTextField search = new JTextField();
        search.setToolTipText("Search by faculty ID, employee ID, name, email or department");
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

        fFacultyId = newField();
        fEmployeeId = newField();
        fFullName = newField();
        fEmail = newField();
        fPhone = newField();
        fAddress = newField();
        fDob = newField();
        fDepartment = newField();
        fDesignation = newField();
        fSpecialization = newField();

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIStyles.CARD);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIStyles.LINE, 1), "Faculty Details",
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
        addFieldRow(form, g, "Faculty ID *", fFacultyId);
        addFieldRow(form, g, "Employee ID *", fEmployeeId);
        addFieldRow(form, g, "Full Name *", fFullName);
        addFieldRow(form, g, "Email *", fEmail);
        addFieldRow(form, g, "Phone", fPhone);
        addFieldRow(form, g, "Address", fAddress);
        addFieldRow(form, g, "Date of Birth (yyyy-MM-dd)", fDob);
        addFieldRow(form, g, "Department *", fDepartment);
        addFieldRow(form, g, "Designation *", fDesignation);
        addFieldRow(form, g, "Specialization *", fSpecialization);
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
        fFacultyId.setText(valueOf(table.getValueAt(row, 1)));
        fEmployeeId.setText(valueOf(table.getValueAt(row, 2)));
        fFullName.setText(valueOf(table.getValueAt(row, 3)));
        fEmail.setText(valueOf(table.getValueAt(row, 4)));
        fPhone.setText(valueOf(table.getValueAt(row, 5)));
        fAddress.setText(valueOf(table.getValueAt(row, 6)));
        fDob.setText(valueOf(table.getValueAt(row, 7)));
        fDepartment.setText(valueOf(table.getValueAt(row, 8)));
        fDesignation.setText(valueOf(table.getValueAt(row, 9)));
        fSpecialization.setText(valueOf(table.getValueAt(row, 10)));
    }

    private String valueOf(Object v) {
        return v == null ? "" : String.valueOf(v);
    }

    private void doSearch(String keyword) {
        SwingWorker<List<Faculty>, Void> worker = new SwingWorker<List<Faculty>, Void>() {
            @Override
            protected List<Faculty> doInBackground() throws Exception {
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
            service.create(readFacultyId(), readEmployeeId(), readFullName(), readEmail(),
                    readPhone(), readAddress(), readDob(), readDepartment(),
                    readDesignation(), readSpecialization());
            showSuccess("Faculty member saved successfully.");
            clearForm();
            refreshData();
        } catch (AppException ex) {
            showError(ex.getMessage());
        }
    }

    private void doUpdate() {
        if (selectedId < 0) {
            showWarning("Select a faculty member from the table to update.");
            return;
        }
        try {
            service.update(selectedId, readFacultyId(), readEmployeeId(), readFullName(),
                    readEmail(), readPhone(), readAddress(), readDob(), readDepartment(),
                    readDesignation(), readSpecialization());
            showSuccess("Faculty member updated successfully.");
            clearForm();
            refreshData();
        } catch (AppException ex) {
            showError(ex.getMessage());
        }
    }

    private void doDelete() {
        if (selectedId < 0) {
            showWarning("Select a faculty member from the table to delete.");
            return;
        }
        String name = fFullName.getText();
        if (!confirm("Delete faculty member \"" + name + "\"?\n"
                + "Allowed only if no courses are assigned to them.", "Confirm Delete")) {
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
                        showSuccess("Faculty member deleted.");
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

    private void fillTable(List<Faculty> list) {
        Object[][] data = new Object[list.size()][11];
        for (int i = 0; i < list.size(); i++) {
            Faculty f = list.get(i);
            data[i] = new Object[] {
                f.getId(), f.getFacultyId(), f.getEmployeeId(), f.getFullName(),
                f.getEmail(), f.getPhone(), f.getAddress(),
                DateUtils.display(f.getDateOfBirth()), f.getDepartment(),
                f.getDesignation(), f.getSpecialization()
            };
        }
        replaceTableModel(table, data,
            new String[] { "ID", "Faculty ID", "Employee ID", "Full Name", "Email", "Phone",
                "Address", "DOB", "Department", "Designation", "Specialization" });
        if (list.isEmpty()) {
            showWarning("No faculty members found.");
        }
    }

    private void clearForm() {
        selectedId = -1;
        fFacultyId.setText(""); fEmployeeId.setText(""); fFullName.setText("");
        fEmail.setText(""); fPhone.setText(""); fAddress.setText("");
        fDob.setText(""); fDepartment.setText(""); fDesignation.setText("");
        fSpecialization.setText("");
        table.clearSelection();
    }

    private String readFacultyId() { return fFacultyId.getText(); }
    private String readEmployeeId() { return fEmployeeId.getText(); }
    private String readFullName() { return fFullName.getText(); }
    private String readEmail() { return fEmail.getText(); }
    private String readPhone() { return fPhone.getText(); }
    private String readAddress() { return fAddress.getText(); }
    private String readDob() { return fDob.getText(); }
    private String readDepartment() { return fDepartment.getText(); }
    private String readDesignation() { return fDesignation.getText(); }
    private String readSpecialization() { return fSpecialization.getText(); }
}

