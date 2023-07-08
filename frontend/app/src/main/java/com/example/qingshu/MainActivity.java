package com.example.qingshu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String sharedPreferenceFile = "com.example.android.qingshu";
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(sharedPreferenceFile, MODE_PRIVATE);
        String userId = sharedPreferences.getString("UserId", null);
        System.out.println(userId);
        if (userId != null){
            LogIn.UserId = userId;
            Intent intent = new Intent(this, Nav.class);
            startActivity(intent);
        }

//        ImageView logo = findViewById(R.id.logo);
//        logo.setImageResource(R.drawable.logo);
        Button signUp = findViewById(R.id.btn_signup);
        signUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
            }
        });

        Button logIn = findViewById(R.id.btn_login);
        logIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, LogIn.class);
                startActivity(intent);
            }
        });

        // 系统通知
        final Runnable task = new Runnable() {
            public void run() {
                String requestUrl_ = BuildConfig.URL + "/notice-get";
                OkHttpClient client_ = new OkHttpClient();
                FormBody.Builder builder_ = new FormBody.Builder()
                        .add("user_id", LogIn.UserId);

                Request request_ = new Request.Builder()
                        .url(requestUrl_)
                        .post(builder_.build())
                        .build();
                client_.newCall(request_).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {}
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                        String res = Objects.requireNonNull(response.body()).string();
                        JSONObject jsonObject = JSON.parseObject(res);
//                        System.out.println(res);
                        try {
                            if(jsonObject.getString("status").equals("success")){
                                JSONArray noticeList = jsonObject.getJSONArray("notices");
                                for(int i = 0; i < noticeList.size(); ++i){
                                    // 显示系统通知
                                    SystemNotification.displayNotification(MainActivity.this, "小清书", noticeList.getString(i));
                                }
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        };
        scheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);

    }
}