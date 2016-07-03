package com.streamingvideo.classes;

	/*  Creation Date : 20/06/2016
		Creators : Rayan DINAR, Erwan FOUACHE, Mouhamad KOREISSI
		Description : Cardview class for display and accEssible video functions list
	*/
	
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
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
import com.streamingvideo.activities.SignInTeacherActivity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class RecyclerViewVideos extends RecyclerView.Adapter<RecyclerViewVideos.VideoViewHolder> {

    public static class VideoViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView title;
        LinearLayout linearLayout;
        ImageView image;

        VideoViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cardView_video);
            title = (TextView)itemView.findViewById(R.id.title_video);
            linearLayout =(LinearLayout)itemView.findViewById(R.id.linearLayout);
            image = (ImageView)itemView.findViewById(R.id.screen_video);
            image.setImageResource(R.drawable.default_video);
        }
    }
    private Context context;
    private String action;
    List<Video> videos;

    public RecyclerViewVideos(List<Video> videos, Context context, String action){
        this.videos = videos;
        this.context = context;
        this.action = action;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_cardview, viewGroup, false);
        VideoViewHolder videoViewHolder = new VideoViewHolder(view);
        return videoViewHolder;
    }

    @Override
    public void onBindViewHolder(final VideoViewHolder videoViewHolder, final int i) {
        videoViewHolder.title.setText(videos.get(i).title);
        //movieViewHolder.video.setVideoURI(Uri.parse(MainActivity.urlServer + "movies/" + movieViewHolder.title.getText().toString()));

        videoViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(action.equals("remove")){
                    RemoveVideoActivity.removeVideoClick(context, videoViewHolder.title.getText().toString());
                }
                else if(action.equals("launch")){
                    try {
                        String str = "prof:" + MainActivity.sharedpreferences.getInt("userID", -1) + ":" + videos.get(i).title;
                        PrintWriter out = new PrintWriter(new BufferedWriter(
                                new OutputStreamWriter(SignInTeacherActivity.socket.getOutputStream())),
                                true);
                        out.println(str);
                        MainActivity.displayToast(context,"Lancement de " + videos.get(i).title);
                        Log.d("Out", "Test " + String.valueOf(out));
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

        });

        /*if(action.equals("launch")){
            videoViewHolder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    RemoveVideoActivity.removeVideoClick(context, videoViewHolder.title.getText().toString());
                    return true;
                }
            });
        }*/
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }
}