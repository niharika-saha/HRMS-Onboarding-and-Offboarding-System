package gui;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Builds the fixed left-side navigation sidebar.
 */
public final class SidebarPanel {

    private SidebarPanel() {}

    public static JPanel build() {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(BG_SIDEBAR);
        sb.setPreferredSize(new Dimension(210, 0));
        sb.setBorder(new MatteBorder(0, 0, 0, 1, BORDER));

        // Brand block
        JPanel brand = new JPanel();
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBackground(BG_SIDEBAR);
        brand.setBorder(new EmptyBorder(22, 18, 18, 18));
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel appLabel = label("HRMS", new Font("Segoe UI", Font.BOLD, 16), TEXT_PRIMARY);
        JLabel verLabel = label("v2.1.0", FONT_LABEL, TEXT_MUTED);
        appLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        verLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        brand.add(appLabel);
        brand.add(Box.createVerticalStrut(2));
        brand.add(verLabel);
        sb.add(brand);
        sb.add(hRule());
        sb.add(Box.createVerticalStrut(10));

        // Dashboard section
        JPanel dashH = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 6));
        dashH.setBackground(BG_SIDEBAR);
        dashH.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        dashH.add(label("DASHBOARD", new Font("Segoe UI", Font.BOLD, 10), TEXT_MUTED));
        sb.add(dashH);
        sb.add(subNavItem("  Onboarding",  false));
        sb.add(subNavItem("  Offboarding", true));
        sb.add(Box.createVerticalStrut(8));
        sb.add(hRule());
        sb.add(Box.createVerticalStrut(10));

        // Manage section
        JPanel manH = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 6));
        manH.setBackground(BG_SIDEBAR);
        manH.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        manH.add(label("MANAGE", new Font("Segoe UI", Font.BOLD, 10), TEXT_MUTED));
        sb.add(manH);
        sb.add(topNavItem("Employees", false));
        sb.add(topNavItem("Reports",   false));
        sb.add(topNavItem("Settings",  false));
        sb.add(Box.createVerticalGlue());
        return sb;
    }

    private static JPanel topNavItem(String text, boolean active) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 8));
        item.setBackground(active ? new Color(0x26262B) : BG_SIDEBAR);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        item.add(label(text, active ? FONT_BOLD : FONT_UI,
                             active ? TEXT_PRIMARY : TEXT_SECONDARY));
        if (!active) item.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { item.setBackground(new Color(0x1E1E22)); }
            @Override public void mouseExited(MouseEvent e)  { item.setBackground(BG_SIDEBAR); }
        });
        return item;
    }

    private static JPanel subNavItem(String text, boolean active) {
        JPanel item = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (active) {
                    g.setColor(ACCENT);
                    g.fillRect(0, 6, 3, getHeight() - 12);
                }
            }
        };
        item.setOpaque(true);
        item.setBackground(active ? new Color(0x22202E) : BG_SIDEBAR);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel lbl = label(text, active ? FONT_BOLD : FONT_UI,
                                  active ? new Color(0xC8BEFF) : TEXT_SECONDARY);
        lbl.setBorder(new EmptyBorder(0, 20, 0, 0));
        item.add(lbl, BorderLayout.CENTER);
        item.setBorder(new EmptyBorder(6, 6, 6, 10));
        if (!active) item.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { item.setBackground(new Color(0x1E1E22)); }
            @Override public void mouseExited(MouseEvent e)  { item.setBackground(BG_SIDEBAR); }
        });
        return item;
    }
}
