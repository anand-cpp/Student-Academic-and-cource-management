package ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Icon;
import util.UIStyles;

/**
 * Hand-drawn line icons rendered with Java2D at any size.
 *
 * <p>No image assets, icon fonts or third-party libraries are used. Every glyph
 * is defined on a 24x24 grid and scaled to the requested size.
 */
public final class AppIcon implements Icon {

    public enum Glyph {
        DASHBOARD, STUDENTS, FACULTY, COURSES, ENROLLMENTS, ATTENDANCE,
        RESULTS, REPORTS, SEARCH, REFRESH, PLUS, SAVE, EDIT, TRASH, RESET,
        CHECK, CLOSE, CHEVRON_DOWN, CHEVRON_RIGHT, CALENDAR, LOGOUT,
        DATABASE, USER, INFO, WARNING, ARROW_RIGHT, SPARK, BOOKMARK
    }

    private final Glyph glyph;
    private final int size;
    private final Color color;
    private final float stroke;

    public AppIcon(Glyph glyph) {
        this(glyph, 18, null, 1.7f);
    }

    public AppIcon(Glyph glyph, int size) {
        this(glyph, size, null, 1.7f);
    }

    public AppIcon(Glyph glyph, int size, Color color) {
        this(glyph, size, color, 1.7f);
    }

