package com.example.qingshu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OthersProfile extends AppCompatActivity {
    private String s_userId;
    private int relation; // 0表示未关注，1表示已关注, -1表示已拉黑
    private TextView userId;
    private ImageView userAvatar;
    private TextView userNickname;
    private TextView userIntro;
    private TextView care;
    private TextView fan;
    private TextView hate;
    private TextView followBtn;
    private ImageView blockBtn;
    private ImageView chatBtn;
    private ArrayList<Info> articles = new ArrayList<>();
    RecyclerView recyclerView;
    InfoAdapter infoAdapter;

    public void onCardClick(View view) {
        Intent intent = new Intent(this, Detail.class);
        TextView id = view.findViewById(R.id.article_id);
        intent.putExtra("ID", id.getText());
        startActivity(intent);
    }

    // 获取用户文章
    public void getArticles() {
        String requestUrl = BuildConfig.URL + "/article-user";

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", s_userId);

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
                        runOnUiThread(() -> {
                            recyclerView.stopScroll();
                            infoAdapter.notifyDataSetChanged();
                        });
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public void loadNickname(){
        String requestUrl = BuildConfig.URL + "/user-getname";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", s_userId);

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
                        runOnUiThread(() -> {
                            userNickname.setText(jsonObject.getString("user_name"));
                        });
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void loadIntro(){
        String requestUrl = BuildConfig.URL + "/user-getprofile";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", s_userId);

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
                        runOnUiThread(() -> {
                            userIntro.setText(jsonObject.getString("user_profile"));
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
                .add("user_id", s_userId);

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
                        runOnUiThread(() -> {
                            String s_userAvatar = jsonObject.getString("user_avatar");
                            Glide.with(userAvatar.getContext())
                                    .load(BuildConfig.URL + s_userAvatar)
                                    .circleCrop()
                                    .into(userAvatar);
                        });
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void loadId(){
        userId.setText("小清书账号：" + s_userId);
    }

    public void loadCareNum(){
        String requestUrl = BuildConfig.URL + "/user-getfollowing";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", s_userId);

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
                        runOnUiThread(() -> {
                            care.setText(String.valueOf(careNum) + "\n关注");
                        });
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
                .add("user_id", s_userId);

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
                        runOnUiThread(() -> {
                            fan.setText(String.valueOf(fanNum) + "\n粉丝");
                        });
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
                .add("user_id", s_userId);

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
                        runOnUiThread(() -> {
                            hate.setText(String.valueOf(hateNum) + "\n黑名单");
                        });
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void followOrUnblockSomebody(){
        if(relation == -11){ // 本来是已拉黑，拉出黑名单
            String requestUrl = BuildConfig.URL + "/user-block";
            OkHttpClient client = new OkHttpClient();
            FormBody.Builder builder = new FormBody.Builder()
                    .add("user_id", LogIn.UserId)
                    .add("user_block_id", s_userId);

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
                            runOnUiThread(() -> {
                                relation = 0;
                                followBtn.setText("关注");
                                blockBtn.setVisibility(View.INVISIBLE);
                            });
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        else{ // 没有拉黑，关注/取关
            String requestUrl = BuildConfig.URL + "/user-follow";
            OkHttpClient client = new OkHttpClient();
            FormBody.Builder builder = new FormBody.Builder()
                    .add("user_id", LogIn.UserId)
                    .add("user_follow_id", s_userId);

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
                            runOnUiThread(() -> {
                                if (relation == 1) {
                                    relation = 0;
                                    followBtn.setText("关注");
                                }
                                else {
                                    relation = 1;
                                    followBtn.setText("已关注");
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

    private void blockSomebody(){
        String requestUrl = BuildConfig.URL + "/user-block";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", LogIn.UserId)
                .add("user_block_id", s_userId);

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
                        runOnUiThread(() -> {
                            if(relation != -1){
                                blockBtn.setVisibility(View.GONE);
                                // 本来关注了，取关
                                if(relation == 1){
                                    String requestUrl = BuildConfig.URL + "/user-follow";
                                    OkHttpClient client = new OkHttpClient();
                                    FormBody.Builder builder = new FormBody.Builder()
                                            .add("user_id", LogIn.UserId)
                                            .add("user_follow_id", s_userId);

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

                                                }
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    });
                                }
                                relation = -1;
                                followBtn.setText("已拉黑");
                            }
                        });
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void checkFollowAndBlock(){
        String requestUrl = BuildConfig.URL + "/user-checkrelation";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", LogIn.UserId)
                .add("user_check_id", s_userId);

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
                        relation = jsonObject.getInteger("relation");
                        runOnUiThread(() -> {
                            if(relation == -1){
                                // 右上角没有拉黑按钮
                                blockBtn.setVisibility(View.GONE);
                                // 紫色按钮为已拉黑
                                followBtn.setText("已拉黑");
                            }
                            else {
                                // 右上角有拉黑按钮
                                Glide.with(blockBtn.getContext())
                                        .load(R.drawable.blocked)
                                        .into(blockBtn);
                                // 判断是否关注
                                if(relation == 0){
                                    followBtn.setText("关注");
                                }
                                else{
                                    followBtn.setText("已关注");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        s_userId = bundle.getString("user_id");

        Toolbar myToolbar = findViewById(R.id.titleBar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.white_back);

        // 绑定
        userId = findViewById(R.id.id);
        userAvatar = findViewById(R.id.user);
        userNickname = findViewById(R.id.nickname);
        userIntro = findViewById(R.id.intro);
        care = findViewById(R.id.cares);
        fan = findViewById(R.id.fans);
        hate = findViewById(R.id.hates);
        followBtn = findViewById(R.id.btn_care);
        blockBtn = findViewById(R.id.btn_block);
        chatBtn = findViewById(R.id.btn_chat);

        // 加载avatar, id, nickname, intro
        loadAvatar();
        loadId();
        loadNickname();
        loadIntro();
        // 加载关注、粉丝、黑名单数量
        loadCareNum();
        loadFanNum();
        loadHateNum();

        // 加载关注/拉黑状态
        checkFollowAndBlock();

        // 关注/取关TA/将TA拉出黑名单
        followBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                followOrUnblockSomebody();
            }
        });

        // 屏蔽/取消屏蔽TA
        blockBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                blockSomebody();
            }
        });

        // TA的笔记
        recyclerView = findViewById(R.id.list);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        infoAdapter = new InfoAdapter(this, articles, v -> {onCardClick(v);});
        recyclerView.setAdapter(infoAdapter);
        getArticles();

        // 私信TA
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();//存各种数据
                bundle.putString("user_chat_id",s_userId);
                Intent intent = new Intent(OthersProfile.this, Chat.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Respond to the action bar's Up/Home button
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}