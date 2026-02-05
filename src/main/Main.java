package main;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import clases.*;

public class Main {

	private static File ficheroPersonas = new File("personas.dat");
	private static File ficheroVehiculos = new File("vehiculos.dat");
	private static File ficheroReservas = new File("reservas.dat");
	private static HashMap<String, String[]> traducciones = new HashMap<>();

	private static ArrayList<Persona> personas = new ArrayList<>();

	private static ArrayList<Vehiculo> vehiculos = new ArrayList<>();
	private static TreeMap<Integer, Reserva> reservas = new TreeMap<>();

	private static int nreservas = 100;
	private static int idiomaSeleccionado = 1;

	public static void main(String[] args) {
		cargarTraducciones();
		
		cargarDatos();

		do {
			System.out.println("1. Español");
			System.out.println("2. English");
			idiomaSeleccionado = Util.leerInt();
		} while (idiomaSeleccionado != 1 && idiomaSeleccionado != 2);

		Scanner teclado = new Scanner(System.in);
		int opcion;

		do {
			mostrarMenu();
			opcion = leerOpcionValida(1, 13);

			switch (opcion) {
			case 1:
				altaCliente();
				break;
			case 2:
				listarClientes();
				break;
			case 3:
				modificarCliente();
				break;
			case 4:
				altaEmpleado();
				break;
			case 5:
				listarEmpleados();
				break;
			case 6:
				altaVehiculo();
				break;
			case 7:
				listarVehiculos();
				break;
			case 8:
				añadirReserva();
				break;
			case 9:
				listarReservas();
				break;
			case 10:
				mostrarReservasCliente();
				break;
			case 11:
				anularReserva();
				break;
			case 12:
				guardarDatos();
				break;
			case 13:
				msg("CONFIRMAR_SALIDA");
				if (teclado.next().equalsIgnoreCase("s")) {
					guardarDatos();
					System.exit(0);
				}
				break;
			}

			pausar();
		} while (true);
	}

	// ==================== METODOS DE INTERFAZ ====================

	private static void mostrarMenu() {
		limpiarPantalla();

		if (idiomaSeleccionado == 2) {

			// Menú en inglés cargado desde archivo externo
			File archivo = new File("ingles.txt");
			FileReader fr;
			BufferedReader br;

			try {
				fr = new FileReader(archivo);
				br = new BufferedReader(fr);

				// Se muestra cada línea del archivo
				String linea;
				while ((linea = br.readLine()) != null) {
					System.out.println(linea);
				}

				br.close();
				fr.close();

			} catch (IOException e) {
				// Si falla la lectura, se avisa al usuario
				System.out.println("Error al leer el archivo en inglés.");
			}

		} else {

			// Menú en castellano mostrado directamente desde el código
			System.out.println("===== ALQUILER DE VEHICULOS =====");
			System.out.println("|||||||||||||||||||||||||||||||||");
			System.out.println("¡Bienvenid@ al menu principal!");
			System.out.println("\nOpciones disponibles:");
			System.out.println("1. Alta de cliente");
			System.out.println("2. Listar clientes");
			System.out.println("3. Modificar cliente");
			System.out.println("4. Añadir empleado");
			System.out.println("5. Listar empleados");
			System.out.println("6. Añadir vehiculo");
			System.out.println("7. Listar vehiculos");
			System.out.println("8. Añadir reserva");
			System.out.println("9. Listar reservas");
			System.out.println("10. Mostrar reservas de cliente");
			System.out.println("11. Anular reserva");
			System.out.println("12. Guardar datos");
			System.out.println("13. Salir");
		}

	}

	// Añade líneas en blanco para separar pantallas
	private static void limpiarPantalla() {
		for (int i = 0; i < 2; i++) {
			System.out.println();
		}
	}

	// Pausa hasta que el usuario pulse una tecla
	private static void pausar() {
		msg("PAUSAR");
		try {
			System.in.read(); // Espera pulsación
			while (System.in.available() > 0)
				System.in.read(); // Limpia buffer
		} catch (IOException e) {
		}
	}

	// Lee una opción dentro del rango permitido
	private static int leerOpcionValida(int min, int max) {
		int opcion;
		do {
			msg("SELECCIONE_OPCION");
			opcion = Util.leerInt();
			if (opcion < min || opcion > max)
				msg("OPCION_INVALIDA");
		} while (opcion < min || opcion > max);
		return opcion;
	}

