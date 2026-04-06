import java.awt.*;

public class corpoCeleste {

    final String nome;
    final double focoX, focoY;
    final double semieixoX;
    final double semieixoY;
    final double c;
    final double velAngular;
    final int raio;
    final Color color;
    private double anguloatual;
    double x, y;

    public corpoCeleste(
        String nome,
        double focoX,
        double focoY,
        int raio,
        Color color,
        double semieixoX,
        double semieixoY,
        double velAngular
    ) {
        this.nome = nome;
        this.focoX = focoX;
        this.focoY = focoY;
        this.raio = raio;
        this.color = color;
        this.c = Math.sqrt(semieixoX * semieixoX - semieixoY * semieixoY);
        this.semieixoX = semieixoX;
        this.semieixoY = semieixoY;
        this.velAngular = velAngular;
        this.anguloatual = Math.random() * 2 * Math.PI;

        atualizarPosicao();
    }

    void step() {
        anguloatual += velocidadeAtual();
        atualizarPosicao();
    }

    private void atualizarPosicao() {
        if (semieixoX == 0 && semieixoY == 0) {
            x = focoX;
            y = focoY;
            return;
        }

        double origemX = focoX + c;
        double origemY = focoY;
        x = origemX + semieixoX * Math.cos(anguloatual);
        y = origemY + semieixoY * Math.sin(anguloatual);
    }

    private double velocidadeAtual() {
        double dx = x - focoX;
        double dy = y - focoY;
        double distancia = Math.sqrt(dx * dx + dy * dy);

        return velAngular * (semieixoX / distancia);
    }

    void desenhar(Graphics2D d) {
        d.setColor(color.brighter());
        d.fillOval(
            (int) (x - raio) - 1,
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

/*ao longo do projeto criar os métodos para os corpocelestes como:
- mostrarInformacoes()
- mudarVelocidade()
- calcularForcaGravitacional()

- criar outras subclasses também (estrela, meteoroides, cometa, satelites)
*/
