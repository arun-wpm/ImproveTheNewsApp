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
    URL imgurl;
//    Bitmap img;
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

//    public class ArticleAsync extends AsyncTask<ImageView, Void, Bitmap> {
//        ImageView imageView;
//
//        @Override
//        protected Bitmap doInBackground(ImageView... imageViews) {
//            Bitmap img;
//            try {
//                img = BitmapFactory.decodeStream(imgurl.openConnection().getInputStream());
//            } catch (IOException e) {
//                img = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_image);
//                e.printStackTrace();
//            } catch (NullPointerException e) {
//                img = null;
//                e.printStackTrace();
//            }
//            imageView = imageViews[0];
//            return img;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap img) {
//            super.onPostExecute(img);
//            imageView.setImageBitmap(img);
//            setImg(img);
//        }
//    }

//    private void setImg(Bitmap img) {
//        this.img = img;
//        if (this.img != null)
//            Log.d(TAG, Integer.toString(this.img.getHeight()));
//    }

//    Article(final URL imgurl, String title, String source, Date time, URL url, String[] tags) {
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Bitmap img;
//                try {
//                    img = BitmapFactory.decodeStream(imgurl.openConnection().getInputStream());
//                } catch (IOException e) {
//                    img = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_image);
//                    e.printStackTrace();
//                } catch (NullPointerException e) {
//                    img = null;
//                    e.printStackTrace();
//                }
//                setImg(img);
//                Log.d(TAG, "setImg done");
//            }
//        });
//        t.start();
//        this.title = title;
//        this.source = source;
//        this.time = time;
//        this.url = url;
////        this.tags = tags;
//    }
//
//    Article(final URL imgurl, String title, String source, String reltime, URL url, String[] tags) {
//        this.imgurl = imgurl;
//        this.title = title;
//        this.source = source;
//        this.reltime = reltime;
//        this.url = url;
////        this.tags = tags;
//    }
//
//    Article(final URL imgurl, String title, String mnemonic, String source, String reltime, URL url, int type) {
//        this.imgurl = imgurl;
//        this.title = title;
//        this.mnemonic = mnemonic;
//        this.source = source;
//        this.reltime = reltime;
//        this.url = url;
////        this.tags = null;
//        this.type = type;
//    }

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
//    public ArticleAsync loadImg(ImageView imageView) {
//        if (imgurl != null) {
//            if (img == null) {
//                ArticleAsync async = new ArticleAsync();
//                async.execute(imageView);
//                return async;
//            }
//            else {
//                imageView.setImageBitmap(img);
//                return null;
//            }
//        }
//        else
//            return null;
//    }
    public void loadImg(ImageView imageView) {
        if (imgurl != null) {
            Picasso.get().load(imgurl.toString()).into(imageView);
        }
    }
//    public Bitmap getImg() {
//        if (img == null && imgurl != null) {
//            Thread t = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Bitmap img;
//                    try {
//                        img = BitmapFactory.decodeStream(imgurl.openConnection().getInputStream());
//                    } catch (IOException e) {
//                        img = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_image);
//                        e.printStackTrace();
//                    } catch (NullPointerException e) {
//                        img = null;
//                        e.printStackTrace();
//                    }
//                    setImg(img);
//                    Log.d(TAG, "setImg done");
//                }
//            });
//            t.start();
////            try {
////                t.join();
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
//        }
//        return img;
//    }
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