	// Solicita un DNI y valida su formato
	private static String solicitarDNIValido() {
	    String dni;
	    while (true) { // Bucle infinito hasta que hagamos un return
	        msg("INTRODUCIR_DNI");
	        dni = Util.introducirCadena();
	        
	        // Comprobamos el formato con la expresión regular
	        if (!dni.matches("\\d{8}[A-Za-z]")) {
	            msg("DNI_FORMATO_INCORRECTO");
	            // No hay return, así que vuelve arriba al "while"
	        } else {
	            return dni; // Formato correcto, salimos de la función con el valor
	        }
	    }
	}

	// Imprime una línea separadora
	private static void imprimirSeparador() {
		System.out.println("-".repeat(80));
	}

	// Muestra un título formateado
	private static void imprimirTitulo(String titulo) {
		System.out.println("\n" + "=".repeat(80));
		System.out.println("  " + titulo);
		System.out.println("=".repeat(80));
	}

	// ==================== GESTION DE CLIENTES ====================

	// Alta de un nuevo cliente
	private static void altaCliente() {
		imprimirTitulo("ALTA DE CLIENTES");

		String dni = solicitarDNIValido(); // Pide DNI válido

		// Comprueba si el cliente ya existe
		boolean existe = false;
		for (Persona p : personas) {
			if (p instanceof Cliente) {
				Cliente c = (Cliente) p;
				if (dni.equalsIgnoreCase(c.getDni())) {
					existe = true;
				}
			}
		}

		if (existe) {
			msg("CLIENTE_EXISTE"); // Evita duplicados
			return;
		}

		// Solicita datos del cliente
		msg("INTRODUCIR_NOMBRE");
		String nombre = Util.introducirCadena();

		msg("INTRODUCIR_APELLIDO");
		String apellido = Util.introducirCadena();

		msg("INTRODUCIR_TELEFONO");
		int telefono = Util.leerInt();

		// Crea y guarda el cliente
		Cliente c = new Cliente(dni, nombre, apellido, telefono);
		personas.add(c);

		msg("CLIENTE_ANADIDO");
		guardarPersonas(); // Guarda en archivo
	}

	// Muestra todos los clientes registrados
	private static void listarClientes() {
		imprimirTitulo("LISTADO DE CLIENTES");

		// Filtra solo los clientes
		ArrayList<Cliente> clientes = new ArrayList<>();
		for (Persona p : personas) {
			if (p instanceof Cliente) {
				clientes.add((Cliente) p);
			}
		}

		if (clientes.isEmpty()) {
			msg("NO_HAY_CLIENTES"); // No hay clientes
		} else {
			// Cabecera del listado
			System.out.println();
			System.out.printf("%-12s %-20s %-20s %-12s%n", "DNI", "NOMBRE", "APELLIDO", "TELEFONO");
			imprimirSeparador();

			// Muestra cada cliente
			for (Cliente c : clientes) {
				System.out.printf("%-12s %-20s %-20s %-12d%n", c.getDni(), c.getNombre(), c.getApellido(),
						c.getTelefono());
			}

			msg("TOTAL_CLIENTES"); // Mensaje final
		}
	}

