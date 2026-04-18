package gui;

import data.IDocumentData;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.io.File;
import java.util.List;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Modal dialog for uploading a Knowledge Transfer report and optionally
 * adding comments before proceeding with the "knowledge" pipeline step.
 */
public final class KnowledgeTransferDialog {

    private KnowledgeTransferDialog() {}

    public static void show(EmployeeRecord emp, IDocumentData documentData) {
        JDialog dialog = new JDialog((Frame) null, "Knowledge Transfer — " + emp.name, true);
        dialog.setSize(500, 380);
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_APP);
        root.setBorder(new EmptyBorder(24, 28, 20, 28));

        root.add(buildTitleBlock(), BorderLayout.NORTH);

        // Track upload status across listeners
        final boolean[] uploaded = {false};
        final JLabel statusLabel = label("No document uploaded", FONT_SMALL, TEXT_MUTED);

        JTextArea commentsArea = buildCommentsArea();
        root.add(buildCentrePanel(emp, documentData, dialog, uploaded, statusLabel, commentsArea),
                BorderLayout.CENTER);
        root.add(buildButtonRow(emp, documentData, dialog, uploaded, commentsArea),
                BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    // ── Title ─────────────────────────────────────────────────────────────────

    private static JLabel buildTitleBlock() {
        JLabel title = label("Upload Knowledge Transfer Report", FONT_BOLD, TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 16, 0));
        return title;
    }

    // ── Centre panel (upload button + comments) ───────────────────────────────

    private static JPanel buildCentrePanel(EmployeeRecord emp, IDocumentData documentData,
                                            JDialog dialog, boolean[] uploaded,
                                            JLabel statusLabel, JTextArea commentsArea) {
        JPanel centre = new JPanel();
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setBackground(BG_APP);

        // Upload row
        JPanel uploadRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        uploadRow.setBackground(BG_APP);
        uploadRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton uploadBtn = ghostButton("Choose File…");
        uploadRow.add(uploadBtn);
        uploadRow.add(Box.createHorizontalStrut(12));
        uploadRow.add(statusLabel);

        uploadBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                model.Document doc = new model.Document(
                    "DOC-" + emp.empId, emp.empId, "Knowledge Transfer Report", "UPLOADED");
                documentData.uploadDocument(doc);
                statusLabel.setText("Uploaded: " + f.getName());
                statusLabel.setForeground(GREEN);
                uploaded[0] = true;
            }
        });

        centre.add(uploadRow);
        centre.add(Box.createVerticalStrut(20));

        // Comments
        JLabel commentsLbl = label("Additional Comments (Optional)", FONT_SMALL, TEXT_SECONDARY);
        commentsLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        centre.add(commentsLbl);
        centre.add(Box.createVerticalStrut(6));

        JScrollPane scroll = new JScrollPane(commentsArea);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setBorder(new LineBorder(BORDER, 1, true));
        centre.add(scroll);
        return centre;
    }

    private static JTextArea buildCommentsArea() {
        JTextArea area = new JTextArea(4, 30);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(FONT_UI);
        area.setForeground(TEXT_PRIMARY);
        area.setBackground(BG_SURFACE);
        area.setCaretColor(TEXT_PRIMARY);
        area.setBorder(new EmptyBorder(8, 10, 8, 10));
        return area;
    }

    // ── Button row ────────────────────────────────────────────────────────────

    private static JPanel buildButtonRow(EmployeeRecord emp, IDocumentData documentData,
                                          JDialog dialog, boolean[] uploaded,
                                          JTextArea commentsArea) {
        JButton cancelBtn = ghostButton("Cancel");
        JButton saveBtn   = ghostButton("Save");
        JButton verifyBtn = primaryButton("Verify & Proceed");

        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            emp.feedback = commentsArea.getText();
            JOptionPane.showMessageDialog(dialog, "Saved.", "Saved",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        verifyBtn.addActionListener(e -> {
            // Check that a KT report has been uploaded for this employee
            List<model.Document> docs = documentData.getDocumentsByEmployee(emp.empId);
            boolean found = false;
            if (docs != null) {
                for (model.Document d : docs) {
                    if ("Knowledge Transfer Report".equals(d.getType())) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                JOptionPane.showMessageDialog(dialog,
                    "Please upload the report before proceeding.",
                    "Missing Document", JOptionPane.WARNING_MESSAGE);
                return;
            }
            emp.feedback = commentsArea.getText();
            dialog.dispose();
            MainGUI.runSingleStep(emp, "knowledge");
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setBackground(BG_APP);
        btnRow.setBorder(new EmptyBorder(16, 0, 0, 0));
        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);
        btnRow.add(verifyBtn);
        return btnRow;
    }
}
