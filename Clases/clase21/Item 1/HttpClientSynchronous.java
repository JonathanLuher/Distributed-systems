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
        // URL de la API para obtener frases
        String apiUrl = "https://api.breakingbadquotes.xyz/v1/quotes";
        
        int numberOfQuotes = 1;
        String fullUrl = apiUrl + (numberOfQuotes > 1 ? "/" + numberOfQuotes : "");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(fullUrl))
                .setHeader("User-Agent", "Java 11 HttpClient Bot")
                .setHeader("Accept", "application/json")
                .build();

        System.out.println("URL: " + fullUrl + "\n");

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        HttpHeaders headers = response.headers();
        headers.map().forEach((k, v) -> System.out.println(k + ": " + v));

        System.out.println(response.statusCode());

        System.out.println(response.body());
    }
}