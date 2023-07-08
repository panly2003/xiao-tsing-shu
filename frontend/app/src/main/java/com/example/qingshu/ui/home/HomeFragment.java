package com.example.qingshu.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.qingshu.BuildConfig;
import com.example.qingshu.Info;
import com.example.qingshu.InfoAdapter;
import com.example.qingshu.Detail;
import com.example.qingshu.LogIn;
import com.example.qingshu.R;
import com.example.qingshu.SystemNotification;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.kongzue.dialogx.dialogs.TipDialog;
import com.kongzue.dialogx.dialogs.WaitDialog;

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

public class HomeFragment extends Fragment {

    // Using ArrayList to store images data
    RecyclerView recyclerView;
    View view;
    InfoAdapter infoAdapter;
    private ArrayList<Info> articles = new ArrayList<>();
    private final String[] types = new String[]{"关注", "收藏", "热门", "购物", "旅行", "美食", "摄影"};
    private final String[] orderTypes = new String[]{"最新", "最热", "最多赞", "最多收藏", "最多评论"};
    private final String[] order = new String[]{"time", "hot", "likes", "stars", "comments"};
    private int currentPos = 0;
    private String orderSelected = "time";

    private long lastRequestId = 0;
    public void onCardClick(View view) {
        Intent intent = new Intent(getActivity(), Detail.class);
        TextView id = view.findViewById(R.id.article_id);
        intent.putExtra("ID", id.getText());
        startActivity(intent);
    }
    public void getArticles(int pos) {
        long thisRequestId = ++lastRequestId;
        String userid = LogIn.UserId;
        System.out.println(userid);
        String type = null;
        String requestUrl = BuildConfig.URL + "/article-";
        if (pos == 0) requestUrl += "follow";
        else if (pos == 1) requestUrl += "star";
        else if (pos == 2) requestUrl += "hot";
        else {
            requestUrl += "type";
            type = types[pos];
        }
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", userid);
        if (type != null)
            builder.add("type", type);
        builder.add("order", orderSelected);

        Request request = new Request.Builder()
                .url(requestUrl)
                .post(builder.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                if (thisRequestId != lastRequestId) {
                    // 如果这个响应不是最新的请求的响应，那么我们就忽略它
                    return;
                }
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
                        if (getActivity() != null && isAdded()) {
                            infoAdapter.articles = articles;
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        for (String type : types) tabLayout.addTab(tabLayout.newTab().setText(type));
        for (int i = 0; i < types.length; i++) {
            View tabView = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            tabView.setBackgroundResource(R.drawable.no_ripple);
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            assert tab != null;
            String tabStr = Objects.requireNonNull(tab.getText()).toString();
            if (tab.getCustomView() == null || !(tab.getCustomView() instanceof TextView)) {
                TextView tv = new TextView(tabLayout.getContext());
                tv.setTextColor(tab.isSelected() ? Color.BLACK : Color.rgb(170, 170, 170));
                tv.setText(tabStr);
                tv.setTextSize(tab.isSelected() ? 16 : 15);
                tv.setGravity(Gravity.CENTER);
                tab.setCustomView(tv);
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                               @Override
                                               public void onTabSelected(TabLayout.Tab tab) {
                                                   TextView tv = (TextView) tab.getCustomView();
                                                   assert tv != null;
                                                   tv.setTextColor(Color.BLACK);
                                                   tv.setTextSize(16);
                                                   getArticles(tab.getPosition());
                                               }

                                               @Override
                                               public void onTabUnselected(TabLayout.Tab tab) {
                                                   TextView tv = (TextView) tab.getCustomView();
                                                   assert tv != null;
                                                   tv.setTextColor(Color.rgb(170, 170, 170));
                                                   tv.setTextSize(15);
                                               }

                                               @Override
                                               public void onTabReselected(TabLayout.Tab tab) {

                                               }
                                           });
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            currentPos = (currentPos + 1) % order.length;
            orderSelected = order[currentPos];
            getArticles(tabLayout.getSelectedTabPosition());
            TipDialog.show(orderTypes[currentPos], WaitDialog.TYPE.SUCCESS, 200);
        });

        // Getting reference of recyclerView
        recyclerView = view.findViewById(R.id.list);

        // Setting the layout as Staggered Grid for vertical orientation
        recyclerView.setLayoutManager( new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        infoAdapter = new InfoAdapter(view.getContext(), articles, this::onCardClick);
        recyclerView.setAdapter(infoAdapter);
        getArticles(tabLayout.getSelectedTabPosition());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
