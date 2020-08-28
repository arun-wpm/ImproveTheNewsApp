package com.improvethenews.projecta;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.ContentValues.TAG;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {
    private List<Slider> sliderList;
    private SliderViewHolder pieholder;
    private Context context;

    public static class SliderViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView sliderTitle, startText, endText;
        SeekBar seekBar;
        View color;
        String code;
        PieChart pieChart;
        int index;

        public SliderViewHolder(View itemView) {
            super(itemView);
            this.card = (CardView) itemView.findViewById(R.id.card);
            this.sliderTitle = (TextView) itemView.findViewById(R.id.slider_title);
            this.startText = (TextView) itemView.findViewById(R.id.startText);
            this.endText = (TextView) itemView.findViewById(R.id.endText);
            this.seekBar = (SeekBar) itemView.findViewById(R.id.seekBar);
            this.color = (View) itemView.findViewById(R.id.slider_color);
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
            case -2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_card_topic, parent, false);
                break;
            case -1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_card_topic, parent, false);
                break;
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_card_large, parent, false);
                break;
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_pie, parent, false);
                break;
            default:
                //case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_card_small, parent, false);
                break;
        }

        final SliderViewHolder sliderViewHolder = new SliderViewHolder(view);
        context = parent.getContext();
        return sliderViewHolder;
    }

    @Override
    public void onViewAttachedToWindow(SliderViewHolder holder) {
        int type = holder.getItemViewType();
        switch (type) {
            case 0:
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
            Log.d(TAG, "onViewDetachedFromWindow" + holder.index);
        }
    }

    public void renderPie() {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        for (int i = 0; i < sliderList.size(); i++) {
            if (sliderList.get(i).getType() == 1 && sliderList.get(i).getValue() > 0) {
                entries.add(new PieEntry(sliderList.get(i).getValue(), sliderList.get(i).getTitle()));
                colors.add(sliderList.get(i).getColor());
            }
        }
        PieDataSet set = new PieDataSet(entries, "Topic Distribution");
        set.setColors(colors);
        set.setSliceSpace(2);
        PieData data = new PieData(set);
        data.setDrawValues(false);
        data.setValueTextSize(12);
        data.setValueTypeface(ResourcesCompat.getFont(context, R.font.opensans));
        this.pieholder.pieChart.setData(data);
        this.pieholder.pieChart.setDrawEntryLabels(false);
        this.pieholder.pieChart.setHoleColor(android.R.color.transparent);
        this.pieholder.pieChart.getDescription().setEnabled(false);
        Legend legend = this.pieholder.pieChart.getLegend();
        legend.setEnabled(false);
        this.pieholder.pieChart.invalidate();
    }

    public void reevaluateValues() {
        int sum = 0;
        for (Slider slider : sliderList)
            if (slider.getType() == 1)
                sum += slider.getValue();
        for (Slider slider : sliderList)
            if (slider.getType() == 1)
                slider.setValue(slider.getValue()*99/sum);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final SliderViewHolder holder, final int index) {
        //TODO: refactor this part to a switch case
        int type = holder.getItemViewType();
        holder.index = index;
        if (type != 2)
            holder.sliderTitle.setText(sliderList.get(holder.index).getTitle());
        holder.code = sliderList.get(holder.index).getCode();
        if (type == 0 || type == 1) {
            holder.seekBar.setProgress(sliderList.get(holder.index).getValue());
            Log.d(TAG, "onBindViewHolder: " + sliderList.get(holder.index).getValue());
            holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser == true) {
                        sliderList.get(holder.index).setValue(progress);
                        Log.d(TAG, "onProgressChanged: " + progress);
                        renderPie();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    reevaluateValues();
                }
            });
        }

        LinearLayout.LayoutParams params0, params1;
        switch (type) {
            case -2:
                //Category header "Bias Sliders"
                break;
            case -1:
                //Category header "Your ... feed"
                holder.sliderTitle.setTextColor(Color.WHITE);
                break;
            case 0:
                holder.startText.setText(sliderList.get(holder.index).getStart());
                holder.endText.setText(sliderList.get(holder.index).getEnd());
                break;
            case 2:
                //Pie chart
                this.pieholder = holder;
                renderPie();
                break;
            default:
                //case 1:
                holder.color.setBackgroundColor(sliderList.get(holder.index).getColor());
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
