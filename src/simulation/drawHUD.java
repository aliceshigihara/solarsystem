package simulation;

import body.*;
import human.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

public class drawHUD {

    public static void drawPainelInfo(
            Graphics2D g2,
            List<corpoCeleste> corpos,
            List<Estrela> estrelas,
            List<String> infoFoguetes,
            boolean estrelasAtivas
    ) {
        int altPainel = 160 + (infoFoguetes.size() * 15);

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(10, 10, 320, altPainel);
        g2.setColor(new Color(255, 255, 255, 50));
        g2.drawRect(10, 10, 320, altPainel);

        int planetas = 0;
        int satelites = 0;
        int foguetes = 0;
        int asteroides = 0;
        int foguetesEmVoo = 0;
        int foguetesAterrissados = 0;

        for (corpoCeleste c : corpos) {
            if (c instanceof Planetas) planetas++;
            if (c instanceof Satelite) satelites++;
            if (c instanceof Asteroide) asteroides++;

            if (c instanceof Foguete) {
                foguetes++;
                Foguete f = (Foguete) c;

                if (f.isAterrissado()) {
                    foguetesAterrissados++;
                } else {
                    foguetesEmVoo++;
                }
            }
        }

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString("Sistema Solar", 20, 30);

        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString("Ativar/Desativar estrelas -> D", 20, 240);
        g2.drawString("Sol: 1", 20, 50);
        g2.drawString("Planetas: " + planetas, 20, 65);
        g2.drawString("Satélites: " + satelites, 20, 80);
        g2.drawString(
                "Foguetes: " + foguetes + " (Voo: " + foguetesEmVoo + ", Aterr: " + foguetesAterrissados + ")",
                20,
                95
        );
        g2.drawString("Asteroides: " + asteroides, 20, 110);
        g2.drawString("Estrelas: " + (estrelasAtivas ? estrelas.size() : 0) +
                " [" + (estrelasAtivas ? "ON" : "OFF") + "]", 20, 125);
        g2.drawString("Configs de foguete: " + infoFoguetes.size(), 20, 140);

        int y = 160;
        for (String info : infoFoguetes) {
            g2.setColor(Color.YELLOW);
            g2.drawString(info, 20, y);
            y += 15;
        }
    }

    public static void drawTooltip(Graphics2D g2, corpoCeleste corpoSobMouse) {
        if (corpoSobMouse == null) return;

        int tx = (int) corpoSobMouse.getX() + 25;
        int ty = (int) corpoSobMouse.getY() - 60;

        if (corpoSobMouse instanceof Planetas) {
            Planetas p = (Planetas) corpoSobMouse;
            Nucleo n = p.getNucleo();

            g2.setColor(new Color(0, 0, 0, 220));
            g2.fillRect(tx, ty, 220, 120);
            g2.setColor(Color.WHITE);
            g2.drawRect(tx, ty, 220, 120);

            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString(corpoSobMouse.getNome(), tx + 5, ty + 20);

            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            String[] linhas = n.getDescricao().split("\\n");
            for (int i = 0; i < linhas.length; i++) {
                g2.drawString(linhas[i], tx + 5, ty + 35 + (i * 15));
            }
        }

        if (corpoSobMouse instanceof Foguete) {
            Foguete f = (Foguete) corpoSobMouse;

            g2.setColor(new Color(0, 0, 0, 220));
            g2.fillRect(tx, ty, 200, 60);
            g2.setColor(Color.WHITE);
            g2.drawRect(tx, ty, 200, 60);

            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString(f.getNome(), tx + 5, ty + 20);

            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            g2.drawString("Destino: " + f.getDestino().getNome(), tx + 5, ty + 35);
            g2.drawString("Status: " + (f.isAterrissado() ? "Aterrissado" : "Em voo"), tx + 5, ty + 50);
        }

    }
}
