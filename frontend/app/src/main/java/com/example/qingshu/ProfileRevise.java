package com.example.qingshu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class ProfileRevise extends AppCompatActivity {

    ImageButton password_change_btn;
    ImageButton nickname_change_btn;
    ImageButton intro_change_btn;
    AlertDialog.Builder builder;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private int currentOption = 0;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    ImageView avatar;
    TextView username;
    TextView id;
    TextView profile;

    public void setUsername(){
        String requestUrl = BuildConfig.URL + "/user-getname";
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
                    String name = jsonObject.getString("user_name");
                    runOnUiThread(() -> {
                        username.setText(name);
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

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
                        profile.setText(profile_text);
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void setAvatar(){
        String requestUrl = BuildConfig.URL + "/user-getavatar";
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
                    String avatar_url = jsonObject.getString("user_avatar");
                    runOnUiThread(() -> {
                        Glide.with(avatar.getContext())
                                .load(BuildConfig.URL + avatar_url)
                                .circleCrop()
                                .into(avatar);
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "拍照权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_revise);

        Toolbar myToolbar = findViewById(R.id.titleBar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);

        password_change_btn = findViewById(R.id.btn_user_password);
        nickname_change_btn = findViewById(R.id.btn_user_nickname);
        intro_change_btn = findViewById(R.id.btn_user_intro);

        password_change_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ProfileRevise.this, PasswordRevise.class);
                startActivity(intent);
            }
        });
        nickname_change_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ProfileRevise.this, NicknameRevise.class);
                startActivity(intent);
            }
        });
        intro_change_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ProfileRevise.this, IntroRevise.class);
                startActivity(intent);
            }
        });

        id = (TextView) findViewById(R.id.user_id_content);
        id.setText(LogIn.UserId);
        username = (TextView) findViewById(R.id.user_nickname_content);
        profile = (TextView) findViewById(R.id.user_intro_content);
        avatar = (ImageView) findViewById(R.id.user_avatar);
        setUsername();
        setProfile();
        setAvatar();
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
                InputStream inputStream = getFileFromUri(uri);
                OkHttpClient client = new OkHttpClient();

                String url = BuildConfig.URL + "/user-setavatar";

                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                builder.addFormDataPart("user_id", LogIn.UserId);
                {
                    String mimetype = "image/png";
                    RequestBody requestBody = createRequestBodyFromInputStream(inputStream, mimetype);
                    builder.addFormDataPart("user_new_avatar", "avatar" + "_" + System.currentTimeMillis() + "." + mimetype.split("/")[1], requestBody);
                }
                RequestBody requestBody = builder.build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) { System.out.println("ERROR");}

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                        String res = Objects.requireNonNull(response.body()).string();
                        System.out.println(res);
                        setAvatar();
                    }
                });
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });
        final CharSequence[] options = {"拍摄", "从相册选择", "取消"};
        builder = new AlertDialog.Builder(this);
        builder.setItems(options, (dialog, which) -> {
            currentOption = which;
            if (options[which].equals("拍摄")) {
                requestCameraPermission();
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    startActivityForResult(takePictureIntent, 0);
                } catch (ActivityNotFoundException e) {
                    // display error state to the user
                }
            }
            else if (options[which].equals("从相册选择")) {

                    pickMedia.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                }
            else if (options[which].equals("取消")) dialog.dismiss();
        });
        avatar.setOnClickListener(v -> builder.show());
    }

    public InputStream bitmapToInputStream(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        return new ByteArrayInputStream(bos.toByteArray());
    }

    public InputStream getFileFromUri(Uri uri){
        ContentResolver resolver = getApplicationContext()
                .getContentResolver();
        InputStream stream;
        try {
            stream = resolver.openInputStream(uri);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return stream;
    }

    public RequestBody createRequestBodyFromInputStream(final InputStream inputStream, final String contentType) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse(contentType);
            }

            @Override
            public long contentLength() {
                try {
                    return inputStream.available();
                } catch (IOException e) {
                    return 0;
                }
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source = null;
                try {
                    source = Okio.source(inputStream);
                    sink.writeAll(source);
                } finally {
                    Util.closeQuietly(source);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                InputStream inputStream = bitmapToInputStream(imageBitmap);
                OkHttpClient client = new OkHttpClient();

                String url = BuildConfig.URL + "/user-setavatar";

                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                builder.addFormDataPart("user_id", LogIn.UserId);
                {
                    String mimetype = "image/png";
                    RequestBody requestBody = createRequestBodyFromInputStream(inputStream, mimetype);
                    builder.addFormDataPart("user_new_avatar", "avatar" + "_" + System.currentTimeMillis() + "." + mimetype.split("/")[1], requestBody);
                }
                RequestBody requestBody = builder.build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println("ERROR");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                        String res = Objects.requireNonNull(response.body()).string();
                        System.out.println(res);
                        setAvatar();
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUsername();
        setProfile();
        setAvatar();
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