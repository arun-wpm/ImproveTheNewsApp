package com.improvethenews.projecta;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

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

import static android.content.ContentValues.TAG;

public class AnnotatedWebViewActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_annotated_web_view);

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

        final int[] contentHeight = new int[1];
        WebView myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl(url);
//        class JsObject {
//            @JavascriptInterface
//            public void toString(String jsResult) {
//                Log.d("TAG", "toString: " + jsResult);
//                contentHeight[0] = (int) (Integer.parseInt(jsResult)*getResources().getDisplayMetrics().density);
//            }
//        }
//        myWebView.addJavascriptInterface(new JsObject(), "HTMLOUT");
//        myWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                view.loadUrl("javascript:( function () { var h = document.body.scrollHeight; window.HTMLOUT.toString(h); } ) ()");
//            }
//        });

        //TODO: change the name of this lol
        LinearLayout constraintLayout = (LinearLayout) findViewById(R.id.constraintLayout);
//        ConstraintSet set = new ConstraintSet();
//        Log.d("TAG", "onCreate: " + contentHeight[0]);
//        constraintLayout.setLayoutParams(new ConstraintLayout.LayoutParams(0, contentHeight[0]);

        try {
            JSONArray markup = new JSONArray(getIntent().getStringExtra("markup"));
            for (int i = 0; i < markup.length(); i++) {
                final JSONArray item = markup.getJSONArray(i);

                MarkupCardView markupCardView = new MarkupCardView(this, item.getString(1), item.getString(0));
                markupCardView.setId(View.generateViewId());
                markupCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), AnnotatedWebViewActivity.class);
                        String url = null;
                        try {
                            url = item.getString(2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        intent.putExtra("URL", url);
                        intent.putExtra("from", from);
                        intent.putExtra("markup", "");
                        v.getContext().startActivity(intent);
                    }
                });
                Log.d(TAG, "onCreate: " + markupCardView.text);
                constraintLayout.addView(markupCardView, i, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        } catch (JSONException e) {
            e.printStackTrace();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
//        set.clone(constraintLayout);
//        set.connect(imageView.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP);
//        set.applyTo(constraintLayout);
    }
}
