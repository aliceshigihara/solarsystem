import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import body.*;

public class simulationPanel extends JPanel {
    private final List<corpoCeleste> corpos = new ArrayList<>();
    private final List<Estrela> estrelas = new ArrayList<>();

    public simulationPanel() {
        setBackground(Color.BLACK);
        setupMouseListener();
        initSystem();
    }

    private void initSystem() {
        int cx = 450,
            cy = 450;

        for (int i = 0; i < 300; i++) {
            estrelas.add(new Estrela(900, 900));
        }

        corpoCeleste sol = new corpoCeleste("sol",cx, cy, 30, Color.YELLOW);
        corpos.add(sol);

        List<corpoCeleste> pcs = new ArrayList<>();

        for (int i = 0; i <= 8; i++) {
            String nome = "";
            int raio = 4 + (int)(Math.random() * 10);
            Color cor = new Color((int)(Math.random() * 255),(int)(Math.random() * 255),(int)(Math.random() * 255));

            double semieixoX = 80 + (i * 35) + Math.random() * 30;
            double semieixoY;
            if (Math.random() < 0.5) {
                semieixoY = semieixoX;
            } else {
                semieixoY = semieixoX * (0.5 + Math.random() * 0.4);
            }
            double velAngular = 0.003 + (1.5 / semieixoX);

            Nucleo nucleo = Nucleo.criarNucleoAleatorio(raio, semieixoX);

            Orbita orbita = new Orbita(sol, semieixoX, semieixoY, velAngular);

            Planetas p = new Planetas(nome, raio, cor, orbita, nucleo, Math.random() < 0.5);            corpos.add(p);

            if (Math.random() < 0.5){
                pcs.add(p);
            }
        }

        for(corpoCeleste p : pcs){
            int nums = 1 + (int)(Math.random() * 3);

            for(int j = 0; j < nums; j++){
                String ns = " ";
                int rs = 2 + (int)(Math.random() * 4);
                Color cs = Color.RED;
                double semieixoXs = 15 + (j * 10) + Math.random() * 10;
                double semieixoYs = semieixoXs * (0.7 + Math.random() * 0.3);
                double vs = 0.08 + Math.random() * 0.04;

                Orbita os = new Orbita(p, semieixoXs, semieixoYs, vs);

                Satelite sat = new Satelite(ns, rs, cs, os, Math.random() < 0.3);

                corpos.add(sat);
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
        if (!c.temOrbita()) return 0;
        return 1 + calcularprf(c.getOrbita().getCentro());
    }

    private corpoCeleste corpoSobMouse = null;

    // Método para detectar mouse (adicione no construtor)
    private void setupMouseListener() {
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                corpoSobMouse = null;
                for (corpoCeleste c : corpos) {
                    double dx = e.getX() - c.getX();
                    double dy = e.getY() - c.getY();
                    if (Math.sqrt(dx*dx + dy*dy) < c.getRaio() + 3) {
                        corpoSobMouse = c;
                        break;
                    }
                }
            }
        });
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
            if (c.temOrbita()) {
                Orbita orb = c.getOrbita();
                corpoCeleste centro = orb.getCentro();
                double centroX = centro.getX() + orb.getC();
                double centroY = centro.getY();

                g2.draw(new Ellipse2D.Double(
                    centroX - orb.getSemieixoX(),
                    centroY - orb.getSemieixoY(),
                    orb.getSemieixoX() * 2,
                    orb.getSemieixoY() * 2
                ));
            }
        }

        corpos.forEach(c -> c.draw(g2));

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString("Sistema Solar - Simulação", 20, 30);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString("Corpos: " + corpos.size(), 20, 50);
        g2.drawString("Estrelas: " + estrelas.size(), 20, 65);

        int planetas = 0, satelites = 0;
        for (corpoCeleste c : corpos) {
            if (c instanceof Planetas) planetas++;
            if (c instanceof Satelite) satelites++;
        }
        g2.drawString("Planetas: " + planetas, 20, 80);
        g2.drawString("Satélites: " + satelites, 20, 95);

        if (corpoSobMouse instanceof Planetas) {
            Planetas p = (Planetas) corpoSobMouse;
            Nucleo n = p.getNucleo();

            g2.setColor(new Color(0, 0, 0, 220));
            g2.fillRect((int)corpoSobMouse.getX() + 20, (int)corpoSobMouse.getY() - 60, 220, 110);
            g2.setColor(Color.WHITE);
            g2.drawRect((int)corpoSobMouse.getX() + 20, (int)corpoSobMouse.getY() - 60, 220, 110);

            // Texto
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("☄ " + corpoSobMouse.getNome(),
                          (int)corpoSobMouse.getX() + 25, (int)corpoSobMouse.getY() - 40);

            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            String[] linhas = n.getDescricao().split("\n");
            for (int i = 0; i < linhas.length; i++) {
                g2.drawString(linhas[i],
                             (int)corpoSobMouse.getX() + 25,
                             (int)corpoSobMouse.getY() - 25 + (i * 15));
            }
        }

    }
}
