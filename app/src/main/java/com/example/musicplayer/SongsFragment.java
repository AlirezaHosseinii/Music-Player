package com.example.musicplayer;

import static com.example.musicplayer.MainActivity.preferences;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class SongsFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Song> songs = new ArrayList<>();
    private SongDbHelper dbHelper;
    public SongsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.songs_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        dbHelper = new SongDbHelper(getContext().getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        fetchSongs();
        SongAdapter adapter = new SongAdapter(songs, getContext());
        recyclerView.setAdapter(adapter);
        System.out.println("is null"  + (preferences == null));

        return view;
    }

    private void fetchSongs() {
        ContentResolver contentResolver = getActivity().getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);
        if (cursor != null) {
            SongDbHelper dbHelper = new SongDbHelper(getContext().getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            try {
                while (cursor.moveToNext()) {
                    int titleColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                    int artistColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                    int albumIdColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                    int dataColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

                    String title = cursor.getString(titleColumnIndex);
                    System.out.println(title);
                    String artist = cursor.getString(artistColumnIndex);
                    String filePath = cursor.getString(dataColumnIndex);
                    Long albumId = cursor.getLong(albumIdColumnIndex);
                    Uri albumArtUri = ContentUris.withAppendedId(Uri
                            .parse("content://media/external/audio/albumart"), albumId);

                    ContentValues values = new ContentValues();
                    values.put(SongDbHelper.COLUMN_TITLE, title);
                    values.put(SongDbHelper.COLUMN_ARTIST, artist);
                    values.put(SongDbHelper.COLUMN_FILE_PATH, filePath);
                    values.put(SongDbHelper.COLUMN_COVER_IMAGE_URL, String.valueOf(albumArtUri));
                    songs.add(new Song(title,filePath,artist,albumArtUri.toString()));
                    if(!SongDbHelper.songExists(db, filePath)){
                        long newRowId = db.insert(SongDbHelper.TABLE_NAME, null, values);
                    }
                }
            } finally {
                System.out.println(cursor.getCount());
                cursor.close();
                db.close();
            }
        }
    }
}