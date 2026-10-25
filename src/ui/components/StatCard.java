package ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import util.UIStyles;

/**
 * A stat card for the dashboard: large value, label, optional trend indicator.
 *
 * <p>Usage:<br>
 *   {@code new StatCard("Total Students", "1,234", UIStyles.PRIMARY, "+12%")}<br>
 *   The trend can be null if not applicable.
 */
public class StatCard extends RoundedPanel {

    private static final long serialVersionUID = 1L;

    private JLabel valueLabel;
    private JLabel labelLabel;
    private JLabel trendLabel;

    public StatCard(String label, String value, Color accent, String trend) {
        setRadius(16);
        setShadow(true);
        setFill(UIStyles.CARD);
        setStroke(UIStyles.LINE, 1f);

        setLayout(null); // absolute positioning for precise card design

        // Left accent bar
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setBounds(0, 0, 4, getPreferredSize().height);
        accentBar.setOpaque(true);
        add(accentBar);

        // Label
        labelLabel = UIStyles.captionBold(label.toUpperCase());
        labelLabel.setBounds(20, 16, 200, 20);
        add(labelLabel);

        // Value
        valueLabel = new JLabel(value);
        valueLabel.setFont(UIStyles.CARD_VALUE);
        valueLabel.setForeground(accent);
        valueLabel.setBounds(20, 36, 260, 48);
        add(valueLabel);

        // Trend (optional)
        if (trend != null && !trend.isEmpty()) {
            trendLabel = new JLabel(trend);
            trendLabel.setFont(UIStyles.CAPTION);
            boolean positive = trend.startsWith("+") || !trend.startsWith("-");
            trendLabel.setForeground(positive ? UIStyles.SUCCESS : UIStyles.DANGER);
            trendLabel.setBounds(20, 88, 120, 18);
            add(trendLabel);
        }

        // Set preferred size
        setPreferredSize(new Dimension(260, 120));
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setLabel(String label) {
        labelLabel.setText(label.toUpperCase());
    }

    public void setTrend(String trend) {
        if (trendLabel != null) {
            trendLabel.setText(trend);
            boolean positive = trend.startsWith("+") || !trend.startsWith("-");
            trendLabel.setForeground(positive ? UIStyles.SUCCESS : UIStyles.DANGER);
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        // Resize accent bar
        for (int i = 0; i < getComponentCount(); i++) {
            if (getComponent(i) instanceof JPanel) {
                getComponent(i).setBounds(0, 0, 4, height);
                break;
            }
        }
    }
}
