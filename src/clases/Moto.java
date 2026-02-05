package clases;

public class Moto extends Vehiculo {
	private int cilindrada;

	public Moto(String matricula, String marca, String modelo, int cilindrada) {
		super(matricula, marca, modelo);
		this.cilindrada = cilindrada;
	}

	public int getCilindrada() {
		return cilindrada;
	}

	public void setCilindrada(int cilindrada) {
		this.cilindrada = cilindrada;
	}

	@Override
	public String toString() {
		return super.toString() + " , Cilindrada: " + cilindrada + "cc";
	}
}