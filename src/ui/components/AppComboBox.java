package ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import util.UIStyles;

/**
 * Rounded combo box with a custom chevron and flat popup rendering.
 */
public class AppComboBox<E> extends JComboBox<E> {

    private static final long serialVersionUID = 1L;

    public AppComboBox() {
        super();
        setOpaque(false);
        setFont(UIStyles.BODY);
        setForeground(UIStyles.INK);
        setBackground(UIStyles.SURFACE);
        setBorder(new EmptyBorder(0, 10, 0, 30));
        setPreferredSize(new Dimension(220, UIStyles.CONTROL_HEIGHT));
        setFocusable(true);

        setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton b = new JButton(new AppIcon(AppIcon.Glyph.CHEVRON_DOWN, 14, UIStyles.MUTED));
                b.setBorder(BorderFactory.createEmptyBorder());
                b.setContentAreaFilled(false);
                b.setFocusable(false);
                b.setOpaque(false);
                b.setPreferredSize(new Dimension(26, 26));
                return b;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                // Background is painted by the combo itself.
            }
        });

        setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(UIStyles.BODY);
                setBorder(new EmptyBorder(7, 10, 7, 10));
                if (index < 0) {
                    // The current value sits on the combo's custom rounded surface.
                    setOpaque(false);
                } else if (isSelected) {
                    setOpaque(true);
                    setBackground(UIStyles.PRIMARY_LIGHT);
                    setForeground(UIStyles.PRIMARY_DARK);
                } else {
                    setOpaque(true);
                    setBackground(UIStyles.SURFACE);
                    setForeground(UIStyles.INK);
                }
                return this;
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            UIStyles.applyQualityHints(g2);
            int w = getWidth();
            int h = getHeight();
            int r = UIStyles.RADIUS;
            g2.setColor(UIStyles.SURFACE);
            g2.fillRoundRect(0, 0, w - 1, h - 1, r, r);
        } finally {
            g2.dispose();
        }
        super.paintComponent(g);
        // The current-value renderer and arrow button paint inside the border,
        // so the outline is drawn last to keep the rounded frame intact.
        Graphics2D outline = (Graphics2D) g.create();
        try {
            UIStyles.applyQualityHints(outline);
            int w = getWidth();
            int h = getHeight();
            int r = UIStyles.RADIUS;
            outline.setColor(isFocusOwner() ? UIStyles.PRIMARY : UIStyles.LINE);
            outline.setStroke(new BasicStroke(isFocusOwner() ? 1.6f : 1f));
            outline.drawRoundRect(0, 0, w - 1, h - 1, r, r);
        } finally {
            outline.dispose();
        }
    }
}
