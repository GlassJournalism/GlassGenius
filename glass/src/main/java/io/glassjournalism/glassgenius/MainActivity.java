package io.glassjournalism.glassgenius;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.firebase.client.Firebase;
import com.google.android.glass.widget.CardScrollView;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.glassjournalism.glassgenius.data.json.CardFoundResponse;
import io.glassjournalism.glassgenius.data.json.Constants;
import io.glassjournalism.glassgenius.data.json.GeniusCardListener;
import io.glassjournalism.glassgenius.data.json.GeniusLoadListener;

public class MainActivity extends Activity implements GeniusCardListener, GeniusLoadListener {

    private final String TAG = "MainActivity";
    @InjectView(R.id.cardScrollView)
    CardScrollView mCardScroller;
    @InjectView(R.id.statusText)
    TextView loadingText;
    @InjectView(R.id.sessionText)
    TextView sessionText;
    @InjectView(R.id.loading)
    LinearLayout loadingView;
    private GeniusCardAdapter geniusCardAdapter;
    public TransientAudioService mAudioService;
    private boolean mIsBound = false;
    private Deque<CardFoundResponse> cardQueue = new LinkedList<CardFoundResponse>();
    private Timer mTimer;
    private Firebase cardsRef;
    private String sessionID;
    private boolean canClick = false;

    private Firebase getCardsRef() {
        if (null == cardsRef) {
            cardsRef = new Firebase(Constants.FIREBASE_URL).child("sessions").child(sessionID);
        }
        return cardsRef;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        Crashlytics.start(this);
        Firebase.setAndroidContext(this);
        geniusCardAdapter = new GeniusCardAdapter(MainActivity.this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCardScroller.setAdapter(geniusCardAdapter);
        sessionID = UUID.randomUUID().toString().substring(0, 5);
        SharedPreferences sharedPrefs = sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPrefs.edit().putString("session", sessionID).apply();
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
            mAudioService.setLoadListener(MainActivity.this);
        }

        public void onServiceDisconnected(ComponentName className) {
        }
    };

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_DPAD_CENTER && canClick) {
            canClick = false;
            mAudioService.setCardListener(MainActivity.this);
            loadingText.setText("Listening...");
            sessionText.setText("");
            return true;
        }
        return super.onKeyDown(keycode, event);
    }


    @Override
    public void onKeywordsLoaded() {
        loadingText.setText("Genius Ready, tap to start");
        sessionText.setText("Session: " + sessionID);
        canClick = true;
    }

    @Override
    public void onError(String error) {
        loadingText.setText(error);

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
                                CardFoundResponse newCardFound = cardQueue.pop();
                                geniusCardAdapter.addCard(newCardFound);
                                getCardsRef().push().setValue(newCardFound);
                                mCardScroller.setSelection(0);
                            }
                        });
                    }
                }

            }, 0, 4000);
        }
    }
}