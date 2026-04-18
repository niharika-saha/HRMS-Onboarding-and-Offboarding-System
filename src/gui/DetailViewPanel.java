package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import static gui.PipelineConfig.*;
import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Builds the detail view for a single employee: pipeline steps, progress insights,
 * and the activity log. Also owns the live widget references that are updated
 * during step execution.
 */
public final class DetailViewPanel {

    private DetailViewPanel() {}

    // ── Live widget references (updated externally) ───────────────────────────
    public static final Map<String, StepIcon> stepIcons        = new LinkedHashMap<>();
    public static final Map<String, JLabel>   stepNameLabels   = new LinkedHashMap<>();
    public static final Map<String, JLabel>   stepStatusLabels = new LinkedHashMap<>();
    public static ProgressBar detailProgressBar;
    public static JLabel      detailPct;
    public static JLabel      detailStage;
    public static JTextPane   detailLogPane;
    public static JButton     detailProceedBtn;
    public static JButton     detailResetBtn;

    // ── Build ─────────────────────────────────────────────────────────────────

    public static JPanel build(EmployeeRecord emp) {
        stepIcons.clear();
        stepNameLabels.clear();
        stepStatusLabels.clear();

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_APP);
        outer.setBorder(new EmptyBorder(20, 24, 20, 24));

        outer.add(buildHeader(emp), BorderLayout.NORTH);

        JPanel cols = new JPanel(new BorderLayout(16, 0));
        cols.setBackground(BG_APP);
        cols.add(buildLeftCol(emp),  BorderLayout.CENTER);
        cols.add(buildRightCol(emp), BorderLayout.EAST);
        outer.add(cols, BorderLayout.CENTER);

