package gui;

import javax.swing.*;
import java.awt.*;

import static gui.Theme.*;

/**
 * Thin, rounded, animated progress bar.
 * Call {@link #setProgress(float, Color)} to update — the fill animates smoothly.
 */
public class ProgressBar extends JComponent {

    private float pct      = 0f;
    private Color barColor = GREEN;
    private float animPct  = 0f;
    private Timer animTimer;

    public ProgressBar() {
        setPreferredSize(new Dimension(0, 8));
        setMinimumSize(new Dimension(0, 8));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        setOpaque(false);
    }

    public void setProgress(float target, Color color) {
        this.pct      = Math.max(0f, Math.min(1f, target));
        this.barColor = color;
        if (animTimer != null) animTimer.stop();
        animTimer = new Timer(16, null);
        animTimer.addActionListener(e -> {
            float diff = pct - animPct;
            if (Math.abs(diff) < 0.005f) {
                animPct = pct;
                animTimer.stop();
            } else {
                animPct += diff * 0.18f;
            }
            repaint();
        });
        animTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight(), arc = h;
        // Track
        g2.setColor(new Color(0x2C2C30));
        g2.fillRoundRect(0, 0, w, h, arc, arc);
        // Fill
        int fillW = (int) (w * animPct);
        if (fillW > 0) {
            g2.setColor(barColor);
            g2.fillRoundRect(0, 0, Math.max(fillW, arc), h, arc, arc);
        }
        // Gloss
        g2.setColor(new Color(255, 255, 255, 18));
        g2.fillRoundRect(0, 0, w, h / 2, arc, arc);
        g2.dispose();
    }
}
