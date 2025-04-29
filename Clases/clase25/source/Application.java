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

import java.util.ArrayList;
import java.util.List;

public class Application {
    private static final String WORKER_ADDRESS_1 = "http://34.171.77.204:8081/searchtoken";
    public static void main(String[] args) {
        Aggregator aggregator = new Aggregator();

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
    }
}