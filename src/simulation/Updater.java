package simulation;
import body.Estrela;
import body.corpoCeleste;

public class Updater {

    private final simulationPanel panel;

    public Updater(simulationPanel panel) {
        this.panel = panel;
    }

    public void update() {
        if (!panel.isSistemaInicializado()) {
            return;
        }

        long agora = System.currentTimeMillis();
        int cx = panel.getWidth() / 2;
        int cy = panel.getHeight() / 2;

        panel.atualizarLancamentoFoguetes(agora, cx, cy);

        if (panel.isEstrelasAtivas()) {
            for (Estrela estrela : panel.getEstrelas()) {
                estrela.step();
            }
        }

        panel.ordenarCorposPorProfundidade();

        for (corpoCeleste corpo : panel.getCorpos()) {
            corpo.step();
        }

        panel.repaint();
    }
}
