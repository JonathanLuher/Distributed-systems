import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.Random;
import javax.swing.JPanel;

public class Asteroide extends PoligonoIrreg implements Runnable {
    private Coordenada posicion;
    private double velocidad;
    private int lados;
    private double anguloMovimiento;
    private final Random random = new Random();
    private final JPanel panel;
    private final Color color;
    
    // Parámetros de control
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private static final double MAX_AREA = (SCREEN_WIDTH * SCREEN_HEIGHT) / 8.0;

    public Asteroide(JPanel panel) {
        this.panel = panel;
        this.posicion = new Coordenada(random.nextDouble() * SCREEN_WIDTH, 
                                     random.nextDouble() * SCREEN_HEIGHT);
        
        this.lados = 5 + random.nextInt(8); // 5-12 lados
        
        double radioBase = 10 + random.nextDouble() * 30;
        double area = Math.PI * radioBase * radioBase;
        if (area > MAX_AREA) {
            radioBase = Math.sqrt(MAX_AREA / Math.PI);
        }
        
        for (int i = 0; i < lados; i++) {
            double radio = radioBase * (0.7 + 0.6 * random.nextDouble());
            double angulo = 2 * Math.PI * i / lados;
            double x = posicion.abcisa() + radio * Math.cos(angulo);
            double y = posicion.ordenada() + radio * Math.sin(angulo);
            this.anadeVertice(new Coordenada(x, y));
        }
        
        this.velocidad = 150 / calcularArea();
        this.anguloMovimiento = random.nextDouble() * 2 * Math.PI;
        this.color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public double calcularArea() {
        double area = 0.0;
        int numVertices = getNumVertices();
        for (int i = 0; i < numVertices; i++) {
            Coordenada actual = getVertice(i);
            Coordenada siguiente = getVertice((i + 1) % numVertices);
            area += actual.abcisa() * siguiente.ordenada() - siguiente.abcisa() * actual.ordenada();
        }
        return Math.abs(area) / 2.0;
    }

    public void run() {
        while (true) {
            double nuevaX = posicion.abcisa() + Math.cos(anguloMovimiento) * velocidad;
            double nuevaY = posicion.ordenada() + Math.sin(anguloMovimiento) * velocidad;

            if (nuevaX < 0) nuevaX = SCREEN_WIDTH;
            if (nuevaX > SCREEN_WIDTH) nuevaX = 0;
            if (nuevaY < 0) nuevaY = SCREEN_HEIGHT;
            if (nuevaY > SCREEN_HEIGHT) nuevaY = 0;

            double desplazamientoX = nuevaX - posicion.abcisa();
            double desplazamientoY = nuevaY - posicion.ordenada();
            posicion = new Coordenada(nuevaX, nuevaY);
            
            // Actualizar vértices
            for (int i = 0; i < getNumVertices(); i++) {
                Coordenada vertice = getVertice(i);
                double x = vertice.abcisa() + desplazamientoX;
                double y = vertice.ordenada() + desplazamientoY;
                setVertice(i, new Coordenada(x, y));
            }
            
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void dibujar(Graphics g) {
        int numVertices = getNumVertices();
        int[] xPoints = new int[numVertices];
        int[] yPoints = new int[numVertices];
        
        for (int i = 0; i < numVertices; i++) {
            Coordenada vertice = getVertice(i);
            xPoints[i] = (int) vertice.abcisa();
            yPoints[i] = (int) vertice.ordenada();
        }
        
        g.setColor(color);
        g.fillPolygon(xPoints, yPoints, numVertices);
    }

    public void manejarColision(Asteroide otro) {
        double distancia = Math.sqrt(
            Math.pow(this.posicion.abcisa() - otro.posicion.abcisa(), 2) +
            Math.pow(this.posicion.ordenada() - otro.posicion.ordenada(), 2)
        );
        
        double sumaRadios = Math.sqrt(this.calcularArea() / Math.PI) + 
                          Math.sqrt(otro.calcularArea() / Math.PI);
        
        if (distancia < sumaRadios) {
            // Reducir tamaño (aproximadamente a la mitad del área)
            for (int i = 0; i < this.getNumVertices(); i++) {
                Coordenada vertice = this.getVertice(i);
                double x = posicion.abcisa() + (vertice.abcisa() - posicion.abcisa()) * 0.7;
                double y = posicion.ordenada() + (vertice.ordenada() - posicion.ordenada()) * 0.7;
                this.setVertice(i, new Coordenada(x, y));
            }
            
            for (int i = 0; i < otro.getNumVertices(); i++) {
                Coordenada vertice = otro.getVertice(i);
                double x = otro.posicion.abcisa() + (vertice.abcisa() - otro.posicion.abcisa()) * 0.7;
                double y = otro.posicion.ordenada() + (vertice.ordenada() - otro.posicion.ordenada()) * 0.7;
                otro.setVertice(i, new Coordenada(x, y));
            }
            
            this.velocidad = 150 / this.calcularArea();
            otro.velocidad = 150 / otro.calcularArea();
        }
    }
}