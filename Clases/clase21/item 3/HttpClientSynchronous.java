import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class HttpClientSynchronous {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void main(String[] args) throws IOException, InterruptedException {
        // Configuración de parámetros
        String apiKey = "AIzaSyCYKSQo6FmT5IXJhabu_w0siFnQTd5Up-E";
        String targetLanguage = "es";
        String textToTranslate = "People have the right to disagree with your opinions and to dissent.";
        
        // Codificar el texto para URL
        String encodedText = URLEncoder.encode(textToTranslate, StandardCharsets.UTF_8);
        
        // Construir URL con parámetros
        String apiUrl = "https://translation.googleapis.com/language/translate/v2" +
                "?key=" + apiKey +
                "&target=" + targetLanguage +
                "&q=" + encodedText;

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody()) // POST sin cuerpo
                .uri(URI.create(apiUrl))
                .setHeader("User-Agent", "Java 11 HttpClient Bot")
                .setHeader("Content-Type", "application/json")
                .setHeader("Accept", "application/json")
                .build();

        System.out.println("Enviando solicitud de traducción a Google Cloud Translation API...");
        System.out.println("URL: " + apiUrl + "\n");

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        HttpHeaders headers = response.headers();
        headers.map().forEach((k, v) -> System.out.println(k + ": " + v));

        System.out.println(response.statusCode());

        System.out.println(response.body());
    }
}