	// Modifica los datos de un cliente existente
	private static void modificarCliente() {
		imprimirTitulo("MODIFICAR CLIENTE");

		if (personas.size() == 0) { // No hay clientes
			msg("NO_HAY_CLIENTES_MODIFICAR");
			return;
		}

		String dni = solicitarDNIValido(); // Pide DNI
		Cliente cliente = buscarClientePorDni(dni); // Busca cliente

		if (cliente == null) { // No encontrado
			msg("CLIENTE_NO_ENCONTRADO");
			return;
		}

		msg("DATOS_ACTUALES");
		System.out.println(cliente.toString()); // Muestra datos actuales

		// Guarda valores previos para actualizar reservas si cambia el nombre
		String nombreAnterior = cliente.getNombre();
		String apellidoAnterior = cliente.getApellido();

		// Menú de modificación
		msg("QUE_DESEA_MODIFICAR");
		msg("MOD_NOMBRE");
		msg("MOD_APELLIDO");
		msg("MOD_TELEFONO");
		msg("MOD_CANCELAR");

		int opcion = Util.leerInt();

		switch (opcion) {
		case 1: // Cambiar nombre
			msg("NUEVO_NOMBRE");
			cliente.setNombre(Util.introducirCadena());
			break;

		case 2: // Cambiar apellido
			msg("NUEVO_APELLIDO");
			cliente.setApellido(Util.introducirCadena());
			break;

		case 3: // Cambiar teléfono
			msg("NUEVO_TELEFONO");
			cliente.setTelefono(Util.leerInt());
			break;

		case 4: // Cancelar
			msg("OPERACION_CANCELADA");
			return;

		default:
			msg("OPCION_INVALIDA");
			return;
		}

		// Si cambia nombre o apellido, se actualizan sus reservas
		if (!cliente.getNombre().equals(nombreAnterior) || !cliente.getApellido().equals(apellidoAnterior)) {
			actualizarNombreEnReservas(dni, cliente.getNombre(), cliente.getApellido());
		}

		guardarPersonas(); // Guarda cambios
		guardarReservas();

		msg("CLIENTE_MODIFICADO");
	}

	// Actualiza nombre y apellido en todas las reservas del cliente
	private static void actualizarNombreEnReservas(String dni, String nuevoNombre, String nuevoApellido) {
		for (Reserva r : reservas.values()) {
			if (r.getDnicli().equals(dni)) { // Reserva del cliente
				r.setNombreCli(nuevoNombre);
				r.setApellidoCli(nuevoApellido);
			}
		}
	}

	// ==================== GESTION DE EMPLEADOS ====================

	// Alta de un nuevo empleado
	private static void altaEmpleado() {
	    imprimirTitulo("ALTA DE EMPLEADO");

	    String dni = "";
	    boolean dniValido = false;

	    // Bucle para insistir hasta que el DNI sea correcto
	    while (!dniValido) {
	        msg("INTRODUCIR_DNI_EMPLEADO");
	        dni = Util.introducirCadena();

	        try {
	            validarDNI(dni); // Lanza DNIException si el formato es malo
	            
	            if (dniEmpleadoExiste(dni)) {
	                msg("EMPLEADO_EXISTE");
	                return; // Si ya existe, salimos porque no queremos duplicados
	            }
	            
	            dniValido = true; // Si llega aquí, es que no hubo excepción ni duplicado
	            
	        } catch (DNIException e) {
	            System.out.println(e.getMessage()); // Muestra "El DNI no tiene el formato correcto"
	            System.out.println("Por favor, inténtelo de nuevo.");
	        }
	    }

	// Datos del empleado
		msg("INTRODUCIR_NOMBRE_EMPLEADO");
		String nombre = Util.introducirCadena();

		msg("INTRODUCIR_APELLIDO_EMPLEADO");
		String apellido = Util.introducirCadena();

		// Selección del cargo
		Cargo cargo = null;
		boolean cargoValido = false;
		int opcionCargo;

		while (!cargoValido) {
			msg("INTRODUCIR_CARGO");
			opcionCargo = Util.leerInt();

			if (opcionCargo == 1) {
				cargo = Cargo.COMERCIAL;
				cargoValido = true;
			} else if (opcionCargo == 2) {
				cargo = Cargo.RECEPCIONISTA;
				cargoValido = true;
			} else if (opcionCargo == 3) {
				cargo = Cargo.MECANICO;
				cargoValido = true;
			} else {
				msg("CARGO_INVALIDO");
			}
		}

		// Crea y guarda el empleado
		Empleado nuevo = new Empleado(dni, nombre, apellido, cargo);
		personas.add(nuevo);

		msg("EMPLEADO_ANADIDO");
		System.out.println(nuevo);

		guardarPersonas(); // Guarda cambios
	}

