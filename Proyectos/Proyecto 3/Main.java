import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.ArrayList;

public class Main extends JFrame {
    private final JPanel panel;
    private final ExecutorService threadPool;
    private final List<Asteroide> asteroides;
    private final ExecutorService drawingThread;

    public Main(int numAsteroides) {
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(Color.BLACK);
                synchronized (asteroides) {
                    for (Asteroide asteroide : asteroides) {
                        asteroide.dibujar(g);
                    }
                }
            }
        };

        threadPool = Executors.newFixedThreadPool(numAsteroides);
        drawingThread = Executors.newSingleThreadExecutor();
        asteroides = new ArrayList<>();

        setTitle("Asteroides");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(panel);
        setVisible(true);
    }

    private void crearAsteroides(int numAsteroides) {
        for (int i = 0; i < numAsteroides; i++) {
            Asteroide asteroide = new Asteroide(panel);
            synchronized (asteroides) {
                asteroides.add(asteroide);
            }
            threadPool.submit(asteroide);
        }

        // Hilo para manejar colisiones y redibujado
        drawingThread.submit(() -> {
            while (true) {
                synchronized (asteroides) {
                    // Detectar colisiones
                    for (int i = 0; i < asteroides.size(); i++) {
                        for (int j = i + 1; j < asteroides.size(); j++) {
                            asteroides.get(i).manejarColision(asteroides.get(j));
                        }
                    }
                }
                panel.repaint();
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: java Main <numero_asteroides>");
            return;
        }
        int numAsteroides = Integer.parseInt(args[0]);
        SwingUtilities.invokeLater(() -> {
            Main app = new Main(numAsteroides);
            app.crearAsteroides(numAsteroides);
        });
    }
}