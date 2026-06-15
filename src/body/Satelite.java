package body;

import java.awt.*;

public class Satelite extends corpoCeleste {
    private boolean temAtividadeGeologica;

    public Satelite(
        String nome,
        int raio,
        Color color,
        Orbita orbita,
        boolean temAtividadeGeologica
    ) {
        super(nome, 0, 0, raio, color, orbita);
        this.temAtividadeGeologica = temAtividadeGeologica;
    }

    @Override
    public void draw(Graphics2D d) {
        super.draw(d);

        if (temAtividadeGeologica) {
            d.setColor(new Color(255, 100, 0, 150));
            d.fillOval(
                (int) (x + getRaio()/3),
                (int) (y - getRaio()/3),
                getRaio()/2,
                getRaio()/2
            );
        }
    }

    public boolean temAtividadeGeologica(){
        return temAtividadeGeologica;
    }
}
