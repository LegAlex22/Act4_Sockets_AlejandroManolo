package Act4_Sockets;
import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class Servidor
{
	public static int clientes_actuales = 0; // Número de clientes conectados actualmente
	public static boolean cerrar_server = false; // Indicar si el servidor debe cerrarse
	public static boolean clientes_conectados = false; // Indica si en algún momento ha habido clientes conectados, para el cierre de conexión si se van todos los clientes.
	public static ArrayList<Socket> listaSockets = new ArrayList<>(); //Array para indicar todos los clientes conectados, para desconectarlos todos cuando detecte palabra clase servidor.

	
	public static void main(String[] args)
	{
		int puerto = Integer.parseInt(args[0]); // Puerto
		String palabra_parar_a_servidor = args[1]; // Palabra clave de cierre server
		int max_clientes = Integer.parseInt(args[2]); // Numero maximo de clientes conectados simultaneamente
		
		try
		{
			// Crear el socket del servidor en el puerto indicado
			ServerSocket servidor = new ServerSocket(puerto);
			System.out.println("Servidor chat en puerto " + puerto + "... OK");
			System.out.println("Iniciando servidor... OK");
			
			// Bucle pa siempre para aceptar conexiones
			while (true)
			{
				System.out.println("Servidor esperando conexión... OK");
				Socket socket = servidor.accept(); //// Espera (bloqueante) hasta que un cliente se conecte
				listaSockets.add(socket);
				System.out.println("Cliente conectado: " + socket.getInetAddress()); // Muestra la IP del cliente que se ha conectado, util pero aqui meh
				
				// Comprobar si se ha alcanzado el máximo de clientes conectados simultáneamente.
				if (clientes_actuales < max_clientes) 
				{
					System.out.println("Conectando a: " + socket.getInetAddress());
					
					// Crear un manejador para el cliente
					ClienteHandler cliente = new ClienteHandler(socket, palabra_parar_a_servidor);
					
					// Crear un hilo para atender al cliente de forma independiente
					Thread hilo = new Thread(cliente);
					hilo.start(); 
					
					clientes_actuales++; // Incrementar contador de clientes
					clientes_conectados = true; // Indicar que ya ha habido clientes conectados, para asi entrar al condicional de cerrar server si todos se piran.
				}
				else
				{
					// Si se supera el máximo, se rechaza la conexión
					System.out.println("Maximo de clientes alcanzado. Conexion rechazada.");
					socket.close();
				}
			}
			
		} catch (IOException e) { e.printStackTrace(); }
	}
}