    public AppIcon(Glyph glyph, int size, Color color, float stroke) {
        this.glyph = glyph;
        this.size = size;
        this.color = color;
        this.stroke = stroke;
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            UIStyles.applyQualityHints(g2);
            double scale = size / 24.0;
            g2.translate(x, y);
            g2.scale(scale, scale);
            g2.setColor(color != null ? color : c.getForeground());
            g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            draw(g2, glyph);
        } finally {
            g2.dispose();
        }
    }

    private static void draw(Graphics2D g, Glyph glyph) {
        switch (glyph) {
            case DASHBOARD:
                g.draw(new RoundRectangle2D.Double(3, 3, 8, 8, 2, 2));
                g.draw(new RoundRectangle2D.Double(13, 3, 8, 5, 2, 2));
                g.draw(new RoundRectangle2D.Double(13, 10, 8, 11, 2, 2));
                g.draw(new RoundRectangle2D.Double(3, 13, 8, 8, 2, 2));
                break;
            case STUDENTS:
                g.draw(new Ellipse2D.Double(8.5, 4, 6, 6));
                g.draw(new Ellipse2D.Double(15.5, 6, 4.5, 4.5));
                g.draw(new java.awt.geom.Arc2D.Double(4.5, 13, 14, 11, 0, 180, java.awt.geom.Arc2D.OPEN));
                g.draw(new java.awt.geom.Arc2D.Double(16.5, 14, 5, 6, 0, 120, java.awt.geom.Arc2D.OPEN));
                break;
            case FACULTY:
                g.draw(new Path2D.Double() {{
                    moveTo(2, 8);
                    lineTo(12, 3.5);
                    lineTo(22, 8);
                    lineTo(12, 12.5);
                    closePath();
                }});
                g.draw(new Path2D.Double() {{
                    moveTo(6, 10.5);
                    lineTo(6, 16);
                    curveTo(6, 18, 18, 18, 18, 16);
                    lineTo(18, 10.5);
                }});
                g.draw(new java.awt.geom.Line2D.Double(21, 9, 21, 14));
                break;
            case COURSES:
                g.draw(new Path2D.Double() {{
                    moveTo(12, 6.5);
                    curveTo(9, 4.5, 5, 4.5, 3, 5.5);
                    lineTo(3, 18);
                    curveTo(5, 17, 9, 17, 12, 19);
                    curveTo(15, 17, 19, 17, 21, 18);
                    lineTo(21, 5.5);
                    curveTo(19, 4.5, 15, 4.5, 12, 6.5);
                    closePath();
                }});
                g.draw(new java.awt.geom.Line2D.Double(12, 6.5, 12, 19));
                break;
            case ENROLLMENTS:
                g.draw(new RoundRectangle2D.Double(4, 3, 14, 18, 2, 2));
                g.draw(new java.awt.geom.Line2D.Double(8, 3, 8, 6));
                g.draw(new java.awt.geom.Line2D.Double(14, 3, 14, 6));
                g.draw(new java.awt.geom.Line2D.Double(8, 10, 14, 10));
                g.draw(new java.awt.geom.Line2D.Double(8, 14, 12, 14));
                g.draw(new Path2D.Double() {{
                    moveTo(15, 17);
                    lineTo(17, 19);
                    lineTo(21, 14);
                }});
                break;
            case ATTENDANCE:
                g.draw(new RoundRectangle2D.Double(3, 5, 16, 15, 2, 2));
                g.draw(new java.awt.geom.Line2D.Double(3, 9.5, 19, 9.5));
                g.draw(new java.awt.geom.Line2D.Double(7, 3, 7, 7));
                g.draw(new java.awt.geom.Line2D.Double(15, 3, 15, 7));
                g.draw(new Path2D.Double() {{
                    moveTo(7, 14);
                    lineTo(10, 17);
                    lineTo(16, 11.5);
                }});
                break;
            case RESULTS:
                g.draw(new java.awt.geom.Line2D.Double(4, 20, 20, 20));
                g.draw(new RoundRectangle2D.Double(5, 12, 3.5, 8, 1, 1));
                g.draw(new RoundRectangle2D.Double(10.25, 7, 3.5, 13, 1, 1));
                g.draw(new RoundRectangle2D.Double(15.5, 3.5, 3.5, 16.5, 1, 1));
                break;
            case REPORTS:
                g.draw(new Path2D.Double() {{
                    moveTo(6, 2.5);
                    lineTo(14, 2.5);
                    lineTo(19, 7.5);
                    lineTo(19, 21.5);
                    lineTo(6, 21.5);
                    closePath();
                }});
                g.draw(new Path2D.Double() {{
                    moveTo(14, 2.5);
                    lineTo(14, 7.5);
                    lineTo(19, 7.5);
                }});
                g.draw(new java.awt.geom.Line2D.Double(9, 12, 16, 12));
                g.draw(new java.awt.geom.Line2D.Double(9, 16, 16, 16));
                break;
            case SEARCH:
                g.draw(new Ellipse2D.Double(4, 4, 12.5, 12.5));
                g.draw(new java.awt.geom.Line2D.Double(15.5, 15.5, 21, 21));
                break;
            case REFRESH:
                g.draw(new java.awt.geom.Arc2D.Double(4, 4, 16, 16, 55, 275, java.awt.geom.Arc2D.OPEN));
                g.draw(new Path2D.Double() {{
                    moveTo(16.5, 2.5);
                    lineTo(20.5, 5);
                    lineTo(16, 7.5);
                }});
                break;
            case PLUS:
                g.draw(new java.awt.geom.Line2D.Double(12, 5, 12, 19));
                g.draw(new java.awt.geom.Line2D.Double(5, 12, 19, 12));
                break;
            case SAVE:
                g.draw(new RoundRectangle2D.Double(3, 3, 18, 18, 3, 3));
                g.draw(new RoundRectangle2D.Double(7, 3, 10, 7, 1, 1));
                g.draw(new RoundRectangle2D.Double(7, 14, 10, 7, 1.5, 1.5));
                break;
            case EDIT:
                g.draw(new Path2D.Double() {{
                    moveTo(4, 20);
                    lineTo(5, 15);
                    lineTo(16, 4);
                    lineTo(20, 8);
                    lineTo(9, 19);
                    closePath();
                }});
                g.draw(new java.awt.geom.Line2D.Double(14, 6, 18, 10));
                break;
            case TRASH:
                g.draw(new java.awt.geom.Line2D.Double(4, 6.5, 20, 6.5));
                g.draw(new Path2D.Double() {{
                    moveTo(6, 6.5);
                    lineTo(7, 21);
                    lineTo(17, 21);
                    lineTo(18, 6.5);
                }});
                g.draw(new Path2D.Double() {{
                    moveTo(9, 6.5);
                    lineTo(9, 3.5);
                    lineTo(15, 3.5);
                    lineTo(15, 6.5);
                }});
                g.draw(new java.awt.geom.Line2D.Double(10.5, 10, 10.5, 17.5));
                g.draw(new java.awt.geom.Line2D.Double(13.5, 10, 13.5, 17.5));
                break;
            case RESET:
                g.draw(new java.awt.geom.Arc2D.Double(4, 4, 16, 16, 125, 275, java.awt.geom.Arc2D.OPEN));
                g.draw(new Path2D.Double() {{
                    moveTo(3.5, 3);
                    lineTo(4, 8);
                    lineTo(9, 7.5);
                }});
                break;
            case CHECK:
                g.draw(new Path2D.Double() {{
                    moveTo(4, 12.5);
                    lineTo(9.5, 18);
                    lineTo(20, 6);
                }});
                break;
            case CLOSE:
                g.draw(new java.awt.geom.Line2D.Double(6, 6, 18, 18));
                g.draw(new java.awt.geom.Line2D.Double(18, 6, 6, 18));
                break;
            case CHEVRON_DOWN:
                g.draw(new Path2D.Double() {{
                    moveTo(6, 9);
                    lineTo(12, 15);
                    lineTo(18, 9);
                }});
                break;
            case CHEVRON_RIGHT:
                g.draw(new Path2D.Double() {{
                    moveTo(9, 6);
                    lineTo(15, 12);
                    lineTo(9, 18);
                }});
                break;
            case CALENDAR:
                g.draw(new RoundRectangle2D.Double(3, 5, 18, 16, 2.5, 2.5));
                g.draw(new java.awt.geom.Line2D.Double(3, 10, 21, 10));
                g.draw(new java.awt.geom.Line2D.Double(7.5, 2.5, 7.5, 7));
                g.draw(new java.awt.geom.Line2D.Double(16.5, 2.5, 16.5, 7));
                g.fill(new RoundRectangle2D.Double(7, 13, 2.5, 2.5, 1, 1));
                g.fill(new RoundRectangle2D.Double(12, 13, 2.5, 2.5, 1, 1));
                g.fill(new RoundRectangle2D.Double(17, 13, 2.5, 2.5, 1, 1));
                break;
            case LOGOUT:
                g.draw(new Path2D.Double() {{
                    moveTo(10, 3);
                    lineTo(4, 3);
                    lineTo(4, 21);
                    lineTo(10, 21);
                }});
                g.draw(new Path2D.Double() {{
                    moveTo(9, 12);
                    lineTo(21, 12);
                }});
                g.draw(new Path2D.Double() {{
                    moveTo(17.5, 8);
                    lineTo(21.5, 12);
                    lineTo(17.5, 16);
                }});
                break;
            case DATABASE:
                g.draw(new Ellipse2D.Double(4, 2.5, 16, 5));
                g.draw(new Path2D.Double() {{
                    moveTo(4, 5);
                    lineTo(4, 19);
                    curveTo(4, 21, 20, 21, 20, 19);
                    lineTo(20, 5);
                }});
                g.draw(new java.awt.geom.Arc2D.Double(4, 8.5, 16, 5, 180, 180, java.awt.geom.Arc2D.OPEN));
                g.draw(new java.awt.geom.Arc2D.Double(4, 13.5, 16, 5, 180, 180, java.awt.geom.Arc2D.OPEN));
                break;
            case USER:
                g.draw(new Ellipse2D.Double(8, 3, 8, 8));
                g.draw(new java.awt.geom.Arc2D.Double(4, 13, 16, 12, 0, 180, java.awt.geom.Arc2D.OPEN));
                break;
            case INFO:
                g.draw(new Ellipse2D.Double(3, 3, 18, 18));
                g.draw(new java.awt.geom.Line2D.Double(12, 11, 12, 17));
                g.fill(new Ellipse2D.Double(10.9, 6.5, 2.2, 2.2));
                break;
            case WARNING:
                g.draw(new Path2D.Double() {{
                    moveTo(12, 3);
                    lineTo(22, 21);
                    lineTo(2, 21);
                    closePath();
                }});
                g.draw(new java.awt.geom.Line2D.Double(12, 9.5, 12, 14.5));
                g.fill(new Ellipse2D.Double(10.9, 16.8, 2.2, 2.2));
                break;
            case ARROW_RIGHT:
                g.draw(new java.awt.geom.Line2D.Double(4, 12, 20, 12));
                g.draw(new Path2D.Double() {{
                    moveTo(14, 6);
                    lineTo(20, 12);
                    lineTo(14, 18);
                }});
                break;
            case SPARK:
                g.draw(new Path2D.Double() {{
                    moveTo(12, 2.5);
                    lineTo(13.8, 9.2);
                    lineTo(20.5, 11);
                    lineTo(13.8, 12.8);
                    lineTo(12, 19.5);
                    lineTo(10.2, 12.8);
                    lineTo(3.5, 11);
                    lineTo(10.2, 9.2);
                    closePath();
                }});
                break;
            case BOOKMARK:
                g.draw(new Path2D.Double() {{
                    moveTo(6, 2.5);
                    lineTo(18, 2.5);
                    lineTo(18, 21.5);
                    lineTo(12, 17);
                    lineTo(6, 21.5);
                    closePath();
                }});
                break;
            default:
                g.draw(new Ellipse2D.Double(4, 4, 16, 16));
        }
    }
}
