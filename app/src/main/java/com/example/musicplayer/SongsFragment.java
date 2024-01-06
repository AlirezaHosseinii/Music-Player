package com.example.musicplayer;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class SongsFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Song> songs = new ArrayList<>();
    public SongsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.songs_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        fetchSongs();

        SongAdapter adapter = new SongAdapter(songs, getContext());
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void fetchSongs() {
        System.out.println("running");
        ContentResolver contentResolver = getActivity().getContentResolver();
        System.out.println("running2");

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        System.out.println("cursor: " + cursor == null);
        System.out.println(cursor.getCount());
        System.out.println("running3");
        File file = new File("/storage/emulated/0/Audiobooks/sohrab.mp3");
        System.out.println(file.exists());
        if (cursor != null) {
            System.out.println("here cursor");
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

                songs.add(new Song(title,filePath ,artist, albumArtUri.toString()));
            }
        }
        if (cursor != null) {
            cursor.close();
        }
    }
}