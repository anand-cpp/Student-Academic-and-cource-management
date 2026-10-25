package ui.components;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import util.UIStyles;

/**
 * A section header for module panels: title + description + optional action.
 */
public class SectionHeader extends JPanel {

    private static final long serialVersionUID = 1L;

    public SectionHeader(String title, String description) {
        setLayout(new java.awt.BorderLayout(8, 4));
        setOpaque(false);
        setBorder(new javax.swing.border.EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyles.H2);
        titleLabel.setForeground(UIStyles.INK);
        add(titleLabel, java.awt.BorderLayout.NORTH);

        if (description != null && !description.isEmpty()) {
            JLabel descLabel = new JLabel(description);
            descLabel.setFont(UIStyles.BODY_SM);
            descLabel.setForeground(UIStyles.MUTED);
            add(descLabel, java.awt.BorderLayout.SOUTH);
        }
    }

    public SectionHeader(String title) {
        this(title, null);
    }
}
