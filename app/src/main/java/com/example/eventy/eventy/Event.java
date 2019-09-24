package com.example.eventy.eventy;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event {
    private String uidEvent;
    private String author;
    private String authorUid;
    private String TitleEvent;
    private String InfoEvent;
    private String EventUrl;
    private boolean recommended;
    private long createdate;
    private boolean ispicupload;
    private String imageURL;


    public Event () {

    }

    public Event(String uidEvent, String author, String authorUid, String titleEvent, String infoEvent, String eventUrl){
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
        this.uidEvent = uidEvent;
        this.author = author;
        this.authorUid = authorUid;
        this.TitleEvent = titleEvent;
        this.InfoEvent = infoEvent;
        this.EventUrl = eventUrl;
        this.createdate = new Date().getTime();
        this.recommended = false;
        this.ispicupload = false;

    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uidEvent", uidEvent);
        result.put("author", author);
        result.put("authorUid", authorUid);
        result.put("EventUrl", EventUrl);
        result.put("TitleEvent", TitleEvent);
        result.put("InfoEvent", InfoEvent);
        result.put("recommended", recommended);
        result.put("createdate", createdate);

        return result;
    }



    public String getUidEvent() {
        return this.uidEvent;
    }

    public void setuidEvent(String uidEvent) {
        this.uidEvent = uidEvent;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitleEvent() {
        return this.TitleEvent;
    }

    public void setTitleEvent(String titleEvent) {
        this.TitleEvent = titleEvent;
    }

    public String getInfoEvent() {
        return this.InfoEvent;
    }

    public void setInfoEvent(String infoEvent) {
        this.InfoEvent = infoEvent;
    }

    public boolean isRecommended() {
        return this.recommended;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }

    public long getCreatedate() {
        return this.createdate;
    }

    public void setCreatedate(long createdate) {
        this.createdate = createdate;
    }

    public String getEventUrl() {
        return this.EventUrl;
    }

    public void setEventUrl(String eventUrl) {
        EventUrl = eventUrl;
    }

    public String getAuthorUid() {
        return this.authorUid;
    }

    public void setAuthorUid(String authorUid) {
        this.authorUid = authorUid;
    }

    public boolean isIspicupload() {
        return this.ispicupload;
    }

    public void setIspicupload(boolean ispicupload) {
        this.ispicupload = ispicupload;
    }

    public String getimageURL() {
        return imageURL;
    }

    public void setimageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
