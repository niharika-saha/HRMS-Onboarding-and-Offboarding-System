package gui;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;

import static gui.Theme.*;

/**
 * Factory methods that produce consistently styled Swing widgets.
 * Every method is stateless; call them freely from any GUI class.
 */
public final class UIFactory {

    private UIFactory() {}

    // ── Labels ────────────────────────────────────────────────────────────────

    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    public static JLabel cardTitle(String text) {
        JLabel l = label(text.toUpperCase(), new Font("Segoe UI", Font.BOLD, 10), TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // ── Panels ────────────────────────────────────────────────────────────────

    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(BG_CARD);
        p.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(16, 16, 16, 16)));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    // ── Separator ─────────────────────────────────────────────────────────────

    public static Component hRule() {
        JSeparator s = new JSeparator();
        s.setForeground(BORDER);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return s;
    }

    // ── Buttons ───────────────────────────────────────────────────────────────

    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0x5B4EE8));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
        btn.setBorder(new CompoundBorder(
            new LineBorder(new Color(0x7060FF), 1, true),
            new EmptyBorder(9, 18, 9, 18)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(new Color(0x6B5EF8));
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(0x5B4EE8));
            }
        });
        return btn;
    }

    public static JButton ghostButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_SMALL);
        btn.setForeground(TEXT_SECONDARY);
        btn.setBackground(BG_SURFACE);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(5, 12, 5, 12)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(0x2E2E32));
                btn.setForeground(TEXT_PRIMARY);
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(BG_SURFACE);
                btn.setForeground(TEXT_SECONDARY);
            }
        });
        return btn;
    }

    public static MouseAdapter hoverEffect(JButton btn, Color hoverBg, Color normalBg) {
        return new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(hoverBg); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(normalBg); }
        };
    }

    // ── Form inputs ───────────────────────────────────────────────────────────

    public static JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(FONT_UI);
        f.setForeground(TEXT_PRIMARY);
        f.setBackground(BG_SURFACE);
        f.setCaretColor(TEXT_PRIMARY);
        f.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(8, 10, 8, 10)));
        return f;
    }

    public static <T> JComboBox<T> styledCombo(T[] items) {
        JComboBox<T> box = new JComboBox<>(items);
        box.setFont(FONT_UI);
        box.setBackground(BG_SURFACE);
        box.setForeground(TEXT_PRIMARY);
        box.setBorder(new LineBorder(BORDER, 1, true));
        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean sel, boolean focus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(
                        list, value, index, sel, focus);
                l.setFont(FONT_UI);
                l.setBorder(new EmptyBorder(6, 10, 6, 10));
                l.setBackground(sel ? new Color(0x2E2E36) : BG_SURFACE);
                l.setForeground(TEXT_PRIMARY);
                l.setOpaque(true);
                return l;
            }
        });
        return box;
    }

    public static JCheckBox styledCheckBox(String text) {
        JCheckBox cb = new JCheckBox(text);
        cb.setFont(FONT_UI);
        cb.setForeground(TEXT_PRIMARY);
        cb.setBackground(BG_APP);
        cb.setFocusPainted(false);
        return cb;
    }

    // ── Status chip ───────────────────────────────────────────────────────────

    public static JLabel statusChip(String text, Color color) {
        JLabel l = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(
                        color.getRed(), color.getGreen(), color.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                        getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(color);
        l.setBorder(new EmptyBorder(6, 12, 6, 12));
        l.setOpaque(false);
        return l;
    }

    // ── Form field block (label above + input) ────────────────────────────────

    public static JPanel formFieldBlock(String labelText, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_CARD);
        JLabel lbl = label(labelText, FONT_SMALL, TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl);
        p.add(Box.createVerticalStrut(5));
        p.add(field);
        return p;
    }

    // ── Colour utilities ──────────────────────────────────────────────────────

    public static String colorHex(Color c) {
        return String.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    public static Color parseHexColor(String hex, Color fallback) {
        try { return Color.decode("#" + hex); }
        catch (Exception e) { return fallback; }
    }
}
