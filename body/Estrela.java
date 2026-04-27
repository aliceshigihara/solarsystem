package body;

import java.awt.*;

public class Estrela{
    private double x,y;
    private int tam;
    private float brilho;
    private float velbrilho;
    private Color cor;

    public Estrela(int largT, int altT){
        this.x = Math.random() * largT;
        this.y = Math.random() * altT;
        this.tam = 1 + (int)(Math.random() * 3);
        this.brilho = 0.3f + (float)(Math.random() * 0.7);
        this.velbrilho = 0.003f + (float)(Math.random() * 0.015);
        this.cor = sortCor();
    }

    public void step(){
        brilho += velbrilho;
        if (brilho > 1.0f || brilho < 0.3f){
            velbrilho = -velbrilho;
        }
    }

    public void draw(Graphics2D g2){
        Color corbrilho = new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), (int)(brilho * 255));
        g2.setColor(corbrilho);
        g2.fillOval((int)x, (int)y, tam, tam);

        if (tam > 1 && brilho > 0.7f){
            g2.setColor(new Color(255, 255, 255, (int)(brilho * 180)));
            g2.fillOval((int)x, (int)y, 1, 1);
        }
    }

    private Color sortCor(){
        double sort = Math.random();
        if (sort < 0.7){
            return Color.WHITE;
        } else if (sort < 0.85){
            return new Color(255, 220, 180);
        } else if (sort < 0.95){
            return new Color(255, 200, 255);
        } else {
            return new Color(255, 180, 180);
        }
    }
}
