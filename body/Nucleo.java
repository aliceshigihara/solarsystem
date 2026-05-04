package body;

public class Nucleo {
    private double temperatura;      // temperatura em Kelvin
    private boolean estadoSolido;    // true = sólido, false = líquido
    private String composicao;       // composição principal
    private double densidade;        // densidade em g/cm³
    private double raioNucleo;       // raio do núcleo em km
    private boolean temCampoMagnetico; // se possui campo magnético

    public Nucleo(double temperatura, boolean estadoSolido, String composicao,
                  double densidade, double raioNucleo, boolean temCampoMagnetico) {
        this.temperatura = temperatura;
        this.estadoSolido = estadoSolido;
        this.composicao = composicao;
        this.densidade = densidade;
        this.raioNucleo = raioNucleo;
        this.temCampoMagnetico = temCampoMagnetico;
    }

    public Nucleo(double temperatura, boolean estadoSolido, String composicao) {
        this(temperatura, estadoSolido, composicao,
             5.0 + Math.random() * 10,
             1000 + Math.random() * 3000,
             Math.random() < 0.5);
    }

    public static Nucleo criarNucleoRochoso() {
        return new Nucleo(
            3000 + Math.random() * 2000, true, "Ferro e Níquel", 8.0 + Math.random() * 4, 1500 + Math.random() * 2000, Math.random() < 0.3);
    }

    public static Nucleo criarNucleoGasoso() {
        return new Nucleo(
            5000 + Math.random() * 3000, false, "Hidrogênio Metálico", 2.0 + Math.random() * 3, 5000 + Math.random() * 10000, Math.random() < 0.7);
    }

    public double getTemperatura() {
        return temperatura;
    }

    public boolean isEstadoSolido() {
        return estadoSolido;
    }

    public String getComposicao() {
        return composicao;
    }

    public double getDensidade() {
        return densidade;
    }

    public double getRaioNucleo() {
        return raioNucleo;
    }

    public boolean isTemCampoMagnetico() {
        return temCampoMagnetico;
    }

    public String getEstadoFisico() {
        return estadoSolido ? "Sólido" : "Líquido";
    }

    public String getDescricao() {
        StringBuilder desc = new StringBuilder();
        desc.append("Núcleo: ").append(composicao).append("\n");
        desc.append("Estado: ").append(getEstadoFisico()).append("\n");
        desc.append("Temperatura: ").append(String.format("%.0f", temperatura)).append(" K\n");
        desc.append("Densidade: ").append(String.format("%.1f", densidade)).append(" g/cm³\n");
        desc.append("Raio: ").append(String.format("%.0f", raioNucleo)).append(" km\n");
        desc.append("Campo Magnético: ").append(temCampoMagnetico ? "Sim" : "Não");
        return desc.toString();
    }

    public double getAtividadeGeologica() {
        // Retorna um valor de 0 a 1 indicando atividade geológica
        double atividade = 0;

        // Núcleos líquidos são mais ativos
        if (!estadoSolido) {
            atividade += 0.4;
        }

        // Temperatura alta aumenta atividade
        if (temperatura > 4000) {
            atividade += 0.3;
        }

        // Campo magnético indica atividade
        if (temCampoMagnetico) {
            atividade += 0.3;
        }

        return Math.min(atividade, 1.0);
    }

    public static Nucleo criarNucleoAleatorio(int raioPlaneta, double distanciaDoSol) {
        double temperatura;
        boolean estadoSolido;
        String composicao;
        double densidade;
        double raioNucleo;
        boolean temCampoMagnetico;

        if (raioPlaneta <= 8 || distanciaDoSol < 200) {
            temperatura = 2000 + Math.random() * 3000;
            estadoSolido = Math.random() < 0.8;

            String[] composicoes = {
                "Ferro e Níquel",
                "Ferro e Enxofre",
                "Níquel e Silicatos",
                "Ferro fundido com Enxofre",
                "Ferro-Níquel com Olivina"
            };
            composicao = composicoes[(int)(Math.random() * composicoes.length)];
            densidade = 6.0 + Math.random() * 6.0;
            raioNucleo = raioPlaneta * (0.4 + Math.random() * 0.3);
            temCampoMagnetico = !estadoSolido && Math.random() < 0.7;
        }
        else if (raioPlaneta > 10) {
            temperatura = 5000 + Math.random() * 10000;
            estadoSolido = Math.random() < 0.2;

            String[] composicoes = {
                "Hidrogênio Metálico",
                "Hidrogênio e Hélio Líquido",
                "Gelo e Rocha",
                "Hidrogênio Metálico com Gelo"
            };
            composicao = composicoes[(int)(Math.random() * composicoes.length)];
            densidade = 1.0 + Math.random() * 4.0;
            raioNucleo = raioPlaneta * (0.2 + Math.random() * 0.3);
            temCampoMagnetico = Math.random() < 0.9;
        }
        else {
            temperatura = 3000 + Math.random() * 4000;
            estadoSolido = Math.random() < 0.5;

            String[] composicoes = {
                "Gelo e Rocha",
                "Água e Amônia",
                "Metano e Gelo",
                "Rocha e Gelo"
            };
            composicao = composicoes[(int)(Math.random() * composicoes.length)];
            densidade = 3.0 + Math.random() * 5.0;
            raioNucleo = raioPlaneta * (0.3 + Math.random() * 0.4);
            temCampoMagnetico = Math.random() < 0.6;
        }

        return new Nucleo(temperatura, estadoSolido, composicao, densidade, raioNucleo, temCampoMagnetico);
    }

    @Override
    public String toString() {
        return String.format("Núcleo de %s a %.0f K", composicao, temperatura);
    }
}
