import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import body.Estrela;

public class simulationPanel extends JPanel {
    private final List<corpoCeleste> corpos = new ArrayList<>();
    private final List<Estrela> estrelas = new ArrayList<>();

    public simulationPanel() {
        setBackground(Color.BLACK);
        initSystem();
    }

    private void initSystem() {
        int cx = 450,
            cy = 450;

        for (int i = 0; i < 300; i++) {
            estrelas.add(new Estrela(900, 900));
        }

        corpos.add(new corpoCeleste("Sol", cx, cy, 30, Color.YELLOW, 0, 0, 0));

        List<corpoCeleste> pcs = new ArrayList<>();

        for (int i = 0; i <= 12; i++) {
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

            corpoCeleste p = new corpoCeleste(nome, cx, cy, raio, cor, semieixoX, semieixoY, vel);
            corpos.add(p);

            if (Math.random() < 0.5){
                pcs.add(p);
            }
        }

        for(corpoCeleste p : pcs){
            int nums = 1 + (int)(Math.random() * 3);

            for(int j = 0; j < nums; j++){
                String ns = p.nome + "-nsat" + j;
                int rs = 2 + (int)(Math.random() * 4);
                Color cs = Color.RED;
                double semieixoXs = 20 + j * 10;
                double semieixoYs = semieixoXs * (0.7 + Math.random() * 0.2);
                double vs = 0.05;

                corpoCeleste s = new corpoCeleste(ns,0, 0,rs,cs,semieixoXs,semieixoYs,vs,p);
                corpos.add(s);
            }
        }
    }

    public void update() {

        estrelas.forEach(Estrela::step);

        Map<corpoCeleste, Integer> prf = new HashMap<>();
        for (corpoCeleste c : corpos){
            prf.put(c, calcularprf(c));
        }

        corpos.sort(Comparator.comparingInt(prf::get));

        corpos.forEach(corpoCeleste::step);
        repaint();
    }

    private int calcularprf(corpoCeleste c){
        if (c.orbitando == null) return 0;
        return 1 + calcularprf(c.orbitando);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        estrelas.forEach(e -> e.draw(g2));

        g2.setColor(new Color(255, 255, 255, 30));
           for (corpoCeleste c : corpos) {
               if (c.orbitando == null && c.semieixoX > 0) {
                   double centroX = c.focoX + c.c;
                   double centroY = c.focoY;
                   g2.draw(new Ellipse2D.Double(
                       centroX - c.semieixoX,
                       centroY - c.semieixoY,
                       c.semieixoX * 2,
                       c.semieixoY * 2
                   ));
               } else if (c.orbitando != null) {
                   double centroX = c.orbitando.x + c.c;
                   double centroY = c.orbitando.y;
                   g2.draw(new Ellipse2D.Double(
                       centroX - c.semieixoX,
                       centroY - c.semieixoY,
                       c.semieixoX * 2,
                       c.semieixoY * 2
                   ));
               }
           }

           corpos.forEach(c -> c.desenhar(g2));
       }
   }
