package Act4_Sockets;
import java.io.*;
import java.net.*;

public class Cliente
{
	public static void main(String[] args)
	{
		int puerto = Integer.parseInt(args[0]); // Puerto del server
		String palabra_parar_a_cliente = args[1]; // Palabra clave de cierre del cliente
		
		try
		{
			// Crear conexión con el servidor (localhost)
			Socket socket = new Socket("127.0.0.1", puerto);
			System.out.println("Cliente chat en puerto " + puerto + "... OK");
			System.out.println("Iniciando cliente... OK");
			
			// Entrada por teclado (lo que escribe el usuario)
			BufferedReader entrada_cliente = new BufferedReader(new InputStreamReader(System.in));
			// Salida hacia el servidor
			PrintWriter salida_cliente = new PrintWriter(socket.getOutputStream(), true);
			// Entrada desde el servidor (mensajes recibidos)
			BufferedReader respuesta_servidor = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			// Bucle de mensajes cliente-servidor
			while (true)
			{
				// Pedir mensaje al usuario
				System.out.print("#Enviar al servidor: ");
				String ent_cliente = entrada_cliente.readLine();
				
				// Si el cliente escribe la palabra clave, se envía y se cierra
				if (ent_cliente.contains(palabra_parar_a_cliente)) 
				{
					System.out.println("Client keyword detected!");
					salida_cliente.println(ent_cliente);
					break;
				}
				
				// Enviar mensaje al servidor
				salida_cliente.println(ent_cliente);
				System.out.println("Enviando mensaje... OK");
				
				// Recibir respuesta del servidor
				String res_servidor = respuesta_servidor.readLine();
				
				// Si el servidor se ha cerrado, salir
				if (res_servidor == null)
				{
					System.out.println("Servidor cerrado.");
					break;
				}
				
				// Mostrar respuesta del servidor
				System.out.println("#Rebut del servidor: " + res_servidor);
				
				// Si el server envía la palabra clave, cerrar conexión
				if (res_servidor.equals(palabra_parar_a_cliente)) 
				{
					System.out.println("Server keyword detected!");
					break;
				}
			}
			
			// Cierre de recursos
			System.out.println("Closing chat... OK");
			entrada_cliente.close();
			salida_cliente.close();
			respuesta_servidor.close();
			
			// Cierre del socket
			socket.close();
			System.out.println("Closing client... OK");
			System.out.println("Bye!");
			
		} catch (IOException e) { e.printStackTrace(); }
	}
}