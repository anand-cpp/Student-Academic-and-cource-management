package ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import util.UIStyles;

/**
 * A pill-shaped status badge: colored background, white/ink text, rounded.
 *
 * <p>Styles:<br>
 *   Active - green<br>
 *   Inactive / Dropped - gray<br>
 *   Completed - blue<br>
 *   Present - green<br>
 *   Absent - red<br>
 *   Late - amber<br>
 *   Default - indigo
 */
public class StatusBadge extends JPanel {

    private static final long serialVersionUID = 1L;

    public StatusBadge(String text, Style style) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 8, 2));
        setOpaque(false);

        Color bg, fg;
        switch (style) {
            case ACTIVE:
            case PRESENT:
                bg = UIStyles.SUCCESS;
                fg = UIStyles.INVERSE;
                break;
            case COMPLETED:
                bg = UIStyles.INFO;
                fg = UIStyles.INVERSE;
                break;
            case ABSENT:
            case DROPPED:
                bg = UIStyles.DANGER;
                fg = UIStyles.INVERSE;
                break;
            case LATE:
                bg = UIStyles.WARNING;
                fg = UIStyles.INVERSE;
                break;
            case INACTIVE:
            default:
                bg = UIStyles.ACCENT;
                fg = UIStyles.INVERSE;
                break;
        }

        RoundedPanel pill = new RoundedPanel();
        pill.setRadius(12);
        pill.setFill(bg);
        pill.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 2));

        JLabel label = new JLabel(text);
        label.setFont(UIStyles.BUTTON_SM);
        label.setForeground(fg);
        pill.add(label);

        add(pill);
    }

    public enum Style {
        ACTIVE, INACTIVE, COMPLETED, DROPPED, PRESENT, ABSENT, LATE
    }
}
