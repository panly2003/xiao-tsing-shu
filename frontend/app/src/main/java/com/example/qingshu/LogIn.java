package com.example.qingshu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LogIn extends AppCompatActivity {
    private EditText username, password;
    private TextView result;
    private Button logIn;

    public static String UserId = null;

    private SharedPreferences sharedPreferences;
    private String sharedPreferenceFile = "com.example.android.qingshu";
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
        setContentView(R.layout.activity_log_in);
        logIn = findViewById(R.id.btn_login);
        username = findViewById(R.id.edt_account);
        password = findViewById(R.id.edt_password);
        result = findViewById(R.id.result);
        logIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String usr = username.getText().toString();
                String pwd = password.getText().toString();
                String requestUrl = BuildConfig.URL + "/user-login";
                try{
                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = new FormBody.Builder()
                            //类似左边键名，右边键值
                            .add("user_id", usr)
                            .add("user_password", pwd)
                            .build();
                    Request request = new Request.Builder()
                            .url(requestUrl)
                            .post(body)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {}
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
                                UserId = usr;
                                sharedPreferences = getSharedPreferences(sharedPreferenceFile, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("UserId", UserId);
                                editor.apply();
                                Intent intent = new Intent(LogIn.this, Nav.class);
                                startActivity(intent);
                            }
                        }
                    });

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}