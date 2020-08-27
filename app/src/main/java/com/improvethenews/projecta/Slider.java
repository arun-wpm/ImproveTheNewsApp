package com.improvethenews.projecta;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class Slider {
    String title;
    String code;
    int value;
    int usualvalue;
    int type;
    String start, end;
    int color;

    Slider(String title, String code, int value, int usualvalue, int type) {
        this.title = title;
        this.code = code;
        this.value = value;
        this.usualvalue = usualvalue;
        this.type = type;
        this.color = Color.TRANSPARENT;
        this.start = "";
        this.end = "";
    }

    Slider(String title, String code, int value, int usualvalue, int type, String start, String end) {
        this.title = title;
        this.code = code;
        this.value = value;
        this.usualvalue = usualvalue;
        this.type = type;
        this.color = Color.TRANSPARENT;
        this.start = start;
        this.end = end;
    }

    Slider(String title, String code, int value, int usualvalue, int type, int color) {
        this.title = title;
        this.code = code;
        this.value = value;
        this.usualvalue = usualvalue;
        this.type = type;
        this.color = color;
        this.start = "";
        this.end = "";
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getCode() {
        return code;
    }
    public int getValue() {
        return value;
    }
    public int getUsualvalue() {
        return usualvalue;
    }
    public void setValue(int value) {
        this.value = value;
    }
    public int getType() {
        return type;
    }
    public int getColor() {
        return color;
    }
    public String getStart() {
        return start;
    }
    public String getEnd() {
        return end;
    }
}
