package util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.JLabel;

/**
 * Professional design system for the Student Academic & Course Management System.
 *
 * <p>Color palette (navy / indigo / white with restrained accents):<br>
 *   PRIMARY   - Deep navy (#1e293b) - sidebar, header, primary actions<br>
 *   PRIMARY_DARK - Darker navy (#0f172a) - active states, depth<br>
 *   ACCENT    - Indigo (#4f46e5) - secondary actions, links, highlights<br>
 *   ACCENT_DARK - Darker indigo (#4338ca) - active accent<br>
 *   ACCENT_LIGHT - Light indigo tint (#e8e8f9) - active sidebar bg<br>
 *   SUCCESS   - Emerald (#10b981) - save, success feedback<br>
 *   SUCCESS_LIGHT - Light green tint<br>
 *   DANGER    - Rose (#ef4444) - delete, destructive actions<br>
 *   DANGER_LIGHT - Light red tint<br>
 *   WARNING   - Amber (#f59e0b) - warnings, cautions<br>
 *   WARNING_LIGHT - Light amber tint<br>
 *   INFO      - Blue (#3b82f6) - information, neutral actions<br>
 *   INFO_LIGHT - Light blue tint<br>
 *   INK       - Slate-900 (#0f172a) - primary text<br>
 *   BODY      - Slate-700 (#334155) - secondary text<br>
 *   MUTED     - Slate-500 (#64748b) - placeholder, captions<br>
 *   MUTED_LIGHT - Slate-400 (#94a3b8) - lighter muted text<br>
 *   CARD      - White (#ffffff) - panel backgrounds<br>
 *   CARD_HOVER - Slate-50 (#f8fafc) - hover state<br>
 *   LINE      - Slate-200 (#e2e8f0) - borders, dividers<br>
 *   ROW_ALT   - Slate-50 (#f8fafc) - alternating table rows<br>
 *   SELECTION - Slate-200 (#e2e8f0) - table selection bg<br>
 *   SHADOW    - Soft black for elevation effects<br>
 *   INVERSE   - White text on dark backgrounds
 */
public final class UIStyles {

    /* ---------- Color palette ---------- */
    public static final Color PRIMARY = new Color(30, 41, 59);        // #1e293b
    public static final Color PRIMARY_DARK = new Color(15, 23, 42);   // #0f172a
    public static final Color ACCENT = new Color(79, 70, 229);        // #4f46e5
    public static final Color ACCENT_DARK = new Color(67, 56, 202);   // #4338ca
    public static final Color ACCENT_LIGHT = new Color(232, 232, 249); // light indigo tint
    public static final Color SUCCESS = new Color(16, 185, 129);      // #10b981
    public static final Color SUCCESS_LIGHT = new Color(209, 250, 229);
    public static final Color DANGER = new Color(239, 68, 68);        // #ef4444
    public static final Color DANGER_LIGHT = new Color(254, 226, 226);
    public static final Color WARNING = new Color(245, 158, 11);      // #f59e0b
    public static final Color WARNING_LIGHT = new Color(253, 230, 138);
    public static final Color INFO = new Color(59, 130, 246);         // #3b82f6
    public static final Color INFO_LIGHT = new Color(219, 234, 254);
    public static final Color INK = new Color(15, 23, 42);            // #0f172a
    public static final Color TEXT_BODY = new Color(51, 65, 85);      // #334155
    public static final Color MUTED = new Color(100, 116, 139);       // #64748b
    public static final Color MUTED_LIGHT = new Color(148, 163, 184); // #94a3b8
    public static final Color CARD = Color.WHITE;
    public static final Color CARD_HOVER = new Color(248, 250, 252);  // #f8fafc
    public static final Color LINE = new Color(226, 232, 240);        // #e2e8f0
    public static final Color ROW_ALT = new Color(248, 250, 252);     // #f8fafc
    public static final Color SELECTION = new Color(226, 232, 240);
    public static final Color SHADOW = new Color(0, 0, 0, 40);
    public static final Color INVERSE = Color.WHITE;

    /* ---------- Typography ---------- */
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font H1 = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font H2 = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font H3 = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font BODY_LG = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BODY_SM = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font CAPTION = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font CAPTION_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font BUTTON_SM = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font LABEL = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font TABLE_CELL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font CARD_VALUE = new Font("Segoe UI", Font.BOLD, 32);
    public static final Font CARD_LABEL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font SIDEBAR_ITEM = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SIDEBAR_BRAND = new Font("Segoe UI", Font.BOLD, 18);

    /* Alias BODY to BODY_FONT for backward compatibility */
    public static final Font BODY = BODY_FONT;

    /* ---------- Spacing constants ---------- */
    public static final int SPACE_2 = 4;
    public static final int SPACE_3 = 8;
    public static final int SPACE_4 = 16;
    public static final int SPACE_5 = 20;
    public static final int SPACE_6 = 24;
    public static final int SPACE_8 = 32;
    public static final int SPACE_10 = 40;
    public static final int SPACE_12 = 48;

    public static final int BORDER_RADIUS = 10;
    public static final int BORDER_RADIUS_LG = 16;
    public static final int TABLE_ROW_HEIGHT = 40;
    public static final int HEADER_HEIGHT = 64;
    public static final int SIDEBAR_WIDTH = 250;

    private UIStyles() {
        // Utility class - not instantiable
    }

    /* ---------- Borders ---------- */

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                new LineBorder(LINE, 1, true),
                new EmptyBorder(1, 1, 1, 1));
    }

    public static Border inputBorder() {
        return new LineBorder(LINE, 1, true);
    }

    public static Border inputFocusBorder() {
        return new LineBorder(ACCENT, 2, true);
    }

    public static Border inputErrorBorder() {
        return new LineBorder(DANGER, 2, true);
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

    /* ---------- Rendering hints ---------- */

    public static void applyQualityHints(java.awt.Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    /* ---------- Typography helpers ---------- */

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
        JLabel l = new JLabel(text);
        l.setFont(LABEL);
        l.setForeground(INK);
        return l;
    }

    public static JLabel fieldCaption(String text) {
        JLabel l = new JLabel(text);
        l.setFont(CAPTION);
        l.setForeground(MUTED);
        return l;
    }

    /* ---------- Dimension helpers ---------- */

    public static Dimension pref(int w, int h) {
        return new Dimension(w, h);
    }

    public static boolean isDark(Color c) {
        return (0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue()) < 186;
    }

    public static Color contrast(Color c) {
        return isDark(c) ? INVERSE : INK;
    }
}
