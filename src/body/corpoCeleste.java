package body;
import java.awt.*;

public class corpoCeleste {

    final String nome;
    final double focoX, focoY;
    final int raio;
    final Color color;
    public double x, y;
    private Orbita orbita;

    public corpoCeleste(String nome, double focoX, double focoY, int raio, Color color){
        this.nome = nome;
        this.focoX = focoX;
        this.focoY = focoY;
        this.raio = raio;
        this.color = color;
        this.orbita = null;
        this.x = focoX;
        this.y = focoY;
    }

    public corpoCeleste(String nome, double focoX, double focoY, int raio, Color color, Orbita orbita) {
        this.nome = nome;
        this.focoX = focoX;
        this.focoY = focoY;
        this.raio = raio;
        this.color = color;
        this.orbita = orbita;
        updatePosition();
    }

    public void step() {
        if (temOrbita()){
            orbita.update(x, y);
            updatePosition();
        }
    }

    private void updatePosition(){
        if (temOrbita()){
            x = orbita.positionX();
            y = orbita.positionY();
        } else {
            x = focoX;
            y = focoY;
        }
    }

    public boolean temOrbita() {
        return orbita != null;
    }

    public Orbita getOrbita() {
        return orbita;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public String getNome(){
        return nome;
    }

    public int getRaio(){
        return raio;
    }

    public Color getColor(){
        return color;
    }

    public void draw(Graphics2D d) {
        d.setColor(color.brighter());
        d.fillOval(
            (int)(x - raio) - 1,
            (int) (y - raio) - 1,
            (raio + 1) * 2,
            (raio + 1) * 2
        );

        d.setColor(color);
        d.fillOval((int) (x - raio), (int) (y - raio), raio * 2, raio * 2);

        d.setColor(Color.WHITE);
        d.setFont(new Font("Arial", Font.PLAIN, 10));
        d.drawString(nome, (int) x + raio + 3, (int) y + 4);
        }
}
