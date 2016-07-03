package com.streamingvideo.activities;

	/*  Creation Date: 18/06/2016
		Créators : Rayan DINAR, Erwan FOUACHE, Mouhamad KOREISSI
		Description : Activity for displaying the menu and
                      the video list (Professor authentication)
	*/

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.streamingvideo.R;
import com.streamingvideo.classes.RecyclerViewVideos;
import com.streamingvideo.classes.Video;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SignInTeacherActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout drawer;

    private ArrayList<Video> videos = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView titleActivity;

    private Set<String> videoListPreferences;
    public static Socket socket;

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
        navigationView.getMenu().getItem(0).setChecked(true);

        recyclerView=(RecyclerView)findViewById(R.id.recyclerView_movies);
        titleActivity = (TextView)findViewById(R.id.title_activity) ;

        RelativeLayout layout_teacher = (RelativeLayout)findViewById(R.id.layout_teacher);
        RelativeLayout layout_addVideo = (RelativeLayout)findViewById(R.id.addVideoView_teacher);
        RelativeLayout layout_removeVideo = (RelativeLayout)findViewById(R.id.removeVideoView_teacher);
        layout_teacher.removeView(layout_addVideo);
        layout_teacher.removeView(layout_removeVideo);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        View v = navigationView.getHeaderView(0);
        TextView profile_name = (TextView ) v.findViewById(R.id.profile_name_teacher);
        profile_name.setText(profile_name.getText().toString() + " - " + MainActivity.sharedpreferences.getString("login",""));

        initializeData();
        new Thread(new ClientThread()).start();
    }

    private void initializeData(){
        videos.clear();
        videoListPreferences = MainActivity.sharedpreferences.getStringSet("videosList", new HashSet<String>());
        for (String value : videoListPreferences) {
            videos.add(new Video(value));
        }

        if(videos.isEmpty()) titleActivity.setText(getResources().getString(R.string.no_video));
        else    if(videos.size()==1)    titleActivity.setText(getResources().getString(R.string.list_video));
        else    titleActivity.setText(getResources().getString(R.string.list_videos));
        initializeAdapter();
    }

    private void initializeAdapter(){
        RecyclerViewVideos adapter = new RecyclerViewVideos(videos, SignInTeacherActivity.this, "launch");
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(0).setChecked(true);
        //if((AddVideoActivity.videoAdded != null && AddVideoActivity.videoAdded) || (RemoveVideoActivity.videoRemoved != null && RemoveVideoActivity.videoRemoved)){
            videos.clear();
            initializeData();
            MainActivity.setPreferencesVideoList(videoListPreferences);
          //  AddVideoActivity.videoAdded = false;
        //}
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))   drawer.closeDrawer(GravityCompat.START);
        else    MainActivity.displayToast(getApplicationContext(),getResources().getString(R.string.error_back));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
	/*Menu navigation*/
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.add_video){
            MainActivity.displayToast(getApplicationContext(),getResources().getString(R.string.add_video));
            Intent myIntent = new Intent(this, AddVideoActivity.class);
            this.startActivity(myIntent);
        }
        else if(id == R.id.remove_video){
            MainActivity.displayToast(getApplicationContext(),getResources().getString(R.string.remove_video));
            Intent myIntent = new Intent(SignInTeacherActivity.this, RemoveVideoActivity.class);
            SignInTeacherActivity.this.startActivity(myIntent);
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
	/*Web server connection*/
    class ClientThread implements Runnable {

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(MainActivity.IP);

                socket = new Socket(serverAddr, MainActivity.SERVERPORT);

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
    }
}
