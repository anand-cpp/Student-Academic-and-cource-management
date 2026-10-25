package ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import util.UIStyles;

/**
 * Search input with a leading magnifier and a clear affordance.
 */
public class SearchField extends JTextField {

    private static final long serialVersionUID = 1L;

    private String placeholder;
    private boolean focused;

    public SearchField(String placeholder) {
        this.placeholder = placeholder;
        setFont(UIStyles.BODY);
        setForeground(UIStyles.INK);
        setCaretColor(UIStyles.PRIMARY);
        setOpaque(false);
        setBorder(new EmptyBorder(0, 38, 0, 30));
        setPreferredSize(new java.awt.Dimension(310, UIStyles.CONTROL_HEIGHT));
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                focused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                focused = false;
                repaint();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!getText().isEmpty() && e.getX() > getWidth() - 30) {
                    setText("");
                    repaint();
                }
            }
        });
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            UIStyles.applyQualityHints(g2);
            int w = getWidth();
            int h = getHeight();
            int r = UIStyles.RADIUS;

            g2.setColor(UIStyles.SURFACE);
            g2.fillRoundRect(0, 0, w - 1, h - 1, r, r);
            g2.setColor(focused ? UIStyles.PRIMARY : UIStyles.LINE);
            g2.setStroke(new BasicStroke(focused ? 1.6f : 1f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, r, r);

            new AppIcon(AppIcon.Glyph.SEARCH, 16, UIStyles.MUTED_LIGHT).paintIcon(this, g2, 13, (h - 16) / 2);

            if (getText().isEmpty() && placeholder != null && !placeholder.isEmpty()) {
                g2.setFont(getFont());
                g2.setColor(UIStyles.MUTED_LIGHT);
                java.awt.FontMetrics fm = g2.getFontMetrics();
                g2.drawString(placeholder, 38, (h - fm.getHeight()) / 2 + fm.getAscent());
            }

            if (!getText().isEmpty()) {
                new AppIcon(AppIcon.Glyph.CLOSE, 12, UIStyles.MUTED_LIGHT)
                        .paintIcon(this, g2, w - 22, (h - 12) / 2);
            }
        } finally {
            g2.dispose();
        }
        super.paintComponent(g);
    }
}
