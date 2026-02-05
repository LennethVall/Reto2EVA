package clases;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Util {

	public static String introducirCadena(){
		 String cadena="";
		 InputStreamReader entrada =new InputStreamReader(System.in);
		 BufferedReader teclado= new BufferedReader(entrada);
		try {
			cadena=teclado.readLine();
		} catch (IOException e) {
			System.out.println("Error en la entrada de datos");
		}
		 return cadena;
	}

	
	public static char leerChar(){
		boolean error=false;
		String letra;
		
		do{
			error=false;
			letra=introducirCadena();
			if(letra.length()!=1){
				System.out.println("Error, introduce solo un carácter: ");
				error=true;
			}
			
		}while(error);
		return letra.charAt(0);
	}

	
	
	public static int leerInt(){
		int num=0;
		boolean error;
		do{
			error=false;
			try{
				num=Integer.parseInt(introducirCadena());
			}
			catch(NumberFormatException e){
				System.out.println("Error al introducir el número. Introduce un número sin decimales");
				error=true;
			}
		}while(error);
		return num;
	}



	public static float leerFloat(){
		float num=0;
		boolean error;
		do{
			error=false;
			try{
				num=Float.parseFloat(introducirCadena());
			}
			catch(NumberFormatException e){
				System.out.println("Error al introducir el número. Introduce un número (puede llevar decimales (Ejemplo 5.3)):");
				error=true;
			}
		}while(error);
		return num;
	}

	
	
	

	public static double leerDouble() {
		double fNumero = 0;
		boolean ok;
		do {
			try {
				ok = true;
				fNumero = Double.parseDouble(introducirCadena());
			} catch (NumberFormatException e) {
				System.out.println("Error al introducir el número. Introduce un número (puede llevar decimales (Ejemplo 5.3)):");
				ok = false;
			}
		} while (!ok);
		return fNumero;
	}

}