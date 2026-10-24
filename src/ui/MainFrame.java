package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import util.UIStyles;
import db.DatabaseConnection;
import util.AppException;

/**
 * Main application window.
 *
 * <p>Layout (all standard Swing layout managers, no absolute positioning):<br>
 *   NORTH  - header (page title, subtitle, DB status, refresh)<br>
 *   WEST   - sidebar navigation<br>
 *   CENTER - CardLayout holding every module panel<br>
 *
 * <p>Database operations that could block run in a {@link SwingWorker} so the
 * Event Dispatch Thread stays responsive.
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final JPanel center = new JPanel(new CardLayout());
    private final CardLayout cardLayout = (CardLayout) center.getLayout();

    private final JLabel dbStatusDot = new JLabel("●");
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
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));

        add(buildSidebar(), BorderLayout.WEST);
        add(buildHeader(), BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        initializePanels();
        registerNavigation();

        // Initial database status check (off the EDT)
        refreshDbStatus();
    }

    /* ---------- Sidebar ---------- */

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UIStyles.PRIMARY);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setLayout(new BorderLayout());

        JPanel brand = new JPanel();
        brand.setBackground(UIStyles.PRIMARY_DARK);
        brand.setBorder(new EmptyBorder(20, 16, 24, 16));
        brand.setLayout(new BorderLayout());
        JLabel appName = new JLabel("Student");
        JLabel sub = new JLabel("Academic System");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 19));
        appName.setForeground(Color.WHITE);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(165, 175, 195));
        brand.add(appName, BorderLayout.NORTH);
        brand.add(sub, BorderLayout.SOUTH);

        JPanel nav = new JPanel();
        nav.setBackground(UIStyles.PRIMARY);
        nav.setLayout(new java.awt.GridLayout(0, 1, 0, 4));
        nav.setBorder(new EmptyBorder(8, 10, 8, 10));

        String[] items = {
            "Dashboard", "Students", "Faculty", "Courses",
            "Enrollments", "Attendance", "Results", "Reports", "Exit"
        };
        for (String item : items) {
            JButton b = new JButton(item);
            b.setBackground(UIStyles.PRIMARY);
            b.setForeground(new Color(203, 213, 225));
            b.setFont(UIStyles.BUTTON_FONT);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setContentAreaFilled(false);
            b.setOpaque(true);
            b.setBorder(new EmptyBorder(12, 16, 12, 16));
            b.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            b.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                    b.setBackground(new Color(55, 65, 81));
                }
                @Override public void mouseExited(java.awt.event.MouseEvent e) {
                    b.setBackground(UIStyles.PRIMARY);
                }
            });
            nav.add(b);
            String page = item;
            b.addActionListener((ActionEvent e) -> navigateTo(page));
        }

        sidebar.add(brand, BorderLayout.NORTH);
        sidebar.add(nav, BorderLayout.CENTER);

        JPanel version = new JPanel();
        version.setBackground(UIStyles.PRIMARY_DARK);
        version.setBorder(new EmptyBorder(12, 16, 16, 16));
        JLabel ver = new JLabel("v1.0.0  ·  JDK 21 + JDBC");
        ver.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ver.setForeground(new Color(120, 130, 145));
        version.add(ver);
        sidebar.add(version, BorderLayout.SOUTH);

        return sidebar;
    }

    /* ---------- Header ---------- */

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setBackground(UIStyles.ACCENT);
        header.setBorder(new EmptyBorder(10, 20, 10, 20));
        header.setLayout(new BorderLayout(12, 0));

        JPanel left = new JPanel();
        left.setBackground(UIStyles.ACCENT);
        left.setLayout(new BorderLayout(6, 4));
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        pageTitle.setForeground(Color.WHITE);
        pageSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pageSubtitle.setForeground(new Color(209, 213, 221));
        left.add(pageTitle, BorderLayout.NORTH);
        left.add(pageSubtitle, BorderLayout.SOUTH);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setBackground(UIStyles.ACCENT);
        dbStatusDot.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dbStatusDot.setForeground(UIStyles.WARNING);
        dbStatusText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dbStatusText.setForeground(Color.WHITE);
        JButton refresh = new JButton("Refresh");
        UIStyles.applyOutlineButton(refresh);
        refresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refresh.addActionListener(e -> {
            refreshDbStatus();
            javax.swing.SwingUtilities.invokeLater(() -> {
                java.awt.Component c = center.getComponent(center.getComponentCount() - 1);
                // refresh the visible panel if it exposes refreshData()
                if (c instanceof Refreshable) {
                    ((Refreshable) c).refreshData();
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
        setPageTitle("Dashboard", "Academic administration overview — live statistics from MySQL.");
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
        if (center.getComponent(0) instanceof Refreshable) {
            // find the shown panel
            for (java.awt.Component c : center.getComponents()) {
                if (c.isVisible() && c instanceof Refreshable) {
                    ((Refreshable) c).refreshData();
                }
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
            case "Students": return "Manage student records — add, search, edit, delete.";
            case "Faculty": return "Manage faculty members and their course assignments.";
            case "Courses": return "Manage courses, credits, marks, and faculty assignment.";
            case "Enrollments": return "Enroll students in courses and manage enrollment status.";
            case "Attendance": return "Mark and track attendance per course and date.";
            case "Results": return "Enter marks, calculate percentages and grades.";
            case "Reports": return "Academic reports and performance summaries.";
            default: return "Academic administration overview — live statistics from MySQL.";
        }
    }

    /* ---------- Database status ---------- */

    void refreshDbStatus() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
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
                boolean connected;
                try {
                    connected = get();
                } catch (InterruptedException | ExecutionException ex) {
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
        };
        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }

    /** Panels implement this so navigation can refresh their data. */
    public interface Refreshable {
        void refreshData();
    }
}

