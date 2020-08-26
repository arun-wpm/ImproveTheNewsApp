package com.improvethenews.projecta;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;

public class SliderCardView extends ConstraintLayout {
    public SliderCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.slider_card_large, this);
    }
}
