public abstract class figura implements Desplazable{

	Coordenada centro;

	public figura(Coordenada centro){
		this.centro = centro;
	}

	public abstract double area();

	public void dessplazar(double dx, double dy){
		centro = new Coordenada(centro.abcisa()+dx,centro.ordenada()+dy);
	}

}