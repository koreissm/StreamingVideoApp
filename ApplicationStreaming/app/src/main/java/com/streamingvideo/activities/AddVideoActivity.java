package com.streamingvideo.activities;

	/*  Creation date : 18/06/2016
		Créators : Rayan DINAR, Erwan FOUACHE, Mouhamad KOREISSI
		Description : Activity to add a video
                      which will be connected to the teacher on the server
                      (Upload video of + db link)
	*/

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.streamingvideo.R;
import com.streamingvideo.ServiceHandler;
import com.streamingvideo.classes.AndroidMultiPartEntity;
import com.streamingvideo.classes.RealPathUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.streamingvideo.classes.AndroidMultiPartEntity.ProgressListener;


public class AddVideoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout drawer;

    private Button validate;
    private Button select;
    private TextView titleVideo;
    private TextView txtPercentage;
    private ProgressBar progressBar;

    public static int SELECT_VIDEO = 0;

    private AddVideoAsyncTask addVideo;
    private String urlAddVideo = MainActivity.urlServer + "addVideo.php";
    private String urlUploadVideo = MainActivity.urlServer + "uploadVideo.php";

    public static Boolean videoAdded;
    private Boolean isOkUpload;
    private String message;

    private String realPath;
    private long totalSize = 0;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 200; // On s'en fout de la valeur, c'est toi qui l'as définie

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_teacher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);

        RelativeLayout layout_teacher = (RelativeLayout)findViewById(R.id.layout_teacher) ;
        RelativeLayout layout_listVideo = (RelativeLayout)findViewById(R.id.listVideoView_teacher);
        RelativeLayout layout_removeVideo = (RelativeLayout)findViewById(R.id.removeVideoView_teacher);
        layout_teacher.removeView(layout_listVideo);
        layout_teacher.removeView(layout_removeVideo);

        select = (Button)findViewById(R.id.select_video);
        validate = (Button)findViewById(R.id.add_video_ok);
        titleVideo = (TextView)findViewById(R.id.title_video);
        txtPercentage = (TextView)findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar)findViewById(R.id.progressBar_upload);
        View v = navigationView.getHeaderView(0);
        TextView profile_name = (TextView) v.findViewById(R.id.profile_name_teacher);
        profile_name.setText(profile_name.getText().toString() + " - " + MainActivity.sharedpreferences.getString("login",""));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    MainActivity.setPreferences("permissionToRead", true);
                    //reload my activity with permission granted or use the features what required the permission
                }
                else{
                    MainActivity.displayToast(getApplicationContext(),getResources().getString(R.string.error_permission));
                }
            }
        }
    }

    protected void selectVideo(View v){
        if(validate.getVisibility()== View.VISIBLE)    validate.setVisibility(View.GONE);
        if(progressBar.getVisibility() == View.VISIBLE) progressBar.setVisibility(View.GONE);

        int permissionCheckReadStorage = ContextCompat.checkSelfPermission(AddVideoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheckReadStorage != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        if(permissionCheckReadStorage == PackageManager.PERMISSION_GRANTED){
            Intent myintent;

            if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
                myintent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            else
                myintent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI);

            myintent.setType("video/*");
            myintent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(myintent, SELECT_VIDEO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent myIntent) {
        if(requestCode == SELECT_VIDEO && resultCode == RESULT_OK){
            if(myIntent.getData()!=null){
                if (Build.VERSION.SDK_INT < 11) // SDK < API11
                    realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, myIntent.getData());
                else if (Build.VERSION.SDK_INT < 19)    // SDK >= 11 && SDK < 19
                    realPath = RealPathUtil.getRealPathFromURI_API11to18(this, myIntent.getData());
                else {    // SDK > 19 (Android 4.4)
                        realPath = RealPathUtil.getRealPathFromURI_API19(this, myIntent.getData());
                }

                String fileName = realPath.substring(realPath.lastIndexOf('/') + 1);
                titleVideo.setText(fileName);
            }
            else{
                MainActivity.displayToast(getApplicationContext(), getResources().getString(R.string.error_selectedFile));
            }
            if(titleVideo.getText().toString().equals(getResources().getString(R.string.no_video_selected))) MainActivity.displayToast(getApplicationContext(),getResources().getString(R.string.no_video_selected));
            else validate.setVisibility(View.VISIBLE);
        }
        else    MainActivity.displayToast(getApplicationContext(), "Aucune vidéo sélectionnée" );
    }
    File fileToUpload;
    protected void addVideo(View v){
        MainActivity.isInternetPresent = MainActivity.connectionDetector.isConnectingToInternet();
        if(MainActivity.isInternetPresent) {
            fileToUpload = new File(realPath);
            addVideo = new AddVideoAsyncTask(MainActivity.sharedpreferences.getInt("userID", -1), titleVideo.getText().toString());
            addVideo.execute();
        }
        else MainActivity.connectionDetector.displayError();
    }

    private class AddVideoAsyncTask extends AsyncTask<Void, Integer, Void> {

        private int teacherID;
        private String titleVideoString;

        public AddVideoAsyncTask(int teacherID, String titleVideoString){
            this.teacherID = teacherID;
            this.titleVideoString = titleVideoString;
        }

        public void uploadTest(){
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urlUploadVideo);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(realPath);

                // Adding file data to http body
                entity.addPart("uploaded_file", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("teacherID", new StringBody(String.valueOf(MainActivity.sharedpreferences.getInt("userID",-1))));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                    JSONObject result = null; //Convert String to JSON Object
                    try {
                        result = new JSONObject(responseString);
                        if(result.getInt("success")==1) isOkUpload = true;
                        else{
                            isOkUpload = false;
                            message = result.getString("message");
                        }
                    }
                    catch (JSONException e) {e.printStackTrace();}
                }
                else {
                    message = getResources().getString(R.string.error_http) + statusCode;
                    isOkUpload = false;
                }
            }
            catch (ClientProtocolException e) {e.printStackTrace();}
            catch (IOException e) {message = e.toString();}
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setProgress(0);
            validate.setEnabled(false);
            select.setEnabled(false);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressBar.setVisibility(View.VISIBLE);    // Making progress bar visible
            txtPercentage.setVisibility(View.VISIBLE);
            progressBar.setProgress(progress[0]);   // updating progress bar value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }


        @Override
        protected Void doInBackground(Void... params) {

            try {
                URL url = new URL(MainActivity.urlServer+"/videosToStore/"+teacherID+"/"+titleVideoString);
                HttpURLConnection httpconn = (HttpURLConnection)url.openConnection();
                if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    message = getResources().getString(R.string.error_exists);
                    videoAdded = false;
                }
                else{
                    uploadTest();
                    if(isOkUpload){
                        try{
                            ServiceHandler sh = new ServiceHandler();
                            List<NameValuePair> valuesToSend = new ArrayList<NameValuePair>(2);
                            valuesToSend.add(new BasicNameValuePair("teacherID", String.valueOf(teacherID)));
                            valuesToSend.add(new BasicNameValuePair("titleVideo", titleVideoString));

                            String jsonStr2 = sh.makeServiceCall(urlAddVideo, ServiceHandler.POST, valuesToSend);
                            Log.d("Response",jsonStr2);
                            if (jsonStr2 != null) {
                                try {
                                    JSONObject jsonObj2 = new JSONObject(jsonStr2);
                                    int success = jsonObj2.getInt("success");

                                    if(success == 1)    videoAdded = true;
                                    else{
                                        videoAdded = false;
                                        message = jsonObj2.getString("message");
                                    }
                                }
                                catch (JSONException e) {e.printStackTrace();}
                            }
                        }
                        catch (Exception e){e.printStackTrace();}
                    }
                    else videoAdded = false;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            txtPercentage.setVisibility(View.GONE);
            validate.setEnabled(true);
            select.setEnabled(true);
            Resources res = getResources();
            String text;

            if (videoAdded) {    // If it's good
                MainActivity.addVideoToPreferences(titleVideo.getText().toString());
                text = String.format(res.getString(R.string.success_selectedFile), titleVideo.getText().toString());
                MainActivity.displayToast(getApplicationContext(), text);
                titleVideo.setText(getResources().getString(R.string.no_video_selected));
                validate.setVisibility(View.GONE);
            }
            else {
                text = String.format(res.getString(R.string.error_selectedFile), titleVideo.getText().toString()) + " - " + message;
                MainActivity.displayToast(getApplicationContext(), text);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))   drawer.closeDrawer(GravityCompat.START);
        else    super.onBackPressed();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.home){
            finish();
            MainActivity.displayToast(getApplicationContext(),getResources().getString(R.string.home));
        }
        else if(id == R.id.remove_video){
            finish();
            MainActivity.displayToast(getApplicationContext(),getResources().getString(R.string.remove_video));
            Intent myIntent = new Intent(AddVideoActivity.this, RemoveVideoActivity.class);
            AddVideoActivity.this.startActivity(myIntent);
        }
        else if (id == R.id.deconnection) {
            MainActivity.displayToast(getApplicationContext(),getResources().getString(R.string.deconnection));
            MainActivity.resetPreferences();
            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(myIntent);
        }
        else if(id == R.id.about){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.about_teacher))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) { //do things
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}