package util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

/**
 * "Registrar" design system for the Student Academic & Course Management System.
 *
 * <p>A warm, editorial console: parchment canvas, deep-forest navigation,
 * white cards, a serif display face for headings and a clean sans for data.
 * Implemented entirely with the JDK — no third-party look-and-feel.
 *
 * <h2>Palette</h2>
 * <pre>
 *   CANVAS        #F3F1EA  warm parchment page background
 *   SURFACE       #FFFFFF  cards, inputs, popups
 *   SIDEBAR       #0D1F19  deep forest navigation
 *   PRIMARY       #146B4B  forest emerald — primary actions
 *   ACCENT        #C2643F  terracotta — highlights
 *   INK           #14201B  primary text
 * </pre>
 */
public final class UIStyles {

    /* ---------------------------------------------------------------
       Core palette
       --------------------------------------------------------------- */

    public static final Color CANVAS = new Color(0xF3, 0xF1, 0xEA);
    public static final Color CANVAS_DEEP = new Color(0xEC, 0xE9, 0xDF);
    public static final Color SURFACE = new Color(0xFF, 0xFF, 0xFF);

    public static final Color SIDEBAR = new Color(0x0D, 0x1F, 0x19);
    public static final Color SIDEBAR_DEEP = new Color(0x09, 0x17, 0x12);
    public static final Color SIDEBAR_HOVER = new Color(0x17, 0x2F, 0x26);
    public static final Color SIDEBAR_ACTIVE = new Color(0x1C, 0x3A, 0x2F);
    public static final Color SIDEBAR_LINE = new Color(0x1E, 0x35, 0x2C);
    public static final Color SIDEBAR_TEXT = new Color(0xCF, 0xDB, 0xD4);
    public static final Color SIDEBAR_MUTED = new Color(0x7E, 0x92, 0x88);
    public static final Color SIDEBAR_ACCENT = new Color(0x8F, 0xE0, 0xB8);

    public static final Color PRIMARY = new Color(0x14, 0x6B, 0x4B);
    public static final Color PRIMARY_DARK = new Color(0x0E, 0x52, 0x38);
    public static final Color PRIMARY_LIGHT = new Color(0xE6, 0xF2, 0xEB);
    public static final Color PRIMARY_SOFT = new Color(0xCF, 0xE7, 0xDA);

    public static final Color ACCENT = new Color(0xC2, 0x64, 0x3F);
    public static final Color ACCENT_DARK = new Color(0xA4, 0x4E, 0x2D);
    public static final Color ACCENT_LIGHT = new Color(0xFA, 0xEC, 0xE5);

    public static final Color SUCCESS = new Color(0x2E, 0x7D, 0x5B);
    public static final Color SUCCESS_LIGHT = new Color(0xE5, 0xF3, 0xEA);
    public static final Color DANGER = new Color(0xB4, 0x44, 0x3C);
    public static final Color DANGER_LIGHT = new Color(0xFA, 0xE9, 0xE7);
    public static final Color WARNING = new Color(0xA8, 0x6E, 0x18);
    public static final Color WARNING_LIGHT = new Color(0xFA, 0xF1, 0xDF);
    public static final Color INFO = new Color(0x2F, 0x6F, 0x8F);
    public static final Color INFO_LIGHT = new Color(0xE7, 0xF1, 0xF5);

    public static final Color INK = new Color(0x14, 0x20, 0x1B);
    public static final Color TEXT_BODY = new Color(0x3E, 0x4E, 0x46);
    public static final Color MUTED = new Color(0x73, 0x82, 0x79);
    public static final Color MUTED_LIGHT = new Color(0x9A, 0xA6, 0x9E);

    public static final Color CARD = SURFACE;
    public static final Color CARD_HOVER = new Color(0xF8, 0xF6, 0xF0);
    public static final Color LINE = new Color(0xE3, 0xDF, 0xD4);
    public static final Color LINE_STRONG = new Color(0xD2, 0xCC, 0xBC);
    public static final Color ROW_ALT = new Color(0xFA, 0xF9, 0xF4);
    public static final Color SELECTION = new Color(0xE8, 0xF3, 0xEC);
    public static final Color SELECTION_BORDER = new Color(0x9F, 0xC9, 0xB2);
    public static final Color SHADOW = new Color(0x1A, 0x2A, 0x22, 26);
    public static final Color INVERSE = Color.WHITE;

    /* ---------------------------------------------------------------
       Typography — serif display, sans interface
       --------------------------------------------------------------- */

    private static final String SANS = "Segoe UI";
    private static final String SERIF = "Georgia";

