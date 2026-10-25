package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import model.Faculty;
import service.FacultyService;
import ui.components.AppIcon;
import ui.components.DataCard;
import ui.components.FormField;
import ui.components.FormPanel;
import ui.components.RoundedButton;
import ui.components.SearchField;
import ui.components.SectionHeader;
import ui.components.TableCard;
import util.AppException;
import util.DateUtils;

/**
 * Faculty management: maintain the teaching staff register and their
 * departments, designations and specialisations.
 */
public class FacultyPanel extends BasePanel {

    private static final long serialVersionUID = 1L;

    private final FacultyService service = new FacultyService();

    private FormField fFacultyId, fEmployeeId, fFullName, fEmail, fPhone,
            fAddress, fDob, fDepartment, fDesignation, fSpecialization;

    private JTable table;
    private TableCard tableCard;
    private SearchField search;
    private int selectedId = -1;
    private List<Faculty> displayed = List.of();

    public FacultyPanel(MainFrame main) {
        super(main);
        buildUI();
        refreshData();
    }

    private void buildUI() {
        SectionHeader head = header("Module 03 \u00B7 People", "Faculty",
                "The teaching roster: departments, designations and specialisations.");
        add(head, BorderLayout.NORTH);

        table = buildTable(new Object[0][0],
                new String[]{"Faculty ID", "Name", "Department"});
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                loadSelectedRow();
            }
        });

        tableCard = tableCard("Faculty register", "Teaching staff currently on record.",
                table, "No faculty on file",
                "Add the first faculty member on the right, or clear the search to see everyone.");
        RoundedButton cardRefresh = iconButton(AppIcon.Glyph.REFRESH, "Reload faculty");
        cardRefresh.addActionListener(e -> refreshData());
        tableCard.getCard().addHeaderAction(cardRefresh);

        JPanel body = pageBody();

        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        search = new SearchField("Search by ID, name, email or department");
        search.addActionListener(e -> doSearch(search.getText(), true));
        RoundedButton searchBtn = new RoundedButton("Search", AppIcon.Glyph.SEARCH);
        searchBtn.applyOutline();
        searchBtn.addActionListener(e -> doSearch(search.getText(), true));
        RoundedButton refresh = new RoundedButton("Refresh", AppIcon.Glyph.REFRESH);
        refresh.applyGhost();
        refresh.addActionListener(e -> refreshData());
        left.add(search);
        left.add(searchBtn);
        left.add(refresh);

        RoundedButton addNew = new RoundedButton("New faculty", AppIcon.Glyph.PLUS);
        addNew.applyPrimary();
        addNew.addActionListener(e -> clearForm());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(addNew);

        toolbar.add(left, BorderLayout.WEST);
        toolbar.add(right, BorderLayout.EAST);

        JPanel split = new JPanel(new BorderLayout(16, 0));
        split.setOpaque(false);
        split.add(tableCard, BorderLayout.CENTER);
        split.add(buildEditor(), BorderLayout.EAST);

        body.add(toolbar, BorderLayout.NORTH);
        body.add(split, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    private JPanel buildEditor() {
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

        DataCard editor = card("Faculty details", "Select a row to edit, or enter a new record.");
        FormPanel form = new FormPanel();

        form.addRow(field("Faculty ID *", fFacultyId, "Unique code, e.g. FAC0001"));
        form.addRow(field("Employee ID *", fEmployeeId, null));
        form.addRow(field("Full name *", fFullName, null));
        form.addRow(field("Email *", fEmail, null));
        form.addRow(field("Phone", fPhone, null));
        form.addRow(field("Address", fAddress, null));
        form.addRow(field("Date of birth", fDob, "yyyy-MM-dd"));

        JPanel pair = new JPanel(new GridLayout(1, 2, 10, 0));
        pair.setOpaque(false);
        pair.add(field("Department *", fDepartment, null));
        pair.add(field("Designation *", fDesignation, null));
        form.addRow(pair);
        form.addRow(field("Specialization *", fSpecialization, null), 18);

        JPanel actions = new JPanel(new GridLayout(2, 2, 8, 8));
        actions.setOpaque(false);
        RoundedButton save = new RoundedButton("Save", AppIcon.Glyph.SAVE);
        save.applyPrimary();
        save.addActionListener(e -> doSave());
        RoundedButton update = new RoundedButton("Update", AppIcon.Glyph.EDIT);
        update.applyOutline();
        update.addActionListener(e -> doUpdate());
        RoundedButton delete = new RoundedButton("Delete", AppIcon.Glyph.TRASH);
        delete.applyDanger();
        delete.addActionListener(e -> doDelete());
        RoundedButton reset = new RoundedButton("Reset", AppIcon.Glyph.RESET);
        reset.applyGhost();
        reset.addActionListener(e -> clearForm());
        actions.add(save);
        actions.add(update);
        actions.add(delete);
        actions.add(reset);
        form.addRow(actions, 0);
        form.addGlue();

        JScrollPane scroll = scroll(form);
        scroll.setPreferredSize(new Dimension(342, 0));
        editor.setBody(scroll);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setPreferredSize(new Dimension(378, 0));
        wrap.add(editor, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel strut(int height) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(0, height));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        p.setMinimumSize(new Dimension(0, height));
        return p;
    }

    private void loadSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        Faculty f = displayed.get(table.convertRowIndexToModel(row));
        selectedId = f.getId();
        fFacultyId.setText(valueOf(f.getFacultyId()));
        fEmployeeId.setText(valueOf(f.getEmployeeId()));
        fFullName.setText(valueOf(f.getFullName()));
        fEmail.setText(valueOf(f.getEmail()));
        fPhone.setText(valueOf(f.getPhone()));
        fAddress.setText(valueOf(f.getAddress()));
        fDob.setText(f.getDateOfBirth() == null ? "" : DateUtils.display(f.getDateOfBirth()));
        fDepartment.setText(valueOf(f.getDepartment()));
        fDesignation.setText(valueOf(f.getDesignation()));
        fSpecialization.setText(valueOf(f.getSpecialization()));
    }

    private String valueOf(Object v) {
        return v == null ? "" : String.valueOf(v);
    }

    /* ---------- Operations ---------- */

    private void doSearch(String keyword, boolean notify) {
        new SwingWorker<List<Faculty>, Void>() {
            @Override
            protected List<Faculty> doInBackground() throws Exception {
                return service.search(keyword == null ? "" : keyword.trim());
            }

            @Override
            protected void done() {
                try {
                    fillTable(get(), notify);
                } catch (Exception ex) {
                    showError("Search failed: " + message(ex));
                }
            }
        }.execute();
    }

    private void doSave() {
        try {
            service.create(readFacultyId(), readEmployeeId(), readFullName(), readEmail(),
                    readPhone(), readAddress(), readDob(), readDepartment(),
                    readDesignation(), readSpecialization());
            showSuccess("Faculty member \u201C" + readFullName() + "\u201D added.");
            clearForm();
            refreshData();
        } catch (AppException ex) {
            showError(ex.getMessage());
        }
    }

    private void doUpdate() {
        if (selectedId < 0) {
            showWarning("Select a faculty member from the register first.");
            return;
        }
        try {
            service.update(selectedId, readFacultyId(), readEmployeeId(), readFullName(),
                    readEmail(), readPhone(), readAddress(), readDob(), readDepartment(),
                    readDesignation(), readSpecialization());
            showSuccess("Faculty record updated.");
            clearForm();
            refreshData();
        } catch (AppException ex) {
            showError(ex.getMessage());
        }
    }

    private void doDelete() {
        if (selectedId < 0) {
            showWarning("Select a faculty member from the register first.");
            return;
        }
        String name = fFullName.getText();
        if (!confirmDanger("Delete faculty member \u201C" + name + "\"?\n"
                + "This is only allowed when no courses are assigned to them.",
                "Delete faculty member")) {
            return;
        }
        new SwingWorker<Boolean, Void>() {
            private String error;

            @Override
            protected Boolean doInBackground() {
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
                        showSuccess("Faculty record deleted.");
                        clearForm();
                        refreshData();
                    } else {
                        showError(error);
                    }
                } catch (Exception ex) {
                    showError("Delete failed: " + message(ex));
                }
            }
        }.execute();
    }

    /* ---------- Helpers ---------- */

    private void refreshData() {
        doSearch("", false);
    }

    private void fillTable(List<Faculty> list, boolean notify) {
        displayed = list;
        Object[][] data = new Object[list.size()][3];
        for (int i = 0; i < list.size(); i++) {
            Faculty f = list.get(i);
            data[i] = new Object[]{
                f.getFacultyId(), f.getFullName(), f.getDepartment()
            };
        }
        replaceTableData(table, data,
                new String[]{"Faculty ID", "Name", "Department"});
        setColumnWidths(table, 108, 180, 190);
        tableCard.setSubtitle(list.isEmpty()
                ? "No records match the current search."
                : "Teaching staff currently on record.");
        tableCard.setCount(list.size(), "faculty member");
        tableCard.sync();
        if (notify && list.isEmpty()) {
            showInfo("No faculty match that search.");
        }
    }

    private void hideColumns(JTable table, String... names) {
        for (String name : names) {
            javax.swing.table.TableColumn column = table.getColumn(name);
            if (column != null) {
                table.removeColumn(column);
            }
        }
    }

    private void clearForm() {
        selectedId = -1;
        fFacultyId.setText("");
        fEmployeeId.setText("");
        fFullName.setText("");
        fEmail.setText("");
        fPhone.setText("");
        fAddress.setText("");
        fDob.setText("");
        fDepartment.setText("");
        fDesignation.setText("");
        fSpecialization.setText("");
        table.clearSelection();
        fFacultyId.requestFocusInWindow();
    }

    private String nullToDash(String value) {
        return value == null || value.isBlank() ? "\u2014" : value;
    }

    private String message(Exception ex) {
        return ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
    }

    private String readFacultyId() {
        return fFacultyId.getText();
    }

    private String readEmployeeId() {
        return fEmployeeId.getText();
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

    private String readDesignation() {
        return fDesignation.getText();
    }

    private String readSpecialization() {
        return fSpecialization.getText();
    }
}
