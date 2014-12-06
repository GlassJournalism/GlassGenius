package io.glassjournalism.glassgenius.read;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.glassjournalism.glassgenius.R;
import io.glassjournalism.glassgenius.data.json.Article;
import io.glassjournalism.glassgenius.data.json.Constants;


public class ReadActivity extends Activity {

    private final String TAG = getClass().getName();
    @InjectView(R.id.cardScrollView)
    CardScrollView mCardScroller;
    @InjectView(R.id.loading)
    View loadingView;
    private List<CardBuilder> mCards = new ArrayList<CardBuilder>();
    private Map<CardBuilder, Article> articleMap = new HashMap<CardBuilder, Article>();
    private ArticleCardScrollAdapter mAdapter;
    private AudioManager audio;
    SharedPreferences sharedPrefs;

    public final static String EXTRA_MESSAGE = "ARTICLE_TEXT";
    public final static String SOURCE_URL = "SOURCE_URL";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_read);
        ButterKnife.inject(this);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAdapter = new ArticleCardScrollAdapter();
        mCardScroller.setAdapter(mAdapter);
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                audio.playSoundEffect(Sounds.TAP);
                CardBuilder card = (CardBuilder) mAdapter.getItem(i);
                Article article = articleMap.get(card);
                Intent intent = new Intent(ReadActivity.this, SpeedReader.class);
                intent.putExtra(EXTRA_MESSAGE, article.getContents());
                intent.putExtra(SOURCE_URL, article.getSource());
                startActivity(intent);
            }
        });

        Ion.with(this)
                .load(Constants.API_ROOT + "/article")
                .as(new TypeToken<List<Article>>() {
                })
                .setCallback(new FutureCallback<List<Article>>() {
                    @Override
                    public void onCompleted(Exception e, List<Article> articles) {
                        if (null != e) Log.d(TAG, e.getMessage());
                        Log.d(TAG, "article count: " + articles.size());
                        for (Article article : articles) {
                            new FetchArticleTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, article);
                        }
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
            Log.d(TAG, "doInBackground");
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
            int length = article.getContents().split("[,\\s]+").length;
            int minutes = length / sharedPrefs.getInt("WPM", 500);
            String time;
            if (minutes <= 1) {
                time = "Less than 1 minute";
            } else {
                time = minutes + " minutes";
            }
            CardBuilder newCard = new CardBuilder(ReadActivity.this, CardBuilder.Layout.AUTHOR)
                    .setText(article.getTitle())
                    .setHeading(article.getPublication())
                    .setSubheading(article.getAuthor())
                    .setFootnote(time)
                    .setTimestamp(article.getDate())
                    .setIcon(icon)
                    .setAttributionIcon(R.drawable.glass_genius_glass)
                    .addImage(image);
            mCards.add(newCard);
            articleMap.put(newCard, article);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "onPostExeucte");
            mAdapter.notifyDataSetChanged();
            loadingView.setVisibility(View.GONE);
        }
    }

}