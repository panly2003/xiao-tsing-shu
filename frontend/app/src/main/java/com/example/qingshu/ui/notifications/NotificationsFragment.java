package com.example.qingshu.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.qingshu.BuildConfig;
import com.example.qingshu.Chat;
import com.example.qingshu.Dialog;
import com.example.qingshu.LogIn;
import com.example.qingshu.Message;
import com.example.qingshu.R;
import com.example.qingshu.SystemNotification;
import com.example.qingshu.User;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NotificationsFragment extends Fragment {

//    private FragmentNotificationsBinding binding;
    private DialogsList dialogsList;
    private DialogsListAdapter<Dialog> dialogsListAdapter;
    private ArrayList<Dialog> chats = new ArrayList<>();

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications
                , container, false);

//        Picasso.get().setLoggingEnabled(true);

        dialogsList = view.findViewById(R.id.dialogsList);

        //初始化适配器并添加图像加载
        dialogsListAdapter = new DialogsListAdapter<>((imageView, url, payload) -> Picasso.get().load(url).into(imageView));
        //给予列表初始数据

        String requestUrl = BuildConfig.URL + "/chat-list";
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
                        JSONArray chatlist = jsonObject.getJSONArray("chatlist");
                        for(int i = 0; i < chatlist.size(); ++i){
                            String user_id = chatlist.getJSONObject(i).getString("user_id");
                            String user_name = chatlist.getJSONObject(i).getString("user_name");
                            String user_avatar = BuildConfig.URL + chatlist.getJSONObject(i).getString("user_avatar");
                            String last_time = chatlist.getJSONObject(i).getString("last_time");
                            String last_content = chatlist.getJSONObject(i).getString("last_content");
                            int unread_num = chatlist.getJSONObject(i).getInteger("unread_num");

                            // form user
                            ArrayList<User> user = new ArrayList<>();
                            User mUser = new User(user_id, user_name, user_avatar, false);
                            user.add(mUser);

                            // form last message
                            Date date = parseServerTime(last_time, "yyyy-MM-dd HH:mm:ss");
                            Message lastMessage = new Message(LogIn.UserId, mUser, last_content, date, 1);

                            chats.add(new Dialog(user_id, user_name, user_avatar, user, lastMessage, unread_num));
                            System.out.println(dialogsListAdapter);
                            if (getActivity() != null && isAdded()) {
                                getActivity().runOnUiThread(() -> {
                                    //item项的点击事件，跳转到聊天界面
                                    dialogsListAdapter.setOnDialogClickListener(dialog -> {
                                        Bundle bundle = new Bundle();//存各种数据
                                        bundle.putString("user_chat_id", user_id);
                                        System.out.println(user_id);
                                        Intent intent = new Intent(getActivity(), Chat.class);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    });
                                });
                            }
                        }
                        if (getActivity() != null && isAdded()) {
                            getActivity().runOnUiThread(() -> {
                                dialogsListAdapter.addItems(chats);
                                dialogsListAdapter.setOnDialogClickListener(dialog -> {
                                    Bundle bundle = new Bundle();//存各种数据
                                    bundle.putString("user_chat_id", dialog.getId());
                                    Intent intent = new Intent(getActivity(), Chat.class);
                                    intent.putExtras(bundle);
                                    startActivityForResult(intent, 0);
                                });
                                dialogsList.setAdapter(dialogsListAdapter);
                            });
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestcode, int resultcode, Intent data)
    {
        chats = new ArrayList<>();
        String requestUrl = BuildConfig.URL + "/chat-list";
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
                        JSONArray chatlist = jsonObject.getJSONArray("chatlist");
                        for(int i = 0; i < chatlist.size(); ++i){
                            String user_id = chatlist.getJSONObject(i).getString("user_id");
                            String user_name = chatlist.getJSONObject(i).getString("user_name");
                            String user_avatar = BuildConfig.URL + chatlist.getJSONObject(i).getString("user_avatar");
                            String last_time = chatlist.getJSONObject(i).getString("last_time");
                            String last_content = chatlist.getJSONObject(i).getString("last_content");
                            int unread_num = chatlist.getJSONObject(i).getInteger("unread_num");

                            // form user
                            ArrayList<User> user = new ArrayList<>();
                            User mUser = new User(user_id, user_name, user_avatar, false);
                            user.add(mUser);

                            // form last message
                            Date date = parseServerTime(last_time, "yyyy-MM-dd HH:mm:ss");
                            Message lastMessage = new Message(LogIn.UserId, mUser, last_content, date, 1);

                            chats.add(new Dialog(user_id, user_name, user_avatar, user, lastMessage, unread_num));
                        }
                        System.out.println("chats");
                        System.out.println(chats);
                        if (getActivity() != null && isAdded()) {
                            getActivity().runOnUiThread(() -> {
//                            dialogsListAdapter.addItems(chats);
                                dialogsListAdapter.setOnDialogClickListener(dialog -> {
                                    Bundle bundle = new Bundle();//存各种数据
                                    bundle.putString("user_chat_id", dialog.getId());
                                    Intent intent = new Intent(getActivity(), Chat.class);
                                    intent.putExtras(bundle);
                                    startActivityForResult(intent, 0);
                                });
                                dialogsListAdapter.setItems(chats);
//                            dialogsListAdapter.notifyDataSetChanged();
//                            System.out.println("chats:");
//                            System.out.println(chats);
//                            System.out.println(dialogsListAdapter);
//                            dialogsList.setAdapter(dialogsListAdapter);
                            });
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


//    @Override
//    public void onResume() {
//        super.onResume();
//
//        //初始化适配器并添加图像加载
////        dialogsListAdapter = new DialogsListAdapter<>((imageView, url, payload) -> Picasso.get().load(url).into(imageView));
//        //给予列表初始数据
////        dialogsListAdapter.clear();
//        System.out.println(chats);
//        chats = new ArrayList<>();
//        System.out.println(chats);
//        String requestUrl = BuildConfig.URL + "/chat-list";
//        OkHttpClient client = new OkHttpClient();
//        FormBody.Builder builder = new FormBody.Builder()
//                .add("user_id", LogIn.UserId);
//
//        Request request = new Request.Builder()
//                .url(requestUrl)
//                .post(builder.build())
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {}
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
//                String res = Objects.requireNonNull(response.body()).string();
//                JSONObject jsonObject = JSON.parseObject(res);
//                System.out.println(res);
//                try {
//                    if(jsonObject.getString("status").equals("success")){
//                        JSONArray chatlist = jsonObject.getJSONArray("chatlist");
//                        for(int i = 0; i < chatlist.size(); ++i){
//                            String user_id = chatlist.getJSONObject(i).getString("user_id");
//                            String user_name = chatlist.getJSONObject(i).getString("user_name");
//                            String user_avatar = BuildConfig.URL + chatlist.getJSONObject(i).getString("user_avatar");
//                            String last_time = chatlist.getJSONObject(i).getString("last_time");
//                            String last_content = chatlist.getJSONObject(i).getString("last_content");
//                            int unread_num = chatlist.getJSONObject(i).getInteger("unread_num");
//
//                            // form user
//                            ArrayList<User> user = new ArrayList<>();
//                            User mUser = new User(user_id, user_name, user_avatar, false);
//                            user.add(mUser);
//
//                            // form last message
//                            Date date = parseServerTime(last_time, "yyyy-MM-dd HH:mm:ss");
//                            Message lastMessage = new Message(LogIn.UserId, mUser, last_content, date, 1);
//
//                            chats.add(new Dialog(user_id, user_name, user_avatar, user, lastMessage, unread_num));
//                        }
//                        System.out.println("chats");
//                        System.out.println(chats);
//                        getActivity().runOnUiThread(() -> {
////                            dialogsListAdapter.addItems(chats);
//                            dialogsListAdapter.setOnDialogClickListener(dialog -> {
//                                Bundle bundle = new Bundle();//存各种数据
//                                bundle.putString("user_chat_id", dialog.getId());
//                                Intent intent = new Intent(getActivity(), Chat.class);
//                                intent.putExtras(bundle);
//                                startActivity(intent);
//                            });
//                            dialogsListAdapter.setItems(chats);
////                            dialogsListAdapter.notifyDataSetChanged();
////                            System.out.println("chats:");
////                            System.out.println(chats);
////                            System.out.println(dialogsListAdapter);
////                            dialogsList.setAdapter(dialogsListAdapter);
//                        });
//                    }
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//    }
}