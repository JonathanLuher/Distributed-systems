import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpClientSynchronous {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final String API_KEY = "AIzaSyCYKSQo6FmT5IXJhabu_w0siFnQTd5Up-E";
    private static final String TRANSLATION_API_URL = "https://translation.googleapis.com/language/translate/v2";
    private static final String BREAKING_BAD_QUOTES_URL = "https://api.breakingbadquotes.xyz/v1/quotes/3";

    public static void main(String[] args) throws IOException, InterruptedException {
        String quotesJson = getBreakingBadQuotes();

        if (quotesJson != null) {
            System.out.println(quotesJson);
            System.out.println();

            Pattern pattern = Pattern.compile("\\{\\s*\"quote\"\\s*:\\s*\"(.*?)\",\\s*\"author\"\\s*:\\s*\"(.*?)\"\\s*\\}");
            Matcher matcher = pattern.matcher(quotesJson);

            while (matcher.find()) {
                String quote = matcher.group(1).replace("\\\"", "\"");
                String author = matcher.group(2).replace("\\\"", "\"");

                String translated = translateText(quote);

                System.out.println("Autor:\t\"" + author + "\"");
                System.out.println("Cita: \"" + quote + "\"");
                System.out.println("Traducción: " + translated);
                System.out.println(); // Separador
            }
        } else {
            System.out.println("No se pudo obtener el JSON.");
        }
    }

    private static String getBreakingBadQuotes() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(BREAKING_BAD_QUOTES_URL))
                .setHeader("User-Agent", "Java 11 HttpClient Bot")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return (response.statusCode() == 200) ? response.body() : null;
    }

    private static String translateText(String text) throws IOException, InterruptedException {
        String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
        String apiUrl = TRANSLATION_API_URL + "?key=" + API_KEY + "&target=es&q=" + encodedText;

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            Pattern translationPattern = Pattern.compile("\"translatedText\"\\s*:\\s*\"(.*?)\"");
            Matcher matcher = translationPattern.matcher(responseBody);
            if (matcher.find()) {
                return matcher.group(1).replace("&#39;", "'");
            }
        }
        return "Error en la traducción.";
    }
}
