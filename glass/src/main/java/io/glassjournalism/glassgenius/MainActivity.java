package io.glassjournalism.glassgenius;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.google.android.glass.widget.CardScrollView;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import io.glassjournalism.glassgenius.data.json.GeniusCardListener;

public class MainActivity extends Activity implements GeniusCardListener {

    private final String TAG = "MainActivity";
    private CardScrollView mCardScroller;
    private GeniusCardAdapter geniusCardAdapter;
    public TransientAudioService mAudioService;
    private boolean mIsBound = false;
    private Deque<String> cardQueue = new LinkedList<String>();
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Crashlytics.start(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCardScroller = new CardScrollView(this);
        setContentView(mCardScroller);

        geniusCardAdapter = new GeniusCardAdapter(MainActivity.this);
        mCardScroller.setEmptyView(new ProgressBar(this));
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
    public void onCardFound(final String cardId) {
        cardQueue.push(cardId);
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
