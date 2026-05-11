import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import body.*;
import human.*;

public class simulationPanel extends JPanel {
    private final List<corpoCeleste> corpos = new ArrayList<>();
    private final List<Estrela> estrelas = new ArrayList<>();
    private corpoCeleste corpoSobMouse = null;
    private corpoCeleste sol;
    private boolean sistemaInicializado = false;

    private int contadorFoguete = 0;
    private long ultimoLancamento = 0;
    private final long INTERVALO_LANCAMENTO = 5000;
    private List<Planetas> planetasCarregados;

    public simulationPanel() {
        setBackground(Color.BLACK);
        setupMouseListener();
        setupResizeListener();
    }

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
            planetasCarregados = new ArrayList<>();
            carregarSistema("planets.txt");

            if (sol != null) {
                sol.x = cx;
                sol.y = cy;
            }
        } catch (IOException e) {
            System.err.println("erro: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Erro ao carregar 'planets.txt'!\n\n" + e.getMessage(),
                "Erro de Configuração",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return;
        }

        List<corpoCeleste> planetasComSatelites = new ArrayList<>();
        for (Planetas p : planetasCarregados) {
            if (p.getRaio() > 7) {
                planetasComSatelites.add(p);
            }
        }

        for (corpoCeleste p : planetasComSatelites) {
            int nums = 1 + (int)(Math.random() * 3);
            for (int j = 0; j < nums; j++) {
                String ns = p.getNome() + "-Lua" + (j + 1);
                int rs = 2 + (int)(Math.random() * 4);
                Color cs = new Color(180, 180, 180);
                double semieixoXs = 15 + (j * 10) + Math.random() * 10;
                double semieixoYs = semieixoXs * (0.7 + Math.random() * 0.3);
                double vs = 0.08 + Math.random() * 0.04;
                Orbita os = new Orbita(p, semieixoXs, semieixoYs, vs);
                Satelite sat = new Satelite(ns, rs, cs, os, Math.random() < 0.3);
                corpos.add(sat);
            }
        }

        lancarFoguete(cx, cy);
        ultimoLancamento = System.currentTimeMillis();

        for (int i = 0; i < 5; i++) {
            double ax = Math.random() * largura;
            double ay = Math.random() * altura;
            double vx = (Math.random() - 0.5) * 3;
            double vy = (Math.random() - 0.5) * 3;
            Asteroide ast = new Asteroide("Aster " + (i + 1), ax, ay, 4, Color.GRAY, vx, vy, largura, altura);
            corpos.add(ast);
        }
    }

    private void carregarSistema(String caminhoArquivo) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo));
        String linha;
        int linhaNum = 0;

        while ((linha = br.readLine()) != null) {
            linhaNum++;
            linha = linha.trim();

            if (linha.isEmpty() || linha.startsWith("#")) continue;

            String[] partes = linha.split(";");
            if (partes.length < 10) {
                System.out.println("Linha " + linhaNum + " ignorada: " + linha);
                continue;
            }

            String tipo = partes[0].trim().toLowerCase();
            String nome = partes[1].trim();
            int raio = Integer.parseInt(partes[4].trim());
            int r = Integer.parseInt(partes[5].trim());
            int g = Integer.parseInt(partes[6].trim());
            int b = Integer.parseInt(partes[7].trim());
            double semieixoX = Double.parseDouble(partes[8].trim());
            double excentricidade = Double.parseDouble(partes[9].trim());
            boolean atmosfera = Boolean.parseBoolean(partes[10].trim());

            Color cor = new Color(r, g, b);

            if (tipo.equals("estrela")) {
                sol = new corpoCeleste(nome, 0, 0, raio, cor);
                corpos.add(sol);
                System.out.println("☀ Estrela carregada: " + nome);
            } else if (tipo.equals("planeta")) {
                if (sol == null) {
                    br.close();
                    throw new IOException("O Sol deve ser definido antes dos planetas!");
                }
                double semieixoY = semieixoX * Math.sqrt(1 - excentricidade * excentricidade);
                double velAngular = 0.003 + (1.5 / semieixoX);

                Nucleo nucleo = Nucleo.criarNucleoAleatorio(raio, semieixoX);
                Orbita orbita = new Orbita(sol, semieixoX, semieixoY, velAngular);
                Planetas planeta = new Planetas(nome, raio, cor, orbita, nucleo, atmosfera);
                corpos.add(planeta);
                planetasCarregados.add(planeta);
                System.out.println("Planeta carregado: " + nome);
            } else {
                System.out.println("Tipo desconhecido na linha " + linhaNum + ": " + tipo);
            }
        }
        br.close();

        if (sol == null) {
            throw new IOException("Nenhuma estrela (Sol) encontrada no arquivo!");
        }

        if (planetasCarregados.isEmpty()) {
            throw new IOException("nenhum planeta encontrado no arquivo");
        }

        System.out.println(corpos.size() + " corpos carregados.");
    }

    private void lancarFoguete(int cx, int cy) {
        if (planetasCarregados == null || planetasCarregados.isEmpty()) return;

        contadorFoguete++;
        corpoCeleste destino = planetasCarregados.get((int)(Math.random() * planetasCarregados.size()));

        double startX = cx + (Math.random() - 0.5) * 200;
        double startY = cy - 50 - Math.random() * 100;
        double velocidade = 1.0 + Math.random() * 1.0;

        Color[] coresFoguete = {Color.WHITE, Color.CYAN, Color.ORANGE, Color.PINK, Color.GREEN};
        Color cor = coresFoguete[(int)(Math.random() * coresFoguete.length)];

        Foguete foguete = new Foguete(
            "Foguete-" + contadorFoguete,
            startX, startY,
            5, cor,
            destino,
            velocidade
        );
        corpos.add(foguete);

        System.out.println("Lançado: " + foguete.getNome() + " → " + destino.getNome());
    }

    private void setupResizeListener() {
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
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
                Asteroide a = (Asteroide) c;
                a.setLimitesTela(largura, altura);
            }
        }
    }

    public void update() {
        if (!sistemaInicializado) return;

        long agora = System.currentTimeMillis();

        if (agora - ultimoLancamento > INTERVALO_LANCAMENTO) {
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            lancarFoguete(cx, cy);
            ultimoLancamento = agora;
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

    private void setupMouseListener() {
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        estrelas.forEach(e -> e.draw(g2));

        g2.setColor(new Color(255, 255, 255, 20));
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                                     0, new float[]{5, 5}, 0));

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

        desenharPainelInfo(g2);
        desenharTooltip(g2);
    }

    private void desenharPainelInfo(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(10, 10, 250, 140);
        g2.setColor(new Color(255, 255, 255, 50));
        g2.drawRect(10, 10, 250, 140);

        int planetas = 0, satelites = 0, foguetes = 0, asteroides = 0;
        int foguetesEmVoo = 0, foguetesAterrissados = 0;

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
        g2.drawString("Foguetes: " + foguetes + " (Voo: " + foguetesEmVoo +
                     ", Aterr: " + foguetesAterrissados + ")", 20, 95);
        g2.drawString("Asteroides: " + asteroides, 20, 110);
        g2.drawString("Estrelas: " + estrelas.size(), 20, 125);

        long agora = System.currentTimeMillis();
        long tempoRestante = (INTERVALO_LANCAMENTO - (agora - ultimoLancamento)) / 1000;
        if (tempoRestante < 0) tempoRestante = 0;

        g2.setColor(tempoRestante == 0 ? Color.GREEN : Color.YELLOW);
        g2.drawString("Próximo foguete: " + tempoRestante + "s", 20, 140);
    }

    private void desenharTooltip(Graphics2D g2) {
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
            String[] linhas = n.getDescricao().split("\n");
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
}
