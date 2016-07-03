package com.streamingvideo.activities;

	/*  Creation date : 18/06/2016
		Creators : Rayan DINAR, Erwan FOUACHE, Mouhamad KOREISSI
		Description : Application activity for home
                      enabling authentication, returns to
                      activity pages based on user type
	*/

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.streamingvideo.R;
import com.streamingvideo.ServiceHandler;
import com.streamingvideo.classes.ConnectionDetector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    /* Connection Server variable */
    public static String IP = "192.168.1.63";
    public static final int SERVERPORT = 9002;

    /* URL for interactions with BDD */
    public static String urlServer = "http://" + IP + "/videoamademande/";
    private String urlGetUser = urlServer + "getUser.php";
	
	/* Layout */
    private EditText loginField, passwordField;
    private ProgressDialog progressDialog;
	
    private GetDataAsyncTask getData;
    private boolean canSignIn;
    private String message;
    private String type;
    private int userID;
    private List<String> videosList;
    private List<String> teachersList;
    private List<String> teacherIDsList;
	
	/* User preference setting */
    public static SharedPreferences sharedpreferences;
    public static SharedPreferences.Editor editor;
    public static final String MyPREFERENCES = "MyPrefs";

	/* Internet detection parameter */
    public static ConnectionDetector connectionDetector;
    public static Boolean isInternetPresent;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        /* *** The text fields are declared *** */
        loginField =  (EditText)findViewById(R.id.loginEditText);
        passwordField =  (EditText)findViewById(R.id.passwordEditText);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        connectionDetector = new ConnectionDetector(getApplicationContext());
        isInternetPresent = connectionDetector.isConnectingToInternet();

        if(isInternetPresent){
            if(sharedpreferences.contains("isConnected") && sharedpreferences.getBoolean("isConnected",true)){
                if(sharedpreferences.getString("type","").equals("teacher"))    launchNewIntent("teacher");
                else if(sharedpreferences.getString("type","").equals("student"))   launchNewIntent("student");
            }
        }
    }

    public static void setPreferences(String key, String value){
        editor.putString(key, value);
        editor.commit();
    }

    public static void setPreferences(String key, Boolean value){
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void setPreferences(String key, int value){
        editor.putInt(key, value);
        editor.commit();
    }

	/* Preference for a teacher kind */
    public static void setPreferencesVideoList(Set<String> videoList){
        String login = sharedpreferences.getString("login","");
        String type = sharedpreferences.getString("type","");
        int userID = sharedpreferences.getInt("userID",-1);
        editor.clear();
        setPreferences("login", login);
        setPreferences("type", type);
        setPreferences("userID", userID);
        setPreferences("isConnected", true);
        editor.putStringSet("videosList", videoList);
        editor.commit();
        //Log.d("setPreferencesVideoList", String.valueOf(videoList));
    }
	
	/* Preference for a student kind */
    public static void setPreferencesTeacherList(Set<String> teacherList, Set<String> teacherIDList){
        String login = sharedpreferences.getString("login","");
        String type = sharedpreferences.getString("type","");
        int userID = sharedpreferences.getInt("userID",-1);
        editor.clear();
        setPreferences("login", login);
        setPreferences("type", type);
        setPreferences("userID", userID);
        setPreferences("isConnected", true);
        editor.putStringSet("teachersList", teacherList);
        editor.commit();
        editor.putStringSet("teacherIDsList", teacherIDList);
        editor.commit();
        Log.d("Teacher Login", String.valueOf(teacherList));
        Log.d("Teacher ID", String.valueOf(teacherIDList));
    }

	// Preference reset 
    public static void resetPreferences(){
        MainActivity.setPreferences("login", "");
        MainActivity.setPreferences("password", "");
        MainActivity.setPreferences("isConnected", false);
        MainActivity.setPreferences("type", "");
        MainActivity.setPreferences("userID", -1);
        Set<String> hs = sharedpreferences.getStringSet("videosList", new HashSet<String>());
        Set<String> hs2 = sharedpreferences.getStringSet("teachersList", new HashSet<String>());
		Set<String> hs3 = sharedpreferences.getStringSet("teacherIDsList", new HashSet<String>());
        hs.clear();
        hs2.clear();
		hs3.clear();
    }

    public static void addVideoToPreferences(String titleVideo){
        Set<String> videoListPreferences = sharedpreferences.getStringSet("videosList", new HashSet<String>());
        videoListPreferences.add(titleVideo);
        setPreferencesVideoList(videoListPreferences);
    }

    public static void addTeacherToPreferences(String teacherLogin){
        Set<String> teacherListPreferences = sharedpreferences.getStringSet("teachersList", new HashSet<String>());
        teacherListPreferences.add(teacherLogin);
        setPreferencesVideoList(teacherListPreferences);
    }
	
	/* Initialization and authentication method */
    protected void signIn(View v){
        isInternetPresent = connectionDetector.isConnectingToInternet();
        if(isInternetPresent) {
            try {
                getData = new GetDataAsyncTask(loginField.getText().toString(), passwordField.getText().toString());
                getData.execute();
                getData.get();

                if (canSignIn) {
                    displayToast(getApplicationContext(), getResources().getString(R.string.success_login));
                    setPreferences("login", loginField.getText().toString());
                    setPreferences("password", passwordField.getText().toString());
                    setPreferences("userID", userID);

                    // New Activity
                    launchNewIntent(type);
                } else displayToast(getApplicationContext(), message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        else    connectionDetector.displayError();
    }

	/*Launching a new activity based on user type*/
    public void launchNewIntent(String type){
        Intent myIntent;

        if(!sharedpreferences.contains("isConnected") || sharedpreferences.getBoolean("isConnected",true) == false) setPreferences("isConnected", true);
		//For Professor
        if(type.equals("teacher")){
            myIntent = new Intent(MainActivity.this, SignInTeacherActivity.class);
            if(!sharedpreferences.contains("type") || !sharedpreferences.getString("type","").equals("teacher")){

                setPreferences("type", "teacher");

                if(!videosList.isEmpty()){
                    Set<String> videoListPreferences = sharedpreferences.getStringSet("videosList", new HashSet<String>());
                    for( int i=0;i<videosList.size();i++){
                        videoListPreferences.add(videosList.get(i));
                    }
                    setPreferencesVideoList(videoListPreferences);
                }
            }
        }
		//For Student
        else{
            myIntent = new Intent(MainActivity.this, SignInStudentActivity.class);
            if(!sharedpreferences.contains("type") || !sharedpreferences.getString("type","").equals("student")){

                setPreferences("type", "student");

                if(!teachersList.isEmpty()){
                    Set<String> teacherListPreference = sharedpreferences.getStringSet("teachersList", new HashSet<String>());
                    Set<String> teacherIDsListPreference = sharedpreferences.getStringSet("teacherIDsList", new HashSet<String>());
                    for( int i=0;i<teachersList.size();i++){
                        teacherListPreference.add(teachersList.get(i));
                        teacherIDsListPreference.add(teacherIDsList.get(i));
                    }
                    setPreferencesTeacherList(teacherListPreference, teacherIDsListPreference);
                }
            }
        }

        MainActivity.this.startActivity(myIntent);
    }

    public static void displayToast(Context context, String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private class GetDataAsyncTask extends  AsyncTask<Void, Void, Void> {

        private String loginInput;
        private String passwordInput;

        public GetDataAsyncTask(String loginInput, String passwordInput){
            this.loginInput = loginInput;
            this.passwordInput = passwordInput;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.loading_wait), getResources().getString(R.string.loading_connection), true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                ServiceHandler sh = new ServiceHandler();
                List<NameValuePair> valuesToSend = new ArrayList<NameValuePair>(2);
                valuesToSend.add(new BasicNameValuePair("login", loginInput));
                valuesToSend.add(new BasicNameValuePair("password", passwordInput));

                /*
                    ContentValues valuesToSend = new ContentValues();
                    valuesToSend.put("login", loginInput);
                    valuesToSend.put("password", passwordInput);
                */
                String jsonStr2 = sh.makeServiceCall(urlGetUser, ServiceHandler.POST, valuesToSend);
                Log.d("Response",jsonStr2);
                if (jsonStr2 != null) {
                    try {
                        JSONObject jsonObj2 = new JSONObject(jsonStr2);
                        int success = jsonObj2.getInt("success");

                        if(success == 1){
                            canSignIn = true;
                            type = jsonObj2.getString("type");
                            userID = jsonObj2.getInt("userID");
                            if(type.equals("teacher")){
                                videosList = new ArrayList<String>();
                                JSONArray dataValues = jsonObj2.getJSONArray("videos");
                                for(int j=0;j<dataValues.length();j++){
                                    JSONObject values = dataValues.getJSONObject(j);
                                    String title = values.getString("title");
                                    videosList.add(title);
                                }
                            }
                            else if(type.equals("student")){
                                teachersList = new ArrayList<String>();
                                teacherIDsList = new ArrayList<String>();
                                JSONArray dataValues = jsonObj2.getJSONArray("teachers");
                                for(int j=0;j<dataValues.length();j++){
                                    JSONObject values = dataValues.getJSONObject(j);
                                    String teacher = values.getString("teacherLogin");
                                    String idTeacher = values.getString("idTeacher");
                                    teachersList.add(teacher);
                                    teacherIDsList.add(idTeacher);
                                }
                            }
                        }
                        else{
                            canSignIn = false;
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
            if (progressDialog.isShowing()) progressDialog.dismiss();
        }
    }
}