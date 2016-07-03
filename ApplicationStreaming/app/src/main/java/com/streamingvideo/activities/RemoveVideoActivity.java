package com.streamingvideo.activities;

	/*  Creation Date : 19/06/2016
		Creators : Rayan DINAR, Erwan FOUACHE, Mouhamad KOREISSI
		Description : Activity for the suppression of a video
                      which is connected to the teacher on the server
                      (Video deletion + db link)
	*/

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.streamingvideo.R;
import com.streamingvideo.ServiceHandler;
import com.streamingvideo.classes.RecyclerViewVideos;
import com.streamingvideo.classes.Video;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class RemoveVideoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout drawer;

    private static ArrayList<Video> videos = new ArrayList<>();
    private static Set<String> videoListPreferences;
    private static RecyclerView recyclerView;
    private static TextView titleActivity;

    private static RemoveVideoAsyncTask removeVideo;
    private static String urlRemoveVideo = MainActivity.urlServer + "removeVideo.php";

    public static Boolean videoRemoved;
    private static String message;

    public static Context context;

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

        context = this;

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(2).setChecked(true);

        recyclerView=(RecyclerView)findViewById(R.id.recyclerView_moviesToDelete);
        titleActivity = (TextView)findViewById(R.id.titleVideosToDelete_activity);

        View v = navigationView.getHeaderView(0);
        TextView profile_name = (TextView ) v.findViewById(R.id.profile_name_teacher);
        profile_name.setText(profile_name.getText().toString() + " - " + MainActivity.sharedpreferences.getString("login",""));

        RelativeLayout layout_teacher = (RelativeLayout)findViewById(R.id.layout_teacher);
        RelativeLayout layout_listVideo = (RelativeLayout)findViewById(R.id.listVideoView_teacher);
        RelativeLayout layout_addVideo = (RelativeLayout)findViewById(R.id.addVideoView_teacher);
        layout_teacher.removeView(layout_listVideo);
        layout_teacher.removeView(layout_addVideo);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        initializeData();
    }

    private static void initializeData(){
        videos.clear();
        videoListPreferences = MainActivity.sharedpreferences.getStringSet("videosList", new HashSet<String>());
        for (String value : videoListPreferences) {
            videos.add(new Video(value));
        }
        if(videos.isEmpty()) titleActivity.setText(context.getString(R.string.no_video_toDelete));
        else titleActivity.setText(context.getString(R.string.remove_video));
        initializeAdapter();
    }

    private static void initializeAdapter(){
        RecyclerViewVideos adapter = new RecyclerViewVideos(videos, context, "remove");
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(2).setChecked(true);
        initializeData();
        MainActivity.setPreferencesVideoList(videoListPreferences);
    }

    private static void removeVideo(Context context, String nameVideo){
        MainActivity.isInternetPresent = MainActivity.connectionDetector.isConnectingToInternet();
        if(MainActivity.isInternetPresent) {
            try {
                removeVideo = new RemoveVideoAsyncTask(MainActivity.sharedpreferences.getInt("userID", -1), nameVideo);
                removeVideo.execute();
                removeVideo.get();
                if (videoRemoved) {
                    for (String value : videoListPreferences) {
                        if (value.equals(nameVideo)) {
                            videoListPreferences.remove(value);
                            break;
                        }
                    }
                    MainActivity.setPreferencesVideoList(videoListPreferences);
                    initializeData();
                    Resources res = context.getResources();
                    String text = String.format(res.getString(R.string.remove_videoSelected), nameVideo);

                    MainActivity.displayToast(context, text);
                } else MainActivity.displayToast(context, message);

            }
            catch (InterruptedException e) {e.printStackTrace();}
            catch (ExecutionException e) {e.printStackTrace();}
        }
    }

    public static void removeVideoClick(final Context context, final String nameVideo){
        Resources res = context.getResources();
        String text = String.format(res.getString(R.string.confirm_remove_video),
                nameVideo);
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(res.getString(R.string.remove_video))
                .setMessage(text)
                .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeVideo(context, nameVideo);
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.no), null)
                .show();
    }

    private static class RemoveVideoAsyncTask extends AsyncTask<Void, Void, Void> {

        private int teacherID;
        private String titleVideo;

        public RemoveVideoAsyncTask(int teacherID, String titleVideo){
            this.teacherID = teacherID;
            this.titleVideo = titleVideo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                ServiceHandler sh = new ServiceHandler();
                List<NameValuePair> valuesToSend = new ArrayList<NameValuePair>(2);
                valuesToSend.add(new BasicNameValuePair("teacherID", String.valueOf(teacherID)));
                valuesToSend.add(new BasicNameValuePair("titleVideo", titleVideo));

                String jsonStr2 = sh.makeServiceCall(urlRemoveVideo, ServiceHandler.POST, valuesToSend);
                Log.d("Response",jsonStr2);
                if (jsonStr2 != null) {
                    try {
                        JSONObject jsonObj2 = new JSONObject(jsonStr2);
                        int success = jsonObj2.getInt("success");

                        if(success == 1)    videoRemoved = true;
                        else{
                            videoRemoved = false;
                            message = jsonObj2.getString("message");
                        }
                    }
                    catch (JSONException e) {e.printStackTrace();}
                }
            }
            catch (Exception e){e.printStackTrace();}
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
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
        else if(id == R.id.add_video){
            finish();
            MainActivity.displayToast(getApplicationContext(),getResources().getString(R.string.add_video));
            Intent myIntent = new Intent(RemoveVideoActivity.this, AddVideoActivity.class);
            RemoveVideoActivity.this.startActivity(myIntent);
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