package gui;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Builds the main "list" view that shows summary tiles and a scrollable employee list.
 */
public final class ListViewPanel {

    private ListViewPanel() {}

    public static JPanel build(List<EmployeeRecord> employees, Runnable onNewClick) {
        JPanel outer = new JPanel(new BorderLayout(0, 0));
        outer.setBackground(BG_APP);
        outer.setBorder(new EmptyBorder(24, 24, 24, 24));

        outer.add(buildPageHeader(onNewClick), BorderLayout.NORTH);
        outer.add(buildScrollableBody(employees), BorderLayout.CENTER);
        return outer;
    }

    // ── Page header ───────────────────────────────────────────────────────────

    private static JPanel buildPageHeader(Runnable onNewClick) {
        JPanel pageHeader = new JPanel(new BorderLayout());
        pageHeader.setBackground(BG_APP);
        pageHeader.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setBackground(BG_APP);
        titleBlock.add(label("Employee Offboarding",
                new Font("Segoe UI", Font.BOLD, 20), TEXT_PRIMARY));
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(label("Manage and track employee departure processes", FONT_UI, TEXT_MUTED));
        pageHeader.add(titleBlock, BorderLayout.WEST);

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        btnWrapper.setBackground(BG_APP);
        JButton newBtn = primaryButton("+ Start New Offboarding");
        newBtn.addActionListener(e -> onNewClick.run());
        btnWrapper.add(newBtn);
        pageHeader.add(btnWrapper, BorderLayout.EAST);
        return pageHeader;
    }

    // ── Scrollable body (tiles + employee rows) ───────────────────────────────

    private static JScrollPane buildScrollableBody(List<EmployeeRecord> employees) {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(BG_APP);

        body.add(buildSummaryTiles(employees));
        body.add(Box.createVerticalStrut(24));

        JLabel sectionLbl = label("RECENT EMPLOYEES",
                new Font("Segoe UI", Font.BOLD, 10), TEXT_MUTED);
        sectionLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(sectionLbl);
        body.add(Box.createVerticalStrut(10));

        if (employees.isEmpty()) {
            JLabel emptyLbl = label(
                "No employees are currently in the offboarding pipeline.", FONT_UI, TEXT_MUTED);
            emptyLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            emptyLbl.setBorder(new EmptyBorder(16, 0, 0, 0));
            body.add(emptyLbl);
        } else {
            for (EmployeeRecord emp : employees) {
                body.add(buildEmployeeRow(emp));
                body.add(Box.createVerticalStrut(8));
            }
        }

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_APP);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    // ── Summary tiles ─────────────────────────────────────────────────────────

