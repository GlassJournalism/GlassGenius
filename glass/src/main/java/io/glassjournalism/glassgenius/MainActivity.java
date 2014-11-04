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

import com.google.android.glass.widget.CardScrollView;

import java.util.List;

import io.glassjournalism.glassgenius.data.json.GeniusCard;
import io.glassjournalism.glassgenius.data.json.GeniusCardListener;
import io.glassjournalism.glassgenius.data.json.GlassGeniusAPI;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCardScroller = new CardScrollView(this);
        setContentView(mCardScroller);

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://glacial-ridge-6503.herokuapp.com").build();
        glassGeniusAPI = restAdapter.create(GlassGeniusAPI.class);
        glassGeniusAPI.getCards(new Callback<List<GeniusCard>>() {
            @Override
            public void success(List<GeniusCard> geniusCards, Response response) {
                geniusCardAdapter = new GeniusCardAdapter(MainActivity.this, geniusCards);
                mCardScroller.setAdapter(geniusCardAdapter);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
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
