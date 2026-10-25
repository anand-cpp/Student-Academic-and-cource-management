package ui.components;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * A card that hosts a table and swaps to an empty state when the model has no
 * rows. Count chip and subtitle stay in sync with the data.
 */
public class TableCard extends JPanel {

    private static final long serialVersionUID = 1L;

    private final DataCard card;
    private final JTable table;
    private final CardLayout cards = new CardLayout();
    private final JPanel stack = new JPanel(cards);
    private final EmptyState emptyState;

    public TableCard(String title, String subtitle, JTable table,
            String emptyTitle, String emptyDescription) {
        setOpaque(false);
        setLayout(new BorderLayout());
        this.table = table;
        this.card = new DataCard(title, subtitle);
        this.emptyState = new EmptyState("search", emptyTitle, emptyDescription);

        JScrollPane scroll = new JScrollPane(table);
        AppScrollBarUI.apply(scroll);
        scroll.setPreferredSize(new Dimension(0, 240));

        JPanel emptyHolder = new JPanel(new BorderLayout());
        emptyHolder.setBackground(util.UIStyles.SURFACE);
        emptyHolder.add(emptyState, BorderLayout.CENTER);

        stack.setOpaque(false);
        stack.add(scroll, "table");
        stack.add(emptyHolder, "empty");
        stack.setMinimumSize(new Dimension(0, 120));

        card.setBody(stack);
        add(card, BorderLayout.CENTER);
        sync();
    }

    public DataCard getCard() {
        return card;
    }

    public JTable getTable() {
        return table;
    }

    public void setCount(String text) {
        card.setCount(text);
    }

    public void setCount(int rows, String noun) {
        card.setCount(rows + " " + noun + (rows == 1 ? "" : "s"));
    }

    public void setSubtitle(String subtitle) {
        card.setSubtitle(subtitle);
    }

    /** Switch between the table and the empty state based on row count. */
    public void sync() {
        boolean hasRows = table.getModel().getRowCount() > 0;
        cards.show(stack, hasRows ? "table" : "empty");
        if (!hasRows) {
            card.setCount("0");
        }
        stack.revalidate();
        stack.repaint();
    }
}
