package com.example.musicplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.security.Provider;

public class MusicPlayer extends Service{
    private Context context;
    public  MediaPlayer mediaPlayer;
    private static final int NOTIFICATION_ID = 1;
    public MusicPlayer(Context context) {
        this.context = context;
    }

    public MusicPlayer(){}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("clicked on play");
        if (intent != null && intent.getAction() != null) {
            if(intent.getAction().equals("PLAY")) {
                String filePath = intent.getStringExtra("filePath");
                playMusic(filePath);
                startForeground(NOTIFICATION_ID, createNotification()); // Start as a foreground service
            }else if(intent.getAction().equals("PAUSE")){
                System.out.println("here to stop");
                pauseMusic();
            }
        }
        return START_STICKY;
    }

    public void playMusic(String filePath){
        System.out.println("start to play");
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
    private Notification createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "CHANNEL_ID",
                    "Music Player Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setContentTitle("Music Player")
                .setContentText("Playing Music")
                .setSmallIcon(R.drawable.default_image_cover);

        Intent stopIntent = new Intent(this, MusicPlayer.class);
        stopIntent.setAction("PAUSE");
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);
        builder.addAction(android.R.drawable.ic_media_pause, "Stop Music", stopPendingIntent);

        return builder.build();
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
