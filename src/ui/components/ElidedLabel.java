package ui.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.JLabel;
import util.UIStyles;

/**
 * Single-line label that elides its text with an ellipsis when it does not fit,
 * keeping the full value in the tooltip. Used for card titles and subtitles so
 * narrow cards never show text cut mid-word.
 */
public class ElidedLabel extends JLabel {

    private static final long serialVersionUID = 1L;

    public ElidedLabel(String text) {
        super(text == null ? "" : text);
        setToolTipText(getText().isEmpty() ? null : getText());
    }

    @Override
    public void setText(String text) {
        String value = text == null ? "" : text;
        super.setText(value);
        setToolTipText(value.isEmpty() ? null : value);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            UIStyles.applyQualityHints(g2);
            Insets in = getInsets();
            int available = getWidth() - in.left - in.right;
            String full = getText();
            if (full == null || available <= 0) {
                return;
            }
            g2.setFont(getFont());
            java.awt.FontMetrics fm = g2.getFontMetrics();
            String draw = full;
            if (fm.stringWidth(draw) > available) {
                while (draw.length() > 1 && fm.stringWidth(draw + "\u2026") > available) {
                    draw = draw.substring(0, draw.length() - 1);
                }
                draw = draw + "\u2026";
            }
            int y = in.top + (getHeight() - in.top - in.bottom - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(getForeground());
            g2.drawString(draw, in.left, y);
        } finally {
            g2.dispose();
        }
    }
}
