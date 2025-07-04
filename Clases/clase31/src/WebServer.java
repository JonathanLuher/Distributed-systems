/*
 *  MIT License
 *
 *  Copyright (c) 2019 Michael Pogrebinsky - Distributed Systems & Cloud Computing with Java
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;

public class WebServer {
    private static final String TASK_ENDPOINT = "/task";
    private static final String STATUS_ENDPOINT = "/status";
    private static final String SEARCH_ENDPOINT = "/searchtoken";

    private final int port;
    private HttpServer server;

    public static void main(String[] args) {
        int serverPort = 8083;
        if (args.length == 1) {
            serverPort = Integer.parseInt(args[0]);
        }

        WebServer webServer = new WebServer(serverPort);
        webServer.startServer();

        System.out.println("Servidor escuchando en el puerto " + serverPort);
    }

    public WebServer(int port) {
        this.port = port;
    }

    public void startServer() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        HttpContext taskContext = server.createContext(TASK_ENDPOINT);
        HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
        HttpContext searchContext = server.createContext(SEARCH_ENDPOINT);

        taskContext.setHandler(this::handleTaskRequest);
        statusContext.setHandler(this::handleStatusCheckRequest);
        searchContext.setHandler(this::handleSearchRequest);

        server.setExecutor(Executors.newFixedThreadPool(1));
        server.start();
    }

    private void handleTaskRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }

        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        
        // Deserializar el objeto Demo
        Demo receivedDemo = (Demo) SerializationUtils.deserialize(requestBytes);
        
        // Imprimir el contenido del objeto recibido
        System.out.println("\nObjeto Demo recibido en el servidor:");
        System.out.println("a = " + receivedDemo.a);
        System.out.println("b = " + receivedDemo.b);

        // Responder confirmación de recepción
        String response = "Mensaje recibido correctamente";
        sendResponse(response.getBytes(), exchange);
    }

    private void handleSearchRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }

        Headers headers = exchange.getRequestHeaders();

        boolean isDebugMode = false;
        if (headers.containsKey("X-Debug") && headers.get("X-Debug").get(0).equalsIgnoreCase("true")) {
            isDebugMode = true;
        }

        long startTime = System.nanoTime();

        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        byte[] responseBytes = getSubstring(requestBytes);

        long finishTime = System.nanoTime();

        if (isDebugMode) {
            long duration = finishTime - startTime;
            long seconds = duration / 1_000_000_000;
            long millis = (duration % 1_000_000_000) / 1_000_000;
            String debugMessage = String.format("La operacion tomo %d nanosegundos = %d segundos con %d milisegundos", 
                duration, seconds, millis);
            exchange.getResponseHeaders().put("X-Debug-Info", Arrays.asList(debugMessage));
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sendResponse(responseBytes, exchange);
    }

    private byte[] getSubstring(byte[] requestBytes) {
        String bodyString = new String(requestBytes);
        String[] stringData = bodyString.split(",");

        int num = Integer.parseInt(stringData[0]);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            String s = generateString();
            System.out.print(s + " ");
            list.add(s);
        }

        int count = 0;
        for (String s : list) {
            if (s.equalsIgnoreCase(stringData[1])) count++;
        }

        return String.format("Encontrada %d veces\n", count).getBytes();
    }

    private String generateString() {
        Random r = new Random();
        StringBuilder s = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            char c = (char) (r.nextInt(26) + 'A');
            s.append(c);
        }

        return s.toString();
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
        exchange.close();
    }
}