package clases;

import java.io.Serializable;

public abstract class Persona implements Serializable {
	protected static final long serialVersionUID = 1L;
	protected String dni;
	protected String nombre;
	protected String apellido;

	public Persona(String dni, String nombre, String apellido) {
		if (!dni.matches("\\d{8}[A-Za-z]")) {
			throw new DNIException("DNI incorrecto: " + dni);
		}
		this.dni = dni;
		this.nombre = nombre;
		this.apellido = apellido;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	@Override
	public String toString() {
		return "Persona -> DNI: " + dni + " , Nombre: " + nombre + " , Apellido: " + apellido;
	}
}
