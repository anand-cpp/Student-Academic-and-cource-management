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
import javax.swing.BoxLayout;
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
import ui.components.EmptyState;
import ui.components.RoundedButton;
import ui.components.StatusBadge;
import ui.components.TableRenderer;
import ui.components.ToastPanel;
import util.UIStyles;

/**
 * Base class for all module panels.
 *
 * <p>Provides shared UI helpers: themed buttons, styled tables, form field
 * rows, toast notifications, confirmation dialogs, and empty-state panels.
 * Every panel inherits consistent spacing, typography, and color usage.
 */
public abstract class BasePanel extends JPanel {

    protected final MainFrame main;
    protected JPanel toastContainer;

    public BasePanel(MainFrame main) {
        this.main = main;
        setBackground(UIStyles.CARD);
        setLayout(new BorderLayout(0, 0));
    }

    /* ---------- Toast notifications ---------- */

    /**
     * Show a toast notification at the bottom right of the window.
     * The toast auto-disappears after 3 seconds.
     */
    protected void toast(String message, ToastPanel.Type type) {
        if (toastContainer == null) {
            toastContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
            toastContainer.setOpaque(false);
        }

        ToastPanel toast = new ToastPanel(message, type);
        toast.setSize(320, 60);

        // Use a layered pane approach: add to the main frame's glass pane area
        // For simplicity, add to the main frame content pane at bottom right
        toastContainer.removeAll();
        toastContainer.add(toast);

        // Position toast container at bottom right of main frame
        if (toastContainer.getParent() == null) {
            main.getContentPane().add(toastContainer);
        }
        toastContainer.setBounds(main.getWidth() - 360, main.getHeight() - 100, 340, 80);
        toastContainer.setVisible(true);
        main.repaint();

        // Auto-remove after 3 seconds
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                toastContainer.removeAll();
                toastContainer.setVisible(false);
                main.repaint();
            }
        }, 3000);
    }

    protected void showSuccess(String message) {
        toast(message, ToastPanel.Type.SUCCESS);
    }

    protected void showError(String message) {
        toast(message, ToastPanel.Type.ERROR);
    }

    protected void showWarning(String message) {
        toast(message, ToastPanel.Type.WARNING);
    }

    protected void showInfo(String message) {
        toast(message, ToastPanel.Type.INFO);
    }

    protected boolean confirm(String message, String title) {
        return JOptionPane.showConfirmDialog(main, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
                == JOptionPane.YES_OPTION;
    }

    /* ---------- Buttons ---------- */

    protected JButton primaryButton(String text, ActionListener listener) {
        RoundedButton b = new RoundedButton(text);
        b.applyPrimary();
        b.addActionListener(listener);
        return b;
    }

    protected JButton accentButton(String text, ActionListener listener) {
        RoundedButton b = new RoundedButton(text);
        b.applyAccent();
        b.addActionListener(listener);
        return b;
    }

    protected JButton successButton(String text, ActionListener listener) {
        RoundedButton b = new RoundedButton(text);
        b.applySuccess();
        b.addActionListener(listener);
        return b;
    }

    protected JButton dangerButton(String text, ActionListener listener) {
        RoundedButton b = new RoundedButton(text);
        b.applyDanger();
        b.addActionListener(listener);
        return b;
    }

    protected JButton ghostButton(String text, ActionListener listener) {
        RoundedButton b = new RoundedButton(text);
        b.applyGhost();
        b.addActionListener(listener);
        return b;
    }

    protected JButton outlineButton(String text, ActionListener listener) {
        RoundedButton b = new RoundedButton(text);
        b.applyOutline();
        b.addActionListener(listener);
        return b;
    }

    /* ---------- Table builder ---------- */

    /**
     * Build a styled JTable with zebra striping and proper header styling.
     * Cells are non-editable by default.
     */
    protected JTable buildTable(Object[][] data, String[] columns) {
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        styleTable(table);
        return table;
    }

    /** Replace table model data while keeping styling. */
    protected void replaceTableData(JTable table, Object[][] data, String[] columns) {
        table.setModel(new DefaultTableModel(data, columns) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        styleTable(table);
    }

    /** Apply consistent table styling. */
    protected void styleTable(JTable table) {
        table.setRowHeight(UIStyles.TABLE_ROW_HEIGHT);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(UIStyles.SELECTION);
        table.setSelectionForeground(UIStyles.INK);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(UIStyles.TABLE_HEADER);
        table.getTableHeader().setBackground(UIStyles.PRIMARY);
        table.getTableHeader().setForeground(UIStyles.INVERSE);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, UIStyles.PRIMARY));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.setDefaultRenderer(Object.class, new TableRenderer());
    }

    /* ---------- Form helpers ---------- */

    /** Create a label + field row for a form panel. */
    public static JPanel formFieldRow(String label, JTextField field, String caption) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel labelComp = UIStyles.fieldLabel(label);
        field.setFont(UIStyles.BODY);
        field.setBorder(new javax.swing.border.LineBorder(UIStyles.LINE, 1, true));
        field.setPreferredSize(new Dimension(280, 40));

        row.add(labelComp);
        row.add(field);
        if (caption != null && !caption.isEmpty()) {
            JLabel capComp = UIStyles.fieldCaption(caption);
            capComp.setBorder(new EmptyBorder(2, 0, 0, 0));
            row.add(capComp);
        }
        return row;
    }

    /** A titled section panel with a card background. */
    public static JPanel cardSection(String title, Component... children) {
        ui.components.RoundedPanel panel = new ui.components.RoundedPanel();
        panel.setRadius(12);
        panel.setShadow(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 8, 8, 8);

        if (title != null && !title.isEmpty()) {
            JLabel t = UIStyles.heading3(title);
            panel.add(t, gbc);
            gbc.gridy++;
        }

        for (Component c : children) {
            panel.add(c, gbc);
            gbc.gridy++;
        }

        return panel;
    }

    /** Empty state panel. */
    protected JPanel emptyState(String icon, String title, String description) {
        return new EmptyState(icon, title, description);
    }

    /** Section header with title and description. */
    protected JPanel sectionHeader(String title, String description) {
        return new ui.components.SectionHeader(title, description);
    }

    /** Status badge for tables. */
    protected javax.swing.JPanel statusBadge(String text, ui.components.StatusBadge.Style style) {
        return new ui.components.StatusBadge(text, style);
    }

    /** Standard page wrapper: header section + content area. */
    protected JPanel pageWrapper(String title, String description, JPanel content) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 0));
        wrapper.setOpaque(false);

        ui.components.SectionHeader header = new ui.components.SectionHeader(title, description);
        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(content, BorderLayout.CENTER);

        return wrapper;
    }
}
