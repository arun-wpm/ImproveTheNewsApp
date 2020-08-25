package com.improvethenews.projecta;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ArticleExtractor {
    URL url;
    ArrayList<Article> articleList;

//    private Article parseLine(String line) {
//        String[] info = line.split("\t");
//        //Format:
//        //UNK, UNK, relative time, headline, url, imgurl, article size?, tags...
//        try {
//            Article a = new Article(new URL(info[5]), info[3], "New York Times", info[2], new URL(info[4]), Arrays.copyOfRange(info, 7, info.length));
//            return a;
//        } catch (MalformedURLException e) {
//            Article a = new Article(null, "", "", "", "", null, 0);
//            e.printStackTrace();
//            return a;
//        }
//    }

    private Article parseJSON(JSONArray array, int type) {
        //[<medianame>,<howlongago>,<title>,<url>,<imgurl>,<markup>]
        try {
            Article a;
            if (array.length() > 5) //has markup
                a = new Article(new URL(array.getString(4)), array.getString(2), null, null, 0, array.getString(0), array.getString(1), new URL(array.getString(3)), type, array.getJSONArray(5));
            else
                a = new Article(new URL(array.getString(4)), array.getString(2), null, null, 0, array.getString(0), array.getString(1), new URL(array.getString(3)), type, null);
            return a;
        } catch (MalformedURLException e) {
            Article a = new Article(null, "", "", "",0,"", "", null, 0, null);
            e.printStackTrace();
            return a;
        } catch (JSONException e) {
            Article a = new Article(null, "", "", "", 0,"","", null, 0, null);
            e.printStackTrace();
            return a;
        }
    }

    ArticleExtractor(URL url) {
        this.url = url;
        this.articleList = new ArrayList<Article>();
    }

//    public void pull(final int n) {
//        //old: used for .tsv files format
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
//                    String line;
//                    while ((line = br.readLine()) != null) {
//                        articleList.add(0, parseLine(line));
//                        if (articleList.size() > n) {
//                            articleList.remove(n);
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                Log.d(TAG, "pull done");
//            }
//        });
//        t.start();
//        try {
//            t.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    public void pull() {
        //new
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //performance improvement?
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
//                    connection.connect();
//                    InputStream stream = connection.getInputStream();
//                    String all = "";
//                    if (stream != null) {
//                        Reader reader = new InputStreamReader(stream, "UTF-8");
//                        StringBuffer buffer = new StringBuffer();
//                        char[] rawBuffer = new char[8192];
//                        int readSize;
//                        while ((readSize = reader.read(rawBuffer)) != -1) {
//                            if (readSize > 8192) {
//                                readSize = 8192;
//                            }
//                            buffer.append(rawBuffer, 0, readSize);
//                        }
//                        all = buffer.toString();
//                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                    String all = br.readLine();
                    JSONArray array = new JSONArray(all);
                    for (int i = 0; i < array.length(); i++) {
                        //for each topic
                        JSONArray subarray = array.getJSONArray(i);
                        JSONArray topic = subarray.getJSONArray(0);
                        JSONArray articles = subarray.getJSONArray(1);
                        Log.d(TAG, "run: " + articles);
                        Article a = new Article(null, topic.getString(2), topic.getString(0), topic.getString(5), topic.getDouble(4),"", "", null, -1, null);
                        articleList.add(a);
                        for (int j = 0; j < articles.length(); j++) {
                            //for each article
                            Log.d(TAG, "run: " + articles.getJSONArray(j));
                            articleList.add(parseJSON(articles.getJSONArray(j), (i == 0)?0:1));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "pull done");
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Article> getArticleList() {
//        for (int i = 0; i < articleList.size(); i++) {
//            articleList.get(i).getImg();
//        }
        for (int i = 0; i < articleList.size(); i++) {
            Log.d(TAG, "getArticleList: " + articleList.get(i).getUrl());
        }
        return articleList;
    }
}
