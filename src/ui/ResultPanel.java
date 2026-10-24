package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import dao.EnrollmentDAO;
import model.Enrollment;
import service.ResultService;
import util.AppException;
import util.UIStyles;

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

    private JComboBox<EnrollmentPanel.ComboItem> cboEnrollments;
    private JTextField fMarks;
    private JTable table;

    public ResultPanel(MainFrame main) {
        super(main);
        buildUI();
        refreshData();
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIStyles.CARD);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIStyles.LINE, 1), "Enter / Update Marks",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14), UIStyles.PRIMARY));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 7, 7, 7);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0;
        g.gridy = 0;

        cboEnrollments = new JComboBox<>();
        fMarks = new JTextField();
        fMarks.setFont(UIStyles.BODY_FONT);
        fMarks.setBorder(UIStyles.line());
        fMarks.setToolTipText("Marks obtained (0 to course maximum)");

        JLabel lblEnr = new JLabel("Enrollment *");
        lblEnr.setFont(UIStyles.BODY_FONT);
        JLabel lblMarks = new JLabel("Marks Obtained *");
        lblMarks.setFont(UIStyles.BODY_FONT);

        g.gridwidth = 2;
        addField(form, g, lblEnr, cboEnrollments);
        addField(form, g, lblMarks, fMarks);
        g.gridwidth = 1;

        JButton btnLoad = button("Load Enrollments", UIStyles.PRIMARY, e -> loadEnrollments());
        JButton btnSave = button("Save Result", UIStyles.SUCCESS, e -> doSave());
        JButton btnRefresh = button("Refresh", UIStyles.PRIMARY_DARK, e -> refreshData());

        JPanel btns = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 6));
        btns.setBackground(UIStyles.CARD);
        btns.add(btnLoad);
        btns.add(btnSave);
        btns.add(btnRefresh);

        g.gridx = 0;
        g.gridy++;
        g.gridwidth = 4;
        form.add(btns, g);

        JPanel formWrap = new JPanel(new BorderLayout());
        formWrap.setBackground(UIStyles.CARD);
        formWrap.add(form, BorderLayout.NORTH);
        add(formWrap, BorderLayout.NORTH);

        table = new JTable();
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(UIStyles.line());
        add(scroll, BorderLayout.CENTER);
    }

    private void addField(JPanel panel, GridBagConstraints g, JLabel label, java.awt.Component field) {
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
                    cboEnrollments.setModel(new DefaultComboBoxModel<>(
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
                    UIStyles.styleTable(table);
                } catch (Exception ex) {
                    // ignore
                }
            }
        };
        worker.execute();
    }
}