    private static JPanel buildSummaryTiles(List<EmployeeRecord> employees) {
        long pending   = employees.stream().filter(e -> "Idle".equals(e.overallStatus)).count();
        long running   = employees.stream().filter(e -> "Running".equals(e.overallStatus)
                                                     || "Awaiting".equals(e.overallStatus)).count();
        long completed = employees.stream().filter(e -> "Complete".equals(e.overallStatus)).count();
        long errors    = employees.stream().filter(e -> "Error".equals(e.overallStatus)).count();

        JPanel tilesRow = new JPanel(new GridLayout(1, 4, 12, 0));
        tilesRow.setBackground(BG_APP);
        tilesRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        tilesRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 108));
        tilesRow.add(summaryTile("Pending",      String.valueOf(pending),   TEXT_MUTED, new Color(0x2C2C30)));
        tilesRow.add(summaryTile("In Progress",  String.valueOf(running),   AMBER,      AMBER_BG));
        tilesRow.add(summaryTile("Completed",    String.valueOf(completed), GREEN,      GREEN_BG));
        tilesRow.add(summaryTile("Needs Action", String.valueOf(errors),    RED,        RED_BG));
        return tilesRow;
    }

    private static JPanel summaryTile(String lbl, String value, Color accent, Color bgColor) {
        JPanel tile = new JPanel(new BorderLayout());
        tile.setBackground(BG_CARD);
        tile.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(0, 16, 14, 16)));

        // Accent stripe at top
        JPanel accentLine = new JPanel();
        accentLine.setBackground(accent.equals(TEXT_MUTED) ? new Color(0x2C2C30) : accent);
        accentLine.setPreferredSize(new Dimension(0, 3));
        tile.add(accentLine, BorderLayout.NORTH);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(BG_CARD);
        inner.setBorder(new EmptyBorder(12, 0, 0, 0));

        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valLabel.setForeground(accent);
        valLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblLabel = new JLabel(lbl.toUpperCase());
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblLabel.setForeground(TEXT_MUTED);
        lblLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        inner.add(valLabel);
        inner.add(Box.createVerticalStrut(3));
        inner.add(lblLabel);
        tile.add(inner, BorderLayout.CENTER);
        return tile;
    }

    // ── Employee row ──────────────────────────────────────────────────────────

    private static JPanel buildEmployeeRow(EmployeeRecord emp) {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setBackground(BG_CARD);
        row.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(14, 16, 17, 16)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Avatar circle
        JLabel avatar = new JLabel(emp.initials()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        avatar.setForeground(Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setVerticalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(42, 42));
        avatar.setOpaque(false);
        row.add(avatar, BorderLayout.WEST);

        // Name + meta (centre)
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(BG_CARD);
        info.setBorder(new EmptyBorder(0, 4, 0, 0));
        JLabel nameLabel = label(emp.name, FONT_BOLD, TEXT_PRIMARY);
        JLabel metaLabel = label(
            emp.role + "  ·  " + emp.department + "  ·  Last day: " + emp.lastDay,
            FONT_SMALL, TEXT_MUTED);
        info.add(nameLabel);
        info.add(Box.createVerticalStrut(4));
        info.add(metaLabel);
        row.add(info, BorderLayout.CENTER);

        // Progress block (right)
        JPanel progressBlock = new JPanel();
        progressBlock.setLayout(new BoxLayout(progressBlock, BoxLayout.Y_AXIS));
        progressBlock.setBackground(BG_CARD);

        JPanel pctRow = new JPanel(new BorderLayout());
        pctRow.setBackground(BG_CARD);
        pctRow.add(label("Progress", FONT_LABEL, TEXT_MUTED), BorderLayout.WEST);
        pctRow.add(label(Math.round(emp.completionPct() * 100) + "%",
                FONT_LABEL, TEXT_SECONDARY), BorderLayout.EAST);

        ProgressBar pb = new ProgressBar();
        Color barColor = emp.errorCount > 0 ? RED : (emp.awaitCount > 0 ? BLUE : GREEN);
        pb.setProgress(emp.completionPct(), barColor);

        JLabel chip = statusChip(emp.overallStatus, emp.statusColor());
        chip.setHorizontalAlignment(SwingConstants.RIGHT);

        progressBlock.add(pctRow);
        progressBlock.add(Box.createVerticalStrut(4));
        progressBlock.add(pb);
        progressBlock.add(Box.createVerticalStrut(6));
        progressBlock.add(chip);

        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(BG_CARD);
        right.setPreferredSize(new Dimension(200, 0));
        right.add(progressBlock, BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);

        // Hover highlight — propagate to all sub-panels
        List<JPanel> subs = new ArrayList<>();
        subs.add(info); subs.add(right); subs.add(pctRow); subs.add(progressBlock);
        row.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                row.setBackground(new Color(0x222226));
                subs.forEach(p -> p.setBackground(new Color(0x222226)));
            }
            @Override public void mouseExited(MouseEvent e) {
                row.setBackground(BG_CARD);
                subs.forEach(p -> p.setBackground(BG_CARD));
            }
            @Override public void mouseClicked(MouseEvent e) {
                MainGUI.showDetailView(emp);
            }
        });
        return row;
    }
}
