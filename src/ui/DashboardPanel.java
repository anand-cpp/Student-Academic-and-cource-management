package ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import service.ReportService;
import ui.components.AppIcon;
import ui.components.AppScrollBarUI;
import ui.components.RoundedButton;
import ui.components.SectionHeader;
import ui.components.StatCard;
import ui.components.TableCard;
import util.DateUtils;
import util.UIStyles;

/**
 * Registrar dashboard.
 *
 * <p>Every figure is computed from MySQL through {@link ReportService}: totals,
 * institution-wide attendance and academic averages, the grade mix and the
 * most recent registrations. Nothing is hardcoded.
 */
public class DashboardPanel extends BasePanel implements MainFrame.Refreshable {

    private static final long serialVersionUID = 1L;

    private final ReportService report = new ReportService();

    private StatCard cardStudents;
    private StatCard cardFaculty;
    private StatCard cardCourses;
    private StatCard cardEnrollments;
    private StatCard cardAvgAttendance;
    private StatCard cardAvgAcademic;
    private GradeMixCard gradeMix;

    private JTable recentStudentsTable;
    private JTable recentEnrollmentsTable;
    private TableCard recentStudentsCard;
    private TableCard recentEnrollmentsCard;

    public DashboardPanel(MainFrame main) {
        super(main);
        buildUI();
        refreshData();
    }

    private void buildUI() {
        SectionHeader head = header("Registrar \u00B7 Overview", "Dashboard",
                "A live reading of the institution \u2014 totals, averages and recent activity.");
        add(head, BorderLayout.NORTH);

        JPanel content = new DashboardContent();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(0, UIStyles.PAGE_PADDING, 24, UIStyles.PAGE_PADDING));

