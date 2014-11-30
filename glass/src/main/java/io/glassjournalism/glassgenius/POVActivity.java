package io.glassjournalism.glassgenius;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.crashlytics.android.Crashlytics;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class POVActivity extends Activity {

    private final String TAG = "POVActivity";
    @InjectView(R.id.statusText)
    TextView loadingText;
    @InjectView(R.id.loading)
    LinearLayout loadingView;
    @InjectView(R.id.videoView)
    VideoView videoView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_pov);
        ButterKnife.inject(this);
        Crashlytics.start(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Uri videoUri = Uri.parse("http://dcarr.io/magic.mp4");
        videoView.setBackgroundColor(0);
        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.d(TAG, "onPrepared");
                loadingView.setVisibility(View.GONE);
                videoView.start();
            }
        });
    }
}