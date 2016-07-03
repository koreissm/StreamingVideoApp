package com.streamingvideo.classes;

	/*  Creation Date: 20/06/2016
		Creators : Rayan DINAR, Erwan FOUACHE, Mouhamad KOREISSI
		Description : Cardview class for display and accéssible functions of the teacher list
	*/
	

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.streamingvideo.R;
import com.streamingvideo.activities.MainActivity;
import com.streamingvideo.activities.RemoveVideoActivity;
import com.streamingvideo.activities.SignInStudentActivity;
import com.streamingvideo.activities.SignInTeacherActivity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class RecyclerViewTeachers extends RecyclerView.Adapter<RecyclerViewTeachers.TeacherViewHolder> {

    public static class TeacherViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView title;
        LinearLayout linearLayout;
        ImageView image;

        TeacherViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cardView_video);
            title = (TextView)itemView.findViewById(R.id.title_video);
            linearLayout =(LinearLayout)itemView.findViewById(R.id.linearLayout);
            image = (ImageView)itemView.findViewById(R.id.screen_video);
        }
    }
    private Context context;
    List<Teacher> teachers;
    List<Teacher> idTeachers;

    public RecyclerViewTeachers(List<Teacher> teachers, List<Teacher> idTeachers, Context context){
        this.teachers = teachers;
        this.idTeachers = idTeachers;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public TeacherViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_cardview, viewGroup, false);
        TeacherViewHolder videoViewHolder = new TeacherViewHolder(view);
        return videoViewHolder;
    }

    @Override
    public void onBindViewHolder(final TeacherViewHolder videoViewHolder, final int i) {
        videoViewHolder.title.setText(teachers.get(i).login);
        //movieViewHolder.video.setVideoURI(Uri.parse(MainActivity.urlServer + "movies/" + movieViewHolder.title.getText().toString()));

        videoViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TEACHER ID", idTeachers.get(i).login);
                int permissionCheckReadStorage = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionCheckReadStorage != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, SignInStudentActivity.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                if(permissionCheckReadStorage == PackageManager.PERMISSION_GRANTED) {

                    ExecutorServiceTest executorServiceTest = new ExecutorServiceTest(context);
                    executorServiceTest.test2(idTeachers.get(i).login);
                }
            }

        });
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }
}