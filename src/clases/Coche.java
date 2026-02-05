package clases;

public class Coche extends Vehiculo {
	private int puertas;

	public Coche(String matricula, String marca, String modelo, int puertas) {
		super(matricula, marca, modelo);
		this.puertas = puertas;
	}

	public int getPuertas() {
		return puertas;
	}

	public void setPuertas(int puertas) {
		this.puertas = puertas;
	}

	@Override
	public String toString() {
		return super.toString() + " , Puertas: " + puertas;
	}
}