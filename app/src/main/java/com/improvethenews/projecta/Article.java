package com.improvethenews.projecta;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class Article {
    //type:
    //-1: article_card_topic
    //0: article_card
    //1: article_card_small_left
    //2: article_card_small_right
    //3: article_card_half
    //4: article_card_footer

    URL imgurl;
    String title;
    String mnemonic;
    String code;
    String source;
    Date time;
    String reltime;
    URL url;
//    String[] tags;
    int type;
    double popularity;
    JSONArray markup;

    Article(final URL imgurl, String title, String mnemonic, String code, double popularity, String source, String reltime, URL url, int type, JSONArray markup) {
        Log.d(TAG, "Article: " + imgurl + title + mnemonic + code + popularity + source + reltime + url + type);
        this.imgurl = imgurl;
        this.title = title;
        this.mnemonic = mnemonic;
        this.code = code;
        this.popularity = popularity;
        this.source = source;
        this.reltime = reltime;
        this.url = url;
        this.type = type;
        this.markup = markup;
    }

    public URL getImgurl () {
        return imgurl;
    }

    public void loadImg(ImageView imageView) {
        if (imgurl != null) {
            Picasso.get().load(imgurl.toString()).into(imageView);
        }
    }

    public String getTitle() {
        return title;
    }
    public String getMnemonic() {
        return mnemonic;
    }
    public String getCode() {
        return code;
    }
    public double getPopularity() {
        Log.d(TAG, "getPopularity: " + popularity);
        return popularity;
    }
    public String getSource() {
        return source;
    }
    public Date getTime() {
        return time;
    }
    public String getReltime() {
        return reltime;
    }
    public URL getUrl() {
        return url;
    }
//    public String getTags(int i) {
//        return tags[i];
//    }
//    public int getTagsLength() {
//        if (tags == null)
//            return 0;
//        else
//            return tags.length;
//    }
    public int getType() {
        return type;
    }
    public JSONArray getMarkup() {
        return markup;
    }
}
