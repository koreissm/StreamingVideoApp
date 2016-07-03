package com.streamingvideo.activities;

	/*  Creation Date : 18/06/2016
		Creators : Rayan DINAR, Erwan FOUACHE, Mouhamad KOREISSI
		Description : Activity for displaying the menu and
                      the list of teachers which the student
                      enrolled before (student authentication)
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
import com.streamingvideo.classes.RecyclerViewTeachers;
import com.streamingvideo.classes.RecyclerViewVideos;
import com.streamingvideo.classes.Teacher;
import com.streamingvideo.classes.Video;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SignInStudentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout drawer;

    private ArrayList<Teacher> teachers = new ArrayList<>();
    private ArrayList<Teacher> teacherIDs = new ArrayList<>();

    private RecyclerView recyclerView;
    private TextView titleActivity;

    private Set<String> teacherListPreferences;
    private Set<String> teacherIDListPreferences;

    public static Socket socket;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_student);
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

        recyclerView=(RecyclerView)findViewById(R.id.recyclerView_teachers);
        titleActivity = (TextView)findViewById(R.id.title_activity) ;

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        View v = navigationView.getHeaderView(0);
        TextView profile_name = (TextView ) v.findViewById(R.id.profile_name_student);
        profile_name.setText(profile_name.getText().toString() + " - " + MainActivity.sharedpreferences.getString("login",""));

        initializeData();
    }

    private void initializeData(){
        teachers.clear();
        teacherListPreferences = MainActivity.sharedpreferences.getStringSet("teachersList", new HashSet<String>());
        teacherIDListPreferences = MainActivity.sharedpreferences.getStringSet("teacherIDsList", new HashSet<String>());
        for (String value : teacherListPreferences) {
            teachers.add(new Teacher(value));
        }
        for (String value : teacherIDListPreferences) {
            teacherIDs.add(new Teacher(value));
        }

        if(teachers.isEmpty()) titleActivity.setText(getResources().getString(R.string.no_teacher));
        else    if(teachers.size()==1)    titleActivity.setText(getResources().getString(R.string.list_teacher));
        else    titleActivity.setText(getResources().getString(R.string.list_teachers));
        initializeAdapter();
    }

    private void initializeAdapter(){
        RecyclerViewTeachers adapter = new RecyclerViewTeachers(teachers, teacherIDs, SignInStudentActivity.this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(0).setChecked(true);
        //if((AddVideoActivity.videoAdded != null && AddVideoActivity.videoAdded) || (RemoveVideoActivity.videoRemoved != null && RemoveVideoActivity.videoRemoved)){
        teachers.clear();
        initializeData();
        MainActivity.setPreferencesTeacherList(teacherListPreferences, teacherIDListPreferences);
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
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.deconnection) {
            MainActivity.displayToast(getApplicationContext(),getResources().getString(R.string.deconnection));
            MainActivity.resetPreferences();
            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(myIntent);
        }
        else if(id == R.id.about){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            Resources res = getResources();
            String text = String.format(res.getString(R.string.about_student),
                    res.getString(R.string.button_launchVideo));
            builder.setMessage(text)
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
