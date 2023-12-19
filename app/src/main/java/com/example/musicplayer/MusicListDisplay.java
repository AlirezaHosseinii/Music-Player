package com.example.musicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MusicListDisplay {
    private Context context;
    private MediaPlayer mediaPlayer;
    public MusicListDisplay(Context context) {
        this.context = context;
        this.mediaPlayer = new MediaPlayer();
    }


    protected void displayMusicList(ListView musicListView){
        ArrayList<String> musicList = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri,null,null,null,null);
        if (cursor != null && cursor.moveToFirst()) {
            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int dataColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                String title = cursor.getString(titleColumn);
                String artist = cursor.getString(artistColumn);
                String filePath = cursor.getString(dataColumn);
                musicList.add(title + " - " + artist);
            } while (cursor.moveToNext());
            cursor.close();

            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.music_item, R.id.musicItemTextView,musicList);
            musicListView.setAdapter(adapter);

        }else {
            Toast.makeText(context, "No music files found.", Toast.LENGTH_SHORT).show();
        }
}
}
