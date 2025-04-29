import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpClientSynchronous {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void main(String[] args) throws IOException, InterruptedException {

        String url = "https://storage.googleapis.com/storage/v1/b/jonathan_bucketsd/o/redhood.jpg";
        String bearerToken = "ya29.c.c0ASRK0GYarnFPe63ZY6RFIPJ4fq2uAxmTyKTMSeifdL4UMqSpHfVTykU_nI7ij60JrRNXk0HDpdiCAwJu4DFj4cEiix7oePoR9ieQaKZNtl9GXkS151DRYbA__XrCsHnIlkTkejGKbIs_fuXY3HaVAJhJKVjemy2Bk-wLnSPAb5s_skNtjx5WImZDkRhnS4DzdGASiBoAtpm9jyCAR6Fhub_fD6_j72ponZBkG9wXcEDFyKNBcoCDX190NADLirnzElGFwFRWiFVYd0EXjz5sfLKrEKErIlCM-MoDv9reA3zCN82hSR0qpsL0QuHNLHMFYJbb1-2ej1l4x_v0luBXAE3Eg62n-cuvu90tAy9tGceyTKtszrf9SvKex75C1_t0njnZMwE399PdtU7amokbMvZRJMq5XskU4iMtyx-QztaWvZXxiMkplOQwO1VOI7Y4Mwe-457d5btuVUswWkUwx7r8eu4618qdOqO4Ze3qopebUQto3zQhpgvJUQie86Vhxw3n2nvSXl6XpRY2wasii-I0mVMoxs-wsp2lQZh3ayMBtw29kZll97wSyXpO-9_RRbf9blmjbY4UstsVM6_h8eQS8x7R3ZUbf61pUY6hrkw5k-FzcgBvrgbUBg75xqUFJ3oc9lbdWw88gJphXpSQJWQaa7JzO7kSavIU2lY-aMtt32e_XUtcqkkieqY8ebM3u5wQ_feerJ9uMngstJnVpv7YfaIfgOysuteJja8FWh486UYF9i7cV1xfpJolJq4u3n8ka9v-a8O3wxcMVghq6S-tabpaUMhlq_MBSXfMYa6Yr5R6nZFsewnrnRzgoRj9oJ_wzuWjs5O1UfrQjItbmm3VZk-wuy-37isV2l9u-4X1n217WOxrlQtx4kSh0Ohzlhn-qh1I9tb4Rt-mIlyUXIkjm_s0f_aifnjhgOx4e3VyU583lVOp1SmO0I9lcQFnxudM2pQUWupUBBZrucgJ6ShFBMyqln4v0yy0gheofendz3MBed4pjt_"; // Token de autorización

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .setHeader("Authorization", "Bearer " + bearerToken) // Token de autorización
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // Header opcional
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Imprimir los encabezados de la respuesta
        HttpHeaders headers = response.headers();
        System.out.println("Encabezados de la respuesta:");
        headers.map().forEach((k, v) -> System.out.println(k + ": " + v));

        // Imprimir el código de estado
        System.out.println("\nCódigo de estado HTTP: " + response.statusCode());

        // Imprimir el cuerpo de la respuesta
        System.out.println("\nCuerpo de la respuesta:");
        System.out.println(response.body());
    }
}
