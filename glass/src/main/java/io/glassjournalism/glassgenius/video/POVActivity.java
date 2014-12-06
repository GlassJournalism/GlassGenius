package io.glassjournalism.glassgenius.video;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.google.android.glass.widget.CardScrollView;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.glassjournalism.glassgenius.R;
import io.glassjournalism.glassgenius.data.json.Constants;
import io.glassjournalism.glassgenius.data.json.VideoResponse;

public class POVActivity extends Activity {

    private final String TAG = "POVActivity";
    private VideoCardAdapter videoCardAdapter;
    @InjectView(R.id.loading)
    LinearLayout loadingView;
    @InjectView(R.id.cardScrollView)
    CardScrollView mCardScroller;

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
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
        videoCardAdapter = new VideoCardAdapter(POVActivity.this);
        mCardScroller.setAdapter(videoCardAdapter);
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick");
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
                        videoCardAdapter.addVideos(videoResponses);
                        loadingView.setVisibility(View.GONE);
                    }
                });

    }
}