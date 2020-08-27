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
import android.widget.ImageButton;
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
        ImageButton shareButton;
        String url = "";
        JSONArray markup = null;
        String mnemonic = "";
        View itemView;
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
            this.shareButton = (ImageButton) itemView.findViewById(R.id.shareButton);
            this.itemView = itemView;
        }
    }

    public ArticleAdapter(List<Article> articleList, String from, String path, String depth, int screenHeight, int screenWidth) {
//        this.articleList = articleList.subList(1, articleList.size());
        this.articleList = articleList;
        try {
            this.articleList.add(new Article(null, "Improve the News", null, "",0,"", "", new URL("http://www.improvethenews.org/index.php/faq/"), 4, null));
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
        //type:
        //-1: article_card_topic
        //0: article_card
        //1: article_card_small_left
        //2: article_card_small_right
        //3: article_card_half
        //4: article_card_footer
        return articleList.get(position).getType();
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
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_card_small_left, parent, false);
                break;
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_card_small_right, parent, false);
                break;
            case 3:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_card_half, parent, false);
                break;
            case 4:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_card_footer, parent, false);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }

        final ArticleViewHolder articleViewHolder = new ArticleViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (articleViewHolder.getItemViewType() == -1) {
                    //Links to another topic, not a news article
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    String mnemonic = articleViewHolder.mnemonic;
                    intent.putExtra("Mnemonic", mnemonic);
                    intent.putExtra("Path", path + " > ");
                    intent.putExtra("Depth", String.valueOf(depth + 1));
                    v.getContext().startActivity(intent);
                }
                else if (articleViewHolder.getItemViewType() != 4) {
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
//        Log.d(TAG, "onViewAttachedToWindow" + holder.index);
    }

    @Override
    public void onViewDetachedFromWindow(ArticleViewHolder holder) {
//        Log.d(TAG, "onViewDetachedFromWindow" + holder.index);
    }

    @Override
    public void onBindViewHolder(final ArticleViewHolder holder, final int index) {
        int type = holder.getItemViewType();
        int i = holder.getAdapterPosition();
        int px;
        switch (type) {
            case -1:
                holder.articleTitle.setText(articleList.get(i).getTitle());
                if (articleList.get(i).getMnemonic() != null)
                    holder.mnemonic = articleList.get(i).getMnemonic();
                if (holder.articleBreadcrumb != null) {
                    if (i == 0)
                        holder.articleBreadcrumb.setText(path);
                    else
                        holder.articleBreadcrumb.setText(path + " > " + articleList.get(i).getTitle());
                }
                break;
            case 0:
                holder.articleImage.getLayoutParams().height = screenWidth*9/16;
                holder.articleTitle.setText(articleList.get(i).getTitle());
                if (articleList.get(i).getImgurl() != null)
                    articleList.get(i).loadImg(holder.articleImage);
                if (articleList.get(i).getSource() != null)
                    holder.articleSource.setText(articleList.get(i).getSource());
                if (articleList.get(i).getReltime() != null)
                    holder.articleTime.setText(articleList.get(i).getReltime());
                if (articleList.get(i).getUrl() != null)
                    holder.url = articleList.get(i).getUrl().toString();
                if (articleList.get(i).getMarkup() != null)
                    holder.markup = articleList.get(i).getMarkup();
                break;
            case 3:
                holder.card.getLayoutParams().width = (int) (screenWidth*0.5);
                holder.articleImage.getLayoutParams().height = (int) (screenWidth*0.5)*9/16;
            case 1:
            case 2:
                holder.articleImage.getLayoutParams().width = (int) (screenWidth*0.5);
                holder.articleImage.getLayoutParams().height = (int) (screenWidth*0.5)*9/16;
                holder.articleTitle.setText(articleList.get(i).getTitle());
                if (articleList.get(i).getImgurl() != null)
                    articleList.get(i).loadImg(holder.articleImage);
                if (articleList.get(i).getSource() != null)
                    holder.articleSource.setText(articleList.get(i).getSource());
                if (articleList.get(i).getReltime() != null)
                    holder.articleTime.setText(articleList.get(i).getReltime());
                if (articleList.get(i).getUrl() != null)
                    holder.url = articleList.get(i).getUrl().toString();
                if (articleList.get(i).getMarkup() != null)
                    holder.markup = articleList.get(i).getMarkup();
                break;
            case 4:
                holder.shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "http://www.improvethenews.org/");
                        sendIntent.setType("text/html");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        v.getContext().startActivity(Intent.createChooser(shareIntent, null));
                    }
                });
                break;
        }
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
