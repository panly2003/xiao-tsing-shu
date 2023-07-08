package com.example.qingshu;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// Extends the InfoAdapter class to RecyclerView.InfoAdapter
// and implement the unimplemented methods
public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {
    public ArrayList<Info> articles;
    Context context;

    private final View.OnClickListener onClickListener;
    // Constructor for initialization
    public InfoAdapter(Context context, ArrayList<Info> articles, View.OnClickListener onClickListener) {
        this.context = context;
        this.articles = articles;
        this.onClickListener = onClickListener;
    }
    @NonNull
    @Override
    public InfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoAdapter.ViewHolder holder, int position) {
        Info info = articles.get(position);
        holder.title.setText(info.title);
        holder.username.setText(info.user_name);
        holder.likes.setText(String.valueOf(info.likes));
        String requestUrl = BuildConfig.URL + "/checklike";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", LogIn.UserId)
                .add("article_id", info.article_id);

        Request request = new Request.Builder()
                .url(requestUrl)
                .post(builder.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                String res = Objects.requireNonNull(response.body()).string();
                JSONObject jsonObject = JSON.parseObject(res);
                System.out.println(res);
                try {
                    if(jsonObject.getString("status").equals("success")){
                        int liked = jsonObject.getInteger("liked");
                        if (liked == 1)
                            holder.isLiked.setImageResource(R.drawable.liked);
                        else
                            holder.isLiked.setImageResource(R.drawable.like);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        holder.ID.setText(info.article_id);
        Glide.with(holder.avatar.getContext())
                .load(info.user_avatar)
                .circleCrop()
                .into(holder.avatar);
        if (!info.cover.equals(BuildConfig.URL))
            Glide.with(holder.cover.getContext())
                    .load(info.cover)
                    .transform(new RoundedCorners(20))
                    .into(holder.cover);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        // Returns number of items currently available in InfoAdapter
        return articles.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        ImageView avatar;
        ImageView isLiked;
        TextView title;
        TextView username;
        TextView likes;
        TextView ID;

        public ViewHolder(View view) {
            super(view);
            ID = view.findViewById(R.id.article_id);
            cover = view.findViewById(R.id.imageView);
            avatar = view.findViewById(R.id.avatar);
            title = view.findViewById(R.id.title);
            likes = view.findViewById(R.id.like);
            isLiked = view.findViewById(R.id.isLiked);
            username = view.findViewById(R.id.username);
            view.setOnClickListener(onClickListener);
        }
    }
}
