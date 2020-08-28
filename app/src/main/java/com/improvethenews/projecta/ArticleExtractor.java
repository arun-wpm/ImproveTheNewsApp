package com.improvethenews.projecta;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;

public class ArticleExtractor {
    URL url;
    boolean initial;
    ArrayList<Article> articleList;
    ArticleAdapter articleAdapter;

    private class PullAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.connect();
                if (c.getResponseCode() == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    String all = br.readLine();
                    Log.d(TAG, "doInBackground: " + all);
                    if (all == null) // Bug in the backend?
                        return "";
                    JSONArray array = new JSONArray(all);
                    for (int i = 0; i < array.length(); i++) {
                        //for each topic
                        JSONArray subarray = array.getJSONArray(i);
                        JSONArray topic = subarray.getJSONArray(0);
                        JSONArray articles = subarray.getJSONArray(1);
                        Log.d(TAG, "run: " + articles);
                        if (i == 0)
                            articleList.add(new Article(null, topic.getString(2), topic.getString(0), topic.getString(5), topic.getDouble(4), "", "", null, -3, null));
                        else
                            articleList.add(new Article(null, topic.getString(2), topic.getString(0), topic.getString(5), topic.getDouble(4), "", "", null, -1, null));
                        for (int j = 0; j < articles.length(); j++) {
                            //for each article
                            Log.d(TAG, "run: " + articles.getJSONArray(j));
                            articleList.add(parseJSON(articles.getJSONArray(j), (i == 0) ? 0 : (j < 2) ? i % 2 + 1 : 3));
                        }
                        if (i == 0)
                            articleList.add(new Article(null, topic.getString(2), topic.getString(0), topic.getString(5), topic.getDouble(4), "", "", null, -4, null));
                        else
                            articleList.add(new Article(null, topic.getString(2), topic.getString(0), topic.getString(5), topic.getDouble(4), "", "", null, -2, null));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return articleList.get(0).getTitle();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            articleAdapter.notifyDataSetChanged();
        }
    }

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

    ArticleExtractor(URL url, boolean initial, ArrayList articleList, ArticleAdapter articleAdapter) {
        this.url = url;
        this.initial = initial;
        this.articleList = articleList;
        this.articleList.clear();
        this.articleAdapter = articleAdapter;
    }

    public String pull() {
        //type:
        //-4: article_card_more but iself
        //-3: article_card_topic but itself
        //-2: article_card_more
        //-1: article_card_topic
        //0: article_card
        //1: article_card_small_left
        //2: article_card_small_right
        //3: article_card_half
        //4: article_card_footer
        AsyncTask<Void, Void, String> task = new PullAsyncTask();
        if (initial) {
            try {
                String title = task.execute().get();
                return title;
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else
            task.execute();
        return null;
    }

//    public ArrayList<Article> getArticleList() {
//        for (int i = 0; i < articleList.size(); i++) {
//            Log.d(TAG, "getArticleList: " + articleList.get(i).getUrl());
//        }
//        return articleList;
//    }
}
