package util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Reusable Swing styling system.
 *
 * <p>Provides a consistent navy / indigo / white design system with restrained
 * accent colors. Every panel uses these helpers so buttons, labels, tables and
 * cards look identical across the application. This avoids duplicated styling
 * logic and keeps the UI maintainable.
 *
 * <p>Color roles:
 *   PRIMARY   - deep navy (sidebar, header, primary buttons)
 *   ACCENT    - indigo (secondary buttons, active states)
 *   SUCCESS   - green (save, success feedback)
 *   DANGER    - red (delete, destructive feedback)
 *   WARNING   - amber (warning feedback)
 *   INFO      - light blue (informational feedback)
 *   INK       - near-black text
 *   MUTED     - gray secondary text
 *   CARD      - white panel background
 *   LINE      - light gray borders
 *   ROW_ALT   - very light gray alternating table row
 */
public final class UIStyles {

    public static final Color PRIMARY = new Color(31, 41, 55);       // navy #1f2937
    public static final Color PRIMARY_DARK = new Color(17, 24, 39);  // #111827
    public static final Color ACCENT = new Color(79, 70, 229);       // indigo #4f46e5
    public static final Color ACCENT_DARK = new Color(67, 56, 202);  // #4338ca
    public static final Color SUCCESS = new Color(16, 185, 129);     // green #10a57d-ish
    public static final Color DANGER = new Color(220, 38, 38);       // red #dc2626
    public static final Color WARNING = new Color(217, 119, 6);      // amber
    public static final Color INFO = new Color(59, 130, 246);        // blue
    public static final Color INK = new Color(15, 23, 42);           // #0f172a
    public static final Color MUTED = new Color(100, 116, 139);      // #64748b
    public static final Color CARD = Color.WHITE;
    public static final Color LINE = new Color(226, 232, 240);       // #e2e8f0
    public static final Color ROW_ALT = new Color(247, 249, 252);    // #f7f9fc
    public static final Color SELECTION = new Color(226, 232, 240);

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font CARD_VALUE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font CARD_LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    private static final Insets BUTTON_PADDING = new Insets(8, 16, 8, 16);

    private UIStyles() {
        // Utility class - not instantiable
    }

    public static void applyPrimaryButton(JButton button) {
        styleButton(button, PRIMARY, Color.WHITE, PRIMARY_DARK);
    }

    public static void applyAccentButton(JButton button) {
        styleButton(button, ACCENT, Color.WHITE, ACCENT_DARK);
    }

    public static void applySuccessButton(JButton button) {
        styleButton(button, SUCCESS, Color.WHITE, new Color(13, 148, 104));
    }

    public static void applyDangerButton(JButton button) {
        styleButton(button, DANGER, Color.WHITE, new Color(185, 28, 28));
    }

    public static void applyOutlineButton(JButton button) {
        styleButton(button, CARD, INK, new Color(241, 245, 249));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE, 1),
                new EmptyBorder(8, 16, 8, 16)));
    }

    private static void styleButton(JButton button, Color bg, Color fg, Color hover) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setMargin(BUTTON_PADDING);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(8, 16, 8, 16));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hover);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bg);
            }
        });
    }

    public static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE, 1),
                new EmptyBorder(16, 16, 16, 16)));
        return panel;
    }

    public static JLabel heading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADER_FONT);
        label.setForeground(INK);
        return label;
    }

    public static JLabel sub(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SUBTITLE_FONT);
        label.setForeground(MUTED);
        return label;
    }

    public static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BODY_FONT);
        label.setForeground(INK);
        return label;
    }

    public static void styleTable(JTable table) {
        table.setFont(TABLE_FONT);
        table.setRowHeight(30);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(SELECTION);
        table.setSelectionForeground(INK);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(TABLE_HEADER_FONT);
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY));
        table.getTableHeader().setPreferredSize(new Dimension(0, 32));
    }

    /** Alternating row colors for readability. */
    public static class AlternatingRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? CARD : ROW_ALT);
                c.setForeground(INK);
            } else {
                c.setBackground(SELECTION);
                c.setForeground(INK);
            }
            setFont(TABLE_FONT);
            return c;
        }
    }

    /** Center-aligned numeric renderer. */
    public static class CenterRenderer extends DefaultTableCellRenderer {
        public CenterRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? CARD : ROW_ALT);
                c.setForeground(INK);
            } else {
                c.setBackground(SELECTION);
                c.setForeground(INK);
            }
            setFont(TABLE_FONT);
            return c;
        }
    }

    public static Border empty(int top, int left, int bottom, int right) {
        return new EmptyBorder(top, left, bottom, right);
    }

    public static Border line() {
        return BorderFactory.createLineBorder(LINE, 1);
    }

    public static Dimension pref(int w, int h) {
        return new Dimension(w, h);
    }
}
