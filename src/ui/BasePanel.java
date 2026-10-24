package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import util.UIStyles;

/**
 * Base class for all module panels.
 *
 * <p>Provides shared UI helpers so every panel uses the same styling, dialogs,
 * table builder, and form-field factory. This avoids duplicated styling and
 * keeps the interface consistent.
 */
public abstract class BasePanel extends JPanel {

    protected final MainFrame main;

    public BasePanel(MainFrame main) {
        this.main = main;
        setBackground(UIStyles.CARD);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new BorderLayout(10, 10));
    }

    /* ---------- Feedback ---------- */

    protected void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    protected void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    protected void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation",
                JOptionPane.WARNING_MESSAGE);
    }

    protected boolean confirm(String message, String title) {
        return JOptionPane.showConfirmDialog(this, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
                == JOptionPane.YES_OPTION;
    }

    /* ---------- Form helpers ---------- */

    /** Labeled field pair: label above a text field. */
    public static Component[] fieldRow(String labelText, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(4, 4));
        row.setBackground(UIStyles.CARD);
        JLabel label = UIStyles.fieldLabel(labelText);
        field.setFont(UIStyles.BODY_FONT);
        field.setBorder(UIStyles.line());
        field.setPreferredSize(new Dimension(260, 30));
        row.add(label, BorderLayout.NORTH);
        row.add(field, BorderLayout.SOUTH);
        return new Component[] { row, label, field };
    }

    /** A labelled panel with a title. */
    protected JPanel titledPanel(String title, Component... children) {
        JPanel panel = new JPanel();
        panel.setBackground(UIStyles.CARD);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIStyles.LINE, 1), title,
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14), UIStyles.PRIMARY));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 6, 6, 6);
        for (Component c : children) {
            panel.add(c, gbc);
            gbc.gridy++;
        }
        return panel;
    }

    /** Styled action button. */
    protected JButton button(String text, java.awt.Color bg, ActionListener listener) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(UIStyles.BUTTON_FONT);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setBorder(new EmptyBorder(8, 16, 8, 16));
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        b.addActionListener(listener);
        return b;
    }

    /* ---------- Table builder ---------- */

    /**
     * Build a styled JTable from data and column names, wrapped in a scroll pane.
     * Alternating row colors and centered numeric columns are applied.
     */
    protected JScrollPane buildTable(JTable table, Object[][] data, String[] columns) {
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(model);
        UIStyles.styleTable(table);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new UIStyles.AlternatingRowRenderer());
        }
        table.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(UIStyles.line());
        scroll.getViewport().setBackground(UIStyles.CARD);
        return scroll;
    }

    /** Replace table model data, keeping column headers. */
    protected void replaceTableModel(JTable table, Object[][] data, String[] columns) {
        table.setModel(new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        UIStyles.styleTable(table);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new UIStyles.AlternatingRowRenderer());
        }
        table.getTableHeader().setDefaultRenderer(new HeaderRenderer());
    }

    private static final class HeaderRenderer extends javax.swing.table.DefaultTableCellRenderer {
        HeaderRenderer() {
            setHorizontalAlignment(javax.swing.JLabel.LEFT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);
            setBackground(UIStyles.PRIMARY);
            setForeground(Color.WHITE);
            setFont(UIStyles.TABLE_HEADER_FONT);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyles.LINE));
            return c;
        }
    }

    /** Empty-state message panel. */
    protected JPanel emptyState(String message) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(UIStyles.CARD);
        JLabel label = new JLabel(message);
        label.setFont(UIStyles.BODY_FONT);
        label.setForeground(UIStyles.MUTED);
        panel.add(label);
        return panel;
    }

    protected void status(String message, Color color) {
        // Subclasses can override to show an in-page status line.
    }
}
