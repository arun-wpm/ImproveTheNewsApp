package com.improvethenews.projecta;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;

import com.google.android.flexbox.FlexboxLayoutManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ArticleAdapter articleAdapter;
    private SliderAdapter sliderAdapter;
    private TopicAdapter topicAdapter;
    private ArrayList<Slider> sliderList;
    private ArrayList<Article> articleList;
    private ArrayList<Topic> topicList;
    private String topic, mnemonic, settings, path, depth;
    private RecyclerView rv;
    private SharedPreferences sp;
    private MenuItem searchItem;
    private SearchView searchView;
    private FlexboxLayoutManager lim;
    private DisplayMetrics displayMetrics;
    private BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_news:
                    searchItem.setVisible(true);
                    searchView.setVisibility(View.VISIBLE);
                    applySliderListChanges();
                    getArticleList(topic);
                    articleAdapter = new ArticleAdapter(articleList, topic + " " + settings, path, depth, displayMetrics.heightPixels, displayMetrics.widthPixels);
                    rv.setAdapter(articleAdapter);
                    return true;
                case R.id.navigation_sliders:
                    searchItem.setVisible(false);
                    searchView.setVisibility(View.GONE);
                    applySliderListChanges();
                    getTopicAndSliderList(topic, depth);
                    rv.setAdapter(sliderAdapter);
                    return true;
            }
            return false;
        }
    };

    private void showTopicsScreen() {
        searchItem.setVisible(true);
        searchView.setVisibility(View.VISIBLE);
        applySliderListChanges();
        topicAdapter = new TopicAdapter(topicList);
        rv.setAdapter(topicAdapter);
    }

    private String getRequestURL(String topic) {
        String base = getResources().getString(R.string.article_source) + topic;
        settings = "";
        base += "&sliders=";
        Map<String,?> allSliders = sp.getAll();
        //Throw all settings into the URL (is it slow?)
        for (Map.Entry<String,?> slider : allSliders.entrySet()) {
            if (slider.getKey().length() != 2) continue;
            settings += slider.getKey();
            String n = String.valueOf(slider.getValue());
            settings += ("00" + n).substring(n.length());
        }
        Log.d("TAG", "getRequestURL: " + base + settings);
        return base + settings;
    }

    private void getArticleList(String topic) {
        try {
//            ArticleExtractor extractor = new ArticleExtractor(new URL("https://raw.githubusercontent.com/rayaburong/rayaburong.github.io/master/data/summary_nytimes_crawled.tsv"));
            ArticleExtractor extractor = new ArticleExtractor(new URL(getRequestURL(topic)));
            extractor.pull();
            articleList = extractor.getArticleList();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    String[][] defaultSliders = {{"Left-Right", "LR", "50"}, {"Pro-establishment", "PE", "50"}, {"Nuance", "NU", "70"}, {"Depth", "DE", "70"}, {"Shelf-life", "SL", "70"}, {"Recent", "RE", "70"}};
    private void getTopicAndSliderList(String topic, String depthString) {
        //mnemonic, to display, official name, "lowercase" name, depth, popularity, code
        topicList = new ArrayList<Topic>();
        sliderList = new ArrayList<Slider>();
//        sliderList.add(new Slider("Bias Sliders", "", 0, 0, -1));
//        for (int i = 0; i < defaultSliders.length; i++) {
//            sliderList.add(new Slider(defaultSliders[i][0], defaultSliders[i][1], sp.getInt(defaultSliders[i][1], Integer.parseInt(defaultSliders[i][2])), Integer.parseInt(defaultSliders[i][2]), 0));
//        }
        sliderList.add(new Slider("Your " + mnemonic + " Feed", "", 0, 0, -1));
        sliderList.add(new Slider("", "", 0, 0, 2));
        boolean inRange = false;
        int depth = Integer.valueOf(depthString);
        double base = 1.f;
        InputStream is = this.getResources().openRawResource(R.raw.topics);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String readLine;
        try {
            while ((readLine = br.readLine()) != null) {
                String[] row = readLine.split("\t");
                if (!row[1].equals("0")) {
                    topicList.add(new Topic(row[2], row[0], Integer.parseInt(row[4])));
                    if (row[0].equals(topic)) {
                        inRange = true;
                        base = Double.parseDouble(row[5]);
                    }
                    else if (inRange && Integer.parseInt(row[4]) == depth + 1) {
                        int pop = Double.valueOf(Double.parseDouble(row[5])/base*99).intValue();
                        sliderList.add(new Slider(row[2], row[6], sp.getInt(row[6], pop), pop, 1));
                        Log.d("TAG", "getTopicList: " + row[2] + row[6] + pop);
                    }
                    else if (inRange && Integer.parseInt(row[4]) == depth)
                        inRange = false;
                }
            }
            is.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void applySliderListChanges() {
        SharedPreferences.Editor editor = sp.edit();
        for (int i = 0; i < sliderList.size(); i++) {
            sliderList.set(i, sliderAdapter.getItem(i));
            if (sliderList.get(i).getCode().length() == 2) {
                editor.putInt(sliderList.get(i).getCode(), sliderList.get(i).getValue());
                Log.d("TAG", "applySliderListChanges: " + sliderList.get(i).getCode() + sliderList.get(i).getValue());
            }
        }
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //user id system
        sp = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        if (!sp.contains("userID"))
        {
            Random random = new Random();
            SharedPreferences.Editor editor = sp.edit();
            editor.putLong("userID", random.nextLong());
            editor.commit();
            Log.d("TAG", "onCreate: " + sp.getLong("userID", 0));
        }

        //screen width and height
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        topic = getIntent().getStringExtra("Mnemonic");
        if (topic == null)
            topic = "news";
//            topic = "test";

        path = getIntent().getStringExtra("Path");
        if (path == null)
            path = "";

        depth = getIntent().getStringExtra("Depth");
        if (depth == null)
            depth = "0";

        rv = (RecyclerView) findViewById(R.id.recycler);
        rv.setHasFixedSize(true);
        lim = new FlexboxLayoutManager(this);
        rv.setLayoutManager(lim);

        getTopicAndSliderList(topic, depth);
        topicAdapter = new TopicAdapter(topicList);
        getArticleList(topic);
        mnemonic = articleList.get(0).getTitle();
        path = path + mnemonic;
        articleAdapter = new ArticleAdapter(articleList, topic + " " + settings, path, depth, displayMetrics.heightPixels, displayMetrics.widthPixels);
        sliderAdapter = new SliderAdapter(sliderList);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        //default: show articles
        rv.setAdapter(articleAdapter);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        searchItem = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                topicAdapter.getFilter().filter(s);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTopicsScreen();
            }
        });

        return true;
    }


}
