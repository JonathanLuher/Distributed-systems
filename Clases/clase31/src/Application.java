import java.util.ArrayList;
import java.util.List;

public class Application {
    private static final String WORKER_ADDRESS_1 = "http://localhost:8083/searchtoken";
    private static final String WORKER_ADDRESS_2 = "http://localhost:8083/task";

    public static void main(String[] args) {
        Aggregator aggregator = new Aggregator();

        // Parte original del ejercicio
        List<String> listaTareas = new ArrayList<String>();
        listaTareas.add("1757600,IPN");
        listaTareas.add("17576,SAL");
        listaTareas.add("70000,MAS");
        listaTareas.add("1757600,PEZ");
        listaTareas.add("175700,SOL");

        List<String> workers = new ArrayList<>();
        workers.add(WORKER_ADDRESS_1);
        workers.add(WORKER_ADDRESS_2);

        System.out.println("En el método sendTasksToWorkers se asignaron las siguientes tareas a los servidores:");
        
        List<String> results = aggregator.sendTasksToWorkers(workers, listaTareas);

        for (String result : results) {
            System.out.println(result);
        }

        // Parte nueva para el endpoint /task
        System.out.println("\nProbando endpoint /task:");
        WebClient client = new WebClient();
        CompletableFuture<String> demoResponse = client.sendDemoObject("http://localhost:8083/task");
        System.out.println("Respuesta del servidor para /task:");
        System.out.println(demoResponse.join());
    }
}