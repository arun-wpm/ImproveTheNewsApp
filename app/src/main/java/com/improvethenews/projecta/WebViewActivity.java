package com.improvethenews.projecta;

import android.content.Context;
import android.os.AsyncTask;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

public class WebViewActivity extends AppCompatActivity {

    public class SendPost extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(getResources().getString(R.string.feedback_url));
                String data = params[0];

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
//                urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
//                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                urlConnection.connect();

                Log.d("TAG", "doInBackground: " + urlConnection.getResponseCode() + urlConnection.getResponseMessage());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        final Date loadTime = Calendar.getInstance().getTime();
        final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        final Button save = (Button) findViewById(R.id.button);
        final String url = getIntent().getStringExtra("URL");
        final String from = getIntent().getStringExtra("from");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rating = (int) ratingBar.getRating();
                Date submitTime = Calendar.getInstance().getTime();
                long readTime = submitTime.getTime() - loadTime.getTime();
                String data = "";
                data += "id=" + getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE).getLong("userID", 0);
                data += "&url=" + url;
                data += "&rating=" + rating;
                data += "&seconds=" + ((float) readTime)/1000.0;
                data += "&page=" + from;
                Log.d("TAG", "onClick: " + data);
                SendPost sendPost = new SendPost();
                sendPost.execute(data);
                save.setVisibility(View.INVISIBLE);
                ratingBar.setIsIndicator(true);

                Toast toast = Toast.makeText(getApplicationContext(), "Submitted rating!", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        WebView myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl(url);
    }
}
