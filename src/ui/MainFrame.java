package ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import ui.components.AppDialog;
import ui.components.AppIcon;
import ui.components.Avatar;
import ui.components.RoundedButton;
import ui.components.SidebarItem;
import util.UIStyles;
import db.DatabaseConnection;
import util.AppException;

/**
 * Main application window — the "Registrar" console.
 *
 * <p>Layout:<br>
 *   WEST   - deep-forest sidebar: brand seal, workspace navigation, database
 *            health and sign-out<br>
 *   CENTER - parchment workspace with a slim top bar and a CardLayout holding
 *            every module page.
 *
 * <p>Database calls run off the EDT; the shell only owns navigation, the
 * breadcrumb, the database indicator and global refresh.
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final JPanel center = new JPanel(new java.awt.CardLayout());
    private final java.awt.CardLayout cardLayout = (java.awt.CardLayout) center.getLayout();

    private final JLabel breadcrumb = new JLabel("REGISTRAR / DASHBOARD");
    private final DbStatus dbStatus = new DbStatus();
    private final java.util.Map<String, SidebarItem> navItems = new java.util.LinkedHashMap<>();

    private DashboardPanel dashboard;
    private StudentPanel students;
    private FacultyPanel faculty;
    private CoursePanel courses;
    private EnrollmentPanel enrollments;
    private AttendancePanel attendance;
    private ResultPanel results;
    private ReportsPanel reports;

    public MainFrame() {
        super("Registrar \u2014 Student Academic & Course Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        java.awt.Rectangle work = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();
        double uiScale = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration()
                .getDefaultTransform().getScaleX();
        if (uiScale <= 0) {
            uiScale = 1.0;
        }
        int workWidth = (int) Math.floor(work.width / uiScale);
        int workHeight = (int) Math.floor(work.height / uiScale);
        int width = Math.min(1366, workWidth - 16);
        int height = Math.min(768, workHeight - 16);
        width = Math.max(width, Math.min(1040, workWidth));
        height = Math.max(height, Math.min(640, workHeight));
        setSize(width, height);
        setMinimumSize(new Dimension(Math.min(1040, workWidth), Math.min(640, workHeight)));
        setLocation(work.x + Math.max(0, (workWidth - width) / 2),
                work.y + Math.max(0, (workHeight - height) / 2));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIStyles.CANVAS);

        root.add(buildSidebar(), BorderLayout.WEST);

        JPanel workspace = new JPanel(new BorderLayout());
        workspace.setBackground(UIStyles.CANVAS);
        workspace.add(buildTopBar(), BorderLayout.NORTH);
        workspace.add(center, BorderLayout.CENTER);

        center.setBackground(UIStyles.CANVAS);
        root.add(workspace, BorderLayout.CENTER);
        setContentPane(root);

        initializePanels();
        setVisible(true);
        refreshDbStatus();
    }

    /* ---------------------------------------------------------------
       Sidebar
       --------------------------------------------------------------- */

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    UIStyles.applyQualityHints(g2);
                    java.awt.GradientPaint paint = new java.awt.GradientPaint(
                            0, 0, UIStyles.SIDEBAR_DEEP,
                            0, getHeight(), UIStyles.SIDEBAR);
                    g2.setPaint(paint);
                    g2.fillRect(0, 0, getWidth(), getHeight());

                    // Very subtle vignette in the lower corner.
                    g2.setPaint(new java.awt.RadialGradientPaint(
                            new java.awt.geom.Point2D.Double(getWidth() * 0.4, getHeight() * 1.05),
                            Math.max(180f, getHeight() * 0.7f),
                            new float[]{0f, 1f},
                            new Color[]{new Color(0x1E, 0x4A, 0x38, 70), new Color(0, 0, 0, 0)}));
                    g2.fillRect(0, 0, getWidth(), getHeight());

                    g2.setColor(UIStyles.SIDEBAR_LINE);
                    g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
                } finally {
                    g2.dispose();
                }
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(UIStyles.SIDEBAR_WIDTH, 0));
        sidebar.setOpaque(true);
        sidebar.setBackground(UIStyles.SIDEBAR);

        sidebar.add(buildBrand());
        sidebar.add(groupLabel("WORKSPACE"));

        addNav(sidebar, "Dashboard", AppIcon.Glyph.DASHBOARD);
        addNav(sidebar, "Students", AppIcon.Glyph.STUDENTS);
        addNav(sidebar, "Faculty", AppIcon.Glyph.FACULTY);
        addNav(sidebar, "Courses", AppIcon.Glyph.COURSES);
        addNav(sidebar, "Enrollments", AppIcon.Glyph.ENROLLMENTS);
        addNav(sidebar, "Attendance", AppIcon.Glyph.ATTENDANCE);
        addNav(sidebar, "Results", AppIcon.Glyph.RESULTS);

        sidebar.add(groupLabel("INSIGHTS"));
        addNav(sidebar, "Reports", AppIcon.Glyph.REPORTS);

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(buildDbPanel());
        sidebar.add(buildSignOut());
        sidebar.add(buildVersion());
        return sidebar;
    }

    private JPanel buildBrand() {
        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.X_AXIS));
        brand.setBorder(new EmptyBorder(24, 22, 20, 18));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 96));

        JPanel seal = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    UIStyles.applyQualityHints(g2);
                    int d = Math.min(getWidth(), getHeight());
                    int x = (getWidth() - d) / 2;
                    int y = (getHeight() - d) / 2;
                    g2.setColor(UIStyles.PRIMARY);
                    g2.fillOval(x, y, d, d);
                    g2.setColor(UIStyles.alpha(UIStyles.SIDEBAR_ACCENT, 120));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawOval(x + 1, y + 1, d - 2, d - 2);
                    g2.drawOval(x + 5, y + 5, d - 10, d - 10);
                    g2.setFont(new java.awt.Font("Georgia", java.awt.Font.BOLD, 19));
                    g2.setColor(Color.WHITE);
                    FontMetrics fm = g2.getFontMetrics();
                    String letter = "R";
                    g2.drawString(letter, x + (d - fm.stringWidth(letter)) / 2,
                            y + (d - fm.getHeight()) / 2 + fm.getAscent());
                } finally {
                    g2.dispose();
                }
            }
        };
        seal.setOpaque(false);
        seal.setPreferredSize(new Dimension(42, 42));
        seal.setMinimumSize(new Dimension(42, 42));
        seal.setMaximumSize(new Dimension(42, 42));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel name = new JLabel("Registrar");
        name.setFont(UIStyles.BRAND);
        name.setForeground(Color.WHITE);

        JLabel sub = new JLabel("ACADEMIC CONSOLE");
        sub.setFont(UIStyles.EYEBROW);
        sub.setForeground(UIStyles.SIDEBAR_MUTED);

        text.add(name);
        text.add(Box.createVerticalStrut(2));
        text.add(sub);

        brand.add(seal);
        brand.add(Box.createHorizontalStrut(12));
        brand.add(text);
        brand.add(Box.createHorizontalGlue());
        return brand;
    }

    private JLabel groupLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIStyles.SIDEBAR_GROUP);
        label.setForeground(UIStyles.SIDEBAR_MUTED);
        label.setBorder(new EmptyBorder(16, 22, 6, 22));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void addNav(JPanel sidebar, String page, AppIcon.Glyph glyph) {
        SidebarItem item = new SidebarItem(page, glyph);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.addActionListener((ActionEvent e) -> navigateTo(page));
        navItems.put(page, item);
        sidebar.add(item);
        sidebar.add(Box.createVerticalStrut(2));
    }

    private JPanel buildDbPanel() {
        JPanel holder = new JPanel(new BorderLayout());
        holder.setOpaque(false);
        holder.setBorder(new EmptyBorder(8, 14, 8, 14));
        holder.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        holder.setAlignmentX(Component.LEFT_ALIGNMENT);
        holder.add(dbStatus, BorderLayout.CENTER);
        return holder;
    }

    private JPanel buildSignOut() {
        JPanel holder = new JPanel(new BorderLayout());
        holder.setOpaque(false);
        holder.setBorder(new EmptyBorder(0, 14, 4, 14));
        holder.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        holder.setAlignmentX(Component.LEFT_ALIGNMENT);

        SidebarItem signOut = new SidebarItem("Sign out", AppIcon.Glyph.LOGOUT);
        signOut.setToolTipText("Close the registrar console");
        signOut.addActionListener(e -> {
            boolean leave = AppDialog.confirm(this,
                    "Leave the registrar?",
                    "The application will close and any unsaved form changes will be lost.",
                    "Sign out", true);
            if (leave) {
                DatabaseConnection.close();
                System.exit(0);
            }
        });
        holder.add(signOut, BorderLayout.CENTER);
        return holder;
    }

    private JPanel buildVersion() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(8, 22, 16, 22));
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel version = new JLabel("v1.0  \u00B7  JDK 21  \u00B7  MySQL + JDBC");
        version.setFont(UIStyles.CAPTION);
        version.setForeground(UIStyles.SIDEBAR_MUTED);
        footer.add(version, BorderLayout.WEST);
        return footer;
    }

    /* ---------------------------------------------------------------
       Top bar
       --------------------------------------------------------------- */

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(16, 0));
        bar.setBackground(UIStyles.CANVAS);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyles.LINE),
                new EmptyBorder(0, UIStyles.PAGE_PADDING, 0, UIStyles.PAGE_PADDING)));
        bar.setPreferredSize(new Dimension(0, 60));

        breadcrumb.setFont(UIStyles.EYEBROW);
        breadcrumb.setForeground(UIStyles.MUTED);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(breadcrumb);
        bar.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        right.setOpaque(false);

        JPanel date = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 0));
        date.setOpaque(false);
        JLabel dateIcon = new JLabel(new AppIcon(AppIcon.Glyph.CALENDAR, 14, UIStyles.MUTED));
        JLabel dateText = new JLabel(DateTimeFormatter.ofPattern("EEE, MMM d, yyyy")
                .format(LocalDate.now()));
        dateText.setFont(UIStyles.BODY_SM);
        dateText.setForeground(UIStyles.MUTED);
        date.add(dateIcon);
        date.add(dateText);

        RoundedButton refresh = new RoundedButton("");
        refresh.setIcon(new AppIcon(AppIcon.Glyph.REFRESH, 15, null));
        refresh.applyOutline();
        refresh.setToolTipText("Refresh the current page and database status");
        refresh.setPreferredSize(new Dimension(38, 38));
        refresh.addActionListener(e -> {
            refreshDbStatus();
            for (Component c : center.getComponents()) {
                if (c.isVisible() && c instanceof Refreshable r) {
                    r.refreshData();
                }
            }
        });

        Avatar avatar = new Avatar("Registry Admin", 30, true);
        JPanel user = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 0));
        user.setOpaque(false);
        user.add(avatar);
        JLabel role = new JLabel("Registrar");
        role.setFont(UIStyles.CAPTION_BOLD);
        role.setForeground(UIStyles.INK);
        user.add(role);

        right.add(date);
        right.add(separator());
        right.add(dbStatus.topPill());
        right.add(refresh);
        right.add(separator());
        right.add(user);
        bar.add(right, BorderLayout.EAST);

        return bar;
    }

    private Component separator() {
        JPanel line = new JPanel();
        line.setBackground(UIStyles.LINE_STRONG);
        line.setPreferredSize(new Dimension(1, 22));
        return line;
    }

    /* ---------------------------------------------------------------
       Panels & navigation
       --------------------------------------------------------------- */

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
        setActiveNav("Dashboard");
    }

    void navigateTo(String page) {
        if ("Exit".equalsIgnoreCase(page)) {
            DatabaseConnection.close();
            System.exit(0);
        }
        cardLayout.show(center, page);
        setActiveNav(page);
        breadcrumb.setText("REGISTRAR / " + page.toUpperCase());

        for (Component c : center.getComponents()) {
            if (c.isVisible() && c instanceof Refreshable r) {
                r.refreshData();
            }
        }
        refreshDbStatus();
    }

    private void setActiveNav(String page) {
        navItems.forEach((name, item) -> item.setActive(name.equals(page)));
    }

    void setPageTitle(String title, String subtitle) {
        breadcrumb.setText("REGISTRAR / " + title.toUpperCase());
        setActiveNav(title);
    }

    /* ---------------------------------------------------------------
       Database indicator
       --------------------------------------------------------------- */

    void refreshDbStatus() {
        dbStatus.setChecking();
        new javax.swing.SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try (java.sql.Connection connection = DatabaseConnection.getConnection()) {
                    return connection.isValid(2);
                } catch (AppException | java.sql.SQLException ex) {
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
                dbStatus.setConnected(connected);
            }
        }.execute();
    }

    /** Sidebar database tile plus the compact top-bar pill. */
    private static final class DbStatus extends JPanel {

        private static final long serialVersionUID = 1L;

        private final JLabel dot = new JLabel("\u25CF");
        private final JLabel title = new JLabel("MySQL");
        private final JLabel state = new JLabel("Checking\u2026");
        private final JLabel pillDot = new JLabel("\u25CF");
        private final JLabel pillText = new JLabel("Connecting");
        private Boolean connected;

        DbStatus() {
            setOpaque(false);
            setLayout(new BorderLayout(10, 0));
            setBorder(new EmptyBorder(11, 12, 11, 12));

            dot.setFont(new java.awt.Font("Segoe UI Symbol", java.awt.Font.PLAIN, 11));
            dot.setForeground(UIStyles.WARNING);

            title.setFont(UIStyles.CAPTION_BOLD);
            title.setForeground(Color.WHITE);
            state.setFont(UIStyles.CAPTION);
            state.setForeground(UIStyles.SIDEBAR_MUTED);

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            text.add(title);
            text.add(Box.createVerticalStrut(1));
            text.add(state);

            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            left.setOpaque(false);
            left.add(dot);

            add(left, BorderLayout.WEST);
            add(text, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                UIStyles.applyQualityHints(g2);
                g2.setColor(new Color(0x14, 0x2C, 0x23));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(UIStyles.SIDEBAR_LINE);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                if (connected != null) {
                    g2.setColor(connected ? UIStyles.SIDEBAR_ACCENT : UIStyles.DANGER);
                    g2.fillRoundRect(0, 12, 3, getHeight() - 24, 3, 3);
                }
            } finally {
                g2.dispose();
            }
        }

        void setChecking() {
            dot.setForeground(UIStyles.WARNING);
            state.setText("Checking\u2026");
            pillDot.setForeground(UIStyles.WARNING);
            pillText.setText("Connecting");
        }

        void setConnected(boolean ok) {
            connected = ok;
            dot.setForeground(ok ? UIStyles.SIDEBAR_ACCENT : UIStyles.DANGER);
            state.setText(ok ? "Connected \u00B7 3306" : "Disconnected");
            pillDot.setForeground(ok ? UIStyles.SUCCESS : UIStyles.DANGER);
            pillText.setText(ok ? "MySQL connected" : "MySQL offline");
            repaint();
        }

        /** Compact pill rendered in the top bar. */
        Component topPill() {
            JPanel pill = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0)) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    try {
                        UIStyles.applyQualityHints(g2);
                        g2.setColor(UIStyles.SURFACE);
                        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                        g2.setColor(UIStyles.LINE);
                        g2.setStroke(new BasicStroke(1f));
                        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                    } finally {
                        g2.dispose();
                    }
                }
            };
            pill.setOpaque(false);
            pill.setBorder(new EmptyBorder(6, 10, 6, 12));

            pillDot.setFont(new java.awt.Font("Segoe UI Symbol", java.awt.Font.PLAIN, 10));
            pillDot.setForeground(UIStyles.WARNING);
            pillText.setFont(UIStyles.CAPTION);
            pillText.setForeground(UIStyles.MUTED);

            pill.add(pillDot);
            pill.add(pillText);
            return pill;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }

    public interface Refreshable {
        void refreshData();
    }
}
