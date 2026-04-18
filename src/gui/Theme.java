package gui;

import java.awt.Color;
import java.awt.Font;

/**
 * Centralised dark-theme palette and font definitions.
 * All GUI classes reference these constants instead of hardcoding values.
 */
public final class Theme {

    private Theme() {}

    // ── Background colours ────────────────────────────────────────────────────
    public static final Color BG_APP     = new Color(0x0F0F10);
    public static final Color BG_SIDEBAR = new Color(0x161618);
    public static final Color BG_CARD    = new Color(0x1C1C1F);
    public static final Color BG_SURFACE = new Color(0x232326);
    public static final Color BG_TOPBAR  = new Color(0x161618);
    public static final Color BORDER     = new Color(0x2C2C30);

    // ── Text colours ──────────────────────────────────────────────────────────
    public static final Color TEXT_PRIMARY   = new Color(0xEDECE6);
    public static final Color TEXT_SECONDARY = new Color(0xA0A09A);
    public static final Color TEXT_MUTED     = new Color(0x60605A);

    // ── Semantic colours ──────────────────────────────────────────────────────
    public static final Color GREEN        = new Color(0x4AC26B);
    public static final Color GREEN_BG     = new Color(0x0D2818);
    public static final Color GREEN_BORDER = new Color(0x1B4332);

    public static final Color RED          = new Color(0xFF6B6B);
    public static final Color RED_BG       = new Color(0x2A0E0E);
    public static final Color RED_BORDER   = new Color(0x4A1818);

    public static final Color AMBER        = new Color(0xE5AC4D);
    public static final Color AMBER_BG     = new Color(0x261B00);
    public static final Color AMBER_BORDER = new Color(0x3D2D00);

    public static final Color BLUE         = new Color(0x6AADFF);
    public static final Color BLUE_BG      = new Color(0x091929);
    public static final Color BLUE_BORDER  = new Color(0x0C2844);

    public static final Color ACCENT = new Color(0x7C6AF7);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_UI    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BOLD  = new Font("Segoe UI", Font.BOLD,  13);
}
