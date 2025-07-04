//PROYECTO 2 - SISTEMAS DISTRIBUIDOS 7CM3
//LUCIANO HERNANDEZ JONATHAN

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleGui2 extends JFrame {
    public static void main(String[] args) {
        // Verificar que se hayan proporcionado los parámetros necesarios
        if (args.length < 2) {
            System.out.println("Uso: java SimpleGui2 <numPerseguidores> <velocidadPerseguidores>");
            return;
        }

        int numPerseguidores = Integer.parseInt(args[0]); // Número de perseguidores
        double velocidadPerseguidores = Double.parseDouble(args[1]); // Velocidad de los perseguidores

        // Validar el número máximo de perseguidores
        if (numPerseguidores < 1 || numPerseguidores > 7) {
            System.out.println("El número de perseguidores debe estar entre 1 y 7.");
            return;
        }

        // Iniciar la aplicación
        SwingUtilities.invokeLater(() -> new SimpleGui2(numPerseguidores, velocidadPerseguidores));
    }

    private final int numPerseguidores;
    private final double velocidadPerseguidores;

    public SimpleGui2(int numPerseguidores, double velocidadPerseguidores) {
        this.numPerseguidores = numPerseguidores;
        this.velocidadPerseguidores = velocidadPerseguidores;

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        Panel panel = new Panel(numPerseguidores, velocidadPerseguidores);
        add(panel);
        setVisible(true);
        panel.startAnimation();
    }

    private static class Panel extends JPanel {
        private int x1, y1; // Posición del primer píxel
        private int targetX, targetY; // Objetivo del primer píxel
        private final int pixelSize = 10;
        private final Random random = new Random();
        private double speed1 = 2.0; // Velocidad del primer píxel
        private final List<PixelPerseguidor> perseguidores = new ArrayList<>();
        private final double smoothness = 0.05; // Suavidad del cambio de dirección
        private BufferedImage trailImage; // Imagen para almacenar el rastro
        private boolean programaTerminado = false;
        private double directionX1 = 0, directionY1 = 0;
        private long tiempoInicio;

        public Panel(int numPerseguidores, double velocidadPerseguidores) {
            x1 = random.nextInt(800 - pixelSize);
            y1 = random.nextInt(600 - pixelSize);
            targetX = x1;
            targetY = y1;

            // Inicializar la imagen del rastro
            trailImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = trailImage.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, 800, 600);
            g2d.dispose();

            // Crear los píxeles perseguidores
            for (int i = 0; i < numPerseguidores; i++) {
                perseguidores.add(new PixelPerseguidor(random.nextInt(800 - pixelSize), random.nextInt(600 - pixelSize), velocidadPerseguidores, Color.getHSBColor((float) i / numPerseguidores, 1.0f, 1.0f)));
            }

            tiempoInicio = System.currentTimeMillis();
        }

        public void startAnimation() {
            Timer timer = new Timer(30, e -> {
                if (programaTerminado) {
                    return;
                }

                // Verificar si ha pasado el tiempo límite
                if (System.currentTimeMillis() - tiempoInicio >= 120000) {
                    programaTerminado = true;
                    mostrarMensaje("Ningún perseguidor alcanzó al píxel original en 2 minutos.");
                    ((Timer) e.getSource()).stop(); // Detener el Timer
                    return;
                }

                if (Math.abs(x1 - targetX) < pixelSize && Math.abs(y1 - targetY) < pixelSize) {
                    setNewTarget();
                }
                movePixel1(); // Mover el primer píxel
                for (PixelPerseguidor perseguidor : perseguidores) {
                    perseguidor.moverHacia(x1, y1); // Mover cada perseguidor hacia el primer píxel
                    perseguidor.dibujarRastro(trailImage);

                    // Verificar colisión con el primer píxel
                    if (colisiona(x1, y1, perseguidor.getX(), perseguidor.getY())) {
                        programaTerminado = true;
                        mostrarMensaje("Programa terminado: Un perseguidor tocó al primer píxel.");
                        ((Timer) e.getSource()).stop();
                    }
                }
                repaint();
            });
            timer.start();
        }

        private void setNewTarget() {
            targetX = random.nextInt(800 - pixelSize);
            targetY = random.nextInt(600 - pixelSize);
        }

        private void movePixel1() {
            // Calcular la dirección hacia el objetivo
            double targetDirectionX = targetX - x1;
            double targetDirectionY = targetY - y1;

            // Normalizar la dirección hacia el objetivo
            double length = Math.sqrt(targetDirectionX * targetDirectionX + targetDirectionY * targetDirectionY);
            if (length > 0) {
                targetDirectionX /= length;
                targetDirectionY /= length;
            }

            // Suavizar el cambio de dirección del primer píxel
            directionX1 += (targetDirectionX - directionX1) * smoothness;
            directionY1 += (targetDirectionY - directionY1) * smoothness;

            // Normalizar la dirección suavizada
            length = Math.sqrt(directionX1 * directionX1 + directionY1 * directionY1);
            if (length > 0) {
                directionX1 /= length;
                directionY1 /= length;
            }

            // Mover el primer píxel en la dirección suavizada
            x1 += directionX1 * speed1;
            y1 += directionY1 * speed1;

            // Asegurarse de que no se salga de los límites
            x1 = Math.max(0, Math.min(800 - pixelSize, x1));
            y1 = Math.max(0, Math.min(600 - pixelSize, y1));

            // Dibujar el rastro del primer píxel en la imagen
            Graphics2D g2d = trailImage.createGraphics();
            g2d.setColor(Color.GRAY); // Color del rastro del primer píxel
            g2d.fillRect(x1, y1, pixelSize, pixelSize);
            g2d.dispose();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Dibujar la imagen del rastro
            g.drawImage(trailImage, 0, 0, this);

            // Dibujar el primer píxel
            g.setColor(Color.BLACK);
            g.fillRect(x1, y1, pixelSize, pixelSize);

            // Dibujar los píxeles perseguidores
            for (PixelPerseguidor perseguidor : perseguidores) {
                g.setColor(perseguidor.getColor());
                g.fillRect(perseguidor.getX(), perseguidor.getY(), pixelSize, pixelSize);
            }
        }

        // Método para verificar colisión entre dos píxeles
        private boolean colisiona(int x1, int y1, int x2, int y2) {
            return Math.abs(x1 - x2) < pixelSize && Math.abs(y1 - y2) < pixelSize;
        }

        // Método para mostrar un mensaje en una ventana emergente
        private void mostrarMensaje(String mensaje) {
            JOptionPane.showMessageDialog(this, mensaje, "Fin del programa", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Clase para representar un píxel perseguidor
    private static class PixelPerseguidor {
        private int x, y;
        private double directionX = 0, directionY = 0;
        private final double speed;
        private final double smoothness = 0.05;
        private final Color color;

        public PixelPerseguidor(int x, int y, double speed, Color color) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.color = color;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Color getColor() {
            return color;
        }

        public void moverHacia(int targetX, int targetY) {
            // Calcular la dirección hacia el objetivo
            double targetDirectionX = targetX - x;
            double targetDirectionY = targetY - y;

            // Normalizar la dirección hacia el objetivo
            double length = Math.sqrt(targetDirectionX * targetDirectionX + targetDirectionY * targetDirectionY);
            if (length > 0) {
                targetDirectionX /= length;
                targetDirectionY /= length;
            }

            // Suavizar el cambio de dirección
            directionX += (targetDirectionX - directionX) * smoothness;
            directionY += (targetDirectionY - directionY) * smoothness;

            // Normalizar la dirección suavizada
            length = Math.sqrt(directionX * directionX + directionY * directionY);
            if (length > 0) {
                directionX /= length;
                directionY /= length;
            }

            // Mover el píxel en la dirección suavizada
            x += directionX * speed;
            y += directionY * speed;

            // Asegurarse de que no se salga de los límites
            x = Math.max(0, Math.min(800 - 10, x));
            y = Math.max(0, Math.min(600 - 10, y));
        }

        // Método para dibujar el rastro del perseguidor
        public void dibujarRastro(BufferedImage trailImage) {
            Graphics2D g2d = trailImage.createGraphics();
            g2d.setColor(color); // Usar el color del perseguidor
            g2d.fillRect(x, y, 10, 10); // Dibujar el rastro
            g2d.dispose();
        }
    }
}