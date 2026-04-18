package gui;

import integration.*;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static gui.Theme.*;
import static gui.UIFactory.*;

public final class ExitInterviewDialog {

    private ExitInterviewDialog() {}

    public static void show(EmployeeRecord emp) {
        JDialog dialog = new JDialog((Frame) null,
                "Exit Interview — " + emp.name, true);
        dialog.setSize(520, 520);
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_CARD);
        root.setBorder(new EmptyBorder(24, 24, 20, 24));

        root.add(buildTitleBlock(emp), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(buildForm(emp, dialog));
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_CARD);

        root.add(scroll, BorderLayout.CENTER);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private static JPanel buildTitleBlock(EmployeeRecord emp) {
        JPanel titleRow = new JPanel(new BorderLayout(0, 4));
        titleRow.setBackground(BG_CARD);
        titleRow.setBorder(new EmptyBorder(0, 0, 20, 0));

        titleRow.add(
            label("Exit Interview", new Font("Segoe UI", Font.BOLD, 15), TEXT_PRIMARY),
            BorderLayout.NORTH);

        titleRow.add(
            label("Please fill in the exit interview details for " + emp.name + ".",
                  FONT_SMALL, TEXT_MUTED),
            BorderLayout.SOUTH);

        return titleRow;
    }

    private static JPanel buildForm(EmployeeRecord emp, JDialog dialog) {

        IFormIntegration formIntegration =
                MainGUI.getCustomization().getFormIntegration();

        IFormIntegration.FormDefinition formDef;

        try {
            formDef = formIntegration.getFormByName("Exit Clearance Form");
        } catch (CustomizationException e) {
            throw new RuntimeException("Form not found: " + e.getMessage());
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_CARD);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(BG_CARD);

        Map<String, JComponent> fieldMap = new HashMap<>();

        // 🔥 Build fields dynamically
        for (IFormIntegration.Field f : formDef.fields) {

            JComponent comp;

            switch (f.fieldType) {

                case "Text":
                    comp = styledField();
                    break;

                case "Textarea":
                    JTextArea area = new JTextArea(3, 20);
                    area.setLineWrap(true);
                    area.setWrapStyleWord(true);
                    area.setBorder(new LineBorder(BORDER, 1, true));
                    comp = area;
                    break;

                case "Dropdown":
                    try {
                        ILookupIntegration lookup =
                                MainGUI.getCustomization().getLookupIntegration();

                        String[] values = lookup.getValues(f.lookupCode)
                                .toArray(new String[0]);

                        comp = styledCombo(values);

                    } catch (CustomizationException e) {
                        comp = styledCombo(new String[]{"N/A"});
                    }
                    break;

                case "Date":
                    comp = styledField(); // can plug calendar here later
                    break;

                default:
                    comp = styledField();
            }

            fieldMap.put(f.fieldName, comp);

            formPanel.add(formFieldBlock(
                    f.fieldName + (f.required ? " *" : ""), comp));
            formPanel.add(Box.createVerticalStrut(10));
        }

        wrapper.add(formPanel, BorderLayout.CENTER);
        wrapper.add(buildButtonRow(emp, dialog, fieldMap, formIntegration, formDef),
                BorderLayout.SOUTH);

        return wrapper;
    }

    private static JPanel buildButtonRow(EmployeeRecord emp, JDialog dialog,
                                          Map<String, JComponent> fieldMap,
                                          IFormIntegration formIntegration,
                                          IFormIntegration.FormDefinition formDef) {

        JButton cancelBtn = ghostButton("Cancel");
        JButton saveBtn   = primaryButton("Save & Proceed");

        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {

            for (String key : fieldMap.keySet()) {

                JComponent comp = fieldMap.get(key);
                String value = "";

                if (comp instanceof JTextField) {
                    value = ((JTextField) comp).getText().trim();
                } else if (comp instanceof JTextArea) {
                    value = ((JTextArea) comp).getText().trim();
                } else if (comp instanceof JComboBox) {
                    value = String.valueOf(((JComboBox<?>) comp).getSelectedItem());
                }

                try {
                    formIntegration.validateField(formDef.formId, key, value);
                } catch (CustomizationException error) {
                    JOptionPane.showMessageDialog(null, error.getMessage());
                    return;
                }

                switch (key.toLowerCase()) {
                    case "assets returned":
                        emp.feedback = value;
                        break;
                    case "exit remarks":
                        emp.reason = value;
                        break;
                }
            }

            emp.interviewDataCollected = true;

            dialog.dispose();
            MainGUI.showDetailView(emp);

            SwingUtilities.invokeLater(() ->
                    MainGUI.runSingleStep(emp, "interview"));
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setBackground(BG_CARD);
        btnRow.setBorder(new EmptyBorder(20, 0, 0, 0));

        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);

        return btnRow;
    }
}