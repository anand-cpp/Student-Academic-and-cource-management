package ui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import util.UIStyles;

/**
 * Table cell renderer that applies zebra striping, hover rows, and
 * proper text colors based on the UIStyles design system.
 */
public class TableRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);

        if (isSelected) {
            c.setBackground(UIStyles.SELECTION);
            c.setForeground(UIStyles.INK);
        } else if (row % 2 == 0) {
            c.setBackground(UIStyles.CARD);
            c.setForeground(UIStyles.INK);
        } else {
            c.setBackground(UIStyles.ROW_ALT);
            c.setForeground(UIStyles.INK);
        }

        setFont(UIStyles.TABLE_CELL);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));

        // Align numeric columns right, text columns left
        if (column > 0 && isNumeric(value)) {
            setHorizontalAlignment(JLabel.RIGHT);
        } else {
            setHorizontalAlignment(JLabel.LEFT);
        }

        return c;
    }

    private boolean isNumeric(Object value) {
        if (value == null) return false;
        return value instanceof Number || value instanceof java.math.BigDecimal;
    }
}

/**
 * Header renderer for table columns: dark background, white text, bold.
 */
class TableHeaderRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    public TableHeaderRenderer() {
        setHorizontalAlignment(JLabel.LEFT);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);
        setBackground(UIStyles.PRIMARY);
        setForeground(UIStyles.INVERSE);
        setFont(UIStyles.TABLE_HEADER);
        setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyles.LINE));
        setPreferredSize(new Dimension(0, 36));
        return c;
    }
}
