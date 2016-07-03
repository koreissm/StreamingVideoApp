package com.streamingvideo.classes;

	/*  Creation Date : 10/06/2016
		Creators : Rayan DINAR, Erwan FOUACHE, Mouhamad KOREISSI
		Description : Class to read the requested video
	*/
	

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;

import com.streamingvideo.R;
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

public class ReadVideo {
    private Context context;

    public static final String EXTRA_VIDEO_LIST = "video_list";
    //public static final String EXTRA_POSITION = "position";

    public ReadVideo(Context context) {
        this.context = context;
    }

    public void launchVideo() {
        try{
            // Video Path
            Uri videoUri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies/DLVideo.mp4");

            // New activity for viewing data ( in our case video)
            Intent intent = new Intent(Intent.ACTION_VIEW);

            // We specify the data of the activity and the MIME (eg application, text, image, audio, etc...)
            intent.setDataAndType(videoUri, "application/*");
            
            intent.putExtra(EXTRA_VIDEO_LIST, new Parcelable[] {videoUri});    // Permet d'éviter de lire toutes les vidéos du dossier du chemin envoyé
        
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Log.d("Read", "finish");
        }
        catch( ActivityNotFoundException e2){
            MainActivity.displayToast(context, context.getResources().getString(R.string.error_unknownMX)); // Erreur, on affiche un message à l'utilisateur
            Log.e( "Error", context.getResources().getString(R.string.error_unknownMX));
        }

    }
}
