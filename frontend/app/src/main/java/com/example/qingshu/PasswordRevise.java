package com.example.qingshu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;

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

public class PasswordRevise extends AppCompatActivity {
    Button change_btn;
    EditText old_password;
    EditText new_password;
    EditText confirm_password;
    TextView result;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String res = (String) msg.obj;
            result.setText(res);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_revise);

        Toolbar myToolbar = findViewById(R.id.titleBar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);

        old_password = findViewById(R.id.old_password);
        new_password = findViewById(R.id.new_password);
        confirm_password = findViewById(R.id.confirm_password);
        result = findViewById(R.id.result);
        change_btn = findViewById(R.id.btn_change);
        change_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String pwd = new_password.getText().toString();
                String confirmPwd = confirm_password.getText().toString();
                if(pwd.equals("")){
                    result.setText("新密码不能为空");
                }
                else if(!pwd.equals(confirmPwd)){
                    result.setText("两次新密码输入不一致");
                }
                else {
                    String requestUrl = "http:////101.132.65.207:8000/xiao-tsing-shu/user-setpassword";
                    OkHttpClient client = new OkHttpClient();
                    FormBody.Builder builder = new FormBody.Builder()
                            .add("user_id", LogIn.UserId)
                            .add("user_password", old_password.getText().toString())
                            .add("user_new_password", new_password.getText().toString());

                    Request request = new Request.Builder()
                            .url(requestUrl)
                            .post(builder.build())
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                            String res = Objects.requireNonNull(response.body()).string();
                            JSONObject jsonObject = JSON.parseObject(res);
                            System.out.println(res);
                            String msg = jsonObject.getString("message");
                            if (msg != null) {
                                Message message = new Message();
                                message.obj = msg;
                                handler.sendMessage(message);
                            }
                            if (jsonObject.getString("status").equals("success")){
                                PasswordRevise.this.finish();
                            }
                        }
                    }
                    );
                }
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