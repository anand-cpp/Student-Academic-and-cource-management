package ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import util.UIStyles;

/**
 * Rounded text input with placeholder text, focus ring and inline error state.
 * Drawn entirely with Java2D so it matches the console on every platform.
 */
public class FormField extends JTextField {

    private static final long serialVersionUID = 1L;

    private final String placeholder;
    private boolean focused;
    private boolean error;

    public FormField(String placeholder) {
        this.placeholder = placeholder == null ? "" : placeholder;
        setFont(UIStyles.BODY);
        setForeground(UIStyles.INK);
        setCaretColor(UIStyles.PRIMARY);
        setOpaque(false);
        setBorder(new EmptyBorder(0, 12, 0, 12));
        setPreferredSize(new java.awt.Dimension(200, UIStyles.CONTROL_HEIGHT));
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
    }

    public void setError(boolean error) {
        this.error = error;
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

            Color border = error ? UIStyles.DANGER : (focused ? UIStyles.PRIMARY : UIStyles.LINE);
            g2.setColor(border);
            g2.setStroke(new BasicStroke(focused || error ? 1.6f : 1f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, r, r);

            if (getText().isEmpty() && !placeholder.isEmpty()) {
                g2.setFont(getFont());
                g2.setColor(UIStyles.MUTED_LIGHT);
                java.awt.FontMetrics fm = g2.getFontMetrics();
                g2.drawString(placeholder, 12, (h - fm.getHeight()) / 2 + fm.getAscent());
            }
        } finally {
            g2.dispose();
        }
        super.paintComponent(g);
    }
}
