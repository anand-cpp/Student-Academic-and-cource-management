package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import dao.EnrollmentDAO;
import model.Enrollment;
import service.ResultService;
import util.AppException;
import util.UIStyles;
import ui.components.FormField;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import ui.components.SectionHeader;

/**
 * Marks and academic results panel.
 *
 * <p>Select an enrollment, enter marks, and save. The service validates marks
 * against the course maximum, computes the percentage with BigDecimal and
 * assigns a grade using the documented grading scale. One final result per
 * enrollment.
 */
public class ResultPanel extends BasePanel implements MainFrame.Refreshable {

    private static final long serialVersionUID = 1L;

    private final ResultService service = new ResultService();

    private javax.swing.JComboBox<EnrollmentPanel.ComboItem> cboEnrollments;
    private JTextField fMarks;
    private JTable table;

    public ResultPanel(MainFrame main) {
        super(main);
        buildUI();
        refreshData();
    }

    private void buildUI() {
        JPanel header = sectionHeader("Results",
                "Enter marks, calculate percentages and grades.");

        RoundedPanel form = new RoundedPanel();
        form.setRadius(12);
        form.setShadow(false);
        form.setFill(UIStyles.CARD);
        form.setLayout(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 16, 16, 16));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0;
        g.gridy = 0;

        cboEnrollments = new javax.swing.JComboBox<>();
        fMarks = new JTextField();
        fMarks.setFont(UIStyles.BODY);
        fMarks.setBorder(new javax.swing.border.LineBorder(UIStyles.LINE, 1, true));
        fMarks.setToolTipText("Marks obtained (0 to course maximum)");

        JLabel lblEnr = UIStyles.fieldLabel("Enrollment *");
        JLabel lblMarks = UIStyles.fieldLabel("Marks Obtained *");

        g.gridwidth = 2;
        addField(form, g, lblEnr, cboEnrollments);
        addField(form, g, lblMarks, fMarks);
        g.gridwidth = 1;

        RoundedButton btnLoad = new RoundedButton("Load Enrollments");
        btnLoad.applyPrimary();
        btnLoad.addActionListener(e -> loadEnrollments());

        RoundedButton btnSave = new RoundedButton("Save Result");
        btnSave.applySuccess();
        btnSave.addActionListener(e -> doSave());

        RoundedButton btnRefresh = new RoundedButton("Refresh");
        btnRefresh.applyGhost();
        btnRefresh.addActionListener(e -> refreshData());

        JPanel btns = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 6));
        btns.setOpaque(false);
        btns.add(btnLoad);
        btns.add(btnSave);
        btns.add(btnRefresh);

        g.gridx = 0;
        g.gridy++;
        g.gridwidth = 4;
        form.add(btns, g);

        JPanel formWrap = new JPanel(new BorderLayout());
        formWrap.setOpaque(false);
        formWrap.add(form, BorderLayout.NORTH);

        table = new JTable();
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UIStyles.CARD);
        styleTable(table);

        setLayout(new BorderLayout(0, 0));
        add(header, BorderLayout.NORTH);
        add(formWrap, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // Fix layout: header already added, formWrap below it
        setLayout(new BorderLayout(0, 0));
        add(header, BorderLayout.NORTH);
        JPanel topWrap = new JPanel(new BorderLayout());
        topWrap.setOpaque(false);
        topWrap.add(formWrap, BorderLayout.NORTH);
        add(topWrap, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void addField(JPanel panel, GridBagConstraints g, javax.swing.JLabel label, java.awt.Component field) {
        g.gridx = 0;
        panel.add(label, g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        panel.add(field, g);
        g.gridx = 0;
        g.gridy++;
        g.fill = GridBagConstraints.NONE;
        g.weightx = 0;
    }

    private void loadEnrollments() {
        SwingWorker<List<EnrollmentPanel.ComboItem>, Void> worker =
                new SwingWorker<List<EnrollmentPanel.ComboItem>, Void>() {
            @Override
            protected List<EnrollmentPanel.ComboItem> doInBackground() throws Exception {
                List<EnrollmentPanel.ComboItem> list = new ArrayList<>();
                EnrollmentDAO dao = new EnrollmentDAO();
                for (Enrollment e : dao.findAll()) {
                    list.add(new EnrollmentPanel.ComboItem(e.getId(),
                            "Enrollment #" + e.getId() + " (student " + e.getStudentId()
                            + ", course " + e.getCourseId() + ")"));
                }
                return list;
            }

            @Override
            protected void done() {
                try {
                    List<EnrollmentPanel.ComboItem> list = get();
                    cboEnrollments.setModel(new javax.swing.DefaultComboBoxModel<>(
                            list.toArray(new EnrollmentPanel.ComboItem[0])));
                } catch (Exception ex) {
                    showError("Failed to load enrollments: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void doSave() {
        EnrollmentPanel.ComboItem item = (EnrollmentPanel.ComboItem) cboEnrollments.getSelectedItem();
        if (item == null) {
            showWarning("Select an enrollment.");
            return;
        }
        String marks = fMarks.getText().trim();
        if (marks.isEmpty()) {
            showWarning("Enter marks obtained.");
            return;
        }
        SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
            private String error;

            @Override
            protected Integer doInBackground() throws Exception {
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
                        showSuccess("Result saved (percentage and grade calculated).");
                        fMarks.setText("");
                        refreshData();
                    } else {
                        showError(error);
                    }
                } catch (Exception ex) {
                    showError("Save failed: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    @Override
    public void refreshData() {
        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<List<Map<String, Object>>, Void>() {
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
                        data[i] = new Object[] {
                            r.get("studentCode"), r.get("studentName"), r.get("courseCode"),
                            r.get("courseName"), r.get("marksObtained"),
                            pct == null ? "-" : pct + "%", r.get("grade")
                        };
                    }
                    table.setModel(new javax.swing.table.DefaultTableModel(
                            data, new String[] { "Student ID", "Student", "Course Code",
                                "Course", "Marks", "Percentage", "Grade" }));
                    styleTable(table);
                } catch (Exception ex) {
                    // ignore
                }
            }
        };
        worker.execute();
    }

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




