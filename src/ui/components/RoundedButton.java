package ui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import util.UIStyles;

/**
 * A rounded, themeable button with hover and pressed states.
 *
 * <p>Styles:<br>
 *   {@link #applyPrimary()} - Navy fill, white text (primary action)<br>
 *   {@link #applyAccent()} - Indigo fill, white text (secondary action)<br>
 *   {@link #applySuccess()} - Emerald fill, white text (save/create)<br>
 *   {@link #applyDanger()} - Rose fill, white text (delete/destructive)<br>
 *   {@link #applyGhost()} - Transparent fill, ink text, line border (subtle)<br>
 *   {@link #applyOutline()} - White fill, ink text, line border (form actions)
 */
public class RoundedButton extends JButton {

    private static final long serialVersionUID = 1L;

    private Color fill;
    private Color fillHover;
    private Color fillPressed;
    private Color text;
    private Color textHover;
    private Color borderColor;
    private boolean bordered;

    public RoundedButton(String text) {
        super(text);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(UIStyles.BUTTON);
        setMargin(new java.awt.Insets(10, 20, 10, 20));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                repaint();
            }
        });

        applyPrimary();
    }

    public void applyPrimary() {
        fill = UIStyles.PRIMARY;
        fillHover = UIStyles.PRIMARY_DARK;
        fillPressed = new Color(9, 17, 30);
        text = UIStyles.INVERSE;
        textHover = UIStyles.INVERSE;
        borderColor = null;
        bordered = false;
        repaint();
    }

    public void applyAccent() {
        fill = UIStyles.ACCENT;
        fillHover = UIStyles.ACCENT_DARK;
        fillPressed = new Color(49, 46, 161);
        text = UIStyles.INVERSE;
        textHover = UIStyles.INVERSE;
        borderColor = null;
        bordered = false;
        repaint();
    }

    public void applySuccess() {
        fill = UIStyles.SUCCESS;
        fillHover = new Color(11, 150, 101);
        fillPressed = new Color(6, 122, 83);
        text = UIStyles.INVERSE;
        textHover = UIStyles.INVERSE;
        borderColor = null;
        bordered = false;
        repaint();
    }

    public void applyDanger() {
        fill = UIStyles.DANGER;
        fillHover = new Color(200, 48, 48);
        fillPressed = new Color(164, 34, 34);
        text = UIStyles.INVERSE;
        textHover = UIStyles.INVERSE;
        borderColor = null;
        bordered = false;
        repaint();
    }

    public void applyGhost() {
        fill = null;
        fillHover = UIStyles.CARD_HOVER;
        fillPressed = UIStyles.LINE;
        text = UIStyles.TEXT_BODY;
        textHover = UIStyles.PRIMARY;
        borderColor = null;
        bordered = false;
        repaint();
    }

    public void applyOutline() {
        fill = null;
        fillHover = UIStyles.CARD_HOVER;
        fillPressed = UIStyles.LINE;
        text = UIStyles.INK;
        textHover = UIStyles.PRIMARY;
        borderColor = UIStyles.LINE;
        bordered = true;
        repaint();
    }

    public void setDisabled(boolean disabled) {
        setEnabled(!disabled);
        if (disabled) {
            text = UIStyles.MUTED_LIGHT;
            fill = UIStyles.MUTED_LIGHT;
            borderColor = UIStyles.LINE;
        } else {
            // Restore would need tracking previous state; keep simple
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UIStyles.applyQualityHints(g2);

        int w = getWidth();
        int h = getHeight();
        boolean hovered = getModel().isRollover();
        boolean pressed = getModel().isPressed();

        Color bg = fill;
        if (disabled()) {
            bg = UIStyles.MUTED_LIGHT;
        } else if (pressed && fillPressed != null) {
            bg = fillPressed;
        } else if (hovered && fillHover != null) {
            bg = fillHover;
        }

        // Background
        if (bg != null) {
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w, h, 10, 10);
        }

        // Border
        if (bordered && borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new java.awt.BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);
        }

        // Text
        g2.setColor(disabled() ? UIStyles.MUTED_LIGHT : (hovered ? textHover : text));
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        String t = getText();
        int tw = fm.stringWidth(t);
        int th = fm.getHeight();
        g2.drawString(t, (w - tw) / 2, (h + th) / 2 - 4);

        g2.dispose();
    }

    private boolean disabled() {
        return !isEnabled();
    }

    @Override
    public boolean isOpaque() {
        return false;
    }
}


