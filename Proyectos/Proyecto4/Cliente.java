import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Cliente {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String SERVER_IP = "34.9.139.95"; // IP del servidor
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Cliente de búsqueda de textos literarios");
        System.out.println("Servidor: " + SERVER_IP + ":" + SERVER_PORT);
        System.out.print("Ingrese la palabra a buscar (en inglés): ");
        String wordToSearch = scanner.nextLine();
        
        try {
            System.out.println("\nBuscando '" + wordToSearch + "'...\n");
            String response = sendSearchRequest(wordToSearch);
            System.out.println("Resultados de la búsqueda:\n");
            System.out.println(response);
        } catch (Exception e) {
            System.err.println("Error al realizar la búsqueda: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static String sendSearchRequest(String word) throws IOException, InterruptedException {
        String requestUrl = "http://" + SERVER_IP + ":" + SERVER_PORT + "/search";
        
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(word))
                .uri(URI.create(requestUrl))
                .header("Content-Type", "text/plain")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new IOException("Error en el servidor: " + response.body());
        }
        
        return response.body();
    }
}