import java.awt.*;
import main.corpoCeleste;

public class Planetas extends corpoCeleste {

    private Nucleo nucleo;
    private boolean possuiAtmosfera;

    public Planetas(
        String nome,
        double origemX,
        double origemY,
        int raio,
        Color color,
        double raioOrbita,
        double velAngular,
        Nucleo nucleo,
        boolean possuiAtmosfera
    ) {
        super(nome, origemX, origemY, raio, color, raioOrbita, velAngular);
        this.nucleo = nucleo;
        this.possuiAtmosfera = possuiAtmosfera;
    }
}
