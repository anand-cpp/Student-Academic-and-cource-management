package ui.components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Vertical form body: one column of rows that stretch to the card width,
 * separated by fixed gaps and packed to the top.
 *
 * <p>Rows keep their preferred height (so paired fields and button grids are
 * not stretched vertically); a weighted glue row absorbs any leftover space.
 */
public class FormPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final GridBagConstraints gbc = new GridBagConstraints();
    private int row;

    public FormPanel() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(18, 18, 18, 18));
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
    }

    /** Add a full-width row followed by {@code gapAfter} pixels of space. */
    public void addRow(Component component, int gapAfter) {
        gbc.gridy = row++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, Math.max(0, gapAfter), 0);
        add(component, gbc);
    }

    public void addRow(Component component) {
        addRow(component, 12);
    }

    /** Let spare vertical space collect below the fields. */
    public void addGlue() {
        gbc.gridy = row++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(Box.createGlue(), gbc);
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(Math.max(200, d.width), d.height);
    }
}
