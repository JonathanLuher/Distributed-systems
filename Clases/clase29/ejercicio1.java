import java.util.*;

public class ejercicio1 {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Uso: java ejercicio1 <porcentajeCPU> <tiempoSegundos>");
            return;
        }

        int porcentajeCPU = Integer.parseInt(args[0]);
        int tiempoSegundos = Integer.parseInt(args[1]);

        if (porcentajeCPU < 1 || porcentajeCPU > 100) {
            System.out.println("Error: El porcentaje de CPU debe estar entre 1 y 100.");
            return;
        }

        if (tiempoSegundos < 1) {
            System.out.println("Error: El tiempo debe ser mayor a 0 segundos.");
            return;
        }

        System.out.println("Ejecutando con " + porcentajeCPU + "% de uso de CPU durante " + tiempoSegundos + " segundos.");
        
        Random ran = new Random();

        // Tiempo total de ejecución en milisegundos
        long tiempoTotalMs = tiempoSegundos * 1000L;

        long tiempoInicio = System.currentTimeMillis();

        while (System.currentTimeMillis() - tiempoInicio < tiempoTotalMs) {
            List<Boolean> intervaloCarga = new ArrayList<>(Collections.nCopies(1000, Boolean.FALSE));

            int milisegundosCarga = porcentajeCPU * 10;
            int count = 0;

            while (count < milisegundosCarga) {
                int index = ran.nextInt(1000);
                if (!intervaloCarga.get(index)) {
                    intervaloCarga.set(index, true);
                    count++;
                }
            }

            // Ejecutamos cada uno de los 1000 milisegundos
            for (int i = 0; i < 1000; i++) {
                long msInicio = System.currentTimeMillis();
                if (intervaloCarga.get(i)) {
                    // Carga de CPU 
                    while (System.currentTimeMillis() - msInicio < 1) {
                        Math.sqrt(ran.nextInt(2147483647));
                    }
                } else {
                    // Reposo
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        System.out.println("Finalizado.");
    }
}
