package ui.components;

import java.awt.Color;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import util.UIStyles;

/**
 * An empty state component shown when a table or list has no data.
 *
 * <p>Displays an icon, title, and optional description centered in the panel.
 */
public class EmptyState extends JPanel {

    private static final long serialVersionUID = 1L;

    public EmptyState(String icon, String title, String description) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));
        setOpaque(false);
        setBorder(new javax.swing.border.EmptyBorder(60, 40, 60, 40));

        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new java.awt.Font("Segoe UI Symbol", Font.PLAIN, 48));
        iconLabel.setForeground(UIStyles.MUTED_LIGHT);
        add(iconLabel);

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyles.H3);
        titleLabel.setForeground(UIStyles.TEXT_BODY);
        add(titleLabel);

        // Description (optional)
        if (description != null && !description.isEmpty()) {
            JLabel descLabel = new JLabel(description);
            descLabel.setFont(UIStyles.BODY_SM);
            descLabel.setForeground(UIStyles.MUTED);
            descLabel.setHorizontalAlignment(SwingConstants.CENTER);
            descLabel.setPreferredSize(new java.awt.Dimension(300, 20));
            add(descLabel);
        }
    }
}


