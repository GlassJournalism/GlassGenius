package io.glassjournalism.glassgenius.read;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.glassjournalism.glassgenius.R;
import io.glassjournalism.glassgenius.data.json.Article;
import io.glassjournalism.glassgenius.data.json.GlassGeniusAPI;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ReadActivity extends Activity {

    private final String TAG = getClass().getName();
    private CardScrollView mCardScroller;
    private List<CardBuilder> mCards;
    private List<Article> articleList = new ArrayList<Article>();
    private ArticleCardScrollAdapter mAdapter;

    public final static String EXTRA_MESSAGE = "ARTICLE_TEXT";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mAdapter = new ArticleCardScrollAdapter();
        mCardScroller = new CardScrollView(ReadActivity.this);
        mCardScroller.setAdapter(mAdapter);
        setContentView(mCardScroller);
        mCardScroller.activate();
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ReadActivity.this, SpeedReader.class);
                intent.putExtra(EXTRA_MESSAGE, articleList.get(i).getContents());
                startActivity(intent);
            }
        });

        GlassGeniusAPI.GlassGeniusAPI.getArticles(new Callback<List<Article>>() {
            @Override
            public void success(final List<Article> articles, Response response) {
                mCards = new ArrayList<CardBuilder>();
                articleList.addAll(articles);
                for (Article article : articles) {
                    new FetchArticleTask().execute(article);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, error.getResponse().getReason() + " " + error.getUrl());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        mCardScroller.deactivate();
        super.onPause();
    }

    private class ArticleCardScrollAdapter extends CardScrollAdapter {
        @Override
        public int getPosition(Object item) {
            return mCards.indexOf(item);
        }

        @Override
        public int getCount() {
            return mCards.size();
        }

        @Override
        public Object getItem(int position) {
            return mCards.get(position);
        }

        @Override
        public int getViewTypeCount() {
            return CardBuilder.getViewTypeCount();
        }

        @Override
        public int getItemViewType(int position) {
            return mCards.get(position).getItemViewType();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return mCards.get(position).getView(convertView, parent);
        }
    }

    private class FetchArticleTask extends AsyncTask<Article, Void, Void> {

        @Override
        protected Void doInBackground(Article... articles) {
            Article article = articles[0];
            Bitmap icon = null;
            Bitmap image = null;
            try {
                icon = Ion.with(ReadActivity.this).load(article.getIconURL()).asBitmap().get();
                image = Ion.with(ReadActivity.this).load(article.getThumbnailURL()).asBitmap().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            mCards.add(new CardBuilder(ReadActivity.this, CardBuilder.Layout.AUTHOR)
                    .setHeading(article.getTitle())
                    .setSubheading(article.getLocation())
                    .setFootnote(article.getDates())
                    .setIcon(icon)
                    .setAttributionIcon(R.drawable.glass_genius_glass)
                    .addImage(image));
            mAdapter.notifyDataSetChanged();
            return null;
        }
    }

}