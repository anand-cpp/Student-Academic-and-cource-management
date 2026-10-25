package ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JLabel;
import javax.swing.JPanel;
import util.UIStyles;

/**
 * Soft status pill: tinted background, saturated text, optional leading dot.
 * Drawn with Java2D so it keeps a consistent silhouette in tables and headers.
 */
public class StatusBadge extends JPanel {

    private static final long serialVersionUID = 1L;

    public enum Style {
        ACTIVE, INACTIVE, COMPLETED, DROPPED, PRESENT, ABSENT, LATE
    }

    private final String text;
    private final Color bg;
    private final Color fg;

    public StatusBadge(String text, Style style) {
        this.text = text == null ? "" : text;
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setOpaque(false);

        switch (style) {
            case ACTIVE, PRESENT -> {
                bg = UIStyles.SUCCESS_LIGHT;
                fg = UIStyles.SUCCESS;
            }
            case COMPLETED -> {
                bg = UIStyles.INFO_LIGHT;
                fg = UIStyles.INFO;
            }
            case ABSENT, DROPPED -> {
                bg = UIStyles.DANGER_LIGHT;
                fg = UIStyles.DANGER;
            }
            case LATE -> {
                bg = UIStyles.WARNING_LIGHT;
                fg = UIStyles.WARNING;
            }
            case INACTIVE -> {
                bg = UIStyles.CANVAS_DEEP;
                fg = UIStyles.MUTED;
            }
            default -> {
                bg = UIStyles.CANVAS_DEEP;
                fg = UIStyles.MUTED;
            }
        }

        JLabel label = new JLabel(this.text) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    UIStyles.applyQualityHints(g2);
                    int w = getWidth();
                    int h = getHeight();
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, w, h, h, h);
                    g2.setColor(fg);
                    g2.fillOval(9, h / 2 - 2, 4, 4);
                } finally {
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        label.setFont(UIStyles.CAPTION_BOLD);
        label.setForeground(fg);
        label.setBorder(new javax.swing.border.EmptyBorder(3, 18, 3, 10));

        add(label);
    }

    public String getText() {
        return text;
    }

    /** Small hairline outline variant used inside dense tables. */
    public static JPanel subtle(String text, Color color) {
        JPanel pill = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 2)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    UIStyles.applyQualityHints(g2);
                    g2.setColor(UIStyles.alpha(color, 26));
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
                    g2.setColor(UIStyles.alpha(color, 70));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
                } finally {
                    g2.dispose();
                }
            }
        };
        pill.setOpaque(false);
        JLabel l = new JLabel(text);
        l.setFont(UIStyles.CAPTION_BOLD);
        l.setForeground(color);
        pill.add(l);
        return pill;
    }

    /** Unused helper kept for symmetry with the previous API. */
    static int dotWidth(FontMetrics fm) {
        return fm.stringWidth("•") + 4;
    }
}
