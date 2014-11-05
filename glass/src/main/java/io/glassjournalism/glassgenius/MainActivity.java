package io.glassjournalism.glassgenius;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.google.android.glass.widget.CardScrollView;

import io.glassjournalism.glassgenius.data.json.Constants;
import io.glassjournalism.glassgenius.data.json.GeniusCard;
import io.glassjournalism.glassgenius.data.json.GeniusCardListener;
import io.glassjournalism.glassgenius.data.json.GlassGeniusAPI;
import retrofit.RestAdapter;

public class MainActivity extends Activity implements GeniusCardListener {

    private final String TAG = "MainActivity";
    private CardScrollView mCardScroller;
    private GeniusCardAdapter geniusCardAdapter;
    private GlassGeniusAPI glassGeniusAPI;
    public TransientAudioService mAudioService;
    private boolean mIsBound = false;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Crashlytics.start(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCardScroller = new CardScrollView(this);
        setContentView(mCardScroller);

        geniusCardAdapter = new GeniusCardAdapter(MainActivity.this);
        mCardScroller.setAdapter(geniusCardAdapter);


        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Constants.API_ROOT).build();
//        glassGeniusAPI = restAdapter.create(GlassGeniusAPI.class);
//        glassGeniusAPI.getCards(new Callback<List<GeniusCard>>() {
//            @Override
//            public void success(List<GeniusCard> geniusCards, Response response) {
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                Log.d(TAG, "retrofit error " + error.getUrl());
//            }
//        });
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
        Log.d(TAG, "doUnbindService");
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    private void doBindService() {
        Log.d(TAG, "doBindService");
        bindService(new Intent(this,
                TransientAudioService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mAudioService = ((TransientAudioService.TransientAudioBinder) service).getService();
            Log.d(TAG, "service Connected");
            mAudioService.setCardListener(MainActivity.this);
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, "service Disconnected");
        }
    };


    @Override
    public void onCardFound(GeniusCard card) {
        if (mCardScroller.isActivated()) {
            geniusCardAdapter.addCard(card);
        }
    }
}
