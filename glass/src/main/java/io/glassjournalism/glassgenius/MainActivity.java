package io.glassjournalism.glassgenius;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class MainActivity extends Activity implements GeniusCardListener {

    private final String TAG = "MainActivity";
    @InjectView(R.id.cardScrollView)
    CardScrollView mCardScroller;
    @InjectView(R.id.statusText)
    TextView loadingText;
    @InjectView(R.id.loading)
    LinearLayout loadingView;
    private GeniusCardAdapter geniusCardAdapter;
    public TransientAudioService mAudioService;
    private boolean mIsBound = false;
    private Deque<CardFoundResponse> cardQueue = new LinkedList<CardFoundResponse>();
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        Crashlytics.start(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        geniusCardAdapter = new GeniusCardAdapter(MainActivity.this);
        mCardScroller.setAdapter(geniusCardAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
        doBindService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCardScroller.deactivate();
        doUnbindService();
    }

    private void doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    private void doBindService() {
        bindService(new Intent(this,
                TransientAudioService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mAudioService = ((TransientAudioService.TransientAudioBinder) service).getService();
            mAudioService.setCardListener(MainActivity.this);
        }

        public void onServiceDisconnected(ComponentName className) {
        }
    };


    @Override
    public void onKeywordsLoaded() {
        loadingText.setText("Genius Ready");
    }

    @Override
    public void onCardFound(CardFoundResponse cardFound) {
        loadingView.setVisibility(View.GONE);
        cardQueue.push(cardFound);
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (cardQueue.size() > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                geniusCardAdapter.addCard(cardQueue.pop());
                            }
                        });
                    }
                }

            }, 0, 2000);
        }
    }
}
