package com.example.gui.adapter;

public class calendar_Data {
    private String title;
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public calendar_Data(String title, String content){
        this.title = title;
        this.content = content;
    }
}
