package com.example.qingshu;

import android.net.Uri;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.util.BannerUtils;

import java.util.List;

public class MultipleTypesAdapter extends BannerAdapter<DataBean, RecyclerView.ViewHolder> {
    private SparseArray<RecyclerView.ViewHolder> mVHMap = new SparseArray<>();

    public MultipleTypesAdapter(List<DataBean> mData) {
        super(mData);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                return new ImageHolder(BannerUtils.getView(parent, R.layout.banner_image));
            case 2:
                return new VideoHolder(BannerUtils.getView(parent, R.layout.banner_video));
        }
        return new ImageHolder(BannerUtils.getView(parent, R.layout.banner_image));
    }

    @Override
    public int getItemViewType(int position) {
        return getRealData(position).viewType;
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, DataBean data, int position, int size) {
        int viewType = holder.getItemViewType();
        switch (viewType) {
            case 1:
                ImageHolder imageHolder = (ImageHolder) holder;
                mVHMap.append(position,imageHolder);
                Glide.with(imageHolder.imageView.getContext())
                        .load(data.imageUrl)
                        .into(imageHolder.imageView);
                break;
            case 2:
                VideoHolder videoHolder = (VideoHolder) holder;
                mVHMap.append(position, videoHolder);
                Uri videoUri = Uri.parse(data.imageUrl);
                ExoPlayer player = new ExoPlayer.Builder(holder.itemView.getContext()).build();
                player.addMediaItem(/* index= */ 0, MediaItem.fromUri(videoUri));
                videoHolder.player.setPlayer(player);
                player.prepare();
                break;

        }
    }

    public SparseArray<RecyclerView.ViewHolder> getVHMap() {
        return mVHMap;
    }
}
