package ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import util.UIStyles;

/**
 * Centred empty state: a hand-drawn glyph in a tinted disc, a serif headline
 * and a short explanation. Used when a table or report returns no rows.
 */
public class EmptyState extends JPanel {

    private static final long serialVersionUID = 1L;

    private final String title;
    private final String description;
    private final AppIcon.Glyph glyph;

    public EmptyState(String icon, String title, String description) {
        this.title = title == null ? "Nothing here yet" : title;
        this.description = description == null ? "" : description;
        this.glyph = glyphFor(icon);

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(24, 32, 24, 32));

        JPanel art = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    UIStyles.applyQualityHints(g2);
                    int size = 66;
                    int x = (getWidth() - size) / 2;
                    g2.setColor(UIStyles.PRIMARY_LIGHT);
                    g2.fillOval(x, 0, size, size);
                    g2.setColor(UIStyles.alpha(UIStyles.PRIMARY, 55));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawOval(x, 0, size - 1, size - 1);
                    new AppIcon(glyph, 26, UIStyles.PRIMARY, 1.5f)
                            .paintIcon(this, g2, x + (size - 26) / 2, (size - 26) / 2);
                } finally {
                    g2.dispose();
                }
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(120, 72);
            }
        };
        art.setOpaque(false);
        art.setAlignmentX(CENTER_ALIGNMENT);
        art.setMinimumSize(new Dimension(120, 72));
        art.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JLabel titleLabel = new JLabel(this.title);
        titleLabel.setFont(UIStyles.H1);
        titleLabel.setForeground(UIStyles.INK);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        add(Box.createVerticalGlue());
        add(art);
        add(Box.createVerticalStrut(6));
        add(titleLabel);

        if (!this.description.isEmpty()) {
            add(Box.createVerticalStrut(6));
            JLabel desc = new JLabel(text(this.description));
            desc.setFont(UIStyles.BODY_SM);
            desc.setForeground(UIStyles.MUTED);
            desc.setAlignmentX(CENTER_ALIGNMENT);
            desc.setHorizontalAlignment(JLabel.CENTER);
            add(desc);
        }
        add(Box.createVerticalGlue());
    }

    private static String text(String value) {
        String t = value == null ? "" : value;
        if (t.length() <= 90) {
            return t;
        }
        return t.substring(0, 87) + "…";
    }

    private static AppIcon.Glyph glyphFor(String icon) {
        if (icon == null) {
            return AppIcon.Glyph.SEARCH;
        }
        String value = icon.toLowerCase();
        if (value.contains("student")) {
            return AppIcon.Glyph.STUDENTS;
        }
        if (value.contains("faculty") || value.contains("person")) {
            return AppIcon.Glyph.FACULTY;
        }
        if (value.contains("course") || value.contains("book")) {
            return AppIcon.Glyph.COURSES;
        }
        if (value.contains("calendar") || value.contains("date")) {
            return AppIcon.Glyph.CALENDAR;
        }
        if (value.contains("result") || value.contains("chart") || value.contains("grade")) {
            return AppIcon.Glyph.RESULTS;
        }
        if (value.contains("report") || value.contains("file")) {
            return AppIcon.Glyph.REPORTS;
        }
        if (value.contains("database") || value.contains("connection")) {
            return AppIcon.Glyph.DATABASE;
        }
        if (value.contains("lock") || value.contains("warning")) {
            return AppIcon.Glyph.WARNING;
        }
        return AppIcon.Glyph.SEARCH;
    }

    /** Convenience for tables that need a scrollable empty placeholder. */
    public static JPanel inScroll(EmptyState state) {
        JPanel holder = new JPanel(new java.awt.BorderLayout());
        holder.setBackground(UIStyles.SURFACE);
        holder.add(state, java.awt.BorderLayout.CENTER);
        return holder;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        FontMetrics fm = getFontMetrics(UIStyles.BODY_SM);
        int width = Math.min(600, Math.max(d.width, fm.stringWidth(description) + 48));
        return new Dimension(width, d.height);
    }
}
