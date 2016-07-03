package com.streamingvideo.classes;

	/*  Creation Date: 19/06/2016
		Creators : Rayan DINAR, Erwan FOUACHE, Mouhamad KOREISSI
		Description : Class to the launch the two threads (DownLoadVideo and ReadVideo)
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

public class ExecutorServiceTest {
    private Context context;

    public static final String EXTRA_VIDEO_LIST = "video_list";
    //public static final String EXTRA_POSITION = "position";

    public ExecutorServiceTest(Context context){
        this.context = context;
    }

    public void test2(final String idTeacher){
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("T1","Starting T1");
                DownloadVideo dv = new DownloadVideo();
                dv.connexion(idTeacher);
            }
        });
        t1.start();
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("T2", "Starting T2");
                try {
                    Thread.sleep(5000);
                    ReadVideo rv = new ReadVideo(context);
                    rv.launchVideo();
                }
                catch (InterruptedException e) {e.printStackTrace();}
            }
        });
        t2.start();
    }
}