    /** Large editorial page title. */
    public static final Font DISPLAY = new Font(SERIF, Font.BOLD, 27);
    /** Brand wordmark. */
    public static final Font BRAND = new Font(SERIF, Font.BOLD, 20);
    /** Card headline. */
    public static final Font H1 = new Font(SANS, Font.BOLD, 19);
    public static final Font H2 = new Font(SANS, Font.BOLD, 15);
    public static final Font H3 = new Font(SANS, Font.BOLD, 13);
    public static final Font TITLE_FONT = DISPLAY;
    public static final Font BODY_LG = new Font(SANS, Font.PLAIN, 14);
    public static final Font BODY_FONT = new Font(SANS, Font.PLAIN, 13);
    public static final Font BODY = BODY_FONT;
    public static final Font BODY_SM = new Font(SANS, Font.PLAIN, 12);
    public static final Font CAPTION = new Font(SANS, Font.PLAIN, 11);
    public static final Font CAPTION_BOLD = new Font(SANS, Font.BOLD, 11);
    public static final Font EYEBROW = new Font(SANS, Font.BOLD, 10);
    public static final Font BUTTON = new Font(SANS, Font.BOLD, 12);
    public static final Font BUTTON_SM = new Font(SANS, Font.BOLD, 11);
    public static final Font LABEL = new Font(SANS, Font.BOLD, 10);
    public static final Font TABLE_HEADER = new Font(SANS, Font.BOLD, 10);
    public static final Font TABLE_CELL = new Font(SANS, Font.PLAIN, 12);
    public static final Font TABLE_CELL_BOLD = new Font(SANS, Font.BOLD, 12);
    public static final Font CARD_VALUE = new Font(SERIF, Font.BOLD, 30);
    public static final Font CARD_LABEL = new Font(SANS, Font.PLAIN, 12);
    public static final Font SIDEBAR_ITEM = new Font(SANS, Font.PLAIN, 13);
    public static final Font SIDEBAR_GROUP = new Font(SANS, Font.BOLD, 9);
    public static final Font SIDEBAR_BRAND = BRAND;
    public static final Font MONO = new Font("Consolas", Font.PLAIN, 11);

    /* ---------------------------------------------------------------
       Metrics
       --------------------------------------------------------------- */

    public static final int SPACE_2 = 4;
    public static final int SPACE_3 = 8;
    public static final int SPACE_4 = 16;
    public static final int SPACE_5 = 20;
    public static final int SPACE_6 = 24;
    public static final int SPACE_8 = 32;
    public static final int SPACE_10 = 40;
    public static final int SPACE_12 = 48;

    public static final int RADIUS_SM = 8;
    public static final int RADIUS = 10;
    public static final int RADIUS_LG = 14;
    public static final int BORDER_RADIUS = RADIUS;
    public static final int BORDER_RADIUS_LG = RADIUS_LG;

    public static final int TABLE_ROW_HEIGHT = 44;
    public static final int TABLE_HEADER_HEIGHT = 40;
    public static final int HEADER_HEIGHT = 66;
    public static final int SIDEBAR_WIDTH = 254;
    public static final int CONTROL_HEIGHT = 38;
    public static final int PAGE_PADDING = 28;

    private UIStyles() {
        // Utility class
    }

    /* ---------------------------------------------------------------
       Borders
       --------------------------------------------------------------- */

    public static Border cardBorder() {
        return new LineBorder(LINE, 1, true);
    }

    public static Border inputBorder() {
        return new EmptyBorder(0, 12, 0, 12);
    }

    public static Border inputFocusBorder() {
        return new EmptyBorder(0, 12, 0, 12);
    }

    public static Border inputErrorBorder() {
        return new EmptyBorder(0, 12, 0, 12);
    }

    public static Border sectionBorder() {
        return new MatteBorder(0, 0, 1, 0, LINE);
    }

    public static EmptyBorder empty(int top, int left, int bottom, int right) {
        return new EmptyBorder(top, left, bottom, right);
    }

    public static EmptyBorder empty(int all) {
        return new EmptyBorder(all, all, all, all);
    }

    public static Border line(Color color, int thickness) {
        return new LineBorder(color, thickness, true);
    }

    public static Border dashedLine(Color color) {
        return BorderFactory.createDashedBorder(color, 4f, 3f, 1f, true);
    }

    /* ---------------------------------------------------------------
       Rendering
       --------------------------------------------------------------- */

    public static void applyQualityHints(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    /* ---------------------------------------------------------------
       Label factories
       --------------------------------------------------------------- */

    public static JLabel heading1(String text) {
        JLabel l = new JLabel(text);
        l.setFont(H1);
        l.setForeground(INK);
        return l;
    }

    public static JLabel heading2(String text) {
        JLabel l = new JLabel(text);
        l.setFont(H2);
        l.setForeground(INK);
        return l;
    }

    public static JLabel heading3(String text) {
        JLabel l = new JLabel(text);
        l.setFont(H3);
        l.setForeground(INK);
        return l;
    }

    public static JLabel body(String text) {
        JLabel l = new JLabel(text);
        l.setFont(BODY);
        l.setForeground(TEXT_BODY);
        return l;
    }

    public static JLabel caption(String text) {
        JLabel l = new JLabel(text);
        l.setFont(CAPTION);
        l.setForeground(MUTED);
        return l;
    }

    public static JLabel captionBold(String text) {
        JLabel l = new JLabel(text);
        l.setFont(CAPTION_BOLD);
        l.setForeground(MUTED);
        return l;
    }

    public static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(LABEL);
        l.setForeground(MUTED);
        return l;
    }

    public static JLabel fieldCaption(String text) {
        JLabel l = new JLabel(text);
        l.setFont(CAPTION);
        l.setForeground(MUTED_LIGHT);
        return l;
    }

    public static JLabel eyebrow(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(EYEBROW);
        l.setForeground(PRIMARY);
        return l;
    }

    /* ---------------------------------------------------------------
       Helpers
       --------------------------------------------------------------- */

    public static Dimension pref(int w, int h) {
        return new Dimension(w, h);
    }

    public static boolean isDark(Color c) {
        return (0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue()) < 186;
    }

    public static Color contrast(Color c) {
        return isDark(c) ? INVERSE : INK;
    }

    /** Blend two colors; ratio 0 = a, 1 = b. */
    public static Color mix(Color a, Color b, double ratio) {
        double r = Math.max(0, Math.min(1, ratio));
        return new Color(
                (int) Math.round(a.getRed() + (b.getRed() - a.getRed()) * r),
                (int) Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * r),
                (int) Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * r));
    }

    /** Color with a replaced alpha channel. */
    public static Color alpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }
}
