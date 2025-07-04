java.lang.Math();

public class TrianguloEq extends figura{
	double lado;

	public TrianguloEq(Coordenada centro, double lado){
		super(centro);
		this.lado = lado;
	}

	public double area(){
		return (math.sqrt(3)/4)*Math.pow(lado,2);
	}

	
}