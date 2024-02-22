package com.example.musicplayer;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Song{
    private final String title;
    private final String filePath;
    private final String artist;
    private final String coverImageUrl;

    public Song(String title, String filePath, String artist, String coverImageUrl) {
        this.title = title;
        this.filePath = filePath;
        this.artist = artist;
        this.coverImageUrl = coverImageUrl;
    }

    public String getTitle() {return title;}

    public String getFilePath() {return filePath;}

    public String getArtist() {return artist;}

    public String getCoverImageUrl() {return coverImageUrl;}

}

