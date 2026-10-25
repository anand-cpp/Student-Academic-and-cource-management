package ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import util.UIStyles;

/**
 * Sidebar navigation row: custom-painted icon, label, hover and active states.
 */
public class SidebarItem extends JButton {

    private static final long serialVersionUID = 1L;

    private boolean active;
    private boolean hover;
    private final AppIcon.Glyph glyph;

    public SidebarItem(String label, AppIcon.Glyph glyph) {
        super(label);
        this.glyph = glyph;
        setFont(UIStyles.SIDEBAR_ITEM);
        setForeground(UIStyles.SIDEBAR_TEXT);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setHorizontalAlignment(LEFT);
        setBorder(new EmptyBorder(0, 44, 0, 12));
        setPreferredSize(new Dimension(0, 42));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setRolloverEnabled(true);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    /** Backwards-compatible constructor used by older code. */
    public SidebarItem(String label, String ignoredGlyph) {
        this(label, glyphFor(label));
    }

    private static AppIcon.Glyph glyphFor(String label) {
        return switch (label) {
            case "Dashboard" -> AppIcon.Glyph.DASHBOARD;
            case "Students" -> AppIcon.Glyph.STUDENTS;
            case "Faculty" -> AppIcon.Glyph.FACULTY;
            case "Courses" -> AppIcon.Glyph.COURSES;
            case "Enrollments" -> AppIcon.Glyph.ENROLLMENTS;
            case "Attendance" -> AppIcon.Glyph.ATTENDANCE;
            case "Results" -> AppIcon.Glyph.RESULTS;
            case "Reports" -> AppIcon.Glyph.REPORTS;
            case "Exit" -> AppIcon.Glyph.LOGOUT;
            default -> AppIcon.Glyph.SPARK;
        };
    }

    public void setActive(boolean active) {
        this.active = active;
        revalidate();
        repaint();
    }

    public boolean isActive() {
        return active;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            UIStyles.applyQualityHints(g2);
            int w = getWidth();
            int h = getHeight();

            if (active) {
                g2.setColor(UIStyles.SIDEBAR_ACTIVE);
                g2.fillRoundRect(6, 0, w - 12, h, 10, 10);

                g2.setColor(UIStyles.SIDEBAR_ACCENT);
                g2.fillRoundRect(6, 11, 3, h - 22, 3, 3);
            } else if (hover) {
                g2.setColor(UIStyles.SIDEBAR_HOVER);
                g2.fillRoundRect(6, 0, w - 12, h, 10, 10);
            }

            Color fg = active ? Color.WHITE : (hover ? Color.WHITE : UIStyles.SIDEBAR_TEXT);
            Color iconColor = active ? UIStyles.SIDEBAR_ACCENT
                    : (hover ? UIStyles.SIDEBAR_TEXT : UIStyles.SIDEBAR_MUTED);

            new AppIcon(glyph, 18, iconColor, 1.6f).paintIcon(this, g2, 20, (h - 18) / 2);

            g2.setFont(getFont());
            g2.setColor(fg);
            FontMetrics fm = g2.getFontMetrics();
            String text = getText();
            g2.drawString(text, 44, (h - fm.getHeight()) / 2 + fm.getAscent());

            if (active) {
                g2.setColor(UIStyles.alpha(UIStyles.SIDEBAR_ACCENT, 90));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(6, 0, w - 12, h - 1, 10, 10);
            }
        } finally {
            g2.dispose();
        }
    }
}
