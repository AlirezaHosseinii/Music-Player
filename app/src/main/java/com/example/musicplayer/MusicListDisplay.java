package com.example.musicplayer;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MusicListDisplay extends ArrayAdapter<MusicListDisplay.MusicInfo> {
    private Context context;
    MusicPlayer musicPlayer;
    public MusicListDisplay(Context context, ArrayList<MusicInfo> musicInfos) {
        super(context, R.layout.music_item, R.id.musicItemTextView, musicInfos);
        this.context = context;
        musicPlayer = new MusicPlayer(context);
    }

    public static class MusicInfo{
        private String title;
        private String artist;
        private String duration;
        private String filePath;
        private Uri albumArtUri;
        public MusicInfo(String title,String artist, String duration, String filePath, Uri albumArtUri){
            this.title = title;
            this.artist = artist;
            this.duration = duration;
            this.filePath = filePath;
            this.albumArtUri = albumArtUri;
        }
    }


    public static ArrayList<MusicInfo> getMusicInfos(Context context) {

        Long albumArt;
        String title = "";
        String artist = "";
        String filePath = "";
        String duration = "";
        Uri uriAlbum = null;
        ArrayList<MusicInfo> musicInfos = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();
        final Uri[] uri = {MediaStore.Audio.Media.EXTERNAL_CONTENT_URI};
        Cursor cursor = contentResolver.query(uri[0], null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int dataColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            do {
                title = cursor.getString(titleColumn);
                artist = cursor.getString(artistColumn);
                filePath = cursor.getString(dataColumn);
                duration = String.valueOf(cursor.getLong(durationColumn));

                MainActivity.musicsHashMap.put(title, filePath);

                int albumArtColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                if (albumArtColumnIndex >= 0) {
                    albumArt = cursor.getLong(albumArtColumnIndex);
                    Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
                    uriAlbum = ContentUris.withAppendedId(albumArtUri, albumArt);
                }

                MusicInfo musicInfo = new MusicInfo(title, artist, duration, filePath, uriAlbum);
                musicInfos.add(musicInfo);

            } while (cursor.moveToNext());
        }else {
            Toast.makeText(context, "No music files found.", Toast.LENGTH_SHORT).show();
        }
        cursor.close();

        return musicInfos;
    }

    protected void displayMusicList (ListView musicListView, ArrayList<MusicInfo> musicInfos){

        MusicListDisplay adapter = new MusicListDisplay(context, musicInfos) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                ImageView albumArtImageView = view.findViewById(R.id.musicItemAlbumArtImageView);

                ArrayList<Uri> albumArtUris = new ArrayList<>();
                ArrayList<String> filePaths = new ArrayList<>();
                for(MusicInfo musicInfo: musicInfos) {
                    albumArtUris.add(musicInfo.albumArtUri);
                    filePaths.add(musicInfo.filePath);
                }

                Glide.with(context)
                        .load(albumArtUris.get(position))
                        .placeholder(R.drawable.default_image_cover)
                        .error(R.drawable.default_image_cover)
                        .into(albumArtImageView);

                ImageButton playButton = view.findViewById(R.id.playImageButton);
                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        musicPlayer.playMusic(filePaths.get(position));
                    }
                });

                ImageButton pauseButton = view.findViewById(R.id.pauseImageButton);
                pauseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        musicPlayer.pauseMusic();
                    }
                });

                return view;
            }
        };
        musicListView.setAdapter(adapter);
    }
}

