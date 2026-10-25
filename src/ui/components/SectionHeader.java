package ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import util.UIStyles;

/**
 * Editorial page header: eyebrow, serif title, supporting sentence and an
 * optional right-aligned action area.
 */
public class SectionHeader extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JLabel eyebrowLabel;
    private final JLabel titleLabel;
    private final JLabel subtitleLabel;
    private final JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

    public SectionHeader(String title, String description) {
        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(26, 28, 14, 28));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        eyebrowLabel = new JLabel(" ");
        eyebrowLabel.setFont(UIStyles.EYEBROW);
        eyebrowLabel.setForeground(UIStyles.ACCENT);

        titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyles.DISPLAY);
        titleLabel.setForeground(UIStyles.INK);

        subtitleLabel = new JLabel(description == null ? " " : description);
        subtitleLabel.setFont(UIStyles.BODY_SM);
        subtitleLabel.setForeground(UIStyles.MUTED);

        text.add(eyebrowLabel);
        text.add(Box.createVerticalStrut(5));
        text.add(titleLabel);
        text.add(Box.createVerticalStrut(4));
        text.add(subtitleLabel);

        add(text, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        actions.setOpaque(false);
        add(actions, gbc);
    }

    public void setEyebrow(String text) {
        eyebrowLabel.setText(text == null ? " " : text.toUpperCase());
        repaint();
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
    }

    public void addAction(java.awt.Component component) {
        actions.add(component);
        revalidate();
        repaint();
    }

    public JLabel getTitleLabel() {
        return titleLabel;
    }
}
