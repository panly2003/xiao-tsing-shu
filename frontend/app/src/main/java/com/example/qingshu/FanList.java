package com.example.qingshu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FanList extends AppCompatActivity {

    private List<Person> fanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 加载初始化的关注列表
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan_list);

        Toolbar myToolbar = findViewById(R.id.titleBar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);
        //RecyclerView获取他的对象
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //LayoutManager用于指定RecyclerView的布局方式
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //给layoutManager 的展示方式设置为竖直方向
        layoutManager .setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // 读取粉丝列表
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
                        for(int i = 0; i < follower.size(); ++i){
                            String user_id = follower.getJSONObject(i).getString("user_id");
                            String user_name = follower.getJSONObject(i).getString("user_name");
                            String user_avatar = follower.getJSONObject(i).getString("user_avatar");
                            runOnUiThread(() -> {
                                fanList.add(new Person(user_id, user_name, user_avatar));
                            });
                        }
                        runOnUiThread(() -> {
                            PersonAdapter adapter = new PersonAdapter(fanList);
                            recyclerView.setAdapter(adapter);
                        });
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
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