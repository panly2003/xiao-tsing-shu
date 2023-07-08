package com.example.qingshu;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    ArrayList<Comment> comments;
    // Constructor for initialization
    public CommentAdapter(ArrayList<Comment> comments) {
        this.comments = comments;
        Log.d("test", String.valueOf(comments.size()));
    }
    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.username.setText(comment.user_name);
        String textPart1 = comment.content + " ";
        String textPart2 = comment.time;
        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(Color.rgb(170,170,170));
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(textPart1 + textPart2);
        stringBuilder.setSpan(colorSpan2, textPart1.length(), textPart1.length() + textPart2.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        int fontSizePx = (int) (11 * holder.itemView.getContext().getResources().getDisplayMetrics().density);  // 设置字体大小，单位是像素
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(fontSizePx);
        stringBuilder.setSpan(sizeSpan, textPart1.length(), textPart1.length() + textPart2.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        holder.content.setText(stringBuilder);
        holder.user_id.setText(comment.user_id);
        //holder.time.setText(comment.time);
        Glide.with(holder.avatar.getContext())
                .load(comment.user_avatar)
                .circleCrop()
                .into(holder.avatar);
    }

    // Binding data to the into specified position

    @Override
    public int getItemCount() {
        // Returns number of items currently available in InfoAdapter
        return comments.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView user_id;
        ImageView avatar;
        TextView username;
        TextView content;
        public ViewHolder(View view) {
            super(view);
            avatar = view.findViewById(R.id.avatar);
            username = view.findViewById(R.id.username);
            content = view.findViewById(R.id.content);
            user_id = view.findViewById(R.id.user_id);
        }
    }
}