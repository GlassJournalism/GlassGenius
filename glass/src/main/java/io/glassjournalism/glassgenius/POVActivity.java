package io.glassjournalism.glassgenius;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.google.android.glass.widget.CardScrollView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.glassjournalism.glassgenius.data.json.GlassGeniusAPI;
import io.glassjournalism.glassgenius.data.json.VideoResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        GlassGeniusAPI.GlassGeniusAPI.getAllVideos(new Callback<List<VideoResponse>>() {
            @Override
            public void success(List<VideoResponse> videoResponses, Response response) {
                videoCardAdapter = new VideoCardAdapter(POVActivity.this, videoResponses);
                mCardScroller.setAdapter(videoCardAdapter);
                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "retrofit error loading videos");
            }
        });


    }
}