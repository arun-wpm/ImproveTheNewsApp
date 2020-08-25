package com.improvethenews.projecta;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {
    private List<Slider> sliderList;
    private SliderViewHolder pieholder;

    public static class SliderViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView sliderTitle;
        SeekBar seekBar;
        View view0, view1;
        String code;
        PieChart pieChart;
        int index;

        public SliderViewHolder(View itemView) {
            super(itemView);
            this.card = (CardView) itemView.findViewById(R.id.card);
            this.sliderTitle = (TextView) itemView.findViewById(R.id.slider_title);
            this.seekBar = (SeekBar) itemView.findViewById(R.id.seekBar);
            this.view0 = (View) itemView.findViewById(R.id.view0);
            this.view1 = (View) itemView.findViewById(R.id.view1);
            this.pieChart = (PieChart) itemView.findViewById(R.id.chart);
        }
    }

    public SliderAdapter(List<Slider> sliderList) {
        this.sliderList = sliderList;
        for (int i = 0; i < this.sliderList.size(); i++) {
            Log.d(TAG, "SliderAdapter: " + this.sliderList.get(i).getTitle());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return sliderList.get(position).getType();
    }

    @Override
    public SliderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case -1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_card_topic, parent, false);
                break;
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_card, parent, false);
                break;
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_pie, parent, false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_card, parent, false);
//                view.setVisibility(View.GONE);
                break;
        }

        final SliderViewHolder sliderViewHolder = new SliderViewHolder(view);
        return sliderViewHolder;
    }

    @Override
    public void onViewAttachedToWindow(SliderViewHolder holder) {
        int type = holder.getItemViewType();
        switch (type) {
            case 0:
                holder.seekBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
                holder.seekBar.getThumb().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
                holder.seekBar.setProgress(sliderList.get(holder.index).getValue());
                break;
            case 1:
                holder.seekBar.setProgress(sliderList.get(holder.index).getValue());
                break;
        }
        Log.d(TAG, "onViewAttachedToWindow" + holder.index);
    }

    @Override
    public void onViewDetachedFromWindow(SliderViewHolder holder) {
        int type = holder.getItemViewType();
        if (type == 0 || type == 1) {
            holder.seekBar.getProgressDrawable().clearColorFilter();
            holder.seekBar.getThumb().clearColorFilter();
            Log.d(TAG, "onViewDetachedFromWindow" + holder.index);
        }
    }

    public void renderPie() {
        List<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < sliderList.size(); i++) {
            if (sliderList.get(i).getType() == 1)
                entries.add(new PieEntry(sliderList.get(i).getValue(), sliderList.get(i).getTitle()));
        }
        PieDataSet set = new PieDataSet(entries, "Topic Distribution");
//        set.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
//        set.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        set.setColors(new int[] {R.color.colorPrimaryLight, R.color.colorPrimaryDark}, this.pieholder.card.getContext());
        PieData data = new PieData(set);
        this.pieholder.pieChart.setData(data);
//        this.pieholder.pieChart.setHoleRadius(50);
        Legend legend = this.pieholder.pieChart.getLegend();
        legend.setEnabled(false);
        this.pieholder.pieChart.invalidate();
    }

    @Override
    public void onBindViewHolder(final SliderViewHolder holder, final int index) {
        //TODO: refactor this part to a switch case
        int type = holder.getItemViewType();
        holder.index = index;
        if (type != 2)
            holder.sliderTitle.setText(sliderList.get(index).getTitle());
        holder.code = sliderList.get(index).getCode();
        if (type == 0 || type == 1) {
            holder.seekBar.setProgress(sliderList.get(index).getValue());
            holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser == true) {
                        sliderList.get(index).setValue(progress);
                        Log.d(TAG, "onProgressChanged: " + progress);
                        renderPie();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        LinearLayout.LayoutParams params0, params1;
        switch (type) {
            case -1:
                //Category header
                holder.card.setRadius(24);
                holder.card.setCardElevation(0);
                holder.sliderTitle.setTextSize(28);
                break;
            case 0:
                params0 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                );
                holder.view0.setLayoutParams(params0);
                params1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0
                );
                holder.view1.setLayoutParams(params1);
                holder.seekBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
                holder.seekBar.getThumb().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
                break;
            case 2:
                //Pie chart
                this.pieholder = holder;
                renderPie();
                break;
            default:
                params0 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        99 - sliderList.get(index).getUsualvalue()
                );
                Log.d(TAG, "onBindViewHolder: " + sliderList.get(index).getUsualvalue());
                holder.view0.setLayoutParams(params0);
                params1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        sliderList.get(index).getUsualvalue()
                );
                holder.view1.setLayoutParams(params1);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return sliderList.size();
    }

    public Slider getItem(int position) {
        return sliderList.get(position);
    }
}
