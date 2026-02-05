package clases;

import java.io.Serializable;

public abstract class Vehiculo implements Serializable {
	private static final long serialVersionUID = 1L;
	protected String matricula;
	protected String marca;
	protected String modelo;

	public Vehiculo(String matricula, String marca, String modelo) {
		this.matricula = matricula;
		this.marca = marca;
		this.modelo = modelo;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	@Override
	public String toString() {
		return "Vehículo -> Matrícula: " + matricula + " , Marca: " + marca + " , Modelo: " + modelo;
	}
}