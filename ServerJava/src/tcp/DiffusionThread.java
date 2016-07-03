package tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.Scanner;

	/*  Creation Date : 24/02/2016
		Creators : Rayan DINAR, Erwan FOUACHE, Mouhamad KOREISSI
		Description : Videotape
                      Recovery Video
                      Sends Video
	*/

public class DiffusionThread extends Thread{
	
	private Socket socket;
	private String out;
	private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	private String clientSentence;

	public DiffusionThread (Socket socket) {
		this.socket = socket;
	}

	public void run () {
		// Send video
		boolean isConnected = true;
		//If client connnected
		while(isConnected) {
			try {
	        	String temp;
	        	BufferedReader inFromClient =
						new BufferedReader(new InputStreamReader(socket.getInputStream()));
				DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
				
				clientSentence = inFromClient.readLine();
				//Initialization with the video error if the requested video is unavailable
				String sendFile = "error.mp4";
				try {
					//If it detects a prof message type
					if(clientSentence.contains("prof:")){
						//Decomposition Message
						String[] sentenceSplit = clientSentence.split(":");
						String existingAskShare = sentenceSplit[0] + ":" + sentenceSplit[1];
						
						File entree = new File("output.txt");
						File sortie = new File("temp.txt");
						BufferedReader br = new BufferedReader(new FileReader(entree));
						BufferedWriter bw = new BufferedWriter(new FileWriter(sortie));
						String ligne="";
						boolean isExist = false;
						while ((ligne = br.readLine()) != null){
							//The new request is written if there is a professor messages already present
							if(ligne.startsWith(existingAskShare)){
							     bw.write(clientSentence +"\n");
							     bw.flush();
							     isExist = true;
							}else{
								//We rewrite the file
							     bw.write(ligne+"\n");
							     bw.flush();
							}
						}
						//If he found no line, written at the end of the file
						if (!isExist) {
							bw.write(clientSentence+"\n");
						    bw.flush();
						}
						bw.close();
						br.close();
						
						entree.delete();
						sortie.renameTo(new File("output.txt"));
					}
					//If it is a student message type (form: etu : id)
					else{
						File entree = new File("output.txt");
						//Decomposition message
						String[] sentenceSplit = clientSentence.split(":");
						String existShare = ":" + sentenceSplit[1] + ":";
						
						BufferedReader br = new BufferedReader(new FileReader(entree));
						String line;
						boolean isExist = false;
						//File path to see if the professor has launched a diffusion
						while ((line = br.readLine()) != null && !isExist){
							//If we find the teacher, we give the name of the video that the teacher wants to broadcast
							if(line.contains(existShare)){
								isExist = true;
								String[] lineSplit = line.split(":");
								sendFile = lineSplit[1] +"/"+ lineSplit[2];
							}
						}
						br.close();
						//If there is a video, it sends the selected video
						if (isExist) {
								Util.transfertVideo(
								 new URL("http://localhost/videoamademande/videosToStore/" + sendFile).openStream(),
						         socket.getOutputStream(),
						         socket
						         );
								isConnected = false;
						}
						//Otherwise we send the error video
						else {
							Util.transfertVideo(
									 new URL("http://localhost/videoamademande/videosToStore/" + sendFile).openStream(),
							         socket.getOutputStream(),
							         socket
							         );
									isConnected = false;
						}
					}
				} catch (NullPointerException e) {
					System.out.println("No message Received");
					isConnected = false;
				} catch (FileNotFoundException e) {
					sendFile = "error.mp4";
					Util.transfertVideo(
							 new URL("http://localhost/videoamademande/videosToStore/" + sendFile).openStream(),
					         socket.getOutputStream(),
					         socket
					         );
				}
				
			} catch (SocketException e) {
	        	System.out.println("\nClient " + socket.getInetAddress() +" déconnecté !");
	        	isConnected = false;
	        } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isConnected = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isConnected = false;
			}
		}
	}
}
	