	// Lista todos los empleados registrados
	private static void listarEmpleados() {
		imprimirTitulo("LISTADO DE EMPLEADOS");

		// Filtra solo empleados
		ArrayList<Empleado> empleados = new ArrayList<>();
		for (Persona p : personas) {
			if (p instanceof Empleado) {
				empleados.add((Empleado) p);
			}
		}

		if (empleados.size() == 0) {
			msg("NO_HAY_EMPLEADOS");
		} else {
			// Cabecera
			System.out.println();
			System.out.printf("%-12s %-20s %-20s %-15s%n", "DNI", "NOMBRE", "APELLIDO", "CARGO");
			imprimirSeparador();

			// Muestra cada empleado
			for (Empleado e : empleados) {
				System.out.printf("%-12s %-20s %-20s %-15s%n", e.getDni(), e.getNombre(), e.getApellido(),
						e.getCargo());
			}

			msg("TOTAL_EMPLEADOS");
		}
	}

	// Comprueba si un DNI ya pertenece a un empleado
	private static boolean dniEmpleadoExiste(String dni) {
		for (Persona p : personas) {
			if (p instanceof Empleado) {
				Empleado e = (Empleado) p;
				if (e.getDni().equalsIgnoreCase(dni)) {
					return true;
				}
			}
		}
		return false;
	}

	// Valida el formato del DNI mediante expresión regular
	private static void validarDNI(String dni) throws DNIException {
		Pattern modelo = Pattern.compile("\\d{8}[A-Za-z]");
		Matcher m = modelo.matcher(dni);

		if (!m.matches()) {
			throw new DNIException("El DNI no tiene el formato correcto.");
		}
	}

	// ==================== GESTION DE VEHICULOS ====================

	// Alta de un nuevo vehículo
	private static void altaVehiculo() {
		imprimirTitulo("ALTA DE VEHICULO");

		msg("TIPO_VEHICULO");
		int tipo = Util.leerInt(); // 1 coche, 2 moto

		if (tipo != 1 && tipo != 2) { // Tipo no válido
			msg("TIPO_INVALIDO");
			return;
		}

		msg("INTRODUCIR_MATRICULA");
		String matricula = Util.introducirCadena();

		// Evita matriculas duplicadas
		for (Vehiculo v : vehiculos) {
			if (v.getMatricula().equalsIgnoreCase(matricula)) {
				msg("VEHICULO_EXISTE");
				return;
			}
		}

		// Datos comunes
		msg("INTRODUCIR_MARCA");
		String marca = Util.introducirCadena();

		msg("INTRODUCIR_MODELO");
		String modelo = Util.introducirCadena();

		Vehiculo vehiculo;

		// Crea coche o moto según tipo
		if (tipo == 2) {
			msg("INTRODUCIR_CILINDRADA");
			int cilindrada = Util.leerInt();
			vehiculo = new Moto(matricula, marca, modelo, cilindrada);
		} else {
			msg("INTRODUCIR_PUERTAS");
			int puertas = Util.leerInt();
			vehiculo = new Coche(matricula, marca, modelo, puertas);
		}

		vehiculos.add(vehiculo); // Guarda en memoria
		msg("VEHICULO_ANADIDO");
		guardarVehiculos(); // Guarda en archivo
	}

	// Lista todos los vehículos registrados
	private static void listarVehiculos() {
		imprimirTitulo("LISTADO DE VEHICULOS");

		if (vehiculos.size() == 0) {
			msg("NO_HAY_VEHICULOS");
		} else {
			// Cabecera del listado
			System.out.println();
			System.out.printf("%-12s %-15s %-20s %-10s %-15s%n", "MATRICULA", "MARCA", "MODELO", "TIPO", "DETALLE");
			imprimirSeparador();

			// Muestra cada vehículo
			for (Vehiculo v : vehiculos) {
				String tipo, detalle;

				if (v instanceof Coche) {
					tipo = "Coche";
					detalle = ((Coche) v).getPuertas() + " puertas";
				} else {
					tipo = "Moto";
					detalle = ((Moto) v).getCilindrada() + " cc";
				}

				System.out.printf("%-12s %-15s %-20s %-10s %-15s%n", v.getMatricula(), v.getMarca(), v.getModelo(),
						tipo, detalle);
			}

			System.out.println("\nTotal de vehiculos: " + vehiculos.size());
		}
	}

	// ==================== GESTION DE RESERVAS ====================

