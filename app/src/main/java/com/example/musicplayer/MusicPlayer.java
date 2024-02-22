package com.example.musicplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

public class MusicPlayer extends Service {
    private Context context;
    public static MediaPlayer mediaPlayer;
    public static String currentSongFilePath;
    private static String previousSongFilePath;
    private int currentPlaybackPosition = 0;
    private static final int NOTIFICATION_ID = 1;
    private Song currentSong;
    private int currentSongIndex;

    public MusicPlayer(Context context) {
        this.context = context;
    }

    public MusicPlayer() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (intent != null && intent.getAction() != null) {
                if (intent.getAction().equals("PLAY")) {
                    ArrayList<Song> songs = SongDbHelper.retrieveSongs(this);
                    previousSongFilePath = currentSongFilePath;
                    currentSongFilePath = intent.getStringExtra("filePath");
                    System.out.println("cc" + currentSongFilePath);
                    for (int i = 0; i < songs.size(); i++) {
                        Song song = songs.get(i);
                        if (song.getFilePath().equals(currentSongFilePath)) {
                            currentSong = song;
                            currentSongIndex = i;
                            System.out.println("ii" + i);
                        }
                    }

                    playMusic(currentSongFilePath);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify("tag", NOTIFICATION_ID, createNotification());
                } else if (intent.getAction().equals("PAUSE")) {
                    pauseMusic();
                } else if (intent.getAction().equals("NEXT")) {
                    playNext();
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify("tag", NOTIFICATION_ID, createNotification());
                } else if (intent.getAction().equals("PREVIOUS")) {
                    playPrevious();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            Toast.makeText(getApplicationContext(), "error: " + e.getLocalizedMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        return START_STICKY;
    }

    private void playNext() {
        ArrayList<Song> songs = SongDbHelper.retrieveSongs(this);

        if (currentSongIndex + 1 == SongAdapter.songs.size()) {
            playMusic(SongAdapter.songs.get(0).getFilePath());
            currentSong = songs.get(0);
        } else {
            currentSongIndex = currentSongIndex + 1;
            currentSong = songs.get(currentSongIndex);
            playMusic(SongAdapter.songs.get(currentSongIndex).getFilePath());
        }
    }

    private void playPrevious() {
        if (currentSongIndex > 0) {
            currentSongIndex--;
            currentPlaybackPosition = 0;
            playMusic(SongAdapter.songs.get(currentSongIndex).getFilePath());
        } else {
            // Optional: Go to the last song if reached the beginning
            currentSongIndex = SongAdapter.songs.size() - 1;
            currentPlaybackPosition = 0;
            playMusic(SongAdapter.songs.get(currentSongIndex).getFilePath());
        }
    }


    public void playMusic(String filePath) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            mediaPlayer.reset();
        }

        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();

            if (previousSongFilePath != null) {
                if (!previousSongFilePath.isEmpty() && previousSongFilePath.equals(currentSongFilePath)) {
                    if (currentPlaybackPosition > 0) {
                        mediaPlayer.seekTo(currentPlaybackPosition);
                    }
                }
            }
            mediaPlayer.start();
        } catch (Exception e) {
            Toast.makeText(context, "Couldn't play the music. " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Notification createNotification() {
        String coverImageUrl = currentSong.getCoverImageUrl();

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
                .setContentTitle(currentSong.getTitle())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.default_image_cover)
                .setContentText(currentSong.getArtist())
                .setAutoCancel(true);


        if(!coverImageUrl.isEmpty() && coverImageUrl != null){
            Glide.with(this)
                    .asBitmap()
                    .load(coverImageUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            try {
                                builder.setLargeIcon(resource);

                                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                                        .bigPicture(resource)
                                        .bigLargeIcon((Icon) null);

                                builder.setStyle(bigPictureStyle);

                                // Notify
                                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                NotificationManagerCompat.from(getApplicationContext()).notify(NOTIFICATION_ID, builder.build());
                            } catch (Exception e) {
                                // Log the exception details for debugging
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }else{
            Bitmap defaultLargeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.default_image_cover);
            builder.setLargeIcon(defaultLargeIcon);
            NotificationManagerCompat.from(getApplicationContext()).notify(NOTIFICATION_ID, builder.build());

        }


        Intent pauseIntent = new Intent(this, MusicPlayer.class);
        pauseIntent.setAction("PAUSE");
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE);
        builder.addAction(android.R.drawable.ic_media_pause, "Pause Music", pausePendingIntent);

        Intent nextIntent = new Intent(this, MusicPlayer.class);
        nextIntent.setAction("NEXT");
        PendingIntent nextPendingIntent = PendingIntent.getService(this, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE);
        builder.addAction(android.R.drawable.btn_radio, "NEXT", nextPendingIntent);



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
