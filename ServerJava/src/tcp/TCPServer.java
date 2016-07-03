package tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

	/*  Creation Date : 21/06/2016
		Creators : Rayan DINAR, Erwan FOUACHE, Mouhamad KOREISSI
		Description : TCP Server 
	*/

public class TCPServer {

	public static void main(String[] args) throws IOException {
		String clientSentence = null;
		
		ServerSocket welcomeSocket = new ServerSocket (9002);
		
		boolean bool = true;
		Socket connectionSocket = welcomeSocket.accept();
		while(bool) {
			
			BufferedReader inFromClient =
					new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			clientSentence = inFromClient.readLine();
			System.out.println("Received: " + clientSentence);
			//bool = false;
			
		}
		System.out.println(clientSentence);

	}

}
