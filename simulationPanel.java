import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import body.*;
import human.*;


public class simulationPanel extends JPanel {
//instanciando uma lista dos corpos do sistema
    private final List<corpoCeleste> corpos = new ArrayList<>();
    private final List<Estrela> estrelas = new ArrayList<>();
    private final List<Planetas> planetasCarregados = new ArrayList<>();
    private final List<ConfigFoguete> foguetesConfigurados = new ArrayList<>();
//instanciando objetos auxiliares e sol
    private corpoCeleste corpoSobMouse = null;
    private corpoCeleste sol;
    private boolean sistemaInicializado = false;
    private int contadorFoguete = 0;
//criação da classe configfoguete para parametrizar tempo dos foguetes
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
//gera o cenario
    public simulationPanel() {
        setBackground(Color.BLACK);
        setupMouseListener();
        setupResizeListener();
    }
//inicia o sistema, gera as estrelas e carrega o arquivo texto dos planetas e foguetes
    public void initSystem() {
        if (getWidth() == 0 || getHeight() == 0) return;
        if (sistemaInicializado) return;

        sistemaInicializado = true;

        int largura = getWidth();
        int altura = getHeight();
        int cx = largura / 2;
        int cy = altura / 2;

        for (int i = 0; i < 300; i++) {
            estrelas.add(new Estrela(largura, altura));
        }

        try {
            carregarSistema("sistema_solar.txt");
            //exceção caso o nome do arquivo esteja diferente
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,"erro ao carregar sistema_solar.txt:\n\n" + e.getMessage(),"erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        if (sol != null) {
            sol.x = cx;
            sol.y = cy;
        }
//cria uma lista com os planetas carregados do arquivo texto
        List<Planetas> planetasComSatelites = new ArrayList<>();
        for (Planetas p : planetasCarregados) {
            if (p.getRaio() > 7) {
                planetasComSatelites.add(p);
            }
        }
/* planetas com satelites, parametros criados (orbitas para cada planeta etc etc) conforme os atributos do arquivo texto
simplificar a criação dos parametros;
*/
        for (corpoCeleste p : planetasComSatelites) {
            int nums = 1 + (int) (Math.random() * 3);
            for (int j = 0; j < nums; j++) {
                String ns = p.getNome() + "-Lua" + (j + 1);
                int rs = 2 + (int) (Math.random() * 4);
                Color cs = new Color(180, 180, 180);
                double semieixoXs = 15 + (j * 10) + Math.random() * 10;
                double semieixoYs = semieixoXs * (0.7 + Math.random() * 0.3);
                double vs = 0.08 + Math.random() * 0.04;
                Orbita os = new Orbita(p, semieixoXs, semieixoYs, vs);
                Satelite sat = new Satelite(ns, rs, cs, os, Math.random() < 0.3);
                corpos.add(sat);
            }
        }

        long agora = System.currentTimeMillis();
        for (ConfigFoguete config : foguetesConfigurados) {
            lancarFoguete(config, cx, cy);
            config.ultimoLancamento = agora;
        }

        for (int i = 0; i < 5; i++) {
            double ax = Math.random() * largura;
            double ay = Math.random() * altura;
            double vx = (Math.random() - 0.5) * 3;
            double vy = (Math.random() - 0.5) * 3;
            Asteroide ast = new Asteroide("Aster " + (i + 1), ax, ay, 4, Color.GRAY, vx, vy, largura, altura);
            corpos.add(ast);
        }
    }
//ler arquivo
    private void carregarSistema(String caminhoArquivo) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            int linhaNum = 0;

            while ((linha = br.readLine()) != null) {
                linhaNum++;
                linha = linha.trim();

                if (linha.isEmpty() || linha.startsWith("#")) continue;

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
//carregar as estrelas no fundo
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

    private corpoCeleste buscarDestino(String nome) {
        for (Planetas p : planetasCarregados) {
            if (p.getNome().equalsIgnoreCase(nome)) {
                return p;
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

    public void update() {
        if (!sistemaInicializado) return;

        long agora = System.currentTimeMillis();
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        for (ConfigFoguete config : foguetesConfigurados) {
            if (agora - config.ultimoLancamento >= config.intervaloLancamentoMs) {
                lancarFoguete(config, cx, cy);
                config.ultimoLancamento = agora;
            }
        }

        estrelas.forEach(Estrela::step);

        Map<corpoCeleste, Integer> prf = new HashMap<>();
        for (corpoCeleste c : corpos) {
            prf.put(c, calcularprf(c));
        }

        corpos.sort(Comparator.comparingInt(prf::get));
        corpos.forEach(corpoCeleste::step);
        repaint();
    }

    private int calcularprf(corpoCeleste c) {
        if (!c.temOrbita()) return 0;
        return 1 + calcularprf(c.getOrbita().getCentro());
    }

    private void setupResizeListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (!sistemaInicializado) return;
                reposicionarSistema();
            }
        });
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

        estrelas.clear();
        for (int i = 0; i < 300; i++) {
            estrelas.add(new Estrela(largura, altura));
        }

        for (corpoCeleste c : corpos) {
            if (c instanceof Asteroide) {
                ((Asteroide) c).setLimitesTela(largura, altura);
            }
        }
    }