        HeroBanner hero = new HeroBanner();
        hero.setAlignmentX(LEFT_ALIGNMENT);
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 112));
        content.add(hero);
        content.add(Box.createVerticalStrut(16));

        content.add(buildKpiRow());
        content.add(Box.createVerticalStrut(16));
        content.add(buildMidRow());
        content.add(Box.createVerticalStrut(16));
        content.add(buildTablesRow());

        JScrollPane scroll = new JScrollPane(content);
        AppScrollBarUI.apply(scroll);
        scroll.getViewport().setBackground(UIStyles.CANVAS);
        scroll.setBackground(UIStyles.CANVAS);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildKpiRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        cardStudents = new StatCard("Students", "0", UIStyles.PRIMARY,
                new AppIcon(AppIcon.Glyph.STUDENTS, 17, UIStyles.PRIMARY));
        cardStudents.setCaption("on the register");

        cardFaculty = new StatCard("Faculty", "0", UIStyles.INFO,
                new AppIcon(AppIcon.Glyph.FACULTY, 17, UIStyles.INFO));
        cardFaculty.setCaption("teaching staff");

        cardCourses = new StatCard("Courses", "0", UIStyles.ACCENT,
                new AppIcon(AppIcon.Glyph.COURSES, 17, UIStyles.ACCENT));
        cardCourses.setCaption("in the catalogue");

        cardEnrollments = new StatCard("Enrollments", "0", UIStyles.SUCCESS,
                new AppIcon(AppIcon.Glyph.ENROLLMENTS, 17, UIStyles.SUCCESS));
        cardEnrollments.setCaption("registrations");

        row.add(cardStudents);
        row.add(cardFaculty);
        row.add(cardCourses);
        row.add(cardEnrollments);
        return row;
    }

    private JPanel buildMidRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 14, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        cardAvgAttendance = new StatCard("Attendance", "0.00%", UIStyles.INFO,
                new AppIcon(AppIcon.Glyph.ATTENDANCE, 17, UIStyles.INFO));
        cardAvgAttendance.setCaption("present + late across all sessions");
        cardAvgAttendance.setProgress(0);

        cardAvgAcademic = new StatCard("Academic average", "0.00%", UIStyles.PRIMARY,
                new AppIcon(AppIcon.Glyph.RESULTS, 17, UIStyles.PRIMARY));
        cardAvgAcademic.setCaption("mean of all recorded results");
        cardAvgAcademic.setProgress(0);

        gradeMix = new GradeMixCard();

        row.add(cardAvgAttendance);
        row.add(cardAvgAcademic);
        row.add(gradeMix);
        return row;
    }

    private JPanel buildTablesRow() {
        recentStudentsTable = buildTable(new Object[0][0],
                new String[]{"Student ID", "Name", "Department", "Sem."});
        recentStudentsCard = tableCard("Recently added students", "The newest entries in the register.",
                recentStudentsTable, "No students yet",
                "Add a student from the Students module to populate this list.");
        RoundedButton allStudents = new RoundedButton("Open register", AppIcon.Glyph.ARROW_RIGHT);
        allStudents.applyGhost();
        allStudents.addActionListener(e -> main.navigateTo("Students"));
        recentStudentsCard.getCard().addHeaderAction(allStudents);

        recentEnrollmentsTable = buildTable(new Object[0][0],
                new String[]{"Student", "Course", "Status", "Date"});
        recentEnrollmentsCard = tableCard("Recent enrollments", "The latest course registrations.",
                recentEnrollmentsTable, "No enrollments yet",
                "Enroll a student from the Enrollments module to see activity here.");
        RoundedButton allEnrollments = new RoundedButton("Open ledger", AppIcon.Glyph.ARROW_RIGHT);
        allEnrollments.applyGhost();
        allEnrollments.addActionListener(e -> main.navigateTo("Enrollments"));
        recentEnrollmentsCard.getCard().addHeaderAction(allEnrollments);

        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 254));
        row.add(recentStudentsCard);
        row.add(recentEnrollmentsCard);
        return row;
    }

    /* ---------- Data loading (off the EDT) ---------- */

    @Override
    public void refreshData() {
        new SwingWorker<Stats, Void>() {
            @Override
            protected Stats doInBackground() throws Exception {
                Stats s = new Stats();
                s.totalStudents = report.totalStudents();
                s.totalFaculty = report.totalFaculty();
                s.totalCourses = report.totalCourses();
                s.totalEnrollments = report.totalEnrollments();
                s.averageAttendance = report.averageAttendance();
                s.averageAcademic = report.averageAcademicPercentage();

                int[] grades = new int[5];
                for (Map<String, Object> row : report.allResults()) {
                    String grade = String.valueOf(row.get("grade"));
                    int index = switch (grade) {
                        case "A" -> 0;
                        case "B" -> 1;
                        case "C" -> 2;
                        case "D" -> 3;
                        case "F" -> 4;
                        default -> -1;
                    };
                    if (index >= 0) {
                        grades[index]++;
                    }
                }
                s.grades = grades;
                return s;
            }

            @Override
            protected void done() {
                try {
                    Stats s = get();
                    cardStudents.setValue(String.valueOf(s.totalStudents));
                    cardStudents.setCaption(s.totalStudents == 1 ? "student on the register" : "students on the register");
                    cardFaculty.setValue(String.valueOf(s.totalFaculty));
                    cardFaculty.setCaption(s.totalFaculty == 1 ? "teaching member" : "teaching staff");
                    cardCourses.setValue(String.valueOf(s.totalCourses));
                    cardEnrollments.setValue(String.valueOf(s.totalEnrollments));
                    cardAvgAttendance.setValue(format(s.averageAttendance));
                    cardAvgAttendance.setProgress(s.averageAttendance);
                    cardAvgAcademic.setValue(format(s.averageAcademic));
                    cardAvgAcademic.setProgress(s.averageAcademic);
                    gradeMix.setCounts(s.grades);
                } catch (Exception ex) {
                    showError("Dashboard totals could not be loaded: " + message(ex));
                }
                loadRecentStudents();
                loadRecentEnrollments();
            }
        }.execute();
    }

    private void loadRecentStudents() {
        new SwingWorker<List<Map<String, Object>>, Void>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return report.studentDirectory();
            }

            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> rows = get();
                    Object[][] data = new Object[rows.size()][4];
                    for (int i = 0; i < rows.size(); i++) {
                        Map<String, Object> r = rows.get(i);
                        data[i] = new Object[]{
                            r.get("studentId"), r.get("fullName"),
                            r.get("department"), r.get("semester")
                        };
                    }
                    replaceTableData(recentStudentsTable, data,
                            new String[]{"Student ID", "Name", "Department", "Sem."});
                    setColumnWidths(recentStudentsTable, 95, 150, 120, 55);
                    recentStudentsCard.setCount(rows.size(), "student");
                    recentStudentsCard.sync();
                } catch (Exception ex) {
                    // Keep the empty state; totals already surface connection errors.
                }
            }
        }.execute();
    }

    private void loadRecentEnrollments() {
        new SwingWorker<List<Map<String, Object>>, Void>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return report.recentEnrollments();
            }

            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> rows = get();
                    Object[][] data = new Object[rows.size()][4];
                    for (int i = 0; i < rows.size(); i++) {
                        Map<String, Object> r = rows.get(i);
                        data[i] = new Object[]{
                            r.get("studentName"), r.get("courseCode"),
                            r.get("status"), DateUtils.display((java.time.LocalDate) r.get("enrollmentDate"))
                        };
                    }
                    replaceTableData(recentEnrollmentsTable, data,
                            new String[]{"Student", "Course", "Status", "Date"});
                    setColumnWidths(recentEnrollmentsTable, 150, 90, 100, 95);
                    recentEnrollmentsCard.setCount(rows.size(), "enrollment");
                    recentEnrollmentsCard.sync();
                } catch (Exception ex) {
                    // Keep the empty state.
                }
            }
        }.execute();
    }

    private String format(double value) {
        return String.format("%.2f%%", value);
    }

    private String message(Exception ex) {
        return ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
    }

    private static final class Stats {

        int totalStudents;
        int totalFaculty;
        int totalCourses;
        int totalEnrollments;
        double averageAttendance;
        double averageAcademic;
        int[] grades = new int[5];
    }

    /** Vertical stack that follows the viewport width so cards never clip. */
    private static final class DashboardContent extends JPanel implements javax.swing.Scrollable {

        private static final long serialVersionUID = 1L;

        DashboardContent() {
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
            return 20;
        }

        @Override
        public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
            return 140;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    /* ---------------------------------------------------------------
       Hero banner
       --------------------------------------------------------------- */

    private final class HeroBanner extends JPanel {

        private static final long serialVersionUID = 1L;

        HeroBanner() {
            setOpaque(false);
            setLayout(new BorderLayout(20, 0));
            setBorder(new EmptyBorder(20, 26, 20, 22));
            setPreferredSize(new Dimension(0, 112));

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

            JLabel eyebrow = new JLabel("ACADEMIC YEAR 2026 \u2013 27  \u00B7  LIVE REGISTRY");
            eyebrow.setFont(UIStyles.EYEBROW);
            eyebrow.setForeground(UIStyles.SIDEBAR_ACCENT);

            JLabel title = new JLabel("Good morning, Registrar.");
            title.setFont(new java.awt.Font("Georgia", java.awt.Font.BOLD, 24));
            title.setForeground(Color.WHITE);

            JLabel subtitle = new JLabel("Every figure below is read directly from MySQL \u2014 no sample values.");
            subtitle.setFont(UIStyles.BODY_SM);
            subtitle.setForeground(UIStyles.SIDEBAR_TEXT);

            text.add(eyebrow);
            text.add(Box.createVerticalStrut(4));
            text.add(title);
            text.add(Box.createVerticalStrut(3));
            text.add(subtitle);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            actions.setOpaque(false);

            RoundedButton addStudent = new RoundedButton("New student", AppIcon.Glyph.PLUS);
            addStudent.applySecondary();
            addStudent.addActionListener(e -> main.navigateTo("Students"));

            RoundedButton openReports = new RoundedButton("Open reports", AppIcon.Glyph.REPORTS);
            openReports.applySecondary();
            openReports.addActionListener(e -> main.navigateTo("Reports"));

            actions.add(addStudent);
            actions.add(openReports);

            add(text, BorderLayout.WEST);
            add(actions, BorderLayout.EAST);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                UIStyles.applyQualityHints(g2);
                int w = getWidth();
                int h = getHeight();
                int r = UIStyles.RADIUS_LG;

                g2.setColor(new Color(20, 32, 25, 22));
                g2.fillRoundRect(1, 3, w - 2, h - 3, r, r);
                g2.setPaint(new java.awt.GradientPaint(0, 0, UIStyles.PRIMARY_DARK, w, h,
                        new Color(0x1A, 0x4F, 0x3A)));
                g2.fillRoundRect(0, 0, w - 1, h - 3, r, r);

                // Decorative rings.
                g2.setColor(new Color(255, 255, 255, 16));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawOval(w - 230, -70, 220, 220);
                g2.drawOval(w - 190, -30, 160, 160);
                g2.setColor(new Color(0xC2, 0x64, 0x3F, 90));
                g2.fillRoundRect(0, 18, 4, h - 42, 4, 4);
                g2.setColor(new Color(255, 255, 255, 28));
                g2.drawRoundRect(0, 0, w - 1, h - 3, r, r);
            } finally {
                g2.dispose();
            }
        }
    }

    /* ---------------------------------------------------------------
       Grade mix panel
       --------------------------------------------------------------- */

    private static final class GradeMixCard extends JPanel {

        private static final long serialVersionUID = 1L;

        private static final String[] GRADES = {"A", "B", "C", "D", "F"};
        private static final Color[] COLORS = {
            UIStyles.SUCCESS, UIStyles.PRIMARY, UIStyles.INFO, UIStyles.WARNING, UIStyles.DANGER
        };

        private int[] counts = new int[5];

        GradeMixCard() {
            setOpaque(false);
            setPreferredSize(new Dimension(240, 126));
        }

        void setCounts(int[] values) {
            counts = values == null ? new int[5] : values;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                UIStyles.applyQualityHints(g2);
                int w = getWidth();
                int h = getHeight();
                int r = UIStyles.RADIUS_LG;

                g2.setColor(new Color(20, 32, 25, 9));
                g2.fillRoundRect(1, 2, w - 2, h - 2, r, r);
                g2.setColor(UIStyles.SURFACE);
                g2.fillRoundRect(0, 0, w - 1, h - 2, r, r);
                g2.setColor(UIStyles.LINE);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w - 1, h - 2, r, r);

                int pad = 18;
                g2.setFont(UIStyles.LABEL);
                g2.setColor(UIStyles.MUTED);
                g2.drawString("GRADE MIX", pad, pad + 9);

                int total = 0;
                for (int c : counts) {
                    total += c;
                }

                int barY = 52;
                int barW = w - pad * 2;
                int barH = 10;
                g2.setColor(UIStyles.CANVAS_DEEP);
                g2.fillRoundRect(pad, barY, barW, barH, barH, barH);
                if (total > 0) {
                    int x = pad;
                    for (int i = 0; i < counts.length; i++) {
                        if (counts[i] == 0) {
                            continue;
                        }
                        int seg = Math.max(4, (int) Math.round((double) counts[i] / total * barW));
                        if (x + seg > pad + barW) {
                            seg = pad + barW - x;
                        }
                        g2.setColor(COLORS[i]);
                        g2.fillRect(x, barY, seg, barH);
                        x += seg;
                    }
                }

                // Legend
                g2.setFont(UIStyles.CAPTION);
                int legendY = barY + barH + 24;
                int columns = 3;
                int colW = barW / columns;
                for (int i = 0; i < GRADES.length; i++) {
                    int col = i % columns;
                    int line = i / columns;
                    int x = pad + col * colW;
                    int y = legendY + line * 20;
                    g2.setColor(COLORS[i]);
                    g2.fillOval(x, y - 7, 6, 6);
                    g2.setColor(UIStyles.TEXT_BODY);
                    g2.drawString(GRADES[i], x + 11, y);
                    g2.setColor(UIStyles.MUTED);
                    g2.drawString(String.valueOf(counts[i]), x + 28, y);
                }
            } finally {
                g2.dispose();
            }
        }
    }
}
