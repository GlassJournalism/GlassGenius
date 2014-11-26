package io.glassjournalism.glassgenius;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.crashlytics.android.Crashlytics;
import com.google.android.glass.widget.CardScrollView;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.glassjournalism.glassgenius.data.json.CardFoundResponse;
import io.glassjournalism.glassgenius.data.json.GeniusCardListener;

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