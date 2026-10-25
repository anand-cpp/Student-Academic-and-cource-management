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
import model.Faculty;
import service.FacultyService;
import util.AppException;
import util.DateUtils;
import util.InputValidator;
import ui.components.FormField;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import ui.components.SectionHeader;
import util.UIStyles;

/**
 * Faculty management panel: add, view, search, update, delete faculty members.
 */
public class FacultyPanel extends BasePanel {

    private static final long serialVersionUID = 1L;

    private final FacultyService service = new FacultyService();

    private FormField fFacultyId, fEmployeeId, fFullName, fEmail, fPhone,
            fAddress, fDob, fDepartment, fDesignation, fSpecialization;

    private JTable table;
    private int selectedId = -1;

    public FacultyPanel(MainFrame main) {
        super(main);
        buildUI();
        refreshData();
    }

    private void buildUI() {
        JPanel header = sectionHeader("Faculty",
                "Manage faculty members and course assignments.");

        JPanel toolbar = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 12, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 12, 0));

        JTextField search = new JTextField();
        search.setFont(UIStyles.BODY);
        search.setBorder(new javax.swing.border.LineBorder(UIStyles.LINE, 1, true));
        search.setPreferredSize(new Dimension(340, 40));
        search.setToolTipText("Search by faculty ID, employee ID, name, email or department");

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

        fFacultyId = new FormField("FAC0001");
        fEmployeeId = new FormField("EMP0001");
        fFullName = new FormField("Dr. Anita Sharma");
        fEmail = new FormField("anita@college.edu");
        fPhone = new FormField("+91 98765 43210");
        fAddress = new FormField("12, College Road, Delhi");
        fDob = new FormField("1980-05-12");
        fDepartment = new FormField("Computer Science");
        fDesignation = new FormField("Professor");
        fSpecialization = new FormField("Databases");

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
        addFormRow(formCard, g, "Faculty ID *", fFacultyId);
        addFormRow(formCard, g, "Employee ID *", fEmployeeId);
        addFormRow(formCard, g, "Full Name *", fFullName);
        addFormRow(formCard, g, "Email *", fEmail);
        addFormRow(formCard, g, "Phone", fPhone);
        addFormRow(formCard, g, "Address", fAddress);
        addFormRow(formCard, g, "Date of Birth (yyyy-MM-dd)", fDob);
        addFormRow(formCard, g, "Department *", fDepartment);
        addFormRow(formCard, g, "Designation *", fDesignation);
        addFormRow(formCard, g, "Specialization *", fSpecialization);
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

        JPanel formWrap = new JPanel(new BorderLayout());
        formWrap.setOpaque(false);
        formWrap.setPreferredSize(new Dimension(420, 0));
        formWrap.add(formCard, BorderLayout.NORTH);

        table = new JTable();
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UIStyles.CARD);
        styleTable(table);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || table.getSelectedRow() < 0) {
                return;
            }
            loadSelectedRow();
        });

        JPanel content = new JPanel(new BorderLayout(16, 16));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(0, 20, 20, 20));
        content.add(formWrap, BorderLayout.WEST);
        content.add(scroll, BorderLayout.CENTER);

        setLayout(new BorderLayout(0, 0));
        add(header, BorderLayout.NORTH);
        add(toolbar, BorderLayout.SOUTH);
        add(content, BorderLayout.CENTER);
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
        replaceTableData(table, data,
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




