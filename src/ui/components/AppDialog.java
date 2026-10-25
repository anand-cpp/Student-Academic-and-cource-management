package ui.components;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import util.UIStyles;

/**
 * Custom modal dialog: a rounded white card, tinted glyph, serif headline and
 * two custom-painted actions. Replaces JOptionPane so confirmations match the
 * console exactly.
 */
public final class AppDialog {

    private AppDialog() {
    }

    public static boolean confirm(Component parent, String title, String message,
            String confirmText, boolean danger) {
        return show(parent, title, message, confirmText, danger, false);
    }

    public static void info(Component parent, String title, String message) {
        show(parent, title, message, "Got it", false, true);
    }

    public static void error(Component parent, String title, String message) {
        show(parent, title, message, "Close", true, true);
    }

    private static boolean show(Component parent, String title, String message,
            String confirmText, boolean danger, boolean alertOnly) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = createDialog(owner, title);
        boolean[] result = {false};

        JPanel card = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    UIStyles.applyQualityHints(g2);
                    int w = getWidth();
                    int h = getHeight();
                    int r = 16;
                    g2.setColor(new Color(8, 18, 13, 46));
                    for (int i = 0; i < 4; i++) {
                        g2.fillRoundRect(4 - i, 6 - i, w - 8 + i * 2, h - 10 + i * 2, r + i, r + i);
                    }
                    g2.setColor(UIStyles.SURFACE);
                    g2.fillRoundRect(4, 4, w - 8, h - 8, r, r);
                    g2.setColor(UIStyles.LINE);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(4, 4, w - 9, h - 9, r, r);
                } finally {
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(26, 28, 22, 28));

        Color accentColor = danger ? UIStyles.DANGER : UIStyles.PRIMARY;
        Color tint = danger ? UIStyles.DANGER_LIGHT : UIStyles.PRIMARY_LIGHT;
        AppIcon.Glyph glyph = danger ? AppIcon.Glyph.WARNING : AppIcon.Glyph.INFO;

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.X_AXIS));

        JPanel badge = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    UIStyles.applyQualityHints(g2);
                    g2.setColor(tint);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    new AppIcon(glyph, 22, accentColor, 1.7f)
                            .paintIcon(this, g2, (getWidth() - 22) / 2, (getHeight() - 22) / 2);
                } finally {
                    g2.dispose();
                }
            }
        };
        badge.setOpaque(false);
        badge.setPreferredSize(new Dimension(44, 44));
        badge.setMinimumSize(new Dimension(44, 44));
        badge.setMaximumSize(new Dimension(44, 44));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title == null ? "" : title);
        titleLabel.setFont(new java.awt.Font("Georgia", java.awt.Font.BOLD, 20));
        titleLabel.setForeground(UIStyles.INK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel messageLabel = new JLabel("<html><body style='width:320px'>" + escape(message) + "</body></html>");
        messageLabel.setFont(UIStyles.BODY_SM);
        messageLabel.setForeground(UIStyles.TEXT_BODY);
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        text.add(titleLabel);
        text.add(Box.createVerticalStrut(6));
        text.add(messageLabel);

        head.add(badge);
        head.add(Box.createHorizontalStrut(14));
        head.add(text);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(22, 0, 0, 0));

        RoundedButton confirm = new RoundedButton(confirmText == null ? "Confirm" : confirmText);
        confirm.setPreferredSize(new Dimension(112, 36));
        if (danger) {
            confirm.applyDangerSolid();
        } else {
            confirm.applyPrimary();
        }

        if (!alertOnly) {
            RoundedButton cancel = new RoundedButton("Cancel");
            cancel.applyOutline();
            cancel.setPreferredSize(new Dimension(96, 36));
            cancel.addActionListener(e -> {
                result[0] = false;
                dialog.dispose();
            });
            actions.add(cancel);
        }
        actions.add(confirm);

        confirm.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });

        card.add(head, BorderLayout.NORTH);
        card.add(actions, BorderLayout.SOUTH);

        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(6, 6, 6, 6));
        root.add(card, BorderLayout.CENTER);

        dialog.setContentPane(root);
        dialog.getRootPane().registerKeyboardAction(
                e -> dialog.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialog.getRootPane().registerKeyboardAction(
                new AbstractAction() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        result[0] = true;
                        dialog.dispose();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        dialog.pack();
        dialog.setSize(Math.max(440, dialog.getWidth()), Math.max(alertOnly ? 214 : 226, dialog.getHeight()));
        dialog.setLocationRelativeTo(owner != null ? owner : parent);
        SwingUtilities.invokeLater(confirm::requestFocusInWindow);
        dialog.setVisible(true);
        return result[0];
    }

    private static JDialog createDialog(Window owner, String title) {
        JDialog dialog;
        if (owner instanceof Frame f) {
            dialog = new JDialog(f, title == null ? "Confirm" : title, true);
        } else if (owner instanceof Dialog d) {
            dialog = new JDialog(d, title == null ? "Confirm" : title, true);
        } else {
            dialog = new JDialog((Frame) null, title == null ? "Confirm" : title, true);
        }
        dialog.setUndecorated(true);
        try {
            dialog.setBackground(new Color(0, 0, 0, 0));
        } catch (UnsupportedOperationException | IllegalStateException ex) {
            dialog.setBackground(UIStyles.CANVAS);
        }
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        return dialog;
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>");
    }
}
