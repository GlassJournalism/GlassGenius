package io.glassjournalism.glassgenius.video;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.VideoView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.glassjournalism.glassgenius.R;

public class VideoPlaybackActivity extends Activity {

    private final String TAG = getClass().getName();
    @InjectView(R.id.videoView)
    VideoView videoView;
    @InjectView(R.id.loading)
    View loading;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_video_playback);
        ButterKnife.inject(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Bundle extras = getIntent().getExtras();
        String videoURL = extras.getString("videoURL");
        Uri videoURI = Uri.parse(videoURL);
        videoView.setVideoURI(videoURI);
        videoView.setBackgroundColor(0);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.d(TAG, "prepared");
                loading.setVisibility(View.GONE);
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d(TAG, "video over, finishing activity");
                finish();
            }
        });
    }
}
