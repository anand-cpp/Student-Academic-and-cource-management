package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import ui.components.AppDialog;
import ui.components.AppIcon;
import ui.components.AppScrollBarUI;
import ui.components.DataCard;
import ui.components.EmptyState;
import ui.components.FormField;
import ui.components.RoundedButton;
import ui.components.SectionHeader;
import ui.components.StatusBadge;
import ui.components.TableCard;
import ui.components.TableRenderer;
import ui.components.ToastManager;
import ui.components.ToastPanel;
import util.UIStyles;

/**
 * Base class for every module page.
 *
 * <p>Provides the shared visual grammar: an editorial header, generous page
 * padding, white cards, custom buttons, styled tables that fall back to empty
 * states, and toast notifications attached to the frame.
 *
 * <p>Business logic stays in the concrete panels and services; this class only
 * owns presentation and a handful of dialogs.
 */
public abstract class BasePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    protected final MainFrame main;

    public BasePanel(MainFrame main) {
        this.main = main;
        setBackground(UIStyles.CANVAS);
        setLayout(new BorderLayout());
    }

    /* ---------------------------------------------------------------
       Page scaffolding
       --------------------------------------------------------------- */

    /** Editorial page header with an uppercase eyebrow. */
    protected SectionHeader header(String eyebrow, String title, String subtitle) {
        SectionHeader header = new SectionHeader(title, subtitle);
        header.setEyebrow(eyebrow);
        header.setBorder(new EmptyBorder(24, UIStyles.PAGE_PADDING, 12, UIStyles.PAGE_PADDING));
        return header;
    }

    /** Standard page body: transparent, padded, 16px vertical rhythm. */
    protected JPanel pageBody() {
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(0, UIStyles.PAGE_PADDING, 22, UIStyles.PAGE_PADDING));
        return body;
    }

    /** A single white card. */
    protected DataCard card(String title, String subtitle) {
        return new DataCard(title, subtitle);
    }

    /** A table card that swaps to an empty state when there are no rows. */
    protected TableCard tableCard(String title, String subtitle, JTable table,
            String emptyTitle, String emptyDescription) {
        return new TableCard(title, subtitle, table, emptyTitle, emptyDescription);
    }

    /** Transparent horizontal band for toolbars and filter rows. */
    protected JPanel row(int gap) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, gap, 0));
        panel.setOpaque(false);
        return panel;
    }

    protected JPanel rightRow(int gap) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, gap, 0));
        panel.setOpaque(false);
        return panel;
    }

    /* ---------------------------------------------------------------
       Toasts & dialogs
       --------------------------------------------------------------- */

    protected void toast(String message, ToastPanel.Type type) {
        ToastManager.show(main, message, type);
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
        return AppDialog.confirm(this, title, message, "Confirm", false);
    }

    protected boolean confirmDanger(String message, String title) {
        return AppDialog.confirm(this, title, message, "Delete", true);
    }

    /* ---------------------------------------------------------------
       Buttons
       --------------------------------------------------------------- */

    protected JButton primaryButton(String text, ActionListener listener) {
        return button(text, null, listener, 0);
    }

    protected JButton primaryButton(String text, AppIcon.Glyph glyph, ActionListener listener) {
        return button(text, glyph, listener, 0);
    }

    protected JButton accentButton(String text, ActionListener listener) {
        return button(text, null, listener, 2);
    }

    protected JButton successButton(String text, ActionListener listener) {
        return button(text, null, listener, 0);
    }

    protected JButton dangerButton(String text, ActionListener listener) {
        return button(text, null, listener, 3);
    }

    protected JButton ghostButton(String text, ActionListener listener) {
        return button(text, null, listener, 4);
    }

    protected JButton outlineButton(String text, ActionListener listener) {
        return button(text, null, listener, 1);
    }

    protected JButton outlineButton(String text, AppIcon.Glyph glyph, ActionListener listener) {
        return button(text, glyph, listener, 1);
    }

    private JButton button(String text, AppIcon.Glyph glyph, ActionListener listener, int variant) {
        RoundedButton b = new RoundedButton(text);
        if (glyph != null) {
            b.setIcon(new AppIcon(glyph, 15, null));
        }
        switch (variant) {
            case 1 -> b.applyOutline();
            case 2 -> b.applyAccent();
            case 3 -> b.applyDanger();
            case 4 -> b.applyGhost();
            default -> b.applyPrimary();
        }
        if (listener != null) {
            b.addActionListener(listener);
        }
        return b;
    }

    /** Square icon-only button for toolbars. */
    protected RoundedButton iconButton(AppIcon.Glyph glyph, String tooltip) {
        RoundedButton b = new RoundedButton("");
        b.setIcon(new AppIcon(glyph, 16, null));
        b.applyOutline();
        b.setToolTipText(tooltip);
        b.setPreferredSize(new Dimension(UIStyles.CONTROL_HEIGHT, UIStyles.CONTROL_HEIGHT));
        return b;
    }

    /* ---------------------------------------------------------------
       Tables
       --------------------------------------------------------------- */

    protected JTable buildTable(Object[][] data, String[] columns) {
        JTable table = new JTable(model(data, columns));
        styleTable(table);
        return table;
    }

    private DefaultTableModel model(Object[][] data, String[] columns) {
        return new DefaultTableModel(data, columns) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    protected void replaceTableData(JTable table, Object[][] data, String[] columns) {
        table.setModel(model(data, columns));
        styleTable(table);
    }

    protected void styleTable(JTable table) {
        table.setRowHeight(UIStyles.TABLE_ROW_HEIGHT);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(UIStyles.SELECTION);
        table.setSelectionForeground(UIStyles.INK);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setOpaque(true);
        table.setBackground(UIStyles.SURFACE);
        table.setForeground(UIStyles.TEXT_BODY);
        table.setFont(UIStyles.TABLE_CELL);
        table.setBorder(BorderFactory.createEmptyBorder());
        table.setDefaultRenderer(Object.class, new TableRenderer());

        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(true);
        table.getTableHeader().setPreferredSize(new Dimension(0, UIStyles.TABLE_HEADER_HEIGHT));
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                    boolean focus, int row, int column) {
                super.getTableCellRendererComponent(t, value, false, false, row, column);
                setText(value == null ? "" : value.toString().toUpperCase());
                setFont(UIStyles.TABLE_HEADER);
                setForeground(UIStyles.MUTED);
                setBackground(UIStyles.CANVAS_DEEP);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                return this;
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UIStyles.LINE);
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        });

        final int[] hovered = {-1};
        table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != hovered[0]) {
                    hovered[0] = row;
                    table.putClientProperty("hoverRow", row);
                    table.repaint();
                }
            }
        });
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hovered[0] = -1;
                table.putClientProperty("hoverRow", -1);
                table.repaint();
            }
        });
    }

    protected JScrollPane scroll(Component view) {
        JScrollPane pane = new JScrollPane(view);
        AppScrollBarUI.apply(pane);
        return pane;
    }

    /** Assign proportional widths to the visible columns of a table. */
    protected void setColumnWidths(JTable table, int... widths) {
        int count = Math.min(widths.length, table.getColumnModel().getColumnCount());
        for (int i = 0; i < count; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    /* ---------------------------------------------------------------
       Forms
       --------------------------------------------------------------- */

    /** Vertical label + input + optional hint. The input fills the row width. */
    protected JPanel field(String label, Component input, String hint) {
        JPanel row = new JPanel(new BorderLayout(0, 5));
        row.setOpaque(false);

        JLabel labelComp = UIStyles.fieldLabel(label);
        row.add(labelComp, BorderLayout.NORTH);
        row.add(input, BorderLayout.CENTER);

        if (hint != null && !hint.isEmpty()) {
            JLabel hintLabel = UIStyles.fieldCaption(hint);
            JPanel south = new JPanel(new BorderLayout());
            south.setOpaque(false);
            south.setBorder(new EmptyBorder(3, 0, 0, 0));
            south.add(hintLabel, BorderLayout.WEST);
            row.add(south, BorderLayout.SOUTH);
        }
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        return row;
    }

    protected FormField textField(String placeholder) {
        return new FormField(placeholder);
    }

    /* ---------------------------------------------------------------
       Compatibility helpers retained for reports and misc panels
       --------------------------------------------------------------- */

    protected JPanel emptyState(String icon, String title, String description) {
        return new EmptyState(icon, title, description);
    }

    protected JPanel sectionHeader(String title, String description) {
        return new SectionHeader(title, description);
    }

    protected JPanel statusBadge(String text, StatusBadge.Style style) {
        return new StatusBadge(text, style);
    }

    protected JPanel pageWrapper(String title, String description, JPanel content) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        SectionHeader header = new SectionHeader(title, description);
        header.setEyebrow("Registrar");
        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(content, BorderLayout.CENTER);
        return wrapper;
    }

    /** @deprecated use {@link #field(String, Component, String)} instead. */
    @Deprecated
    public static JPanel formFieldRow(String label, JTextField field, String caption) {
        JPanel row = new JPanel();
        row.setLayout(new javax.swing.BoxLayout(row, javax.swing.BoxLayout.Y_AXIS));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(0, 0, 16, 0));
        row.add(UIStyles.fieldLabel(label));
        field.setFont(UIStyles.BODY);
        field.setPreferredSize(new Dimension(280, 38));
        row.add(javax.swing.Box.createVerticalStrut(5));
        row.add(field);
        if (caption != null && !caption.isEmpty()) {
            row.add(UIStyles.fieldCaption(caption));
        }
        return row;
    }
}
