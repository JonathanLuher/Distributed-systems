import java.util.Random;

public class CpuSimulator {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Uso: java SimulaCargaCpu <porcentaje> <tiempo>");
            return;
        }

        int porcentajeCpu = Integer.parseInt(args[0]);
        int tiempoSegundos = Integer.parseInt(args[1]);

        if (porcentajeCpu < 1 || porcentajeCpu > 100) {
            System.out.println("El porcentaje de CPU debe estar entre 1 y 100.");
            return;
        }

        // Imprimir la información de la simulación
        System.out.println("Simulando un uso de CPU del " + porcentajeCpu + "% durante " + tiempoSegundos + " segundos.");

        long tiempoInicio = System.currentTimeMillis();
        long tiempoFin = tiempoInicio + (tiempoSegundos * 1000);

        // Número de intervalos de 1 milisegundo en un segundo
        int intervalosPorSegundo = 1000;
        // Número de intervalos durante los cuales usaremos el CPU
        int intervalosUsoCpu = (porcentajeCpu * intervalosPorSegundo) / 100;

        Random ran = new Random();

        // Ciclo para simular el uso de la CPU durante el tiempo especificado
        while (System.currentTimeMillis() < tiempoFin) {
            long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;

            // Verificar si el tiempo ha transcurrido más que el tiempo indicado
            if (tiempoTranscurrido >= (tiempoSegundos * 1000)) {
                break;
            }

            // Distribuir aleatoriamente el uso del CPU
            if (tiempoTranscurrido % intervalosPorSegundo < intervalosUsoCpu) {
                // Usar el CPU de manera aleatoria
                Math.sqrt(ran.nextInt(2147483647));
            } else {
                // No usar el CPU, "descansar"
                try {
                    Thread.sleep(1);  // Dormir por 1 milisegundo
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        System.out.println("Simulación terminada.");
    }
}