    private void setupMouseListener() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                corpoSobMouse = null;
                for (corpoCeleste c : corpos) {
                    if (c == null) continue;
                    double dx = e.getX() - c.getX();
                    double dy = e.getY() - c.getY();
                    if (Math.sqrt(dx * dx + dy * dy) < c.getRaio() + 5) {
                        corpoSobMouse = c;
                        break;
                    }
                }
            }
        });
    }

    private void drawPainelInfo(Graphics2D g2){
        int altPainel = 160 + (foguetesConfigurados.size() * 15);

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(10, 10, 320, altPainel);
        g2.setColor(new Color(255, 255, 255, 50));
        g2.drawRect(10, 10, 320, altPainel);

        int planetas = 0;
        int satelites = 0;
        int foguetes = 0;
        int asteroides = 0;
        int foguetesEmVoo = 0;
        int foguetesAterrissados = 0;

        for (corpoCeleste c : corpos) {
            if (c instanceof Planetas) planetas++;
            if (c instanceof Satelite) satelites++;
            if (c instanceof Foguete) {
                foguetes++;
                Foguete f = (Foguete) c;
                if (f.isAterrissado()) {
                    foguetesAterrissados++;
                } else {
                    foguetesEmVoo++;
                }
            }
            if (c instanceof Asteroide) asteroides++;
        }

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString("Sistema Solar", 20, 30);

        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString("Sol: 1", 20, 50);
        g2.drawString("Planetas: " + planetas, 20, 65);
        g2.drawString("Satélites: " + satelites, 20, 80);
        g2.drawString("Foguetes: " + foguetes + " (Voo: " + foguetesEmVoo + ", Aterr: " + foguetesAterrissados + ")", 20, 95);
        g2.drawString("Asteroides: " + asteroides, 20, 110);
        g2.drawString("Estrelas: " + estrelas.size(), 20, 125);
        g2.drawString("Configs de foguete: " + foguetesConfigurados.size(), 20, 140);

        int y = 160;
           long agora = System.currentTimeMillis();

           for (ConfigFoguete config : foguetesConfigurados) {
               long tempoRestante = Math.max(0,
                       (config.intervaloLancamentoMs - (agora - config.ultimoLancamento)) / 1000);

               g2.setColor(tempoRestante == 0 ? Color.GREEN : Color.YELLOW);
               g2.drawString(config.nomeBase + " -> " + config.destinoNome + ": " + tempoRestante + "s", 20, y);
               y += 15;
           }
    }

    private void drawTooltip(Graphics2D g2){
        if (corpoSobMouse == null) return;

            int tx = (int) corpoSobMouse.getX() + 25;
            int ty = (int) corpoSobMouse.getY() - 60;

            if (corpoSobMouse instanceof Planetas) {
                Planetas p = (Planetas) corpoSobMouse;
                Nucleo n = p.getNucleo();

                g2.setColor(new Color(0, 0, 0, 220));
                g2.fillRect(tx, ty, 220, 120);
                g2.setColor(Color.WHITE);
                g2.drawRect(tx, ty, 220, 120);

                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString(corpoSobMouse.getNome(), tx + 5, ty + 20);

                g2.setFont(new Font("Arial", Font.PLAIN, 10));
                String[] linhas = n.getDescricao().split("\\n");
                for (int i = 0; i < linhas.length; i++) {
                    g2.drawString(linhas[i], tx + 5, ty + 35 + (i * 15));
                }
            }

            if (corpoSobMouse instanceof Foguete) {
                Foguete f = (Foguete) corpoSobMouse;

                g2.setColor(new Color(0, 0, 0, 220));
                g2.fillRect(tx, ty, 200, 60);
                g2.setColor(Color.WHITE);
                g2.drawRect(tx, ty, 200, 60);

                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString(f.getNome(), tx + 5, ty + 20);

                g2.setFont(new Font("Arial", Font.PLAIN, 10));
                g2.drawString("Destino: " + f.getDestino().getNome(), tx + 5, ty + 35);
                g2.drawString("Status: " + (f.isAterrissado() ? "Aterrissado" : "Em voo"), tx + 5, ty + 50);
            }
        }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        estrelas.forEach(e -> e.draw(g2));

        g2.setColor(new Color(255, 255, 255, 20));
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5, 5}, 0));

        for (corpoCeleste c : corpos) {
            if (c.temOrbita()) {
                Orbita orb = c.getOrbita();
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

        corpos.forEach(c -> c.draw(g2));
        drawPainelInfo(g2);
        drawTooltip(g2);
    }
}
