package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import model.Course;
import service.CourseService;
import ui.components.AppIcon;
import ui.components.DataCard;
import ui.components.FormField;
import ui.components.FormPanel;
import ui.components.RoundedButton;
import ui.components.SearchField;
import ui.components.SectionHeader;
import ui.components.TableCard;
import util.AppException;
import util.InputValidator;
import util.UIStyles;

/**
 * Course catalogue: codes, credits, assessment bounds and faculty assignment.
 */
public class CoursePanel extends BasePanel implements MainFrame.Refreshable {

    private static final long serialVersionUID = 1L;

    private final CourseService service = new CourseService();

    private FormField fCourseCode, fCourseName, fDepartment, fSemester,
            fCredits, fMaximumMarks, fPassingMarks, fAssignedFacultyId;

    private JTable table;
    private TableCard tableCard;
    private SearchField search;
    private int selectedId = -1;
    private List<Course> displayed = List.of();

    public CoursePanel(MainFrame main) {
        super(main);
        buildUI();
        refreshData();
    }

    private void buildUI() {
        SectionHeader head = header("Module 04 \u00B7 Curriculum", "Courses",
                "The course catalogue: codes, credits, assessment bounds and faculty assignment.");
        add(head, BorderLayout.NORTH);

        table = buildTable(new Object[0][0],
                new String[]{"Code", "Course", "Dept.", "Sem.", "Credits"});
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                loadSelectedRow();
            }
        });

        tableCard = tableCard("Course catalogue", "Every course offered this term.",
                table, "The catalogue is empty",
                "Add the first course on the right. Codes must be unique.");
        RoundedButton cardRefresh = iconButton(AppIcon.Glyph.REFRESH, "Reload courses");
        cardRefresh.addActionListener(e -> refreshData());
        tableCard.getCard().addHeaderAction(cardRefresh);

        JPanel body = pageBody();

        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        search = new SearchField("Search by course code, name or department");
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

        RoundedButton addNew = new RoundedButton("New course", AppIcon.Glyph.PLUS);
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
        fCourseCode = new FormField("CS101");
        fCourseName = new FormField("Database Systems");
        fDepartment = new FormField("Computer Science");
        fSemester = new FormField("3");
        fCredits = new FormField("4");
        fMaximumMarks = new FormField("100");
        fPassingMarks = new FormField("40");
        fAssignedFacultyId = new FormField("1 (optional)");

        DataCard editor = card("Course details", "Select a row to edit, or enter a new course.");
        FormPanel form = new FormPanel();

        form.addRow(field("Course code *", fCourseCode, "Unique, e.g. CS101"));
        form.addRow(field("Course name *", fCourseName, null));
        form.addRow(field("Department *", fDepartment, null));

        JPanel pair1 = new JPanel(new GridLayout(1, 2, 10, 0));
        pair1.setOpaque(false);
        pair1.add(field("Semester *", fSemester, "1\u20138"));
        pair1.add(field("Credits *", fCredits, "1\u20136"));
        form.addRow(pair1);

        JPanel pair2 = new JPanel(new GridLayout(1, 2, 10, 0));
        pair2.setOpaque(false);
        pair2.add(field("Maximum marks *", fMaximumMarks, null));
        pair2.add(field("Passing marks *", fPassingMarks, null));
        form.addRow(pair2);

        form.addRow(field("Assigned faculty ID", fAssignedFacultyId, "Optional \u00B7 must exist in faculty"), 18);

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
        form.addRow(actions, 10);

        JPanel note = new JPanel(new BorderLayout());
        note.setOpaque(false);
        javax.swing.JLabel noteText = new javax.swing.JLabel(
                "<html>Passing marks cannot exceed maximum marks.<br>Faculty IDs are validated by the service.</html>");
        noteText.setFont(UIStyles.CAPTION);
        noteText.setForeground(UIStyles.MUTED);
        note.add(noteText, BorderLayout.WEST);
        form.addRow(note, 0);
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
        Course c = displayed.get(table.convertRowIndexToModel(row));
        selectedId = c.getId();
        fCourseCode.setText(valueOf(c.getCourseCode()));
        fCourseName.setText(valueOf(c.getCourseName()));
        fDepartment.setText(valueOf(c.getDepartment()));
        fSemester.setText(valueOf(c.getSemester()));
        fCredits.setText(valueOf(c.getCredits()));
        fMaximumMarks.setText(valueOf(c.getMaximumMarks()));
        fPassingMarks.setText(valueOf(c.getPassingMarks()));
        fAssignedFacultyId.setText(c.getAssignedFacultyId() == null
                ? "" : valueOf(c.getAssignedFacultyId()));
    }

    private String valueOf(Object v) {
        return v == null ? "" : String.valueOf(v);
    }

    /* ---------- Operations ---------- */

    private void doSearch(String keyword, boolean notify) {
        new SwingWorker<List<Course>, Void>() {
            @Override
            protected List<Course> doInBackground() throws Exception {
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
            validateMarks();
            service.create(readCourseCode(), readCourseName(), readDepartment(), readSemester(),
                    readCredits(), fMaximumMarks.getText(), fPassingMarks.getText(),
                    fAssignedFacultyId.getText());
            showSuccess("Course \u201C" + readCourseCode() + "\u201D added to the catalogue.");
            clearForm();
            refreshData();
        } catch (AppException ex) {
            showError(ex.getMessage());
        }
    }

    private void doUpdate() {
        if (selectedId < 0) {
            showWarning("Select a course from the catalogue first.");
            return;
        }
        try {
            validateMarks();
            service.update(selectedId, readCourseCode(), readCourseName(), readDepartment(),
                    readSemester(), readCredits(), fMaximumMarks.getText(), fPassingMarks.getText(),
                    fAssignedFacultyId.getText());
            showSuccess("Course updated.");
            clearForm();
            refreshData();
        } catch (AppException ex) {
            showError(ex.getMessage());
        }
    }

    private void validateMarks() throws AppException {
        BigDecimal max = InputValidator.decimal(fMaximumMarks.getText(), "Maximum marks");
        BigDecimal pass = InputValidator.decimal(fPassingMarks.getText(), "Passing marks");
        if (pass.compareTo(max) > 0) {
            throw new AppException("OUT_OF_RANGE", "Passing marks cannot exceed maximum marks.");
        }
    }

    private void doDelete() {
        if (selectedId < 0) {
            showWarning("Select a course from the catalogue first.");
            return;
        }
        String name = fCourseName.getText();
        if (!confirmDanger("Delete course \u201C" + name + "\"?\n"
                + "This is only allowed when no enrollments, attendance or results reference it.",
                "Delete course")) {
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
                        showSuccess("Course deleted.");
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

    @Override
    public void refreshData() {
        doSearch("", false);
    }

    private void fillTable(List<Course> list, boolean notify) {
        displayed = list;
        Object[][] data = new Object[list.size()][5];
        for (int i = 0; i < list.size(); i++) {
            Course c = list.get(i);
            data[i] = new Object[]{
                c.getCourseCode(), c.getCourseName(), c.getDepartment(),
                c.getSemester(), c.getCredits()
            };
        }
        replaceTableData(table, data,
                new String[]{"Code", "Course", "Dept.", "Sem.", "Credits"});
        setColumnWidths(table, 68, 158, 118, 44, 60);
        tableCard.setSubtitle(list.isEmpty()
                ? "No courses match the current search."
                : "Every course offered this term.");
        tableCard.setCount(list.size(), "course");
        tableCard.sync();
        if (notify && list.isEmpty()) {
            showInfo("No courses match that search.");
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
        fCourseCode.setText("");
        fCourseName.setText("");
        fDepartment.setText("");
        fSemester.setText("");
        fCredits.setText("");
        fMaximumMarks.setText("");
        fPassingMarks.setText("");
        fAssignedFacultyId.setText("");
        table.clearSelection();
        fCourseCode.requestFocusInWindow();
    }

    private String message(Exception ex) {
        return ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
    }

    private String readCourseCode() {
        return fCourseCode.getText();
    }

    private String readCourseName() {
        return fCourseName.getText();
    }

    private String readDepartment() {
        return fDepartment.getText();
    }

    private String readSemester() {
        return fSemester.getText();
    }

    private String readCredits() {
        return fCredits.getText();
    }
}
