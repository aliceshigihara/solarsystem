package simulation;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import body.*;
import human.Foguete;

public class simulationPanel extends JPanel {

    private static final int QUANTIDADE_ESTRELAS = 300;
    private static final int QUANTIDADE_ASTEROIDES = 5;

    private final List<corpoCeleste> corpos = new ArrayList<>();
    private final List<Estrela> estrelas = new ArrayList<>();
    private final List<Planetas> planetasCarregados = new ArrayList<>();
    private final List<ConfigFoguete> foguetesConfigurados = new ArrayList<>();

    private final Updater updater;

    private corpoCeleste corpoSobMouse = null;
    private corpoCeleste sol;
    private boolean sistemaInicializado = false;
    private boolean estrelasAtivas = true;
    private int contadorFoguete = 0;

    private static class ConfigFoguete {
        String nomeBase;
        int raio;
        Color cor;
        String destinoNome;
        double velocidade;
        long intervaloLancamentoMs;
        long ultimoLancamento = 0;

        ConfigFoguete(String nomeBase, int raio, Color cor, String destinoNome,
                      double velocidade, long intervaloLancamentoMs) {
            this.nomeBase = nomeBase;
            this.raio = raio;
            this.cor = cor;
            this.destinoNome = destinoNome;
            this.velocidade = velocidade;
            this.intervaloLancamentoMs = intervaloLancamentoMs;
        }
    }

    public simulationPanel() {
        setBackground(Color.BLACK);
        new simulationInput(this).install();
        this.updater = new Updater(this);
    }

    public void initSystem() {
        if (getWidth() == 0 || getHeight() == 0 || sistemaInicializado) {
            return;
        }

        sistemaInicializado = true;

        int largura = getWidth();
        int altura = getHeight();
        int cx = largura / 2;
        int cy = altura / 2;

        gerarEstrelas(largura, altura);

        try {
            carregarSistema("src/resources/sistema_solar.txt");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "erro ao carregar sistema_solar.txt:\n\n" + e.getMessage(),
                    "erro",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
            return;
        }

        if (sol != null) {
            sol.x = cx;
            sol.y = cy;
        }

        criarSatelites();
        lancarFoguetesIniciais(cx, cy);
        criarAsteroides(largura, altura);
    }

    public void update() {
        updater.update();
    }

    public boolean isSistemaInicializado() {
        return sistemaInicializado;
    }

    public boolean isEstrelasAtivas() {
        return estrelasAtivas;
    }

    public List<corpoCeleste> getCorpos() {
        return corpos;
    }

    public List<Estrela> getEstrelas() {
        return estrelas;
    }

    public void alternarEstrelas() {
        estrelasAtivas = !estrelasAtivas;
        repaint();
    }

    public void atualizarCorpoSobMouse(int mouseX, int mouseY) {
        corpoSobMouse = null;

        for (corpoCeleste corpo : corpos) {
            if (corpo == null) {
                continue;
            }

            double dx = mouseX - corpo.getX();
            double dy = mouseY - corpo.getY();

            if (Math.sqrt(dx * dx + dy * dy) < corpo.getRaio() + 5) {
                corpoSobMouse = corpo;
                break;
            }
        }
    }

    public void reposicionarSistemaSeNecessario() {
        if (!sistemaInicializado) {
            return;
        }
        reposicionarSistema();
    }

    public void atualizarLancamentoFoguetes(long agora, int cx, int cy) {
        for (ConfigFoguete config : foguetesConfigurados) {
            if (agora - config.ultimoLancamento >= config.intervaloLancamentoMs) {
                lancarFoguete(config, cx, cy);
                config.ultimoLancamento = agora;
            }
        }
    }

    public void ordenarCorposPorProfundidade() {
        Map<corpoCeleste, Integer> profundidades = new HashMap<>();

        for (corpoCeleste corpo : corpos) {
            profundidades.put(corpo, calcularProfundidade(corpo));
        }

        corpos.sort(Comparator.comparingInt(profundidades::get));
    }

    private void carregarSistema(String caminhoArquivo) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            int linhaNum = 0;

