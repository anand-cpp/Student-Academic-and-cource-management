package ui.components;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import util.UIStyles;

/**
 * Notification card with an accent rail, glyph and message. Fades in and out
 * under the control of {@link ToastManager}.
 */
public class ToastPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public enum Type { SUCCESS, ERROR, WARNING, INFO }

    private final String message;
    private final Type type;
    private float alpha = 1f;

    public ToastPanel(String message, Type type) {
        this.message = message == null ? "" : message;
        this.type = type == null ? Type.INFO : type;
        setOpaque(false);
        setPreferredSize(new Dimension(352, 68));
        setMaximumSize(new Dimension(352, 68));
    }

    public void setAlpha(float alpha) {
        this.alpha = Math.max(0f, Math.min(1f, alpha));
        repaint();
    }

    public float getAlpha() {
        return alpha;
    }

    private Color color() {
        return switch (type) {
            case SUCCESS -> UIStyles.SUCCESS;
            case ERROR -> UIStyles.DANGER;
            case WARNING -> UIStyles.WARNING;
            case INFO -> UIStyles.INFO;
        };
    }

    private Color tint() {
        return switch (type) {
            case SUCCESS -> UIStyles.SUCCESS_LIGHT;
            case ERROR -> UIStyles.DANGER_LIGHT;
            case WARNING -> UIStyles.WARNING_LIGHT;
            case INFO -> UIStyles.INFO_LIGHT;
        };
    }

    private AppIcon.Glyph glyph() {
        return switch (type) {
            case SUCCESS -> AppIcon.Glyph.CHECK;
            case ERROR -> AppIcon.Glyph.WARNING;
            case WARNING -> AppIcon.Glyph.WARNING;
            case INFO -> AppIcon.Glyph.INFO;
        };
    }

    private String title() {
        return switch (type) {
            case SUCCESS -> "SAVED";
            case ERROR -> "ERROR";
            case WARNING -> "WARNING";
            case INFO -> "NOTICE";
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            UIStyles.applyQualityHints(g2);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            int w = getWidth();
            int h = getHeight();
            int r = 12;

            g2.setColor(new Color(10, 20, 15, 40));
            g2.fillRoundRect(2, 4, w - 4, h - 6, r, r);
            g2.setColor(UIStyles.SURFACE);
            g2.fillRoundRect(0, 0, w - 2, h - 4, r, r);
            g2.setColor(UIStyles.LINE);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, w - 3, h - 5, r, r);

            g2.setColor(color());
            g2.fillRoundRect(0, 10, 4, h - 24, 4, 4);

            g2.setColor(tint());
            g2.fillOval(18, (h - 34) / 2, 34, 34);
            new AppIcon(glyph(), 16, color(), 2f).paintIcon(this, g2, 27, (h - 16) / 2);

            g2.setFont(UIStyles.EYEBROW);
            g2.setColor(color());
            g2.drawString(title(), 64, 26);

            g2.setFont(UIStyles.BODY_SM);
            g2.setColor(UIStyles.INK);
            FontMetrics fm = g2.getFontMetrics();
            String text = message;
            int max = w - 88;
            if (fm.stringWidth(text) > max) {
                while (text.length() > 4 && fm.stringWidth(text + "…") > max) {
                    text = text.substring(0, text.length() - 1);
                }
                text = text + "…";
            }
            g2.drawString(text, 64, 46);
        } finally {
            g2.dispose();
        }
    }
}
