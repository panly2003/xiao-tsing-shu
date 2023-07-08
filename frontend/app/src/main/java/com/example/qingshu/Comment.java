package com.example.qingshu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Comment {
    public String user_id;
    public String user_name;
    public String user_avatar;
    public String content;
    public String time;


    public String formatTime(String inputTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat outputFormat2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat3 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault());
        SimpleDateFormat outputFormat4 = new SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault());

        String[] weekdays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

        try {
            Date date = inputFormat.parse(inputTime);
            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                Calendar messageCalendar = Calendar.getInstance();
                messageCalendar.setTime(date);

                long difference = calendar.getTimeInMillis() - messageCalendar.getTimeInMillis();

                if (difference < 60 * 1000) {
                    return "刚刚";
                } else if (difference < 60 * 60 * 1000) {
                    return difference / (60 * 1000) + "分钟前";
                } else if (outputFormat2.format(date).equals(outputFormat2.format(calendar.getTime()))) {
                    return (messageCalendar.get(Calendar.HOUR_OF_DAY) < 12 ? "上午 " : "下午 ") + outputFormat1.format(date);
                } else {
                    calendar.add(Calendar.DATE, -1);
                    if (outputFormat2.format(date).equals(outputFormat2.format(calendar.getTime()))) {
                        return "昨天 " + (messageCalendar.get(Calendar.HOUR_OF_DAY) < 12 ? "上午 " : "下午 ") + outputFormat1.format(date);
                    } else {
                        calendar.add(Calendar.DATE, -6);
                        if (messageCalendar.after(calendar)) {
                            return weekdays[messageCalendar.get(Calendar.DAY_OF_WEEK) - 1] + " " + (messageCalendar.get(Calendar.HOUR_OF_DAY) < 12 ? "上午 " : "下午 ") + outputFormat1.format(date);
                        } else {
                            calendar.add(Calendar.DATE, 7);
                            calendar.add(Calendar.YEAR, -1);
                            if (messageCalendar.after(calendar)) {
                                return outputFormat4.format(date) + (messageCalendar.get(Calendar.HOUR_OF_DAY) < 12 ? "上午 " : "下午 ") + outputFormat1.format(date);
                            } else {
                                return outputFormat3.format(date) + (messageCalendar.get(Calendar.HOUR_OF_DAY) < 12 ? "上午 " : "下午 ") + outputFormat1.format(date);
                            }
                        }
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }


    public Comment(String user_id, String user_name, String user_avatar, String content, String time){
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_avatar = BuildConfig.URL + user_avatar;
        this.content = content;
        this.time = formatTime(time);
    }

}