            while ((linha = br.readLine()) != null) {
                linhaNum++;
                linha = linha.trim();

                if (linha.isEmpty() || linha.startsWith("#")) {
                    continue;
                }

                String[] partes = linha.split(";");
                String tipo = partes[0].trim().toLowerCase();

                switch (tipo) {
                    case "estrela":
                        carregarEstrela(partes, linhaNum);
                        break;
                    case "planeta":
                        carregarPlaneta(partes, linhaNum);
                        break;
                    case "foguete":
                        carregarFoguete(partes, linhaNum);
                        break;
                    default:
                        throw new IOException("Tipo desconhecido na linha " + linhaNum + ": " + tipo);
                }
            }
        }

        if (sol == null) {
            throw new IOException("Nenhuma estrela encontrada.");
        }

        if (planetasCarregados.isEmpty()) {
            throw new IOException("Nenhum planeta carregado.");
        }
    }

    private void carregarEstrela(String[] partes, int linhaNum) throws IOException {
        if (partes.length < 6) {
            throw new IOException("Linha " + linhaNum + " inválida para estrela.");
        }

        String nome = partes[1].trim();
        int raio = Integer.parseInt(partes[2].trim());
        int r = Integer.parseInt(partes[3].trim());
        int g = Integer.parseInt(partes[4].trim());
        int b = Integer.parseInt(partes[5].trim());

        sol = new corpoCeleste(nome, 0, 0, raio, new Color(r, g, b));
        corpos.add(sol);
    }

    private void carregarPlaneta(String[] partes, int linhaNum) throws IOException {
        if (partes.length < 9) {
            throw new IOException("Linha " + linhaNum + " inválida para planeta.");
        }

        if (sol == null) {
            throw new IOException("O Sol deve vir antes dos planetas.");
        }

        String nome = partes[1].trim();
        int raio = Integer.parseInt(partes[2].trim());
        int r = Integer.parseInt(partes[3].trim());
        int g = Integer.parseInt(partes[4].trim());
        int b = Integer.parseInt(partes[5].trim());
        double semieixoX = Double.parseDouble(partes[6].trim());
        double excentricidade = Double.parseDouble(partes[7].trim());
        boolean atmosfera = Boolean.parseBoolean(partes[8].trim());

        double semieixoY = semieixoX * Math.sqrt(1 - excentricidade * excentricidade);
        double velAngular = 0.003 + (1.5 / semieixoX);

        Nucleo nucleo = Nucleo.criarNucleoAleatorio(raio, semieixoX);
        Orbita orbita = new Orbita(sol, semieixoX, semieixoY, velAngular);
        Planetas planeta = new Planetas(nome, raio, new Color(r, g, b), orbita, nucleo, atmosfera);

        corpos.add(planeta);
        planetasCarregados.add(planeta);
    }

    private void carregarFoguete(String[] partes, int linhaNum) throws IOException {
        if (partes.length < 9) {
            throw new IOException("Linha " + linhaNum + " inválida para foguete.");
        }

        String nome = partes[1].trim();
        int raio = Integer.parseInt(partes[2].trim());
        int r = Integer.parseInt(partes[3].trim());
        int g = Integer.parseInt(partes[4].trim());
        int b = Integer.parseInt(partes[5].trim());
        String destino = partes[6].trim();
        double velocidade = Double.parseDouble(partes[7].trim());
        long intervalo = Long.parseLong(partes[8].trim());

        foguetesConfigurados.add(
                new ConfigFoguete(nome, raio, new Color(r, g, b), destino, velocidade, intervalo)
        );
    }

    private void gerarEstrelas(int largura, int altura) {
        estrelas.clear();
        for (int i = 0; i < QUANTIDADE_ESTRELAS; i++) {
            estrelas.add(new Estrela(largura, altura));
        }
    }

    private void criarSatelites() {
        List<Planetas> planetasComSatelites = new ArrayList<>();

        for (Planetas planeta : planetasCarregados) {
            if (planeta.getRaio() > 7) {
                planetasComSatelites.add(planeta);
            }
        }

        for (Planetas planeta : planetasComSatelites) {
            int quantidade = 1 + (int) (Math.random() * 3);

            for (int j = 0; j < quantidade; j++) {
                String nomeSatelite = planeta.getNome() + "-Lua" + (j + 1);
                int raioSatelite = 2 + (int) (Math.random() * 4);
                Color corSatelite = new Color(180, 180, 180);
                double semieixoX = 15 + (j * 10) + Math.random() * 10;
                double semieixoY = semieixoX * (0.7 + Math.random() * 0.3);
                double velocidadeAngular = 0.08 + Math.random() * 0.04;

                Orbita orbita = new Orbita(planeta, semieixoX, semieixoY, velocidadeAngular);
                Satelite satelite = new Satelite(
                        nomeSatelite,
                        raioSatelite,
                        corSatelite,
                        orbita,
                        Math.random() < 0.3
                );

                corpos.add(satelite);
            }
        }
    }

    private void lancarFoguetesIniciais(int cx, int cy) {
        long agora = System.currentTimeMillis();

        for (ConfigFoguete config : foguetesConfigurados) {
            lancarFoguete(config, cx, cy);
            config.ultimoLancamento = agora;
        }
    }

    private void criarAsteroides(int largura, int altura) {
        for (int i = 0; i < QUANTIDADE_ASTEROIDES; i++) {
            double ax = Math.random() * largura;
            double ay = Math.random() * altura;
            double vx = (Math.random() - 0.5) * 3;
            double vy = (Math.random() - 0.5) * 3;

            Asteroide ast = new Asteroide(
                    "Aster " + (i + 1),
                    ax,
                    ay,
                    4,
                    Color.GRAY,
                    vx,
                    vy,
                    largura,
                    altura
            );

            corpos.add(ast);
        }
    }

    private corpoCeleste buscarDestino(String nome) {
        for (Planetas planeta : planetasCarregados) {
            if (planeta.getNome().equalsIgnoreCase(nome)) {
                return planeta;
            }
        }
        return null;
    }

    private void lancarFoguete(ConfigFoguete config, int cx, int cy) {
        corpoCeleste destino = buscarDestino(config.destinoNome);

        if (destino == null) {
            System.err.println("Destino não encontrado: " + config.destinoNome);
            return;
        }

        contadorFoguete++;

        double startX = cx + (Math.random() - 0.5) * 200;
        double startY = cy - 50 - Math.random() * 100;

        Foguete foguete = new Foguete(
                config.nomeBase + "-" + contadorFoguete,
                startX,
                startY,
                config.raio,
                config.cor,
                destino,
                config.velocidade
        );

        corpos.add(foguete);
    }

    private int calcularProfundidade(corpoCeleste corpo) {
        if (!corpo.temOrbita()) {
            return 0;
        }
        return 1 + calcularProfundidade(corpo.getOrbita().getCentro());
    }

    private void reposicionarSistema() {
        int largura = getWidth();
        int altura = getHeight();
        int cx = largura / 2;
        int cy = altura / 2;

        if (sol != null) {
            sol.x = cx;
            sol.y = cy;
        }

        gerarEstrelas(largura, altura);

        for (corpoCeleste corpo : corpos) {
            if (corpo instanceof Asteroide) {
                ((Asteroide) corpo).setLimitesTela(largura, altura);
            }
        }
    }

    private List<String> montarInfoFoguetes() {
        List<String> infoFoguetes = new ArrayList<>();
        long agora = System.currentTimeMillis();

        for (ConfigFoguete config : foguetesConfigurados) {
            long tempoRestante = Math.max(
                    0,
                    (config.intervaloLancamentoMs - (agora - config.ultimoLancamento)) / 1000
            );

            infoFoguetes.add(
                    config.nomeBase + " -> " + config.destinoNome + ": " + tempoRestante + "s"
            );
        }

        return infoFoguetes;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (estrelasAtivas) {
            estrelas.forEach(estrela -> estrela.draw(g2));
        }

        desenharOrbitas(g2);
        corpos.forEach(corpo -> corpo.draw(g2));

        drawHUD.drawPainelInfo(g2, corpos, estrelas, montarInfoFoguetes(), estrelasAtivas);
        drawHUD.drawTooltip(g2, corpoSobMouse);
    }

    private void desenharOrbitas(Graphics2D g2) {
        g2.setColor(new Color(255, 255, 255, 20));
        g2.setStroke(new BasicStroke(
                1,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND,
                0,
                new float[]{5, 5},
                0
        ));

        for (corpoCeleste corpo : corpos) {
            if (corpo.temOrbita()) {
                Orbita orb = corpo.getOrbita();
                corpoCeleste centro = orb.getCentro();
                double centroX = centro.getX() + orb.getC();
                double centroY = centro.getY();

                g2.draw(new Ellipse2D.Double(
                        centroX - orb.getSemieixoX(),
                        centroY - orb.getSemieixoY(),
                        orb.getSemieixoX() * 2,
                        orb.getSemieixoY() * 2
                ));
            }
        }
    }
}
