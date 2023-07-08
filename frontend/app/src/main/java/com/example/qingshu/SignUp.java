package com.example.qingshu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SignUp extends AppCompatActivity {
    private EditText username, password, confirmPassword, nickname;
    private TextView result;
    private Button signUp;

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
        setContentView(R.layout.activity_sign_up);

        signUp = findViewById(R.id.btn_signup);
        username = findViewById(R.id.edt_account);
        password = findViewById(R.id.edt_password);
        confirmPassword = findViewById(R.id.edt_confirmPassword);
        nickname = findViewById(R.id.edt_nickname);
        result = findViewById(R.id.result);

        signUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String usr = username.getText().toString();
                String pwd = password.getText().toString();
                String confirmPwd = confirmPassword.getText().toString();
                String nik = nickname.getText().toString();

                if(usr.equals("")){
                    result.setText("用户名不能为空");
                }
                else if(pwd.equals("")){
                    result.setText("密码不能为空");
                }
                else if(!pwd.equals(confirmPwd)){
                    result.setText("两次密码输入不一致");
                }
                else if(nik.equals("")){
                    result.setText("昵称不能为空");
                }
                else{
                    String requestUrl = BuildConfig.URL + "/user-signup";
                    try{
                        OkHttpClient client = new OkHttpClient();

                        RequestBody body = new FormBody.Builder()
                                //类似左边键名，右边键值
                                .add("user_id", usr)
                                .add("user_password", pwd)
                                .add("user_name", nik)
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
                                    Intent intent = new Intent(SignUp.this, SignUpSuccess.class);
                                    startActivity(intent);
                                }
                            }
                        });

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}