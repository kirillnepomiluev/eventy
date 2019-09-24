package com.example.eventy.eventy;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;

public class FeedItem {
    private String title;
    private String thumbnail;
    private int number;



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
