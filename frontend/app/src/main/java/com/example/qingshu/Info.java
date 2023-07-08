package com.example.qingshu;

import java.util.ArrayList;

public class Info {
    public String article_id;
    public String user_id;
    public String user_name;
    public String user_avatar;
    public String title;
    public String text;
    public ArrayList<String> media;
    public String address;
    public String type;
    public String time;
    public String cover;
    public int likes, stars;

    public Info(String article_id,
                String user_id,
                String user_name,
                String user_avatar,
                String title,
                String text,
                String cover,
                String address,
                String type,
                String time,
                int likes,
                int stars
                ){
        this.article_id = article_id;
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_avatar = BuildConfig.URL + user_avatar;
        this.title = title;
        this.text = text;
        this.media = new ArrayList<>();
        this.address = address;
        this.type = type;
        this.cover = BuildConfig.URL + cover;
        this.time = time;
        this.likes = likes;
        this.stars = stars;
    }
}