import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PoligonoIrreg {
    private List<Coordenada> vertices;

    public PoligonoIrreg() {
        vertices = new ArrayList<>();
    }

    public PoligonoIrreg(int t, int min, int max) {
        Random r = new Random();
        vertices = new ArrayList<>();
        for (int i = 0; i < t; i++)
            vertices.add(new Coordenada(min + (max - min) * r.nextDouble(), 
                                      min + (max - min) * r.nextDouble()));
    }

    // Métodos nuevos para acceso controlado
    public void anadeVertice(Coordenada c) {
        vertices.add(c);
    }

    public int getNumVertices() {
        return vertices.size();
    }

    public Coordenada getVertice(int index) {
        return vertices.get(index);
    }

    public List<Coordenada> getVertices() {
        return new ArrayList<>(vertices); // Devuelve una copia para protección
    }

    public void setVertice(int index, Coordenada c) {
        vertices.set(index, c);
    }

    public void ordenaVertices() {
        Collections.sort(vertices, (v1, v2) -> {
            return Double.compare(v1.getMagnitud(), v2.getMagnitud());
        });
    }

    @Override
    public String toString() {
        String s = "";
        for (Coordenada c: vertices) s += c + "\n";
        s += "Vertices totales: " + vertices.size();
        return s;
    }
}