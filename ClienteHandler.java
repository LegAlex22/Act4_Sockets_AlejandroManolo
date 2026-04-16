package Act4_Sockets;
import java.io.*;
import java.net.*;

public class ClienteHandler implements Runnable
{
	private Socket socket; // Socket asociado a este cliente
	private String palabra_parar_a_servidor; // Palabra clave para cerrar la conexión
	private static int contador = 0; // Contador global para asignar un ID único a cada cliente
	private int id; // ID del cliente actual
	
	public ClienteHandler(Socket socket, String palabra_parar_a_servidor)
	{
		this.socket = socket;
		this.palabra_parar_a_servidor = palabra_parar_a_servidor;
		this.id = ++contador; // Es un contador para cuando muestre a que Cliente se refiere.
	}
	
	public void run()
	{
		try
		{
			System.out.println("[Cliente " + id + "] Iniciando chat... OK");
			
			// Entrada por teclado del servidor (para responder al cliente)
			BufferedReader entrada_servidor = new BufferedReader(new InputStreamReader(System.in));
			// Entrada de datos desde el cliente
			BufferedReader respuesta_cliente = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// Salida de datos hacia el cliente
			PrintWriter salida_servidor = new PrintWriter(socket.getOutputStream(), true);
			
			// Bucle de comunicación con el cliente
			while (true)
			{
				// Si el servidor ha decidido cerrar, cerrar todos los hilos
				if (Servidor.cerrar_server)
				{
					try { socket.close(); } 
					catch (IOException e) {}	
					return; 
				}
				
				// Recibir mensaje del cliente
				String res_cliente;
				try { res_cliente = respuesta_cliente.readLine();} 
				catch (IOException e)
				{
					System.out.println("[Cliente " + id + "] conexión cerrada.");
					break;
				}
				
				// Si el cliente se ha desconectado correctamente
				if (res_cliente == null)
				{
					System.out.println("[Cliente " + id + "] desconectado.");
					break;
				}
				
				// Mostrar mensaje recibido del cliente
				System.out.println("[Cliente " + id + "] dice: " + res_cliente);
				
				// Si el cliente envía la palabra clave, se cierra la conexión sin responder
				if (res_cliente.contains(palabra_parar_a_servidor)) 
				{
					System.out.println("[Cliente " + id + "] desconectado.");
					
					// Cierre de recursos asociados al cliente
					respuesta_cliente.close();
					salida_servidor.close();
					socket.close();
					
					break;
				}
				
				// Pedir respuesta del servidor
				System.out.print("[Cliente " + id + "] responder: ");
				String ent_servidor = entrada_servidor.readLine();
				
				// Comprobar si se detecta la palabra clave servidor | Si el servidor escribe la palabra clave, se cerrará todo el sistema
				if (ent_servidor.contains(palabra_parar_a_servidor))
				{
					System.out.println("[Servidor] keyword detectada!");
					Servidor.cerrar_server = true;
					
					//Cierra todas las conexiones con los clientes conectados
					for (Socket s : Servidor.listaSockets)
					{
						try { s.close(); } 
						catch (IOException e) {}
					}
				}
				
				// Comprobar que el socket sigue abierto antes de enviar
				if (socket.isClosed()) { break; }
				
				// Enviar respuesta al cliente
				salida_servidor.println(ent_servidor);
				System.out.println("[Cliente " + id + "] Enviado OK");
			}
			
			
			// Cierre final de recursos
			respuesta_cliente.close();
			salida_servidor.close();
			System.out.println("[Cliente " + id + "] Closing chat... OK");
			
			Servidor.clientes_actuales--; // Reducir contador de clientes activos
			
			// Si no quedan clientes UNA VEZ HABIDO 1 ANTERIORMENTE (BOOLEAN TRUE), se cierra el servidor
			if (Servidor.clientes_actuales == 0 && Servidor.clientes_conectados)
			{
				System.out.println("No hay clientes conectados. Cerrando servidor...");
				System.exit(0);
			}
			
			// Mostrar número actual de clientes conectados, informativo pero guay.
			System.out.println("Clientes actuales: " + Servidor.clientes_actuales);
			
			socket.close();
			
		}
		catch (IOException e) { e.printStackTrace(); }
	}
}

/*
PORQUE HAY QUE DAR A ENTER CUANDO UN CLIENTE SE VA.
Cuando el servidor usa entrada por teclado en un entorno multicliente, la lectura con readLine() es bloqueante. Por eso, aunque un cliente se desconecte, el servidor puede quedar esperando entrada del teclado y es necesario pulsar Enter para continuar el flujo.”

CUANDO CREO EL CLIENT3, ESE TERMINAL ESTA ROTO, HAY QUE CREAR OTRO. COMO SI FUERA UN 3.B
Cuando el servidor alcanza el máximo de clientes, rechaza nuevas conexiones. Si después se libera un cliente, es necesario crear una nueva conexión, ya que la anterior fue cerrada.


ENTONCES TENDRIA LO SIGUIENTE:
SERVIDOR
CLIENTE1 - DESCONECTADO (UNA VEZ HAYA SALIDO AVISO DE MAX)
CLIENTE2 - PERMITE ESCRIBIR
CLIENTE3.A - NO TOCAR ESTA ROTO (EJECUTA OTRA VEZ EL RUN Y TE CREARÁ EL DE ABAJO EL 3.B)
CLIENTE3.B - PERMITE ESCRIBIR

*/