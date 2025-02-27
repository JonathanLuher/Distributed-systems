public class main {
	public static void main(string[] args){
		Coordenada centroR = new Coordenada (-1.0,-1.0);
		Coordenada centroT = new Coordenada(1.0,1.0)
		TrianguloEq triangulo1 = new TrianguloEq(centroT,3.0);
		Rectangulo rectangulo1 = new Rectangulo(centroR,2.0,4.0);


		System.out.println(triangulo1.area);
		System.out.println(rectangulo1.area);

		triangulo1.desplazar(1.0,1.0);

		System.out.println(triangulo1.centro);
		System.out.println(rectangulo1.centro);
	}
}