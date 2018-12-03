package com.best.grocery.entity;

import android.util.Log;

import com.best.grocery.AppConfig;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FileObject {
    String path;
    String name;
    String created;

    public FileObject() {
    }

    public FileObject(String path, String name) {
        this.path = path;
        this.name = name;
        String time = this.name.replace(".db", "").trim();
        this.created = convertMilliSecondsToFormattedDate(time);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {

    }

    public String convertMilliSecondsToFormattedDate(String milliSeconds) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConfig.DATE_SERVER_PATTERN_FULL);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(milliSeconds));
            return simpleDateFormat.format(calendar.getTime());
        } catch (Exception e) {
            Log.e("Error", "Read file backup");
            return milliSeconds;
        }

    }
}
