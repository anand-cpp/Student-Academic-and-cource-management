package ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import javax.swing.JComponent;
import util.UIStyles;

/**
 * Circular monogram avatar. Colour is derived from the name so the same person
 * always receives the same tint; initials are painted in a sans face.
 */
public class Avatar extends JComponent {

    private static final long serialVersionUID = 1L;

    private static final Color[] TINTS = {
        UIStyles.PRIMARY, UIStyles.INFO, UIStyles.ACCENT,
        new Color(0x6B, 0x5A, 0x8E), new Color(0x3F, 0x6B, 0x5A),
        new Color(0x8A, 0x5A, 0x3B)
    };

    private final String initials;
    private final Color tint;
    private final int diameter;
    private final boolean ring;

    public Avatar(String name, int diameter) {
        this(name, diameter, false);
    }

    public Avatar(String name, int diameter, boolean ring) {
        this.initials = initialsOf(name);
        this.diameter = diameter;
        this.tint = tintOf(name);
        this.ring = ring;
        setOpaque(false);
        setPreferredSize(new Dimension(diameter, diameter));
        setMinimumSize(new Dimension(diameter, diameter));
        setMaximumSize(new Dimension(diameter, diameter));
    }

    private static String initialsOf(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "?";
        }
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (sb.length() >= 2) {
                break;
            }
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0)));
            }
        }
        return sb.length() == 0 ? "?" : sb.toString();
    }

    private static Color tintOf(String name) {
        if (name == null) {
            return TINTS[0];
        }
        int hash = Math.abs(name.trim().toLowerCase().hashCode());
        return TINTS[hash % TINTS.length];
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            UIStyles.applyQualityHints(g2);
            int d = Math.min(diameter, Math.min(getWidth(), getHeight()));
            int x = (getWidth() - d) / 2;
            int y = (getHeight() - d) / 2;

            // Soft gradient disc for depth.
            RadialGradientPaint paint = new RadialGradientPaint(
                    new Point2D.Double(x + d * 0.32, y + d * 0.28),
                    Math.max(1f, d * 0.9f),
                    new float[]{0f, 1f},
                    new Color[]{UIStyles.mix(tint, Color.WHITE, 0.22), tint});
            g2.setPaint(paint);
            g2.fillOval(x, y, d, d);

            if (ring) {
                g2.setColor(UIStyles.alpha(Color.WHITE, 190));
                g2.setStroke(new BasicStroke(1.6f));
                g2.drawOval(x + 1, y + 1, d - 2, d - 2);
            }

            g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, Math.max(10, (int) (d * 0.38))));
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(initials);
            g2.drawString(initials, x + (d - tw) / 2, y + (d - fm.getHeight()) / 2 + fm.getAscent());
        } finally {
            g2.dispose();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(diameter, diameter);
    }
}
