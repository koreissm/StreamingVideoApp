package com.streamingvideo.classes;

	/*  Creation Date : 10/06/2016
		Creators : Rayan DINAR, Erwan FOUACHE, Mouhamad KOREISSI
		Description : CClass to download the requested video
	*/

import android.os.Environment;
import android.util.Log;

import com.streamingvideo.activities.MainActivity;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class DownloadVideo {
    private Socket client;

        public DownloadVideo() {}

        public void connexion(String idTeacher) {
            try {
                client = new Socket(MainActivity.IP, MainActivity.SERVERPORT);

                if (client != null) {
                    String str = "etu:" + idTeacher;
                    PrintWriter out1 = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(client.getOutputStream())),
                            true);
                    out1.println(str);
                    // Received video
                    InputStream in = client.getInputStream();
                    OutputStream out[] = new OutputStream[1];
                    // Store on device
                    out[0] = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies/DLVideo.mp4");
                    byte buf[] = new byte[1024];
                    Log.d("Download" , "Downloading");
                    int n;
                    while ((n = in.read(buf)) != -1) {
                        out[0].write(buf, 0, n);
                        Log.d("byte" , "" + out);
                    }
                    Log.d("Download" , "Download done");
                    in.close();
                    out[0].close();

                    client.close();
                } else {
                    System.out.println("Pas de serveur lancé sur le port " + client.getLocalPort() + " ...");
                }
            } catch (UnknownHostException e) {
                System.out.println("Impossible de se connecter au serveur " + MainActivity.IP);
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Impossible de se connecter au serveur" + MainActivity.IP);
                e.printStackTrace();
            }
        }
}
