package com.example.musicplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

public class MusicPlayer{
    private Context context;
    public  MediaPlayer mediaPlayer;

    public MusicPlayer(Context context) {
        this.context = context;
    }


    public void playMusic(String filePath){
        if (mediaPlayer == null) {
            System.out.println("media player is null");
            mediaPlayer = new MediaPlayer();
        }else{
            mediaPlayer.reset();
        }

        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            Toast.makeText(context, "Couldn't play the music. " + e.getLocalizedMessage() , Toast.LENGTH_SHORT).show();
        }
    }

    public void pauseMusic(){
        try {
            if(mediaPlayer != null && mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Couldn't pause the music. "  + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
