package ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import util.UIStyles;

/**
 * A styled text field with floating-label support, focus ring, and error state.
 *
 * <p>States:<br>
 *   default - Line border in LINE color<br>
 *   focused - 2px ACCENT border<br>
 *   error - 2px DANGER border with red text hint
 */
public class FormField extends JTextField {

    private static final long serialVersionUID = 1L;

    private String placeholder;
    private boolean error = false;

    public FormField() {
        this("");
    }

    public FormField(String placeholder) {
        this.placeholder = placeholder;
        setFont(UIStyles.BODY);
        setMargin(new Insets(10, 12, 10, 12));
        setBorder(new LineBorder(UIStyles.LINE, 1, true));
        setBackground(UIStyles.CARD);

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!error) {
                    setBorder(new LineBorder(UIStyles.ACCENT, 2, true));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!error) {
                    setBorder(new LineBorder(UIStyles.LINE, 1, true));
                }
            }
        });
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setError(boolean error) {
        this.error = error;
        if (error) {
            setBorder(new LineBorder(UIStyles.DANGER, 2, true));
        } else if (!hasFocus()) {
            setBorder(new LineBorder(UIStyles.LINE, 1, true));
        } else {
            setBorder(new LineBorder(UIStyles.ACCENT, 2, true));
        }
        repaint();
    }

    public boolean isError() {
        return error;
    }

    public void clearError() {
        setError(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if ((getText() == null || getText().isEmpty()) && !hasFocus() && placeholder != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            UIStyles.applyQualityHints(g2);
            g2.setColor(error ? UIStyles.DANGER : UIStyles.MUTED_LIGHT);
            g2.setFont(getFont().deriveFont(Font.PLAIN));
            g2.drawString(placeholder, 12, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2);
            g2.dispose();
        }
    }

    @Override
    public boolean isOpaque() {
        return true;
    }
}
