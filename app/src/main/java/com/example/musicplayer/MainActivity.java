package com.example.musicplayer;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private Button playPauseButton;
    private TextView songTextView;
    private ListView musicListView;
    public static HashMap<String,String> musicsHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager);

//        musicListView = findViewById(R.id.musicListView);
        musicsHashMap = new HashMap<String,String>();

        while (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        viewPager2.setAdapter(new CustomPagerAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Songs");
                            break;
                        case 1:
                            tab.setText("Favorite songs");
                            break;
                    }
                }
        ).attach();

//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//
//                ArrayList<MusicListDisplay.MusicInfo> musicInfos = MusicListDisplay.getMusicInfos(getApplicationContext());
//                musicListDisplay = new MusicListDisplay(getApplicationContext(),musicInfos);
//                musicListDisplay.displayMusicList(musicListView, musicInfos);
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                tab.view.setBackgroundColor(Color.TRANSPARENT);
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//                System.out.println("hadj");
//            }
//        });
   }
}