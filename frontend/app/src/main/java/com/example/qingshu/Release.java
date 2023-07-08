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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.dialogs.BottomMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
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

public class Release extends AppCompatActivity {
    private static final String LOG_TAG
            = Release.class.getSimpleName();
    private TextView addButton;
    private LinearLayout mediaList;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private final static int MEDIA_MAX_NUMBER = 9;
    private ArrayList<Pair<String, InputStream>> mediaFiles = new ArrayList<>();
    private int currentFileType = 0, currentOption = 0;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private AlertDialog.Builder builder;
    private AlertDialog.Builder subBuilder;
    private TextView mTitle, mType;
    private EditText mContent;
    private ActivityResultLauncher<String[]> locationPermissionRequest;
    private TextView mLocation;
    private LocationManager locationManager;
    private String provider;
    private Pair<String, InputStream> articleCover;
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // 当位置改变时，这里的代码会被执行
            System.out.println(location);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.d("Location", latitude + "," + longitude);

                getAddress(latitude, longitude);
            } else {
                Log.d("Location", "No location :(");
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

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
    private void addImageView(Uri uri) {
        ImageView image = new ImageView(this);
        final float scale = getResources().getDisplayMetrics().density;
        int pixels_120dp = (int) (120 * scale + 0.5f);
        int rightMarginPixels = (int) (8 * scale + 0.5f);

        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(pixels_120dp, pixels_120dp);
        marginParams.setMargins(0, 0, rightMarginPixels, 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(marginParams);
        image.setLayoutParams(layoutParams);
        image.setImageURI(uri);
        int pos = mediaList.getChildCount() - 1;
        Pair<String, InputStream> mediaFile = new Pair<>(getContentResolver().getType(uri), getFileFromUri(uri));
        if (pos == 0)
            articleCover = new Pair<>(getContentResolver().getType(uri), getFileFromUri(uri));
        mediaList.addView(image, pos);
        mediaFiles.add(mediaFile);
    }
    private void addVideoView(Uri uri) {
        ImageView image = new ImageView(this);
        final float scale = getResources().getDisplayMetrics().density;
        int pixels_120dp = (int) (120 * scale + 0.5f);
        int rightMarginPixels = (int) (8 * scale + 0.5f);

        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(pixels_120dp, pixels_120dp);
        marginParams.setMargins(0, 0, rightMarginPixels, 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(marginParams);
        image.setLayoutParams(layoutParams);
        Bitmap cover = null;
        try {
            cover = displayVideoFrame(uri, image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int pos = mediaList.getChildCount() - 1;
        Pair<String, InputStream> mediaFile = new Pair<>(getContentResolver().getType(uri), getFileFromUri(uri));
        if (pos == 0) {
            if (cover == null) try {
                throw new Exception("cover is null");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            articleCover = new Pair<>("image/png", bitmapToInputStream(cover));
        }
        mediaList.addView(image, pos);
        mediaFiles.add(mediaFile);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);
        Toolbar myToolbar = findViewById(R.id.titleBar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);
        actionBar.setDisplayShowTitleEnabled(false);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Button send = findViewById(R.id.post);
        send.setOnClickListener(v -> postArticle());
        DialogX.init(this);
        mType = findViewById(R.id.theme);
        mType.setOnClickListener(v -> BottomMenu.show(new String[]{"购物", "旅行", "美食", "摄影"})
                                    .setMessage("选择发布主题")
                                    .setOnMenuItemClickListener((dialog, text, index) -> {
                                        mType.setText("  " + text.toString());
                                        return false;
                                    }));
        //Criteria criteria = new Criteria();
        //provider = locationManager.getBestProvider(criteria, false);
        provider = LocationManager.GPS_PROVIDER;
        mLocation = findViewById(R.id.address);
        mLocation.setOnClickListener(v -> getLocation());
        mediaList = findViewById(R.id.MediaList);
        addButton = findViewById(R.id.add);
        addButton.setOnClickListener(v -> selectMedia());
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                        if (uri != null) {
                            Log.d("PhotoPicker", "Selected URI: " + uri);
                            if (currentFileType == 1)
                                addImageView(uri);
                            else
                                addVideoView(uri);
                        } else {
                            Log.d("PhotoPicker", "No media selected");
                        }
                    });
        final CharSequence[] options = {"拍摄", "从相册选择", "取消"};
        final CharSequence[] subOptions = {"视频", "图片", "取消"};
        builder = new AlertDialog.Builder(this);
        subBuilder = new AlertDialog.Builder(this);
        builder.setTitle("上传图片或视频");
        subBuilder.setTitle("文件类型");

        builder.setItems(options, (dialog, which) -> {
            currentOption = which;
            if (options[which].equals("拍摄")) {
                requestCameraPermission();
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED)
                    subBuilder.show();
            }
            else if (options[which].equals("从相册选择")) subBuilder.show();
            else if (options[which].equals("取消")) dialog.dismiss();
        });

        subBuilder.setItems(subOptions, (dialog, which) -> {
            currentFileType = which;
            if (subOptions[which].equals("视频")) {
                if (currentOption == 0) {
                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    try {
                        startActivityForResult(takeVideoIntent, 1);
                    } catch (ActivityNotFoundException e) {
                        // display error state to the user
                    }
                }else
                    pickMedia.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(
                                    ActivityResultContracts.PickVisualMedia.VideoOnly.INSTANCE)
                            .build());

            } else if (subOptions[which].equals("图片")) {
                if (currentOption == 0) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        startActivityForResult(takePictureIntent, 0);
                    } catch (ActivityNotFoundException e) {
                        // display error state to the user
                    }
                }else
                    pickMedia.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
            } else if (subOptions[which].equals("取消")) dialog.dismiss();
        });
        mTitle = findViewById(R.id.editText_title);
        mContent = findViewById(R.id.editText_content);


        locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                                System.out.println(provider);
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
                            } else {
                                // No location access granted.
                                Log.d("Location", "No location access granted.");
                            }
                        }
                );

    }
    public void getAddress(double lat, double lon) {
        OkHttpClient client = new OkHttpClient();

        String url = "http://dev.virtualearth.net/REST/v1/Locations/" + lat + "," + lon + "?key=AjfkLtKzaotM4TuWS5ysOJ9sRS4t7ouk7WIWC3AALb3l_eRakCCwPEr5ArU5vsoE&c=zh-Hans";
        System.out.println(url);
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "MyApp")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray sets = jsonObject.getJSONArray("resourceSets");
                    JSONArray res = sets.getJSONObject(0).getJSONArray("resources");
                    String address = res.getJSONObject(0).getString("name");
                    System.out.println(address);
                    runOnUiThread(() -> mLocation.setText("  " + address));
                    Log.d(LOG_TAG, address);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    private void getLocation() {
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

    }
    private Bitmap displayVideoFrame(Uri videoUri, ImageView imageView) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(this, videoUri);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeUs = Long.parseLong(time) * 500;
            Bitmap frameBitmap = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST);
            imageView.setImageBitmap(frameBitmap);
            return frameBitmap;
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return null;
    }
    public InputStream bitmapToInputStream(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        return new ByteArrayInputStream(bos.toByteArray());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ImageView image = new ImageView(this);
                final float scale = getResources().getDisplayMetrics().density;
                int pixels_120dp = (int) (120 * scale + 0.5f);
                int rightMarginPixels = (int) (8 * scale + 0.5f);

                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(pixels_120dp, pixels_120dp);
                marginParams.setMargins(0, 0, rightMarginPixels, 0);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(marginParams);
                image.setLayoutParams(layoutParams);
                image.setImageBitmap(imageBitmap);

                int pos = mediaList.getChildCount() - 1;
                Pair<String, InputStream> mediaFile = new Pair<>("image/png", bitmapToInputStream(imageBitmap));
                if (pos == 0)
                    articleCover = new Pair<>("image/png", bitmapToInputStream(imageBitmap));
                mediaList.addView(image, pos);
                mediaFiles.add(mediaFile);
            } else if (requestCode == 1) {
                Uri videoUri = data.getData();
                addVideoView(videoUri);
            }
        }
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
    private void selectMedia() {
        Log.d("SelectMedia", "start" );
        if (mediaFiles.size() == MEDIA_MAX_NUMBER) return ;
        builder.show();
    }

    private void postArticle() {
        OkHttpClient client = new OkHttpClient();

        String url = BuildConfig.URL + "/article-post";

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        builder.addFormDataPart("user_id", LogIn.UserId);
        builder.addFormDataPart("title", mTitle.getText().toString());
        builder.addFormDataPart("text", mContent.getText().toString());
        String location = mLocation.getText().toString().trim().equals("  添加地点") ? "未知" : mLocation.getText().toString().trim();
        builder.addFormDataPart("address", location);
        String type = mType.getText().toString().trim().equals("  添加类型") ? "无类型" : mType.getText().toString().trim();
        builder.addFormDataPart("type", type);
        builder.addFormDataPart("media_num", String.valueOf(mediaFiles.size()));
        for (int i = 0; i < mediaFiles.size(); i++) {
            InputStream inputStream = mediaFiles.get(i).second;
            String mimetype = mediaFiles.get(i).first;
            RequestBody requestBody = createRequestBodyFromInputStream(inputStream, mimetype);
            builder.addFormDataPart("media" + i, "file" + i + "_" + System.currentTimeMillis() + "." + mimetype.split("/")[1], requestBody);
        }
        if (mediaFiles.size() > 0) {
            InputStream inputStream = articleCover.second;
            String mimetype = articleCover.first;
            RequestBody requestBody = createRequestBodyFromInputStream(inputStream, mimetype);
            builder.addFormDataPart("cover", "cover_" + System.currentTimeMillis() + "." + mimetype.split("/")[1], requestBody);
        }

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("post article failed");
                System.out.println(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    System.out.println("post article ERROR");
                } else {
                    Log.d(LOG_TAG, "article posted successfully");
                }
            }
        });
        finish();
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
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }
}