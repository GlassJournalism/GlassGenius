package io.glassjournalism.glassgenius.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.crashlytics.android.Crashlytics;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.glassjournalism.glassgenius.R;
import io.glassjournalism.glassgenius.data.json.Constants;
import io.glassjournalism.glassgenius.data.json.VideoResponse;

public class POVActivity extends Activity {

    private final String TAG = getClass().getName();
    private VideoCardAdapter videoCardAdapter;
    @InjectView(R.id.cardScrollView)
    CardScrollView mCardScroller;
    @InjectView(R.id.loading)
    View loadingView;
    private AudioManager audio;

    private List<GeniusBuilder> builders = new ArrayList<GeniusBuilder>();

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCardScroller.deactivate();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_pov);
        ButterKnife.inject(this);
        Crashlytics.start(this);
        videoCardAdapter = new VideoCardAdapter();
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mCardScroller.setAdapter(videoCardAdapter);
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                audio.playSoundEffect(Sounds.TAP);
                VideoResponse video = (VideoResponse) videoCardAdapter.getItem(i);
                Intent videoIntent = new Intent(POVActivity.this, VideoPlaybackActivity.class);
                videoIntent.putExtra("videoURL", video.getUrl());
                startActivity(videoIntent);
            }
        });

        Ion.with(this)
                .load(Constants.API_ROOT + "/video")
                .as(new TypeToken<List<VideoResponse>>() {
                })
                .setCallback(new FutureCallback<List<VideoResponse>>() {
                    @Override
                    public void onCompleted(Exception e, List<VideoResponse> videoResponses) {
                        for (VideoResponse video : videoResponses) {
                            GeniusBuilder builder = new GeniusBuilder();
                            builder.video = video;

                            new FetchVideoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, builder);
                        }
                    }
                });

    }

    private class FetchVideoTask extends AsyncTask<GeniusBuilder, Void, Void> {

        @Override
        protected Void doInBackground(GeniusBuilder... params) {
            GeniusBuilder builder = params[0];
            Bitmap image = null;
            try {
                image = Ion.with(POVActivity.this).load(builder.video.getThumbnail()).asBitmap().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            builder.builder = new CardBuilder(POVActivity.this, CardBuilder.Layout.TITLE)
                    .setText(builder.video.getName())
                    .addImage(image);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            videoCardAdapter.notifyDataSetChanged();
            loadingView.setVisibility(View.GONE);
        }
    }

    private class VideoCardAdapter extends CardScrollAdapter {
        @Override
        public int getCount() {
            return builders.size();
        }

        @Override
        public Object getItem(int position) {
            return builders.get(position);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            return builders.get(position).builder.getView(view, parent);
        }

        @Override
        public int getPosition(Object o) {
            return 0;
        }
    }

    private class GeniusBuilder {
        public CardBuilder builder;
        public VideoResponse video;
    }
}