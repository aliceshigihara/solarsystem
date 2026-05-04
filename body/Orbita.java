package body;

public class Orbita {
    private corpoCeleste centro;
    private double semieixoX;
    private double semieixoY;
    private double c;
    private double velAngular;
    private double anguloAtual;

    public Orbita(corpoCeleste centro, double semieixoX, double semieixoY, double velAngular){
        this.centro = centro;
        this.semieixoX = semieixoX;
        this.semieixoY = semieixoY;
        this.velAngular = velAngular;
        this.c = Math.sqrt(Math.abs(semieixoX * semieixoX - semieixoY * semieixoY));
        this.anguloAtual = Math.random() * 2 * Math.PI;
    }

    public double positionX(){
        if (semieixoX == 0 && semieixoX == 0){
            return centro.getX() + c;
        }

        double centroX;
        if (centro.temOrbita()){
            centroX = centro.getX() + c;
        } else {
            centroX = centro.getX() + c;
        }

        return centroX + semieixoX * Math.cos(anguloAtual);
    }

    public double positionY(){
        if (semieixoY == 0 && semieixoY == 0){
            return centro.getY();
        }

        double centroY;
        if (centro.temOrbita()){
            centroY = centro.getY();
        } else {
            centroY = centro.getY();
        }

        return centroY + semieixoY * Math.sin(anguloAtual);
    }

    public void update(double x, double y){
        double dx = x - centro.getX();
        double dy = y - centro.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        double velocidadeAtual = distance > 0 ? velAngular * (semieixoX / distance) : velAngular;

        anguloAtual += velocidadeAtual;
    }

        public corpoCeleste getCentro(){
            return centro;
        }
        public double getSemieixoX(){
            return semieixoX;
        }
        public double getSemieixoY(){
            return semieixoY;
        }
        public double getC(){
            return c;
        }
        public double getAnguloAtual(){
        return anguloAtual;
        }
}
