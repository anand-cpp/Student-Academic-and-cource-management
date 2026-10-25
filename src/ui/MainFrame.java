package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import ui.components.SidebarItem;
import util.UIStyles;
import db.DatabaseConnection;
import util.AppException;

/**
 * Main application window.
 *
 * <p>Layout:<br>
 *   WEST   - Sidebar navigation (250px, navy background, active states)<br>
 *   NORTH  - Header (page title, subtitle, DB status, refresh)<br>
 *   CENTER - CardLayout holding every module panel<br>
 *
 * <p>Database operations run off the EDT so the UI stays responsive.
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final JPanel center = new JPanel(new java.awt.CardLayout());
    private final java.awt.CardLayout cardLayout = (java.awt.CardLayout) center.getLayout();

    private final JLabel dbStatusDot = new JLabel("\u25CF");
    private final JLabel dbStatusText = new JLabel("Checking database...");
    private final JLabel pageTitle = new JLabel("Dashboard");
    private final JLabel pageSubtitle = new JLabel("Academic administration overview");

    // Module panels
    private DashboardPanel dashboard;
    private StudentPanel students;
    private FacultyPanel faculty;
    private CoursePanel courses;
    private EnrollmentPanel enrollments;
    private AttendancePanel attendance;
    private ResultPanel results;
    private ReportsPanel reports;

    public MainFrame() {
        super("Student Academic & Course Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1366, 768);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));

        // Root panel with card background
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UIStyles.CARD);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);

        add(root, BorderLayout.CENTER);

        initializePanels();
        registerNavigation();

        setVisible(true);

        // Initial database status check (off the EDT)
        refreshDbStatus();
    }

    /* ---------- Sidebar ---------- */

    private JPanel buildSidebar() {
        RoundedPanel sidebar = new RoundedPanel();
        sidebar.setRadius(0);
        sidebar.setFill(UIStyles.PRIMARY);
        sidebar.setShadow(false);
        sidebar.setLayout(new BorderLayout(0, 0));
        sidebar.setPreferredSize(new Dimension(UIStyles.SIDEBAR_WIDTH, 0));

        // Brand section
        JPanel brand = new JPanel();
        brand.setBackground(UIStyles.PRIMARY_DARK);
        brand.setBorder(BorderFactory.createEmptyBorder(24, 20, 24, 20));
        brand.setLayout(new BorderLayout(8, 4));

        JLabel appName = new JLabel("Student");
        appName.setFont(UIStyles.SIDEBAR_BRAND);
        appName.setForeground(UIStyles.INVERSE);

        JLabel sub = new JLabel("Academic System");
        sub.setFont(UIStyles.CAPTION);
        sub.setForeground(new Color(148, 163, 184));

        brand.add(appName, BorderLayout.NORTH);
        brand.add(sub, BorderLayout.SOUTH);

        // Navigation items
        JPanel nav = new JPanel();
        nav.setBackground(UIStyles.PRIMARY);
        nav.setLayout(new java.awt.GridLayout(0, 1, 0, 4));
        nav.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        String[][] items = {
            {"Dashboard", "\u2630", "Dashboard"},
            {"Students", "\u2709", "Students"},
            {"Faculty", "\u270A", "Faculty"},
            {"Courses", "\u2728", "Courses"},
            {"Enrollments", "\u270D", "Enrollments"},
            {"Attendance", "\u2705", "Attendance"},
            {"Results", "\u2714", "Results"},
            {"Reports", "\u2713", "Reports"},
            {"Exit", "\u274C", "Exit"}
        };

        for (String[] item : items) {
            SidebarItem b = new SidebarItem(item[0], item[1]);
            b.addActionListener((ActionEvent e) -> navigateTo(item[2]));
            nav.add(b);
        }

        // Version footer
        JPanel version = new JPanel();
        version.setBackground(UIStyles.PRIMARY_DARK);
        version.setBorder(BorderFactory.createEmptyBorder(16, 20, 20, 20));
        JLabel ver = new JLabel("v1.0.0  \u00B7  JDK 21 + JDBC");
        ver.setFont(UIStyles.CAPTION);
        ver.setForeground(new Color(120, 130, 145));
        version.add(ver);

        sidebar.add(brand, BorderLayout.NORTH);
        sidebar.add(nav, BorderLayout.CENTER);
        sidebar.add(version, BorderLayout.SOUTH);

        return sidebar;
    }

    /* ---------- Header ---------- */

    private JPanel buildHeader() {
        RoundedPanel header = new RoundedPanel();
        header.setRadius(0);
        header.setFill(UIStyles.ACCENT);
        header.setShadow(false);
        header.setLayout(new BorderLayout(16, 0));
        header.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        header.setPreferredSize(new Dimension(0, UIStyles.HEADER_HEIGHT));

        // Left: title + subtitle
        JPanel left = new JPanel();
        left.setBackground(UIStyles.ACCENT);
        left.setLayout(new BorderLayout(4, 4));
        pageTitle.setFont(UIStyles.H2);
        pageTitle.setForeground(UIStyles.INVERSE);
        pageSubtitle.setFont(UIStyles.BODY_SM);
        pageSubtitle.setForeground(new Color(209, 213, 221));
        left.add(pageTitle, BorderLayout.NORTH);
        left.add(pageSubtitle, BorderLayout.SOUTH);

        // Right: DB status + refresh
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        right.setBackground(UIStyles.ACCENT);

        dbStatusDot.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 12));
        dbStatusDot.setForeground(UIStyles.WARNING);
        dbStatusText.setFont(UIStyles.BODY_SM);
        dbStatusText.setForeground(UIStyles.INVERSE);

        RoundedButton refresh = new RoundedButton("Refresh");
        refresh.applyOutline();
        refresh.setFont(UIStyles.BUTTON_SM);
        refresh.addActionListener(e -> {
            refreshDbStatus();
            java.awt.EventQueue.invokeLater(() -> {
                for (java.awt.Component c : center.getComponents()) {
                    if (c.isVisible() && c instanceof Refreshable) {
                        ((Refreshable) c).refreshData();
                    }
                }
            });
        });

        right.add(dbStatusDot);
        right.add(dbStatusText);
        right.add(refresh);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    /* ---------- Panels & navigation ---------- */

    private void initializePanels() {
        dashboard = new DashboardPanel(this);
        students = new StudentPanel(this);
        faculty = new FacultyPanel(this);
        courses = new CoursePanel(this);
        enrollments = new EnrollmentPanel(this);
        attendance = new AttendancePanel(this);
        results = new ResultPanel(this);
        reports = new ReportsPanel(this);

        center.add(dashboard, "Dashboard");
        center.add(students, "Students");
        center.add(faculty, "Faculty");
        center.add(courses, "Courses");
        center.add(enrollments, "Enrollments");
        center.add(attendance, "Attendance");
        center.add(results, "Results");
        center.add(reports, "Reports");

        cardLayout.show(center, "Dashboard");
        setPageTitle("Dashboard", "Academic administration overview \u2014 live statistics from MySQL.");
    }

    private void registerNavigation() {
        // Navigation is wired in buildSidebar via navigateTo(page).
    }

    void navigateTo(String page) {
        if ("Exit".equalsIgnoreCase(page)) {
            DatabaseConnection.close();
            System.exit(0);
        }
        cardLayout.show(center, page);
        setPageTitle(page, defaultSubtitle(page));

        // Refresh the shown panel
        for (java.awt.Component c : center.getComponents()) {
            if (c.isVisible() && c instanceof Refreshable) {
                ((Refreshable) c).refreshData();
            }
        }
        refreshDbStatus();
    }

    void setPageTitle(String title, String subtitle) {
        pageTitle.setText(title);
        pageSubtitle.setText(subtitle);
    }

    private String defaultSubtitle(String page) {
        switch (page) {
            case "Students": return "Manage student records \u2014 add, search, edit, delete.";
            case "Faculty": return "Manage faculty members and course assignments.";
            case "Courses": return "Manage courses, credits, marks, and faculty assignment.";
            case "Enrollments": return "Enroll students in courses and manage enrollment status.";
            case "Attendance": return "Mark and track attendance per course and date.";
            case "Results": return "Enter marks, calculate percentages and grades.";
            case "Reports": return "Academic reports and performance summaries.";
            default: return "Academic administration overview \u2014 live statistics from MySQL.";
        }
    }

    /* ---------- Database status ---------- */

    void refreshDbStatus() {
        new javax.swing.SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    DatabaseConnection.getConnection();
                    return true;
                } catch (AppException ex) {
                    return false;
                }
            }

            @Override
            protected void done() {
                boolean connected = false;
                try {
                    connected = get();
                } catch (Exception ex) {
                    connected = false;
                }
                if (connected) {
                    dbStatusDot.setForeground(UIStyles.SUCCESS);
                    dbStatusText.setText("Connected to MySQL");
                } else {
                    dbStatusDot.setForeground(UIStyles.DANGER);
                    dbStatusText.setText("Database disconnected");
                }
            }
        }.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }

    public interface Refreshable {
        void refreshData();
    }
}


