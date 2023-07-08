package com.example.qingshu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Person extends AppCompatActivity {

    private String userNickname;
    private String userId;
    private String userImage;  //News对应的照片1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
    }

    public Person(String userId, String userNickname, String userImage){
        this.userId = userId;
        this.userNickname = userNickname;
        this.userImage = userImage;
    }

    public String getNickname() { return userNickname; }
    public String getUserImage() {
        return userImage;
    }
    public String getUserId() {return userId;}
}