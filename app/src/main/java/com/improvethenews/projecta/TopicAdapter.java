package com.improvethenews.projecta;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> implements Filterable {
    private List<Topic> filteredTopicList;
    private List<Topic> topicList;

    @Override
    public Filter getFilter() {
        return topicFilter;
    }
    private Filter topicFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Topic> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(topicList);
            }
            else {
                String pattern = constraint.toString().toLowerCase().trim();
                for (Topic topic : topicList) {
                    if (topic.getTitle().toLowerCase().contains(pattern))
                        filteredList.add(topic);
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredTopicList.clear();
            filteredTopicList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        View view;
        TextView topicTitle;
        String mnemonic;
        int index;

        public TopicViewHolder(View itemView) {
            super(itemView);
            this.card = (CardView) itemView.findViewById(R.id.card);
            this.topicTitle = (TextView) itemView.findViewById(R.id.topic_title);
            this.view = (View) itemView.findViewById(R.id.view);
        }
    }

    public TopicAdapter(List<Topic> topicList) {
        this.topicList = topicList;
        this.filteredTopicList = new ArrayList<>(topicList);
    }

    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_card, parent, false);
                break;
            default:
                //TODO: have different topic appearances
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_card, parent, false);
                break;
        }

        final TopicViewHolder topicViewHolder = new TopicViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                String mnemonic = topicViewHolder.mnemonic;
                intent.putExtra("Mnemonic", mnemonic);
                v.getContext().startActivity(intent);
            }
        });

        return topicViewHolder;
    }

    @Override
    public void onViewAttachedToWindow(TopicViewHolder holder) {
        Log.d(TAG, "onViewAttachedToWindow" + holder.index);
    }

    @Override
    public void onViewDetachedFromWindow(TopicViewHolder holder) {
        Log.d(TAG, "onViewDetachedFromWindow" + holder.index);
    }

    @Override
    public void onBindViewHolder(final TopicViewHolder holder, final int index) {
        int type = holder.getItemViewType();
        holder.index = index;
        holder.topicTitle.setText(filteredTopicList.get(index).getTitle());
        holder.mnemonic = filteredTopicList.get(index).getMnemonic();
        holder.view.getLayoutParams().width = filteredTopicList.get(index).getDepth()*50;
        //TODO: add expand, contract
    }

    @Override
    public int getItemCount() {
        return filteredTopicList.size();
    }

    public Topic getItem(int position) {
        return filteredTopicList.get(position);
    }
}
