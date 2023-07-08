package com.example.qingshu.ui.mine;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.qingshu.BuildConfig;
import com.example.qingshu.CareList;
import com.example.qingshu.Detail;
import com.example.qingshu.FanList;
import com.example.qingshu.HateList;
import com.example.qingshu.Info;
import com.example.qingshu.InfoAdapter;
import com.example.qingshu.LogIn;
import com.example.qingshu.MainActivity;
import com.example.qingshu.ProfileRevise;
import com.example.qingshu.R;
import com.example.qingshu.SystemNotification;

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


public class Profile extends Fragment {
    Button logOut;
    ImageView avatar;
    TextView username;
    TextView id;
    TextView profile;
    Button revise;
    TextView care;
    TextView fan;
    TextView hate;
    RecyclerView recyclerView;
    InfoAdapter infoAdapter;
    private ArrayList<Info> articles = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private String sharedPreferenceFile = "com.example.android.qingshu";


    public void onCardClick(View view) {
        Intent intent = new Intent(getActivity(), Detail.class);
        TextView id = view.findViewById(R.id.article_id);
        intent.putExtra("ID", id.getText());
        startActivity(intent);
    }
    public void getArticles() {
        String userid = LogIn.UserId;
        String type = null;
        String requestUrl = BuildConfig.URL + "/article-user";

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", userid);

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
                        articles = new ArrayList<>();
                        JSONArray jsonArticles = jsonObject.getJSONArray("articles");
                        for (int i = 0; i < jsonArticles.size(); ++i) {
                            JSONObject jsonArticle = jsonArticles.getJSONObject(i);
                            Info info = new Info(jsonArticle.getString("article_id"),
                                    jsonArticle.getString("user_id"),
                                    jsonArticle.getString("user_name"),
                                    jsonArticle.getString("user_avatar"),
                                    jsonArticle.getString("title"),
                                    jsonArticle.getString("text"),
                                    jsonArticle.getString("cover"),
                                    jsonArticle.getString("address"),
                                    jsonArticle.getString("type"),
                                    jsonArticle.getString("time"),
                                    jsonArticle.getIntValue("likes"),
                                    jsonArticle.getIntValue("stars"));
                            articles.add(info);
                        }
                        infoAdapter.articles = articles;
                        if (getActivity() != null && isAdded()) {
                            getActivity().runOnUiThread(() -> {
                                recyclerView.stopScroll();
                                infoAdapter.notifyDataSetChanged();
                            });
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void loadCareNum(){
        String requestUrl = BuildConfig.URL + "/user-getfollowing";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", LogIn.UserId);

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
                System.out.println(res);
                JSONObject jsonObject = JSON.parseObject(res);
                System.out.println(res);
                try {
                    if(jsonObject.getString("status").equals("success")){
                        JSONArray following = jsonObject.getJSONArray("followinglist");
                        int careNum = following.size();
                        System.out.println(careNum);
                        if (getActivity() != null && isAdded()) {
                            getActivity().runOnUiThread(() -> {
                                care.setText(String.valueOf(careNum) + "\n关注");
                            });
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void loadFanNum(){
        String requestUrl = BuildConfig.URL + "/user-getfollower";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", LogIn.UserId);

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
                        JSONArray follower = jsonObject.getJSONArray("followerlist");
                        int fanNum = follower.size();
                        if (getActivity() != null && isAdded()) {
                            getActivity().runOnUiThread(() -> {
                                fan.setText(String.valueOf(fanNum) + "\n粉丝");
                            });
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void loadHateNum(){
        String requestUrl = BuildConfig.URL + "/user-getblock";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", LogIn.UserId);

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
                        JSONArray block = jsonObject.getJSONArray("blocklist");
                        int hateNum = block.size();
                        if (getActivity() != null && isAdded()) {
                            getActivity().runOnUiThread(() -> {
                                hate.setText(String.valueOf(hateNum) + "\n黑名单");
                            });
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public void loadProfile(){
        String requestUrl = "http:////101.132.65.207:8000/xiao-tsing-shu/user-getprofile";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", LogIn.UserId);

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
                    String profile_text = jsonObject.getString("user_profile");
                    if (getActivity() != null && isAdded()) {
                        getActivity().runOnUiThread(() -> {
                            profile.setText(profile_text);
                        });
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void loadAvatar(){
        String requestUrl = BuildConfig.URL + "/user-getavatar";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", LogIn.UserId);

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
                    String avatar_url = jsonObject.getString("user_avatar");
                    if (getActivity() != null && isAdded()) {
                        getActivity().runOnUiThread(() -> {
                            Glide.with(avatar.getContext())
                                    .load(BuildConfig.URL + avatar_url)
                                    .circleCrop().into(avatar);
                        });
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public void loadUsername(){
        String requestUrl = BuildConfig.URL + "/user-getname";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", LogIn.UserId);

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
                    String name = jsonObject.getString("user_name");
                    if (getActivity() != null && isAdded()) {
                        getActivity().runOnUiThread(() -> {
                            username.setText(name);
                        });
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        logOut = view.findViewById(R.id.btn_logout);
        logOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                sharedPreferences = getActivity().getSharedPreferences(sharedPreferenceFile, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        revise = (Button) view.findViewById(R.id.btn_revise);
        revise.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), ProfileRevise.class);
                startActivity(intent);
            }
        });
        care = (TextView) view.findViewById(R.id.cares);
        care.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), CareList.class);
                startActivity(intent);
            }
        });
        fan = (TextView) view.findViewById(R.id.fans);
        fan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), FanList.class);
                startActivity(intent);
            }
        });
        hate = (TextView) view.findViewById(R.id.hates);
        hate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), HateList.class);
                startActivity(intent);
            }
        });
        id = (TextView) view.findViewById(R.id.id);
        id.setText("小清书账号："+LogIn.UserId);
        username = (TextView) view.findViewById(R.id.nickname);
        profile = (TextView) view.findViewById(R.id.textView);
        avatar = (ImageView) view.findViewById(R.id.user);

        loadUsername();
        loadProfile();
        loadAvatar();
        loadCareNum();
        loadFanNum();
        loadHateNum();
        recyclerView = view.findViewById(R.id.list);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        infoAdapter = new InfoAdapter(view.getContext(), articles, v -> {onCardClick(v);});
        recyclerView.setAdapter(infoAdapter);
        getArticles();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
//        Intent serviceIntent = new Intent(getActivity(), MyService.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            getActivity().startForegroundService(serviceIntent);
//        } else {
//            getActivity().startService(serviceIntent);
//        }
        loadUsername();
        loadProfile();
        loadAvatar();
        loadCareNum();
        loadFanNum();
        loadHateNum();
        getArticles();
    }
}