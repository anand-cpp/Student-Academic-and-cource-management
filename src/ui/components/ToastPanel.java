package ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import util.UIStyles;

/**
 * A toast notification that appears briefly in the corner of the screen.
 *
 * <p>Types:<br>
 *   success - green background<br>
 *   error - red background<br>
 *   warning - amber background<br>
 *   info - blue background
 */
public class ToastPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public ToastPanel(String message, Type type) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 12, 8));
        setOpaque(false);
        setBorder(new javax.swing.border.EmptyBorder(12, 16, 12, 16));

        Color bg;
        Color fg;
        String icon;
        switch (type) {
            case SUCCESS:
                bg = UIStyles.SUCCESS;
                fg = UIStyles.INVERSE;
                icon = "\u2713";
                break;
            case ERROR:
                bg = UIStyles.DANGER;
                fg = UIStyles.INVERSE;
                icon = "\u2716";
                break;
            case WARNING:
                bg = UIStyles.WARNING;
                fg = UIStyles.INVERSE;
                icon = "\u26A0";
                break;
            case INFO:
            default:
                bg = UIStyles.INFO;
                fg = UIStyles.INVERSE;
                icon = "\u2139";
                break;
        }

        RoundedPanel container = new RoundedPanel();
        container.setRadius(8);
        container.setFill(bg);
        container.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Symbol", Font.BOLD, 14));
        iconLabel.setForeground(fg);
        container.add(iconLabel);

        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(UIStyles.BODY_SM);
        msgLabel.setForeground(fg);
        container.add(msgLabel);

        add(container);
    }

    public enum Type {
        SUCCESS, ERROR, WARNING, INFO
    }
}
