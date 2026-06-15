package human;

import body.corpoCeleste;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class Foguete extends corpoCeleste {
    private corpoCeleste destino;
    private double velocidade;
    private boolean aterrissado = false;
    private double angulo = 0;

    public Foguete(String nome, double x, double y, int raio, Color color,
                   corpoCeleste destino, double velocidade) {
        super(nome, x, y, raio, color);
        this.destino = destino;
        this.velocidade = velocidade;
    }

    public corpoCeleste getDestino() {
        return destino;
    }

    public boolean isAterrissado() {
        return aterrissado;
    }

    @Override
    public void step() {
        if (!aterrissado && destino != null) {
            double dx = destino.getX() - x;
            double dy = destino.getY() - y;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < (getRaio() + destino.getRaio())) {
                aterrissado = true;
                x = destino.getX();
                y = destino.getY();
                System.out.println(getNome() + " aterrissou em " + destino.getNome() + "!");
            } else {
                double vx = (dx / dist) * velocidade;
                double vy = (dy / dist) * velocidade;
                x += vx;
                y += vy;
                angulo = Math.atan2(dy, dx);
            }
        }

        if (aterrissado && destino != null) {
            x = destino.getX();
            y = destino.getY();
        }
    }

    @Override
    public void draw(Graphics2D d) {
        AffineTransform old = d.getTransform();
        d.translate(x, y);
        d.rotate(angulo - Math.PI / 2);

        int r = getRaio();

        int[] xs = {0, -r / 2, r / 2};
        int[] ys = {-r * 2, r, r};
        d.setColor(getColor());
        d.fillPolygon(xs, ys, 3);

        if (!aterrissado) {
            d.setColor(new Color(255, 100, 0, 200));
            d.fillRect(-r / 4, r, r / 2, r);
        } else {
            d.setColor(Color.GREEN);
            d.fillRect(-r / 2, -r * 2 - 3, r, 3);
        }

        d.setTransform(old);

        d.setColor(Color.WHITE);
        d.setFont(new Font("Arial", Font.PLAIN, 10));
        d.drawString(getNome(), (int) x + r + 3, (int) y + 4);
    }
}
