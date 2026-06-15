package body;

import java.awt.*;

public class Asteroide extends corpoCeleste {
    private double velX, velY;
    private int larguraTela, alturaTela;

    public Asteroide(String nome, double x, double y, int raio, Color color,
                     double velX, double velY, int larguraTela, int alturaTela) {
        super(nome, x, y, raio, color);
        this.velX = velX;
        this.velY = velY;
        this.larguraTela = larguraTela;
        this.alturaTela = alturaTela;
    }

    public void setLimitesTela(int largura, int altura) {
        this.larguraTela = largura;
        this.alturaTela = altura;
    }

    @Override
    public void step() {
        x += velX;
        y += velY;

        //wrap-around
        if (x < 0) x = larguraTela;
        if (x > larguraTela) x = 0;
        if (y < 0) y = alturaTela;
        if (y > alturaTela) y = 0;
    }

    @Override
    public void draw(Graphics2D d) {
        d.setColor(color);
        d.fillOval((int)(x - raio), (int)(y - raio), raio * 2, raio * 2);

        d.setColor(Color.WHITE);
        d.setFont(new Font("Arial", Font.PLAIN, 9));
        d.drawString(nome, (int)x + raio + 3, (int)y + 4);
    }
}
