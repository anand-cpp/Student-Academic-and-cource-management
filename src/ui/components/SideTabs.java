package ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import util.UIStyles;

/**
 * Vertical section navigator for report pages. Each row shows a two-digit index
 * and a label; the active row is filled with forest and marked by an accent
 * rail. Painted manually, so it needs no look-and-feel support.
 */
public class SideTabs extends JPanel {

    private static final long serialVersionUID = 1L;

    private final List<Item> items = new ArrayList<>();
    private final List<Runnable> listeners = new ArrayList<>();
    private int selected = 0;

    public SideTabs(String[] labels) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(8, 10, 8, 10));
        if (labels != null) {
            for (int i = 0; i < labels.length; i++) {
                Item item = new Item(i, labels[i]);
                items.add(item);
                add(item);
                add(javax.swing.Box.createVerticalStrut(2));
            }
        }
        if (!items.isEmpty()) {
            items.get(0).setActive(true);
        }
        add(javax.swing.Box.createVerticalGlue());
    }

    public void addSelectionListener(Runnable listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public int getSelectedIndex() {
        return selected;
    }

    public void setSelectedIndex(int index) {
        if (index < 0 || index >= items.size() || index == selected) {
            return;
        }
        selected = index;
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setActive(i == index);
        }
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

    private final class Item extends JPanel {

        private static final long serialVersionUID = 1L;

        private final int index;
        private final String label;
        private boolean active;
        private boolean hover;

        Item(int index, String label) {
            this.index = index;
            this.label = label;
            setOpaque(false);
            setLayout(new java.awt.BorderLayout());
            setBorder(new EmptyBorder(0, 0, 0, 6));
            setPreferredSize(new Dimension(220, 40));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            setMinimumSize(new Dimension(120, 40));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    setSelectedIndex(Item.this.index);
                }
            });
        }

        void setActive(boolean active) {
            this.active = active;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                UIStyles.applyQualityHints(g2);
                int w = getWidth();
                int h = getHeight();

                if (active) {
                    g2.setColor(UIStyles.PRIMARY);
                    g2.fillRoundRect(0, 0, w, h, 9, 9);
                    g2.setColor(UIStyles.alpha(Color.WHITE, 60));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 9, 9);
                } else if (hover) {
                    g2.setColor(UIStyles.PRIMARY_LIGHT);
                    g2.fillRoundRect(0, 0, w, h, 9, 9);
                }

                Color numberColor = active ? UIStyles.alpha(Color.WHITE, 160)
                        : (hover ? UIStyles.PRIMARY : UIStyles.MUTED_LIGHT);
                Color textColor = active ? Color.WHITE : (hover ? UIStyles.PRIMARY_DARK : UIStyles.TEXT_BODY);

                g2.setFont(UIStyles.EYEBROW);
                g2.setColor(numberColor);
                g2.drawString(String.format("%02d", index + 1), 13, h / 2 + 4);

                g2.setFont(active ? UIStyles.H3 : UIStyles.BODY);
                g2.setColor(textColor);
                FontMetrics fm = g2.getFontMetrics();
                String text = label;
                int max = w - 54;
                if (fm.stringWidth(text) > max) {
                    while (text.length() > 3 && fm.stringWidth(text + "…") > max) {
                        text = text.substring(0, text.length() - 1);
                    }
                    text = text + "…";
                }
                g2.drawString(text, 40, (h - fm.getHeight()) / 2 + fm.getAscent());

                if (active) {
                    g2.setColor(UIStyles.SIDEBAR_ACCENT);
                    g2.fillRoundRect(w - 5, h / 2 - 7, 3, 14, 3, 3);
                }
            } finally {
                g2.dispose();
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(220, 40);
        }
    }

    /** Convenience: the component that should be shown in a card. */
    public Component asComponent() {
        return this;
    }
}
