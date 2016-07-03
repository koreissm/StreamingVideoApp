package com.streamingvideo.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.streamingvideo.R;
import com.streamingvideo.classes.ExecutorServiceTest;

    /*  Creation Date : 18/06/2016
        Creators : Rayan DINAR, Erwan FOUACHE, Mouhamad KOREISSI
        Description : Student class
    */

public class OldSignInStudentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 200;


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

        View v = navigationView.getHeaderView(0);
        TextView profile_name = (TextView ) v.findViewById(R.id.profile_name_student);
        profile_name.setText(profile_name.getText().toString() + " - " + MainActivity.sharedpreferences.getString("login",""));
    }



    protected void launchVideo(View v) {

        int permissionCheckReadStorage = ContextCompat.checkSelfPermission(OldSignInStudentActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheckReadStorage != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        if(permissionCheckReadStorage == PackageManager.PERMISSION_GRANTED) {

            ExecutorServiceTest executorServiceTest = new ExecutorServiceTest(getApplicationContext());
            //executorServiceTest.test2();
        }
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
