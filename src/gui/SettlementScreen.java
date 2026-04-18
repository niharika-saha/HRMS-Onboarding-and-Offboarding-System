package gui;

import offboarding.ExitType;

import offboarding.OffboardingService;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Full-screen frame that displays the calculated final settlement breakdown
 * and lets HR confirm before the "settlement" pipeline step is executed.
 */
public final class SettlementScreen {

    private SettlementScreen() {}

    public static void show(EmployeeRecord emp, OffboardingService offboardingService) {
        JFrame frame = new JFrame("Final Settlement — " + emp.name);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Pre-calculate settlement (triggers the service calculation only)
        offboardingService.processStep(
            emp.empId, emp.exitType, "settlement", null, null, null, null);
        double total = offboardingService.getLastSettlementAmount();

        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(BG_APP);
        root.setBorder(new EmptyBorder(30, 40, 20, 40));

        root.add(buildTitle(), BorderLayout.NORTH);
        root.add(buildContent(emp.exitType, total), BorderLayout.CENTER);
        root.add(buildButtonPanel(emp, frame), BorderLayout.SOUTH);

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    // ── Title ─────────────────────────────────────────────────────────────────

    private static JLabel buildTitle() {
        JLabel title = new JLabel("Final Settlement");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        return title;
    }

    // ── Breakdown card ────────────────────────────────────────────────────────

    private static JPanel buildContent(ExitType type, double total) {
        // Derive component amounts using the same logic as the original
        double monthlySalary  = 50_000;
        double dailySalary    = monthlySalary / 22;
        double salary         = dailySalary * 20;
        double leave          = 10 * dailySalary;
        double severance      = 0;
        if (type == ExitType.LAYOFF) severance = monthlySalary;
        if (type == ExitType.VRS)    severance = 3 * monthlySalary * 2;
        double reimbursements = 0.01 * monthlySalary;
        double deductions     = (salary + leave + severance + reimbursements) - total;

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_APP);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_SURFACE);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(20, 24, 20, 24)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = label("Settlement Breakdown", FONT_BOLD, TEXT_PRIMARY);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(heading);
        card.add(Box.createVerticalStrut(12));
        card.add(hRule());
        card.add(Box.createVerticalStrut(12));

        addBreakdownRow(card, "Earned Salary",    salary);
        addBreakdownRow(card, "Leave Encashment", leave);
        addBreakdownRow(card, "Severance",        severance);
        addBreakdownRow(card, "Reimbursements",   reimbursements);
        addBreakdownRow(card, "Deductions",        deductions);

        card.add(Box.createVerticalStrut(12));
        card.add(hRule());
        card.add(Box.createVerticalStrut(10));

        JLabel totalLabel = label("TOTAL:  ₹" + fmt(total),
                new Font("Segoe UI", Font.BOLD, 15), GREEN);
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(totalLabel);

        content.add(card);
        return content;
    }

    private static void addBreakdownRow(JPanel card, String rowLabel, double amount) {
        JPanel row = new JPanel(new BorderLayout(40, 0));
        row.setBackground(BG_SURFACE);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        row.add(label(rowLabel, FONT_UI, TEXT_SECONDARY), BorderLayout.WEST);
        row.add(label("₹" + fmt(amount), FONT_UI, TEXT_PRIMARY), BorderLayout.EAST);
        card.add(row);
        card.add(Box.createVerticalStrut(6));
    }

    // ── Button panel ──────────────────────────────────────────────────────────

    private static JPanel buildButtonPanel(EmployeeRecord emp, JFrame frame) {
        JButton proceedBtn = primaryButton("Confirm & Proceed");
        proceedBtn.addActionListener(e -> {
            frame.dispose();
            MainGUI.runSingleStep(emp, "settlement");
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setBackground(BG_APP);
        btnPanel.add(proceedBtn);
        return btnPanel;
    }

    // ── Formatting helper ─────────────────────────────────────────────────────

    private static String fmt(double val) {
        return String.format("%.2f", val);
    }
}
