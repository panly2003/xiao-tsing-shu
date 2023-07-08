package com.example.qingshu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.qingshu.ui.mine.Profile;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.noties.markwon.Markwon;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Detail extends AppCompatActivity {
    private ArrayList<Comment> comments = new ArrayList<>();
    private CommentAdapter commentAdapter;
    private RecyclerView recyclerView;
    private TextView mName;
    private TextView mContent;
    private TextView mTitle;
    private TextView mTime_Address;
    private TextView mCommentNum1;
    private TextView mCommentNum2;
    private EditText mComment;
    private TextView mStar, mLike;
    private ImageView star, like;
    private ImageView mForward, mAvatar;
    private String article_id;
    private TextView followBtn;
    private int liked = 0, stared = 0;
    private int relation = 0; // 未关注为0，已关注为1，拉黑为-1
    private String post_user_id; // 发布该动态的用户id
    private Context context = this;
    private Banner<DataBean, MultipleTypesAdapter> banner;
    private static final String LOG_TAG
            = Detail.class.getSimpleName();
    private void forwardMessage() {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, mTitle.getText());
        startActivity(Intent.createChooser(sendIntent, "分享"));
    }

    private void followOrUnblockSomebody(){
        if(relation == -1){ // 如果已拉黑，点击则拉出黑名单
            String requestUrl = BuildConfig.URL + "/user-block";
            OkHttpClient client = new OkHttpClient();
            FormBody.Builder builder = new FormBody.Builder()
                    .add("user_id", LogIn.UserId)
                    .add("user_block_id", post_user_id);

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
                            });
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        else{ // 关注or取关
            String requestUrl = BuildConfig.URL + "/user-follow";
            OkHttpClient client = new OkHttpClient();
            FormBody.Builder builder = new FormBody.Builder()
                    .add("user_id", LogIn.UserId)
                    .add("user_follow_id", post_user_id);

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

    public int getFileType(String fileName) {
        String[] parts = fileName.split("\\.");
        String extension = "";
        if(parts.length > 0) {
            extension = parts[parts.length - 1];
        }
        extension = extension.toLowerCase();
        switch(extension) {
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "bmp":
            case "webp":
                return 1;
            case "mp4":
            case "3gp":
            case "mkv":
            case "webm":
            case "ts":
            case "avi":
                return 2;
            default:
                return 3;
        }
    }

    public void getDetail() {
        String requestUrl = BuildConfig.URL + "/article-get";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("article_id", article_id);

        Request request = new Request.Builder()
                .url(requestUrl)
                .post(builder.build())
                .build();
        Context ctx = this;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                String res = Objects.requireNonNull(response.body()).string();
                JSONObject jsonObject = JSON.parseObject(res);
                System.out.println("getDetail: ");
                System.out.println(res);
                try {
                    if(jsonObject.getString("status").equals("success")){
                        post_user_id = jsonObject.getString("user_id");
                        final Markwon markwon = Markwon.create(context);
                        List<DataBean> medias = new ArrayList<>();
                        JSONArray jsonMedia = jsonObject.getJSONArray("media");
                        for (int i = 0; i < jsonMedia.size(); ++i) {
                            String url = BuildConfig.URL + jsonMedia.getString(i);
                            medias.add(new DataBean(url, "media" + i, getFileType(url)));
                        }
                        runOnUiThread(() -> {
                            checkFollowAndBlock(); // 加载关注状态：关注、已关注、已拉黑
                            mCommentNum1.setText("共" + jsonObject.getString("comments") + "条评论");
                            mCommentNum2.setText(jsonObject.getString("comments"));
                            mTitle.setText(jsonObject.getString("title"));
                            mTime_Address.setText(jsonObject.getString("time") + " " + jsonObject.getString("address"));
                            mLike.setText(jsonObject.getString("likes"));
                            mStar.setText(jsonObject.getString("stars"));
                            mName.setText(jsonObject.getString("user_name"));
                            markwon.setMarkdown(mContent, jsonObject.getString("text"));
                            Glide.with(mAvatar.getContext())
                                    .load(BuildConfig.URL + jsonObject.getString("user_avatar"))
                                    .circleCrop()
                                    .into(mAvatar);
                            // 点击avatar，如果是自己则不动，否则跳到他人主页
                            mAvatar.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v){
                                    if(post_user_id.equals(LogIn.UserId)){

                                    }
                                    else{
                                        Bundle bundle = new Bundle();//存各种数据
                                        bundle.putString("user_id",post_user_id);
                                        Intent intent = new Intent(Detail.this, OthersProfile.class);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                }
                            });
                            banner.addBannerLifecycleObserver((LifecycleOwner) ctx)//添加生命周期观察者
                                    .setAdapter(new MultipleTypesAdapter(medias))
                                    .setIndicator(new CircleIndicator(ctx));
                            banner.isAutoLoop(false);
                        });
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void getLike() {
        like.setOnClickListener(v -> {
            String requestUrl = BuildConfig.URL + "/like";
            OkHttpClient client = new OkHttpClient();
            FormBody.Builder builder = new FormBody.Builder()
                    .add("user_id", LogIn.UserId)
                    .add("article_id", article_id);

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
                            liked ^= 1;
                            int likes = Integer.parseInt(mLike.getText().toString());
                            System.out.println(likes);
                            runOnUiThread(() -> {
                                if (liked == 1) {
                                    like.setImageResource(R.drawable.liked);
                                    mLike.setText(String.valueOf(likes + 1));
                                }
                                else {
                                    like.setImageResource(R.drawable.like);
                                    mLike.setText(String.valueOf(likes - 1));
                                }
                            });
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });
        String requestUrl = BuildConfig.URL + "/checklike";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", LogIn.UserId)
                .add("article_id", article_id);

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
                        liked = jsonObject.getInteger("liked");
                        runOnUiThread(() -> {
                            if (liked == 1)
                                like.setImageResource(R.drawable.liked);
                            else
                                like.setImageResource(R.drawable.like);
                        });
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public void getStar() {
        star.setOnClickListener(v -> {
            String requestUrl = BuildConfig.URL + "/star";
            OkHttpClient client = new OkHttpClient();
            FormBody.Builder builder = new FormBody.Builder()
                    .add("user_id", LogIn.UserId)
                    .add("article_id", article_id);

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
                            stared ^= 1;
                            int stars = Integer.parseInt(mStar.getText().toString());
                            System.out.println(stars);
                            runOnUiThread(() -> {
                                if (stared == 1) {
                                    star.setImageResource(R.drawable.stared);
                                    mStar.setText(String.valueOf(stars + 1));
                                }
                                else {
                                    star.setImageResource(R.drawable.star);
                                    mStar.setText(String.valueOf(stars - 1));
                                }
                            });
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });
        String requestUrl = BuildConfig.URL + "/checkstar";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", LogIn.UserId)
                .add("article_id", article_id);

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
                        stared = jsonObject.getInteger("stared");
                        runOnUiThread(() -> {
                            if (stared == 1)
                                star.setImageResource(R.drawable.stared);
                            else
                                star.setImageResource(R.drawable.star);
                        });
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    void refreshComments() {
        String requestUrl = BuildConfig.URL + "/article-comments";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("article_id", article_id);

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
                        JSONArray jsonComments = jsonObject.getJSONArray("comments");
                        comments = new ArrayList<>();
                        for (int i = 0; i < jsonComments.size(); ++i) {
                            JSONObject jsonComment = jsonComments.getJSONObject(i);
                            comments.add(new Comment(jsonComment.getString("user_id"),
                                    jsonComment.getString("user_name"),
                                    jsonComment.getString("user_avatar"),
                                    jsonComment.getString("content"),
                                    jsonComment.getString("time")));
                        }
                        commentAdapter.comments = comments;
                        runOnUiThread(() -> {
                            commentAdapter.notifyDataSetChanged();
                            mCommentNum1.setText("共" + comments.size() + "条评论");
                            mCommentNum2.setText(String.valueOf(comments.size()));
                        });
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    void clickOthers() {
        mComment.clearFocus();
        System.out.println("clicked");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(mComment.getWindowToken(), 0);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkFollowAndBlock(){
        if (LogIn.UserId.equals(post_user_id)) {
            TextView mFollow = findViewById(R.id.follow);
            mFollow.setVisibility(View.GONE);
            return ;
        }
        // 获取relation
        String requestUrl = BuildConfig.URL + "/user-checkrelation";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", LogIn.UserId)
                .add("user_check_id", post_user_id); // 缺少user_check_id

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
                        relation = jsonObject.getInteger("relation");
                        runOnUiThread(() -> {
                            if(relation == 1){
                                followBtn.setText("已关注");
                            }
                            else if(relation == 0){
                                followBtn.setText("关注");
                            }
                            else{
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar myToolbar = findViewById(R.id.titleBar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);
        findViewById(R.id.main).setOnClickListener(v -> clickOthers());
        findViewById(R.id.titleBar).setOnClickListener(v -> clickOthers());

        mContent = findViewById(R.id.content);
        mTitle = findViewById(R.id.title);

        mTime_Address = findViewById(R.id.time);
        mCommentNum1 = findViewById(R.id.number_comments);
        mCommentNum2 = findViewById(R.id.comment);
        mStar = findViewById(R.id.star);
        mLike = findViewById(R.id.like);
        mName = findViewById(R.id.username);
        mAvatar =findViewById(R.id.avatar);
        star = findViewById(R.id.tostar);
        like = findViewById(R.id.tolike);
        mComment = findViewById(R.id.sendComment);
        mComment.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                String requestUrl = BuildConfig.URL + "/comment";
                OkHttpClient client = new OkHttpClient();
                FormBody.Builder builder = new FormBody.Builder()
                        .add("user_id", LogIn.UserId)
                        .add("article_id", article_id)
                        .add("content", v.getText().toString());

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
                                refreshComments();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                System.out.println(v.getText());
                v.setText("");
                return true;
            }
            return false;
        });
        mComment.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                star.setVisibility(View.GONE);
                mStar.setVisibility(View.GONE);
                like.setVisibility(View.GONE);
                mLike.setVisibility(View.GONE);
                findViewById(R.id.iconComment).setVisibility(View.GONE);
                mCommentNum2.setVisibility(View.GONE);
            } else {
                star.setVisibility(View.VISIBLE);
                mStar.setVisibility(View.VISIBLE);
                like.setVisibility(View.VISIBLE);
                mLike.setVisibility(View.VISIBLE);
                findViewById(R.id.iconComment).setVisibility(View.VISIBLE);
                mCommentNum2.setVisibility(View.VISIBLE);
            }
        });

        mForward = findViewById(R.id.forward);
        mForward.setOnClickListener(v -> forwardMessage());
        followBtn = findViewById(R.id.follow);
        followBtn.setOnClickListener(v -> followOrUnblockSomebody());
        Intent intent = getIntent();
        article_id = intent.getStringExtra("ID");
        getDetail();
        getLike();
        getStar();

        initComments();
        recyclerView = findViewById(R.id.comments);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        commentAdapter = new CommentAdapter(comments);
        recyclerView.setAdapter(commentAdapter);
        refreshComments();

        banner = findViewById(R.id.banner);
    }
    private void initComments() {
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }
}