package gui;

import javax.swing.*;
import java.awt.*;

import static gui.Theme.*;

/**
 * Custom-painted circular icon that reflects the current state of a pipeline step.
 * Supports animated spinning for the RUNNING state via {@link #tick()}.
 */
public class StepIcon extends JComponent {

    StepState state = StepState.PENDING;
    private int spinFrame = 0;

    public StepIcon() {
        Dimension sz = new Dimension(26, 26);
        setPreferredSize(sz);
        setMinimumSize(sz);
        setMaximumSize(sz);
        setOpaque(false);
    }

    public void setState(StepState s) {
        this.state = s;
        repaint();
    }

    /** Advance the spinner by one frame and repaint. */
    public void tick() {
        spinFrame = (spinFrame + 1) % 8;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        int sz = Math.min(getWidth(), getHeight());
        int p = 1, d = sz - p * 2;
        float cx = p + d / 2f, cy = p + d / 2f;

        switch (state) {
            case PENDING: paintPending(g2, p, d); break;
            case RUNNING:  paintRunning(g2, p, d, cx, cy); break;
            case AWAITING: paintAwaiting(g2, p, d, cx, cy); break;
            case DONE:     paintDone(g2, p, d, cx, cy); break;
            case ERROR:    paintError(g2, p, d, cx, cy); break;
        }
        g2.dispose();
    }

    private void paintPending(Graphics2D g2, int p, int d) {
        g2.setColor(new Color(0x2A2A2D));
        g2.fillOval(p, p, d, d);
        g2.setColor(new Color(0x3C3C40));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawOval(p, p, d - 1, d - 1);
    }

    private void paintRunning(Graphics2D g2, int p, int d, float cx, float cy) {
        g2.setColor(AMBER_BG);
        g2.fillOval(p, p, d, d);
        g2.setColor(AMBER_BORDER);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawOval(p, p, d - 1, d - 1);
        float r = d / 2f - 4f;
        for (int i = 0; i < 8; i++) {
            int age = ((spinFrame - i) + 8) % 8;
            float alpha = 1f - age / 8f;
            g2.setColor(new Color(0.9f, 0.68f, 0.30f, alpha));
            double angle = Math.toRadians(i * 45);
            float dx = (float) Math.cos(angle) * r;
            float dy = (float) Math.sin(angle) * r;
            g2.fillOval((int) (cx + dx - 1.5f), (int) (cy + dy - 1.5f), 3, 3);
        }
    }

    private void paintAwaiting(Graphics2D g2, int p, int d, float cx, float cy) {
        g2.setColor(BLUE_BG);
        g2.fillOval(p, p, d, d);
        g2.setColor(BLUE_BORDER);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawOval(p, p, d - 1, d - 1);
        g2.setColor(BLUE);
        g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        double hA = Math.toRadians(-60);
        float  hL = d / 2f * 0.32f;
        g2.drawLine((int) cx, (int) cy,
                (int) (cx + Math.cos(hA) * hL), (int) (cy + Math.sin(hA) * hL));
        double mA = Math.toRadians(-90);
        float  mL = d / 2f * 0.48f;
        g2.drawLine((int) cx, (int) cy,
                (int) (cx + Math.cos(mA) * mL), (int) (cy + Math.sin(mA) * mL));
        g2.fillOval((int) cx - 1, (int) cy - 1, 3, 3);
    }

    private void paintDone(Graphics2D g2, int p, int d, float cx, float cy) {
        g2.setColor(GREEN_BG);
        g2.fillOval(p, p, d, d);
        g2.setColor(GREEN_BORDER);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawOval(p, p, d - 1, d - 1);
        g2.setColor(GREEN);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        float u = d / 10f;
        g2.drawLine((int) (cx - 2.4f * u), (int) (cy + 0.2f * u),
                    (int) (cx - 0.4f * u), (int) (cy + 2.2f * u));
        g2.drawLine((int) (cx - 0.4f * u), (int) (cy + 2.2f * u),
                    (int) (cx + 2.8f * u), (int) (cy - 2f * u));
    }

    private void paintError(Graphics2D g2, int p, int d, float cx, float cy) {
        g2.setColor(RED_BG);
        g2.fillOval(p, p, d, d);
        g2.setColor(RED_BORDER);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawOval(p, p, d - 1, d - 1);
        g2.setColor(RED);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        float u = d / 10f * 2f;
        g2.drawLine((int) (cx - u), (int) (cy - u), (int) (cx + u), (int) (cy + u));
        g2.drawLine((int) (cx + u), (int) (cy - u), (int) (cx - u), (int) (cy + u));
    }
}
