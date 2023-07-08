package com.example.qingshu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Chat extends AppCompatActivity implements MessagesListAdapter.OnLoadMoreListener{
    private MessagesList messagesList;
    private MessageInput input;
    private ArrayList<Message> messages = new ArrayList<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private String user_chat_id;

    private MessagesListAdapter<Message> messagesListAdapter;

    public Date parseServerTime(String serverTime, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINESE);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        Date date = null;
        try {
            date = sdf.parse(serverTime);
        } catch (Exception e) {
        }
        return date;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar myToolbar = findViewById(R.id.titleBar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);

        Intent intent = getIntent(); //获取
        Bundle bundle = intent.getExtras(); //取出数据
        user_chat_id = bundle.getString("user_chat_id");

        messagesList = findViewById(R.id.messagesList);
        input = findViewById(R.id.input);

        /*
         * senderId:自己的id，用于区分自己和对方，控制消息气泡的位置。
         * imageLoader:图像加载器
         *
         * */
        messagesListAdapter = new MessagesListAdapter<>(LogIn.UserId, (imageView, url, payload) -> Picasso.get().load(url).into(imageView));
        //滑倒顶部时加载历史记录
        messagesListAdapter.setLoadMoreListener(this);

        //发送输入框中的文本，addToStart的第二个参数是列表滚动到底部
        input.setInputListener(input1 -> {
            String requestUrl = BuildConfig.URL + "/user-getinfo";
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
                            String user_name = jsonObject.getString("user_name");
                            String user_avatar = BuildConfig.URL + jsonObject.getString("user_avatar");
                            User user = new User(LogIn.UserId, user_name, user_avatar, true);
                            Date now = Calendar.getInstance().getTime();
                            Message newMessage =  new Message(LogIn.UserId, user, input1.toString(), now, 0);
                            runOnUiThread(() -> {
                                messagesListAdapter.addToStart(newMessage, true);
                            });
                            // 将新消息写入后端
                            String requestUrl = BuildConfig.URL + "/chat-send";
                            OkHttpClient client = new OkHttpClient();
                            FormBody.Builder builder = new FormBody.Builder()
                                    .add("user_id", LogIn.UserId)
                                    .add("user_chat_id", user_chat_id)
                                    .add("content", input1.toString());

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
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return true;
        });

        messagesList.setAdapter(messagesListAdapter);

        //初始化时调用一次加载历史记录
        onLoadMore(0, 1);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 循环调用的函数
                while (true) {
                    System.out.println("getgetget");
                    String requestUrl = BuildConfig.URL + "/chat-unread";
                    OkHttpClient client = new OkHttpClient();
                    FormBody.Builder builder = new FormBody.Builder()
                            .add("user_id", LogIn.UserId)
                            .add("user_chat_id", user_chat_id);

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
                                    int unread = jsonObject.getInteger("unread");
                                    if(unread == 1){
                                        refreshMessages(); // 调用您想要循环执行的函数
                                    }
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                    try {
                        Thread.sleep(2000); // 每5秒调用一次函数，可以根据需要进行调整
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void loadMessages(){
        // 调用chat-get接口获取聊天记录
        String requestUrl = BuildConfig.URL + "/chat-get";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", LogIn.UserId)
                .add("user_chat_id", user_chat_id);

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
                System.out.println("chat record:");
                System.out.println(res);
                try {
                    if(jsonObject.getString("status").equals("success")){
                        JSONArray chats = jsonObject.getJSONArray("chat");
                        for(int i = 0; i < chats.size(); ++i){
                            int user_status = chats.getJSONObject(i).getInteger("user");
                            String user_id;
                            if(user_status == 0){
                                user_id = LogIn.UserId;
                            }
                            else{
                                user_id = user_chat_id;
                            }
                            String user_name = chats.getJSONObject(i).getString("user_name");
                            String user_avatar = BuildConfig.URL + chats.getJSONObject(i).getString("user_avatar");
                            String content = chats.getJSONObject(i).getString("content");
                            String time = chats.getJSONObject(i).getString("time");
                            Date date = parseServerTime(time, "yyyy-MM-dd HH:mm:ss");

                            User user = new User(user_id, user_name, user_avatar, true);
                            Message newMessage = new Message(user_id, user, content, date, i);
                            messages.add(newMessage);

                        }
                        Collections.sort(messages);
                        runOnUiThread(() -> {
                            messagesListAdapter.addToEnd(messages, false);;
                        });
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void refreshMessages(){
        messages = new ArrayList<>();
        runOnUiThread(() -> {
            messagesListAdapter.clear();
        });
        // 调用chat-get接口获取聊天记录
        String requestUrl = BuildConfig.URL + "/chat-get";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", LogIn.UserId)
                .add("user_chat_id", user_chat_id);

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
                System.out.println("chat record:");
                System.out.println(res);
                try {
                    if(jsonObject.getString("status").equals("success")){
                        JSONArray chats = jsonObject.getJSONArray("chat");
                        for(int i = 0; i < chats.size(); ++i){
                            int user_status = chats.getJSONObject(i).getInteger("user");
                            String user_id;
                            if(user_status == 0){
                                user_id = LogIn.UserId;
                            }
                            else{
                                user_id = user_chat_id;
                            }
                            String user_name = chats.getJSONObject(i).getString("user_name");
                            String user_avatar = BuildConfig.URL + chats.getJSONObject(i).getString("user_avatar");
                            String content = chats.getJSONObject(i).getString("content");
                            String time = chats.getJSONObject(i).getString("time");
                            Date date = parseServerTime(time, "yyyy-MM-dd HH:mm:ss");
                            User user = new User(user_id, user_name, user_avatar, true);
                            Message newMessage = new Message(user_id, user, content, date, i);
                            messages.add(newMessage);
                        }

                        Collections.sort(messages);
                        runOnUiThread(() -> {
                            messagesListAdapter.addToEnd(messages, false);;
                        });
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    //滚动到顶部加载历史记录
    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        if (totalItemsCount <= 1) {
            loadMessages();
        }
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
