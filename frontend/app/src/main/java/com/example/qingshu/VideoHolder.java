package com.example.qingshu;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;



public class VideoHolder extends RecyclerView.ViewHolder {
    public PlayerView player;

    public VideoHolder(@NonNull View view) {
        super(view);
        player = view.findViewById(R.id.player_view);
    }
}
