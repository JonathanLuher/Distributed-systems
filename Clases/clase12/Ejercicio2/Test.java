import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.*;

class Task implements Runnable {
    private int taskId;
    private ArrayList<String> aleatorios;
    private ArrayList<String> ordenados;
    private int start, end;
    private CountDownLatch latch;
    private CountDownLatch previousLatch;

    // Constructor que recibe el ID de tarea, la lista de CURPs, el rango y los latches
    public Task(int taskId, ArrayList<String> aleatorios, int start, int end, CountDownLatch latch, CountDownLatch previousLatch) {
        this.taskId = taskId;
        this.aleatorios = aleatorios;
        this.start = start;
        this.end = end;
        this.ordenados = new ArrayList<>();
        this.latch = latch;
        this.previousLatch = previousLatch;
    }

    @Override
    public void run() {
        for (int i = start; i < end; i++) {
            insertOrdered(ordenados, aleatorios.get(i));
        }

        // Esperar a que la tarea anterior termine
        if (previousLatch != null) {
            try {
                previousLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //System.out.println("Tarea " + taskId + " ordenada: " + ordenados);
        latch.countDown();
    }

    // Método para insertar una CURP en orden ascendente en la lista
    private void insertOrdered(ArrayList<String> list, String curp) {
        Iterator<String> iterator = list.iterator();
        int index = 0;

        while (iterator.hasNext()) {
            if (curp.substring(0, 4).compareTo(iterator.next().substring(0, 4)) < 0) {
                break;
            }
            index++;
        }
        list.add(index, curp);
    }
}

public class Test {
    //Se define numero de hilos y de tareas
    static final int TASK_COUNT = 5;
    static final int THREAD_COUNT = 5;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java Test <n (múltiplo de 5)>");
            return;
        }
        int n = Integer.parseInt(args[0]);

        if (n % TASK_COUNT != 0) {
            System.out.println("Error: n debe ser múltiplo de " + TASK_COUNT + ".");
            return;
        }
        ArrayList<String> aleatorios = new ArrayList<>();

        // Generar n CURPs aleatorias
        for (int i = 0; i < n; i++) {
            aleatorios.add(getCURP());
        }
        //System.out.println("Lista inicial de CURPs:"+aleatorios+"\n");


        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch[] latches = new CountDownLatch[TASK_COUNT];
        for (int i = 0; i < TASK_COUNT; i++) {
            latches[i] = new CountDownLatch(1);
        }

        int segmento = n / TASK_COUNT;

        // Crear y ejecutar tareas
        for (int i = 0; i < TASK_COUNT; i++) {
            int start = i * segmento;
            int end = start + segmento;
            CountDownLatch previousLatch = (i == 0) ? null : latches[i - 1]; // Latch de la tarea anterior
            pool.execute(new Task(i + 1, aleatorios, start, end, latches[i], previousLatch));
        }
        pool.shutdown();
    }

    // Método para generar una CURP aleatoria
    private static String getCURP() {
        String letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numeros = "0123456789";
        String sexo = "HM";
        String[] entidad = {"AS", "BC", "BS", "CC", "CS", "CH", "CL", "CM", "DF", "DG", "GT", "GR", "HG", "JC", "MC", "MN", "MS", "NT", "NL", "OC", "PL", "QT", "QR", "SP", "SL", "SR", "TC", "TL", "TS", "VZ", "YN", "ZS"};

        Random rand = new Random();
        StringBuilder curp = new StringBuilder(18);

        for (int i = 0; i < 4; i++) curp.append(letras.charAt(rand.nextInt(letras.length())));
        for (int i = 0; i < 6; i++) curp.append(numeros.charAt(rand.nextInt(numeros.length())));
        curp.append(sexo.charAt(rand.nextInt(sexo.length())));
        curp.append(entidad[rand.nextInt(entidad.length)]);
        for (int i = 0; i < 3; i++) curp.append(letras.charAt(rand.nextInt(letras.length())));
        for (int i = 0; i < 2; i++) curp.append(numeros.charAt(rand.nextInt(numeros.length())));

        return curp.toString();
    }
}