package com.example.musicplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MusicPlayer extends Service{
    private Context context;
    public  static MediaPlayer mediaPlayer;
    public static String currentSongFilePath;
    private static String previousSongFilePath;
    private int currentPlaybackPosition = 0;
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
        try {
            if (intent != null && intent.getAction() != null) {
                if(intent.getAction().equals("PLAY")) {
                    previousSongFilePath = currentSongFilePath;
                    currentSongFilePath = intent.getStringExtra("filePath");
                    playMusic(currentSongFilePath);
                    startForeground(NOTIFICATION_ID, createNotification());
                }else if(intent.getAction().equals("PAUSE")){
                    pauseMusic();
                }
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "error: " + e.getLocalizedMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        return START_STICKY;
    }

    public void playMusic(String filePath){
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }else{
            mediaPlayer.reset();
        }

        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();

            if(previousSongFilePath != null){
                if(!previousSongFilePath.isEmpty() && previousSongFilePath.equals(currentSongFilePath)){
                    if (currentPlaybackPosition > 0) {
                        mediaPlayer.seekTo(currentPlaybackPosition);
                    }
                }
            }
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

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.pause_icon);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setContentTitle("Music Player")
                .setContentText("Playing Music")
                .setSmallIcon(R.drawable.play_icon)
                .setLargeIcon(largeIcon)
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
                currentPlaybackPosition = mediaPlayer.getCurrentPosition();
                mediaPlayer.pause();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Couldn't pause the music. "  + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
