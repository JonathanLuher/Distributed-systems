import java.util.ArrayList;
import java.util.Iterator;

class Main2 {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java Main <cantidad de CURPs>");
            return;
        }
        int num = Integer.parseInt(args[0]);

        ArrayList<String> curpList = new ArrayList<>();

        // Generación e inserción ordenada de CURPs
        for (int i = 0; i < num; i++) {
            String curp = getCURP();
            
            // Si es la primera CURP, se imprime como "CURP inicial"
            if (curpList.isEmpty()) {
                System.out.println("CURP inicial: " + curp);
            } else {
                System.out.println("\nNueva CURP generada: " + curp);
            }
            
            insertOrdered(curpList, curp);
            System.out.println("La lista de CURP es: " + curpList);
        }
    }

    // Método para insertar una CURP en orden ascendente en la lista
    static void insertOrdered(ArrayList<String> list, String curp) {
        Iterator<String> iterator = list.iterator();
        int index = 0;

        while (iterator.hasNext()) {
            if (curp.substring(0, 4).compareTo(iterator.next().substring(0, 4)) < 0) {
                break;
            }
            index++;
        }
        list.add(index, curp);
    }

    // Método para generar una CURP aleatoria
    static String getCURP() {
        String Letra = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String Numero = "0123456789";
        String Sexo = "HM";
        String[] Entidad = {"AS", "BC", "BS", "CC", "CS", "CH", "CL", "CM", "DF", "DG", "GT", "GR", "HG", "JC", "MC", "MN", "MS", "NT", "NL", "OC", "PL", "QT", "QR", "SP", "SL", "SR", "TC", "TL", "TS", "VZ", "YN", "ZS"};

        StringBuilder sb = new StringBuilder(18);

        for (int i = 0; i < 4; i++) {
            sb.append(Letra.charAt((int) (Math.random() * Letra.length())));
        }
        for (int i = 0; i < 6; i++) {
            sb.append(Numero.charAt((int) (Math.random() * Numero.length())));
        }

        sb.append(Sexo.charAt((int) (Math.random() * Sexo.length())));
        sb.append(Entidad[(int) (Math.random() * Entidad.length)]);

        for (int i = 0; i < 3; i++) {
            sb.append(Letra.charAt((int) (Math.random() * Letra.length())));
        }
        for (int i = 0; i < 2; i++) {
            sb.append(Numero.charAt((int) (Math.random() * Numero.length())));
        }

        return sb.toString();
    }
}

