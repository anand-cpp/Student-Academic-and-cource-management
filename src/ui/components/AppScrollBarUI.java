package ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicScrollBarUI;
import util.UIStyles;

/**
 * Slim, transparent scrollbar with a rounded thumb and no arrow buttons.
 * Applied to every scroll pane by hand so the console looks identical across
 * platforms without installing a look-and-feel.
 */
public class AppScrollBarUI extends BasicScrollBarUI {

    private final boolean light;

    public AppScrollBarUI() {
        this(true);
    }

    public AppScrollBarUI(boolean light) {
        this.light = light;
    }

    @Override
    protected void configureScrollBarColors() {
        trackColor = new Color(0, 0, 0, 0);
        thumbColor = light ? new Color(0xC9, 0xC4, 0xB6) : new Color(0x2A, 0x45, 0x39);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return zeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return zeroButton();
    }

    private JButton zeroButton() {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(0, 0));
        b.setMinimumSize(new Dimension(0, 0));
        b.setMaximumSize(new Dimension(0, 0));
        b.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        b.setOpaque(false);
        b.setFocusable(false);
        return b;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        // Transparent track.
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            UIStyles.applyQualityHints(g2);
            boolean vertical = scrollbar.getOrientation() == JScrollBar.VERTICAL;
            int pad = 5;
            if (vertical) {
                int w = 6;
                int x = thumbBounds.x + (thumbBounds.width - w) / 2;
                g2.setColor(thumbColor);
                g2.fillRoundRect(x, thumbBounds.y + pad, w, Math.max(20, thumbBounds.height - pad * 2), w, w);
            } else {
                int h = 6;
                int y = thumbBounds.y + (thumbBounds.height - h) / 2;
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x + pad, y, Math.max(20, thumbBounds.width - pad * 2), h, h, h);
            }
        } finally {
            g2.dispose();
        }
    }

    @Override
    protected Dimension getMinimumThumbSize() {
        return new Dimension(28, 28);
    }

    /** Style a scroll pane and its viewport for the console. */
    public static void apply(JScrollPane pane) {
        if (pane == null) {
            return;
        }
        pane.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        pane.setOpaque(false);
        pane.getViewport().setOpaque(true);
        pane.getViewport().setBackground(UIStyles.SURFACE);
        pane.setBackground(UIStyles.SURFACE);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JScrollBar vertical = pane.getVerticalScrollBar();
        vertical.setUI(new AppScrollBarUI());
        vertical.setPreferredSize(new Dimension(10, 0));
        vertical.setUnitIncrement(18);
        vertical.setBlockIncrement(120);
        vertical.setOpaque(false);

        JScrollBar horizontal = pane.getHorizontalScrollBar();
        horizontal.setUI(new AppScrollBarUI());
        horizontal.setPreferredSize(new Dimension(0, 10));
        horizontal.setOpaque(false);
    }
}
