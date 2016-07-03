package tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

	/*  Creation Date : 05/02/2016
		Creators : Rayan DINAR, Erwan FOUACHE, Mouhamad KOREISSI
		Description : Video sending class
	*/

public class Util {
    public static void transfertVideo(InputStream in, OutputStream out, Socket socket) throws IOException{
        byte buf[] = new byte[1024];
        
        int n;
        while((n=in.read(buf))!=-1){
	        if (socket.isConnected()) {	
        		out.write(buf, 0, n);
	        	try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					
				}
	        }
	        else
	        	break;
        }
    
        in.close();
        out.close();
        socket.close();
    }
}