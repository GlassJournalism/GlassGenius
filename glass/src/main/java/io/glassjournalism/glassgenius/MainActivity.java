package io.glassjournalism.glassgenius;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.glass.widget.CardScrollView;

public class MainActivity extends Activity {

    private CardScrollView mCardScroller;
    private GeniusCardAdapter geniusCardAdapter;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mCardScroller = new CardScrollView(this);
        setContentView(mCardScroller);
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
