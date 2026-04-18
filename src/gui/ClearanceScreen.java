package gui;

import model.ExitRequest;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Full-screen frame for Clearance & Asset Verification.
 * Shown when the "clearance" pipeline step is triggered.
 */
public final class ClearanceScreen {

    private ClearanceScreen() {}

    public static void show(EmployeeRecord emp, ExitRequest req) {
        JFrame frame = new JFrame("Clearance & Asset Verification — " + emp.name);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(BG_APP);
        root.setBorder(new EmptyBorder(30, 40, 20, 40));

        root.add(buildTitle(), BorderLayout.NORTH);
        root.add(buildContent(emp, req, frame), BorderLayout.CENTER);

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    // ── Title ─────────────────────────────────────────────────────────────────

    private static JLabel buildTitle() {
        JLabel title = new JLabel("Clearance & Asset Verification");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        return title;
    }

    // ── Centre content ────────────────────────────────────────────────────────

    private static JPanel buildContent(EmployeeRecord emp, ExitRequest req, JFrame frame) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_APP);

        content.add(buildEmployeeCard(emp));
        content.add(Box.createVerticalStrut(25));

        // Checkboxes
        JCheckBox laptop  = styledCheckBox("Laptop returned");
        JCheckBox idCard  = styledCheckBox("ID card returned");
        JCheckBox access  = styledCheckBox("System access revoked");
        JCheckBox email   = styledCheckBox("Email disabled");
        JCheckBox finance = styledCheckBox("No pending dues");

        if (req != null) {
            laptop .setSelected(req.isLaptopReturned());
            idCard .setSelected(req.isIdCardReturned());
            access .setSelected(req.isAccessRevoked());
            email  .setSelected(req.isEmailDisabled());
            finance.setSelected(req.isFinanceCleared());
        }

        content.add(buildAssetCard(laptop, idCard, access, email, finance));
        content.add(Box.createVerticalStrut(25));
        content.add(buildButtonPanel(emp, req, frame, laptop, idCard, access, email, finance));
        return content;
    }

    // ── Employee info card ────────────────────────────────────────────────────

    private static JPanel buildEmployeeCard(EmployeeRecord emp) {
        JPanel empCard = new JPanel(new GridLayout(2, 2, 10, 10));
        empCard.setBackground(BG_SURFACE);
        empCard.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(15, 20, 15, 20)));
        empCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        empCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        empCard.add(label("Employee ID:", FONT_BOLD, TEXT_SECONDARY));
        empCard.add(label(emp.empId, FONT_UI, TEXT_PRIMARY));
        empCard.add(label("Exit Type:", FONT_BOLD, TEXT_SECONDARY));
        empCard.add(label(emp.exitType != null ? emp.exitType.toString() : "—", FONT_UI, TEXT_PRIMARY));
        return empCard;
    }

    // ── Asset checklist card ──────────────────────────────────────────────────

    private static JPanel buildAssetCard(JCheckBox laptop, JCheckBox idCard,
                                          JCheckBox access, JCheckBox email,
                                          JCheckBox finance) {
        JPanel assetCard = new JPanel();
        assetCard.setLayout(new BoxLayout(assetCard, BoxLayout.Y_AXIS));
        assetCard.setBackground(BG_SURFACE);
        assetCard.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(15, 20, 15, 20)));
        assetCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = label("Asset Verification", FONT_BOLD, TEXT_PRIMARY);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        assetCard.add(heading);
        assetCard.add(Box.createVerticalStrut(10));

        for (JCheckBox cb : new JCheckBox[]{laptop, idCard, access, email, finance}) {
            cb.setAlignmentX(Component.LEFT_ALIGNMENT);
            assetCard.add(cb);
            assetCard.add(Box.createVerticalStrut(4));
        }
        return assetCard;
    }

    // ── Button panel ──────────────────────────────────────────────────────────

    private static JPanel buildButtonPanel(EmployeeRecord emp, ExitRequest req, JFrame frame,
                                            JCheckBox laptop, JCheckBox idCard,
                                            JCheckBox access, JCheckBox email,
                                            JCheckBox finance) {
        JButton cancelBtn = ghostButton("Cancel");
        JButton saveBtn   = ghostButton("Save");
        JButton verifyBtn = primaryButton("Verify & Proceed");

        cancelBtn.addActionListener(e -> frame.dispose());

        saveBtn.addActionListener(e -> {
            boolean anySelected = laptop.isSelected() || idCard.isSelected()
                    || access.isSelected() || email.isSelected() || finance.isSelected();
            if (!anySelected) {
                JOptionPane.showMessageDialog(frame,
                    "Select at least one item to save.",
                    "Nothing to Save", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (req != null) {
                req.setLaptopReturned(laptop.isSelected());
                req.setIdCardReturned(idCard.isSelected());
                req.setAccessRevoked(access.isSelected());
                req.setEmailDisabled(email.isSelected());
                req.setFinanceCleared(finance.isSelected());
            }
            JOptionPane.showMessageDialog(frame,
                "Progress saved successfully.", "Saved", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
        });

        verifyBtn.addActionListener(e -> {
            if (!laptop.isSelected() || !idCard.isSelected()
                    || !access.isSelected() || !email.isSelected() || !finance.isSelected()) {
                JOptionPane.showMessageDialog(frame,
                    "All clearance items must be verified before proceeding.",
                    "Clearance Incomplete", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (req != null) {
                req.setLaptopReturned(true);
                req.setIdCardReturned(true);
                req.setAccessRevoked(true);
                req.setEmailDisabled(true);
                req.setFinanceCleared(true);
            }
            frame.dispose();
            MainGUI.runSingleStep(emp, "clearance");
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setBackground(BG_APP);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        btnPanel.add(verifyBtn);
        return btnPanel;
    }
}
