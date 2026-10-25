package ui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Stacking toast host attached to a frame's layered pane. Toasts fade in,
 * wait, fade out and remove themselves. Only the stack's bounds receive mouse
 * events, so the rest of the window stays interactive.
 */
public final class ToastManager {

    private static final int WIDTH = 352;
    private static final int HEIGHT = 68;
    private static final int GAP = 10;
    private static final int MAX_TOASTS = 3;
    private static final int LIFETIME_MS = 3400;

    private static final WeakHashMap<JFrame, ToastManager> MANAGERS = new WeakHashMap<>();

    private final JFrame frame;
    private final JPanel stack;
    private final List<ToastPanel> toasts = new ArrayList<>();

    private ToastManager(JFrame frame) {
        this.frame = frame;
        this.stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        frame.getRootPane().getLayeredPane().add(stack, JLayeredPane.POPUP_LAYER);
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                reposition();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                reposition();
            }
        });
    }

    public static void show(JFrame frame, String message, ToastPanel.Type type) {
        if (frame == null) {
            return;
        }
        Runnable task = () -> {
            ToastManager manager = MANAGERS.computeIfAbsent(frame, ToastManager::new);
            manager.add(message, type);
        };
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }

    private void add(String message, ToastPanel.Type type) {
        ToastPanel toast = new ToastPanel(message, type);
        toast.setAlignmentX(Component.LEFT_ALIGNMENT);
        toast.setAlpha(0f);

        toasts.add(0, toast);
        stack.add(toast, 0);
        while (toasts.size() > MAX_TOASTS) {
            ToastPanel old = toasts.remove(toasts.size() - 1);
            stack.remove(old);
        }
        stack.revalidate();
        reposition();

        fade(toast, 1f, () -> { });
        Timer life = new Timer(LIFETIME_MS, e -> fade(toast, 0f, () -> remove(toast)));
        life.setRepeats(false);
        life.start();
    }

    private void fade(ToastPanel toast, float target, Runnable after) {
        float start = toast.getAlpha();
        long begin = System.currentTimeMillis();
        Timer timer = new Timer(16, null);
        timer.addActionListener(e -> {
            float t = Math.min(1f, (System.currentTimeMillis() - begin) / 220f);
            float value = start + (target - start) * t;
            toast.setAlpha(value);
            if (t >= 1f) {
                timer.stop();
                after.run();
            }
        });
        timer.start();
    }

    private void remove(ToastPanel toast) {
        toasts.remove(toast);
        stack.remove(toast);
        stack.revalidate();
        stack.repaint();
        reposition();
    }

    private void reposition() {
        if (toasts.isEmpty()) {
            stack.setBounds(0, 0, 0, 0);
            return;
        }
        int count = toasts.size();
        int height = count * HEIGHT + (count - 1) * GAP;
        int width = WIDTH;
        int x = Math.max(12, frame.getLayeredPane().getWidth() - width - 24);
        int y = Math.max(12, frame.getLayeredPane().getHeight() - height - 24);
        stack.setBounds(x, y, width, height);
        stack.doLayout();
        stack.repaint();
        frame.getRootPane().getLayeredPane().repaint();
    }
}
