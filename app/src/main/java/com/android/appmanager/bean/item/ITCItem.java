package com.android.appmanager.bean.item;

import android.graphics.drawable.Drawable;

public class ITCItem {
    private Drawable icon;
    private String title;
    private String content;

    public ITCItem(Drawable icon, String title, String content) {
        this.icon = icon;
        this.title = title;
        this.content = content;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}