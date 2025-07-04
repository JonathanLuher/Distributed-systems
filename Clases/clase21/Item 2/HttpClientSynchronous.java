import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpClientSynchronous {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void main(String[] args) throws IOException, InterruptedException {
        // URL del endpoint
        String apiUrl = "https://jsonplaceholder.typicode.com/posts";
        
        // Datos JSON que vamos a enviar
        String jsonBody = """
                {
                    "userId": 1,
                    "title": "7CM3-EQUIPO 9",
                    "body": "Luciano Hernandez Jonathan"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .uri(URI.create(apiUrl))
                .setHeader("User-Agent", "Java 11 HttpClient Bot")
                .setHeader("Content-Type", "application/json") // Importante para POST JSON
                .setHeader("Accept", "application/json") // Queremos recibir JSON de vuelta
                .build();

        System.out.println("Enviando solicitud POST para crear nuevo recurso...");
        System.out.println("URL: " + apiUrl);
        System.out.println("Cuerpo de la solicitud:\n" + jsonBody + "\n");

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        HttpHeaders headers = response.headers();
        headers.map().forEach((k, v) -> System.out.println(k + ": " + v));

        System.out.println(response.statusCode());

        System.out.println(response.body());
    }
}