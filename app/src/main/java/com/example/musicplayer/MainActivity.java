package com.example.musicplayer;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity{

    private Button playPauseButton;
    private TextView songTextView;
    private ListView musicListView;
    private MusicListDisplay musicListDisplay;
    public static HashMap<String,String> musicsHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playPauseButton = findViewById(R.id.playPauseButton);
        songTextView = findViewById(R.id.songTextView);
        musicListView = findViewById(R.id.musicListView);
        musicsHashMap = new HashMap<String,String>();

        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            ArrayList<MusicListDisplay.MusicInfo> musicInfos = MusicListDisplay.getMusicInfos(getApplicationContext());
            musicListDisplay = new MusicListDisplay(getApplicationContext(),musicInfos);
            musicListDisplay.displayMusicList(musicListView, musicInfos);
        }
    }
}