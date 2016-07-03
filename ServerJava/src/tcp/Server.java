package tcp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

	/*  Creation Date : 04/02/2016
		Creators : Rayan DINAR, Erwan FOUACHE, Mouhamad KOREISSI
		Description : Multiconnexion TCP Server 
	*/

public class Serveur {
	private static String out = "";
    public static void main(String[] args) throws IOException 
    { 
    	// Init Socket
    	ServerSocket serverSocket = null;
    	Socket socket = null;
    	//System.setOut(new PrintStream((new FileOutputStream("output.txt"))));
    	
    	try {
    		serverSocket = new ServerSocket(Constants.PORT);
    	}
    	catch (IOException e){
    		e.printStackTrace();
    	}
    	
    	while (true) {
    		try{
    			socket = serverSocket.accept();
 
    		}
    		catch (IOException e) {
    			e.printStackTrace();
    		}
    		
    		System.out.println("Client " + socket.getInetAddress() + " connecté !");
    		new DiffusionThread(socket).start();;
    		System.out.println("Diffusion");
    		System.out.println("Run");
    	}
    } 
}
