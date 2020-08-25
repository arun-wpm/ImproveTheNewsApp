package com.improvethenews.projecta;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

public class MarkupCardView extends FrameLayout {
    CardView c;
    LinearLayout h, v;
    String text;
    URL imgurl;
    public MarkupCardView(@NonNull Context context, URL imgurl, String text) {
        super(context);
        this.imgurl = imgurl;
        this.text = text;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.article_card_markup, this);

        c = (CardView) getChildAt(0);
        h = (LinearLayout) c.getChildAt(0);
        GetMarkupImage async = new GetMarkupImage();
        async.execute((ImageView) h.getChildAt(0));
        v = (LinearLayout) h.getChildAt(1);
        ((TextView) v.getChildAt(0)).setText(text);
    }

    public class GetMarkupImage extends AsyncTask<ImageView, Void, Bitmap> {
        ImageView imageView;

        @Override
        protected Bitmap doInBackground(ImageView... imageViews) {
            Bitmap img;
            try {
                img = BitmapFactory.decodeStream(imgurl.openConnection().getInputStream());
            } catch (IOException e) {
                img = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_image);
                e.printStackTrace();
            } catch (NullPointerException e) {
                img = null;
                e.printStackTrace();
            }
            imageView = imageViews[0];
            return img;
        }

        @Override
        protected void onPostExecute(Bitmap img) {
            super.onPostExecute(img);
            imageView.setImageBitmap(img);
        }
    }

    @Override
    protected void onFinishInflate() {
        h = (LinearLayout) getChildAt(0);
        GetMarkupImage async = new GetMarkupImage();
        async.execute((ImageView) h.getChildAt(0));
        v = (LinearLayout) h.getChildAt(1);
        ((TextView) v.getChildAt(0)).setText(text);

        super.onFinishInflate();
        Log.d("TAG", "onFinishInflate: done");
    }
}
