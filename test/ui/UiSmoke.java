package ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import ui.components.SideTabs;

/**
 * Deterministic screenshot smoke test driven inside the running Swing app.
 * Navigates every module, loads attendance and three report views, and writes
 * PNGs to build/ui-smoke/. Run from the project root with MySQL running.
 *
 * <p>Screenshots are painted offscreen. On a HiDPI desktop the offscreen font
 * metrics differ slightly from the live window's hinted metrics, so the last
 * glyph of some labels can look clipped in the PNG. The running window itself
 * renders those labels in full; use the PNGs for layout and data checks.
 */
public final class UiSmoke {

    private static MainFrame frame;
    private static final File OUT = new File("build/ui-smoke");

    public static void main(String[] args) throws Exception {
        if (!OUT.isDirectory() && !OUT.mkdirs()) {
            System.out.println("OUTPUT_DIR_FAILED: " + OUT.getAbsolutePath());
            System.exit(1);
        }

        SwingUtilities.invokeAndWait(() -> {
            frame = new MainFrame();
            frame.setVisible(true);
        });
        SwingUtilities.invokeAndWait(() -> {
            frame.toFront();
            frame.requestFocus();
        });
        Thread.sleep(3200);
        shot("smoke-dashboard");

        nav("Students");
        shot("smoke-students-empty");
        SwingUtilities.invokeAndWait(() -> {
            for (JTable table : findAll(frame, JTable.class)) {
                if (table.isShowing() && table.getRowCount() > 0) {
                    table.setRowSelectionInterval(0, 0);
                    break;
                }
            }
        });
        Thread.sleep(700);
        shot("smoke-students-selected");

        nav("Faculty");
        shot("smoke-faculty");

        nav("Courses");
        shot("smoke-courses");

        nav("Enrollments");
        shot("smoke-enrollments");

        nav("Attendance");
        shot("smoke-attendance-empty");
        click("Load students");
        Thread.sleep(2200);
        shot("smoke-attendance-roster");

        nav("Results");
        shot("smoke-results");

        nav("Reports");
        Thread.sleep(600);
        shot("smoke-report-directory");

        selectReport(1);
        click("Load");
        Thread.sleep(2000);
        shot("smoke-report-students-course");

        selectReport(2);
        click("Load");
        Thread.sleep(2000);
        shot("smoke-report-faculty-courses");

        selectReport(3);
        click("Load");
        Thread.sleep(2000);
        shot("smoke-report-attendance");

        selectReport(4);
        click("Load");
        Thread.sleep(2000);
        shot("smoke-report-results");

        selectReport(5);
        Thread.sleep(1500);
        shot("smoke-report-overall");

        System.out.println("SMOKE_DONE screenshots=" + OUT.getAbsolutePath());
        System.exit(0);
    }

    private static void nav(String page) throws Exception {
        SwingUtilities.invokeAndWait(() -> frame.navigateTo(page));
        Thread.sleep(1300);
    }

    private static void selectReport(int index) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            SideTabs tabs = find(frame, SideTabs.class);
            if (tabs != null) {
                tabs.setSelectedIndex(index);
            }
        });
        Thread.sleep(500);
    }

    private static void click(String text) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            List<AbstractButton> buttons = findAll(frame, AbstractButton.class);
            for (AbstractButton b : buttons) {
                if (b.isShowing() && text.equalsIgnoreCase(b.getText())) {
                    b.doClick();
                    return;
                }
            }
            System.out.println("BUTTON_NOT_FOUND: " + text);
        });
        Thread.sleep(400);
    }

    private static void shot(String name) throws Exception {
        Thread.sleep(300);
        SwingUtilities.invokeAndWait(() -> {
            BufferedImage img = new BufferedImage(frame.getWidth(), frame.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            try {
                util.UIStyles.applyQualityHints(g);
                frame.paintAll(g);
            } finally {
                g.dispose();
            }
            try {
                ImageIO.write(img, "png", new File(OUT, name + ".png"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        System.out.println("SHOT " + name);
    }

    private static <T extends Component> T find(Container root, Class<T> type) {
        List<T> list = findAll(root, type);
        return list.isEmpty() ? null : list.get(0);
    }

    private static <T extends Component> List<T> findAll(Container root, Class<T> type) {
        List<T> found = new ArrayList<>();
        collect(root, type, found);
        return found;
    }

    private static <T extends Component> void collect(Container root, Class<T> type, List<T> out) {
        for (Component c : root.getComponents()) {
            if (type.isInstance(c)) {
                out.add(type.cast(c));
            }
            if (c instanceof Container child) {
                collect(child, type, out);
            }
        }
    }
}