	// Crea una nueva reserva
	private static void añadirReserva() {
		imprimirTitulo("NUEVA RESERVA");

		if (personas.size() == 0) { // No hay clientes
			msg("NO_HAY_CLIENTES_RESERVA");
			return;
		}

		if (vehiculos.size() == 0) { // No hay vehículos
			msg("NO_HAY_VEHICULOS_RESERVA");
			return;
		}

		// Lista clientes disponibles
		msg("CLIENTES_DISPONIBLES");
		System.out.printf("%-12s %-20s %-20s%n", "DNI", "NOMBRE", "APELLIDO");
		imprimirSeparador();

		for (Persona p : personas) {
			if (p instanceof Cliente) {
				Cliente c = (Cliente) p;
				System.out.printf("%-12s %-20s %-20s%n", c.getDni(), c.getNombre(), c.getApellido());
			}
		}

		String dni = solicitarDNIValido(); // Pide DNI
		Cliente cliente = buscarClientePorDni(dni);

		if (cliente == null) { // Cliente no existe
			msg("CLIENTE_NO_ENCONTRADO");
			return;
		}

		// Solicita fechas
		LocalDate fechaini = null;
		LocalDate fechafin = null;

		try {
			String textoFecha;
			LocalDate hoy = LocalDate.now();

			// Fecha inicio válida y posterior a hoy
			do {
				msg("INTRODUCIR_FECHA_INICIO");
				textoFecha = Util.introducirCadena();

				if (!textoFecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
					msg("FORMATO_FECHA_INCORRECTO");
					continue;
				}

				fechaini = LocalDate.parse(textoFecha);

				if (!fechaini.isAfter(hoy))
					msg("FECHA_POSTERIOR_HOY");

			} while (fechaini == null || !fechaini.isAfter(hoy));

			// Calcula fecha fin
			msg("INTRODUCIR_DIAS");
			int dias = Util.leerInt();
			fechafin = fechaini.plusDays(dias);

			System.out.println("Fecha fin calculada: " + fechafin);

			// Vehículos disponibles en ese rango
			ArrayList<Vehiculo> vehiculosDisponibles = obtenerVehiculosDisponibles(fechaini, fechafin);

			if (vehiculosDisponibles.isEmpty()) {
				msg("NO_HAY_VEHICULOS_FECHAS");
				return;
			}

			// Muestra vehículos disponibles
			System.out.println("\n--- VEHICULOS DISPONIBLES ---\n");
			System.out.printf("%-12s %-15s %-20s %-10s %-15s%n", "MATRICULA", "MARCA", "MODELO", "TIPO", "DETALLE");
			imprimirSeparador();

			for (Vehiculo v : vehiculosDisponibles) {
				String tipo, detalle;

				if (v instanceof Coche) {
					tipo = "Coche";
					detalle = ((Coche) v).getPuertas() + " puertas";
				} else {
					tipo = "Moto";
					detalle = ((Moto) v).getCilindrada() + " cc";
				}

				System.out.printf("%-12s %-15s %-20s %-10s %-15s%n", v.getMatricula(), v.getMarca(), v.getModelo(),
						tipo, detalle);
			}

			// Selección de vehículo por matrícula
			String matricula;
			Vehiculo vehiculoSeleccionado = null;

			do {
				msg("INTRODUCIR_MATRICULA_RESERVA");
				matricula = Util.introducirCadena();

				for (Vehiculo v : vehiculosDisponibles) {
					if (v.getMatricula().equalsIgnoreCase(matricula)) {
						vehiculoSeleccionado = v;
						break;
					}
				}

				if (vehiculoSeleccionado != null)
					break;

				msg("MATRICULA_NO_VALIDA");

			} while (true);

			// Crea la reserva
			Reserva r = new Reserva(nreservas, dni, cliente.getNombre(), cliente.getApellido(),
					vehiculoSeleccionado.getModelo(), fechaini, fechafin);

			cliente.aniadirReserva(r); // Añade al cliente
			reservas.put(r.getNreserva(), r); // Añade al sistema

			nreservas++; // Incrementa contador

			msg("RESERVA_CREADA");
			System.out.println("Su número de reserva es " + r.getNreserva());

			guardarReservas();
			guardarPersonas();

		} catch (DateTimeParseException e) {
			msg("FORMATO_FECHA_INCORRECTO");
		}
	}

