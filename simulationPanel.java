import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class simulationPanel extends JPanel {

    private final List<corpoCeleste> corpos = new ArrayList<>();

    public simulationPanel() {
        setBackground(Color.BLACK);
        initSystem();
    }

    private void initSystem() {
        int cx = 450,
            cy = 450;

        corpos.add(new corpoCeleste("Sol", cx, cy, 30, Color.YELLOW, 0, 0, 0));
        corpos.add(new corpoCeleste("Mercúrio", cx, cy, 5, Color.GRAY, 80, 55, 0.047));
        corpos.add(new corpoCeleste("Vênus", cx, cy, 8, new Color(255, 165, 0), 120, 90, 0.035));
        corpos.add(new corpoCeleste("Terra", cx, cy, 9, Color.CYAN, 165, 130, 0.029));
        corpos.add(new corpoCeleste("Marte", cx, cy, 7, Color.RED, 215, 160, 0.024));
        corpos.add(new corpoCeleste("Júpiter", cx, cy, 20, new Color(210, 140, 80), 300, 240, 0.013));
        corpos.add(new corpoCeleste("Saturno", cx, cy, 16, new Color(230, 200, 130), 375, 310, 0.009));
        corpos.add(new corpoCeleste("Urano", cx, cy, 12, new Color(130, 220, 220), 430, 370, 0.006));
        corpos.add(new corpoCeleste("Netuno", cx, cy, 11, new Color(60, 80, 230), 480, 420,0.005));
    }

    public void update() {
        corpos.forEach(corpoCeleste::step);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(new Color(255, 255, 255, 30));

        for (int i = 1; i < corpos.size(); i++) {
            corpoCeleste c = corpos.get(i);
            double centroX = c.focoX + c.c;
            double centroY = c.focoY;
            g2.draw(new Ellipse2D.Double(
                    centroX - c.semieixoX,
                    centroY - c.semieixoY,
                    c.semieixoX * 2,
                    c.semieixoY * 2
                ));
        }

        corpos.forEach(c -> c.desenhar(g2));
    }
}
