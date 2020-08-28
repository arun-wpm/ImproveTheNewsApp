package com.improvethenews.projecta;

import android.content.Context;
import android.content.SharedPreferences;
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
    Context context;

    private class PullAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String newvals = "";
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
                    JSONArray newsliders = array.getJSONArray(0);
                    newvals = newsliders.getString(0);
                    for (int i = 1; i < array.length(); i++) {
                        //for each topic
                        //["news", "headline", "Headlines", 14, "aa", 1.0, 1.005049494949495, 1.0, []]
                        //["world", "world", "World", 14, "Aa", 2970, 0.61701, 0.61701, 0.30303030303030304, [["news", "Headlines"]]]
                        JSONArray subarray = array.getJSONArray(i);
                        JSONArray topic = subarray.getJSONArray(0);
                        JSONArray articles = subarray.getJSONArray(1);
                        Log.d(TAG, "run: " + articles);
                        if (i == 1)
                            articleList.add(new Article(null, topic.getString(2), topic.getString(0), topic.getString(4), topic.getDouble(7), topic.getDouble(5),"", "", null, -3, null));
                        else
                            articleList.add(new Article(null, topic.getString(2), topic.getString(0), topic.getString(4), topic.getDouble(8), topic.getDouble(6), "", "", null, -1, null));
                        for (int j = 0; j < articles.length(); j++) {
                            //for each article
                            Log.d(TAG, "run: " + articles.getJSONArray(j));
                            articleList.add(parseJSON(articles.getJSONArray(j), (i == 1) ? 0 : (j < 2) ? i % 2 + 1 : 3));
                        }
                        if (i == 1)
                            articleList.add(new Article(null, topic.getString(2), topic.getString(0), topic.getString(4), topic.getDouble(7), topic.getDouble(5), "", "", null, -4, null));
                        else
                            articleList.add(new Article(null, topic.getString(2), topic.getString(0), topic.getString(4), topic.getDouble(8), topic.getDouble(6), "", "", null, -2, null));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return articleList.get(0).getTitle() + "$" + newvals;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: " + s);
            try {
                String topic = s.split("\\$")[0];
                String newvals = s.split("\\$")[1];
                Log.d(TAG, "onPostExecute: " + newvals);
                SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                for (int i = 0; i < newvals.length(); i += 4) {
                    String code = newvals.substring(i, i+2);
                    int val = Integer.valueOf(newvals.substring(i+2, i+4));
                    editor.putInt(code, val);
                }
                editor.commit();
                ((MainActivity) context).updateSliderList(topic);
            }
            catch(ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            articleAdapter.notifyDataSetChanged();
        }
    }

    private Article parseJSON(JSONArray array, int type) {
        //[<medianame>,<howlongago>,<title>,<url>,<imgurl>,<N/E/P>,<ampurl>,<markup>]
        try {
            Article a;
            String url = (array.getString(6).equals(""))?array.getString(3):array.getString(6);
            a = new Article(new URL(array.getString(4)), array.getString(2), null, null, 0, 0, array.getString(0), array.getString(1), new URL(url), type, array.getJSONArray(7));
            return a;
        } catch (MalformedURLException e) {
            Article a = new Article(null, "", "", "",0,0,"", "", null, 0, null);
            e.printStackTrace();
            return a;
        } catch (JSONException e) {
            Article a = new Article(null, "", "", "", 0,0,"","", null, 0, null);
            e.printStackTrace();
            return a;
        }
    }

    ArticleExtractor(Context context, URL url, boolean initial, ArrayList articleList, ArticleAdapter articleAdapter) {
        this.context = context;
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
