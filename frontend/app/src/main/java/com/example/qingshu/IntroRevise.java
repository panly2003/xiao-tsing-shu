package com.example.qingshu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IntroRevise extends AppCompatActivity {
    Button change_btn;
    EditText edt_intro;

    public void setProfile(){
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
                    runOnUiThread(() -> {
                        edt_intro.setText(profile_text);
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_revise);

        Toolbar myToolbar = findViewById(R.id.titleBar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);

        edt_intro = findViewById(R.id.edt_intro);
        setProfile();
        change_btn = findViewById(R.id.btn_change);
        change_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String requestUrl = "http:////101.132.65.207:8000/xiao-tsing-shu/user-setprofile";
                OkHttpClient client = new OkHttpClient();
                FormBody.Builder builder = new FormBody.Builder()
                        .add("user_id", LogIn.UserId)
                        .add("user_new_profile", edt_intro.getText().toString());

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
                        }
                    }
                );
                IntroRevise.this.finish();
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