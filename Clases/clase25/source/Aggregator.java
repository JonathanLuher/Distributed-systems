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

import networking.WebClient;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class Aggregator {
    private WebClient webClient;

    public Aggregator() {
        this.webClient = new WebClient();
    }

    public List<String> sendTasksToWorkers(List<String> workersAddresses, List<String> tasks) {
        List<String> results = new ArrayList<>();
        List<CompletableFuture<String>> futures = new ArrayList<>();

        // Asignar las primeras 2 tareas a los servidores
        for (int i = 0; i < 2 && i < tasks.size(); i++) {
            String workerAddress = workersAddresses.get(i % workersAddresses.size());
            String task = tasks.get(i);
            
            System.out.println("Servidor " + workerAddress + " -> Tarea: " + task);
            
            byte[] requestPayload = task.getBytes();
            futures.add(webClient.sendTask(workerAddress, requestPayload));
        }

        // Procesar las tareas restantes
        for (int i = 2; i < tasks.size(); i++) {
            // Esperar a que algún servidor termine
            CompletableFuture.anyOf(futures.toArray(new CompletableFuture[0])).join();
            
            // Encontrar el índice del servidor que terminó primero
            int completedIndex = -1;
            for (int j = 0; j < futures.size(); j++) {
                if (futures.get(j).isDone()) {
                    completedIndex = j;
                    break;
                }
            }
            
            // Obtener resultados del servidor que terminó
            try {
                results.add(futures.get(completedIndex).get());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Asignar nueva tarea al servidor que terminó
            String workerAddress = workersAddresses.get(completedIndex % workersAddresses.size());
            String task = tasks.get(i);
            
            System.out.println("Servidor " + workerAddress + " -> Tarea: " + task);
            
            byte[] requestPayload = task.getBytes();
            futures.set(completedIndex, webClient.sendTask(workerAddress, requestPayload));
        }

        // Esperar a que terminen las últimas tareas
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        for (CompletableFuture<String> future : futures) {
            try {
                results.add(future.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return results;
    }
}