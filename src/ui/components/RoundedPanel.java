package ui.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import util.UIStyles;

/**
 * A panel with rounded corners and optional elevation shadow.
 *
 * <p>Use {@link #setShadow(boolean)} to enable a soft drop shadow for
 * elevation (cards, modals, floating panels). Use {@link #setFill(Color)}
 * to set the background color. The panel paints its own background so that
 * rounded corners are respected regardless of the layout manager.
 */
public class RoundedPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private int radius = UIStyles.BORDER_RADIUS;
    private boolean shadow = false;
    private Color fill = UIStyles.CARD;
    private Color stroke = UIStyles.LINE;
    private float strokeWidth = 1f;

    public RoundedPanel() {
        setOpaque(false);
        setBackground(UIStyles.CARD);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
    }

    public RoundedPanel(int radius) {
        this();
        this.radius = radius;
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

    public void setStroke(Color stroke, float width) {
        this.stroke = stroke;
        this.strokeWidth = width;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UIStyles.applyQualityHints(g2);

        int w = getWidth();
        int h = getHeight();
        Insets insets = getInsets();

        // Paint shadow first (behind the panel)
        if (shadow) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
            g2.setColor(UIStyles.SHADOW);
            g2.fillRoundRect(insets.left + 4, insets.top + 4,
                    w - insets.left - insets.right - 8, h - insets.top - insets.bottom - 8,
                    radius, radius);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        // Paint background fill
        g2.setColor(fill);
        g2.fillRoundRect(insets.left, insets.top,
                w - insets.left - insets.right, h - insets.top - insets.bottom,
                radius, radius);

        // Paint stroke/border
        if (stroke != null && strokeWidth > 0) {
            g2.setColor(stroke);
            g2.setStroke(new java.awt.BasicStroke(strokeWidth));
            g2.drawRoundRect(insets.left, insets.top,
                    w - insets.left - insets.right - 1, h - insets.top - insets.bottom - 1,
                    radius, radius);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public boolean isOpaque() {
        return false;
    }
}
