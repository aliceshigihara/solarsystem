package body;

import java.awt.*;

public class Planetas extends corpoCeleste {
    private Nucleo nucleo;
    private boolean possuiAtmosfera;
    private Color corAtmosfera;

    public Planetas(String nome, int raio, Color color, Orbita orbita, Nucleo nucleo, boolean possuiAtmosfera){
        super(nome, 0, 0, raio, color, orbita);
        this.nucleo = nucleo;
        this.possuiAtmosfera = possuiAtmosfera;
        this.corAtmosfera = possuiAtmosfera ? new Color(135, 206, 235, 40) : null;
    }

    @Override
    public void draw(Graphics2D d) {
        if (possuiAtmosfera && corAtmosfera != null) {
            d.setColor(corAtmosfera);
            d.fillOval((int) (x - getRaio() - 4), (int) (y - getRaio() - 4), (getRaio() + 4) * 2, (getRaio() + 4) * 2);
        }

        super.draw(d);
    }

    public Nucleo getNucleo() { return nucleo; }
    public boolean temAtmosfera() { return possuiAtmosfera; }
}
