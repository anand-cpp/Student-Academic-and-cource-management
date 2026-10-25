package ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;
import javax.swing.JPanel;
import util.UIStyles;

/**
 * KPI tile with an accent icon, serif numeral, caption and optional progress
 * bar. Painted manually for crisp typography and a consistent card silhouette.
 */
public class StatCard extends JPanel {

    private static final long serialVersionUID = 1L;

    private String label;
    private String value;
    private String caption = " ";
    private Color accent;
    private final Icon icon;
    private double progress = -1;

    public StatCard(String label, String value, Color accent, Icon icon) {
        this.label = label;
        this.value = value;
        this.accent = accent == null ? UIStyles.PRIMARY : accent;
        this.icon = icon;
        setOpaque(false);
        setPreferredSize(new Dimension(240, 126));
        setMinimumSize(new Dimension(180, 126));
    }

    public void setValue(String value) {
        this.value = value;
        repaint();
    }

    public void setCaption(String caption) {
        this.caption = caption == null ? " " : caption;
        repaint();
    }

    public void setAccent(Color accent) {
        this.accent = accent;
        repaint();
    }

    public void setProgress(double percent) {
        this.progress = percent;
        repaint();
    }

    public String getValue() {
        return value;
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

            // Icon tile
            if (icon != null) {
                int tile = 34;
                int tx = w - pad - tile;
                int ty = pad - 2;
                g2.setColor(UIStyles.alpha(accent, 34));
                g2.fillRoundRect(tx, ty, tile, tile, 10, 10);
                icon.paintIcon(this, g2, tx + (tile - icon.getIconWidth()) / 2,
                        ty + (tile - icon.getIconHeight()) / 2);
            }

            // Label
            g2.setFont(UIStyles.LABEL);
            g2.setColor(UIStyles.MUTED);
            g2.drawString(label == null ? "" : label.toUpperCase(), pad, pad + 10);

            // Value
            g2.setFont(UIStyles.CARD_VALUE);
            g2.setColor(UIStyles.INK);
            FontMetrics vm = g2.getFontMetrics();
            int valueTop = 48 + vm.getAscent();
            g2.drawString(value == null ? "—" : value, pad, valueTop);

            // Caption
            g2.setFont(UIStyles.CAPTION);
            g2.setColor(UIStyles.MUTED_LIGHT);
            g2.drawString(caption == null ? "" : caption, pad, valueTop + 20);

            // Progress bar
            if (progress >= 0) {
                int barY = h - 15;
                int barW = w - pad * 2;
                g2.setColor(UIStyles.CANVAS_DEEP);
                g2.fillRoundRect(pad, barY, barW, 4, 4, 4);
                int fill = (int) Math.round(barW * Math.max(0, Math.min(100, progress)) / 100.0);
                if (fill > 0) {
                    g2.setColor(accent);
                    g2.fillRoundRect(pad, barY, Math.max(4, fill), 4, 4, 4);
                }
            }
        } finally {
            g2.dispose();
        }
    }
}
