package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Properties;

import org.jdatepicker.impl.*;

import integration.CustomizationException;
import integration.ILookupIntegration;

import static gui.Theme.*;
import static gui.UIFactory.*;

public final class NewEmployeeView {

    private NewEmployeeView() {}

    // ── Form field references ────────────────────────────────────────────────
    public static JTextField          empIdField;
    public static JTextField          nameField;
    public static JTextField          roleField;
    public static JComboBox<String>   deptBox;
    public static JDatePickerImpl     lastDayPicker;
    public static JComboBox<String> exitTypeBox;

    // ── Build ───────────────────────────────────────────────────────────────
    public static JPanel build() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_APP);
        outer.setBorder(new EmptyBorder(16, 20, 16, 20)); // reduced padding

        outer.add(buildHeader(), BorderLayout.NORTH);
        outer.add(buildFormScroll(), BorderLayout.CENTER);
        return outer;
    }

    // ── Header ──────────────────────────────────────────────────────────────
    private static JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_APP);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setBackground(BG_APP);

        JButton backBtn = ghostButton("← Back to list");
        backBtn.addActionListener(e -> MainGUI.showListView());
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = label("Start New Offboarding",
                new Font("Segoe UI", Font.BOLD, 18), TEXT_PRIMARY);
        JLabel sub = label("Register an employee to begin the offboarding pipeline",
                FONT_UI, TEXT_MUTED);

        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleBlock.add(backBtn);
        titleBlock.add(Box.createVerticalStrut(8));
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(3));
        titleBlock.add(sub);

        header.add(titleBlock, BorderLayout.WEST);
        return header;
    }

    // ── Form ────────────────────────────────────────────────────────────────
    private static JScrollPane buildFormScroll() {
        JPanel formCard = card();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        formCard.add(hRule());
        formCard.add(Box.createVerticalStrut(10));

        // ── Fields ──────────────────────────────────────────────────────────
        empIdField = styledField();
        nameField  = styledField();
        roleField  = styledField();
        ILookupIntegration lookup = MainGUI.getCustomization().getLookupIntegration();

        // ── Department ─────────────────────────
        try {
            String[] depts = lookup.getValues("DEPARTMENT").toArray(new String[0]);
            deptBox = styledCombo(depts);
        } catch (CustomizationException e) {
            deptBox = styledCombo(new String[]{"Engineering", "HR"});
        }

        setFieldHeight(empIdField);
        setFieldHeight(nameField);
        setFieldHeight(roleField);
        setFieldHeight(deptBox);

        // ── Date Picker (Popup Calendar) ────────────────────────────────────
        UtilDateModel model = new UtilDateModel();
        model.setSelected(true);

        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        lastDayPicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        setFieldHeight(lastDayPicker);

        String[] exitTypes;

        try {
            exitTypes = lookup.getValues("EXIT_TYPE").toArray(new String[0]);
        } catch (CustomizationException e) {
            exitTypes = new String[] { "Resignation", "Termination" }; // fallback
        }

        exitTypeBox = styledCombo(exitTypes);

        setFieldHeight(exitTypeBox);

        // ── Grid (NO MORE STRETCHING) ───────────────────────────────────────
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(BG_CARD);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int y = 0;

        gbc.gridx = 0; gbc.gridy = y;
        grid.add(formFieldBlock("Employee ID *", empIdField), gbc);
        gbc.gridx = 1;
        grid.add(formFieldBlock("Full Name *", nameField), gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        grid.add(formFieldBlock("Role *", roleField), gbc);
        gbc.gridx = 1;
        grid.add(formFieldBlock("Department *", deptBox), gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        grid.add(formFieldBlock("Last Day", lastDayPicker), gbc);
        gbc.gridx = 1;
        grid.add(formFieldBlock("Exit Type *", exitTypeBox), gbc);

        formCard.add(grid);
        formCard.add(Box.createVerticalStrut(16));

        // ── Submit ─────────────────────────────────────────────────────────
        JButton submitBtn = primaryButton("Register Employee & Begin Offboarding");
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitBtn.addActionListener(e -> MainGUI.submitNewEmployee());

        formCard.add(submitBtn);

        formCard.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(formCard);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_APP);
        return scroll;
    }

    // ── Normalize input height ──────────────────────────────────────────────
    private static void setFieldHeight(JComponent comp) {
        comp.setPreferredSize(new Dimension(0, 32));
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
    }
}

class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {

    private final java.text.SimpleDateFormat format =
            new java.text.SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Object stringToValue(String text) throws java.text.ParseException {
        return format.parseObject(text);
    }

    @Override
    public String valueToString(Object value) {
        if (value != null) {
            java.util.Calendar cal = (java.util.Calendar) value;
            return format.format(cal.getTime());
        }
        return "";
    }
}