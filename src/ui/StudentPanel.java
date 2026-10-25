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
import model.Student;
import service.StudentService;
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
import util.UIStyles;

/**
 * Student management: search the register, add a record, or select a row to
 * update and delete. The table and the editor share one page.
 */
public class StudentPanel extends BasePanel {

    private static final long serialVersionUID = 1L;

    private final StudentService service = new StudentService();

    private FormField fStudentId, fRollNumber, fFullName, fEmail, fPhone,
            fAddress, fDob, fDepartment, fSemester, fAdmissionDate;

    private JTable table;
    private TableCard tableCard;
    private SearchField search;
    private int selectedId = -1;
    private List<Student> displayed = List.of();

    public StudentPanel(MainFrame main) {
        super(main);
        buildUI();
        refreshData();
    }

    private void buildUI() {
        SectionHeader head = header("Module 02 \u00B7 People", "Students",
                "The student register: identity, contact details, department and semester.");
        add(head, BorderLayout.NORTH);

        table = buildTable(new Object[0][0],
                new String[]{"Student ID", "Name", "Department", "Sem."});
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                loadSelectedRow();
            }
        });

        tableCard = tableCard("Student register", "Every record currently in the database.",
                table, "No students on file",
                "Add the first student with the form on the right, or clear the search to see everyone.");
        RoundedButton cardRefresh = iconButton(AppIcon.Glyph.REFRESH, "Reload students");
        cardRefresh.addActionListener(e -> refreshData());
        tableCard.getCard().addHeaderAction(cardRefresh);

        JPanel body = pageBody();

        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        search = new SearchField("Search by ID, roll, name, email or department");
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

        RoundedButton addNew = new RoundedButton("New student", AppIcon.Glyph.PLUS);
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

        DataCard editor = card("Student details", "Select a row to edit, or enter a new record.");
        FormPanel form = new FormPanel();

        form.addRow(field("Student ID *", fStudentId, "Unique code, e.g. STU0001"));
        form.addRow(field("Roll number *", fRollNumber, null));
        form.addRow(field("Full name *", fFullName, null));
        form.addRow(field("Email *", fEmail, null));
        form.addRow(field("Phone", fPhone, null));
        form.addRow(field("Address", fAddress, null));

        JPanel pair = new JPanel(new GridLayout(1, 2, 10, 0));
        pair.setOpaque(false);
        pair.add(field("Date of birth", fDob, "yyyy-MM-dd"));
        pair.add(field("Semester *", fSemester, "1\u20138"));
        form.addRow(pair);
        form.addRow(field("Department *", fDepartment, null));
        form.addRow(field("Admission date", fAdmissionDate, "yyyy-MM-dd"), 18);

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
        editor.setPreferredSize(new Dimension(378, 0));
        editor.setMinimumSize(new Dimension(378, 0));

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
        Student s = displayed.get(table.convertRowIndexToModel(row));
        selectedId = s.getId();
        fStudentId.setText(valueOf(s.getStudentId()));
        fRollNumber.setText(valueOf(s.getRollNumber()));
        fFullName.setText(valueOf(s.getFullName()));
        fEmail.setText(valueOf(s.getEmail()));
        fPhone.setText(valueOf(s.getPhone()));
        fAddress.setText(valueOf(s.getAddress()));
        fDob.setText(s.getDateOfBirth() == null ? "" : DateUtils.display(s.getDateOfBirth()));
        fDepartment.setText(valueOf(s.getDepartment()));
        fSemester.setText(valueOf(s.getSemester()));
        fAdmissionDate.setText(s.getAdmissionDate() == null
                ? "" : DateUtils.display(s.getAdmissionDate()));
    }

    private String valueOf(Object v) {
        return v == null ? "" : String.valueOf(v);
    }

    /* ---------- Operations ---------- */

    private void doSearch(String keyword, boolean notify) {
        new SwingWorker<List<Student>, Void>() {
            @Override
            protected List<Student> doInBackground() throws Exception {
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
            service.create(readStudentId(), readRollNumber(), readFullName(), readEmail(),
                    readPhone(), readAddress(), readDob(), readDepartment(),
                    readSemester(), readAdmissionDate());
            showSuccess("Student \u201C" + readFullName() + "\u201D added to the register.");
            clearForm();
            refreshData();
        } catch (AppException ex) {
            showError(ex.getMessage());
        }
    }

    private void doUpdate() {
        if (selectedId < 0) {
            showWarning("Select a student from the register first.");
            return;
        }
        try {
            service.update(selectedId, readStudentId(), readRollNumber(), readFullName(),
                    readEmail(), readPhone(), readAddress(), readDob(), readDepartment(),
                    readSemester(), readAdmissionDate());
            showSuccess("Student record updated.");
            clearForm();
            refreshData();
        } catch (AppException ex) {
            showError(ex.getMessage());
        }
    }

    private void doDelete() {
        if (selectedId < 0) {
            showWarning("Select a student from the register first.");
            return;
        }
        String name = fFullName.getText();
        if (!confirmDanger("Delete student \u201C" + name + "\"?\n"
                + "This is only allowed when the student has no enrollments.",
                "Delete student")) {
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
                        showSuccess("Student record deleted.");
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

    private void fillTable(List<Student> students, boolean notify) {
        displayed = students;
        Object[][] data = new Object[students.size()][4];
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            data[i] = new Object[]{
                s.getStudentId(), s.getFullName(), s.getDepartment(), s.getSemester()
            };
        }
        replaceTableData(table, data,
                new String[]{"Student ID", "Name", "Department", "Sem."});
        setColumnWidths(table, 108, 178, 125, 54);
        tableCard.setSubtitle(students.isEmpty()
                ? "No records match the current search."
                : "Every record currently in the database.");
        tableCard.setCount(students.size(), "student");
        tableCard.sync();
        if (notify && students.isEmpty()) {
            showInfo("No students match that search.");
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
        fStudentId.setText("");
        fRollNumber.setText("");
        fFullName.setText("");
        fEmail.setText("");
        fPhone.setText("");
        fAddress.setText("");
        fDob.setText("");
        fDepartment.setText("");
        fSemester.setText("");
        fAdmissionDate.setText("");
        table.clearSelection();
        fStudentId.requestFocusInWindow();
    }

    private String nullToDash(String value) {
        return value == null || value.isBlank() ? "\u2014" : value;
    }

    private String message(Exception ex) {
        return ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
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
