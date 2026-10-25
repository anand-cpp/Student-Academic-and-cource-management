package ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import util.UIStyles;

/**
 * A rounded surface with an optional hairline border and a soft bottom shadow.
 * The building block for every card in the console.
 */
public class RoundedPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private int radius = UIStyles.RADIUS_LG;
    private boolean shadow = true;
    private Color fill = UIStyles.SURFACE;
    private Color borderColor = UIStyles.LINE;
    private boolean borderVisible = true;

    public RoundedPanel() {
        this(new java.awt.BorderLayout());
    }

    public RoundedPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
        repaint();
    }

    public void setFill(Color fill) {
        this.fill = fill;
        repaint();
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint();
    }

    public void setBorderVisible(boolean visible) {
        this.borderVisible = visible;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            UIStyles.applyQualityHints(g2);
            int w = getWidth();
            int h = getHeight();
            int r = Math.min(radius, Math.min(w, h));

            if (shadow) {
                // Two very soft offset layers instead of a hard drop shadow.
                g2.setColor(new Color(20, 32, 25, 10));
                g2.fillRoundRect(1, 2, w - 2, h - 2, r, r);
                g2.setColor(new Color(20, 32, 25, 8));
                g2.fillRoundRect(0, 1, w - 1, h - 2, r, r);
            }

            g2.setColor(fill);
            g2.fillRoundRect(0, 0, w - 1, h - 2, r, r);

            if (borderVisible) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w - 1, h - 2, r, r);
            }
        } finally {
            g2.dispose();
        }
        super.paintComponent(g);
    }
}
