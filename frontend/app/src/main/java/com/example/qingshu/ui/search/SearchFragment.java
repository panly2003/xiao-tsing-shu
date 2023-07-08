package com.example.qingshu.ui.search;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.google.android.material.tabs.TabLayout;

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

public class SearchFragment extends Fragment {

    // Using ArrayList to store images data
    RecyclerView recyclerView;
    InfoAdapter infoAdapter;
    EditText mText;
    TextView mSearch;
    private ArrayList<Info> articles = new ArrayList<>();
    private final String[] types = new String[]{"最新", "最热", "最多赞", "最多收藏", "最多评论"};
    private final String[] order = new String[]{"time", "hot", "likes", "stars", "comments"};
    private String orderSelected = "time";
    private String searchText = "";
    public void onCardClick(View view) {
        Intent intent = new Intent(getActivity(), Detail.class);
        TextView id = view.findViewById(R.id.article_id);
        intent.putExtra("ID", id.getText());
        startActivity(intent);
    }
    public void searchArticles() {
        String userid = LogIn.UserId;
        String requestUrl = BuildConfig.URL + "/article-search";

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder()
                .add("user_id", userid)
                .add("words", searchText)
                .add("order", orderSelected);

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
                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> infoAdapter.notifyDataSetChanged());
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        Toolbar myToolbar =  view.findViewById(R.id.titleBar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        mText = view.findViewById(R.id.text);
        mSearch = view.findViewById(R.id.search);
        mSearch.setOnClickListener(v -> {
            searchText = mText.getText().toString();
            searchArticles();
        });
        recyclerView = view.findViewById(R.id.list);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        infoAdapter = new InfoAdapter(view.getContext(), articles, this::onCardClick);
        recyclerView.setAdapter(infoAdapter);

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
                orderSelected = order[tab.getPosition()];
                if (!searchText.equals(""))
                    searchArticles();
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

        mText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                searchText = mText.getText().toString();
                searchArticles();
                return true;
            }
            return false;
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
