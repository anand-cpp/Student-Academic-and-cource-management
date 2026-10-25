package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import model.Course;
import service.CourseService;
import util.AppException;
import util.InputValidator;
import ui.components.FormField;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import ui.components.SectionHeader;
import util.UIStyles;

/**
 * Course management panel: add, view, search, update, delete courses and assign faculty.
 */
public class CoursePanel extends BasePanel {

    private static final long serialVersionUID = 1L;

    private final CourseService service = new CourseService();

    private FormField fCourseCode, fCourseName, fDepartment, fSemester,
            fCredits, fMaximumMarks, fPassingMarks, fAssignedFacultyId;

    private JTable table;
    private int selectedId = -1;

    public CoursePanel(MainFrame main) {
        super(main);
        buildUI();
        refreshData();
    }

    private void buildUI() {
        JPanel header = sectionHeader("Courses",
                "Manage courses, credits, marks, and faculty assignment.");

        JPanel toolbar = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 12, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 12, 0));

        JTextField search = new JTextField();
        search.setFont(UIStyles.BODY);
        search.setBorder(new javax.swing.border.LineBorder(UIStyles.LINE, 1, true));
        search.setPreferredSize(new Dimension(340, 40));
        search.setToolTipText("Search by course code, name or department");

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

        fCourseCode = new FormField("CS101");
        fCourseName = new FormField("Database Systems");
        fDepartment = new FormField("Computer Science");
        fSemester = new FormField("3");
        fCredits = new FormField("4");
        fMaximumMarks = new FormField("100");
        fPassingMarks = new FormField("40");
        fAssignedFacultyId = new FormField("1 (optional)");

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
        addFormRow(formCard, g, "Course Code *", fCourseCode);
        addFormRow(formCard, g, "Course Name *", fCourseName);
        addFormRow(formCard, g, "Department *", fDepartment);
        addFormRow(formCard, g, "Semester *", fSemester);
        addFormRow(formCard, g, "Credits *", fCredits);
        addFormRow(formCard, g, "Maximum Marks *", fMaximumMarks);
        addFormRow(formCard, g, "Passing Marks *", fPassingMarks);
        addFormRow(formCard, g, "Assigned Faculty ID (optional)", fAssignedFacultyId);
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
        replaceTableData(table, data,
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




