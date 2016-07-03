package com.streamingvideo.classes;

	/*  Creation Date : 19/06/2016
		Creators : Rayan DINAR, Erwan FOUACHE, Mouhamad KOREISSI
		Description : Class to know the state of the Internet connection
	*/

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.streamingvideo.R;
import com.streamingvideo.activities.MainActivity;

public class ConnectionDetector {

    private Context context;

    public ConnectionDetector(Context context){
        this.context = context;
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)  return true;
        }
        displayError();
        return false;
    }

    public void displayError(){
        MainActivity.displayToast(context,context.getResources().getString(R.string.error_connection));
    }
}