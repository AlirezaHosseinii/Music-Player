package com.example.musicplayer;

public class Song {
    private String title;
    private String duration;
    private String artist;
    private String coverImageUrl;

    public Song(String title, String duration, String artist, String coverImageUrl) {
        this.title = title;
        this.duration = duration;
        this.artist = artist;
        this.coverImageUrl = coverImageUrl;
    }

    public String getTitle() {return title;}

    public String getDuration() {return duration;}

    public String getArtist() {return artist;}

    public String getCoverImageUrl() {return coverImageUrl;}
}

