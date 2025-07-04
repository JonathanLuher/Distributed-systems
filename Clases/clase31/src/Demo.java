import java.io.Serializable;
import java.time.Instant;

public class Demo implements Serializable {
    public int a;
    public String b;

    // Constructor por defecto
    public Demo(int a, String b) {
        this.a = a;
        this.b = b;
    }

    // Constructor sobrecargado que usa la hora actual
    public Demo(int a) {
        this.a = a;
        this.b = Instant.now().toString(); // Asigna la fecha/hora actual como string
    }

    // Método para mostrar los valores (opcional, útil para debugging)
    @Override
    public String toString() {
        return String.format("Demo[a=%d, b='%s']", a, b);
    }
}