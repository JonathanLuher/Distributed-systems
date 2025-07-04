public class PruebaRectangulo {
    public static void main (String[] args) {
        
        Coordenada coordenada1=new Coordenada(2,3);
        Coordenada coordenada2=new Coordenada(5,1);
        double ancho, alto;
        
        Rectangulo rect1 = new Rectangulo(coordenada1,coordenada2);
        
        System.out.println("Calculando el área de un rectángulo dadas sus coordenadas en un plano cartesiano:");
        System.out.println(rect1);
        alto = rect1.superiorIzquierda().ordenada() - rect1.inferiorDerecha().ordenada();
        ancho = rect1.inferiorDerecha().abcisa() - rect1.superiorIzquierda().abcisa();
        System.out.println("El área del rectángulo es = " + ancho*alto);
    }
}