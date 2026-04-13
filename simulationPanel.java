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

        for (int i = 0; i <= 200; i++) {
            String nome = "Planeta" + i;
            int raio = 3 + (int)(Math.random() * 8);
            Color cor = new Color((int)(Math.random() * 255),(int)(Math.random() * 255),(int)(Math.random() * 255));

            double semieixoX = 60 + Math.random() * 390;
            double semieixoY = semieixoX * (0.6 + Math.random() * 0.35);
            if (Math.random() < 0.5) {
                semieixoY = semieixoX;
            } else {
                semieixoY = semieixoX * (0.5 + Math.random() * 0.4);
            }

            double vel = 0.005 + (1.0 / semieixoX) * 2;

            corpos.add(new corpoCeleste(nome, cx, cy, raio, cor, semieixoX, semieixoY, vel));
        }

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