	// Lista todas las reservas del sistema
	private static void listarReservas() {
		imprimirTitulo("LISTADO DE RESERVAS");

		if (reservas.size() == 0) {
			msg("NO_HAY_RESERVAS");
		} else {
			// Cabecera del listado
			System.out.println();
			System.out.printf("%-6s %-12s %-25s %-20s %-12s %-12s %-12s%n", "NUM", "DNI", "CLIENTE", "MODELO", "INICIO",
					"FIN", "ESTADO");
			imprimirSeparador();

			// Muestra cada reserva
			for (Reserva r : reservas.values()) {

				// Acorta nombre si es muy largo
				String nombreCompleto = r.getNombreCli() + " " + r.getApellidoCli();
				if (nombreCompleto.length() > 25) {
					nombreCompleto = nombreCompleto.substring(0, 22) + "...";
				}

				System.out.printf("%-6d %-12s %-25s %-20s %-12s %-12s %-12s%n", r.getNreserva(), r.getDnicli(),
						nombreCompleto, r.getModelo(), r.getFechaini(), r.getFechafin(), r.getEstado());
			}

			System.out.println("\nTotal de reservas: " + reservas.size());
		}
	}

	// Muestra las reservas de un cliente concreto
	private static void mostrarReservasCliente() {
		imprimirTitulo("RESERVAS DE CLIENTE");

		if (personas.size() == 0) { // No hay clientes
			msg("NO_HAY_CLIENTES_RESERVAS");
			return;
		}

		String dni = solicitarDNIValido(); // Pide DNI
		Cliente cliente = buscarClientePorDni(dni);

		if (cliente == null) { // Cliente no existe
			msg("CLIENTE_NO_ENCONTRADO");
			return;
		}

		cliente.listarReservas(); // Muestra sus reservas
	}

	// Anula una reserva del cliente
	private static void anularReserva() {
		imprimirTitulo("ANULAR RESERVA");

		if (personas.size() == 0) { // No hay clientes
			msg("NO_HAY_CLIENTES_ANULAR");
			return;
		}

		String dni = solicitarDNIValido(); // Pide DNI
		Cliente cliente = buscarClientePorDni(dni);

		if (cliente == null) { // Cliente no existe
			msg("CLIENTE_NO_ENCONTRADO");
			return;
		}

		cliente.listarReservas(); // Muestra sus reservas

		msg("INTRODUCIR_NUM_RESERVA");
		int nreserva = Util.leerInt();

		Reserva reservaAEliminar = reservas.get(nreserva); // Busca reserva

		// Comprueba que existe y pertenece al cliente
		if (reservaAEliminar == null || !reservaAEliminar.getDnicli().equals(dni)) {
			msg("RESERVA_NO_EXISTE");
			return;
		}

		// Elimina la reserva del cliente y del sistema
		cliente.eliminarReserva(reservaAEliminar);
		reservas.remove(nreserva);

		guardarReservas(); // Guarda cambios
		guardarPersonas();

		msg("RESERVA_ANULADA");
	}

	// ==================== METODOS AUXILIARES ====================

	// Devuelve los vehículos libres en un rango de fechas
	private static ArrayList<Vehiculo> obtenerVehiculosDisponibles(LocalDate fechaInicio, LocalDate fechaFin) {
		ArrayList<Vehiculo> disponibles = new ArrayList<>();

		for (Vehiculo v : vehiculos) {
			if (vehiculoDisponible(v.getMatricula(), fechaInicio, fechaFin)) {
				disponibles.add(v);
			}
		}
		return disponibles;
	}

	// Comprueba si un vehículo está libre entre dos fechas
	private static boolean vehiculoDisponible(String matricula, LocalDate fechaInicio, LocalDate fechaFin) {
		for (Reserva r : reservas.values()) {

			// Obtiene la matrícula asociada al modelo reservado
			String matriculaReserva = obtenerMatriculaPorModelo(r.getModelo());

			// Si coincide y las fechas se solapan, no está disponible
			if (matriculaReserva != null && matriculaReserva.equalsIgnoreCase(matricula)) {
				if (fechasSeSuperponen(fechaInicio, fechaFin, r.getFechaini(), r.getFechafin())) {
					return false;
				}
			}
		}
		return true;
	}

	// Busca la matrícula de un vehículo por su modelo
	private static String obtenerMatriculaPorModelo(String modelo) {
		for (Vehiculo v : vehiculos) {
			if (v.getModelo().equalsIgnoreCase(modelo)) {
				return v.getMatricula();
			}
		}
		return null;
	}

