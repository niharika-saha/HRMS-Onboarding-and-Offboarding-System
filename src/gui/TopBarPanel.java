package gui;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Builds the top application bar that shows breadcrumb navigation and a status indicator.
 */
public final class TopBarPanel {

    private TopBarPanel() {}

    /** References set during construction and kept by the caller. */
    public static JLabel breadcrumbLabel;
    public static JPanel topDot;
    public static JLabel topStatusLabel;

    public static JPanel build() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_TOPBAR);
        bar.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(14, 20, 14, 20)));

        // Breadcrumb (left side)
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setBackground(BG_TOPBAR);
        breadcrumbLabel = label("Dashboard  /  Offboarding", FONT_UI, TEXT_MUTED);
        left.add(breadcrumbLabel);
        bar.add(left, BorderLayout.WEST);

        // Status dot + text (right side)
        topDot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                ((Graphics2D) g).setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(getBackground());
                g.fillOval(0, 0, 8, 8);
            }
        };
        topDot.setPreferredSize(new Dimension(8, 8));
        topDot.setBackground(TEXT_MUTED);
        topDot.setOpaque(false);
        topStatusLabel = label("Idle", FONT_SMALL, TEXT_SECONDARY);

        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        statusRow.setBackground(BG_TOPBAR);
        statusRow.add(topDot);
        statusRow.add(topStatusLabel);
        bar.add(statusRow, BorderLayout.EAST);

        return bar;
    }
}
