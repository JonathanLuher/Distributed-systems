//Proyecto4 - Sistemas Distribuidos 7CM3 - Luciano Hernández Jonathan

import com.sun.net.httpserver.*;
import com.google.cloud.storage.*;
import com.google.cloud.storage.Blob;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.regex.*;

public class Servidor {
    private static final String STATUS_ENDPOINT = "/status";
    private static final String SEARCH_ENDPOINT = "/search";
    private static final String BUCKET_NAME = "project4-bucket";
    private static final String API_KEY = "AIzaSyCYKSQo6FmT5IXJhabu_w0siFnQTd5Up-E";
    private static final String TRANSLATION_API_URL = "https://translation.googleapis.com/language/translate/v2";

    private final int port;
    private HttpServer server;
    private Storage storage;

    public static void main(String[] args) {
        int serverPort = 8080;
        if (args.length == 1) {
            serverPort = Integer.parseInt(args[0]);
        }

        Servidor webServer = new Servidor(serverPort);
        webServer.startServer();

        System.out.println("Servidor escuchando en el puerto " + serverPort);
    }

    public Servidor(int port) {
        this.port = port;
        try {
            this.storage = StorageOptions.getDefaultInstance().getService();
        } catch (Exception e) {
            System.err.println("Error inicializando el cliente de Cloud Storage: " + e.getMessage());
            System.exit(1);
        }
    }

    public void startServer() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
        HttpContext searchContext = server.createContext(SEARCH_ENDPOINT);

        statusContext.setHandler(this::handleStatusCheckRequest);
        searchContext.setHandler(this::handleSearchRequest);

        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
    }

    private void handleSearchRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }

        // Leer la palabra a buscar del cuerpo de la solicitud
        String searchWord = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        
        // Buscar en los archivos del bucket
        String response = searchInBucket(searchWord);

        sendResponse(response.getBytes(StandardCharsets.UTF_8), exchange);
    }

    private String searchInBucket(String searchWord) {
        StringBuilder result = new StringBuilder();
        Pattern wordPattern = Pattern.compile("\\b" + Pattern.quote(searchWord) + "\\b", Pattern.CASE_INSENSITIVE);
        
        // Listar todos los blobs (archivos) en el bucket
        Iterable<Blob> blobs = storage.list(BUCKET_NAME).iterateAll();
        
        for (Blob blob : blobs) {
            String fileName = blob.getName();
            if (fileName.toLowerCase().endsWith(".pdf")) {
                try {
                    // Descargar el archivo PDF a un InputStream
                    byte[] pdfBytes = blob.getContent();
                    
                    // Procesar el PDF
                    try (PDDocument document = PDDocument.load(pdfBytes)) {
                        PDFTextStripper stripper = new PDFTextStripper();
                        String text = stripper.getText(document);
                        
                        // Extraer título y autor (asumiendo que están en las primeras líneas)
                        String[] lines = text.split("\\r?\\n");
                        String title = lines.length > 0 ? lines[0] : "Desconocido";
                        String author = lines.length > 1 ? lines[1] : "Desconocido";
                        
                        // Buscar la palabra en el texto
                        Matcher matcher = wordPattern.matcher(text);
                        if (matcher.find()) {
                            // Encontrar la oración completa donde aparece la palabra
                            String sentence = extractSentence(text, matcher.start());
                            
                            // Traducir la oración
                            String translated = translateText(sentence);
                            
                            // Resaltar la palabra encontrada
                            String highlighted = sentence.replaceAll("(?i)" + Pattern.quote(searchWord), 
                                "\u001B[31m" + matcher.group() + "\u001B[0m");
                            
                            // Formatear la respuesta
                            result.append("\u001B[34mEn el texto ").append(title)
                                  .append(" de ").append(author)
                                  .append(" se encontró la oración:\u001B[0m\n")
                                  .append(highlighted).append("\n")
                                  .append(translated).append("\n\n");
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error procesando el archivo " + fileName + ": " + e.getMessage());
                }
            }
        }
        
        return result.length() > 0 ? result.toString() : "No se encontró la palabra \"" + searchWord + "\" en ningún texto.";
    }

    private String extractSentence(String text, int position) {
        // Buscar el inicio de la oración
        int start = position;
        while (start > 0 && !isSentenceEnd(text.charAt(start - 1))) {
            start--;
        }
        
        // Buscar el final de la oración
        int end = position;
        while (end < text.length() && !isSentenceEnd(text.charAt(end))) {
            end++;
        }
        
        // Extraer y limpiar la oración
        String sentence = text.substring(start, end).trim();
        return sentence.replaceAll("\\s+", " ");
    }

    private boolean isSentenceEnd(char c) {
        return c == '.' || c == '!' || c == '?' || c == '\n';
    }

    private String translateText(String text) {
        try {
            String encodedText = java.net.URLEncoder.encode(text, StandardCharsets.UTF_8);
            String apiUrl = TRANSLATION_API_URL + "?key=" + API_KEY + "&target=es&q=" + encodedText;

            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .POST(java.net.http.HttpRequest.BodyPublishers.noBody())
                    .uri(java.net.URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

            java.net.http.HttpResponse<String> response = java.net.http.HttpClient.newHttpClient()
                    .send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Pattern translationPattern = Pattern.compile("\"translatedText\"\\s*:\\s*\"(.*?)\"");
                Matcher matcher = translationPattern.matcher(response.body());
                if (matcher.find()) {
                    return matcher.group(1).replace("&#39;", "'");
                }
            }
        } catch (Exception e) {
            System.err.println("Error en la traducción: " + e.getMessage());
        }
        return "Error en la traducción.";
    }

    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }

        String responseMessage = "El servidor está vivo\n";
        sendResponse(responseMessage.getBytes(), exchange);
    }

    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.flush();
        outputStream.close();
    }
}