	// Comprueba si dos rangos de fechas se solapan
	private static boolean fechasSeSuperponen(LocalDate inicio1, LocalDate fin1, LocalDate inicio2, LocalDate fin2) {
		return !inicio1.isAfter(fin2) && !fin1.isBefore(inicio2);
	}

	// Busca un cliente por su DNI
	private static Cliente buscarClientePorDni(String dni) {
		for (Persona p : personas) {
			if (p instanceof Cliente) {
				Cliente c = (Cliente) p;
				if (dni.equalsIgnoreCase(c.getDni())) {
					return c;
				}
			}
		}
		return null;
	}

	// ==================== GESTION DE ARCHIVOS ====================

	// Carga todos los datos del sistema
	private static void cargarDatos() {
		cargarPersonas();
		cargarVehiculos();
		cargarReservas();
		reconstruirRelaciones(); // Reconecta clientes ↔ reservas
	}

	// Guarda todos los datos del sistema
	private static void guardarDatos() {
		guardarPersonas();
		guardarVehiculos();
		guardarReservas();
		System.out.println("Datos guardados correctamente");
	}

	// Carga la lista de personas desde archivo
	@SuppressWarnings("unchecked")
	private static void cargarPersonas() {
		if (!ficheroPersonas.exists())
			return;

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ficheroPersonas))) {
			personas = (ArrayList<Persona>) ois.readObject();
		} catch (Exception e) {
			System.out.println("Error al cargar personas");
		}
	}

	// Guarda personas con sistema seguro: aux → backup → dat
	private static void guardarPersonas() {
	    File dat = ficheroPersonas;
	    File aux = new File("personas.aux");
	    File backup = new File("personas.backup");

	    // PASO 1: Escribir y CERRAR el flujo obligatoriamente
	    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(aux))) {
	        oos.writeObject(personas);
	    } catch (Exception e) {
	        System.out.println("Error al escribir archivo temporal: " + e.getMessage());
	        return;
	    }

	    // PASO 2: Ahora que el flujo está cerrado, manipulamos los archivos
	    try {
	        // Si ya existe un backup antiguo, lo borramos para que no estorbe
	        if (backup.exists()) backup.delete();

	        // El original pasa a ser backup
	        if (dat.exists() && !dat.renameTo(backup)) {
	            System.out.println("Error: no se pudo crear el backup de personas");
	            return;
	        }

	        // El auxiliar pasa a ser el original
	        if (!aux.renameTo(dat)) {
	            System.out.println("Error al renombrar aux. Restaurando backup...");
	            if (backup.exists()) backup.renameTo(dat);
	            return;
	        }

	        // Borramos el backup si todo salió bien
	        Files.deleteIfExists(backup.toPath());

	    } catch (Exception e) {
	        System.out.println("Error en la gestión de archivos: " + e.getMessage());
	    }
	}

	// Carga vehículos desde archivo
	@SuppressWarnings("unchecked")
	private static void cargarVehiculos() {
		if (!ficheroVehiculos.exists())
			return;

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ficheroVehiculos))) {
			vehiculos = (ArrayList<Vehiculo>) ois.readObject();
		} catch (Exception e) {
			System.out.println("Error al cargar vehiculos");
		}
	}

	// Guarda vehículos con sistema seguro
	private static void guardarVehiculos() {
	    File dat = ficheroVehiculos;
	    File aux = new File("vehiculos.aux");
	    File backup = new File("vehiculos.backup");

	    // 1. ESCRIBIR LOS DATOS: Al salir de este bloque try, el archivo 'aux' se cierra automáticamente.
	    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(aux))) {
	        oos.writeObject(vehiculos);
	    } catch (Exception e) {
	        System.out.println("Error al escribir archivo temporal de vehículos: " + e.getMessage());
	        return;
	    }

	    // 2. GESTIÓN DE ARCHIVOS: Ahora que el archivo está libre, podemos renombrar.
	    try {
	        // Borramos backup viejo si existe para evitar errores de colisión
	        if (backup.exists()) backup.delete();

	        // El original pasa a ser backup
	        if (dat.exists() && !dat.renameTo(backup)) {
	            System.out.println("Error: no se pudo crear el backup de vehículos");
	            return;
	        }

	        // El auxiliar pasa a ser el oficial
	        if (!aux.renameTo(dat)) {
	            System.out.println("Error al renombrar aux de vehículos. Restaurando backup...");
	            if (backup.exists()) backup.renameTo(dat);
	            return;
	        }

	        // Si todo ha ido bien, borramos el backup
	        Files.deleteIfExists(backup.toPath());

	    } catch (Exception e) {
	        System.out.println("Error crítico en la gestión de archivos de vehículos: " + e.getMessage());
	    }
	}

	// Carga reservas desde archivo
	@SuppressWarnings("unchecked")
	private static void cargarReservas() {
		if (!ficheroReservas.exists())
			return;

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ficheroReservas))) {

			reservas = (TreeMap<Integer, Reserva>) ois.readObject();

			if (!reservas.isEmpty()) {
				nreservas = reservas.lastKey() + 1; // Actualiza contador
			}

		} catch (Exception e) {
			System.out.println("Error al cargar reservas");
		}
	}

	// Guarda reservas con sistema seguro
	private static void guardarReservas() {
	    File dat = ficheroReservas;
	    File aux = new File("reservas.aux");
	    File backup = new File("reservas.backup");

	    // 1. ESCRIBIR LOS DATOS (El flujo se cierra al salir de la llave del try)
	    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(aux))) {
	        oos.writeObject(reservas);
	    } catch (Exception e) {
	        System.out.println("Error al escribir el archivo temporal de reservas: " + e.getMessage());
	        return;
	    }

	    // 2. GESTIÓN DE ARCHIVOS (Con el archivo ya cerrado y libre)
	    try {
	        // Limpiamos backup antiguo si quedó alguno por un fallo previo
	        if (backup.exists()) backup.delete();

	        // El original pasa a ser backup (dat -> backup)
	        if (dat.exists() && !dat.renameTo(backup)) {
	            System.out.println("Error: no se pudo crear el backup de reservas");
	            return;
	        }

	        // El auxiliar pasa a ser el archivo real (aux -> dat)
	        if (!aux.renameTo(dat)) {
	            System.out.println("Error al renombrar aux de reservas. Restaurando backup...");
	            if (backup.exists()) backup.renameTo(dat);
	            return;
	        }

	        // Si llegamos aquí, todo ha salido bien: borramos el backup
	        Files.deleteIfExists(backup.toPath());

	    } catch (Exception e) {
	        System.out.println("Error crítico en la gestión de archivos de reservas: " + e.getMessage());
	    }
	}

	// Reconstruye cliente ↔ reservas tras cargar archivos
	private static void reconstruirRelaciones() {

		if (personas == null)
			personas = new ArrayList<>();
		if (reservas == null)
			reservas = new TreeMap<>();

		// Limpia reservas previas de cada cliente
		for (Persona p : personas) {
			if (p instanceof Cliente) {
				((Cliente) p).limpiarReservas();
			}
		}

		// Vuelve a asignar reservas a sus clientes
		for (Reserva r : reservas.values()) {
			for (Persona p : personas) {
				if (p instanceof Cliente) {
					Cliente c = (Cliente) p;
					if (r.getDnicli().equals(c.getDni())) {
						c.aniadirReserva(r);
					}
				}
			}
		}
	}

	// Muestra un mensaje según idioma
	private static void msg(String etiqueta) {
		String[] lineas = traducciones.get(etiqueta);

		if (lineas == null) {
			System.out.println("[" + etiqueta + "[");
			return;
		}

		if (idiomaSeleccionado == 2)
			System.out.println(lineas[1]);
		else
			System.out.println(lineas[0]);
	}

	// Carga etiquetas de traducción desde archivo
	private static void cargarTraducciones() {
		File archivo = new File("traducciones.txt");

		try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {

			String linea;
			while ((linea = br.readLine()) != null) {

				if (linea.startsWith("#")) { // Nueva etiqueta
					String etiqueta = linea.substring(1).trim();

					String esp = br.readLine(); // Español
					String eng = br.readLine(); // Inglés

					traducciones.put(etiqueta, new String[] { esp, eng });
				}
			}

		} catch (IOException e) {
			System.out.println("Error al cargar traducciones.");
		}
	}
}