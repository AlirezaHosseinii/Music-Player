package com.example.musicplayer;


import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class SongActivity extends AppCompatActivity {
    private SeekBar songProgressSeekBar;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_activity);

        String songTitle = getIntent().getStringExtra("songTitle");
        String songArtist = getIntent().getStringExtra("songArtist");
        String coverImageUrl = getIntent().getStringExtra("coverImageUrl");
        String duration = getIntent().getStringExtra("duration");

        TextView titleTextView = findViewById(R.id.song_activity_title);
        TextView artistTextView = findViewById(R.id.song_activity_artist);
        ImageView coverImageView = findViewById(R.id.song_activity_cover);
        songProgressSeekBar = findViewById(R.id.song_progress_seekbar);

        titleTextView.setText(songTitle);
        artistTextView.setText(songArtist);

        Glide.with(this)
                .load(coverImageUrl)
                .placeholder(R.drawable.default_image_cover)
                .error(R.drawable.default_image_cover)
                .into(coverImageView);

        updateSeekBar();
        songProgressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    MusicPlayer.mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void updateSeekBar() {
        songProgressSeekBar.setMax(MusicPlayer.mediaPlayer.getDuration());


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MusicPlayer.mediaPlayer != null) {
                    int currentPosition = MusicPlayer.mediaPlayer.getCurrentPosition();
                    songProgressSeekBar.setProgress(currentPosition);
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Release MediaPlayer and handler
//        if (mediaPlayer != null) {
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//        handler.removeCallbacksAndMessages(null);
//    }
}