        // Restore saved step states
        for (String key : STEP_KEYS)
            applyStepState(key, emp.stepStates.get(key), emp);
        updateInsights(emp);
        refreshProceedButton(emp);
        return outer;
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private static JPanel buildHeader(EmployeeRecord emp) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_APP);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(BG_APP);

        JButton backBtn = ghostButton("← Back to list");
        backBtn.addActionListener(e -> {
            MainGUI.stopSpinner();
            MainGUI.showListView();
        });
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = label(emp.name,
                new Font("Segoe UI", Font.BOLD, 18), TEXT_PRIMARY);
        JLabel metaLabel = label(
            emp.role + "  ·  " + emp.department
            + "  ·  Last day: " + emp.lastDay
            + "  ·  " + (emp.exitType != null ? emp.exitType : "—"),
            FONT_UI, TEXT_MUTED);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        metaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(backBtn);
        left.add(Box.createVerticalStrut(10));
        left.add(nameLabel);
        left.add(Box.createVerticalStrut(3));
        left.add(metaLabel);
        header.add(left, BorderLayout.WEST);
        header.add(statusChip(emp.overallStatus, emp.statusColor()), BorderLayout.EAST);
        return header;
    }

    // ── Left column: employee info + pipeline steps ───────────────────────────

    private static JPanel buildLeftCol(EmployeeRecord emp) {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(BG_APP);

        col.add(buildInfoCard(emp));
        col.add(Box.createVerticalStrut(14));
        col.add(buildPipelineCard(emp));
        col.add(Box.createVerticalGlue());
        return col;
    }

    private static JPanel buildInfoCard(EmployeeRecord emp) {
        JPanel infoCard = card();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.add(cardTitle("Employee information"));
        infoCard.add(Box.createVerticalStrut(12));
        addInfoRow(infoCard, "Employee ID", emp.empId);
        addInfoRow(infoCard, "Exit type",   emp.exitType != null ? emp.exitType.toString() : "—");

        if (emp.interviewDataCollected) {
            addInfoRow(infoCard, "Successor ID", emp.successorId.isEmpty() ? "—" : emp.successorId);
            addInfoRow(infoCard, "Reason",       emp.reason.isEmpty()      ? "—" : emp.reason);
            addInfoRow(infoCard, "Feedback",     emp.feedback.isEmpty()    ? "—" : emp.feedback);
            addInfoRow(infoCard, "Rating",       emp.rating > 0 ? emp.rating + " / 5" : "—");
        } else {

        }
        return infoCard;
    }

    private static void addInfoRow(JPanel card, String lbl, String val) {
        JPanel r = new JPanel(new BorderLayout(16, 0));
        r.setBackground(BG_CARD);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        r.setAlignmentX(Component.LEFT_ALIGNMENT);
        r.add(label(lbl, FONT_LABEL, TEXT_MUTED),   BorderLayout.WEST);
        r.add(label(val, FONT_SMALL, TEXT_PRIMARY),  BorderLayout.EAST);
        card.add(r);
        card.add(Box.createVerticalStrut(8));
    }

    private static JPanel buildPipelineCard(EmployeeRecord emp) {
        JPanel pipeCard = card();
        pipeCard.setLayout(new BoxLayout(pipeCard, BoxLayout.Y_AXIS));
        pipeCard.add(cardTitle("Pipeline progress"));
        pipeCard.add(Box.createVerticalStrut(14));
        for (String key : STEP_KEYS) {
            pipeCard.add(buildStepRow(key));
            pipeCard.add(Box.createVerticalStrut(10));
        }
        pipeCard.add(hRule());
        pipeCard.add(Box.createVerticalStrut(12));
        pipeCard.add(buildButtonRow(emp));
        return pipeCard;
    }

    private static JPanel buildStepRow(String key) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(BG_CARD);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        StepIcon icon = new StepIcon();
        stepIcons.put(key, icon);
        JLabel name = label(STEP_NAMES.get(key), FONT_UI, TEXT_SECONDARY);
        stepNameLabels.put(key, name);
        JLabel status = label("", FONT_SMALL, TEXT_MUTED);
        stepStatusLabels.put(key, status);

        row.add(icon, BorderLayout.WEST);
        row.add(name, BorderLayout.CENTER);
        row.add(status, BorderLayout.EAST);
        return row;
    }

    private static JPanel buildButtonRow(EmployeeRecord emp) {
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(BG_CARD);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailProceedBtn = primaryButton("Proceed to Next Step");
        detailProceedBtn.addActionListener(e -> MainGUI.proceedNextStep(emp));

        detailResetBtn = new JButton("Reset");
        detailResetBtn.setFont(FONT_BOLD);
        detailResetBtn.setForeground(RED);
        detailResetBtn.setBackground(RED_BG);
        detailResetBtn.setOpaque(true);
        detailResetBtn.setContentAreaFilled(true);
        detailResetBtn.setBorder(new CompoundBorder(
            new LineBorder(RED_BORDER, 1, true),
            new EmptyBorder(8, 16, 8, 16)));
        detailResetBtn.setFocusPainted(false);
        detailResetBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        detailResetBtn.addActionListener(e -> MainGUI.resetEmployeePipeline(emp));
        detailResetBtn.addMouseListener(hoverEffect(detailResetBtn, new Color(0x3A1010), RED_BG));

        btnRow.add(detailProceedBtn);
        btnRow.add(detailResetBtn);
        return btnRow;
    }

    // ── Right column: insights + log ──────────────────────────────────────────

    private static JPanel buildRightCol(EmployeeRecord emp) {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(BG_APP);
        col.setPreferredSize(new Dimension(290, 0));

        col.add(buildInsightsCard());
        col.add(Box.createVerticalStrut(14));
        col.add(buildLogCard(emp));
        col.add(Box.createVerticalGlue());
        return col;
    }

    private static JPanel buildInsightsCard() {
        JPanel insCard = card();
        insCard.setLayout(new BoxLayout(insCard, BoxLayout.Y_AXIS));
        insCard.add(cardTitle("Progress Insights"));
        insCard.add(Box.createVerticalStrut(14));

        JPanel pctRow = new JPanel(new BorderLayout());
        pctRow.setBackground(BG_CARD);
        pctRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        pctRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        detailPct = label("0%", new Font("Segoe UI", Font.BOLD, 12), TEXT_PRIMARY);
        pctRow.add(label("Completion", FONT_LABEL, TEXT_MUTED), BorderLayout.WEST);
        pctRow.add(detailPct, BorderLayout.EAST);
        insCard.add(pctRow);
        insCard.add(Box.createVerticalStrut(6));

        detailProgressBar = new ProgressBar();
        detailProgressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        insCard.add(detailProgressBar);
        insCard.add(Box.createVerticalStrut(14));
        insCard.add(hRule());
        insCard.add(Box.createVerticalStrut(12));

        JLabel stageLbl = label("Current stage", FONT_LABEL, TEXT_MUTED);
        stageLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        insCard.add(stageLbl);
        detailStage = label("Not started", FONT_BOLD, TEXT_MUTED);
        detailStage.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailStage.setBorder(new EmptyBorder(2, 0, 0, 0));
        insCard.add(detailStage);
        return insCard;
    }

    private static JPanel buildLogCard(EmployeeRecord emp) {
        JPanel logCard = card();
        logCard.setLayout(new BoxLayout(logCard, BoxLayout.Y_AXIS));

        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(BG_CARD);
        hdr.setAlignmentX(Component.LEFT_ALIGNMENT);
        hdr.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        hdr.add(cardTitle("Activity log"), BorderLayout.WEST);
        JButton clearBtn = ghostButton("Clear");
        clearBtn.addActionListener(e -> {
            if (detailLogPane != null) detailLogPane.setText("");
            emp.log.clear();
        });
        hdr.add(clearBtn, BorderLayout.EAST);
        logCard.add(hdr);
        logCard.add(Box.createVerticalStrut(10));

        detailLogPane = new JTextPane();
        detailLogPane.setEditable(false);
        detailLogPane.setBackground(BG_SURFACE);
        detailLogPane.setForeground(TEXT_PRIMARY);
        detailLogPane.setCaretColor(TEXT_PRIMARY);
        detailLogPane.setBorder(new EmptyBorder(6, 8, 6, 8));

        for (String[] entry : emp.log) {
            appendToPane(detailLogPane, entry[0] + "  ", TEXT_MUTED);
            appendToPane(detailLogPane, entry[1] + "\n",
                    parseHexColor(entry[2], TEXT_SECONDARY));
        }

        JScrollPane scroll = new JScrollPane(detailLogPane);
        scroll.setBorder(new LineBorder(BORDER, 1, true));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setPreferredSize(new Dimension(0, 300));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        scroll.getViewport().setBackground(BG_SURFACE);
        logCard.add(scroll);
        return logCard;
    }

    // ── State mutators (called by MainGUI) ────────────────────────────────────

    public static void applyStepState(String key, StepState state, EmployeeRecord emp) {
        StepIcon icon   = stepIcons.get(key);
        JLabel   name   = stepNameLabels.get(key);
        JLabel   status = stepStatusLabels.get(key);
        if (icon == null || name == null || status == null) return;
        icon.setState(state);
        emp.stepStates.put(key, state);
        switch (state) {
            case RUNNING:
                name.setFont(FONT_BOLD); name.setForeground(TEXT_PRIMARY);
                status.setText("Running");  status.setForeground(AMBER);   break;
            case AWAITING:
                name.setFont(FONT_BOLD); name.setForeground(TEXT_PRIMARY);
                status.setText("Awaiting"); status.setForeground(BLUE);    break;
            case DONE:
                name.setFont(FONT_UI);   name.setForeground(TEXT_SECONDARY);
                status.setText("Done");    status.setForeground(GREEN);    break;
            case ERROR:
                name.setFont(FONT_UI);   name.setForeground(TEXT_SECONDARY);
                status.setText("Error");   status.setForeground(RED);      break;
            default:
                name.setFont(FONT_UI);   name.setForeground(TEXT_SECONDARY);
                status.setText("");        status.setForeground(TEXT_MUTED); break;
        }
    }

    public static void updateInsights(EmployeeRecord emp) {
        SwingUtilities.invokeLater(() -> {
            if (detailProgressBar == null || detailPct == null || detailStage == null) return;
            float pct      = emp.completionPct();
            Color barColor = emp.errorCount > 0 ? RED : (emp.awaitCount > 0 ? BLUE : GREEN);
            detailProgressBar.setProgress(pct, barColor);
            detailPct.setText(Math.round(pct * 100) + "%");

            if (emp.doneCount == STEP_KEYS.length) {
                detailStage.setText("All steps complete");
                detailStage.setForeground(GREEN);
            } else if (emp.errorCount > 0) {
                for (String k : STEP_KEYS)
                    if (emp.stepStates.get(k) == StepState.ERROR) {
                        detailStage.setText(STEP_NAMES.get(k) + " failed");
                        detailStage.setForeground(RED);
                        break;
                    }
            } else if (emp.awaitCount > 0) {
                for (String k : STEP_KEYS)
                    if (emp.stepStates.get(k) == StepState.AWAITING) {
                        detailStage.setText(STEP_NAMES.get(k) + " awaiting");
                        detailStage.setForeground(BLUE);
                        break;
                    }
            } else {
                String next = emp.nextPendingKey();
                if (next != null) {
                    StepState s = emp.stepStates.get(next);
                    detailStage.setText(STEP_NAMES.get(next) +
                        (s == StepState.RUNNING ? " in progress" : " — next up"));
                    detailStage.setForeground(s == StepState.RUNNING ? AMBER : TEXT_SECONDARY);
                } else {
                    detailStage.setText("Not started");
                    detailStage.setForeground(TEXT_MUTED);
                }
            }
        });
    }

    public static void refreshProceedButton(EmployeeRecord emp) {
        if (detailProceedBtn == null) return;
        String nextKey = emp.nextPendingKey();
        if (nextKey == null) {
            detailProceedBtn.setText("All steps complete");
            detailProceedBtn.setEnabled(false);
            return;
        }
        StepState curState = emp.stepStates.get(nextKey);
        if (curState == StepState.ERROR || curState == StepState.AWAITING) {
            detailProceedBtn.setText("Retry: " + STEP_NAMES.get(nextKey));
            detailProceedBtn.setEnabled(true);
        } else if (curState == StepState.RUNNING) {
            detailProceedBtn.setText("Running: " + STEP_NAMES.get(nextKey));
            detailProceedBtn.setEnabled(false);
        } else {
            detailProceedBtn.setText("Proceed: " + STEP_NAMES.get(nextKey));
            detailProceedBtn.setEnabled(true);
        }
    }

    // ── Log helpers ───────────────────────────────────────────────────────────

    public static void appendDetailLog(EmployeeRecord emp, String msg, Color color) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        emp.log.add(new String[]{time, msg, colorHex(color)});
        if (detailLogPane != null) {
            SwingUtilities.invokeLater(() -> {
                appendToPane(detailLogPane, time + "  ", TEXT_MUTED);
                appendToPane(detailLogPane, msg + "\n",  color);
                detailLogPane.setCaretPosition(
                        detailLogPane.getStyledDocument().getLength());
            });
        }
    }

    private static void appendToPane(JTextPane pane, String text, Color color) {
        try {
            StyledDocument doc = pane.getStyledDocument();
            Style s = pane.addStyle("s" + System.nanoTime(), null);
            StyleConstants.setForeground(s, color);
            StyleConstants.setFontFamily(s, "Consolas");
            StyleConstants.setFontSize(s, 12);
            doc.insertString(doc.getLength(), text, s);
        } catch (Exception ignored) {}
    }
}
