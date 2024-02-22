package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SongAdapter  extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    public static List<Song> songs;
    public MusicPlayer musicPlayer;
    private Context context;

    public SongAdapter(List<Song> songs, Context context) {
        this.songs = songs;
        this.context = context;
        musicPlayer = new MusicPlayer(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item,parent,false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    int position = ((RecyclerView) parent).getChildAdapterPosition(v);
                    if (position != RecyclerView.NO_POSITION) {
                        Song song = songs.get(position);
                        String filePath = song.getFilePath();
                        Intent intent = new Intent(parent.getContext(), SongActivity.class);

                        if(MusicPlayer.mediaPlayer != null &&
                                MusicPlayer.mediaPlayer.isPlaying() && filePath.equals(MusicPlayer.currentSongFilePath)){
                        }else{
                            Intent playIntent = new Intent(v.getContext(), MusicPlayer.class);
                            playIntent.setAction("PLAY");

                            playIntent.putExtra("filePath", filePath);
                            v.getContext().startService(playIntent);
                        }

                        intent.putExtra("songTitle", song.getTitle());
                        intent.putExtra("songArtist", song.getArtist());
                        intent.putExtra("coverImageUrl", song.getCoverImageUrl());
                        intent.putExtra("filePath", song.getFilePath());
                        parent.getContext().startActivity(intent);
                    }
                }catch (Exception e){
                    System.out.println("Err: " + e.getLocalizedMessage());
                    Toast.makeText(context, "error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT);
                }
            }
        });

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.ViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.songTitle.setText(song.getTitle());
        holder.songArtist.setText(song.getArtist());

        Glide.with(holder.itemView.getContext())
                .load(song.getCoverImageUrl())
                .placeholder(R.drawable.default_image_cover)
                .error(R.drawable.default_image_cover)
                .into(holder.songImageCover);

        ImageButton playButton = holder.itemView.findViewById(R.id.playImageButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playIntent = new Intent(holder.itemView.getContext(), MusicPlayer.class);
                playIntent.setAction("PLAY");
                String filePath = song.getFilePath();
                playIntent.putExtra("filePath", filePath);
                holder.itemView.getContext().startService(playIntent);
            }
        });

        ImageButton pauseButton = holder.itemView.findViewById(R.id.pauseImageButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopIntent = new Intent(holder.itemView.getContext(), MusicPlayer.class);
                stopIntent.setAction("PAUSE");
                holder.itemView.getContext().startService(stopIntent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle, songArtist, songDuration;
        ImageView songImageCover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.songTitle);
            songArtist = itemView.findViewById(R.id.songArtist);
//            tvDuration = itemView.findViewById(R.id.song);
            songImageCover = itemView.findViewById(R.id.songImageCover);
        }
    }
}