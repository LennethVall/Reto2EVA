package clases;

import java.io.Serializable;
import java.time.LocalDate;

public class Reserva implements Serializable {
	private static final long serialVersionUID = 1L;
	private int nreserva;
	private String dnicli;
	private String nombreCli;
	private String apellidoCli;
	private String modelo;
	private LocalDate fechaini, fechafin;

	public Reserva(int nreserva, String dnicli, String nombreCli, String apellidoCli, String modelo, LocalDate fechaini,
			LocalDate fechafin) {
		this.nreserva = nreserva;
		this.dnicli = dnicli;
		this.nombreCli = nombreCli;
		this.apellidoCli = apellidoCli;
		this.modelo = modelo;
		this.fechaini = fechaini;
		this.fechafin = fechafin;
	}

	public String getDnicli() {
		return dnicli;
	}

	public void setDnicli(String dnicli) {
		this.dnicli = dnicli;
	}

	public String getNombreCli() {
		return nombreCli;
	}

	public void setNombreCli(String nombreCli) {
		this.nombreCli = nombreCli;
	}

	public String getApellidoCli() {
		return apellidoCli;
	}

	public void setApellidoCli(String apellidoCli) {
		this.apellidoCli = apellidoCli;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public int getNreserva() {
		return nreserva;
	}

	public void setNreserva(int nreserva) {
		this.nreserva = nreserva;
	}

	public LocalDate getFechaini() {
		return fechaini;
	}

	public void setFechaini(LocalDate fechaini) {
		this.fechaini = fechaini;
	}

	public LocalDate getFechafin() {
		return fechafin;
	}

	public void setFechafin(LocalDate fechafin) {
		this.fechafin = fechafin;
	}

	public String getEstado() {
		LocalDate hoy = LocalDate.now();
		if (hoy.isBefore(fechaini)) {
			return "En reserva";
		} else if (hoy.isAfter(fechafin)) {
			return "Finalizada";
		} else {
			return "Activa";
		}
	}

	@Override
	public String toString() {
		return "Reserva [nreserva=" + nreserva + ", dnicli=" + dnicli + ", Cliente=" + nombreCli + " " + apellidoCli
				+ ", modelo=" + modelo + ", fechaini=" + fechaini + ", fechafin=" + fechafin + ", estado=" + getEstado()
				+ "]";
	}
}