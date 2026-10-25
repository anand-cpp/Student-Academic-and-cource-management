package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import dao.EnrollmentDAO;
import model.Enrollment;
import service.ResultService;
import ui.components.AppComboBox;
import ui.components.AppIcon;
import ui.components.DataCard;
import ui.components.FormField;
import ui.components.RoundedButton;
import ui.components.SectionHeader;
import ui.components.TableCard;
import util.AppException;
import util.UIStyles;

/**
 * Results desk.
 *
 * <p>Select an enrollment, enter marks and save. The service validates marks
 * against the course maximum, computes the percentage with BigDecimal and
 * assigns a grade. One final result per enrollment.
 */
public class ResultPanel extends BasePanel implements MainFrame.Refreshable {

    private static final long serialVersionUID = 1L;

    private final ResultService service = new ResultService();

    private AppComboBox<EnrollmentPanel.ComboItem> cboEnrollments;
    private FormField fMarks;
    private JTable table;
    private TableCard tableCard;

    public ResultPanel(MainFrame main) {
        super(main);
        buildUI();
        refreshData();
    }

    private void buildUI() {
        SectionHeader head = header("Module 07 \u00B7 Assessment", "Results",
                "Enter marks; percentages and grades are computed by the service.");
        add(head, BorderLayout.NORTH);

        JPanel body = pageBody();
        body.add(buildComposer(), BorderLayout.NORTH);

        table = buildTable(new Object[0][0],
                new String[]{"Student ID", "Student", "Course Code", "Course",
                    "Marks", "Percentage", "Grade"});
        tableCard = tableCard("Result ledger", "Final results stored for every enrollment.",
                table, "No results recorded",
                "Choose an enrollment above, enter the marks obtained, and save the result.");
        tableCard.setCount(0, "result");

        RoundedButton refresh = iconButton(AppIcon.Glyph.REFRESH, "Reload results");
        refresh.addActionListener(e -> refreshData());
        tableCard.getCard().addHeaderAction(refresh);

        body.add(tableCard, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    private JPanel buildComposer() {
        DataCard card = card("Record a result",
                "Marks are validated against the course maximum before the grade is assigned.");

        cboEnrollments = new AppComboBox<>();
        fMarks = new FormField("Marks obtained");
        fMarks.setToolTipText("Marks obtained (0 to course maximum)");

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
        form.add(field("Enrollment *", cboEnrollments, null), g);

        g.gridx = 1;
        g.weightx = 0.28;
        form.add(field("Marks obtained *", fMarks, "0 to max marks"), g);

        RoundedButton save = new RoundedButton("Save result", AppIcon.Glyph.SAVE);
        save.applyPrimary();
        save.setPreferredSize(new Dimension(146, 38));
        save.addActionListener(e -> doSave());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actions.setOpaque(false);
        actions.add(save);

        g.gridx = 2;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        g.insets = new Insets(20, 0, 0, 0);
        form.add(actions, g);

        card.setBody(form);
        return card;
    }

    /* ---------- Data ---------- */

    private void loadEnrollments() {
        new SwingWorker<List<EnrollmentPanel.ComboItem>, Void>() {
            @Override
            protected List<EnrollmentPanel.ComboItem> doInBackground() throws Exception {
                List<EnrollmentPanel.ComboItem> list = new ArrayList<>();
                for (Map<String, Object> r : new EnrollmentDAO().findAllSummary()) {
                    list.add(new EnrollmentPanel.ComboItem(((Number) r.get("id")).intValue(),
                            r.get("studentId") + " \u00B7 " + r.get("studentName")
                            + " \u00B7 " + r.get("courseCode")));
                }
                return list;
            }

            @Override
            protected void done() {
                try {
                    List<EnrollmentPanel.ComboItem> list = get();
                    EnrollmentPanel.ComboItem previous =
                            (EnrollmentPanel.ComboItem) cboEnrollments.getSelectedItem();
                    cboEnrollments.setModel(new javax.swing.DefaultComboBoxModel<>(
                            list.toArray(new EnrollmentPanel.ComboItem[0])));
                    if (previous != null) {
                        for (int i = 0; i < cboEnrollments.getItemCount(); i++) {
                            if (cboEnrollments.getItemAt(i).id == previous.id) {
                                cboEnrollments.setSelectedIndex(i);
                                break;
                            }
                        }
                    }
                } catch (Exception ex) {
                    showError("Failed to load enrollments: " + message(ex));
                }
            }
        }.execute();
    }

    private void doSave() {
        EnrollmentPanel.ComboItem item = (EnrollmentPanel.ComboItem) cboEnrollments.getSelectedItem();
        if (item == null) {
            showWarning("Select an enrollment first.");
            return;
        }
        String marks = fMarks.getText().trim();
        if (marks.isEmpty()) {
            showWarning("Enter the marks obtained.");
            return;
        }
        new SwingWorker<Integer, Void>() {
            private String error;

            @Override
            protected Integer doInBackground() {
                try {
                    return service.save(item.id, marks);
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
                        showSuccess("Result saved \u2014 percentage and grade calculated.");
                        fMarks.setText("");
                        refreshData();
                    } else {
                        showError(error);
                    }
                } catch (Exception ex) {
                    showError("Save failed: " + message(ex));
                }
            }
        }.execute();
    }

    @Override
    public void refreshData() {
        loadEnrollments();
        new SwingWorker<List<Map<String, Object>>, Void>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return service.findAllSummary();
            }

            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> rows = get();
                    Object[][] data = new Object[rows.size()][7];
                    for (int i = 0; i < rows.size(); i++) {
                        Map<String, Object> r = rows.get(i);
                        BigDecimal pct = (BigDecimal) r.get("percentage");
                        data[i] = new Object[]{
                            r.get("studentCode"), r.get("studentName"), r.get("courseCode"),
                            r.get("courseName"), r.get("marksObtained"),
                            pct == null ? "\u2014" : pct.setScale(2, java.math.RoundingMode.HALF_UP) + "%",
                            r.get("grade")
                        };
                    }
                    replaceTableData(table, data,
                            new String[]{"Student ID", "Student", "Course Code", "Course",
                                "Marks", "Percentage", "Grade"});
                    setColumnWidths(table, 100, 165, 100, 175, 75, 95, 65);
                    tableCard.setCount(rows.size(), "result");
                    tableCard.setSubtitle(rows.isEmpty()
                            ? "No results recorded yet."
                            : "Final results stored for every enrollment.");
                    tableCard.sync();
                } catch (Exception ex) {
                    showError("Could not load results: " + message(ex));
                }
            }
        }.execute();
    }

    private String message(Exception ex) {
        return ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
    }
}
