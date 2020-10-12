package com.improvethenews.projecta;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.URL;

public class MarkupCardView extends FrameLayout {
    CardView c;
    LinearLayout h, v;
    String text;
    URL imgurl;
//    public MarkupCardView(@NonNull Context context, URL imgurl, String text) {
//        super(context);
//        this.imgurl = imgurl;
//        this.text = text;
//        LayoutInflater inflater = LayoutInflater.from(getContext());
//        inflater.inflate(R.layout.article_card_markup, this);
//
//        c = (CardView) getChildAt(0);
//        h = (LinearLayout) c.getChildAt(0);
//        Picasso.get().load(imgurl.toString()).into((ImageView) h.getChildAt(0));
//        v = (LinearLayout) h.getChildAt(1);
//        ((TextView) v.getChildAt(0)).setText(text);
//    }
    //when did this get changed into just a String??
    public MarkupCardView(@NonNull Context context, String error, String text) {
        super(context);
        this.text = text;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.article_card_markup, this);

        c = (CardView) getChildAt(0);
        h = (LinearLayout) c.getChildAt(0);
        ((TextView) h.getChildAt(0)).setText(error);
        v = (LinearLayout) h.getChildAt(1);
        ((TextView) v.getChildAt(0)).setText(text);
    }
}
