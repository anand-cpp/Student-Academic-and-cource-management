package ui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import util.UIStyles;

/**
 * Table cell renderer: zebra rows, hover highlight, hairline separators,
 * right-aligned numerics and pill badges for status and grade columns.
 */
public class TableRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    private JTable tableRef;
    private int rowRef;
    private int colRef;
    private Color badgeBg;
    private Color badgeFg;
    private boolean badge;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        this.tableRef = table;
        this.rowRef = row;
        this.colRef = column;
        setBorder(null);
        setFont(column == 0 ? UIStyles.TABLE_CELL_BOLD : UIStyles.TABLE_CELL);
        setForeground(isSelected ? UIStyles.INK : UIStyles.TEXT_BODY);

        badge = false;
        badgeBg = null;
        badgeFg = null;

        String text = getText();
        String colName = table == null ? "" : safeColumnName(table, column);

        if ("Status".equalsIgnoreCase(colName) && text != null) {
            badge = true;
            switch (text.trim().toUpperCase()) {
                case "ACTIVE", "PRESENT" -> { badgeBg = UIStyles.SUCCESS_LIGHT; badgeFg = UIStyles.SUCCESS; }
                case "COMPLETED" -> { badgeBg = UIStyles.INFO_LIGHT; badgeFg = UIStyles.INFO; }
                case "ABSENT", "DROPPED", "FAIL" -> { badgeBg = UIStyles.DANGER_LIGHT; badgeFg = UIStyles.DANGER; }
                case "LATE", "PENDING" -> { badgeBg = UIStyles.WARNING_LIGHT; badgeFg = UIStyles.WARNING; }
                default -> { badgeBg = UIStyles.CARD_HOVER; badgeFg = UIStyles.MUTED; }
            }
        } else if ("Grade".equalsIgnoreCase(colName) && text != null) {
            badge = true;
            switch (text.trim().toUpperCase()) {
                case "A" -> { badgeBg = UIStyles.SUCCESS_LIGHT; badgeFg = UIStyles.SUCCESS; }
                case "B" -> { badgeBg = UIStyles.PRIMARY_LIGHT; badgeFg = UIStyles.PRIMARY; }
                case "C" -> { badgeBg = UIStyles.INFO_LIGHT; badgeFg = UIStyles.INFO; }
                case "D" -> { badgeBg = UIStyles.WARNING_LIGHT; badgeFg = UIStyles.WARNING; }
                case "F" -> { badgeBg = UIStyles.DANGER_LIGHT; badgeFg = UIStyles.DANGER; }
                default -> { badgeBg = UIStyles.CARD_HOVER; badgeFg = UIStyles.MUTED; }
            }
        }

        if ("Unassigned".equalsIgnoreCase(text)) {
            setForeground(UIStyles.MUTED_LIGHT);
        }
        setToolTipText(text == null || text.isEmpty() ? null : text);
        return this;
    }

    private String safeColumnName(JTable table, int column) {
        try {
            TableColumnModel cm = table.getColumnModel();
            return cm.getColumn(column).getHeaderValue() == null
                    ? "" : cm.getColumn(column).getHeaderValue().toString();
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        JTable table = tableRef;
        int row = rowRef;
        int col = colRef;

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            UIStyles.applyQualityHints(g2);
            boolean selected = table != null && table.isRowSelected(row);
            Integer hover = null;
            if (table != null && table.getClientProperty("hoverRow") instanceof Integer h) {
                hover = h;
            }

            Color bg;
            if (selected) {
                bg = UIStyles.SELECTION;
            } else if (hover != null && hover == row) {
                bg = UIStyles.CARD_HOVER;
            } else if (row % 2 == 1) {
                bg = UIStyles.ROW_ALT;
            } else {
                bg = UIStyles.SURFACE;
            }
            g2.setColor(bg);
            g2.fillRect(0, 0, getWidth(), getHeight());

            if (selected) {
                g2.setColor(UIStyles.SELECTION_BORDER);
                g2.fillRect(0, 0, 3, getHeight());
            }

            String text = getText();
            if (text != null) {
                int pad = 14;
                int available = getWidth() - pad * 2;
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                String drawText = text;
                int textW = fm.stringWidth(drawText);
                if (textW > available && available > 12) {
                    while (drawText.length() > 1 && fm.stringWidth(drawText + "\u2026") > available) {
                        drawText = drawText.substring(0, drawText.length() - 1);
                    }
                    drawText = drawText + "\u2026";
                    textW = fm.stringWidth(drawText);
                }
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                int align = alignmentFor(table, col);

                int x;
                if (align == SwingConstants.RIGHT) {
                    x = getWidth() - pad - textW;
                } else if (align == SwingConstants.CENTER) {
                    x = (getWidth() - textW) / 2;
                } else {
                    x = pad;
                }

                if (badge && badgeBg != null) {
                    int ph = 22;
                    int pw = Math.min(Math.max(available, 24), textW + 20);
                    int px = align == SwingConstants.CENTER ? (getWidth() - pw) / 2 : pad - 8;
                    int py = (getHeight() - ph) / 2;
                    g2.setColor(badgeBg);
                    g2.fillRoundRect(px, py, pw, ph, ph, ph);
                    g2.setColor(badgeFg);
                    g2.drawString(drawText, px + (pw - textW) / 2, y);
                } else {
                    g2.setColor(getForeground());
                    g2.drawString(drawText, x, y);
                }
            }

            g2.setColor(new Color(0xE9, 0xE5, 0xDB));
            g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
        } finally {
            g2.dispose();
        }
    }

    private int alignmentFor(JTable table, int col) {
        String name = table == null ? "" : safeColumnName(table, col);
        return switch (name) {
            case "ID", "Semester", "Sem.", "Credits", "Max Marks", "Pass Marks",
                    "Marks", "Percentage", "Attend %" -> SwingConstants.RIGHT;
            default -> SwingConstants.LEFT;
        };
    }
}
