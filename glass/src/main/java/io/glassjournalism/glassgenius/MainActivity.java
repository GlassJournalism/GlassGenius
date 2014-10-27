package io.glassjournalism.glassgenius;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.glass.widget.CardScrollView;

import java.util.List;

import io.glassjournalism.glassgenius.data.json.GeniusCard;
import io.glassjournalism.glassgenius.data.json.GlassGeniusAPI;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends Activity {

    private CardScrollView mCardScroller;
    private GeniusCardAdapter geniusCardAdapter;
    private GlassGeniusAPI glassGeniusAPI;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

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
    }

    @Override
    protected void onPause() {
        mCardScroller.deactivate();
        super.onPause();
    }

}
