package ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import util.UIStyles;

/**
 * Flat, custom-painted button with five visual variants.
 *
 * <p>States are drawn manually (normal, hover, pressed, focused) so the button
 * looks identical across platforms and requires no external look-and-feel.
 */
public class RoundedButton extends JButton {

    private static final long serialVersionUID = 1L;

    public enum Variant {
        PRIMARY, SECONDARY, GHOST, DANGER, DANGER_SOLID, ACCENT
    }

    private Variant variant = Variant.PRIMARY;
    private Color iconColor;
    private boolean hover;
    private boolean pressed;

    public RoundedButton(String text) {
        super(text);
        setFont(UIStyles.BUTTON);
        setForeground(UIStyles.INVERSE);
        setBorder(new EmptyBorder(0, 16, 0, 16));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(0, UIStyles.CONTROL_HEIGHT));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                pressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    public RoundedButton(String text, AppIcon.Glyph glyph) {
        this(text);
        setIcon(new AppIcon(glyph, 15, null));
    }

    public void applyPrimary() {
        variant = Variant.PRIMARY;
        setForeground(UIStyles.INVERSE);
        setFont(UIStyles.BUTTON);
        repaint();
    }

    public void applyAccent() {
        variant = Variant.ACCENT;
        setForeground(UIStyles.INVERSE);
        repaint();
    }

    public void applySuccess() {
        variant = Variant.PRIMARY;
        setForeground(UIStyles.INVERSE);
        repaint();
    }

    public void applyDanger() {
        variant = Variant.DANGER;
        setForeground(UIStyles.DANGER);
        repaint();
    }

    public void applyDangerSolid() {
        variant = Variant.DANGER_SOLID;
        setForeground(UIStyles.INVERSE);
        repaint();
    }

    public void applyGhost() {
        variant = Variant.GHOST;
        setForeground(UIStyles.TEXT_BODY);
        repaint();
    }

    public void applyOutline() {
        variant = Variant.SECONDARY;
        setForeground(UIStyles.INK);
        repaint();
    }

    public void applySecondary() {
        applyOutline();
    }

    public boolean isPrimary() {
        return variant == Variant.PRIMARY;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        if (getText() == null || getText().isEmpty()) {
            int side = Math.max(UIStyles.CONTROL_HEIGHT, getHeight());
            return new Dimension(side, UIStyles.CONTROL_HEIGHT);
        }
        FontMetrics fm = getFontMetrics(getFont());
        int w = fm.stringWidth(getText()) + 34;
        if (getIcon() != null) {
            w += getIcon().getIconWidth() + 7;
        }
        return new Dimension(w, UIStyles.CONTROL_HEIGHT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            UIStyles.applyQualityHints(g2);
            int w = getWidth();
            int h = getHeight();
            int r = UIStyles.RADIUS;
            boolean enabled = isEnabled();

            Color bg;
            Color fg;
            Color border = null;

            switch (variant) {
                case PRIMARY -> {
                    bg = hover ? UIStyles.PRIMARY_DARK : UIStyles.PRIMARY;
                    fg = UIStyles.INVERSE;
                }
                case ACCENT -> {
                    bg = hover ? UIStyles.ACCENT_DARK : UIStyles.ACCENT;
                    fg = UIStyles.INVERSE;
                }
                case SECONDARY -> {
                    bg = hover ? UIStyles.CARD_HOVER : UIStyles.SURFACE;
                    fg = UIStyles.INK;
                    border = hover ? UIStyles.LINE_STRONG : UIStyles.LINE;
                }
                case GHOST -> {
                    bg = hover ? UIStyles.CARD_HOVER : new Color(0, 0, 0, 0);
                    fg = UIStyles.TEXT_BODY;
                }
                case DANGER -> {
                    bg = hover ? new Color(0xF6, 0xDC, 0xD8) : UIStyles.DANGER_LIGHT;
                    fg = UIStyles.DANGER;
                }
                case DANGER_SOLID -> {
                    bg = hover ? new Color(0x9C, 0x38, 0x31) : UIStyles.DANGER;
                    fg = UIStyles.INVERSE;
                }
                default -> throw new IllegalStateException();
            }

            if (!enabled) {
                bg = variant == Variant.GHOST ? new Color(0, 0, 0, 0) : UIStyles.CARD_HOVER;
                fg = UIStyles.MUTED_LIGHT;
                border = UIStyles.LINE;
            }

            if (pressed && enabled) {
                bg = UIStyles.mix(bg, Color.BLACK, 0.12);
            }

            if (bg.getAlpha() > 0) {
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w, h, r, r);
            }
            if (border != null) {
                g2.setColor(border);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, r, r);
            }
            if (isFocusOwner()) {
                g2.setColor(UIStyles.alpha(UIStyles.PRIMARY, 70));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(2, 2, w - 5, h - 5, r - 1, r - 1);
            }

            setForeground(fg);
            // Paint icon + text ourselves to control spacing precisely.
            Insets in = getInsets();
            FontMetrics fm = getFontMetrics(getFont());
            String text = getText();
            int iconW = getIcon() == null ? 0 : getIcon().getIconWidth();
            int gap = (iconW > 0 && text != null && !text.isEmpty()) ? 8 : 0;
            int textW = (text == null) ? 0 : fm.stringWidth(text);
            int contentW = iconW + gap + textW;
            int x = (w - contentW) / 2;
            int centerY = h / 2;

            if (getIcon() != null) {
                getIcon().paintIcon(this, g2, x, centerY - getIcon().getIconHeight() / 2);
                x += iconW + gap;
            }
            if (text != null && !text.isEmpty()) {
                g2.setFont(getFont());
                g2.setColor(getForeground());
                g2.drawString(text, x, centerY + (fm.getAscent() - fm.getDescent()) / 2);
            }
        } finally {
            g2.dispose();
        }
    }
}
