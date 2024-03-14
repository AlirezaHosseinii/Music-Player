package com.example.musicplayer;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.SeekBar;
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
    private Handler handler = new Handler();
    public static MediaPlayer mediaPlayer;
    public static String currentSongFilePath;
    private static String previousSongFilePath;
    private int currentPlaybackPosition = 0;
    private static final int NOTIFICATION_ID = 1;
    private Song currentSong;
    private int currentSongIndex;
    private NotificationCompat.Builder notificationBuilder;
    private SeekBar songProgressSeekBar;

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
                    startForeground(NOTIFICATION_ID, createNotification());

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

        notificationBuilder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setContentTitle(currentSong.getTitle())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.default_image_cover_notif)
                .setContentText(currentSong.getArtist())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);


        if (!coverImageUrl.isEmpty() && coverImageUrl != null) {
            Glide.with(this)
                    .asBitmap()
                    .load(coverImageUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            try {
                                notificationBuilder.setLargeIcon(resource);

                                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                                        .bigPicture(resource)
                                        .bigLargeIcon((Icon) null);

                                notificationBuilder.setStyle(bigPictureStyle);

                                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                NotificationManagerCompat.from(getApplicationContext()).notify(NOTIFICATION_ID, notificationBuilder.build());
                            } catch (Exception e) {
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        } else {
            Bitmap defaultLargeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.default_image_cover);
            notificationBuilder.setLargeIcon(defaultLargeIcon);
        }

        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(this, 0,
                mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(mainPendingIntent);

        Intent pauseIntent = new Intent(this, MusicPlayer.class);
        pauseIntent.setAction("PAUSE");
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE);
        notificationBuilder.addAction(android.R.drawable.ic_media_pause, "Pause Music", pausePendingIntent);

        Intent nextIntent = new Intent(this, MusicPlayer.class);
        nextIntent.setAction("NEXT");
        PendingIntent nextPendingIntent = PendingIntent.getService(this, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE);
        notificationBuilder.addAction(android.R.drawable.btn_radio, "NEXT", nextPendingIntent);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    System.out.println("hds");
                    int totalDuration = mediaPlayer.getDuration();
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    notificationBuilder.setProgress(totalDuration, currentPosition, false);
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    NotificationManagerCompat.from(getApplicationContext()).notify(NOTIFICATION_ID, notificationBuilder.build());
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);

        return notificationBuilder.build();
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
