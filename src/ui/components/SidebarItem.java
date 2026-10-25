package ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import util.UIStyles;

/**
 * A sidebar navigation item with hover and active states.
 *
 * <p>Visual states:<br>
 *   default - transparent background, muted text<br>
 *   hover - slate-50 background, ink text<br>
 *   active - indigo-50 background, indigo text, left accent bar
 */
public class SidebarItem extends JPanel {

    private static final long serialVersionUID = 1L;

    private JLabel label;
    private JLabel iconLabel;
    private JPanel accentBar;
    private boolean active = false;
    private final List<ActionListener> listeners = new ArrayList<>();

    public SidebarItem(String text, String icon) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setOpaque(false);
        setBorder(new javax.swing.border.EmptyBorder(12, 16, 12, 16));
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        // Left accent bar (shown when active)
        accentBar = new JPanel();
        accentBar.setBackground(UIStyles.ACCENT);
        accentBar.setOpaque(true);
        accentBar.setVisible(false);

        // Icon
        iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18));
        iconLabel.setForeground(UIStyles.MUTED);
        iconLabel.setPreferredSize(new Dimension(24, 24));

        // Label
        label = new JLabel(text);
        label.setFont(UIStyles.SIDEBAR_ITEM);
        label.setForeground(UIStyles.MUTED);

        add(accentBar);
        add(iconLabel);
        add(label);

        // Hover effects
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!active) {
                    setBackground(UIStyles.CARD_HOVER);
                    label.setForeground(UIStyles.INK);
                    iconLabel.setForeground(UIStyles.TEXT_BODY);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!active) {
                    setBackground(null);
                    label.setForeground(UIStyles.MUTED);
                    iconLabel.setForeground(UIStyles.MUTED);
                }
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                setActive(true);
                for (ActionListener l : listeners) {
                    l.actionPerformed(new ActionEvent(SidebarItem.this, ActionEvent.ACTION_PERFORMED, null));
                }
            }
        });
    }

    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            setBackground(UIStyles.ACCENT_LIGHT);
            label.setForeground(UIStyles.ACCENT);
            iconLabel.setForeground(UIStyles.ACCENT);
            accentBar.setVisible(true);
        } else {
            setBackground(null);
            label.setForeground(UIStyles.MUTED);
            iconLabel.setForeground(UIStyles.MUTED);
            accentBar.setVisible(false);
        }
        repaint();
    }

    public boolean isActive() {
        return active;
    }

    public void setLabel(String text) {
        label.setText(text);
    }
}

