import java.util.*;

class Main {
    public static void main(String[] args) {
        // Verificar que se han pasado los argumentos
        if (args.length < 2) {
            System.out.println("Uso: java Main <cantidad_de_CURPs> <sexo_a_eliminar (H/M)>");
            return;
        }
        
        int n = Integer.parseInt(args[0]);
        char sexoAEliminar = args[1].charAt(0);
        
        if (sexoAEliminar != 'H' && sexoAEliminar != 'M') {
            System.out.println("El segundo parámetro debe ser 'H' o 'M'");
            return;
        }
        
        List<String> curps = new ArrayList<>();
        
        // Generar n CURPs
        for (int i = 0; i < n; i++) {
            curps.add(getCURP());
        }
        
        // Imprimir CURPs generadas
        System.out.println("CURPs generadas:");
        for (String curp : curps) {
            System.out.println(curp);
        }
        
        // Filtrar CURPs usando Iterator
        Iterator<String> iterator = curps.iterator();
        while (iterator.hasNext()) {
            String curp = iterator.next();
            if (curp.charAt(10) == sexoAEliminar) {
                iterator.remove();
            }
        }
        
        // Imprimir CURPs después del filtrado
        System.out.println("\nCURPs después del filtrado:");
        for (String curp : curps) {
            System.out.println(curp);
        }
    }

    static String getCURP() {
        String Letra = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String Numero = "0123456789";
        String Sexo = "HM";
        String[] Entidad = {"AS", "BC", "BS", "CC", "CS", "CH", "CL", "CM", "DF", "DG", "GT", "GR", "HG", "JC", "MC", "MN", "MS", "NT", "NL", "OC", "PL", "QT", "QR", "SP", "SL", "SR", "TC", "TL", "TS", "VZ", "YN", "ZS"};
        int indice;
        
        StringBuilder sb = new StringBuilder(18);
        
        for (int i = 1; i < 5; i++) {
            indice = (int) (Letra.length() * Math.random());
            sb.append(Letra.charAt(indice));
        }
        
        for (int i = 5; i < 11; i++) {
            indice = (int) (Numero.length() * Math.random());
            sb.append(Numero.charAt(indice));
        }
        indice = (int) (Sexo.length() * Math.random());
        sb.append(Sexo.charAt(indice));
        
        sb.append(Entidad[(int) (Math.random() * 32)]);
        
        for (int i = 14; i < 17; i++) {
            indice = (int) (Letra.length() * Math.random());
            sb.append(Letra.charAt(indice));
        }
        for (int i = 17; i < 19; i++) {
            indice = (int) (Numero.length() * Math.random());
            sb.append(Numero.charAt(indice));
        }
        
        return sb.toString();
    }
}
