package gui;

import data.IDocumentData;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.io.File;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Full-screen frame for the Document Generation step.
 * Allows HR to generate or upload Relieving Letter and Experience Certificate.
 */
public final class DocumentScreen {

    private DocumentScreen() {}

    public static void show(EmployeeRecord emp, IDocumentData documentData) {
        JFrame frame = new JFrame("Document Generation — " + emp.name);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(BG_APP);
        root.setBorder(new EmptyBorder(30, 40, 20, 40));

        root.add(buildTitle(), BorderLayout.NORTH);
        root.add(buildContent(emp, documentData), BorderLayout.CENTER);
        root.add(buildButtonPanel(emp, frame), BorderLayout.SOUTH);

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    // ── Title ─────────────────────────────────────────────────────────────────

    private static JLabel buildTitle() {
        JLabel title = new JLabel("Document Generation");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        return title;
    }

    // ── Document cards ────────────────────────────────────────────────────────

    private static JPanel buildContent(EmployeeRecord emp, IDocumentData documentData) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_APP);

        content.add(buildDocCard("Relieving Letter",      emp, documentData));
        content.add(Box.createVerticalStrut(16));
        content.add(buildDocCard("Experience Certificate", emp, documentData));
        return content;
    }

    private static JPanel buildDocCard(String docName, EmployeeRecord emp,
                                        IDocumentData documentData) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_SURFACE);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(16, 20, 16, 20)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = label(docName, FONT_BOLD, TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel statusLabel = label("Not generated", FONT_SMALL, TEXT_MUTED);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton generateBtn = ghostButton("Generate");
        JButton uploadBtn   = ghostButton("Upload");

        generateBtn.addActionListener(e -> {
            statusLabel.setText("Document generated — ready to download.");
            statusLabel.setForeground(GREEN);
        });

        uploadBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(card) == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                model.Document doc = new model.Document(
                    "DOC-" + emp.empId + "-" + docName,
                    emp.empId, docName, "UPLOADED");
                documentData.uploadDocument(doc);
                statusLabel.setText("Uploaded: " + f.getName());
                statusLabel.setForeground(GREEN);
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(BG_SURFACE);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.add(generateBtn);
        btnRow.add(uploadBtn);

        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(btnRow);
        card.add(Box.createVerticalStrut(6));
        card.add(statusLabel);
        return card;
    }

    // ── Button panel ──────────────────────────────────────────────────────────

    private static JPanel buildButtonPanel(EmployeeRecord emp, JFrame frame) {
        JButton cancelBtn  = ghostButton("Cancel");
        JButton confirmBtn = primaryButton("Confirm & Proceed");

        cancelBtn .addActionListener(e -> frame.dispose());
        confirmBtn.addActionListener(e -> {
            frame.dispose();
            MainGUI.runSingleStep(emp, "docs");
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setBackground(BG_APP);
        btnPanel.add(cancelBtn);
        btnPanel.add(confirmBtn);
        return btnPanel;
    }
}
