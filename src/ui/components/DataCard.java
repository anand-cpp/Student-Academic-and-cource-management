package ui.components;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import util.UIStyles;

/**
 * White surface with a compact header (title, count chip, actions), a hairline
 * divider and a stretchable body area. Used for tables, forms and report panes.
 */
public class DataCard extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JLabel titleLabel;
    private final JLabel subtitleLabel;
    private final JLabel countChip;
    private final JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
    private final JPanel body = new JPanel(new BorderLayout());

    public DataCard(String title, String subtitle) {
        setOpaque(false);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(14, 18, 12, 14));

        JPanel text = new JPanel(new BorderLayout(0, 2));
        text.setOpaque(false);

        titleLabel = new ElidedLabel(title);
        titleLabel.setFont(UIStyles.H3);
        titleLabel.setForeground(UIStyles.INK);

        subtitleLabel = new ElidedLabel(subtitle == null ? "" : subtitle);
        subtitleLabel.setFont(UIStyles.CAPTION);
        subtitleLabel.setForeground(UIStyles.MUTED);

        text.add(titleLabel, BorderLayout.NORTH);
        text.add(subtitleLabel, BorderLayout.CENTER);

        countChip = new JLabel(" ") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    UIStyles.applyQualityHints(g2);
                    g2.setColor(UIStyles.CANVAS_DEEP);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                } finally {
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        countChip.setFont(UIStyles.CAPTION_BOLD);
        countChip.setForeground(UIStyles.MUTED);
        countChip.setBorder(new EmptyBorder(3, 9, 3, 9));
        countChip.setVisible(false);

        JPanel chipWrap = new JPanel();
        chipWrap.setOpaque(false);
        chipWrap.setLayout(new BoxLayout(chipWrap, BoxLayout.Y_AXIS));
        countChip.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
        chipWrap.add(countChip);
        chipWrap.add(Box.createVerticalGlue());

        JPanel left = new JPanel(new BorderLayout(10, 0));
        left.setOpaque(false);
        left.add(text, BorderLayout.CENTER);
        left.add(chipWrap, BorderLayout.EAST);

        actions.setOpaque(false);
        header.add(left, BorderLayout.CENTER);
        header.add(actions, BorderLayout.EAST);

        JComponent divider = new JComponent() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(UIStyles.LINE);
                g.drawLine(0, 0, getWidth(), 0);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, 1);
            }
        };

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(divider, BorderLayout.SOUTH);

        body.setOpaque(false);

        add(top, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
    }

    /** Replace the body with a component that stretches to fill the card. */
    public void setBody(JComponent component) {
        body.removeAll();
        body.add(component, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void setCount(String text) {
        boolean show = text != null && !text.isBlank();
        countChip.setText(show ? text : " ");
        countChip.setVisible(show);
        revalidate();
    }

    public void setSubtitle(String text) {
        subtitleLabel.setText(text == null ? " " : text);
    }

    public void setTitle(String text) {
        titleLabel.setText(text);
    }

    public void addHeaderAction(java.awt.Component component) {
        actions.add(component);
        revalidate();
    }

    public JPanel getBody() {
        return body;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            UIStyles.applyQualityHints(g2);
            int w = getWidth();
            int h = getHeight();
            int r = UIStyles.RADIUS_LG;
            g2.setColor(new Color(20, 32, 25, 9));
            g2.fillRoundRect(1, 2, w - 2, h - 2, r, r);
            g2.setColor(UIStyles.SURFACE);
            g2.fillRoundRect(0, 0, w - 1, h - 2, r, r);
            g2.setColor(UIStyles.LINE);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, w - 1, h - 2, r, r);
        } finally {
            g2.dispose();
        }
        super.paintComponent(g);
    }
}
