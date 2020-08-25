package com.improvethenews.projecta;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {
    private List<Article> articleList;
    private String from, path;
    private int depth;
    private int screenHeight, screenWidth;

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        ImageView articleImage;
        TextView articleBreadcrumb;
        TextView articleTitle;
        TextView articleSource;
        TextView articleTime;
//        FlexboxLayout articleTags;
        String url;
        JSONArray markup;
        String mnemonic;
        View itemView;
        int index;
//        Article.ArticleAsync articleAsync = null;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            this.card = (CardView) itemView.findViewById(R.id.card);
            this.articleBreadcrumb = (TextView) itemView.findViewById(R.id.article_breadcrumb);
            this.articleImage = (ImageView) itemView.findViewById(R.id.article_image);
            this.articleTitle = (TextView) itemView.findViewById(R.id.article_title);
            this.articleSource = (TextView) itemView.findViewById(R.id.article_source);
            this.articleTime = (TextView) itemView.findViewById(R.id.article_time);
//            this.articleTags = (FlexboxLayout) itemView.findViewById(R.id.tags);
            this.itemView = itemView;
        }
    }

    public ArticleAdapter(List<Article> articleList, String from, String path, String depth, int screenHeight, int screenWidth) {
//        this.articleList = articleList.subList(1, articleList.size());
        this.articleList = articleList;
        try {
            this.articleList.add(new Article(null, "Improve the News", null, "",0,"", "", new URL("http://www.improvethenews.org/index.php/faq/"), 2, null));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.from = from;
        this.path = path;
        this.depth = Integer.valueOf(depth);
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
    }

    @Override
    public int getItemViewType(int position) {
        return articleList.get(position).getType();
//        return (position < 10)?0:1;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case -1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_card_topic, parent, false);
                break;
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_card, parent, false);
                break;
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_card_half, parent, false);
                break;
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_card_footer, parent, false);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }

        final ArticleViewHolder articleViewHolder = new ArticleViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (articleViewHolder.mnemonic != null) {
                    //Links to another topic, not a news article
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    String mnemonic = articleViewHolder.mnemonic;
                    intent.putExtra("Mnemonic", mnemonic);
                    intent.putExtra("Path", path + " > ");
                    intent.putExtra("Depth", String.valueOf(depth + 1));
                    v.getContext().startActivity(intent);
                }
                else {
//                    Intent intent = new Intent(v.getContext(), WebViewActivity.class);
                    Intent intent = new Intent(v.getContext(), AnnotatedWebViewActivity.class);
                    String url = articleViewHolder.url;
                    intent.putExtra("URL", url);
                    intent.putExtra("from", from);
                    if (articleViewHolder.markup != null)
                        intent.putExtra("markup", articleViewHolder.markup.toString());
                    v.getContext().startActivity(intent);
                }
            }
        });

        return articleViewHolder;
    }

    @Override
    public void onViewAttachedToWindow(ArticleViewHolder holder) {
        if (articleList.get(holder.index).getImgurl() != null) {
//            holder.articleAsync = articleList.get(holder.index).loadImg(holder.articleImage);
            articleList.get(holder.index).loadImg(holder.articleImage);
//            holder.articleImage.setImageBitmap(articleList.get(holder.index).getImg());
//            holder.articleImage.setVisibility(View.VISIBLE);
        }
//        else {
//            //Category header
//            holder.articleImage.setImageResource(0);
//            holder.articleImage.getLayoutParams().height = 0;
//            holder.articleImage.setVisibility(View.INVISIBLE);
//        }
        Log.d(TAG, "onViewAttachedToWindow" + holder.index);
    }

    @Override
    public void onViewDetachedFromWindow(ArticleViewHolder holder) {
//        if (holder.articleAsync != null) {
//            holder.articleAsync.cancel(true);
//            holder.articleAsync = null;
//        }
//        if (holder.articleImage != null)
//            holder.articleImage.setImageResource(0);
        Log.d(TAG, "onViewDetachedFromWindow" + holder.index);
    }

    @Override
    public void onBindViewHolder(final ArticleViewHolder holder, final int index) {
        //TODO: refactor this whole thing
        int type = holder.getItemViewType();
        holder.index = holder.getAdapterPosition();
        if (articleList.get(holder.index).getImgurl() != null) {
            switch (type) {
                case 0:
                    holder.card.setRadius(24);
                    holder.card.setCardElevation(10);
//                    holder.articleImage.setImageBitmap(articleList.get(index).getImg());
//                    holder.articleImage.setVisibility(View.VISIBLE);
                    holder.articleTitle.setTextSize(20);

                    holder.articleImage.getLayoutParams().height = screenWidth*9/16;
                    break;
                case 1:
                    int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 180*9/16, holder.itemView.getContext().getResources().getDisplayMetrics());
                    holder.articleImage.getLayoutParams().height = px;
                    break;
                default:
                    holder.card.setRadius(18);
                    holder.card.setCardElevation(5);
//                    holder.articleImage.setImageBitmap(articleList.get(index).getImg());
//                    holder.articleImage.setVisibility(View.VISIBLE);
                    holder.articleTitle.setTextSize(16);
                    break;
            }
            Log.d(TAG, "onBindViewHolder: done");
            if (articleList.get(holder.index).getSource() != null)
                holder.articleSource.setText(articleList.get(holder.index).getSource());
            else
                holder.articleSource.setText("");

            if (articleList.get(holder.index).getReltime() != null)
                holder.articleTime.setText(articleList.get(holder.index).getReltime());
            else
                holder.articleTime.setText("");

            if (articleList.get(holder.index).getUrl() != null)
                holder.url = articleList.get(holder.index).getUrl().toString();
            else
                holder.url = "";

            if (articleList.get(holder.index).getMarkup() != null)
                holder.markup = articleList.get(holder.index).getMarkup();
            else
                holder.markup = null;
        }
        else {
            //Category header
            holder.card.setRadius(24);
            holder.card.setCardElevation(0);
//            holder.articleImage.setImageResource(0);
//            holder.articleImage.setVisibility(View.INVISIBLE);
            holder.articleTitle.setTextSize(28);

            if (articleList.get(holder.index).getMnemonic() != null)
                holder.mnemonic = articleList.get(holder.index).getMnemonic();
            else
                holder.mnemonic = null;

            if (holder.articleBreadcrumb != null) {
                if (holder.index == 0)
                    holder.articleBreadcrumb.setText(path);
                else
                    holder.articleBreadcrumb.setText(path + " > " + articleList.get(holder.index).getTitle());
            }
        }
        holder.articleTitle.setText(articleList.get(holder.index).getTitle());

        //display tags as well?
//        for (int i = 0; i < articleList.get(index).getTagsLength(); i++) {
//            TextView textView = new TextView(holder.itemView.getContext());
//            textView.setText(articleList.get(index).getTags(i));
//            textView.setPadding(4,0,4,0);
//            holder.articleTags.addView(textView);
//        }
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }
}
