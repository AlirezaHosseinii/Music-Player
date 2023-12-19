package com.example.musicplayer;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button playPauseButton;
    private TextView songTextView;
    private ListView musicListView;
    private MusicListDisplay musicListDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicListDisplay = new MusicListDisplay(getApplicationContext());
        playPauseButton = findViewById(R.id.playPauseButton);
        songTextView = findViewById(R.id.songTextView);
        musicListView = findViewById(R.id.musicListView);

        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            musicListDisplay.displayMusicList(musicListView);

            musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItem = (String) parent.getItemAtPosition(position);
                }
            });

        }
    }
}