package com.example.qingshu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder>{
    private List<Person> mUserList;  //数据源，在new此类的时候传入
//    private int relation; // 0表示无关系, 1表示follow，-1表示拉黑
    //静态内部类， 每个条目对应的布局
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView userNickname;
        TextView followBtn;
        private Handler handler = new Handler();
        public ViewHolder (View view)
        {
            super(view);
            userImage = (ImageView) view.findViewById(R.id.user);
            userNickname = (TextView) view.findViewById(R.id.nickname);
            followBtn = (TextView) view.findViewById(R.id.follow);
        }
    }

    //NewsAdapter的构造方法，加入了数据源参数，在构造的时候赋值给mNewsList
    public PersonAdapter(List <Person> userList){
        mUserList = userList;
    }
    //用于创建ViewHolder实例,并把加载的布局传入到ViewHolder的构造函数去
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_user ,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //是用于对子项的数据进行赋值,会在每个子项被滚动到屏幕内时执行。position得到当前项的News实例
    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        final int[] rel = new int[1];
        Person user = mUserList.get(position);
        holder.userNickname.setText(user.getNickname());
        Glide.with(holder.userImage.getContext())
                .load(BuildConfig.URL + user.getUserImage())
                .circleCrop().into(holder.userImage);

        Bundle bundle = new Bundle();
        bundle.putString("user_id", user.getUserId());

        holder.userImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(v.getContext(), OthersProfile.class);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });

        // 加载关注状态，三个状态：关注、已关注、已拉黑
        String requestUrl = BuildConfig.URL + "/user-checkrelation";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", LogIn.UserId)
                .add("user_check_id", user.getUserId());

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
                try {
                    if(jsonObject.getString("status").equals("success")){
                        int relation = jsonObject.getInteger("relation");
                        System.out.println("relation");
                        System.out.println(relation);
                        holder.handler.post(new Runnable() {
                            public void run() {
                                if (relation == 1) {
                                    holder.followBtn.setText("已关注");
                                }
                                else if(relation == 0){
                                    holder.followBtn.setText("关注");
                                }
                                else{
                                    holder.followBtn.setText("已拉黑");
                                }
                            }
                        });
                        rel[0] = relation;
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        holder.followBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // 若当前是已拉黑状态，点击该按钮，拉出黑名单
                if(rel[0] == -1){
                    String requestUrl = BuildConfig.URL + "/user-block";
                    OkHttpClient client = new OkHttpClient();
                    FormBody.Builder builder = new FormBody.Builder()
                            .add("user_id", LogIn.UserId)
                            .add("user_block_id", user.getUserId());

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
                                    holder.handler.post(new Runnable() {
                                        public void run() {
                                            rel[0] = 0;
                                            holder.followBtn.setText("关注");
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
                // 否则，关注/取关
                else{
                    String requestUrl = BuildConfig.URL + "/user-follow";
                    OkHttpClient client = new OkHttpClient();
                    FormBody.Builder builder = new FormBody.Builder()
                            .add("user_id", LogIn.UserId)
                            .add("user_follow_id", user.getUserId());

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
                                    holder.handler.post(new Runnable() {
                                        public void run() {
                                            if (rel[0] == 1) {
                                                rel[0] = 0;
                                                holder.followBtn.setText("关注");
                                            }
                                            else {
                                                rel[0] = 1;
                                                holder.followBtn.setText("已关注");
                                            }
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            }
        });
    }
    //返回RecyclerView的子项数目
    @Override
    public int getItemCount(){
        return mUserList.size();
    }
}
